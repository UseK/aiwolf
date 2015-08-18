package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;

public class GrassFoxBodyguard extends AbstractYaoBasePlayer {
	boolean seerAttacked=false;
	boolean mediumAttacked=false;
	Agent guardAgent=null;
	
	public Agent guard(){
		//��xGJ���o������q��ɂ͓S��q
		if( guardAgent != null && super.yaoGameInfo.getAlivePlayers().contains(guardAgent) ){
			return guardAgent;
		}
		if( super.believeSeer!=null ){
			//�肢�ӐM�Ȃ�肢��q
			return super.believeSeer;
		}
		if( seerAttacked == false ){
			//�肢�����܂�ĂȂ���ΐ肢��q�B(���ɐ肢��₪��x���܂�Ă���ꍇ�A���̐肢�����܂ꂽ�ق������󂪂������肷��̂ŁA�����͌�q���Ȃ��B
			List<Agent> seers= super.yaoGameInfo.getSeers();
			if( seers!=null && seers.size() >= 1){
				super.sortSeers(seers);
				return seers.get(0);
			}
		}
		if( super.believeMedium!=null){
			//��\�ӐM�Ȃ��\��q
			return super.believeMedium;
		}
		if( mediumAttacked == false ){
			//��\�����܂�Ă��Ȃ���Η�\��q�B
			List<Agent> mediums = super.yaoGameInfo.getMediums();
			if( mediums.size()>=1 ) return super.getRandom(mediums);
		}
		// �����_����q�B�m�荕�͌�q���Ȃ�
		List<Agent> guardTargets=super.yaoGameInfo.getAlivePlayers();
		guardTargets.removeAll(super.yaoGameInfo.getAliveEnemies());
		return getWhitestPlayer(guardTargets);
	}
	
	public Agent getWhitestPlayer(List<Agent> candidates){
		if( candidates==null ) return null;
		if( candidates.size() == 0) return null;
		List<Agent> whitestList=new ArrayList<Agent>();
		int maxscore=0;
		List<Agent> seers=super.yaoGameInfo.getSeers();
		for( Agent c: candidates){
			int score=0;
			for( Agent s: seers){
				if( super.yaoGameInfo.getSeerTable(s, c)==Species.WEREWOLF){
					//�T���ƌ����Ă���l�͊��܂��΂�����
					score-=10;
				}
				if( super.yaoGameInfo.getSeerTable(s,c)==Species.HUMAN ){
					score+=1;
				}
			}
			if( score>maxscore){
				maxscore=score;
				whitestList=new ArrayList<Agent>();
				whitestList.add(c);
			}
			if( score==maxscore){
				whitestList.add(c);
			}
		}
		return super.getRandom(whitestList);
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
