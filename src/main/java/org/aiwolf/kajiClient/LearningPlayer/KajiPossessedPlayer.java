package org.aiwolf.kajiClient.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.lib.Pattern;
import org.aiwolf.kajiClient.lib.PatternMaker;
import org.aiwolf.kajiClient.lib.PossessedFakeRoleChanger;
import org.aiwolf.kajiClient.lib.WolfFakeRoleChanger;
import org.aiwolf.kajiClient.LearningPlayer.AbstractKajiWolfSideAgent;
import org.aiwolf.kajiClient.reinforcementLearning.COtimingNeo;
import org.aiwolf.kajiClient.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.kajiClient.reinforcementLearning.WolfRolePattern;

public class KajiPossessedPlayer extends AbstractKajiWolfSideAgent {

	//黒判定を出す確率
	private static final double BLACK_DIVINEJUDGE_PROBABILITY = 0.25;
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
		double learnedQW = ReinforcementLearning.reInforcementLearn(qW, reward, 0);
		changerMap.put(changer, learnedQ);

	}
}
