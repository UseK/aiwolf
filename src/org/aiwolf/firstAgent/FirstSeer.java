package org.aiwolf.firstAgent;

import java.util.List;
import java.util.ArrayList;
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

public class FirstSeer extends AbstractSeer {
	
	boolean isAlreayComingOuted = false;
	int readTalkNum = 0;
	List<Judge> toledJudgeList = new ArrayList<Judge>();
	List<Agent> fakeSeerAgentList = new ArrayList<Agent>(); 
	GameInfo gameInfo;
	
	@Override
	public void dayStart() {
		super.dayStart();
		readTalkNum = 0;
	}
	
	
	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		this.gameInfo = gameInfo;
		List<Talk> talkList = gameInfo.getTalkList();
		for (int i = readTalkNum; i < talkList.size(); i++) {
			Talk talk = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {
			case AGREE:
				break;
			case ATTACK:
				break;
			case COMINGOUT:
				interpretComingout(utterance, talk);
				break;
			case DISAGREE:
				break;
			case DIVINED:
				break;
			case ESTIMATE:
				break;
			case GUARDED:
				break;
			case INQUESTED:
				break;
			case SKIP:
				break;
			case VOTE:
				break;
			default:
				break;
			}
			readTalkNum++;
		}
	}
	
	
	public void interpretComingout(Utterance utterance, Talk talk) {
		if (utterance.getRole() == Role.SEER &&
		    !talk.getAgent().equals(getMe())) {
			fakeSeerAgentList.add(talk.getAgent());
		}
	}

	@Override
	public Agent divine() {
		List<Agent> divineCandidates = getAliveOthers();
		for(Judge judge: getMyJudgeList()) {
			if (divineCandidates.contains(judge.getTarget())) {
				divineCandidates.remove(judge.getTarget());
			}
		}
		if (divineCandidates.size() > 0) {
			return randomSelect(divineCandidates);
		} else {
			return getMe();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub
		String resultTalk = null;
		resultTalk = hideUntil(1);
		if (resultTalk != null) {
			return resultTalk;
		}
		if (isAlreayComingOuted) {
			for (Judge judge: getMyJudgeList()) {
				if (!toledJudgeList.contains(judge)) {
					resultTalk = TemplateTalkFactory.divined(judge.getTarget(), judge.getResult());
					toledJudgeList.add(judge);
					return resultTalk;
				}
			}
		}
		return resultTalk;
	}
	
	private String comingOutSoon() {
		if (!isAlreayComingOuted) {
			return comingOut();
			}
		else {
			return null;
		}
	}
	
	private String hideUntilFirstWolfDevined() {
		if (!filterWereWolf(getAliveOthers()).isEmpty() && !isAlreayComingOuted) {
			return comingOut();
			}
		else {
			return null;
		}
	}
	
	private String hideUntil(int day) {
		if (isAlreayComingOuted) {
			return null;
		}
		if (!filterWereWolf(getAliveOthers()).isEmpty() || gameInfo.getDay() == day) {
			return comingOut();
			}
		else {
			return null;
		}
	}
	
	private String comingOut() {
		isAlreayComingOuted = true;
		return TemplateTalkFactory.comingout(getMe(), getMyRole());
	}

	@Override
	public Agent vote() {
		if (!fakeSeerAgentList.isEmpty()) {
			for (Agent fakeSeer: fakeSeerAgentList) {
				if (gameInfo.getAliveAgentList().contains(fakeSeer)) {
					return fakeSeer;
				}
			}
		}
		List<Agent> humans = filterHuman(getAliveOthers()),
				    wereWolfs = filterWereWolf(getAliveOthers());
		if (!wereWolfs.isEmpty()) {
			return randomSelect(wereWolfs);
		} else {
			List<Agent> gray = getAliveOthers();
			gray.removeAll(humans);
			return randomSelect(gray);
		}
	}
	
	private List<Agent> getAliveOthers() {
		List<Agent> agentList = new ArrayList<Agent>();
		agentList.addAll(getLatestDayGameInfo().getAliveAgentList());
		agentList.remove(getMe());
		return agentList;
	}
	
	private List<Agent> filterHuman(List<Agent> agentList) {
		List<Agent> filteredList = new ArrayList<Agent>();
		for (Judge judge: getMyJudgeList()) {
			if (agentList.contains(judge.getTarget())) {
				if (judge.getResult() == Species.HUMAN) {
					filteredList.add(judge.getTarget());
				}
			}
		}
		return filteredList;
	}

	private List<Agent> filterWereWolf(List<Agent> agentList) {
		List<Agent> filteredList = new ArrayList<Agent>();
		for (Judge judge: getMyJudgeList()) {
			if (agentList.contains(judge.getTarget())) {
				if (judge.getResult() == Species.WEREWOLF) {
					filteredList.add(judge.getTarget());
				}
			}
		}
		return filteredList;
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
}
