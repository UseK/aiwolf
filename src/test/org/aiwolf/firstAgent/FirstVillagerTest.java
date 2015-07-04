package test.org.aiwolf.firstAgent;

import org.aiwolf.common.net.GameInfo;
import org.aiwolf.firstAgent.FirstVillager;
import org.junit.BeforeClass;


public class FirstVillagerTest {

	static FirstVillager firstVillager;

	@BeforeClass
	public static void setupGameInfo() {
		@SuppressWarnings("unused")
		GameInfo gameInfo = new GameInfo();
		firstVillager = new FirstVillager();
	}
}
