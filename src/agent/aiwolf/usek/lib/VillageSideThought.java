package agent.aiwolf.usek.lib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;

public class VillageSideThought {
	public static final Integer DIVINED_HUMAN = -10;
	public static final Integer DIVINED_WEREWOLF = 100;
	public static final Integer NOT_VOTE_DIVINED_WEREWOLF = 100;
	public static final Integer RIVAL_CO = 100;

	public List<Agent> comingoutedSeerList;
	public List<Agent> comingoutedMediumList;
	public HashMap<Agent, Integer> suspiciousPoints;

	Agent me;
	Role myRole;

	public Queue<String> talksQueue;
	public ArrayList<String> toldTalksLog;

	public List<Talk> divinedHistory;

	enum SuspiciousPoint { DIVINED_WHITE, DIVINED_BLACK };
	public HashMap<Agent, List<SuspiciousPoint>> agentInfo;

	public VillageSideThought(GameInfo gameInfo, Role myRole) {
		comingoutedSeerList = new ArrayList<Agent>();
		suspiciousPoints = new HashMap<Agent, Integer>();
		talksQueue = new ArrayDeque<String>();
		toldTalksLog = new ArrayList<String>();
		me = gameInfo.getAgent();
		this.myRole = myRole;
		suspiciousPoints = new HashMap<Agent, Integer>();
		agentInfo = new HashMap<Agent, List<SuspiciousPoint>>();
		divinedHistory = new ArrayList<Talk>();

		for (Agent agent : gameInfo.getAgentList()) {
			suspiciousPoints.put(agent, 0);
			agentInfo.put(agent, new ArrayList<SuspiciousPoint>());
		}
		suspiciousPoints.remove(me);
	}

	public void comingoutMyRole() {
		String comingout = TemplateTalkFactory.comingout(me, myRole);
		talksQueue.add(comingout);
	}

	public void inquestedMyjudges(ArrayList<Judge> myJugeList) {
		for (Judge judge: myJugeList) {
			String t = TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
			if (hasNeverTold(t)) talksQueue.add(t);
		}
	}

	private boolean hasNeverTold(String t) {
		if (talksQueue.contains(t)) return false;
		if (toldTalksLog.contains(t)) return false;
		return true;
	}

	public String pollTalks() {
		String t = talksQueue.poll();
		toldTalksLog.add(t);
		return t;
	}

	/*
	 * 狼判定されたエージェントへ投票しなかったエージェントのリスト
	 */
	public List<Agent> getNotVotedToDiviendWerewolfAgents(GameInfo gameInfo) {
		List<Talk> divinedYesterday = new ArrayList<Talk>();
		for (Talk talk : divinedHistory) {
			if (talk.getDay() == gameInfo.getDay() - 1) divinedYesterday.add(talk);
		}

		List<Agent> divinedWEREWOLFYesterdayAgent = new ArrayList<Agent>();
		for (Talk talk : divinedYesterday) {
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getResult() == Species.WEREWOLF) {
				divinedWEREWOLFYesterdayAgent.add(utterance.getTarget());
			}
		}

		List<Agent> resultAgents = new ArrayList<Agent>();
		for (Vote vote : gameInfo.getVoteList()) {
			if (!divinedWEREWOLFYesterdayAgent.contains(vote.getTarget())) {
				resultAgents.add(vote.getAgent());
			}
		}

		return resultAgents;
	}

	public void responseVote(GameInfo gameInfo) {
		for (Agent agent : getNotVotedToDiviendWerewolfAgents(gameInfo)) {
			Integer point = suspiciousPoints.get(agent);
			if (point != null) {
				suspiciousPoints.put(agent, point + NOT_VOTE_DIVINED_WEREWOLF);
			}
		}
	}

	public void removeDeadAgent(GameInfo gameInfo) {
		for (Agent agent: suspiciousPoints.keySet()) {
			if (gameInfo.getAliveAgentList().contains(agent)) {
			}
		}
		suspiciousPoints.remove(gameInfo.getAttackedAgent());

	}

	public void responseComingout(Utterance utterance, Talk talk) {
		if (utterance.getRole() == myRole) {
			int targetPoint = suspiciousPoints.get(talk.getAgent());
			targetPoint += RIVAL_CO;
			suspiciousPoints.put(talk.getAgent(), targetPoint);
		}
		if (utterance.getRole() == Role.SEER) {
			comingoutedSeerList.add(talk.getAgent());
		}
		if (utterance.getRole() == Role.MEDIUM) {
		}
	}

	public void responseDivination(Utterance utterance, Talk talk) {
		//System.out.println("Text:" + utterance.getText());
		//System.out.println("TalkText:" + talk.toString());
		divinedHistory.add(talk);
		Agent target = utterance.getTarget();
		if (target.equals(me)) {
		} else {
			Integer targetPoint = suspiciousPoints.get(target);
			try {
			switch (utterance.getResult()) {
			case WEREWOLF:
				targetPoint += DIVINED_WEREWOLF;
				suspiciousPoints.put(target, targetPoint);
				agentInfo.get(target).add(SuspiciousPoint.DIVINED_BLACK);
				break;
			case HUMAN:
				targetPoint += DIVINED_HUMAN;
				suspiciousPoints.put(target, targetPoint);
				agentInfo.get(target).add(SuspiciousPoint.DIVINED_WHITE);
			default:
				break;
			}
			} catch(NullPointerException e) {
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
			if (e.getValue().equals(maxPoint)) {
				mostSuspiciousAgents.add(e.getKey());
			}
		}
		mostSuspiciousAgent = randomSelect(mostSuspiciousAgents);
		//System.out.println("I vevote " + mostSuspiciousAgent);
		return mostSuspiciousAgent;
	}

	public List<Agent> getMostUnsuspiciousAgents() {
		Agent mostUnsuspiciousAgent = null;
		Integer minPoint = 999999999;
		for(Entry<Agent, Integer> e : suspiciousPoints.entrySet()) {
			if (e.getValue() < minPoint) {
				mostUnsuspiciousAgent = e.getKey();
				minPoint = e.getValue();
			}
		}

		List<Agent> mostUnsuspiciousAgents = new ArrayList<Agent>();
		for(Entry<Agent, Integer> e : suspiciousPoints.entrySet()) {
			if (e.getValue().equals(minPoint)) {
				mostUnsuspiciousAgents.add(e.getKey());
			}
		}

		return mostUnsuspiciousAgents;
	}

	private Agent randomSelect(List<Agent> agentList) {
		int num = new Random().nextInt(agentList.size());
		return agentList.get(num);
	}
}
