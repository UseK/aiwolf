package agent.aiwolf.kajiClient.reinforcementLearning;

import org.aiwolf.common.data.Agent;
import org.aiwolf.kajiClient.lib.Pattern;

public enum AgentPattern {
	NULL,
	
	SEER,
	
	MEDIUM,
	
	FAKE_SEER_BLACK,
	FAKE_SEER_WHITE,
	FAKE_SEER_GRAY,
	
	FAKE_MEDIUM_BLACK,
	FAKE_MEDIUM_WHITE,
	FAKE_MEDIUM_GRAY,
	
	JUDGED_BLACK,
	
	WHITE_AGENT,
	//襲撃されたエージェント
	ATTACKED_AGENT,
	//処刑されたエージェント
	EXECUTED_AGENT,
	
	ORDINARY_AGENT;

	public AgentPattern getAgentPattern(Pattern p, Agent a){
		
		if(a.equals(p.getSeerAgent())){
			return SEER;
		}else if(a.equals(p.getMediumAgent())){
			return MEDIUM;
		}
		
		
		return null;
	}
	
}
