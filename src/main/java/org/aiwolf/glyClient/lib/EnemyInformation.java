package org.aiwolf.glyClient.lib;

import org.aiwolf.common.data.Agent;
import org.aiwolf.kajiClient.lib.EnemyCase;

/**
 * 敵情報を記録する．敵であるエージェント
 * @author kengo
 *
 */
public class EnemyInformation {
	Agent enemy = null;
	EnemyCase eCase = null;
	
	public EnemyInformation(){
		
	}
	
	/**
	 * 敵エージェントとそのプレイヤーの情報(白確or黒確or灰色)を引数とする．
	 * @param enemyAgent
	 * @param enemyCase
	 */
	public EnemyInformation(Agent enemyAgent, EnemyCase enemyCase){
		
	}

}
