package agent.aiwolf.tsucchy;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class TsuchidaRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public TsuchidaRoleAssignPlayer() {
		setVillagerPlayer(new TsuchidaVillager());
		setWerewolfPlayer(new TsuchidaWerewolf());
		setBodyguardPlayer(new TsuchidaBodyguard());
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
