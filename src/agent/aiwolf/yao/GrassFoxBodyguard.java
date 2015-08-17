package agent.aiwolf.yao;

import java.util.List;

import org.aiwolf.common.data.Agent;

public class GrassFoxBodyguard extends AbstractYaoBasePlayer {
	boolean seerAttacked=false;
	boolean mediumAttacked=false;
	Agent guardAgent=null;
	
	public Agent guard(){
		//一度GJを出した護衛先には鉄板護衛
		if( guardAgent != null && super.yaoGameInfo.getAlivePlayers().contains(guardAgent) ){
			return guardAgent;
		}
		if( super.believeSeer!=null ){
			//占い盲信なら占い護衛
			return super.believeSeer;
		}
		if( seerAttacked == false ){
			//占いを噛まれてなければ占い護衛。(既に占い候補が一度噛まれている場合、他の占いも噛まれたほうが内訳がすっきりするので、そこは護衛しない。
			List<Agent> seers= super.yaoGameInfo.getSeers();
			if( seers!=null && seers.size() >= 1){
				super.sortSeers(seers);
				return seers.get(0);
			}
		}
		if( super.believeMedium!=null){
			//霊能盲信なら霊能護衛
			return super.believeMedium;
		}
		if( mediumAttacked == false ){
			//霊能を噛まれていなければ霊能護衛。
			List<Agent> mediums = super.yaoGameInfo.getMediums();
			if( mediums.size()>=1 ) return super.getRandom(mediums);
		}
		// ランダム護衛。確定黒は護衛しない
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
