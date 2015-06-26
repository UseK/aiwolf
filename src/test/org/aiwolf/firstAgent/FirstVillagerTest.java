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


	@Test
	public void testInpterpretComingout() {
		String t = "COMINGOUT Agent[14] SEER";
		Agent agent = Agent.getAgent(1);
		Utterance utterance = new Utterance(t);
		Talk talk = new Talk(0, 2, Agent.getAgent(14), t);

		assertEquals(0, firstVillager.comingoutedSeerList.size());
		firstVillager.interpretComingout(utterance, talk);
		assertEquals(1, firstVillager.comingoutedSeerList.size());
		assertEquals(Agent.getAgent(14),
					firstVillager.comingoutedSeerList.get(0));
	}

	@Test
	public void testInpterpretDivined() {
		String t = "DIVINED Agent[09] HUMAN";
		Agent agent = Agent.getAgent(0);
		Utterance utterance = new Utterance(t);
		Talk talk = new Talk(0, 2, agent, t);
		firstVillager.interpretDivined(utterance, talk);
	}

}
