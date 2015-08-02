package agent.aiwolf.yao;

import java.util.ArrayList;

import org.aiwolf.client.base.player.AbstractMedium;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;

public class YaoMedium extends AbstractMedium {
	private boolean isComingOut=false;
	private ArrayList<Judge> myJudgeResult;
	private ArrayList<Judge> toldJudgeResult=new ArrayList<Judge>();
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub
		myJudgeResult=getMyJudgeList();
		if(!isComingOut){
			isComingOut=true;
			return TemplateTalkFactory.comingout(getMe(), Role.MEDIUM);
		}
		else{
			for(Judge judge: myJudgeResult){
				if(!toldJudgeResult.contains(judge)){
					toldJudgeResult.add(judge);
					return TemplateTalkFactory.inquested(judge.getTarget(), judge.getResult());
				}
			}
		}
		return TemplateTalkFactory.over();
	}

	@Override
	public Agent vote() {
		// TODO Auto-generated method stub
		return null;
	}

}
