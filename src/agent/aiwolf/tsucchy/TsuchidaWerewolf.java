package agent.aiwolf.tsucchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TsuchidaWerewolf extends AbstractWerewolf {
	
	//その日のログの何番目まで読み込んだか
	int readTalkNum = 0;
	//占い師COしているプレイヤーのリスト
    List<Agent> seerCOAgent = new ArrayList<Agent>();
    //霊能者COしているプレイヤーのリスト
    List<Agent> mediumCOAgent = new ArrayList<Agent>();
    //狩人COしているプレイヤーのリスト
    List<Agent> bodyguardCOAgent = new ArrayList<Agent>();
    //狼のリスト
    List<Agent> werewolfAgent = new ArrayList<Agent>();

	@Override
	public Agent attack() {
		// TODO 自動生成されたメソッド・スタブ
		List<Agent> attackCandidates = new ArrayList<Agent>();
		
		//アタックリスト
		attackCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		
		//リストから狼を除外
		attackCandidates.remove(getWolfList());

		//狩人優先
		if(!bodyguardCOAgent.isEmpty()) {
			return(checkNonWolfCOAgent(bodyguardCOAgent));
		}
		//役職持ちによらず、6人以下ならランダム
		if(getLatestDayGameInfo().getAliveAgentList().size() <= 6) {
			
			return(randomSelect(attackCandidates));
		}
		//霊能>占いの優先順位
		if(!mediumCOAgent.isEmpty()) {
			return(checkNonWolfCOAgent(mediumCOAgent));
		}
		if(!seerCOAgent.isEmpty()) {
			return(checkNonWolfCOAgent(seerCOAgent));
		}
		
		return(randomSelect(attackCandidates));
	}

	//狼以外の役職CO者をランダムで返す
	private Agent checkNonWolfCOAgent(List<Agent> agentList) {
		if(agentList.isEmpty()) {
			return null;
		}
		List<Agent> nonWolfAgent = new ArrayList<Agent>();
		for (Agent agent : agentList) {
			for (Agent wolf : werewolfAgent) {
				if (agent.getAgentIdx() != wolf.getAgentIdx()) {
					nonWolfAgent.add(agent);
					break;
                }
			}
		}
		return randomSelect(nonWolfAgent);
	}
	
	@Override
	public void dayStart() {
		// TODO 自動生成されたメソッド・スタブ
		readTalkNum = 0;
		//狼リストの取得（一回だけで良い）
		werewolfAgent = getWolfList();
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}
	
	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
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
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
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
