package test.org.aiwolf.firstAgent;

import static org.junit.Assert.*;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.firstAgent.VillageSideThought;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VillageSideThoughtTest {

	VillageSideThought thought;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		thought = new VillageSideThought();
		for(int i = 0; i < 15; i++) {
			thought.suspiciousPoints.put(Agent.getAgent(i), 0);
		}
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testInpterpretComingout() {
		String t = "COMINGOUT Agent[14] SEER";
		Agent agent = Agent.getAgent(1);
		Utterance utterance = new Utterance(t);
		Talk talk = new Talk(0, 2, Agent.getAgent(14), t);

		assertEquals(0, thought.comingoutedSeerList.size());
		thought.responseComingOut(utterance, talk);
		assertEquals(1, thought.comingoutedSeerList.size());
		assertEquals(Agent.getAgent(14),
		thought.comingoutedSeerList.get(0));
	}

	@Test
	public void testInpterpretDivined() {
		String t = "DIVINED Agent[09] HUMAN";
		Utterance utterance = new Utterance(t);
		Talk talk = new Talk(0, 2, Agent.getAgent(9), t);
		thought.responseDivination(utterance, talk);
	}
}
