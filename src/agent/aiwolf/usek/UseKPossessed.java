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

	WerewolfSideThought werewolfSideThought = new WerewolfSideThought();
	GameInfo gameInfo;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		this.gameInfo = gameInfo;
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		this.gameInfo = gameInfo;
		werewolfSideThought.responceUpdatedTalks(gameInfo);
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
		return victims.get(new Random().nextInt(victims.size()));
	}

	@Override
	public void dayStart() {
		werewolfSideThought.readTalkNum = 0;
	}
}
