package org.aiwolf.firstAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class MediumSideThought extends AbstractMedium {
	boolean isAlreadyComingOuted = false;
	boolean toldLatestJudge = false;
	Set<Agent> whiteSet = new HashSet<Agent>(); // 村人側
	Set<Agent> blackSet = new HashSet<Agent>(); // 人狼側
	Set<Agent> fakeMediumSet = new HashSet<Agent>(); // 偽霊能

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
			if (!blackSet.isEmpty()) {
				// COしていない時は，まずCOする
				if (!isAlreadyComingOuted) {
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
		GameInfo gameInfo = this.getLatestDayGameInfo();
		
		// 怪しい人がいる場合は，そこからランダムで
		List<Agent> blackAgents = new ArrayList<Agent>(blackSet);
		if(!blackAgents.isEmpty())
			return randomSelect(blackAgents);
		
		// 生きている人からランダム投票する
		List<Agent> aliveAgents = gameInfo.getAliveAgentList();
		return randomSelect(aliveAgents);
	}

	@Override
	public void update(GameInfo gameInfo) {
		super.update(gameInfo);
		// 他者の発言を処理する
		// とりあえず，他の人が霊能結果を言っていることに対応
		List<Talk> talkList = gameInfo.getTalkList();
		for (Talk talk : talkList) {
			Agent agent = talk.getAgent();
			Utterance utterance = new Utterance(talk.getContent());
			if (utterance.getTopic() == Topic.INQUESTED) {
				if (!agent.equals(getMe())){
					fakeMediumSet.add(agent);
					blackSet.add(agent);
				}
			}
		}
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
					result = TemplateTalkFactory
							.inquested(agent, Species.HUMAN);
					whiteSet.add(agent);
					break;
				case WEREWOLF:
					result = TemplateTalkFactory.inquested(agent,
							Species.WEREWOLF);
					blackSet.add(agent);
					break;
				}
				return result;
			}

		// 何もなかった時は，とにかく何か返す．通常は来ない
		return result;
	}

	private Agent randomSelect(List<Agent> agents) {
		int index = (int) Math.floor(Math.random() * (double) agents.size());
		return agents.get(index);
	}
}
