package org.aiwolf.glyClient.LearningPlayer;

import java.util.HashMap;
import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.glyClient.lib.Pattern;
import org.aiwolf.glyClient.reinforcementLearning.AgentPattern;
import org.aiwolf.glyClient.reinforcementLearning.Qvalues;
import org.aiwolf.glyClient.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.glyClient.reinforcementLearning.Scene;

public class BodyGuardPlayer extends AbstractBasePlayer {

	@Override
	public String getJudgeText() {
		return null;
	}

	@Override
	public String getComingoutText() {
		return null;
	}

	@Override
	public void setVoteTarget() {
		setVoteTargetTemplate(myPatterns);
	}

	@Override
	public Agent guard() {
		Map<Agent, Double> agentPoint = new HashMap<Agent, Double>();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList()){
			double point = 0.0;
			for(Pattern p: myPatterns){
				Scene s = new Scene(p);
				AgentPattern ap = p.getAgentPattern(agent);
				Qvalues qVal = ld.getQvalue(s.getHashNum());
				point += qVal.getHunterGuard().get(ap) * (double)qVal.getLikelihood();
			}
			agentPoint.put(agent, point);
		}
		return selectGreedyTarget(agentPoint);
	}

	@Override
	void updateMiddlePattern(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		super.updateMiddlePattern(day, patternPresent, patternNext, scenePresent, sceneNext);

		//護衛の学習
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());
		AgentPattern ap = patternPresent.getAgentPattern(getGameInfo(day).getGuardedAgent());
		Map<AgentPattern, Double>
				map = qVal.getHunterGuard(),
				mapNext = qValNext.getHunterVote();
		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap), 0.0, nextMaxQVal);
		map.put(ap, learnedQ);
	}

}
