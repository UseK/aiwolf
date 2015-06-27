package org.aiwolf.firstAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class VillageSideThought {
	public static final Integer DIVINED_HUMAN = -10;
	public static final Integer DIVINED_WEREWOLF = 100;

	public List<Agent> comingoutedSeerList;
	public HashMap<Agent, Integer> suspiciousPoints;
	Agent me;

	public VillageSideThought() {
		comingoutedSeerList = new ArrayList<Agent>();
		suspiciousPoints = new HashMap<Agent, Integer>();
	}

	public VillageSideThought(GameInfo gameInfo) {
		comingoutedSeerList = new ArrayList<Agent>();
		suspiciousPoints = new HashMap<Agent, Integer>();
		me = gameInfo.getAgent();
		suspiciousPoints = new HashMap<Agent, Integer>();
		for (Agent agent : gameInfo.getAgentList()) {
			suspiciousPoints.put(agent, 0);
		}
		suspiciousPoints.remove(me);

	}

	public void removeDeadAgent(GameInfo gameInfo) {
		suspiciousPoints.remove(gameInfo.getAttackedAgent());

	}

	public void responseComingOut(Utterance utterance, Talk talk) {
		if (utterance.getRole() == Role.SEER) {
			comingoutedSeerList.add(talk.getAgent());
		}
	}

	public void responseDivination(Utterance utterance, Talk talk) {
		//System.out.println("Text:" + utterance.getText());
		//System.out.println("TalkText:" + talk.toString());
		Agent target = utterance.getTarget();
		if (target.equals(me)) {
		} else {
			Integer targetPoint = suspiciousPoints.get(target);
			try {
			switch (utterance.getResult()) {
			case WEREWOLF:
				targetPoint += DIVINED_WEREWOLF;
				suspiciousPoints.put(target, targetPoint);
				break;
			case HUMAN:

				targetPoint += DIVINED_HUMAN;
				suspiciousPoints.put(target, targetPoint);
			default:
				break;
			}
			} catch(NullPointerException e) {
				System.out.println(e.getMessage());

			}
		}
	}

	public Agent getAgentToVote() {
		Agent mostSuspiciousAgent = null;
		Integer maxPoint = -999999999;
		for(Entry<Agent, Integer> e : suspiciousPoints.entrySet()) {
			//System.out.print("agent:" + e.getKey());
			//System.out.println("point:" + e.getValue());
			if (e.getValue() > maxPoint) {
				mostSuspiciousAgent = e.getKey();
				maxPoint = e.getValue();
			}
		}

		List<Agent> mostSuspiciousAgents = new ArrayList<Agent>();
		for(Entry<Agent, Integer> e : suspiciousPoints.entrySet()) {
			if (e.getValue() == maxPoint) {
				mostSuspiciousAgents.add(e.getKey());
			}
		}
		mostSuspiciousAgent = randomSelect(mostSuspiciousAgents);
		System.out.println("I vevote " + mostSuspiciousAgent);
		return mostSuspiciousAgent;
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
}
