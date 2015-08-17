package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.common.data.Agent;

public class YaoRoleAssignPlayer extends AbstractRoleAssignPlayer {
	
	public YaoRoleAssignPlayer(){
		setSeerPlayer(new GrassFoxSeer());
		setPossessedPlayer(new GrassFoxPossessed());
		setVillagerPlayer(new GrassFoxVillager());
		setWerewolfPlayer(new GrassFoxWolf());
		setBodyguardPlayer(new GrassFoxBodyguard());
		setMediumPlayer(new GrassFoxMedium());
	}

	
	// only for debug
	public YaoRoleAssignPlayer(boolean flag){
		setSeerPlayer(new GrassFoxSeer());
		setPossessedPlayer(new GrassFoxPossessed());
		setWerewolfPlayer(new GrassFoxWolf());
		setBodyguardPlayer(new GrassFoxBodyguard());
		setMediumPlayer(new GrassFoxMedium());
	
		GrassFoxVillager p = new GrassFoxVillager();
		p.setPrint(flag);
		setVillagerPlayer(p);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GrassFox";
	}

}
