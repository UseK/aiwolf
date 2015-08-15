package agent.aiwolf.usek.lib;

import java.util.ArrayDeque;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class CommonThought {

	public static final Integer DIVINED_HUMAN = -10;
	public static final Integer DIVINED_WEREWOLF = 100;
	public static final Integer NOT_VOTE_DIVINED_WEREWOLF = 100;
	public static final Integer RIVAL_CO = 100;
	public List<Agent> comingoutedSeerList;
	public List<Agent> comingoutedMediumList;
	public List<Agent> comingoutedBodyguardList;
	public List<Agent> comingoutedPossesedList;
	protected Agent me;
	protected Role myRole;
	public Queue<String> talksQueue;
	public ArrayList<String> toldTalksLog;
	public GameInfo gameInfo;

	public CommonThought(GameInfo gameInfo, Role myRole) {
		comingoutedSeerList = new ArrayList<Agent>();
		comingoutedMediumList = new ArrayList<Agent>();
		comingoutedBodyguardList = new ArrayList<Agent>();
		comingoutedPossesedList = new ArrayList<Agent>();
		talksQueue = new ArrayDeque<String>();
		toldTalksLog = new ArrayList<String>();
		me = gameInfo.getAgent();
		this.myRole = myRole;
		this.gameInfo = gameInfo;
	}

	protected boolean hasNeverTold(String t) {
		if (talksQueue.contains(t)) return false;
		if (toldTalksLog.contains(t)) return false;
		return true;
	}

	public String pollTalks() {
		String t = talksQueue.poll();
		toldTalksLog.add(t);
		return t;
	}

	protected Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}

	protected void responseComingout(Utterance utterance, Talk talk) {
		switch (utterance.getRole()) {
		case SEER:
			comingoutedSeerList.add(utterance.getTarget());
			break;
		case MEDIUM:
			comingoutedMediumList.add(utterance.getTarget());
			break;
		case BODYGUARD:
			comingoutedBodyguardList.add(utterance.getTarget());
			break;
		default:
			break;
		}
	}
}