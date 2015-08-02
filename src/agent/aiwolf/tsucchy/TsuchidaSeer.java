package agent.aiwolf.tsucchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractSeer;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TsuchidaSeer extends AbstractSeer {

	//その日のログの何番目まで読み込んだか
	int readTalkNum = 0;

	@Override
	public Agent divine() {
		// TODO 自動生成されたメソッド・スタブ
		//占い対象の候補者リスト
		List<Agent> divineCandidates = new ArrayList<Agent>();
		
		divineCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		//自分自身と既に占ったことのあるプレイヤーは候補から外す
		divineCandidates.remove(getMe());
		for(Judge judge: getMyJudgeList()){
			if(divineCandidates.contains(judge.getTarget())){
				divineCandidates.remove(judge.getTarget());
			}
		}
		
		if(divineCandidates.size() > 0){
			//候補者リストからランダムに選択
			return randomSelect(divineCandidates);
		} else {
			//候補者がいない場合は自分を占い
			return getMe();
		}
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	boolean isComingOut = false;
	
	
	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		List<Agent> myToldJudgeList = new ArrayList<Agent>();
		//占いで人狼を見つけたらカミングアウトする
	    if(!isComingOut){
	        for(Judge judge: getMyJudgeList()){
	            if(judge.getResult() == Species.WEREWOLF){ //占い結果が人狼の場合
	                String comingoutTalk = TemplateTalkFactory.comingout(getMe(), getMyRole());
	                isComingOut = true;
	                return comingoutTalk;
	            }
	        }
	    }
	    //カミングアウトした後は，まだ言っていない占い結果を順次報告
	    else{
	        for(Judge judge: getMyJudgeList()){
	            if(!myToldJudgeList.contains(judge)){ //まだ報告していないJudgeの場合
	                String resultTalk = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
	                myToldJudgeList.add(judge.getTarget());
	                return resultTalk;
	            }
	        }
	    }

		return Talk.OVER;
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ
		//投票対象の候補者リスト
		//List<Agent> voteCandidates = new ArrayList<Agent>();
		//Case1 ランダム投票
		/*
		//生きているプレイヤーを候補者リストに加える
		voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());

		//自分自身と白判定のプレイヤーは候補から外す
		voteCandidates.remove(getMe());

		return randomSelect(voteCandidates);
		*/
		
		//Case2 
		List<Agent> whiteAgent = new ArrayList<Agent>(), //白判定だったプレイヤー
				blackAgent = new ArrayList<Agent>(); //黒判定だったプレイヤー

		//今まで占ったプレイヤーをwhiteAgentとblackAgentに分ける
		for(Judge judge: getMyJudgeList()){
			if(getLatestDayGameInfo().getAliveAgentList().contains(judge.getTarget())){
				switch (judge.getResult()) {
				case HUMAN:
					whiteAgent.add(judge.getTarget());
					break;
				case WEREWOLF:
					blackAgent.add(judge.getTarget());
				}
			}
		}
		
		if(blackAgent.size() > 0){
			//blackAgentがいればその中から選択
			return randomSelect(blackAgent);
		}else{
			//投票対象の候補者リスト
			List<Agent> voteCandidates = new ArrayList<Agent>();
			
			voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
			
			//自分自身と白判定のプレイヤーは候補から外す
			voteCandidates.remove(getMe());
			voteCandidates.removeAll(whiteAgent);
			
			return randomSelect(voteCandidates);
		}
	}

	private Agent randomSelect(List<Agent> agentList){
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
	
	@Override
	public void dayStart(){
	    super.dayStart();
	    readTalkNum = 0;
	}

	 //偽占い師COしているプレイヤーのリスト
    List<Agent> fakeSeerCOAgent = new ArrayList<Agent>();

	@Override
	public void update(GameInfo gameInfo) {
	    super.update(gameInfo);
	    //今日のログを取得
	    List<Talk> talkList = gameInfo.getTalkList();

	    for(int i = readTalkNum; i < talkList.size(); i++){
	        Talk talk = talkList.get(i);
	        //発話をパース
	        Utterance utterance = new Utterance(talk.getContent());
	        
	        //発話のトピックごとに処理
	        switch (utterance.getTopic()) {
	        case COMINGOUT:
	            //カミングアウトの発話の処理
	        	//自分以外で占い師COしているプレイヤーの場合
                if(utterance.getRole() == Role.SEER && !talk.getAgent().equals(getMe())){
                        fakeSeerCOAgent.add(utterance.getTarget());
                }
	            break;
	        case DIVINED:
	            // 占い結果の発話の処理
	            break;
	        }
	        readTalkNum++;
	    }
	}

}
