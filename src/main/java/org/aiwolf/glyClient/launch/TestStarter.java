package org.aiwolf.glyClient.launch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.smpl.SampleRoleAssignPlayer;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Team;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.glyClient.LearningPlayer.RoleAssignPlayer;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.DirectConnectServer;

public class TestStarter {
	static protected int GAME_NUM = 100;
	static protected int PLAYER_NUM = 15;

	public static void main(String[] args) throws IOException {
		int villageWinNum = 0;
		int werewolfWinNum = 0;
		for (int gi = 0; gi < GAME_NUM; gi++) {

			/* configure playerlist in "playerMap" */
			Map<Player, Role> playerMap = new HashMap<>();

			playerMap.put(new RoleAssignPlayer(), Role.SEER);
			playerMap.put(new RoleAssignPlayer(), Role.MEDIUM);
			playerMap.put(new RoleAssignPlayer(), Role.BODYGUARD);

			for (int i = 0; i < 8; i++) {
				playerMap.put(new RoleAssignPlayer(), Role.VILLAGER);
			}

			for (int i = 0; i < 3; i++) {
				playerMap.put(new RoleAssignPlayer(), Role.WEREWOLF);
			}

			for (int i = 0; i < PLAYER_NUM - 11; i++) {
				playerMap.put(new SampleRoleAssignPlayer(), null);
			}

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
		System.out.println("village: " + villageWinNum + ", werewolf: "
				+ werewolfWinNum);
	}
}
