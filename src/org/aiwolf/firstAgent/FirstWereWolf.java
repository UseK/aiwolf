package org.aiwolf.firstAgent;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

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

public class FirstWereWolf extends AbstractWerewolf {
	int readTalkNum = 0;
	int readWhisperNum = 0;
	GameInfo gameInfo;

	public List<Agent> comingoutedSeerList = new ArrayList<Agent>();
	public List<Agent> comingoutedMediumList = new ArrayList<Agent>();
	public List<Agent> comingoutedBodyguardList = new ArrayList<Agent>();
	public List<Agent> comingoutedPssesedList = new ArrayList<Agent>();

	public List<Agent> whisperedAttackAgents = new ArrayList<Agent>();

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		this.gameInfo = gameInfo;
	}

	@Override
	public Agent attack() {
		if(!whisperedAttackAgents.isEmpty()) {
			return whisperedAttackAgents.get(whisperedAttackAgents.size() - 1);
		}

		List<Agent> victims = new ArrayList<Agent>(gameInfo.getAliveAgentList());
		victims.remove(getMe());
		for (int i = 0; i < victims.size(); i++) {
			Agent victim = victims.get(i);
			if (getWolfList().contains(victim)) {
				victims.remove(victim);
				continue;
			}
			if (comingoutedSeerList.contains(victim)) {
				return victim;
			}
			if (comingoutedMediumList.contains(victim)) {
				return victim;
			}
			if (comingoutedBodyguardList.contains(victim)) {
				return victim;
			}
		}
		return victims.get(new Random().nextInt(victims.size()));
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
				responseComingout(utterance, talk);
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

		List<Talk> whisperList = new ArrayList<Talk>(gameInfo.getWhisperList());
		for (int i = readWhisperNum; i < whisperList.size(); i++) {
			Talk whisper = whisperList.get(i);
			Utterance utterance = new Utterance(whisper.getContent());
			switch (utterance.getTopic()) {
			case ATTACK:
				whisperedAttackAgents.add(utterance.getTarget());
				break;
			default:
				break;
			}
			readWhisperNum++;
		}

	}

	private void responseComingout(Utterance utterance, Talk talk) {
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

	@Override
	public void dayStart() {
		readTalkNum = 0;
		readWhisperNum = 0;
		whisperedAttackAgents = new ArrayList<Agent>();
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
		List<Agent> victims = new ArrayList<Agent>(gameInfo.getAliveAgentList());
		victims.remove(getMe());
		for (int i = 0; i < victims.size(); i++) {
			Agent victim = victims.get(i);
			if (getWolfList().contains(victim)) {
				victims.remove(victim);
				continue;
			}
		}
		return victims.get(new Random().nextInt(victims.size()));
	}

	@Override
	public String whisper() {
		return null;
	}

}
