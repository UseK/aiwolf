package agent.aiwolf.usek;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Talk;

import agent.aiwolf.usek.lib.WerewolfSideThought;

public class UseKWereWolf extends AbstractWerewolf {

	WerewolfSideThought thought;
	int readWhisperNum = 0;
	GameInfo gameInfo;

	public List<Agent> whisperedAttackAgents = new ArrayList<Agent>();

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		this.gameInfo = gameInfo;
		this.thought = new WerewolfSideThought(gameInfo, Role.WEREWOLF);
	}

	@Override
	public Agent attack() {
		if(!whisperedAttackAgents.isEmpty()) {
			return whisperedAttackAgents.get(whisperedAttackAgents.size() - 1);
		}
		return thought.getVictimToAttack(gameInfo, getWolfList(), getMe());
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		this.gameInfo = gameInfo;
		thought.respondUpdatedTalks(gameInfo);
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

	@Override
	public void dayStart() {
		thought.readTalkNum = 0;
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
