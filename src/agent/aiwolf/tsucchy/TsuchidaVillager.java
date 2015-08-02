package agent.aiwolf.tsucchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TsuchidaVillager extends AbstractVillager {

	//その日のログの何番目まで読み込んだか
	int readTalkNum = 0;
	//占い師COしているプレイヤーのリスト
    List<Agent> seerCOAgent = new ArrayList<Agent>();
    //霊能者COしているプレイヤーのリスト
    List<Agent> mediumCOAgent = new ArrayList<Agent>();
    //狩人COしているプレイヤーのリスト
    List<Agent> bodyguardCOAgent = new ArrayList<Agent>();
	
	@Override
	public void dayStart() {
		// TODO 自動生成されたメソッド・スタブ
		readTalkNum = 0;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		return "";
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ
		// 役職CO以外を優先してランダム投票	
		List<Agent> voteCandidates = new ArrayList<Agent>();
		List<Agent> secondaryVoteCandidates = new ArrayList<Agent>();
		
		voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		secondaryVoteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		//自分自身と白判定のプレイヤーは候補から外す
		voteCandidates.remove(getMe());
		voteCandidates.remove(seerCOAgent);		
		voteCandidates.remove(mediumCOAgent);
		voteCandidates.remove(bodyguardCOAgent);
		secondaryVoteCandidates.remove(getMe());
		secondaryVoteCandidates.remove(voteCandidates);
		if(voteCandidates.size() > 0) {
			return randomSelect(voteCandidates);
		}else{
			return randomSelect(secondaryVoteCandidates);
		}
	}
	
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
	        	switch (utterance.getRole()) {
	        	case SEER:
	        		//占いCO者をリストに追加
	        		seerCOAgent.add(utterance.getTarget());
	        		break;
	        	case MEDIUM:
	        		//霊能CO者をリストに追加
	        		mediumCOAgent.add(utterance.getTarget());
	        		break;
	        	case BODYGUARD:
	        		//狩人CO者をリストに追加
	        		bodyguardCOAgent.add(utterance.getTarget());
	        		break;
	        	}
	            break;
	        case DIVINED:
	            // 占い結果の発話の処理
	            break;
	        }
	        readTalkNum++;
	    }
	}
	
	//ランダムセレクト
	private Agent randomSelect(List<Agent> agentList){
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

}
