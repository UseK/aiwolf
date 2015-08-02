package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractBodyguard;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class YaoBodyguard extends AbstractBodyguard {
	final int GRAY_RANDOM=0;
	final int MEDIUM_ROLLER=1;
	final int DEVINED_WOLF=-100000;
	final int DEVINED_VILLAGER=100000;
	
	List <Agent> playerList=new ArrayList<Agent>();
	List <Agent> seerList=new ArrayList<Agent>();
	List <Agent> mediumList= new ArrayList<Agent>();
	List <Agent> grayList=new ArrayList<Agent>();
	List<Agent> believeSeer=new ArrayList<Agent>();
	int doubtpoint[];
	int evalTable[][];

	int mode=GRAY_RANDOM;
	int voteAgent=-1;
	boolean voteChange=false;
	
	int readTalkNum;
	int today=0;


	
	@Override
	public void dayStart() {
		// TODO Auto-generated method stub
		readTalkNum=0;
		voteChange=true;
		today++;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public Agent guard() {
		// TODO Auto-generated method stub
		Collections.shuffle(believeSeer);
		List<Agent> whiteList=new ArrayList<Agent>();
		for(Agent a : believeSeer){
			if( getLatestDayGameInfo().getAliveAgentList().contains(a)){
				return a;
			}
			int aid=playerList.indexOf(a);
			for( int i = 0 ; i < playerList.size(); i++ ){
				if( evalTable[aid][i]==DEVINED_VILLAGER){
					whiteList.add(playerList.get(i));
				}
			}
		}
		Collections.shuffle(mediumList);
		for( Agent a: mediumList){
			if( getLatestDayGameInfo().getAliveAgentList().contains(a)){
				return a;
			}
		}
		Collections.shuffle(whiteList);
		for( Agent a: whiteList ){
			if( getLatestDayGameInfo().getAliveAgentList().contains(a)){
				return a;
			}
		}
		List<Agent> liveList = getLatestDayGameInfo().getAliveAgentList();
		Collections.shuffle(liveList);
		return liveList.get(0);
	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub		
		if( voteChange ){
			String voteChangeTalk=TemplateTalkFactory.vote(playerList.get(voteAgent));
			voteChange=false;
			return voteChangeTalk;			
		}	
		return Talk.SKIP;
	}

	


	public void update(GameInfo gameInfo){
		if( playerList.size()==0 ){
			playerList.addAll(gameInfo.getAgentList());
			grayList.addAll(playerList);
			doubtpoint= new int[playerList.size()];
			evalTable=new int[playerList.size()][playerList.size()];
			voteAgent=new Random().nextInt( playerList.size());
			voteChange=true;
		}
		List<Talk> talkList=gameInfo.getTalkList();
		int roleId=0;
		int targetId=0;
		for( int i =readTalkNum; i<talkList.size(); i++,readTalkNum++ ){
			Talk talk=talkList.get(i);
			Utterance utterance=new Utterance(talk.getContent());
			switch(utterance.getTopic()){
			case COMINGOUT:
			if(utterance.getRole() == Role.SEER){
				seerList.add(talk.getAgent());
				grayList.remove(talk.getAgent());
			}
			else if(utterance.getRole() == Role.MEDIUM ){
				mediumList.add(talk.getAgent());
				grayList.remove(talk.getAgent());
			}
			break;
			case DIVINED:
				roleId=playerList.indexOf(talk.getAgent());
				targetId=playerList.indexOf(utterance.getTarget());
				if( utterance.getResult() == Species.WEREWOLF ){
					evalTable[roleId][targetId]=DEVINED_WOLF;
				}
				else{
					evalTable[roleId][targetId]=DEVINED_VILLAGER;
				}
				grayList.remove(utterance.getTarget());
				break;
			case INQUESTED:
				roleId=playerList.indexOf(talk.getAgent());
				targetId=playerList.indexOf(utterance.getTarget());
				if( utterance.getResult() == Species.WEREWOLF ){
					evalTable[roleId][targetId]=DEVINED_WOLF;
				}
				else{
					evalTable[roleId][targetId]=DEVINED_VILLAGER;
				}
			}
		}
		
		// decide policy
		// medium roller check
		if( mediumList.size() > 1 )
		{
			System.out.println("Medium Roller! now "+ mediumList.size() +"mediums");
			for(Agent a : mediumList ){
				if( gameInfo.getAliveAgentList().contains(a)){
					if(voteAgent!=playerList.indexOf(a)){
						voteAgent= playerList.indexOf(a);
						voteChange=true;
					}
					return;
				}
			}
		}
		
		// find wrong seer
		believeSeer=new ArrayList<Agent>();
		for( Agent a: seerList ){
			int id=playerList.indexOf(a);
			int w_count=0;
			boolean belive=true;
			for( int i = 0 ; i < playerList.size(); i++ ){
				if( evalTable[id][i] == DEVINED_WOLF){
					w_count++;
					if( mediumList.size()==1 ){
						// see contradiction with medium
						int med=playerList.indexOf(mediumList.get(0));
						if( evalTable[med][i] == DEVINED_VILLAGER ){
							belive=false;
						}
					}
				}
				else{
					if( mediumList.size()==1 ){
						// see contradiction with medium
						int med=playerList.indexOf(mediumList.get(0));
						if( evalTable[med][i] == DEVINED_WOLF ){
							w_count++;
						}
					}
					if( evalTable[id][i] == DEVINED_VILLAGER){
						if( mediumList.size()==1 ){
							// see contradiction with medium
							int med=playerList.indexOf(mediumList.get(0));
							if( evalTable[med][i] == DEVINED_WOLF ){
								belive=false;
							}
						}
					}
				}
			}
			if( w_count>3 ){ // contradiction of wolf number
				belive=false;
			}
			if( belive == false ){
				if( gameInfo.getAliveAgentList().contains(a)){
					if(voteAgent!=id){
						System.out.println("Kill liar seer");
						voteAgent= id;
						voteChange=true;
						return;
					}
				}
			}
			else{
				believeSeer.add(a);
			}
		}
		
		// find live black
		List<Agent> liveBlack=new ArrayList<Agent>();

		for( Agent a: believeSeer){
			int id= playerList.indexOf(a);
			for( int i = 0 ; i < playerList.size(); i++ ){
				if( evalTable[id][i]==DEVINED_WOLF && gameInfo.getAliveAgentList().contains(playerList.get(i)) ){
					liveBlack.add(playerList.get(i));
				}
			}
		}
		
		if( liveBlack.size() > 0){
			System.out.println("Kill black");
			if(liveBlack.contains(playerList.get(voteAgent)) ){
				return;
			}
			else{
				Agent blackAgent=liveBlack.get(new Random().nextInt( liveBlack.size()));
				voteAgent=playerList.indexOf(blackAgent);
				voteChange=true;
				return;
			}
		}
		
		// gray random
		List<Agent> liveGray=new ArrayList<Agent>();
		for( Agent a: grayList){
			if(gameInfo.getAliveAgentList().contains(a) ){
				liveGray.add(a);
			}
		}
		if( liveGray.size()>0 ){
			System.out.println("Kill gray random");
			if(liveGray.contains(playerList.get(voteAgent))){
				return;
			}
			else{
				Agent grayAgent=liveGray.get(new Random().nextInt(liveGray.size()));
				voteAgent=playerList.indexOf(grayAgent);
				voteChange=true;
				return;
			}
		}
		
		// random
		if( !gameInfo.getAliveAgentList().contains(playerList.get(voteAgent))){
			System.out.println("gave up... random vote");
			List<Agent> liveAgent=gameInfo.getAliveAgentList();
			liveAgent.remove(getMe());
			Agent randomAgent=liveAgent.get(new Random().nextInt(liveAgent.size()));
			voteAgent=playerList.indexOf(randomAgent);
			voteChange=true;
			return;
		}		
	}
	
	@Override
	public Agent vote() {
		// TODO Auto-generated method stub
		List<Agent> liveAgent=getLatestDayGameInfo().getAliveAgentList();
		if( liveAgent.contains( playerList.get(voteAgent))){
			return playerList.get(voteAgent);
		}
		else{
			return null;
		}
	}

}
