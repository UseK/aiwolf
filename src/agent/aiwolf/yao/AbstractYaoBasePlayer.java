package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import com.sun.javafx.geom.AreaOp.AddOp;

/**
 * 全役職共通の動きをここで定義する
 * @author Aoshima
 */
public abstract class AbstractYaoBasePlayer extends AbstractRole { 
	
	public YaoGameInfo yaoGameInfo=null; //発言から得られる情報を纏める
	//private List<Pattern> generalPatterns;
	private int readTalkNumber=0;
	private Agent voteAgent=null;
	private Agent toldVoteAgent=null;
	private int state=0;
	//private PatternMaker patternMaker = new PatternMaker();
	public int day=0;
	public double eval[];//各人の評価値を[0-100]で格納しておく。
	public Agent believeSeer=null;  //占い盲信してたらここにその占いをいれる
	public Agent believeMedium=null;//霊能盲信していたらここにその霊能をいれる
	public Agent toldBelieveSeer=null;
	public Agent toldBelieveMedium=null;
	public boolean printFlag=false;
	public int believeSeerDate=3;
	public int believeMediumDate=3;
	public void setPrint(boolean flag){
		printFlag=flag;
	}
	
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		yaoGameInfo=new YaoGameInfo(gameInfo, gameSetting);
		readTalkNumber=0;
		voteAgent=null;
		toldVoteAgent=null;
		//generalPatterns=new ArrayList<Pattern>();
		//patternMaker.initialize(generalPatterns,gameInfo.getAliveAgentList());
		eval=new double[gameSetting.getPlayerNum()+1];
	}
	
	/**
	 * 会話の処理
	 * 暫定投票先の更新
	 * @param gameInfo
	 */
	public void update(GameInfo gameInfo){
		super.update(gameInfo);

		//yaoGameInfo.alivedUpdate(gameInfo.getAliveAgentList()); 襲撃・処刑をみてればalivedは自然と解るよな…
		
		List<Talk> talkList = gameInfo.getTalkList();
		/*
		 * 各発話についての処理
		 * カミングアウトについてはパターンの拡張
		 * 能力結果の発話についてはパターン情報の更新
		 */
		boolean patternChanged = false;
		for(; readTalkNumber < talkList.size(); readTalkNumber++){
			Talk talk = talkList.get(readTalkNumber);
			Utterance utterance = new Utterance(talk.getContent());
			//if( printFlag ) System.out.println(readTalkNumber+" is: " + utterance.getTopic());
			switch (utterance.getTopic()) {
			case COMINGOUT:
				comingoutTalkDealing(talk, utterance);
				patternChanged = true;
				break;
			case DIVINED:
				divinedTalkDealing(talk, utterance);
				patternChanged = true;
				break;
			case INQUESTED:
				inquestedTalkDealing(talk, utterance);
				patternChanged = true;
				break;
			case VOTE:
				voteTalkDealing(talk, utterance);
				break;
			//上記以外
			default:
				break;
			}
		}

//		System.out.println("flag:" + printFlag);
		if(printFlag){
			yaoGameInfo.print();
			System.out.println("believeSeer: "+ believeSeer + "believeMedium"+ believeMedium);
		}
		
		setStrategy();
		setVoteTarget();
		if(printFlag) System.out.println("Now Vote:" +voteAgent);
	}
	
	/*
	 * 戦略を決定する 
	 * 大前提：潜伏は破綻しない限り考えないことにする。
	 * 理由：役職潜伏は基本的にこのルールにおいて悪手だと思う。
	 * 霊能の噛まれ防止は確かにメリットだけども、そう言った潜伏理由を持って村説得できてはじめて成り立つ。
	 */
	private void setStrategy(){
		List<Agent> seers=yaoGameInfo.getSeers();
		List<Agent> mediums=yaoGameInfo.getMediums();
		List<Agent> enemies=yaoGameInfo.getEnemies();
		List<Agent> aliveenemies=yaoGameInfo.getAliveEnemies();
		
		//信じていた占い・霊能が客観的に敵だと判明した場合は考えなおす
		if( believeSeer!=null&&believeMedium!=null&&yaoGameInfo.linePossible(believeSeer,believeMedium)==false){
			believeSeer=null;
			believeMedium=null;
		}
		if( believeSeer!=null&&enemies.contains(believeSeer) ) believeSeer=null;
		if( believeSeer!=null&&enemies.contains(believeMedium) ) believeMedium=null;
			
		if( day>=believeSeerDate || day >= believeMediumDate){
			// 3日目時点で真の可能性がある占い・霊能が１人だった場合はその占いを100%信じる。潜伏は破綻しない限り考えない。
			// レアケとして、2日終了直前COして真占い・真霊能が対抗COする間も無く一日が終わった場合が防げないが、その場合は不運だったと思うこととする。
			if(day>=believeSeerDate&&believeSeer==null&&seers.size()==1){
				eval[seers.get(0).getAgentIdx()]=100;
				believeSeer=seers.get(0);	
			}
			else if(day>=believeMediumDate&&believeMedium==null&&mediums.size()==1){
				eval[mediums.get(0).getAgentIdx()]=100;
				believeMedium=mediums.get(0);
			}
		}
		if( believeSeer!=null &&yaoGameInfo.getEnemiesOfSeer(believeSeer)!=null){
			// もしbelieveSeer視点で矛盾しない霊能が一人しかいないのなら、その人を信じる
			if( yaoGameInfo.getMediumCandidatesOfSeer(believeSeer).size()==1){
				believeMedium=yaoGameInfo.getMediumCandidatesOfSeer(believeSeer).get(0);
			}
		}
		else if( believeMedium!=null&&yaoGameInfo.getEnemiesOfMedium(believeMedium)!=null){
			// もしbelieveMedium視点で矛盾しない霊能が一人しかいないのなら、その人を信じる
			if( yaoGameInfo.getSeerCandidatesOfMedium(believeMedium).size()==1){
				believeSeer=yaoGameInfo.getSeerCandidatesOfMedium(believeMedium).get(0);
			}
		}
		
		aliveenemies=yaoGameInfo.getAliveEnemies(believeSeer,believeMedium);		
		
		//破綻の人が一人でも生きているのであれば、その人を吊るフラグを立てる
		if( aliveenemies.size()>0){	
			if( printFlag )System.out.println("there is"+ aliveenemies.size()+ "enemies");
			state |= State.EnemyKiller;
		}
		else{ 
			state &= ~State.EnemyKiller; 
		}

		//霊能が2人以上存在する時、占いが2人以下ならライン戦、3-2以上の進行か、霊能3人以上なら役職ローラー
		if( believeMedium== null && mediums.size()>=2 ){
			if(mediums.size()>=3){
				if(printFlag)System.out.println("there is"+ 3+ "mediums");
				state|= State.MediumRoller;
			}
			else if( seers.size() >= 3 ){
				if(printFlag)System.out.println("there is"+ 3+ "seers"+ 2 +"mediums");
				state |= State.MediumRoller;
				state |= State.SeerRoller;
			}
			else{
				if(printFlag) System.out.println("let's line battle!");
				state &= ~State.MediumRoller;
				state &= ~State.SeerRoller;
			}
		}
		//占いが４人以上存在すれば占いローラー
		if( believeSeer==null && seers.size() >= 4 ){
			if(printFlag)System.out.println("too many seers! I can't believe it!");
			state |= State.SeerRoller;
		}
		
	}
	/**
	 * 投票先を決定する
	 */
	private void setVoteTarget(){
		if(printFlag)System.out.println(Integer.toBinaryString(state));
		if( (state&State.EnemyKiller) >0 ){
			//確定黒を釣りたい
			if(printFlag)System.out.println("EnemyKiller");
			List<Agent> aliveEnemies=yaoGameInfo.getAliveEnemies(believeSeer,believeMedium);
			if( aliveEnemies.contains(voteAgent) ) return;
			voteAgent=getRandom(aliveEnemies);
			if( voteAgent!=null )return;
		}
		if( (state&State.MediumRoller) > 0 ){
			//霊能ローラー
			if(printFlag)System.out.println("MediumKiller");
			List <Agent> aliveMediums=yaoGameInfo.getAliveMediums();
			if( aliveMediums.contains(voteAgent) ) return;
			voteAgent=getRandom(aliveMediums);
			if( voteAgent!=null )return;
		}
		if( (state&State.SeerRoller) > 0 ){
			//占いローラー
			if(printFlag)System.out.println("SeerKiller");
			List <Agent> aliveSeers=yaoGameInfo.getAliveSeers();
			if( aliveSeers.contains(voteAgent) ) return;
			voteAgent=getRandom(aliveSeers);
			if( voteAgent!=null )return;
		}
		List<Agent> checkingSeers=yaoGameInfo.getSeers();;
		sortSeers(checkingSeers);

		//極力多くの占いで全員のラインが追える時にはその中で最適な人を返す
		List<Agent> alivePlayers=yaoGameInfo.getAlivePlayers();
		for( int n_seer=checkingSeers.size(); n_seer>0; n_seer--){
			List<Agent> voteCandidates=new ArrayList<Agent>();
			for( Agent a: alivePlayers){
				boolean ok=true;
				for( int si=0; si<n_seer; si++){
					if( !yaoGameInfo.possibleToExecute(checkingSeers.get(si),a) ){
						ok=false;
						break;
					}
				}
				if( ok ){
					voteCandidates.add(a);
				}
			}
			if( voteCandidates.size() > 0 ){
				if(printFlag)System.out.println("get worst candidate");
				Agent tmpAgent=getWorstCandidate( voteCandidates, checkingSeers.subList(0,n_seer));
				if( tmpAgent!=null ){
					voteAgent=tmpAgent;
					return;
				}
			}
		}
		
		//ランダム
		if( voteAgent==null||!yaoGameInfo.getAlivePlayers().contains(voteAgent)){
			if( printFlag ){
				System.out.println("Gray Random");
				System.out.println(voteAgent + " is not contained");
			}
			List<Agent> gray=yaoGameInfo.getAlivePlayers();
			gray.remove(getMe());
			voteAgent=getRandom(gray);
		}
		return;
	}
	
	public void sortSeers(List<Agent> seer){
		Collections.sort(seer,
				new Comparator<Agent>(){
					public int compare(Agent a, Agent b){
						return eval(a)-eval(b);
					}
				});
		return;
	}
	public int eval(Agent a){
		if( !yaoGameInfo.getSeers().contains(a)) return -100;
		int ret =100;
		List<Agent> mediums = yaoGameInfo.getMediums();
		boolean lineExists=false;
		for( Agent m: mediums){
			if( yaoGameInfo.linePossible(a,m) )lineExists=true;
		}
		if( !lineExists ) ret-=50;
		Agent me = getMe();
		if( yaoGameInfo.getSeerTable(a, me) == Species.WEREWOLF ) return -100;
		if( yaoGameInfo.getSeerTable(a, me) == Species.HUMAN) ret+=10;
		return ret;
	}
	private Agent getWorstCandidate(List<Agent> candidates, List<Agent> seer){
		double score=0;
		Agent ret=null;
		for( Agent c: candidates){
			double curScore=0;
			if( seer.contains(c) ) curScore-=20;
			if( yaoGameInfo.getMediums().contains(c)) curScore-=10;
			for( int i =0; i < seer.size(); i++ ){				
				double weight=1+0.2*(seer.size()-i);
				curScore+=weight*yaoGameInfo.getWolfProbability(seer.get(i),c );
			}
			if( curScore>score ){
				score=curScore;
				ret=c;
			}
		}
		return ret;
	}
	
	
	public Agent getRandom(List<Agent> list){
		if( list.size()==0) return null;
		return list.get(new Random().nextInt(list.size()));
	}
	
	/**
	 * カミングアウトの発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void comingoutTalkDealing(Talk talk, Utterance utterance){
		//System.out.println("ComingOut: "+ utterance.getRole());
		switch( utterance.getRole()){
			case SEER:
				yaoGameInfo.comintoutSeer(utterance.getTarget());
				//patternMaker.setSeer(generalPatterns,utterance.getTarget());
				break;
			case MEDIUM:
				yaoGameInfo.comingoutMedium(utterance.getTarget());
				//patternMaker.setMedium(generalPatterns,utterance.getTarget());
			default:
				break;
		}	
	}

	/**
	 * 占い結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void divinedTalkDealing(Talk talk, Utterance utterance){
		yaoGameInfo.devinedUpdate(talk.getAgent(),utterance.getTarget(),utterance.getResult());
	}

	/**
	 * 霊能結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void inquestedTalkDealing(Talk talk, Utterance utterance){
		yaoGameInfo.inquestedUpdate(talk.getAgent(), utterance.getTarget(), utterance.getResult());
	}

	/**
	 * 投票意思の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void voteTalkDealing(Talk talk, Utterance utterance){
		yaoGameInfo.voteUpdate(1+utterance.getTalkDay(), talk.getAgent(), utterance.getTarget());
	}
	
	@Override
	public Agent attack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dayStart() {
		// TODO Auto-generated method stub
		day++;
		readTalkNumber=0;
		yaoGameInfo.attackedUpdate(getLatestDayGameInfo().getAttackedAgent());
		yaoGameInfo.executedUpdate(getLatestDayGameInfo().getExecutedAgent());
		yaoGameInfo.actualVoteUpdate(getLatestDayGameInfo().getVoteList(),day-2);
		if( day>believeSeerDate&& yaoGameInfo.getSeers().size()==0 ) believeSeerDate=Math.max(3,day+1);
		if( day>believeMediumDate&&yaoGameInfo.getMediums().size()==0) believeMediumDate=Math.max(3,day+1);
		toldVoteAgent=null;
	}

	@Override
	public Agent divine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Agent guard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String talk() {
		//thanks to  kajiAgent
		//カミングアウトの発話
		String comingoutReport = getComingoutText();
			if(comingoutReport != null){
				return comingoutReport;
			}

		//占い，霊能結果の発話
		String judgeReport = getJudgeText();
		if(judgeReport != null){
			return judgeReport;
		}
		//投票先の発話
		if(toldVoteAgent != voteAgent && voteAgent != null){
			String voteReport = TemplateTalkFactory.vote(voteAgent);
			toldVoteAgent = voteAgent;
			return voteReport;
		}
		//盲信宣言
		if( toldBelieveSeer!=believeSeer && believeSeer!=null){
			String believeSeerClaim = TemplateTalkFactory.estimate(believeSeer, Role.SEER);
			toldBelieveSeer=believeSeer;
			return believeSeerClaim;
		}
		if( toldBelieveMedium!=believeMedium && believeMedium!=null){
			String believeMediumClaim = TemplateTalkFactory.estimate(believeMedium, Role.MEDIUM);
			return believeMediumClaim;
		}
		
		//話すことが何もなければ
		return Talk.OVER;
		
	}
	public abstract String getJudgeText();
	public abstract String getComingoutText();
	
	@Override
	public Agent vote() {
		// TODO Auto-generated method stub
		return voteAgent;
	}

	@Override
	public String whisper() {
		// TODO Auto-generated method stub
		return null;
	}

}
