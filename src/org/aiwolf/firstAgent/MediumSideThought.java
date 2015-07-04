package org.aiwolf.firstAgent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

public class MediumSideThought extends AbstractMedium {
	boolean isAlreadyComingOuted = false;
	boolean toldLatestJudge = false;
	Set<Agent> whiteList = new HashSet<Agent>();
	Set<Agent> blackList = new HashSet<Agent>();

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
			String result = genJudgeResult();
			
			// 狼とわかっている人がいる時だけしゃべる
			if(!blackList.isEmpty()){
				// COしていない時は，まずCOする
				if( !isAlreadyComingOuted){
					isAlreadyComingOuted = true;
					return TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
				}
				toldLatestJudge = true;
				return result;
			}
		}

		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		// とりあえず，生きている人からランダム投票する
		GameInfo gameInfo = this.getLatestDayGameInfo();
		List<Agent> aliveAgents = gameInfo.getAliveAgentList();
		return randomSelect(aliveAgents);
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
		
		// 何もなかった時は，とにかく何か返す．通常は来ない
		return result;
	}
	
	private Agent randomSelect(List<Agent> agents){
		int index = (int)Math.floor(Math.random() * (double)agents.size());
		return agents.get(index);
	}
}
