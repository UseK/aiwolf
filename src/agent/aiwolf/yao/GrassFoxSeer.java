package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;

public class GrassFoxSeer extends AbstractYaoBasePlayer {

	//基本的には２日目から占いCOをして結果を言っていくだけ
	boolean isComingOut=false;
	List<Judge> myJudgeList= new ArrayList<Judge>(); 
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
		List<Agent> a= super.yaoGameInfo.getAliveGraysOfSeer(getMe());
		return super.getRandom(a);
	}
	
}
