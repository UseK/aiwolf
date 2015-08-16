package org.aiwolf.glyClient.launch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.LearningPlayer.RoleAssignPlayer;
import org.aiwolf.glyClient.reinforcementLearning.LearningData;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;

public class TestStarter {
	static protected int GAME_NUM = 1;
	static protected int PLAYER_NUM = 15;

	public static void main(String[] args) throws IOException {
		int villageWinNum = 0;
		int werewolfWinNum = 0;

		// プレイヤーのインスタンスリスト
		List<RoleAssignPlayer> players = new ArrayList<RoleAssignPlayer>(
				PLAYER_NUM + 1);

		// プレイヤーインスタンス自体は16個作らないと行けない
		for (int i = 0; i <= PLAYER_NUM; ++i) {
			LearningData.getInstance(i).LDStart();
			RoleAssignPlayer player = new RoleAssignPlayer();
			player.setLDNumber(i);
			players.add(player);
		}
		for (int gi = 0; gi < GAME_NUM; gi++) {

			// 学習データを読み込む（あれば）

			/* configure playerlist in "playerMap" */
			Map<Player, Role> playerMap = new HashMap<>();

			// ID: 1 は占い
			playerMap.put(players.get(1), Role.SEER);
			// ID: 2 は霊能
			playerMap.put(players.get(2), Role.MEDIUM);
			// ID: 3 は狩人
			playerMap.put(players.get(3), Role.BODYGUARD);
			// ID: 4 は狂人
			playerMap.put(players.get(4), Role.POSSESSED);

			// ID: 5-7は人狼（レギュレーションより，3人のはず）
			for (int i = 5; i <= 7; ++i)
				playerMap.put(players.get(i), Role.WEREWOLF);

			// ID: 8-Lastは村人
			for (int i = 8; i <= PLAYER_NUM; ++i)
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
		for (int i = 1; i <= PLAYER_NUM; ++i)
			LearningData.getInstance(i).LDFinish();

		System.out.println("village: " + villageWinNum + ", werewolf: "
				+ werewolfWinNum);

	}
}
