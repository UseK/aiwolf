package org.aiwolf.firstAgent;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

public class MediumSideThought extends AbstractMedium {
	boolean isAlreadyComingOuted = false;
	boolean toldLatestJudge = false;
	List<Agent> whiteList = new ArrayList<Agent>();
	List<Agent> blackList = new ArrayList<Agent>();

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

		if (!toldLatestJudge) {
			toldLatestJudge = true;
			return genJudgeResult();
		}

		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	private String genJudgeResult() {
		String result = null;

		GameInfo gameInfo = this.getLatestDayGameInfo();
		Agent agent = gameInfo.getExecutedAgent();
		if (agent == null)
			return null;
		for (Judge judge : getMyJudgeList())
			if (agent.compareTo(judge.getTarget()) == 0) {
				switch (judge.getResult()) {
				case HUMAN:
				default:
					result = TemplateTalkFactory.inquested(agent, Species.HUMAN);
					whiteList.add(agent);
					break;
				case WEREWOLF:
					result = TemplateTalkFactory.inquested(agent,  Species.WEREWOLF);
					blackList.add(agent);
					break;
				}
				return result;
			}
		return result;
	}

}
