package org.aiwolf.glyClient.LearningPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.lib.Pattern;
import org.aiwolf.glyClient.reinforcementLearning.COtimingNeo;

public abstract class AbstractGiftedPlayer extends AbstractKajiBasePlayer{
	//まだ報告していないjudge
	List<Judge> notToldjudges = new ArrayList<Judge>();

	//既に報告したjudge
	List<Judge> toldjudges = new ArrayList<Judge>();

	//カミングアウトしたか
	boolean isComingout = false;

	COtimingNeo coTiming;

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		Map<COtimingNeo, Double> map = getCOMap();
		coTiming = selectRandomTarget(map);
	}

	public boolean isJudged(Agent agent){

		Set<Agent> judgedAgents = new HashSet<Agent>();
		for(Judge judge: toldjudges){
			judgedAgents.add(judge.getTarget());
		}
		for(Judge judge: notToldjudges){
			judgedAgents.add(judge.getTarget());
		}

		if(judgedAgents.contains(agent)){
			return true;
		}else{
			return false;
		}

	}
	
	public Map<COtimingNeo, Double> getCOMap(){
		switch (getMyRole()) {
		case SEER:
			return ld.getSeerCO();
		case MEDIUM:
			return ld.getMediumCO();
		case POSSESSED:
			return ld.getPossessedCO();
		case WEREWOLF:
			return ld.getWolfCO();
		default:
			return null;
		}
	}

	public List<Pattern> getHypotheticalPatterns(List<Pattern> originPatterns, Judge judge){
		List<Pattern> hypotheticalPatterns = patternMaker.clonePatterns(originPatterns);
		patternMaker.updateJudgeData(hypotheticalPatterns, judge);
		return hypotheticalPatterns;
	}

	public String getTemplateComingoutText(){
		/*
		 * カミングアウトする日数になる
		 * 他に同じ能力者COが出る
		 * 人狼を見つける
		 * 投票先に選ばれそう（全体の2/3が投票かつ全投票中でマックスが自分）
		 */
		if(isComingout){
			return null;
		}else{
			//日数によるカミングアウト
			if(getDay() == coTiming.getDay() && coTiming.doComingout()){
				isComingout = true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}

			//偽CO出現
			if(coTiming.isAgainst()){
				Map<Agent, Role> comingoutMap = advanceGameInfo.getComingoutMap();
				for(Entry<Agent, Role> set: comingoutMap.entrySet()){
					if(set.getValue() == getMyRole() && !set.getKey().equals(getMe())){
						isComingout = true;
						return TemplateTalkFactory.comingout(getMe(), getMyRole());
					}
				}
			}

			//人狼見つける
			if(coTiming.isHasFoundWolf()){
				for(Judge judge: notToldjudges){
					if(judge.getResult() == Species.WEREWOLF){
						isComingout = true;
						return TemplateTalkFactory.comingout(getMe(), getMyRole());
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
						isComingout = true;
						return TemplateTalkFactory.comingout(getMe(), getMyRole());
					}
				}
			}
		}
		return null;
	}

}
