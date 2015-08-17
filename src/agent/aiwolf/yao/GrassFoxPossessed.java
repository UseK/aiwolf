package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

public class GrassFoxPossessed extends AbstractYaoBasePlayer {

	//基本的には占い騙りに出て狼をサポートできるような占いを出す方針。
	boolean isComingOut=false;
	List<Judge> myJudgeList= new ArrayList<Judge>(); 
	List<Agent> wolves = new ArrayList<Agent>();
	int n_seerWolves=0;
	int n_mediumWolves=0;
	int toldJudgeNum=0;
	int toldWolves=0;

	@Override
	public String getJudgeText() {
		// TODO Auto-generated method stub
		if(isComingOut && toldJudgeNum<myJudgeList.size()){
			String talk = TemplateTalkFactory.divined(myJudgeList.get(toldJudgeNum).getTarget(), myJudgeList.get(toldJudgeNum).getResult());
			if( myJudgeList.get(toldJudgeNum).getResult()==Species.WEREWOLF ){
				toldWolves++;
			}
			toldJudgeNum++;
			
			return talk;
		}
		return null;
	}
	
	public void update(GameInfo gameinfo){
		super.update(gameinfo);
		if( isComingOut ){
			if( super.yaoGameInfo.getSeers().size() >= 3 ) n_seerWolves=Math.max(n_seerWolves,super.yaoGameInfo.getSeers().size()-2);
			if( super.yaoGameInfo.getMediums().size() >= 2 ) n_seerWolves=Math.max(n_seerWolves, super.yaoGameInfo.getMediums().size()-1);
		}
		//意地でも占いは信じない
		believeSeer=null;
	}
	public void dayStart() {
		super.dayStart();
		if(super.day>=2){
			Judge j = getFakeJudge();
			if( j!=null ){
				myJudgeList.add(j);				
			}
		}
	}
	
	public Judge getFakeJudge(){
		if(super.yaoGameInfo.getEnemies().contains(getMe())) return null;
		List<Agent> list=super.yaoGameInfo.getAliveGraysOfSeer(getMe());
		list.remove(getMe());
		System.out.println("Let's devine ");
		for( Agent l: list) System.out.print(l+" ");
		System.out.println();
		int maxWolfNum= super.yaoGameInfo.getMaxWolves(getMe());
		Judge j = new Judge(super.day, getMe(), super.getRandom(list), Species.HUMAN);
		return j;
	}

	@Override
	public String getComingoutText() {
		// TODO Auto-generated method stub
		if( isComingOut==false && super.day >=2 ){
			isComingOut=true;
			return TemplateTalkFactory.comingout(getMe(), Role.SEER);
		}
		return null;
	}

}
