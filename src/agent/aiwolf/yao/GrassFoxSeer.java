package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;

public class GrassFoxSeer extends AbstractYaoBasePlayer {

	//Šî–{“I‚É‚Í‚Q“ú–Ú‚©‚çè‚¢CO‚ğ‚µ‚ÄŒ‹‰Ê‚ğŒ¾‚Á‚Ä‚¢‚­‚¾‚¯
	boolean isComingOut=false;
	List<Judge> myJudgeList= new ArrayList<Judge>(); 
	List<Agent> judgedAgent=new ArrayList<Agent>();
	int toldJudgeNum=0;
	
	@Override
	public String getJudgeText() {
		// TODO Auto-generated method stub
		if(isComingOut && toldJudgeNum<myJudgeList.size()){
			String talk = TemplateTalkFactory.divined(myJudgeList.get(toldJudgeNum).getTarget(), myJudgeList.get(toldJudgeNum).getResult());
			toldJudgeNum++;
			return talk;
		}
		return null;
	}

	public void dayStart() {
		super.dayStart();
		if(getLatestDayGameInfo().getDivineResult() != null){
			myJudgeList.add(getLatestDayGameInfo().getDivineResult());
			judgedAgent.add(getLatestDayGameInfo().getDivineResult().getTarget());
		}
	}
	@Override
	public String getComingoutText() {
		// TODO Auto-generated method stub
		if( isComingOut==false && super.day >=2 ){
			isComingOut=true;
			return TemplateTalkFactory.comingout(getMe(), getMyRole());
		}
		return null;
	}

	public Agent divine() {
		// TODO Auto-generated method stub
		int n_FakeSeers=Math.max(0, super.yaoGameInfo.getSeers().size()-1);
		int n_FakeMediums=Math.max(0, super.yaoGameInfo.getMediums().size()-1);
		int n_enemies=super.yaoGameInfo.getEnemies().size();
		List<Agent> candidates= super.yaoGameInfo.getAliveGraysOfSeer(getMe());
		candidates.removeAll(super.yaoGameInfo.getSeers());
		candidates.removeAll(super.yaoGameInfo.getMediums());
		if( n_FakeSeers+n_FakeMediums+n_enemies <= 3 && candidates.size() >= 0){
			//ö•š˜T‚ğ’T‚·
			return getSeerTarget(candidates);
		}
		else{
			//ö•š˜T‚ª‚¢‚È‚¢‚ª”’•‚Í‚Á‚«‚è‚µ‚È‚¢‰ÓŠ‚ª‚ ‚éê‡A‚»‚±‚ğè‚¤
			candidates=super.yaoGameInfo.getAliveGraysOfSeer(getMe());
			if( candidates.size()>0 ) return getSeerTarget(candidates);
		}
		candidates=super.yaoGameInfo.getAlivePlayers();
		candidates.removeAll(judgedAgent);
		for( Agent e: candidates){
			//è‚¢‹“_‚Åó‹µØ‹’“I‚É˜T‚Æ‚í‚©‚Á‚Ä‚¢‚é‚Æ‚±‚ë‚ğˆê‰³®‚Éè‚Á‚Ä‚¨‚­
			if(super.yaoGameInfo.getSeerTable(getMe(),e)==Species.WEREWOLF){
				return e;
			}
		}
		return getSeerTarget(candidates);
	}
	
	public Agent getSeerTarget(List<Agent> candidates){
		//‚¿‚å‚Á‚Æ‚¾‚¯è‚¢Œó•â‚ği‚éB‹ï‘Ì“I‚É‚Í•[•Ï‚¦‰ñ”‚ª‘½‚¢l‚ğ—Dæ“I‚Éè‚¤‚Æ‚·‚éB
		if( candidates==null) return null;
		if( candidates.size() == 0 ) return null;
		int worstScore=-1;
		List<Agent> worstCandidates = new ArrayList<Agent>();
		for( Agent c: candidates ){
			int score =super.yaoGameInfo.getFakeVoteNum(c);
			if( worstScore < score ){
				worstScore = score;
				worstCandidates = new ArrayList<Agent>();
				worstCandidates.add(c);
			}
			if( worstScore == score ){
				worstCandidates.add(c);
			}
		}
		return super.getRandom(worstCandidates);
	}
}
