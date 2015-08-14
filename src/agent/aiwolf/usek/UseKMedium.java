package agent.aiwolf.usek;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import agent.aiwolf.usek.lib.VillageSideThought;

public class UseKMedium extends AbstractMedium {

	VillageSideThought thought;
	GameInfo gameInfo;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		this.gameInfo = gameInfo;
		this.thought = new VillageSideThought(gameInfo, getMyRole());

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String talk() {
		return null;
	}

	@Override
	public Agent vote() {
		// TODO Auto-generated method stub
		return null;
	}

}
