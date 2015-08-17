package agent.aiwolf.yao;

import java.util.List;

import org.aiwolf.common.data.Agent;

public class GrassFoxBodyguard extends AbstractYaoBasePlayer {
	boolean seerAttacked=false;
	boolean mediumAttacked=false;
	Agent guardAgent=null;
	
	public Agent guard(){
		//ˆê“xGJ‚ğo‚µ‚½Œì‰qæ‚É‚Í“S”ÂŒì‰q
		if( guardAgent != null && super.yaoGameInfo.getAlivePlayers().contains(guardAgent) ){
			return guardAgent;
		}		
		if( seerAttacked == false ){
			//è‚¢‚ğŠš‚Ü‚ê‚Ä‚È‚¯‚ê‚Îè‚¢Œì‰qB(Šù‚Éè‚¢Œó•â‚ªˆê“xŠš‚Ü‚ê‚Ä‚¢‚éê‡A‘¼‚Ìè‚¢‚àŠš‚Ü‚ê‚½‚Ù‚¤‚ª“à–ó‚ª‚·‚Á‚«‚è‚·‚é‚Ì‚ÅA‚»‚±‚ÍŒì‰q‚µ‚È‚¢B
			List<Agent> seers= super.yaoGameInfo.getSeers();
			if( seers!=null && seers.size() >= 1){
				super.sortSeers(seers);
				return seers.get(0);
			}
		}
		if( mediumAttacked == false ){
			//—ì”\‚ğŠš‚Ü‚ê‚Ä‚¢‚È‚¯‚ê‚Î—ì”\Œì‰qB
			List<Agent> mediums = super.yaoGameInfo.getMediums();
			if( mediums.size()>=1 ) return super.getRandom(mediums);
		}
		// ƒ‰ƒ“ƒ_ƒ€Œì‰qBŠm’è•‚ÍŒì‰q‚µ‚È‚¢
		List<Agent> guardTargets=super.yaoGameInfo.getAlivePlayers();
		guardTargets.removeAll(super.yaoGameInfo.getAliveEnemies());
		return super.getRandom(guardTargets);
	}

	@Override
	public void dayStart() {
		super.dayStart();
		if( getLatestDayGameInfo().getAttackedAgent()==getLatestDayGameInfo().getGuardedAgent()){
			//System.out.println("Guarded"+getLatestDayGameInfo().getGuardedAgent());
			guardAgent=getLatestDayGameInfo().getGuardedAgent();
		}
		else if(super.yaoGameInfo.getSeers().contains(getLatestDayGameInfo().getAttackedAgent())){
			seerAttacked=true;
		}
		else if(super.yaoGameInfo.getMediums().contains(getLatestDayGameInfo().getAttackedAgent())){
			mediumAttacked=true;
		}
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
