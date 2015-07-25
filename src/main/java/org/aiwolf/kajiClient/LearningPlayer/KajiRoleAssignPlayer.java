package org.aiwolf.kajiClient.LearningPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.fluentd.logger.FluentLogger;

public class KajiRoleAssignPlayer extends AbstractRoleAssignPlayer{
	private final String name;
	private FluentLogger fLogger;
	
	private KajiVillagerPlayer villagerPlayer;
	private KajiBodyGuardPlayer bodyguardPlayer;
	private KajiMediumPlayer mediumPlayer;
	private KajiPossessedPlayer possessedPlayer;
	private KajiSeerPlayer seerPlayer;
	private KajiWereWolfPlayer werewolfPlayer;
	
	int ldNumber = 0;
	boolean isLearn = false;

	public KajiRoleAssignPlayer(){
		name = "Glycine";
		UUID playerID = UUID.randomUUID();
		fLogger = FluentLogger.getLogger("debug", "localhost", 24224);
		Map<String, Object> testLog = new HashMap<String, Object>();
		testLog.put("type", "value");
		fLogger.log("test", testLog);
		
		villagerPlayer = new KajiVillagerPlayer();
		villagerPlayer.playerID = playerID;
		villagerPlayer.fLogger = fLogger;
		setVillagerPlayer(this.villagerPlayer);
		
		bodyguardPlayer = new KajiBodyGuardPlayer();
		bodyguardPlayer.playerID = playerID;
		bodyguardPlayer.fLogger = fLogger;
		setBodyguardPlayer(this.bodyguardPlayer);
		
		mediumPlayer = new KajiMediumPlayer();
		mediumPlayer.playerID = playerID;
		mediumPlayer.fLogger = fLogger;
		setMediumPlayer(this.mediumPlayer);
		
		possessedPlayer = new KajiPossessedPlayer();
		possessedPlayer.playerID = playerID;
		possessedPlayer.fLogger = fLogger;
		setPossessedPlayer(this.possessedPlayer);
		
		seerPlayer = new KajiSeerPlayer();
		seerPlayer.playerID = playerID;
		seerPlayer.fLogger = fLogger;
		setSeerPlayer(this.seerPlayer);
		
		werewolfPlayer = new KajiWereWolfPlayer();
		werewolfPlayer.playerID = playerID;
		werewolfPlayer.fLogger = fLogger;
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

		KajiVillagerPlayer v = (KajiVillagerPlayer)getVillagerPlayer();
		v.setLD(ldNumber);
		KajiBodyGuardPlayer b = (KajiBodyGuardPlayer)getBodyguardPlayer();
		b.setLD(ldNumber);
		KajiMediumPlayer m = (KajiMediumPlayer)getMediumPlayer();
		m.setLD(ldNumber);
		KajiPossessedPlayer p = (KajiPossessedPlayer)getPossessedPlayer();
		p.setLD(ldNumber);
		KajiSeerPlayer s = (KajiSeerPlayer)getSeerPlayer();
		s.setLD(ldNumber);
		KajiWereWolfPlayer w = (KajiWereWolfPlayer)getWerewolfPlayer();
		w.setLD(ldNumber);
	}
	
	public void setIsLearn(boolean islearn){
		KajiVillagerPlayer v = (KajiVillagerPlayer)getVillagerPlayer();
		v.setIS_LEARNING(islearn);
		KajiBodyGuardPlayer b = (KajiBodyGuardPlayer)getBodyguardPlayer();
		b.setIS_LEARNING(islearn);
		KajiMediumPlayer m = (KajiMediumPlayer)getMediumPlayer();
		m.setIS_LEARNING(islearn);
		KajiPossessedPlayer p = (KajiPossessedPlayer)getPossessedPlayer();
		p.setIS_LEARNING(islearn);
		KajiSeerPlayer s = (KajiSeerPlayer)getSeerPlayer();
		s.setIS_LEARNING(islearn);
		KajiWereWolfPlayer w = (KajiWereWolfPlayer)getWerewolfPlayer();
		w.setIS_LEARNING(islearn);
		this.isLearn = islearn;
	}
	
	public boolean isLearn(){
		return isLearn;
	}


}
