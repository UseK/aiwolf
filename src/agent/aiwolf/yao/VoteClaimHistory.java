package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Vote;

public class VoteClaimHistory {
	private List<List<List<Agent>>> votehistory;
	private List<Agent> latestVoteList;
	
	VoteClaimHistory(int n_player){
		votehistory=new ArrayList<List<List<Agent>>>();
		for( int i=0;i<=n_player; i++){
			ArrayList<List<Agent>> tmparray=new ArrayList<List<Agent>>();
			for(int j=0; j<=2*n_player; j++){
				tmparray.add(new ArrayList<Agent>());
			}
			votehistory.add(tmparray);
			
		}
		latestVoteList=new ArrayList<Agent>();
		for(int i=0 ; i<= n_player; i++){
			latestVoteList.add(null);
		}
	}
	
	public void voteUpdate(int date, Agent from, Agent to){
		int fromIdx=from.getAgentIdx();
		ArrayList<Agent> curVoteList = (ArrayList<Agent>) votehistory.get(fromIdx).get(date);
		if( votehistory.get(fromIdx).get(date).size()==0 ){
			votehistory.get(fromIdx).get(date).add(to);
			latestVoteList.set(fromIdx,to);
		}
		else if( curVoteList.get(curVoteList.size()-1) != to ){
			votehistory.get(fromIdx).get(date).add(to);
			latestVoteList.set(fromIdx,to);
		}
	}
}
