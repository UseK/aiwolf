package org.aiwolf.glyClient.LearningPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.fluentd.logger.FluentLogger;

public class RoleAssignPlayer extends AbstractRoleAssignPlayer{
	private final String name;
	private FluentLogger fLogger;
	
	private VillagerPlayer villagerPlayer;
	private BodyGuardPlayer bodyguardPlayer;
	private MediumPlayer mediumPlayer;
	private PossessedPlayer possessedPlayer;
	private SeerPlayer seerPlayer;
	private WereWolfPlayer werewolfPlayer;
	
	int ldNumber = 0;
	boolean isLearn = false;

	public RoleAssignPlayer(){
		name = "Glycine";
		UUID playerID = UUID.randomUUID();
		fLogger = FluentLogger.getLogger("aiwolf", "localhost", 24224);
		
		villagerPlayer = new VillagerPlayer();
		villagerPlayer.playerID = playerID;
		villagerPlayer.fLogger = fLogger;
		villagerPlayer.name = name;
		setVillagerPlayer(this.villagerPlayer);
		
		bodyguardPlayer = new BodyGuardPlayer();
		bodyguardPlayer.playerID = playerID;
		bodyguardPlayer.fLogger = fLogger;
		bodyguardPlayer.name = name;
		setBodyguardPlayer(this.bodyguardPlayer);
		
		mediumPlayer = new MediumPlayer();
		mediumPlayer.playerID = playerID;
		mediumPlayer.fLogger = fLogger;
		mediumPlayer.name = name;
		setMediumPlayer(this.mediumPlayer);
		
		possessedPlayer = new PossessedPlayer();
		possessedPlayer.playerID = playerID;
		possessedPlayer.fLogger = fLogger;
		possessedPlayer.name = name;
		setPossessedPlayer(this.possessedPlayer);
		
		seerPlayer = new SeerPlayer();
		seerPlayer.playerID = playerID;
		seerPlayer.fLogger = fLogger;
		seerPlayer.name = name;
		setSeerPlayer(this.seerPlayer);
		
		werewolfPlayer = new WereWolfPlayer();
		werewolfPlayer.playerID = playerID;
		werewolfPlayer.fLogger = fLogger;
		werewolfPlayer.name = name;
		setWerewolfPlayer(this.werewolfPlayer);
	}

	@Override
	public String getName() {
		return name;
	}

	public int getLdNumber() {
		return ldNumber;
	}

	public void setLDNumber(int num){
		ldNumber = num;

		VillagerPlayer v = (VillagerPlayer)getVillagerPlayer();
		v.setLD(ldNumber);
		BodyGuardPlayer b = (BodyGuardPlayer)getBodyguardPlayer();
		b.setLD(ldNumber);
		MediumPlayer m = (MediumPlayer)getMediumPlayer();
		m.setLD(ldNumber);
		PossessedPlayer p = (PossessedPlayer)getPossessedPlayer();
		p.setLD(ldNumber);
		SeerPlayer s = (SeerPlayer)getSeerPlayer();
		s.setLD(ldNumber);
		WereWolfPlayer w = (WereWolfPlayer)getWerewolfPlayer();
		w.setLD(ldNumber);
	}
	
	public void setIsLearn(boolean islearn){
		VillagerPlayer v = (VillagerPlayer)getVillagerPlayer();
		v.setIS_LEARNING(islearn);
		BodyGuardPlayer b = (BodyGuardPlayer)getBodyguardPlayer();
		b.setIS_LEARNING(islearn);
		MediumPlayer m = (MediumPlayer)getMediumPlayer();
		m.setIS_LEARNING(islearn);
		PossessedPlayer p = (PossessedPlayer)getPossessedPlayer();
		p.setIS_LEARNING(islearn);
		SeerPlayer s = (SeerPlayer)getSeerPlayer();
		s.setIS_LEARNING(islearn);
		WereWolfPlayer w = (WereWolfPlayer)getWerewolfPlayer();
		w.setIS_LEARNING(islearn);
		this.isLearn = islearn;
	}
	
	public boolean isLearn(){
		return isLearn;
	}


}
