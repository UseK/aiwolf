package agent.aiwolf.usek;

import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractBodyguard;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;

import agent.aiwolf.usek.lib.VillageSideThought;

public class UseKBodyguard extends AbstractBodyguard {
	int readTalkNum = 0;
	GameInfo gameInfo;
	VillageSideThought thought;

	@Override
	public void initialize(GameInfo gameInfo,
			org.aiwolf.common.net.GameSetting gameSetting) {
		this.gameInfo = gameInfo;
		thought = new VillageSideThought(gameInfo, Role.BODYGUARD);
	}

	@Override
	public void dayStart() {
		readTalkNum = 0;
		thought.removeDeadAgent(gameInfo);
		thought.responseVote(gameInfo);
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
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
				thought.responseComingout(utterance, talk);
				break;
			case DISAGREE:
				break;
			case DIVINED:
				thought.responseDivination(utterance, talk);
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

	@Override
	public void finish() {
		// TODO Auto-generated method stub
	}

	@Override
	public Agent guard() {
		List<Agent> agents = thought.getMostUnsuspiciousAgents();
		return agents.get(new Random().nextInt(agents.size()));
	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent vote() {
		return thought.getAgentToVote();
	}

}
