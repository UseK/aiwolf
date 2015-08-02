package agent.aiwolf.usek;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import agent.aiwolf.gly.thought.MyMedium;

public class UseKRoleAssignPleyer extends AbstractRoleAssignPlayer {

	public UseKRoleAssignPleyer() {
		// TODO Auto-generated constructor stub
		setSeerPlayer(new UseKSeer());
		setVillagerPlayer(new UseKVillager());
		setMediumPlayer(new MyMedium());
		setWerewolfPlayer(new UseKWereWolf());
		setBodyguardPlayer(new UseKBodyguard());
	}

	@Override
	public String getName() {
		return null;
	}

}
