package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Judge;

public class GrassFoxMedium extends AbstractYaoBasePlayer {
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


	@Override
	public String getComingoutText() {
		// TODO Auto-generated method stub
		if( isComingOut==false && super.day >=2 ){
			isComingOut=true;
			return TemplateTalkFactory.comingout(getMe(), getMyRole());
		}
		return null;
	}

	public void dayStart() {
		super.dayStart();
		if(getLatestDayGameInfo().getMediumResult() != null){
			myJudgeList.add(getLatestDayGameInfo().getMediumResult());
		}
	}

	
}
