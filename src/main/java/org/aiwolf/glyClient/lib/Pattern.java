package org.aiwolf.glyClient.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.glyClient.reinforcementLearning.AgentPattern;

/**
 * 役職のCO状況のパターン
 * @author kengo
 *
 */
public class Pattern {
	/*
	 * 前提となる情報(プレイヤーと役職のセット)
	 * 確定敵の情報(プレイヤーと狂人かどうか．黒確と白確(他が全員黒確))
	 * 前提から決定する情報(どの前提でも確定する情報はどうするか)
	 * 尤度
	 * 　何日目に各役職が死んでる確率(COタイミング以降は意図的に殺せるから反映無し)
	 * 　何日目に占い，霊能で何人人狼が当たる確率
	 */

	//前提とする占い師と霊能者のエージェント
	private Agent seerAgent = null;
	private Agent mediumAgent = null;

	//敵サイド確定となるエージェント
	private Map<Agent, EnemyCase> enemyMap = new HashMap<Agent, EnemyCase>();

	/**
	 * TODO
	 * パターン作るときに偽物を代入
	 */
	private Set<Agent> fakeSeers = new HashSet<Agent>();
	private Set<Agent> fakeMediums = new HashSet<Agent>();
	
	private List<Agent> aliveAgents;
	//白確エージェント．(真能力者から白判定 or 襲撃死)
//	private List<Agent> whiteAgentList = new ArrayList<Agent>();
	private Set<Agent> whiteAgentSet = new HashSet<Agent>();

	//尤度
	private double likelifood = 0.0;
	
	private Map<Agent, Role> comingoutMap = new HashMap<Agent, Role>();
	
	//前日に処刑，襲撃されたプレイヤー
	private Agent executedAgent;
	private Agent attackedAgent;


	/**
	 *
	 * @param seerAgent
	 * @param mediumAgent
	 * @param comingoutMap
	 */
	public Pattern(Agent seerAgent, Agent mediumAgent, Map<Agent, Role> comingoutMap, List<Agent> aliveAgents){
		this.seerAgent = seerAgent;
		this.mediumAgent = mediumAgent;
		this.setComingoutMap(comingoutMap);
		for(Entry<Agent, Role> entry: comingoutMap.entrySet()){
			if(entry.getValue() != Role.SEER && entry.getValue() != Role.MEDIUM){
				continue;
			}
			if(!entry.getKey().equals(seerAgent) && !entry.getKey().equals(mediumAgent)){
				enemyMap.put(entry.getKey(), EnemyCase.gray);
			}
		}
		this.aliveAgents = aliveAgents;
	}

	public Pattern(){
		return;
	}

	/**
	 * 新しい占い，霊能結果を用いてパターンを更新する．整合性が取れない場合はfalseを返す
	 * @param judge
	 */
	public boolean updatePattern(Judge judge){
		Agent judgment = judge.getAgent();
		if(judgment == seerAgent || judgment == mediumAgent){
			switch (judge.getResult()) {
			case HUMAN:
				Agent target = judge.getTarget();
				whiteAgentSet.add(target);
				/**
				 * 敵陣営のプレイヤーなら狂人確定．他の敵を人狼と確定．
				 */
				if(enemyMap.containsKey(target)){
					Map<Agent, EnemyCase> enemyMapNew = new HashMap<Agent, EnemyCase>();
					for(Entry<Agent, EnemyCase> entry: enemyMap.entrySet()){
						if(entry.getKey().equals(target)){
							enemyMapNew.put(entry.getKey(), EnemyCase.white);
						}else{
							enemyMapNew.put(entry.getKey(), EnemyCase.black);
						}
					}
					enemyMap = enemyMapNew;
				}
				break;
			case WEREWOLF:
				enemyMap.put(judge.getTarget(), EnemyCase.black);
				break;
			}
		}

		if(!isPatternMatched()){
			return false;
		}
		/*
		 * 尤度を更新するアルゴリズムも必要か
		 */

		return true;
	}

	/**
	 * roleMapと整合するパターンの場合はtrueを返す
	 * @param roleMap
	 * @return
	 */
	public boolean isPatternMatched(Map<Agent, Role> roleMap){
		if(seerAgent != null && roleMap.get(seerAgent) != Role.SEER){
			return false;
		}
		else if(mediumAgent != null && roleMap.get(mediumAgent) != Role.MEDIUM){
			return false;
		}
		else{
			for(Entry<Agent, EnemyCase> set: enemyMap.entrySet()){
				if(roleMap.get(set.getKey()) != Role.WEREWOLF && roleMap.get(set.getKey()) != Role.POSSESSED){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isPatternMatched(){
		/*
		 * 人狼するプレイヤー数によって変化するようにしたい
		 */
		int enmeyNumber = 4;

		/**
		 * 敵の数が過多なら嘘．人狼の数がゲーム設定の人狼数を超えても嘘(狂人は1人設定)．
		 */
		if(enemyMap.size() > enmeyNumber){
			return false;
		}else if(enemyMap.size() == enmeyNumber){
			int blackNumber = 0;
			for(Entry<Agent, EnemyCase> entry: enemyMap.entrySet()){
				if(entry.getValue() == EnemyCase.black){
					blackNumber++;
				}
			}
			if(blackNumber > enmeyNumber - 1){
				return false;
			}
		}

		/**
		 * 白確定かつ黒確定がいれば嘘
		 */
		for(Entry<Agent, EnemyCase> entry: enemyMap.entrySet()){

		}
		return true;
	}
	
	public AgentPattern getAgentPattern(Agent agent){
		if(agent == null){
			return AgentPattern.NULL;
		}
		else if(agent.equals(seerAgent)){
			return AgentPattern.SEER;
		}
		else if(agent.equals(mediumAgent)){
			return AgentPattern.MEDIUM;
		}
		else if(fakeSeers.contains(agent)){
			if(enemyMap.containsKey(agent)){
				switch (enemyMap.get(agent)) {
				case black:
					return AgentPattern.FAKE_SEER_BLACK;
				case gray:
					return AgentPattern.FAKE_SEER_GRAY;
				case white:
					return AgentPattern.FAKE_SEER_WHITE;
				}
			}else{
				return AgentPattern.FAKE_SEER_GRAY;
			}
		}
		else if(fakeMediums.contains(agent)){
			if(enemyMap.containsKey(agent)){
				switch (enemyMap.get(agent)) {
				case black:
					return AgentPattern.FAKE_MEDIUM_BLACK;
				case gray:
					return AgentPattern.FAKE_MEDIUM_GRAY;
				case white:
					return AgentPattern.FAKE_MEDIUM_WHITE;
				}
			}else{
				return AgentPattern.FAKE_MEDIUM_GRAY;
			}
		}
		else if(enemyMap.containsKey(agent)){
			return AgentPattern.JUDGED_BLACK;
		}
		else if(whiteAgentSet.contains(agent)){
			return AgentPattern.WHITE_AGENT;
		}
		else if(agent.equals(executedAgent)){
			return AgentPattern.EXECUTED_AGENT;
		}
		else if(agent.equals(attackedAgent)){
			return AgentPattern.ATTACKED_AGENT;
		}
		return AgentPattern.NULL;
	}


	public Agent getSeerAgent() {
		return seerAgent;
	}

	public void setSeerAgent(Agent seerAgent) {
		this.seerAgent = seerAgent;
	}

	public Agent getMediumAgent() {
		return mediumAgent;
	}

	public void setMediumAgent(Agent mediumAgent) {
		this.mediumAgent = mediumAgent;
	}

	public Map<Agent, EnemyCase> getEnemyMap() {
		return enemyMap;
	}

	public void setEnemyMap(Map<Agent, EnemyCase> enemyMap) {
		this.enemyMap = enemyMap;
	}

	public Set<Agent> getWhiteAgentSet() {
		return whiteAgentSet;
	}

	public void setWhiteAgentSet(Set<Agent> whiteAgentSet) {
		this.whiteAgentSet = whiteAgentSet;
	}
/*
	public List<Agent> getWhiteAgentList() {
		return whiteAgentList;
	}

	public void setWhiteAgentList(List<Agent> whiteAgentList) {
		this.whiteAgentList = whiteAgentList;
	}*/

	public double getLikelifood() {
		return likelifood;
	}

	public void setLikelifood(double likelifood) {
		this.likelifood = likelifood;
	}

	@Override
	public Pattern clone(){
		Pattern clonePattern = new Pattern();
		clonePattern.setSeerAgent(seerAgent);
		clonePattern.setMediumAgent(mediumAgent);
		clonePattern.setEnemyMap(new HashMap<Agent, EnemyCase>(enemyMap));
		clonePattern.setFakeSeers(fakeSeers);
		clonePattern.setFakeMediums(fakeMediums);
		clonePattern.setAliveAgents(new ArrayList<Agent>(aliveAgents));
		clonePattern.setWhiteAgentSet(new HashSet<Agent>(whiteAgentSet));
		clonePattern.setLikelifood(likelifood);
		clonePattern.setComingoutMap(new HashMap<Agent, Role>(comingoutMap));
		clonePattern.setExecutedAgent(executedAgent);
		clonePattern.setAttackedAgent(attackedAgent);
		return clonePattern;
	}

	public Set<Agent> getFakeSeers() {
		return fakeSeers;
	}

	public void setFakeSeers(Set<Agent> fakeSeers) {
		this.fakeSeers = fakeSeers;
	}

	public Set<Agent> getFakeMediums() {
		return fakeMediums;
	}

	public void setFakeMediums(Set<Agent> fakeMediums) {
		this.fakeMediums = fakeMediums;
	}

	public List<Agent> getAliveAgents() {
		return aliveAgents;
	}

	public void setAliveAgents(List<Agent> aliveAgents) {
		this.aliveAgents = aliveAgents;
	}

	public Map<Agent, Role> getComingoutMap() {
		return comingoutMap;
	}

	public void setComingoutMap(Map<Agent, Role> comingoutMap) {
		this.comingoutMap = comingoutMap;
	}

	public Agent getExecutedAgent() {
		return executedAgent;
	}

	public void setExecutedAgent(Agent executedAgent) {
		this.executedAgent = executedAgent;
	}

	public Agent getAttackedAgent() {
		return attackedAgent;
	}

	public void setAttackedAgent(Agent attackedAgent) {
		this.attackedAgent = attackedAgent;
	}


}
