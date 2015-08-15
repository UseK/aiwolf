package agent.aiwolf.usek;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class UseKRoleAssignPleyer extends AbstractRoleAssignPlayer {

	public UseKRoleAssignPleyer() {
		setSeerPlayer(new UseKSeer());
		setVillagerPlayer(new UseKVillager());
		setMediumPlayer(new UseKMedium());
		setBodyguardPlayer(new UseKBodyguard());
		setPossessedPlayer(new UseKPossessed());
		setWerewolfPlayer(new UseKWereWolf());
	}

	@Override
	public String getName() {
		return null;
	}

}
