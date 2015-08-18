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
 * �S��E���ʂ̓����������Œ�`����
 * @author Aoshima
 */
public abstract class AbstractYaoBasePlayer extends AbstractRole { 
	
	public YaoGameInfo yaoGameInfo=null; //�������瓾�������Z�߂�
	//private List<Pattern> generalPatterns;
	private int readTalkNumber=0;
	private Agent voteAgent=null;
	private Agent toldVoteAgent=null;
	private int state=0;
	//private PatternMaker patternMaker = new PatternMaker();
	public int day=0;
	public double eval[];//�e�l�̕]���l��[0-100]�Ŋi�[���Ă����B
	public Agent believeSeer=null;  //�肢�ӐM���Ă��炱���ɂ��̐肢�������
	public Agent believeMedium=null;//��\�ӐM���Ă����炱���ɂ��̗�\�������
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
	 * ��b�̏���
	 * �b�蓊�[��̍X�V
	 * @param gameInfo
	 */
	public void update(GameInfo gameInfo){
		super.update(gameInfo);

		//yaoGameInfo.alivedUpdate(gameInfo.getAliveAgentList()); �P���E���Y���݂Ă��alived�͎��R�Ɖ����ȁc
		
		List<Talk> talkList = gameInfo.getTalkList();
		/*
		 * �e���b�ɂ��Ă̏���
		 * �J�~���O�A�E�g�ɂ��Ă̓p�^�[���̊g��
		 * �\�͌��ʂ̔��b�ɂ��Ă̓p�^�[�����̍X�V
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
			//��L�ȊO
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
	 * �헪�����肷�� 
	 * ��O��F�����͔j�]���Ȃ�����l���Ȃ����Ƃɂ���B
	 * ���R�F��E�����͊�{�I�ɂ��̃��[���ɂ����Ĉ��肾�Ǝv���B
	 * ��\�̊��܂�h�~�͊m���Ƀ����b�g�����ǂ��A�����������������R�������đ������ł��Ă͂��߂Đ��藧�B
	 */
	private void setStrategy(){
		List<Agent> seers=yaoGameInfo.getSeers();
		List<Agent> mediums=yaoGameInfo.getMediums();
		List<Agent> enemies=yaoGameInfo.getEnemies();
		List<Agent> aliveenemies=yaoGameInfo.getAliveEnemies();
		
		//�M���Ă����肢�E��\���q�ϓI�ɓG���Ɣ��������ꍇ�͍l���Ȃ���
		if( believeSeer!=null&&believeMedium!=null&&yaoGameInfo.linePossible(believeSeer,believeMedium)==false){
			believeSeer=null;
			believeMedium=null;
		}
		if( believeSeer!=null&&enemies.contains(believeSeer) ) believeSeer=null;
		if( believeSeer!=null&&enemies.contains(believeMedium) ) believeMedium=null;
			
		if( day>=believeSeerDate || day >= believeMediumDate){
			// 3���ڎ��_�Ő^�̉\��������肢�E��\���P�l�������ꍇ�͂��̐肢��100%�M����B�����͔j�]���Ȃ�����l���Ȃ��B
			// ���A�P�Ƃ��āA2���I�����OCO���Đ^�肢�E�^��\���΍RCO����Ԃ�����������I������ꍇ���h���Ȃ����A���̏ꍇ�͕s�^�������Ǝv�����ƂƂ���B
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
			// ����believeSeer���_�Ŗ������Ȃ���\����l�������Ȃ��̂Ȃ�A���̐l��M����
			if( yaoGameInfo.getMediumCandidatesOfSeer(believeSeer).size()==1){
				believeMedium=yaoGameInfo.getMediumCandidatesOfSeer(believeSeer).get(0);
			}
		}
		else if( believeMedium!=null&&yaoGameInfo.getEnemiesOfMedium(believeMedium)!=null){
			// ����believeMedium���_�Ŗ������Ȃ���\����l�������Ȃ��̂Ȃ�A���̐l��M����
			if( yaoGameInfo.getSeerCandidatesOfMedium(believeMedium).size()==1){
				believeSeer=yaoGameInfo.getSeerCandidatesOfMedium(believeMedium).get(0);
			}
		}
		
		aliveenemies=yaoGameInfo.getAliveEnemies(believeSeer,believeMedium);		
		
		//�j�]�̐l����l�ł������Ă���̂ł���΁A���̐l��݂�t���O�𗧂Ă�
		if( aliveenemies.size()>0){	
			if( printFlag )System.out.println("there is"+ aliveenemies.size()+ "enemies");
			state |= State.EnemyKiller;
		}
		else{ 
			state &= ~State.EnemyKiller; 
		}

		//��\��2�l�ȏ㑶�݂��鎞�A�肢��2�l�ȉ��Ȃ烉�C����A3-2�ȏ�̐i�s���A��\3�l�ȏ�Ȃ��E���[���[
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
		//�肢���S�l�ȏ㑶�݂���ΐ肢���[���[
		if( believeSeer==null && seers.size() >= 4 ){
			if(printFlag)System.out.println("too many seers! I can't believe it!");
			state |= State.SeerRoller;
		}
		
	}
	/**
	 * ���[������肷��
	 */
	private void setVoteTarget(){
		if(printFlag)System.out.println(Integer.toBinaryString(state));
		if( (state&State.EnemyKiller) >0 ){
			//�m�荕��ނ肽��
			if(printFlag)System.out.println("EnemyKiller");
			List<Agent> aliveEnemies=yaoGameInfo.getAliveEnemies(believeSeer,believeMedium);
			if( aliveEnemies.contains(voteAgent) ) return;
			voteAgent=getRandom(aliveEnemies);
			if( voteAgent!=null )return;
		}
		if( (state&State.MediumRoller) > 0 ){
			//��\���[���[
			if(printFlag)System.out.println("MediumKiller");
			List <Agent> aliveMediums=yaoGameInfo.getAliveMediums();
			if( aliveMediums.contains(voteAgent) ) return;
			voteAgent=getRandom(aliveMediums);
			if( voteAgent!=null )return;
		}
		if( (state&State.SeerRoller) > 0 ){
			//�肢���[���[
			if(printFlag)System.out.println("SeerKiller");
			List <Agent> aliveSeers=yaoGameInfo.getAliveSeers();
			if( aliveSeers.contains(voteAgent) ) return;
			voteAgent=getRandom(aliveSeers);
			if( voteAgent!=null )return;
		}
		List<Agent> checkingSeers=yaoGameInfo.getSeers();;
		sortSeers(checkingSeers);

		//�ɗ͑����̐肢�őS���̃��C�����ǂ��鎞�ɂ͂��̒��ōœK�Ȑl��Ԃ�
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
		
		//�����_��
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
	 * �J�~���O�A�E�g�̔��b�̏���
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
	 * �肢���ʂ̔��b�̏���
	 * @param talk
	 * @param utterance
	 */
	public void divinedTalkDealing(Talk talk, Utterance utterance){
		yaoGameInfo.devinedUpdate(talk.getAgent(),utterance.getTarget(),utterance.getResult());
	}

	/**
	 * ��\���ʂ̔��b�̏���
	 * @param talk
	 * @param utterance
	 */
	public void inquestedTalkDealing(Talk talk, Utterance utterance){
		yaoGameInfo.inquestedUpdate(talk.getAgent(), utterance.getTarget(), utterance.getResult());
	}

	/**
	 * ���[�ӎv�̔��b�̏���
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
		//�J�~���O�A�E�g�̔��b
		String comingoutReport = getComingoutText();
			if(comingoutReport != null){
				return comingoutReport;
			}

		//�肢�C��\���ʂ̔��b
		String judgeReport = getJudgeText();
		if(judgeReport != null){
			return judgeReport;
		}
		//���[��̔��b
		if(toldVoteAgent != voteAgent && voteAgent != null){
			String voteReport = TemplateTalkFactory.vote(voteAgent);
			toldVoteAgent = voteAgent;
			return voteReport;
		}
		//�ӐM�錾
		if( toldBelieveSeer!=believeSeer && believeSeer!=null){
			String believeSeerClaim = TemplateTalkFactory.estimate(believeSeer, Role.SEER);
			toldBelieveSeer=believeSeer;
			return believeSeerClaim;
		}
		if( toldBelieveMedium!=believeMedium && believeMedium!=null){
			String believeMediumClaim = TemplateTalkFactory.estimate(believeMedium, Role.MEDIUM);
			return believeMediumClaim;
		}
		
		//�b�����Ƃ������Ȃ����
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
