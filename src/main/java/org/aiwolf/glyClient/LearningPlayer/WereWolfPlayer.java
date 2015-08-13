package org.aiwolf.glyClient.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.lib.Pattern;
import org.aiwolf.glyClient.lib.PatternMaker;
import org.aiwolf.glyClient.lib.WolfFakeRoleChanger;
import org.aiwolf.glyClient.reinforcementLearning.AgentPattern;
import org.aiwolf.glyClient.reinforcementLearning.COtimingNeo;
import org.aiwolf.glyClient.reinforcementLearning.Qvalues;
import org.aiwolf.glyClient.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.glyClient.reinforcementLearning.Scene;
import org.aiwolf.glyClient.reinforcementLearning.SelectStrategy;

public class WereWolfPlayer extends AbstractWolfSideAgent {

	//狂人のAgent．不確定の時はnull
	Agent possessedAgent = null;

	//Whisperで自分の騙る役職を伝えたか
	boolean hasWhisperedFakeRole = false;

	//人狼達のfakeRoleに矛盾が起こらないPatterns
	List<Pattern> wolfsPatterns;

	//仲間人狼のfakeRole
	Map<Agent, Role> wolfsFakeRoleMap = new HashMap<Agent, Role>();

	//Whisperをどこまで読んだか
	int readWhisperNumber = 0;

	//今日の嘘JudgeをWhisperで伝えたか
	boolean hasWhisperTodaysFakeJudge;

	//今日の嘘Judge
	Judge todaysFakeJudge;

	//WhisperされたJudgeのリスト
	List<Judge> whisperedJudges = new ArrayList<Judge>();
	
	WolfFakeRoleChanger changer;
	
	@SuppressWarnings("unused")
	private boolean wolfJudged = false; //人狼だと占われた時に騙る役職
	private boolean existVillagerWolf = false; //相方の人狼が村人を騙るといった時に騙る役職
	private boolean existSeerWolf = false;
	private boolean existMediumWolf = false;
	@SuppressWarnings("unused")
	private boolean seerCO = false; //占い師が出てきたときに騙る役職
	@SuppressWarnings("unused")
	private boolean mediumCO = false;
	@SuppressWarnings("unused")
	private boolean isVoteTarget = false;//投票対象になった時に騙る役職


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);

		//myPatternsに仲間の人狼をセットする
		for(Entry<Agent, Role> set: gameInfo.getRoleMap().entrySet()){
			if(!set.getKey().equals(getMe())){
				PatternMaker.settleAgentRole(myPatterns, set.getKey(),
						Role.WEREWOLF);
			}
		}
		wolfsFakeRoleMap.put(getMe(), fakeRole);
		wolfsPatterns = new ArrayList<Pattern>(fakePatterns);
	}


	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);

		//whisperの処理

		List<Talk> whisperList = gameInfo.getWhisperList();

		/*
		 * 各発話についての処理
		 * カミングアウトについてはパターンの拡張
		 * 能力結果の発話についてはパターン情報の更新
		 */
		for(; readWhisperNumber < whisperList.size(); readWhisperNumber++){
			Talk talk = whisperList.get(readWhisperNumber);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {
			case COMINGOUT:
				comingoutWhisperDealing(talk, utterance);
				break;

			case DIVINED:
				divinedWhisperDealing(talk, utterance);
				break;

			case INQUESTED:
				inquestedWhisperDealing(talk, utterance);
				break;

			case VOTE:
				voteWhisperDealing(talk, utterance);
				break;
			//上記以外
			default:
				break;
			}
		}
	}

	@Override
	public void dayStart(){
		super.dayStart();
		readWhisperNumber = 0;
		hasWhisperTodaysFakeJudge = false;
	}


	@Override
	public String whisper(){
		/*
		 * 自分の騙り役職を最初に報告する
		 * fakeJudgeの結果を報告する
		 */
		if(!hasWhisperedFakeRole){
			hasWhisperedFakeRole = true;
			return TemplateWhisperFactory.comingout(getMe(), fakeRole);
		}else{
			if(fakeRole == Role.SEER || fakeRole == Role.MEDIUM){
				if(!hasWhisperTodaysFakeJudge && todaysFakeJudge != null){
					switch (fakeRole) {
					case SEER:
					default:
						hasWhisperTodaysFakeJudge = true;
						return TemplateWhisperFactory.divined(todaysFakeJudge.getTarget(), todaysFakeJudge.getResult());
					case MEDIUM:
						hasWhisperTodaysFakeJudge = true;
						return TemplateWhisperFactory.inquested(todaysFakeJudge.getTarget(), todaysFakeJudge.getResult());
					}
				}
			}
		}
		return TemplateWhisperFactory.over();
	}

	private void comingoutWhisperDealing(Talk talk, Utterance utterance){
		/*
		 * wolfsPatternsを更新する
		 */
		wolfsFakeRoleMap.put(talk.getAgent(), utterance.getRole());
		if(getDay() == 0){
			patternChange(utterance.getRole());
		}
		PatternMaker.settleAgentRole(wolfsPatterns, talk.getAgent(), utterance.getRole());
	}

	private void patternChange(Role role) {
		if(getDay() != 0 && isComingout) return;

		Role preFake = fakeRole;
		switch (role) {
		case VILLAGER:
			if(!existVillagerWolf){
				fakeRole = changer.getExistVillagerWolf();
				existVillagerWolf = true;
			}
			break;
		case SEER:
			if(!existSeerWolf){
				fakeRole = changer.getExistSeerWolf();
				existSeerWolf = true;
			}
			break;
		case MEDIUM:
			if(!existMediumWolf){
				fakeRole = changer.getExistMediumWolf();
				existMediumWolf = true;
			}
			break;
		default:
			break;
		}
		
		if(preFake != fakeRole){
			List<Agent> alives = fakePatterns.get(0).getAliveAgents();
			fakePatterns = new ArrayList<Pattern>();
			fakePatterns.add(new Pattern(null, null, new HashMap<Agent, Role>(), alives));
			PatternMaker.settleAgentRole(fakePatterns, getMe(), fakeRole);
			wolfsPatterns = new ArrayList<Pattern>(fakePatterns);
			for(Entry<Agent, Role> set: advanceGameInfo.getComingoutMap().entrySet()){
				if(set.getKey() != getMe()){
					patternMaker.extendPatternList(fakePatterns, set.getKey(), set.getValue(), advanceGameInfo);
					patternMaker.extendPatternList(wolfsPatterns, set.getKey(), set.getValue(), advanceGameInfo);
				}
			}
		}
		
/*		switch (role) {
		case SEER:
			if(!seerCO){
				fakeRole = changer.getSeerCO();
				seerCO = true;
			}
			break;
		case MEDIUM:
			if(!mediumCO){
				fakeRole = changer.getMediumCO();
				mediumCO = true;
			}
			break;
		default:
			break;
		}
*/	
	}


	private void divinedWhisperDealing(Talk talk, Utterance utterance){
		judgeWhisperDealing(talk, utterance);
	}

	private void inquestedWhisperDealing(Talk talk, Utterance utterance){
		judgeWhisperDealing(talk, utterance);
	}


	private void judgeWhisperDealing(Talk talk, Utterance utterance){
		/*
		 * 人狼同士が協調可能Patternがあるときに，それが消えたら自分のJudgeを書き換え
		 * 村人騙りなら何もしない
		 */
		if(getDay() == 0){
			return;
		}
		Judge judge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		whisperedJudges.add(judge);
		patternMaker.updateJudgeData(wolfsPatterns, judge);

//		if(fakeRole != Role.SEER && fakeRole != Role.MEDIUM ){
//			return;
//		}
//		if(wolfsPatterns.size() != 0){
//			List<Pattern> hypotheticPatterns = patternMaker.clonePatterns(wolfsPatterns);
//
//			patternMaker.updateJudgeData(hypotheticPatterns, todaysFakeJudge);
//			if(hypotheticPatterns.size() == 0){
//				notToldjudges.remove(todaysFakeJudge);
//				System.out.println();
//				switch (fakeRole) {
//				case SEER:
//					setFakeDivineJudge();
//					break;
//				case MEDIUM:
//					setFakeInquestJudge(getLatestDayGameInfo().getExecutedAgent());
//				}
//			}
//		}
	}



	private void voteWhisperDealing(Talk talk, Utterance utterance) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void comingoutTalkDealing(Talk talk, Utterance utterance){
		super.comingoutTalkDealing(talk, utterance);
		if(isComingout) return;
		@SuppressWarnings("unused")
		Role co = utterance.getRole();
		if(getDay() == 0 && getWolfList().contains(talk.getAgent())){
			
			patternChange(utterance.getRole());
		}

	}

	@Override
	public void divinedTalkDealing(Talk talk, Utterance utterance){
		super.divinedTalkDealing(talk, utterance);
		confirmPossessedAgent();
		if(utterance.getTarget() == getMe() && utterance.getResult() == Species.WEREWOLF){
			wolfJudgedDealing();
		}
	}

	@Override
	public void inquestedTalkDealing(Talk talk, Utterance utterance){
		super.inquestedTalkDealing(talk, utterance);
		confirmPossessedAgent();
		if(utterance.getTarget() == getMe() && utterance.getResult() == Species.WEREWOLF){
			wolfJudgedDealing();
		}
	}
	
	private void wolfJudgedDealing(){
/*		if(isComingout) return;
		if(coTiming.isWolfJudged()){
			
		}
*/	}

	/**
	 * 狂人確定のAgentがいるか確かめる
	 * いた場合はpossessedAgentにいれる
	 */
	private void confirmPossessedAgent(){
		loop1:for(Entry<Agent, Role> set: advanceGameInfo.getComingoutMap().entrySet()){
			if(set.getValue() == Role.SEER || set.getValue() == Role.MEDIUM){
				for(Pattern pattern: myPatterns){
					if(set.getKey().equals(pattern.getSeerAgent()) || set.getKey().equals(pattern.getMediumAgent())){
						continue loop1;
					}
				}
			}
			//全てのPatternにおいて真能力者とされていないカミングアウトしたプレイヤー＝狂人
			possessedAgent = set.getKey();
		}
	}

	/**
	 * 狂人が分かっているかを返す
	 * @return
	 */
	private boolean knowsPossessed(){
		if(possessedAgent == null){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 狂人が分かっている場合はFakeRoleを返す
	 * 分かっていない場合はnull
	 * @return
	 */
	@SuppressWarnings("unused")
	private Role possessedFakeRole(){
		if(!knowsPossessed()){
			return null;
		}else{
			return advanceGameInfo.getComingoutMap().get(possessedAgent);
		}
	}

	private List<Agent> getWolfList(){

		List<Agent> wolfList = new ArrayList<Agent>();
		for(Entry<Agent, Role> set: getLatestDayGameInfo().getRoleMap().entrySet()){
			if(set.getValue() == Role.WEREWOLF){
				wolfList.add(set.getKey());
			}
		}
		return wolfList;
	}


	@Override
	public Agent attack(){
		Map<Agent, Double> agentPoint = new HashMap<Agent, Double>();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList()){
			double point = 0.0;
			for(Pattern p: myPatterns){
				Scene s = new Scene(p);
				AgentPattern ap = p.getAgentPattern(agent);
				Qvalues qVal = ld.getQvalue(s.getHashNum());
				point += qVal.getWolfAttack().get(ap) * (double)qVal.getLikelihood();
			}
			agentPoint.put(agent, point);
		}
		return selectGreedyTarget(agentPoint);
	}


	@Override
	protected void setFakeDivineJudge() {
		setTemplateFakeDivineJudge();
		todaysFakeJudge = fakeJudges.get(fakeJudges.size()-1);
	}

	@SuppressWarnings("unused")
	private Judge getMaxEntropyDivineJudge(List<Pattern> patterns){
		Map<Judge, Integer> remainPatternNumMap = new HashMap<Judge, Integer>();

		for(Agent agent: getLatestDayGameInfo().getAliveAgentList()){
			//すでに占っている，または自分ならば候補からはずす
			if(agent.equals(getMe()) || isJudged(agent)){
				continue;
			}else{
				for(Species species: Species.values()){
					Judge judge = new Judge(getDay(), getMe(), agent, species);
					List<Pattern> hypotheticalPatterns = getHypotheticalPatterns(patterns, judge);
					remainPatternNumMap.put(judge, hypotheticalPatterns.size());
				}

			}
		}
		if(remainPatternNumMap.size() != 0){
			return SelectStrategy.getMaxIntValueKey(remainPatternNumMap);
		}
		//候補がなくなってしまったとき
		else{
			return new Judge(getDay(), getMe(), getMe(), Species.HUMAN);
		}
	}







	@Override
	protected void setFakeInquestJudge(Agent executedAgent) {
		setTemplateFakeInquestJudge();
		todaysFakeJudge = fakeJudges.get(fakeJudges.size()-1);
	}

	@SuppressWarnings("unused")
	private Judge getMaxEntropyInquestJudge(List<Pattern> patterns){
		Map<Judge, Integer> remainPatternNumMap = new HashMap<Judge, Integer>();

		for(Species species: Species.values()){
			Judge judge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), species);
			List<Pattern> hypotheticalPatterns = getHypotheticalPatterns(patterns, judge);
			remainPatternNumMap.put(judge, hypotheticalPatterns.size());
		}
		return SelectStrategy.getMaxIntValueKey(remainPatternNumMap);
	}






	@Override
	public void setVoteTarget() {
		setVoteTargetTemplate(myPatterns);
		
/*		//initialize時
		if(wolfsPatterns == null){
			return;
		}
		if (wolfsPatterns.size() != 0) {
			setVoteTargetTemplate(wolfsPatterns);
		}
		// 人狼の協調が不可能な組み合わせの場合
		else {
			setVoteTargetTemplate(fakePatterns);
		}
*/
	}

	@Override
	void updatePreConditionQVal(boolean isVillagerWin){
		//偽役職
//		updateWolfRolePatternQval(isVillagerWin);

		//偽役職のCO
		updateCOElements(isVillagerWin);
	}


/*
	private void updateWolfRolePatternQval(boolean isVillagerWin){
		double reward = (isVillagerWin)? 0.0: 100;
		//偽役職
		List<Role> wolfsFakeRoleList = new ArrayList<Role>();
		for(Entry<Agent, Role> map: wolfsFakeRoleMap.entrySet()){
			wolfsFakeRoleList.add(map.getValue());
		}
		WolfRolePattern wolfRolePattern = WolfRolePattern.getWolfRolePattern(wolfsFakeRoleList);
		if (WolfRolePattern.getWolfRolePattern(wolfsFakeRoleList) != null) {
			double q = ld.getWolfRolePattern().get(wolfRolePattern);
			double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
			ld.getWolfRolePattern().put(wolfRolePattern, learnedQ);
		}
	}
*/

	@Override
	void updateMiddlePattern(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		super.updateMiddlePattern(day, patternPresent, patternNext, scenePresent, sceneNext);

		//襲撃の学習
		updateMiddleAttackQval(day, patternPresent, patternNext, scenePresent, sceneNext);
	}



	private void updateMiddleAttackQval(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext){
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());
		AgentPattern ap = patternPresent.getAgentPattern(patternNext.getAttackedAgent());
		Map<AgentPattern, Double>
				map = qVal.getWolfAttack(),
				mapNext = qValNext.getWolfAttack();
		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap), 0.0, nextMaxQVal);
		map.put(ap, learnedQ);
	}

	@Override
	protected void initializeFakeRole() {
		Map<WolfFakeRoleChanger, Double> map = ld.getWolfFakeRoleChanger();
		if(isIS_LEARNING()){
			changer = selectRandomTarget(map);
		}else {
			changer = selectSoftMaxTarget(map);
		}
		fakeRole = changer.getInitial();
	}

/*
	@Override
	protected void fakeRoleChanger() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
*/

	@Override
	void updateCOElements(boolean isVillagerWin) {
		Map<COtimingNeo, Double> map = getCOMap();
		double q = map.get(coTiming);
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
		map.put(coTiming, learnedQ);
		
		Map<WolfFakeRoleChanger, Double> changerMap = ld.getWolfFakeRoleChanger();
		double qW = changerMap.get(changer);
		@SuppressWarnings("unused")
		double learnedQW = ReinforcementLearning.reInforcementLearn(qW, reward, 0);
		changerMap.put(changer, learnedQ);
	}



}