package org.aiwolf.firstAgent;

import java.util.Set;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;

public class WolfSideThought extends AbstractWerewolf {
	boolean isAlreayComingOuted = false;
	Set<Agent> wolfSet;
	int readTalkNum = 0;
	GameInfo gameInfo;
	
	@Override
	public Agent attack() {
		
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void dayStart() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String talk() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
