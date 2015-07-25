package org.aiwolf.kajiClient.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.kajiClient.lib.AdvanceGameInfo;
import org.aiwolf.kajiClient.lib.CauseOfDeath;
import org.aiwolf.kajiClient.lib.DeadCondition;
import org.aiwolf.kajiClient.lib.EnemyCase;
import org.aiwolf.kajiClient.lib.Pattern;
import org.aiwolf.kajiClient.lib.PatternMaker;
import org.aiwolf.kajiClient.reinforcementLearning.AgentPattern;
import org.aiwolf.kajiClient.reinforcementLearning.LearningData;
import org.aiwolf.kajiClient.reinforcementLearning.Qvalues;
import org.aiwolf.kajiClient.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.kajiClient.reinforcementLearning.Scene;
import org.aiwolf.kajiClient.reinforcementLearning.SelectStrategy;

/**
 * 全役職共通部分のアルゴリズム
 * initialize：初期パターン作成
 * update：発話ログからAGI更新，Pattern更新
 * dayStart：AGIの死亡プレイヤーを更新
 * @author kengo
 *
 */
public abstract class AbstractKajiBasePlayer extends AbstractRole {
	private boolean IS_LEARNING = false;

	private static double EPSILON = 0.1;
	private static double TEMP = 1.0;

	//CO,能力の結果などのデータ集合
	protected AdvanceGameInfo advanceGameInfo = new AdvanceGameInfo();

	//ありうるパターン全て
	protected List<Pattern> generalPatterns = new ArrayList<Pattern>();

	//自分の役職を入れたパターン
	protected List<Pattern> myPatterns = new ArrayList<Pattern>();

	//パターンを更新，拡張するときに用いる
	protected PatternMaker patternMaker;

	//トークをどこまで読んだか
	protected int readTalkNumber = 0;

	//今日投票するプレイヤー(暫定)
	protected Agent voteTarget = null;

	//最新の発話で言った投票先プレイヤー
	protected Agent toldVoteTarget = null;

	//各役職の強さを数値化したもの．暫定版エージェント用
	protected Map<Role, Double> rolePoint = new HashMap<Role, Double>();

	//人間はmyPatternを入れたもの，人狼側はfakePatternsを入れたもの．
	protected List<List<Pattern>> myPatternLists = new ArrayList<List<Pattern>>();

	//学習データ
	protected LearningData ld = LearningData.getInstance(0);


	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		/*
		 * パターン生成
		 */
		super.initialize(gameInfo, gameSetting);
		//初期パターンの作成
		
		
		//CO,能力の結果などのデータ集合
		advanceGameInfo = new AdvanceGameInfo();

		//ありうるパターン全て
		generalPatterns = new ArrayList<Pattern>();

		//自分の役職を入れたパターン
		myPatterns = new ArrayList<Pattern>();

		//トークをどこまで読んだか
		readTalkNumber = 0;

		//今日投票するプレイヤー(暫定)
		voteTarget = null;

		//最新の発話で言った投票先プレイヤー
		toldVoteTarget = null;

		//各役職の強さを数値化したもの．暫定版エージェント用
		rolePoint = new HashMap<Role, Double>();

		myPatternLists = new ArrayList<List<Pattern>>();

		List<Agent> aliveAgents = gameInfo.getAliveAgentList();
		patternMaker = new PatternMaker(gameSetting);
		generalPatterns.add(new Pattern(null, null, new HashMap<Agent, Role>(), aliveAgents));
		Pattern initialPattern;
		switch (getMyRole()) {
		case SEER:
			initialPattern = new Pattern(getMe(), null, new HashMap<Agent, Role>(), aliveAgents);
			break;
		case MEDIUM:
			initialPattern = new Pattern(null, getMe(), new HashMap<Agent, Role>(), aliveAgents);
		default:
			initialPattern = new Pattern(null, null, new HashMap<Agent, Role>(), aliveAgents);
			break;
		}
		myPatterns.add(initialPattern);


		setRolePoint();

	}

	/**
	 * 各役職の強さを入力
	 */
	private void setRolePoint(){
		rolePoint.put(Role.BODYGUARD, 0.3);
		rolePoint.put(Role.MEDIUM, 0.7);
		rolePoint.put(Role.POSSESSED, -0.3);
		rolePoint.put(Role.SEER, 1.2);
		rolePoint.put(Role.VILLAGER, 0.1);
		rolePoint.put(Role.WEREWOLF, -0.1);
		rolePoint.put(Role.FREEMASON, 0.0);
	}

	/**
	 * patternにおいてagentが死亡した時に失われる役職値を返す
	 * @param pattern
	 * @param agent
	 * @param aliveAgents
	 * @return
	 */
	public double getRiskValue(Pattern pattern, Agent agent, List<Agent> aliveAgents){
		double riskValue = 0.0;

		Map<Role, Double> roleProbabilitys = getRoleProbabilitys(pattern, agent, aliveAgents);
		for(Entry<Role, Double> set: roleProbabilitys.entrySet()){
			riskValue += rolePoint.get(set.getKey()) * set.getValue();
		}
		return riskValue;
	}

	/**
	 * patternにおけるagentが各役職に何パーセントでなっているか返す
	 * @param pattern
	 * @param agent
	 * @param aliveAgents
	 * @return
	 */
	public Map<Role, Double> getRoleProbabilitys(Pattern pattern, Agent agent, List<Agent> aliveAgents){
		Map<Role, Double> roleProbabilitys = new HashMap<Role, Double>();

		Map<Role, Integer> roleNumMap = new HashMap<Role, Integer>(getGameSetting().getRoleNumMap());

		if(pattern.getSeerAgent() != null){
			if(pattern.getSeerAgent().equals(agent)){
				roleProbabilitys.put(Role.SEER, 1.0);
				return roleProbabilitys;
			}
			roleNumMap.put(Role.SEER, 0);
		}
		if(pattern.getMediumAgent() != null){
			if(pattern.getMediumAgent().equals(agent)){
				roleProbabilitys.put(Role.MEDIUM, 1.0);
				return roleProbabilitys;
			}
			roleNumMap.put(Role.MEDIUM, 0);
		}

		Map<Agent, EnemyCase> enemyMap = pattern.getEnemyMap();
		if(enemyMap.size() != 0){

			int restBlackNum = roleNumMap.get(Role.WEREWOLF);
			int restWhiteNum = roleNumMap.get(Role.POSSESSED);
			for(Entry<Agent, EnemyCase> set: enemyMap.entrySet()){
				if(set.getValue() == EnemyCase.black){
					restBlackNum--;
				}else if(set.getValue() == EnemyCase.white){
					restWhiteNum--;
				}
			}
			roleNumMap.put(Role.WEREWOLF, restBlackNum);
			roleNumMap.put(Role.POSSESSED, restWhiteNum);

			if(enemyMap.containsKey(agent)){
				switch (enemyMap.get(agent)) {
				case black:
					roleProbabilitys.put(Role.WEREWOLF, 1.0);
					break;
				case white:
					roleProbabilitys.put(Role.POSSESSED, 1.0);
					break;
				case gray:
					roleProbabilitys.put(Role.WEREWOLF, (double)restBlackNum/((double)restBlackNum + (double)restWhiteNum));
					roleProbabilitys.put(Role.POSSESSED, (double)restWhiteNum/((double)restBlackNum + (double)restWhiteNum));
					break;
				}
				return roleProbabilitys;
			}
		}
		int restRoleNum = 0;
		for(Entry<Role, Integer> set: roleNumMap.entrySet()){
			restRoleNum += set.getValue();
		}

		//白確リストに入っている場合
		if(pattern.getWhiteAgentSet().contains(agent)){
			for(Entry<Role, Integer> set: roleNumMap.entrySet()){
				if(set.getKey() != Role.WEREWOLF){
					roleProbabilitys.put(set.getKey(), (double)roleNumMap.get(set.getKey())/((double)restRoleNum - (double)roleNumMap.get(Role.WEREWOLF)));
				}
			}
		}
		//白確リストにも入っていない場合
		else{
			for(Entry<Role, Integer> set: roleNumMap.entrySet()){
				roleProbabilitys.put(set.getKey(), (double)roleNumMap.get(set.getKey())/((double)restRoleNum));
			}
		}
		return roleProbabilitys;
	}

	@Override
	public void update(GameInfo gameInfo){
		/*
		 * 会話の処理
		 * 暫定投票先の更新
		 */
		super.update(gameInfo);

		List<Talk> talkList = gameInfo.getTalkList();

		/*
		 * 各発話についての処理
		 * カミングアウトについてはパターンの拡張
		 * 能力結果の発話についてはパターン情報の更新
		 */
		boolean patternChanged = false;
		for(; readTalkNumber < talkList.size(); readTalkNumber++){
			Talk talk = talkList.get(readTalkNumber);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {
			case COMINGOUT:
				comingoutTalkDealing(talk, utterance);
				patternChanged = true;
				break;

			case DIVINED:
				divinedTalkDealing(talk, utterance);
				patternChanged = true;
				break;


			case INQUESTED:
				inquestedTalkDealing(talk, utterance);
				patternChanged = true;
				break;

			case VOTE:
				voteTalkDealing(talk, utterance);
				break;
			//上記以外
			default:
				break;
			}
		}
		//投票先を更新(更新する条件などはサブクラスで記載)
		if(patternChanged){
			setVoteTarget();
		}
	}

	/**
	 * カミングアウトの発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void comingoutTalkDealing(Talk talk, Utterance utterance){
		advanceGameInfo.putComingoutMap(talk.getAgent(), utterance.getRole());
		patternMaker.extendPatternList(generalPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
		patternMaker.extendPatternList(myPatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
	}

	/**
	 * 引数が違うバージョン
	 * @param talk
	 * @param utterance
	 */
	public void comingoutTalkDealing(Agent talker, Role role){
		advanceGameInfo.putComingoutMap(talker, role);
		patternMaker.extendPatternList(generalPatterns, talker, role, advanceGameInfo);
		patternMaker.extendPatternList(myPatterns, talker, role, advanceGameInfo);
	}

	/**
	 * 占い結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void divinedTalkDealing(Talk talk, Utterance utterance){
		if(advanceGameInfo.getComingoutMap().get(talk.getAgent()) != Role.SEER){
			comingoutTalkDealing(talk.getAgent(), Role.SEER);
		}
		Judge inspectJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		advanceGameInfo.addInspectJudges(inspectJudge);
		patternMaker.updateJudgeData(generalPatterns, inspectJudge);
		patternMaker.updateJudgeData(myPatterns, inspectJudge);
	}

	/**
	 * 霊能結果の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void inquestedTalkDealing(Talk talk, Utterance utterance){
		if(advanceGameInfo.getComingoutMap().get(talk.getAgent()) != Role.MEDIUM){
			comingoutTalkDealing(talk.getAgent(), Role.MEDIUM);
		}
		Judge tellingJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		advanceGameInfo.addMediumJudges(tellingJudge);
		patternMaker.updateJudgeData(generalPatterns, tellingJudge);
		patternMaker.updateJudgeData(myPatterns, tellingJudge);
	}

	/**
	 * 投票意思の発話の処理
	 * @param talk
	 * @param utterance
	 */
	public void voteTalkDealing(Talk talk, Utterance utterance){
		Vote vote = new Vote(getDay(), talk.getAgent(), utterance.getTarget());
		advanceGameInfo.addVote(getDay(), vote);
	}

	@Override
	public void dayStart() {

		/**
		 * 昨日のmyPatternsを学習用に保存
		 */
/*		List<Pattern> copyPatterns;
		
		
		if(getMyRole() == Role.WEREWOLF || getMyRole() == Role.POSSESSED){
			copyPatterns = copyPattern()
		}
		for(Pattern p: myPatterns){
			copyPatterns.add(p.clone());
		}
		myPatternLists.add(copyPatterns);
*/		
		addCopyToMyPatternLists();


		/*
		 * 死亡プレイヤー情報の更新
		 * 暫定投票先の更新
		 */
		readTalkNumber = 0;
		//死亡したプレイヤーをAGIに記録
		Agent attackedAgent = getLatestDayGameInfo().getAttackedAgent();
		patternMaker.updateAttackedData(generalPatterns, attackedAgent);
		patternMaker.updateAttackedData(myPatterns, attackedAgent);
		if(attackedAgent != null){
			DeadCondition attackedAgentCondition = new DeadCondition(attackedAgent, getDay(), CauseOfDeath.attacked);
			advanceGameInfo.addDeadConditions(attackedAgentCondition);
		}

		Agent executedAgent = getLatestDayGameInfo().getExecutedAgent();
		patternMaker.updateExecutedData(generalPatterns, executedAgent);
		patternMaker.updateExecutedData(myPatterns, executedAgent);
		if(executedAgent != null){
			DeadCondition executeddAgentCondition = new DeadCondition(executedAgent, getDay(), CauseOfDeath.executed);
			advanceGameInfo.addDeadConditions(executeddAgentCondition);
		}

		//今日の暫定投票先
		toldVoteTarget = null;
		voteTarget = null;
		setVoteTargetTemplate(myPatterns);

	}
	
	protected void addCopyToMyPatternLists() {
		/**
		 * 昨日のmyPatternsを学習用に保存
		 */
		List<Pattern> copyPatterns = new ArrayList<Pattern>();
		
		for(Pattern p: myPatterns){
			copyPatterns.add(p.clone());
		}
		myPatternLists.add(copyPatterns);
	}

	@Override
	public String talk() {
		/*
		 * 発話順序の優先度
		 * カミングアウト＞能力結果の発話＞投票先の発話
		 */

		//カミングアウトの発話
		String comingoutReport = getComingoutText();
		if(comingoutReport != null){
			return comingoutReport;
		}

		//占い，霊能結果の発話
		String judgeReport = getJudgeText();
		if(judgeReport != null){
			return judgeReport;
		}


		//投票先の発話
		if(toldVoteTarget != voteTarget && voteTarget != null){
			String voteReport = TemplateTalkFactory.vote(voteTarget);
			toldVoteTarget = voteTarget;
			return voteReport;
		}

		//話すことが何もなければ
		return Talk.OVER;
	}

	/**
	 * 占い or 霊能結果の発話を行う．結果の報告をしない場合はnullを返す
	 * @return
	 */
	public abstract String getJudgeText();

	/**
	 * カミングアウトの発話を行う．COしない場合はnullを返す
	 * @return
	 */
	public abstract String getComingoutText();

	/**
	 * 今日投票予定のプレイヤーを決定する
	 * updateとdayStartの最後によばれる
	 * @return
	 */
	public abstract void setVoteTarget();

	/**
	 * 各プレイヤーについて，そのプレイヤーが死亡した際の損害の期待値を出す
	 * 損害が一番低いプレイヤーに投票先を移す
	 */
	public void setVoteTargetTemplate(List<Pattern> patterns){
		Map<Agent, Double> agentPoint = new HashMap<Agent, Double>();
		for(Agent agent: getLatestDayGameInfo().getAliveAgentList()){
			double point = 0.0;
			for(Pattern p: patterns){
				Scene s = new Scene(p);
				AgentPattern ap = p.getAgentPattern(agent);
				Qvalues qVal = ld.getQvalue(s.getHashNum());
				point += getVoteQValMap(qVal).get(ap) * (double)qVal.getLikelihood();
			}
			agentPoint.put(agent, point);
		}
		voteTarget = selectGreedyTarget(agentPoint);
		return;
	}

	public static <T>T selectRandomTarget(Map<T, Double> map){
		return SelectStrategy.randomSelect(map);
	}

	public <T>T selectGreedyTarget(Map<T, Double> map){
		if(IS_LEARNING){
			return SelectStrategy.greedyselect(map, EPSILON);
		}else{
			return SelectStrategy.getMaxDoubleValueKey(map);
		}
	}
	
	public <T>T selectSoftMaxTarget(Map<T, Double> map){
		if(IS_LEARNING){
			return SelectStrategy.greedyselect(map, EPSILON);
		}else{
			return SelectStrategy.softMaxSelect(map, TEMP);
		}
	}
	

	@Override
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent attack() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent divine() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent guard() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent vote() {
		return voteTarget;
	}

	@Override
	public void finish() {
		if(!IS_LEARNING ) return;
		/**
		 * 昨日のmyPatternsを学習用に保存
		 */
		List<Pattern> copyPatterns = new ArrayList<Pattern>();
		for(Pattern p: myPatterns){
			copyPatterns.add(p.clone());
		}
		myPatternLists.add(copyPatterns);


		learn();
	}

	private List<Pattern> getTruePatternList(Map<Agent, Role> roleMap){
		List<Pattern> patternList = new ArrayList<Pattern>();

		/**
		 *
		 */
		loop1: for(List<Pattern> patterns: myPatternLists){
			for(Pattern p: patterns){
				if(p == null) continue;
				if(p.isPatternMatched(roleMap)){
					patternList.add(p);
					continue loop1;
				}
			}
			patterns.add(null);
		}

		return patternList;
	}

	public void learn(){
		// vote
		List<Pattern> truePatterns = getTruePatternList(getLatestDayGameInfo().getRoleMap());
		List<Scene> trueScenes = new ArrayList<Scene>();
		for(int i = 0; i < truePatterns.size(); i++){
			if(truePatterns.get(i) != null){
				Scene scene = new Scene(truePatterns.get(i));
				trueScenes.add(scene);
			}else{
				trueScenes.add(null);
			}

		}
		boolean isVillagerWin = true;
		for(Entry<Agent, Role> set: getLatestDayGameInfo().getRoleMap().entrySet()){
			//finish()時に人狼が生きていたら人狼側の勝ち
			if(set.getValue() == Role.WEREWOLF && getLatestDayGameInfo().getAliveAgentList().contains(set.getKey())){
				isVillagerWin = false;
				break;
			}
		}
		updateQvalue(truePatterns, trueScenes, isVillagerWin);



	}

	private void updateQvalue(List<Pattern> truePatterns, List<Scene> trueScenes, boolean isVillagerWin) {
		updatePreConditionQVal(isVillagerWin);
		for(Scene s: trueScenes){
			int likelihood = ld.getQvalue(s.getHashNum()).getLikelihood();
			ld.getQvalue(s.getHashNum()).setLikelihood(likelihood+1);
		}
		for(int i = truePatterns.size() - 2; i > 0; i--){
			if(truePatterns.get(i) == null){
				continue;
			}else if(getGameInfo(i).getStatusMap().get(getMe()) == Status.DEAD){
				continue;
			}
			//最後のパターンは直接報酬
			if(i == truePatterns.size() - 2){
				updateLastPattern(i, truePatterns.get(i), trueScenes.get(i), isVillagerWin);
			}
			//それ意外のパターン
			else{
				Pattern patternPresent = truePatterns.get(i),
						patternNext = truePatterns.get(i+1);
				Scene	scenePresent = trueScenes.get(i),
						sceneNext = trueScenes.get(i+1);
				if(patternPresent == null || patternNext == null || scenePresent == null || sceneNext == null){
					continue;
				}else{
					updateMiddlePattern(i, patternPresent, patternNext, scenePresent, sceneNext);
				}

			}
		}

	}

	void updatePreConditionQVal(boolean isVillagerWin) {
		// TODO 自動生成されたメソッド・スタブ

	}

	void updateMiddlePattern(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());
		//vote
		AgentPattern ap = patternPresent.getAgentPattern(patternNext.getExecutedAgent());
		Map<AgentPattern, Double>
				map = getVoteQValMap(qVal),
				mapNext = getVoteQValMap(qValNext);
		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap), 0.0, nextMaxQVal);
		map.put(ap, learnedQ);
	}

	void updateLastPattern(int day, Pattern pattern, Scene scene,
			boolean isVillagerWin) {
		Qvalues qVal = ld.getQvalue(scene.getHashNum());
		double reward;
		if(getMyRole() == Role.POSSESSED || getMyRole() == Role.WEREWOLF){
			reward = (!isVillagerWin)? 100.0: 0;
		}else{
			reward = (isVillagerWin)? 100.0: 0;
		}
		//voteの学習
		Agent executedAgent = getLatestDayGameInfo().getExecutedAgent();
		AgentPattern ap = pattern.getAgentPattern(executedAgent);
		Map<AgentPattern, Double> map = getVoteQValMap(qVal);
		double q = 0.0;
		q = map.get(ap);
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0.0);
		map.put(ap, learnedQ);

	}

	private Map<AgentPattern, Double> getVoteQValMap(Qvalues qVal){
		Map<AgentPattern, Double> map = null;;
		switch (getMyRole()) {
		case VILLAGER:
			map = qVal.getVillagerVote();
			break;
		case BODYGUARD:
			map = qVal.getHunterVote();
			break;
		case MEDIUM:
			map = qVal.getMediumVote();
			break;
		case POSSESSED:
			map = qVal.getPossessedVote();
			break;
		case SEER:
			map = qVal.getSeerVote();
			break;
		case WEREWOLF:
			map = qVal.getWolfVote();
			break;
		}
		return map;
	}

	public void setLD(int ldNum){
		ld = LearningData.getInstance(ldNum);
	}

	public boolean isIS_LEARNING() {
		return IS_LEARNING;
	}

	public void setIS_LEARNING(boolean iS_LEARNING) {
		IS_LEARNING = iS_LEARNING;
	}

}
