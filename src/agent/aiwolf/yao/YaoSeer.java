package agent.aiwolf.yao;

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

public class YaoSeer extends AbstractSeer {

	int readTalkNum=0;
	public void dayStart(){
		super.dayStart();
		readTalkNum=0;
	}
	List<Agent> fakeSeerCOAgent=new ArrayList<Agent>();

	public void update(GameInfo gameInfo){
		List<Talk> talkList=gameInfo.getTalkList();
		for( int i =readTalkNum; i<talkList.size(); i++ ){
			Talk talk=talkList.get(i);
			Utterance utterance=new Utterance(talk.getContent());
			switch(utterance.getTopic()){
			case COMINGOUT:
				if(utterance.getRole() == Role.SEER && !talk.getAgent().equals(getMe())){
					fakeSeerCOAgent.add(utterance.getTarget());
				}
				break;
			case DIVINED:
				break;
			}
		}
		
	}
	
	@Override
	public Agent divine() {
		// TODO Auto-generated method stub
		List<Agent> devineCandidates = new ArrayList<Agent>();
		devineCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		devineCandidates.remove(getMe());
		for( Judge judge: getMyJudgeList()){
			if( devineCandidates.contains(judge.getTarget())){
				devineCandidates.remove(judge.getTarget());
			}
		}
		if( devineCandidates.size() >0 ){
			int num=new Random().nextInt(devineCandidates.size());
			return devineCandidates.get(num);
		}
		return getMe();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
	boolean isComingOut=false;
	List<Judge> myToldJudgeList= new ArrayList<Judge>(); 
	@Override
	public String talk() {
		// TODO Auto-generated method stub
		if(!isComingOut){
			for( Judge judge: getMyJudgeList()){
					if( judge.getResult() == Species.WEREWOLF){
						String comingoutTalk=TemplateTalkFactory.comingout(getMe(),getMyRole());
						isComingOut=true;
						return comingoutTalk;
					}
			}
		}
		else{
			for(Judge judge: getMyJudgeList()){
				if(!myToldJudgeList.contains(judge)){
					String resultTalk=TemplateTalkFactory.divined(judge.getTarget(),judge.getResult());
					myToldJudgeList.add(judge);
					return resultTalk;
				}
			}
		}
		return Talk.OVER;
	}

	@Override
	public Agent vote() {
/*
		List<Agent> voteCandidates = new ArrayList<Agent>();
		voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
		voteCandidates.remove(getMe());
		
		// random Select
		int num=new Random().nextInt(voteCandidates.size());
		return voteCandidates.get(num);	
		*/
		List<Agent> whiteAgent = new ArrayList<Agent>();
		List<Agent> blackAgent = new ArrayList<Agent>();
		for( Judge judge: getMyJudgeList() ){
			if( getLatestDayGameInfo().getAliveAgentList().contains(judge.getTarget())){
				switch(judge.getResult()){
				case HUMAN:
					whiteAgent.add(judge.getTarget());
					break;
				case WEREWOLF:
					blackAgent.add(judge.getTarget());
				}
			}
		}
		if( blackAgent.size() > 0 ){
			int ind= new Random().nextInt(blackAgent.size());
			return blackAgent.get(ind);
		}
		else{
			List<Agent> voteCandidates=new ArrayList<Agent>();
			voteCandidates.addAll(getLatestDayGameInfo().getAliveAgentList());
			voteCandidates.remove(getMe());
			voteCandidates.removeAll(whiteAgent);
			int ind=new Random().nextInt(voteCandidates.size());
			return voteCandidates.get(ind);
		}
		
	}

}
