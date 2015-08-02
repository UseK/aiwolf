package org.aiwolf.kajiClient.LearningPlayer;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;
import org.aiwolf.kajiClient.LearningPlayer.*;


public class KajiRoleAssignPlayer extends AbstractRoleAssignPlayer{
	int ldNumber = 0;
	boolean isLearn = false;

	public KajiRoleAssignPlayer(){
		setVillagerPlayer(new KajiVillagerPlayer());
		setBodyguardPlayer(new KajiBodyGuradPlayer());
		setMediumPlayer(new KajiMediumPlayer());
		setPossessedPlayer(new KajiPossessedPlayer());
		setSeerPlayer(new KajiSeerPlayer());
		setWerewolfPlayer(new KajiWereWolfPlayer());
	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return "LearningKaji";
	}

	public int getLdNumber() {
		return ldNumber;
	}

	public void setLDNumber(int num){
		ldNumber = num;

		KajiVillagerPlayer v = (KajiVillagerPlayer)getVillagerPlayer();
		v.setLD(ldNumber);
		KajiBodyGuradPlayer b = (KajiBodyGuradPlayer)getBodyguardPlayer();
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
		KajiBodyGuradPlayer b = (KajiBodyGuradPlayer)getBodyguardPlayer();
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
