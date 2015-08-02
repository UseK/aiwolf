package agent.aiwolf.usek.lib;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class WerewolfSideThought {
	public int readTalkNum = 0;

	public List<Agent> comingoutedSeerList = new ArrayList<Agent>();
	public List<Agent> comingoutedMediumList = new ArrayList<Agent>();
	public List<Agent> comingoutedBodyguardList = new ArrayList<Agent>();
	public List<Agent> comingoutedPossesedList = new ArrayList<Agent>();

	public Agent getVictim4Attack(GameInfo gameInfo, List<Agent> wolfList, Agent me) {
		List<Agent> victims = new ArrayList<Agent>(gameInfo.getAliveAgentList());
		victims.remove(me);
		for (int i = 0; i < victims.size(); i++) {
			Agent victim = victims.get(i);
			if (wolfList.contains(victim)) {
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

	public void responceUpdatedTalks(GameInfo gameInfo) {
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
}