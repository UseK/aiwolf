package agent.aiwolf.kajiClient.reinforcementLearning;

import java.util.Map.Entry;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import agent.aiwolf.kajiClient.lib.EnemyCase;
import agent.aiwolf.kajiClient.lib.Pattern;



/**
 * Patternを抽象化したもの．Agentが違っていても，状態として同じならば同じものとして捉える
 * @author kajiwarakengo
 *
 */
public class Scene {
	private static final int
		WOLF_NUM = 2,
		IS_SEER_HASH = 1,
		IS_SEER_ALIVE_HASH = 2,
		IS_MEDIUM_HASH = 4,
		IS_MEDIUM_ALIVE_HASH = 8,
		SEER_NUM_HASH = 16,
		MEDIUM_NUM_HASH = SEER_NUM_HASH * (WOLF_NUM + 3),
		BLACK_ENEMY_NUM_HASH = MEDIUM_NUM_HASH * (WOLF_NUM + 3),
		WHITE_ENEMY_NUM_HASH = BLACK_ENEMY_NUM_HASH * (WOLF_NUM + 1),
		GRAY_ENEMY_NUM_HASH = WHITE_ENEMY_NUM_HASH * 2,
		WHITE_AGENT_NUM_HASH = GRAY_ENEMY_NUM_HASH * (WOLF_NUM + 2);

	private boolean isSeer,
					isSeerAlive,
					isMedium,
					isMediumAlive;

	private int		seerNum,
					mediumNum,
					blackEnemyNum,
					whiteEnemyNum,
					grayEnemyNum,
					whiteAgentNum;

	public Scene(Pattern p){
		isSeer = (p.getSeerAgent() == null)? false: true;
		isMedium = (p.getMediumAgent() == null)? false: true;
		isSeerAlive = (p.getAliveAgents().contains(p.getSeerAgent()))? true: false;
		isMediumAlive = (p.getAliveAgents().contains(p.getMediumAgent()))? true: false;
		for(Entry<Agent, Role> set: p.getComingoutMap().entrySet()){
			switch (set.getValue()) {
			case SEER:
				seerNum++;
				break;
			case MEDIUM:
				mediumNum++;
				break;
			}
		}

		for(Entry<Agent, EnemyCase> set: p.getEnemyMap().entrySet()){
			switch (set.getValue()) {
			case black:
				blackEnemyNum++;
				break;
			case gray:
				grayEnemyNum++;
				break;
			case white:
				whiteEnemyNum++;
				break;
			}
		}
		whiteAgentNum = p.getWhiteAgentSet().size();
	}

	public Scene getScene(Pattern p){
		return new Scene(p);
	}

	public Scene(int hash){
		whiteAgentNum = hash / WHITE_AGENT_NUM_HASH;
		hash = hash % WHITE_AGENT_NUM_HASH;

		grayEnemyNum = hash / GRAY_ENEMY_NUM_HASH;
		hash = hash % GRAY_ENEMY_NUM_HASH;

		whiteEnemyNum = hash / WHITE_ENEMY_NUM_HASH;
		hash = hash % WHITE_ENEMY_NUM_HASH;

		blackEnemyNum = hash / BLACK_ENEMY_NUM_HASH;
		hash = hash % BLACK_ENEMY_NUM_HASH;

		mediumNum = hash / MEDIUM_NUM_HASH;
		hash = hash % MEDIUM_NUM_HASH;

		seerNum = hash / SEER_NUM_HASH;
		hash = hash % SEER_NUM_HASH;

		isMediumAlive = (hash/IS_MEDIUM_ALIVE_HASH == 1)? true: false;
		hash = hash % IS_MEDIUM_ALIVE_HASH;

		isMedium = (hash/IS_MEDIUM_HASH == 1)? true: false;
		hash = hash % IS_MEDIUM_HASH;

		isSeerAlive  = (hash/IS_SEER_ALIVE_HASH == 1)? true: false;
		hash = hash % IS_SEER_ALIVE_HASH;

		isSeer = (hash/IS_SEER_HASH == 1)? true: false;
	}

	public int getHashNum(){
		/**
		 * private boolean isSeer,
					isMedium;
	private int		blackEnemyNum,
					whiteEnemyNum,
					grayEnemyNum,
					whiteAgnetNum;
		 */
		/*
		 * isSeer 2
		 * isMedium 2
		 * seerNum 5
		 * mediumNum 5
		 * blackNum 3
		 * whiteNum 2
		 * gray 4
		 * whiteAgent たくさん
		 */

		int hash = 0;

		hash += (isSeer)? IS_SEER_HASH: 0;
		hash += (isSeerAlive)? IS_SEER_ALIVE_HASH: 0;
		hash += (isMedium)? IS_MEDIUM_HASH: 0;
		hash += (isMediumAlive)? IS_MEDIUM_HASH: 0;

		hash += seerNum * SEER_NUM_HASH;
		hash += mediumNum * MEDIUM_NUM_HASH;
		hash += blackEnemyNum * BLACK_ENEMY_NUM_HASH;
		hash += whiteEnemyNum * WHITE_ENEMY_NUM_HASH;
		hash += grayEnemyNum * GRAY_ENEMY_NUM_HASH;
		hash += whiteAgentNum * WHITE_AGENT_NUM_HASH;
		return hash;
	}

	public boolean isSeer() {
		return isSeer;
	}

	public void setSeer(boolean isSeer) {
		this.isSeer = isSeer;
	}

	public boolean isMedium() {
		return isMedium;
	}

	public void setMedium(boolean isMedium) {
		this.isMedium = isMedium;
	}

	public int getSeerNum() {
		return seerNum;
	}

	public void setSeerNum(int seerNum) {
		this.seerNum = seerNum;
	}

	public int getMediumNum() {
		return mediumNum;
	}

	public void setMediumNum(int mediumNum) {
		this.mediumNum = mediumNum;
	}

	public int getBlackEnemyNum() {
		return blackEnemyNum;
	}

	public void setBlackEnemyNum(int blackEnemyNum) {
		this.blackEnemyNum = blackEnemyNum;
	}

	public int getWhiteEnemyNum() {
		return whiteEnemyNum;
	}

	public void setWhiteEnemyNum(int whiteEnemyNum) {
		this.whiteEnemyNum = whiteEnemyNum;
	}

	public int getGrayEnemyNum() {
		return grayEnemyNum;
	}

	public void setGrayEnemyNum(int grayEnemyNum) {
		this.grayEnemyNum = grayEnemyNum;
	}

	public int getWhiteAgentNum() {
		return whiteAgentNum;
	}

	public void setWhiteAgentNum(int whiteAgentNum) {
		this.whiteAgentNum = whiteAgentNum;
	}

}
