package test.org.aiwolf.firstAgent;

import static org.junit.Assert.*;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.firstAgent.FirstAgent;
import org.aiwolf.firstAgent.FirstVillager;
import org.junit.BeforeClass;
import org.junit.Test;


public class FirstVillagerTest {

	static FirstVillager firstVillager;

	@BeforeClass
	public static void setupGameInfo() {
		GameInfo gameInfo = new GameInfo();
		firstVillager = new FirstVillager();
	}
}
