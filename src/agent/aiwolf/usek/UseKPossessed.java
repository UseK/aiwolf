package agent.aiwolf.usek;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractPossessed;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import agent.aiwolf.usek.lib.WerewolfSideThought;

public class UseKPossessed extends AbstractPossessed {

	WerewolfSideThought thought;
	GameInfo gameInfo;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		this.gameInfo = gameInfo;
		this.thought = new WerewolfSideThought(getMe(), this.gameInfo);
		thought.comingoutFakeSeer();
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		this.gameInfo = gameInfo;
		thought.respondUpdatedTalks(gameInfo);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String talk() {
		return thought.pollTalks();
	}

	@Override
	public Agent vote() {
		List<Agent> victims = new ArrayList<Agent>(gameInfo.getAliveAgentList());
		victims.remove(getMe());
		return victims.get(new Random().nextInt(victims.size()));
	}

	@Override
	public void dayStart() {
		if (gameInfo.getDay() == 1) {
			thought.comingoutFakeSeer();
			thought.devineRandomFakeWerewolf();
		}
		thought.readTalkNum = 0;
	}
}
