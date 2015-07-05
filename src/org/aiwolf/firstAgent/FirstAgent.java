package org.aiwolf.firstAgent;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class FirstAgent extends AbstractRoleAssignPlayer {

	public FirstAgent() {
		// TODO Auto-generated constructor stub
		setSeerPlayer(new FirstSeer());
		setVillagerPlayer(new FirstVillager());
		setMediumPlayer(new FirstCOMedium());
		setWerewolfPlayer(new FirstWereWolf());
		setBodyguardPlayer(new FirstBodyguard());
	}

	@Override
	public String getName() {
		return null;
	}

}
