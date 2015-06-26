package org.aiwolf.firstAgent;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.print.attribute.standard.PrinterLocation;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;

public class FirstVillager extends AbstractVillager {
	
	int readTalkNum = 0;
	public List<Agent> comingoutedSeerList = new ArrayList<Agent>();
	List<Agent> divinedWhiteList = new ArrayList<Agent>();
	HashMap<Agent, Integer> suspiciousPoints = new HashMap<Agent, Integer>();
	Agent me;
	GameInfo gameInfo;
	List<List<Vote>> votesEachDay = new ArrayList<>();

	
	public static final Integer DIVINED_HUMAN = -10;
	public static final Integer DIVINED_WEREWOLF = 100;
	
	@Override
	public void initialize(GameInfo gameInfo, 
			org.aiwolf.common.net.GameSetting gameSetting) {
		this.gameInfo = gameInfo;
		for (Agent agent : gameInfo.getAgentList()) {
			suspiciousPoints.put(agent, 0);
		}
		me = gameInfo.getAgent();
		suspiciousPoints.remove(me);
	}

	@Override
	public void dayStart() {
		readTalkNum = 0;
		suspiciousPoints.remove(gameInfo.getAttackedAgent());
		votesEachDay.add(gameInfo.getVoteList());
	}
	
	@Override
	public void update(org.aiwolf.common.net.GameInfo gameInfo) {
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
				interpretDivined(utterance, talk);
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
		if (utterance.getRole() == Role.SEER) {
			comingoutedSeerList.add(talk.getAgent());
		}
	}

	public void interpretDivined(Utterance utterance, Talk talk) {
		System.out.println("Text:" + utterance.getText());
		System.out.println("TalkText:" + talk.toString());
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

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent vote() {
		Agent mostSuspiciousAgent = null;
		Integer maxPoint = -999999999;
		for(Map.Entry<Agent, Integer> e : suspiciousPoints.entrySet()) {
			//System.out.print("agent:" + e.getKey());
			//System.out.println("point:" + e.getValue());
			if (e.getValue() > maxPoint) {
				mostSuspiciousAgent = e.getKey();
				maxPoint = e.getValue();
			}
		}
		
		
		List<Agent> mostSuspiciousAgents = new ArrayList<Agent>();
		for(Map.Entry<Agent, Integer> e : suspiciousPoints.entrySet()) {
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
