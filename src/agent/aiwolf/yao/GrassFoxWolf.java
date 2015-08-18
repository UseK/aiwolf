package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class GrassFoxWolf extends AbstractYaoBasePlayer {

	Agent possessed=null;
	List<Agent> wolves;
	Map<Agent,Agent> attack = new HashMap<Agent,Agent>();
	List<Agent> fakeWolves= new ArrayList<Agent>();//êËÇ¢étÇ…ÇÊÇ¡ÇƒòTÇæÇ∆Ç»Ç∑ÇËÇ¬ÇØÇÁÇÍÇƒÇ¢ÇÈêlÇΩÇø
	private int readWhisperNumber=0;
	private Agent toldAttackAgent=null;
	private Agent attackAgent=null;
	private Agent guardedAgent=null;
	private int n_deadVillager=0;
	
	public void update(GameInfo gameInfo){
		super.update(gameInfo);
		List<Talk> whisperList = gameInfo.getWhisperList();
		for(; readWhisperNumber < whisperList.size(); readWhisperNumber++){
			Talk talk = whisperList.get(readWhisperNumber);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {
				case ATTACK:
					attackWhisperDealing(talk, utterance);
				default:
			}
		}
		
		for( Agent s: super.yaoGameInfo.getSeers()){
			for( Agent a: super.yaoGameInfo.getAlivePlayers()){
				if( fakeWolves.contains(a) || wolves.contains(a) )continue;
				if( yaoGameInfo.getSeerTable(s, a) == Species.WEREWOLF){
					fakeWolves.add(a);
				}
			}
		}
		
		setAttackAgent();
	}
	
	public void setAttackAgent(){
		//çUåÇêÊÇåàíËÇ∑ÇÈÅBè≠Ç»Ç≠Ç∆Ç‡òTåÛï‚ÇäöÇÒÇ≈ÇµÇ‹Ç§Ç±Ç∆Ç™ñ≥Ç¢ÇÊÇ§Ç…ÇµÇ¬Ç¬ÉâÉìÉ_ÉÄÇ…äöÇﬁÅB
		List<Agent> candidates=super.yaoGameInfo.getAliveEnemies();
		candidates.removeAll(wolves);
		if( guardedAgent!= null )candidates.remove(guardedAgent);
		candidates.removeAll(fakeWolves);
		if( attackAgent!=null && candidates.contains(attackAgent)) return;
		attackAgent= super.getRandom(candidates);
	}

	@Override
	public String whisper() {
		// TODO Auto-generated method stub
		if( attackAgent!=null && toldAttackAgent!= attackAgent){
			toldAttackAgent = attackAgent;
			String talk = TemplateWhisperFactory.attack(attackAgent);
			return talk;
		}
		return null;
	}
	
	public void attackWhisperDealing(Talk talk, Utterance utterance){
		attack.put(talk.getAgent(), utterance.getTarget());
	}
	
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		super.initialize(gameInfo, gameSetting);
		wolves=new ArrayList<Agent>();
		for(Entry<Agent, Role> set: gameInfo.getRoleMap().entrySet()){
			wolves.add(set.getKey());
		}
	}

	public void dayStart() {
		super.dayStart();
		attack=new HashMap<Agent,Agent>();
		readWhisperNumber=0;
		if(getLatestDayGameInfo().getAliveAgentList().contains(getLatestDayGameInfo().getAttackedAgent())){
			guardedAgent=getLatestDayGameInfo().getAttackedAgent();
		}
		else{
			Agent attacked=getLatestDayGameInfo().getAttackedAgent();
			if( !super.yaoGameInfo.getSeers().contains(attacked) && !super.yaoGameInfo.getMediums().contains(attacked) ) n_deadVillager++;
			guardedAgent=null;
		}
		Agent executed=getLatestDayGameInfo().getExecutedAgent();
		if( !wolves.contains(executed) && !super.yaoGameInfo.getSeers().contains(executed) && !super.yaoGameInfo.getMediums().contains(executed) ){
			n_deadVillager++;
		}
		attackAgent=null;
	}
	
	@Override
	public String getJudgeText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComingoutText() {
		// TODO Auto-generated method stub
		return null;
	}

}
