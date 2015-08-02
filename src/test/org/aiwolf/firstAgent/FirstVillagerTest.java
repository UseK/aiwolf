package test.org.aiwolf.firstAgent;

import org.aiwolf.common.net.GameInfo;
import org.junit.BeforeClass;

import agent.aiwolf.usek.UseKVillager;


public class FirstVillagerTest {

	static UseKVillager firstVillager;

	@BeforeClass
	public static void setupGameInfo() {
		@SuppressWarnings("unused")
		GameInfo gameInfo = new GameInfo();
		firstVillager = new UseKVillager();
	}
}
