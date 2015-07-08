package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.common.data.Agent;

public class YaoAgent extends AbstractRoleAssignPlayer {
	
	public YaoAgent(){
		setSeerPlayer(new YaoSeer());
		setVillagerPlayer(new YaoVillager());
		setWerewolfPlayer(new YaoWolf());
		setBodyguardPlayer(new YaoBodyguard());
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
