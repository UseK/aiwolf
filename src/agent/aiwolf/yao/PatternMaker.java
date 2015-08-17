package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;

public class PatternMaker {
	public void initialize( List<Pattern> pattern, List<Agent> players ){
		int playerSize=players.size();
		for( int i = 0 ; i < playerSize; i++ ){
			for( int j=i+1; j <playerSize; j++){
				for( int k=j+1; k< playerSize; k++ ){
					for( int p=0; p<playerSize; p++){
						if( i!=p && j!=p && k!=p){
							Pattern curPattern= new Pattern(players.get(i),players.get(j),players.get(k),players.get(p));
							pattern.add(curPattern);
						}
					}
				}
			}
		}
	}
	public void setSeer( List<Pattern> pattern, Agent seer){
		ArrayList<Pattern> removePattern = new ArrayList<Pattern>();
		for(Pattern p: pattern){
			p.addSeer(seer);
			if( p.contradict() )removePattern.add(p);
		}
		pattern.removeAll(removePattern);
	}
	public void setMedium(List <Pattern> pattern, Agent medium){
		ArrayList<Pattern> removePattern = new ArrayList<Pattern>();
		for(Pattern p: pattern){
			p.addMedium(medium);
			if( p.contradict() )removePattern.add(p);
		}
		pattern.removeAll(removePattern);
	}
}
