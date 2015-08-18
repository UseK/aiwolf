package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

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
		if( isComingOut== false){
			if( super.day >=4 ){
				isComingOut=true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}
			if( myJudgeList.size() > 0 && myJudgeList.get(myJudgeList.size()-1).getResult() == Species.WEREWOLF){
				isComingOut=true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}
			if( super.yaoGameInfo.getMaxVotedAgent() == getMe() ){
				isComingOut=true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}
			if( super.yaoGameInfo.getMediums().size() >0 ){
				isComingOut=true;
				return TemplateTalkFactory.comingout(getMe(), getMyRole());
			}
		}
		return null;
	}

	public void dayStart() {
		super.dayStart();
		if(getLatestDayGameInfo().getMediumResult() != null){
			myJudgeList.add(getLatestDayGameInfo().getMediumResult());
		}
	}


	public void update(GameInfo gameinfo){
		super.update(gameinfo);
		believeMedium=getMe();
		toldBelieveMedium=getMe();	

	}
	
}
