package org.aiwolf.firstAgent;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class FirstAgent extends AbstractRoleAssignPlayer {
	
	public FirstAgent() {
		// TODO Auto-generated constructor stub
		setSeerPlayer(new FirstSeer());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		int i = 1;
		System.out.println(i);
		return null;
	}

}
