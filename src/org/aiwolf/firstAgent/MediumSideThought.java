package org.aiwolf.firstAgent;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

public class MediumSideThought extends AbstractMedium {
	boolean isAlreadyComingOuted = false;
	boolean toldLatestJudge = false;

	@Override
	public void dayStart() {
		super.dayStart();
		toldLatestJudge = false;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String talk() {
		GameInfo gameInfo = this.getLatestDayGameInfo();
		Agent executedAgent = gameInfo.getExecutedAgent();
		String result = null;
		
		if (!toldLatestJudge) {
			for (Judge judge : getMyJudgeList()) {
				if (executedAgent != null
						&& executedAgent.compareTo(judge.getTarget()) == 0) {
					switch (judge.getResult()) {
					case HUMAN:
					default:
						result = TemplateTalkFactory.inquested(executedAgent, Species.HUMAN);
						break;
					case WEREWOLF:
						result = TemplateTalkFactory.inquested(executedAgent, Species.WEREWOLF);
						break;
					}
				}
			}
		}else
			result = TemplateTalkFactory.over();
		return result;
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
