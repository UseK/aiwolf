package org.aiwolf.glyClient.LearningPlayer;

import java.util.Map;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.reinforcementLearning.COtimingNeo;
import org.aiwolf.glyClient.reinforcementLearning.ReinforcementLearning;

public class MediumPlayer extends AbstractGiftedPlayer {


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		//カミングアウトする日数をランダムに設定(0なら日数経過ではカミングアウトしない)
	}

	@Override
	public void dayStart() {
		super.dayStart();
		if(getLatestDayGameInfo().getMediumResult() != null){
			notToldjudges.add(getLatestDayGameInfo().getMediumResult());
		}
	}

	@Override
	public String getJudgeText() {
		if(isComingout && notToldjudges.size() != 0){
			String talk = TemplateTalkFactory.inquested(notToldjudges.get(0).getTarget(), notToldjudges.get(0).getResult());
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
	void updatePreConditionQVal(boolean isVillagerWin){
		Map<COtimingNeo, Double> map = getCOMap();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
	}
}
