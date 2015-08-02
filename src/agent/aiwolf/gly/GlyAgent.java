package agent.aiwolf.gly;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import agent.aiwolf.gly.thought.MyMedium;
import agent.aiwolf.usek.UseKSeer;
import agent.aiwolf.usek.UseKVillager;

public class GlyAgent extends AbstractRoleAssignPlayer {
	public static final String NAME = "glycine";
	
	public GlyAgent() {
		setSeerPlayer(new UseKSeer());
		setVillagerPlayer(new UseKVillager());
		setMediumPlayer(new MyMedium());
	}

	@Override
	public String getName() {
		return NAME;
	}
}
