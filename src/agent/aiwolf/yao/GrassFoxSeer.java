package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;

public class GrassFoxSeer extends AbstractYaoBasePlayer {

	//��{�I�ɂ͂Q���ڂ���肢CO�����Č��ʂ������Ă�������
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
			//�����T��T��
			return getSeerTarget(candidates);
		}
		else{
			//�����T�����Ȃ��������͂����肵�Ȃ��ӏ�������ꍇ�A������肤
			candidates=super.yaoGameInfo.getAliveGraysOfSeer(getMe());
			if( candidates.size()>0 ) return getSeerTarget(candidates);
		}
		candidates=super.yaoGameInfo.getAlivePlayers();
		candidates.removeAll(judgedAgent);
		for( Agent e: candidates){
			//�肢���_�ŏ󋵏؋��I�ɘT�Ƃ킩���Ă���Ƃ�����ꉞ�����ɐ���Ă���
			if(super.yaoGameInfo.getSeerTable(getMe(),e)==Species.WEREWOLF){
				return e;
			}
		}
		return getSeerTarget(candidates);
	}
	
	public Agent getSeerTarget(List<Agent> candidates){
		//������Ƃ����肢�����i��B��̓I�ɂ͕[�ς��񐔂������l��D��I�ɐ肤�Ƃ���B
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
