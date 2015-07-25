package org.aiwolf.kajiClient.LearningPlayer;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;


public class KajiRoleAssignPlayer extends AbstractRoleAssignPlayer{
	private final String name;
	private AbstractRole villageRole;
	private AbstractRole bodyguardRole;
	private AbstractRole mediumRole;
	private AbstractRole possessedRole;
	private AbstractRole seerRole;
	private AbstractRole werewolfRole;
	
	int ldNumber = 0;
	boolean isLearn = false;

	public KajiRoleAssignPlayer(){
		this.name = "Glycine";
		this.villageRole = new KajiVillagerPlayer();
		this.bodyguardRole = new KajiBodyGuardPlayer();
		this.mediumRole = new KajiMediumPlayer();
		this.possessedRole = new KajiPossessedPlayer();
		this.seerRole = new KajiSeerPlayer();
		this.werewolfRole = new KajiWereWolfPlayer();
		setVillagerPlayer(this.villageRole);
		setBodyguardPlayer(this.bodyguardRole);
		setMediumPlayer(this.mediumRole);
		setPossessedPlayer(this.possessedRole);
		setSeerPlayer(this.seerRole);
		setWerewolfPlayer(this.werewolfRole);
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
