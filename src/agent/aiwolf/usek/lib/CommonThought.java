package agent.aiwolf.usek.lib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

public class CommonThought {

	public static final Integer DIVINED_HUMAN = -10;
	public static final Integer DIVINED_WEREWOLF = 100;
	public static final Integer NOT_VOTE_DIVINED_WEREWOLF = 100;
	public static final Integer RIVAL_CO = 100;
	public List<Agent> comingoutedSeerList;
	public List<Agent> comingoutedMediumList;
	protected Agent me;
	protected Role myRole;
	public Queue<String> talksQueue;
	public ArrayList<String> toldTalksLog;

	public CommonThought(GameInfo gameInfo, Role myRole) {
		comingoutedSeerList = new ArrayList<Agent>();
		talksQueue = new ArrayDeque<String>();
		toldTalksLog = new ArrayList<String>();
		me = gameInfo.getAgent();
		this.myRole = myRole;
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

}