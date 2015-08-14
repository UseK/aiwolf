package org.aiwolf.glyClient.launch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.smpl.SampleRoleAssignPlayer;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.LearningPlayer.RoleAssignPlayer;
import org.aiwolf.glyClient.reinforcementLearning.LearningData;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;

public class TestStarter {
	static protected int GAME_NUM = 10000;
	static protected int PLAYER_NUM = 15;

	public static void main(String[] args) throws IOException {
		int villageWinNum = 0;
		int werewolfWinNum = 0;

		// プレイヤーのインスタンスリスト
		List<RoleAssignPlayer> players = new ArrayList<RoleAssignPlayer>(
				PLAYER_NUM);

		for (int i = 0; i < PLAYER_NUM; ++i){
			LearningData.getInstance(i).LDStart();
			RoleAssignPlayer player = new RoleAssignPlayer();
			player.setLDNumber(i);
			players.add(player);
		}
		for (int gi = 0; gi < GAME_NUM; gi++) {

			// 学習データを読み込む（あれば）

			/* configure playerlist in "playerMap" */
			Map<Player, Role> playerMap = new HashMap<>();
			
			// ID: 0 は占い
			playerMap.put(players.get(0), Role.SEER);
			// ID: 1 は霊能
			playerMap.put(players.get(1), Role.MEDIUM);
			// ID: 2 は狩人
			playerMap.put(players.get(2), Role.BODYGUARD);
			// ID: 3 は狂人
			playerMap.put(players.get(3), Role.POSSESSED);
			
			// ID: 4-6は人狼（15人なら3人のはず）
			for( int i = 4; i <= 6; ++i )
				playerMap.put(players.get(i), Role.WEREWOLF);
			
			// ID: 7-Lastは村人
			for( int i = 7; i < PLAYER_NUM; ++i )
				playerMap.put(players.get(i), Role.VILLAGER);

			DirectConnectServer gameServer = new DirectConnectServer(playerMap);
			GameSetting gameSetting = GameSetting.getDefaultGame(playerMap
					.size());
			AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
			game.setRand(new Random(gameSetting.getRandomSeed()));
			game.start();
			Team winner = game.getWinner();
			if (winner == Team.VILLAGER) {
				villageWinNum++;
			} else {
				werewolfWinNum++;
			}

		}

		// 学習結果を保存する　
		for (int i = 0; i < PLAYER_NUM; ++i)
			LearningData.getInstance(i).LDFinish();

		System.out.println("village: " + villageWinNum + ", werewolf: "
				+ werewolfWinNum);

	}
}
