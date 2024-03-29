package agent.aiwolf.kajiClient.LearningPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import agent.aiwolf.kajiClient.lib.Pattern;
import agent.aiwolf.kajiClient.LearningPlayer.AbstractGiftedPlayer;
import agent.aiwolf.kajiClient.reinforcementLearning.AgentPattern;
import agent.aiwolf.kajiClient.reinforcementLearning.COtiming;
import agent.aiwolf.kajiClient.reinforcementLearning.COtimingNeo;
import agent.aiwolf.kajiClient.reinforcementLearning.Qvalues;
import agent.aiwolf.kajiClient.reinforcementLearning.ReinforcementLearning;
import agent.aiwolf.kajiClient.reinforcementLearning.Scene;

public class KajiSeerPlayer extends AbstractGiftedPlayer {

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
