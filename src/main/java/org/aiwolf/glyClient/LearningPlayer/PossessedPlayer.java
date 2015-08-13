package org.aiwolf.glyClient.LearningPlayer;

import java.util.Map;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.lib.PossessedFakeRoleChanger;
import org.aiwolf.glyClient.reinforcementLearning.COtimingNeo;
import org.aiwolf.glyClient.reinforcementLearning.ReinforcementLearning;

public class PossessedPlayer extends AbstractWolfSideAgent {

	//黒判定を出す確率
	@SuppressWarnings("unused")
	private static final double BLACK_DIVINEJUDGE_PROBABILITY = 0.25;
	@SuppressWarnings("unused")
	private static final double BLACK_INQUESTJUDGE_PROBABILITY = 0.25;

	PossessedFakeRoleChanger changer;
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		//カミングアウトする日数をランダムに設定(0なら日数経過ではカミングアウトしない)
		}


	@Override
	protected void setFakeDivineJudge() {
		setTemplateFakeDivineJudge();
	}



	@Override
	protected void setFakeInquestJudge(Agent executedAgent) {
		setTemplateFakeInquestJudge();

	}

	@Override
	
	public void setVoteTarget() {
		setVoteTargetTemplate(myPatterns);
//		setVoteTargetTemplate(fakePatterns);
	}


	@Override
	void updatePreConditionQVal(boolean isVillagerWin){

//		updatePossessedFakeRoleQval(isVillagerWin);

		//偽役職のCO
		updateCOElements(isVillagerWin);
	}

/*	private void updatePossessedFakeRoleQval(boolean isVillagerWin){
		double reward = (isVillagerWin)? 0.0: 100;
		//偽役職
		Map<Role, Double> map = ld.getPossessedFakeRole();
		double q = map.get(fakeRole);
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		ld.getPossessedFakeRole().put(fakeRole, learnedQ);
		}

*/
	@Override
	protected void initializeFakeRole() {
		Map<PossessedFakeRoleChanger, Double> map = ld.getPossessedFakeRoleChanger();
		if(isIS_LEARNING()){
			changer = selectRandomTarget(map);
		}else {
			changer = selectSoftMaxTarget(map);
		}
		fakeRole = changer.getInitial();
	}




	@Override
	void updateCOElements(boolean isVillagerWin) {
		Map<COtimingNeo, Double> map = getCOMap();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
		
		Map<PossessedFakeRoleChanger, Double> changerMap = ld.getPossessedFakeRoleChanger();
		double qW = changerMap.get(changer);
		@SuppressWarnings("unused")
		double learnedQW = ReinforcementLearning.reInforcementLearn(qW, reward, 0);
		changerMap.put(changer, learnedQ);

	}
}
