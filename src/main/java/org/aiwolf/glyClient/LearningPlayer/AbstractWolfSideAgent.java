package org.aiwolf.glyClient.LearningPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.aiwolf.glyClient.lib.Pattern;
import org.aiwolf.glyClient.reinforcementLearning.AgentPattern;
import org.aiwolf.glyClient.reinforcementLearning.Qvalues;
import org.aiwolf.glyClient.reinforcementLearning.ReinforcementLearning;
import org.aiwolf.glyClient.reinforcementLearning.Scene;

public abstract class AbstractWolfSideAgent extends AbstractGiftedPlayer {


	//騙る役職
	Role fakeRole = null;

	//fakeRoleに沿ったPatterns
	List<Pattern> fakePatterns = new ArrayList<Pattern>();

	List<Judge> fakeJudges = new ArrayList<Judge>();
	
/*	WolfFakeRoleChanger changer = new WolfFakeRoleChanger();
*/

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		//fakeRoleをランダムで選択
		
		initializeFakeRole();
		
		fakePatterns.add(new Pattern(null, null, new HashMap<Agent, Role>(), gameInfo.getAliveAgentList()));
		patternMaker.settleAgentRole(myPatterns, getMe(), getMyRole());
		patternMaker.settleAgentRole(fakePatterns, getMe(), fakeRole);
	}
	
	protected abstract void initializeFakeRole();

	@Override
	public void dayStart() {
		super.dayStart();

		patternMaker.updateAttackedData(fakePatterns, getLatestDayGameInfo().getAttackedAgent());
		patternMaker.updateExecutedData(fakePatterns, getLatestDayGameInfo().getExecutedAgent());
		switch (fakeRole) {
		//占い師騙りの場合，2日目以降fakeJudgeをいれる
		case SEER:
			if(getDay() >= 2){
				setFakeDivineJudge();
			}
			break;

		//霊能者騙りの場合，襲撃されたAgentがいればfakeJudgeをいれる
		case MEDIUM:
			if(getLatestDayGameInfo().getExecutedAgent() != null){
				setFakeInquestJudge(getLatestDayGameInfo().getExecutedAgent());
			}
			break;

		//村人騙りの場合，何もしない
		case VILLAGER:
			break;
		}
	}
	/*
	@Override
	protected void addCopyToMyPatternLists() {
		*//**
		 * 昨日のmyPatternsを学習用に保存
		 *//*
		List<Pattern> copyPatterns = new ArrayList<Pattern>();
		
		for(Pattern p: fakePatterns){
			copyPatterns.add(p.clone());
		}
		myPatternLists.add(copyPatterns);
	}
	*/
	
	
	@Override
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		
		
	}


	/**
	 * 2日目以降のdayStartで呼ばれる
	 * 偽占い結果を作る
	 */
	abstract protected void setFakeDivineJudge();

//	abstract protected void fakeRoleChanger();
	
	protected void setTemplateFakeDivineJudge() {

		Judge fakeJudge;

		class FakeJudgePattern {
			private Agent agent;
			private Species species;

			public FakeJudgePattern(Agent agent, Species species) {
				this.agent = agent;
				this.species = species;
			}
		}

		Map<FakeJudgePattern, Double> judgePoint = new HashMap<FakeJudgePattern, Double>();
		List<Agent> judgeTargets = new ArrayList<Agent>(getLatestDayGameInfo().getAliveAgentList());
		judgeTargets.add(getLatestDayGameInfo().getExecutedAgent());
		judgeTargets.add(getLatestDayGameInfo().getAttackedAgent());
		for(Agent agent: judgeTargets){
			if(agent == null){
				continue;
			}
			for(Species sp: Species.values()){
				double point = 0.0;
				for(Pattern p: myPatterns){
					Scene s = new Scene(p);
					AgentPattern ap = p.getAgentPattern(agent);
					Qvalues qVal = ld.getQvalue(s.getHashNum());
					if(getMyRole() == Role.WEREWOLF){
						point += qVal.getWolfDivine().get(ap).get(sp) * (double)qVal.getLikelihood();
					}else{
						point += qVal.getPossessedDivine().get(ap).get(sp) * (double)qVal.getLikelihood();
					}
				}
				judgePoint.put(new FakeJudgePattern(agent, sp), point);
			}
		}


		FakeJudgePattern fp = selectGreedyTarget(judgePoint);

		fakeJudge = new Judge(getDay()-1, getMe(), fp.agent, fp.species);

		notToldjudges.add(fakeJudge);
		fakeJudges.add(fakeJudge);

	}
	/**
	 * 処刑されたプレイヤーがいた時に呼ばれる
	 * 偽霊能結果を作る
	 * @param executedAgent
	 */
	abstract protected void setFakeInquestJudge(Agent executedAgent);

	protected void setTemplateFakeInquestJudge() {

		Judge fakeJudge;

		Map<Species, Double> judgePoint = new HashMap<Species, Double>();
		for(Species sp: Species.values()){
			double point = 0.0;
			for(Pattern p: myPatterns){
				Scene s = new Scene(p);
				AgentPattern ap = p.getAgentPattern(getLatestDayGameInfo().getExecutedAgent());
				Qvalues qVal = ld.getQvalue(s.getHashNum());
				if(getMyRole() == Role.WEREWOLF){
					point += qVal.getWolfInquest().get(ap).get(sp) * (double)qVal.getLikelihood();
				}else{
					point += qVal.getPossessedInquest().get(ap).get(sp) * (double)qVal.getLikelihood();
				}
			}
			judgePoint.put(sp, point);
		}


		Species fakeSpecies = selectGreedyTarget(judgePoint);

		fakeJudge = new Judge(getDay()-1, getMe(), getLatestDayGameInfo().getExecutedAgent(), fakeSpecies);

		notToldjudges.add(fakeJudge);
		fakeJudges.add(fakeJudge);

	}



	@Override
	public void comingoutTalkDealing(Talk talk, Utterance utterance){
		super.comingoutTalkDealing(talk, utterance);
		patternMaker.extendPatternList(fakePatterns, talk.getAgent(), utterance.getRole(), advanceGameInfo);
	}

	@Override
	public void divinedTalkDealing(Talk talk, Utterance utterance){
		super.divinedTalkDealing(talk, utterance);
		Judge inspectJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		patternMaker.updateJudgeData(fakePatterns, inspectJudge);
	}

	@Override
	public void inquestedTalkDealing(Talk talk, Utterance utterance){
		super.inquestedTalkDealing(talk, utterance);
		Judge tellingJudge = new Judge(getDay(), talk.getAgent(), utterance.getTarget(), utterance.getResult());
		patternMaker.updateJudgeData(fakePatterns, tellingJudge);
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
		/*
		 * カミングアウトする日数になる
		 * 他に同じ能力者COが出る
		 * 人狼を見つける
		 * 投票先に選ばれそう（全体の2/3が投票かつ全投票中で1/4以上が自分に投票）
		 */
		if(isComingout){
			return null;
		}else{
			//日数によるカミングアウト
			if(coTiming.getDay() == getDay()){
				return comingoutFakeRole();
			}

			//偽CO出現
			if(coTiming.isAgainst()){
				Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
				for(Entry<Agent, Role> set: comingoutMap.entrySet()){
					if(set.getValue() == fakeRole && !set.getKey().equals(getMe())){
						return comingoutFakeRole();
					}
				}
			}

			//人狼見つける
			if(coTiming.isHasFoundWolf()){
				for(Judge judge: notToldjudges){
					if(judge.getResult() == Species.WEREWOLF){
						return comingoutFakeRole();
					}
				}
			}

			//投票先に選ばれそう
			if(coTiming.isVoted()){
				List<Vote> votes = advanceGameInfo.getVoteList(getDay());
				if((double)votes.size() * 1.5 > getLatestDayGameInfo().getAliveAgentList().size()){
					int voteToMe = 0;
					for(Vote vote: votes){
						if(vote.getTarget().equals(getMe())){
							voteToMe++;
						}
					}
					if((double)voteToMe * 4 > votes.size()){
						return comingoutFakeRole();
					}
				}
			}
			
			//人狼だと占われた
			if(coTiming.isWolfJudged()){
				for(Judge judge: advanceGameInfo.getInspectJudges()){
					if(getMe().equals(judge.getTarget()) && judge.getResult() == Species.WEREWOLF){
						return comingoutFakeRole();
					}
				}
			}
		}
		return null;
	}
	

	/**
	 * fakeRoleをカミングアウトする
	 * @return
	 */
	private String comingoutFakeRole(){
		isComingout = true;
		return TemplateTalkFactory.comingout(getMe(), fakeRole);
	}

	/**
	 * 今までに出した黒判定の数を返す
	 * @return
	 */
	protected int getBlackJudgeNum(){
		int blackJudgeNum = 0;
		for(Judge judge: toldjudges){
			if(judge.getResult() == Species.WEREWOLF){
				blackJudgeNum++;
			}
		}
		for(Judge judge: notToldjudges){
			if(judge.getResult() == Species.WEREWOLF){
				blackJudgeNum++;
			}
		}
		return blackJudgeNum;
	}

	Judge getFakeJudge(int day){
		for(Judge j: fakeJudges){
			if(j.getDay() == day){
				return j;
			}
		}
		return null;
	}

	abstract void updateCOElements(boolean isVillagerWin);
	/*{
		
		if(fakeRole == Role.SEER || fakeRole == Role.MEDIUM){
			Map<Integer, Double> map = getCOMap();
			
			Map<Integer, Double> map = null;
			switch (fakeRole) {
			case SEER:
				if(getMyRole() == Role.WEREWOLF){
					map = ld.getWolfFakeSeerCO();
				}else if(getMyRole() == Role.POSSESSED){
					map = ld.getPossessedFakeSeerCO();
				}
				break;
			case MEDIUM:
				if(getMyRole() == Role.WEREWOLF){
					map = ld.getWolfFakeMediumCO();
				}else if(getMyRole() == Role.POSSESSED){
					map = ld.getPossessedFakeMediumCO();
				}
				break;
			}
			
			double q = map.get(coTiming.toHash());
			double reward = (isVillagerWin)? 100.0: 0;
			double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
			map.put(coTiming.toHash(), learnedQ);
		}

		Map<Integer, Double> map = ld.getWolfFakeMediumCO();
		double q = map.get(coTiming.toHash());
		double reward = (isVillagerWin)? 100.0: 0;
		double learnedQ = ReinforcementLearning.reInforcementLearn(q, reward, 0);
	}
*/
	@Override
	void updateMiddlePattern(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		super.updateMiddlePattern(day, patternPresent, patternNext, scenePresent, sceneNext);

		//嘘占い
		if(fakeRole == Role.SEER){
			updateMiddleFakeDivine(day, patternPresent, patternNext, scenePresent, sceneNext);
		}

		//嘘霊能
		if(fakeRole == Role.MEDIUM){
			updateMiddleFakeInquest(day, patternPresent, patternNext, scenePresent, sceneNext);
		}
	}


	public Role getFakeRole() {
		return fakeRole;
	}

	public void setFakeRole(Role fakeRole) {
		this.fakeRole = fakeRole;
	}

	void updateMiddleFakeInquest(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {

		if(day < 2){
			return;
		}
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());

		Judge fakeJudge = getFakeJudge(day-1);
		AgentPattern ap = patternPresent.getAgentPattern(fakeJudge.getTarget());
		Species judgeResult = fakeJudge.getResult();

		Map<AgentPattern, Map<Species, Double>> map = null;
		Map<AgentPattern, Double> mapNext = null;
		if(getMyRole() == Role.WEREWOLF){
			map = qVal.getWolfInquest();
			mapNext = qValNext.getWolfAttack();
		}else if(getMyRole() == Role.POSSESSED){
			map = qVal.getPossessedInquest();
			mapNext = qValNext.getPossessedVote();
		}

		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap).get(judgeResult), 0.0, nextMaxQVal);
		map.get(ap).put(judgeResult, learnedQ);
	}


	void updateMiddleFakeDivine(int day, Pattern patternPresent,
			Pattern patternNext, Scene scenePresent, Scene sceneNext) {
		if(day < 2){
			return;
		}
		Qvalues qVal = ld.getQvalue(scenePresent.getHashNum()),
				qValNext = ld.getQvalue(sceneNext.getHashNum());

		Judge fakeJudge = getFakeJudge(day-1);
		AgentPattern ap = patternPresent.getAgentPattern(fakeJudge.getTarget());
		Species judgeResult = fakeJudge.getResult();

		Map<AgentPattern, Map<Species, Double>> map = null;
		Map<AgentPattern, Double> mapNext = null;
		if(getMyRole() == Role.WEREWOLF){
			map = qVal.getWolfDivine();
			mapNext = qValNext.getWolfAttack();
		}else if(getMyRole() == Role.POSSESSED){
			map = qVal.getPossessedDivine();
			mapNext = qValNext.getPossessedVote();
		}

		double nextMaxQVal = Qvalues.getMaxQValue(mapNext);
		double learnedQ = ReinforcementLearning.reInforcementLearn(map.get(ap).get(judgeResult), 0.0, nextMaxQVal);
		map.get(ap).put(judgeResult, learnedQ);
	}




}
