package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;

public class Pattern{
	Agent seer=null;
	Agent medium=null;
	Agent bodyguard=null;
	
	Agent possessed=null;
	Agent wolves[];
	
	List<Agent> fakeseer;
	List<Agent> fakemedium;
	List<Agent> fakebodyguard;
	boolean contradict;
	
	Pattern(){
		contradict=false;
		wolves=new Agent[3];
		fakeseer=new ArrayList<Agent>();
		fakemedium=new ArrayList<Agent>();
		fakebodyguard= new ArrayList<Agent>();
	}
	Pattern(Agent a, Agent b, Agent c, Agent p){
		contradict=false;
		wolves=new Agent[3];
		fakeseer=new ArrayList<Agent>();
		fakemedium=new ArrayList<Agent>();
		fakebodyguard= new ArrayList<Agent>();
		possessed=p;
		wolves[0]=a;wolves[1]=b;wolves[2]=c;
	}
	
	public void addSeer(Agent seer){
		boolean isFake=false;
		if(seer==possessed){
			isFake=true;
		}
		for(int i=0;i<3;i++){
			if( seer==wolves[i] ){
				isFake=true;
			}
		}
		if( isFake&&!fakeseer.contains(seer) ) fakeseer.add(seer);
		else if(this.seer==null){
			this.seer=seer;
		}
		else{
			contradict=true;
		}
	}
	
	public void addMedium(Agent medium){
		boolean isFake=false;
		if(medium==possessed){
			isFake=true;
		}
		for(int i=0;i<3;i++){
			if( medium==wolves[i] ){
				isFake=true;
			}
		}
		if( isFake&&!fakemedium.contains(medium) ) fakemedium.add(medium);
		else if(this.medium==null){
			this.medium=medium;
		}
		else{
			contradict=true;
		}
	}
	public void addBodyGuard(Agent bodyguard){
		boolean isFake=false;
		if(bodyguard==possessed){
			isFake=true;
		}
		for(int i=0;i<3;i++){
			if( bodyguard==wolves[i] ){
				isFake=true;
			}
		}
		if( isFake&&!fakemedium.contains(bodyguard) ) fakemedium.add(bodyguard);
		else if(this.medium==null){
			this.medium=bodyguard;
		}
		else{
			contradict=true;
		}
	}
	public boolean contradict(){
		return contradict;
	}
}
