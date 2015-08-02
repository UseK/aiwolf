package org.aiwolf.glyClient.LearningPlayer;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.lib.Pattern;
import org.aiwolf.glyClient.reinforcementLearning.AgentPattern;
import org.aiwolf.glyClient.reinforcementLearning.COtimingNeo;
import org.aiwolf.glyClient.reinforcementLearning.Qvalues;
import org.aiwolf.glyClient.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.glyClient.reinforcementLearning.Scene;

public class SeerPlayer extends AbstractGiftedPlayer {

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		//カミングアウトする日数をランダムに設定(0なら日数経過ではカミングアウトしない)
		super.initialize(gameInfo, gameSetting);
	}

	@Override
	public void dayStart() {
		super.dayStart();
		if(getLatestDayGameInfo().getDivineResult() != null){
			notToldjudges.add(getLatestDayGameInfo().getDivineResult());
		}
	}

	@Override
	public String getJudgeText() {
		if(isComingout && notToldjudges.size() != 0){
			String talk = TemplateTalkFactory.divined(notToldjudges.get(0).getTarget(), notToldjudges.get(0).getResult());
			toldjudges.add(notToldjudges.get(0));
			notToldjudges.remove(0);
			return talk;
		}
		return null;
	}

	@Override
	public String getComingoutText() {
		return getTemplateComingoutText();
	}

	@Override
	public void setVoteTarget() {
		setVoteTargetTemplate(myPatterns);
	}

	@Override
	public Agent divine() {
		Map<Agent, Double> agentPoint = new HashMap<Agent, Double>();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList()){
			double point = 0.0;
			for(Pattern p: myPatterns){
				Scene s = new Scene(p);
				AgentPattern ap = p.getAgentPattern(agent);
				Qvalues qVal = ld.getQvalue(s.getHashNum());
				point += qVal.getSeerDivine().get(ap) * (double)qVal.getLikelihood();
			}
			agentPoint.put(agent, point);
		}

		Agent target = selectGreedyTarget(agentPoint);
		if(target == null){
			System.out.println("");
		}
		return target;
	}

	@Override
	void updatePreConditionQVal(boolean isVillagerWin){
		Map<COtimingNeo, Double> map = ld.getSeerCO();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
	}


	@Override
	void updateMiddlePattern(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		super.updateMiddlePattern(day, patternPresent, patternNext, scenePresent, sceneNext);

		//占いの学習
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());
		AgentPattern ap = patternPresent.getAgentPattern(getGameInfo(day).getDivineResult().getTarget());
		Map<AgentPattern, Double>
				map = qVal.getSeerDivine(),
				mapNext = qValNext.getSeerVote();
		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap), 0.0, nextMaxQVal);
		map.put(ap, learnedQ);
	}
}
