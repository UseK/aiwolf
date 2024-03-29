package agent.aiwolf.gly;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.firstAgent.FirstSeer;
import org.aiwolf.firstAgent.FirstVillager;

import agent.aiwolf.gly.thought.MyMedium;

public class GlyAgent extends AbstractRoleAssignPlayer {
	public static final String NAME = "glycine";
	
	public GlyAgent() {
		setSeerPlayer(new FirstSeer());
		setVillagerPlayer(new FirstVillager());
		setMediumPlayer(new MyMedium());
	}

	@Override
	public String getName() {
		return NAME;
	}
}
