package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class YaoGameInfo {
	// 村にとって客観的に確定している情報のみを格納するクラス。
	
	private int playerSize; //プレイヤー数
	
	private List<Agent> allPlayers=new ArrayList<Agent>();
	private List<Agent> alivePlayers=new ArrayList<Agent>(); // 生存しているプレイヤーリスト
	private List<Agent> attackedPlayers=new ArrayList<Agent>(); // 襲撃されたプレイヤーリスト
	private List<Agent> executedPlayers=new ArrayList<Agent>(); // 処刑したプレイヤーリスト
	private List<Agent> grayPlayers=new ArrayList<Agent>();	// 完全グレーのリスト
	
	private Role claimTable[][]; //各人が各人をどの役職だと思っているのかのテーブル
	private List<Agent> seerCandidates = new ArrayList<Agent>();    // 占い候補リスト
	private List<Agent> seerCOs=new ArrayList<Agent>(); // 占いCOをした人のリスト
	private Species seerTable[][]; //占い結果を格納するテーブル
	private List<Agent> mediumCandidates = new ArrayList<Agent>();  // 霊能候補リスト
	private List<Agent> mediumCOs=new ArrayList<Agent>(); // 霊能COをした人のリスト
	private Species mediumTable[][];//霊能結果を格納するテーブル
	private List<Agent> guardCandidates = new ArrayList<Agent>();   // 狩人候補リスト
	private List<Agent> enemies = new ArrayList<Agent>();		    // 100% 人狼陣営と判明した人のリスト

	private VoteClaimHistory voteHistory;							// 各人の投票主張の履歴
	
	YaoGameInfo(GameInfo gameInfo, GameSetting gameSetting){
		allPlayers=gameInfo.getAliveAgentList();
		grayPlayers=gameInfo.getAliveAgentList();
		alivePlayers=gameInfo.getAliveAgentList();
		playerSize=grayPlayers.size();
		claimTable=new Role[playerSize+1][playerSize+1];
		seerTable=new Species[playerSize+1][playerSize+1];
		mediumTable=new Species[playerSize+1][playerSize+1];
		voteHistory=new VoteClaimHistory(playerSize);		
		enemies=new ArrayList<Agent>();
		for(int i=0;i<=playerSize;i++)for(int j=0;j<=playerSize;j++){
			claimTable[i][j]=null;seerTable[i][j]=null;mediumTable[i][j]=null;
		}
	}
	public List<Agent> getAlivePlayers(){
		return new ArrayList<Agent>(alivePlayers);
	}
/*	public void setEnemy(Agent target){
		seerCandidates.remove(target);
		mediumCandidates.remove(target);
		guardCandidates.remove(target);
		enemies.add(target);
	}*/
	
	public void comintoutSeer(Agent target) {
		grayPlayers.remove("ComingoutSeer:" + target);
		if(!seerCandidates.contains(target)){
			seerCandidates.add(target);
			seerTable[target.getAgentIdx()][target.getAgentIdx()]=Species.HUMAN;
		}
		if(!seerCOs.contains(target)) seerCOs.add(target);
		if( mediumCandidates.remove(target) || guardCandidates.remove(target) ){			
			//複数役職騙りは無条件に敵とみなす
			enemies.add(target);
		}
	}

	
	public int getMaxWolves(Agent seer){
		//seer視点の狼最大数を数える
		//占い結果が狼の人の死亡者数を数える
		int n_wolves=3;
		for( Agent a: allPlayers){
			if( !getEnemiesOfSeer(seer).contains(a) ){
				if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]==Species.WEREWOLF){
					n_wolves--;
				}
			}
		}
		//村が継続する上で可能な狼の数の最大値
		n_wolves=Math.min(n_wolves,(alivePlayers.size()-1)/2);

		//占い視点で敵と確定している人の中で死亡者数から計算する。
		int n_enemies=4;
		for(Agent a:getEnemiesOfSeer(seer)){
			if( !alivePlayers.contains(a) ){
				n_enemies--;
			}
		}
		n_wolves=Math.min(n_enemies, n_wolves);
		return n_wolves;
		
	}
	public boolean possibleToExecute(Agent seer, Agent p){
		//Agent視点で pを処刑しても詰まないかどうかを判定する。
		if( enemies.contains(seer) ) return true;
		int n_remainWolves=getMaxWolves(seer);
		//pが狼であるか、2人目以上の敵であればセーフである。(人数ギリギリからの狂人吊りによる積みは…考えない)
		if(seerTable[seer.getAgentIdx()][p.getAgentIdx()]==Species.WEREWOLF) return true;
		if(getEnemiesOfSeer(seer).contains(p) && !getPossessedOfSeer(seer).contains(p)){
			for( Agent e: getEnemiesOfSeer(seer) ){
				if( alivePlayers.contains(e)) return true;
			}
		}
		if((alivePlayers.size()-3)/2 >= n_remainWolves ) return true; 
		return false;
	}
	public double getWolfProbability(Agent seer, Agent p){
		//seer視点でpが狼である可能性を返す
		//ここは客観的に測れないということが後で解ったのであまりこのクラスにあるのは適切で無いかもしれない。
		if( enemies.contains(seer) ) return 0.0;
		if( enemies.contains(p)) return 100.0;
		if( seerTable[seer.getAgentIdx()][p.getAgentIdx()] == Species.WEREWOLF) return 100.0;
		if( seerTable[seer.getAgentIdx()][p.getAgentIdx()] == Species.HUMAN) return 0.0;
		int n_remainWolves=getMaxWolves(seer);
		if( getPossessedOfSeer(seer).contains(p) ){
			return 0.0;
		}
		if( getEnemiesOfSeer(seer).contains(p)||seerCandidates.contains(p)){
			if( getPossessedOfSeer(seer).size()>=1) return 100.0;
			if( getEnemiesOfSeer(seer).contains(p) ) return 75.0;
			else return 50.0;
		}		
		if(mediumCandidates.contains(p)){
			if(mediumCandidates.size()>1){
				if( linePossible(seer,p) ){	return 25.0;} //ラインはつながってるので
				else return 50.0; //霊能３以上はロラってるはずなのでとりあえず半々偽くらいで見ておこう
			}
		}
			n_remainWolves-=Math.max(0,(seerCandidates.size()-1));
			n_remainWolves-=Math.max(0,(mediumCandidates.size()-1));
			int n_unknown=0;
			for( Agent a: alivePlayers ){
				if( !seerCandidates.contains(a) && !mediumCandidates.contains(a)){
					if(seerTable[seer.getAgentIdx()][a.getAgentIdx()]==Species.WEREWOLF){
						n_remainWolves--;
					}
					if(seerTable[seer.getAgentIdx()][a.getAgentIdx()]==null){
						n_unknown++;
					}
				}
			}
			if( n_remainWolves <= 0 ) return 0.0;
			if( n_unknown == 0) return 100.0;
			return n_remainWolves*100.0/n_unknown;
		
	}
	public void comingoutMedium(Agent target) {
		grayPlayers.remove(target);
		if(!mediumCandidates.contains(target)) mediumCandidates.add(target);
		if(!mediumCOs.contains(target)) mediumCOs.add(target);
		if( seerCandidates.remove(target)|| guardCandidates.remove(target) ){
			//複数役職騙りは無条件に敵とみなす
			enemies.add(target);
		}
	}
	
	public void checkContradiction(){
		//破綻チェック。
		//ただし「襲撃先に白を出した破綻」は含まないattackedUpdateで処理してる。
		//ここでのチェックは主に人数の帳尻が合わない破綻である。
		//TODO:狩人２CO以上に正直対応出来ていない。
		for( Agent s: seerCandidates){
			if( seerContradiction(s) ) if( !enemies.contains(s) ){
				enemies.add(s);
			}
		}
		seerCandidates.removeAll(enemies);
		for( Agent m: mediumCandidates){
			if( mediumContradiction(m)){
				if( !enemies.contains(m) ) enemies.add(m);
			}
		}
		mediumCandidates.removeAll(enemies);
	}
	public boolean seerContradiction(Agent seer){
		//seerが破綻していないかチェックする
		if(impliedDevineUpdate(seer)==false) return true;
		return false;
	}
	public boolean impliedDevineUpdate(Agent seer){
		//状況的に占い視点で白黒はっきりしているところを補完する。成功すればtrue、矛盾があればfalseを返す。
		//case1. 敵の中で狂人が一人はっきりしている場合、それ意外の敵は狼確定
		if( enemies.contains(seer) ) return false;
		List<Agent> seerPossessed=getPossessedOfSeer(seer);
		List<Agent> seerEnemies=getEnemiesOfSeer(seer);
		if( seerPossessed.size()>=2 ) return false;
		if( seerPossessed.size()==1 ){
			for(Agent a: seerEnemies){
				if( !a.equals(seerPossessed.get(0))){
					seerTable[seer.getAgentIdx()][a.getAgentIdx()]=Species.WEREWOLF;
				}
			}
		}
		//case2. 狼が全露呈している場合、それ意外の人は村確定(一般的な場合)
		int n_wolves=0;
		int n_aliveWolves=0;
		int n_aliveEnemies=0;
		for(Agent a: seerEnemies){
			if(seerTable[seer.getAgentIdx()][a.getAgentIdx()]==Species.WEREWOLF){
				n_wolves++;
				if( alivePlayers.contains(a)) n_aliveWolves++;
			}
			if( alivePlayers.contains(a)) n_aliveEnemies++;
		}
		if( n_wolves>3 || seerEnemies.size()>4) return false;
		else if( n_wolves==3 || seerEnemies.size()==4){
			for( Agent a: allPlayers){
				if( !seerEnemies.contains(a)) seerTable[seer.getAgentIdx()][a.getAgentIdx()]=Species.HUMAN;
			}
		}
		//　case3. 生きている狼が上限数ギリギリであった場合、それ意外の生きてるは村確定
		if( n_aliveWolves>=(alivePlayers.size()-1)/2){
			for( Agent a: alivePlayers){
				if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]!= Species.WEREWOLF){
					seerTable[seer.getAgentIdx()][a.getAgentIdx()]=Species.HUMAN;
				}
			}
		}
		// case4. 生きている敵が上限数ギリギリであった場合、それ意外の生きている人は村確定
		if( n_aliveEnemies>=(alivePlayers.size()+1)/2){
			for( Agent a: alivePlayers){
				if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]!= Species.WEREWOLF){
					seerTable[seer.getAgentIdx()][a.getAgentIdx()]=Species.HUMAN;
				}
			}
		}
		// case5. 生きている人の中で占っていない人が一人しか残っていない場合、その人は狼確定
		List<Agent> aliveGrays=getAliveGraysOfSeer(seer);
		if( aliveGrays.size() == 0 ) return false;
		if( aliveGrays.size() == 1 ){
			seerTable[seer.getAgentIdx()][aliveGrays.get(0).getAgentIdx()]=Species.HUMAN;
		}
		return true;
	}
	public boolean impliedInquestedUpdate(Agent medium){
		//状況的に霊能視点で白黒はっきりしているところを補完する。成功すればtrue、矛盾があればfalseを返す。
		//という関数を用意したが、占いと違いここは補完出来るところがあまりなさそうである。
		return true;
	}
	
	public List<Agent> getAliveGraysOfSeer(Agent seer){
		List<Agent> aliveGrays = new ArrayList<Agent>();
		for( Agent a: alivePlayers){
			if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]==null){
				aliveGrays.add(a);
			}
		}
		return aliveGrays;
	}
	
	
	public boolean mediumContradiction(Agent medium){
		// mediumが破綻していないかどうかをチェックする
		if(impliedInquestedUpdate(medium)==false) return true;
		return false;
	}
	
	public void devinedUpdate(Agent from, Agent to, Species result){
		comintoutSeer(from);
		seerTable[from.getAgentIdx()][to.getAgentIdx()]=result;
		checkContradiction();
	}
	
	public void inquestedUpdate(Agent from, Agent to, Species result){
		comingoutMedium(from);
		mediumTable[from.getAgentIdx()][to.getAgentIdx()]=result;
		checkContradiction();
	}
	
	public void voteUpdate(int date, Agent from, Agent to){
		voteHistory.voteUpdate(date,from,to);
	}
	
	public void attackedUpdate(Agent target){
		if( target==null||attackedPlayers.contains(target) ) return;
		attackedPlayers.add(target);
		alivePlayers.remove(target);
		grayPlayers.remove(target);
		
		// 襲撃先に狼を出した占いは破綻。
		List<Agent> removeAgents=new ArrayList<Agent>();
		for( Agent seer : seerCandidates){
			if(seerTable[seer.getAgentIdx()][target.getAgentIdx()]==Species.WEREWOLF){
				enemies.add(seer);
				removeAgents.add(seer);
			}
			else{
				seerTable[seer.getAgentIdx()][target.getAgentIdx()]=Species.HUMAN;
			}
		}
		seerCandidates.removeAll(removeAgents);
		checkContradiction();
	}
	
	public void executedUpdate(Agent target){
		if(target==null||executedPlayers.contains(target)) return;
		executedPlayers.add(target);
		alivePlayers.remove(target);
		grayPlayers.remove(target);
		checkContradiction();
	}	
	
	/*public int mediumSize(){
		return mediumCandidates.size();
	}
	public int seerSize(){
		return seerCandidates.size();
	}*/
	
	public List<Agent> getSeers(){ return new ArrayList<Agent>(seerCandidates); }
	public List<Agent> getMediums(){ return new ArrayList<Agent>(mediumCandidates); }
	public List<Agent> getEnemies(){ return new ArrayList<Agent>(enemies); }

	public List<Agent> getEnemies(Agent seer, Agent medium){
		if( !linePossible(seer,medium) ) return null;
		List<Agent> seerEnemies=getEnemiesOfSeer(seer);
		List<Agent> mediumEnemies=getEnemiesOfMedium(medium);
		for(Agent e: mediumEnemies){
			if( !seerEnemies.contains(e)) seerEnemies.add(e);
		}
		return seerEnemies;
	}
	
	public List<Agent> getAliveEnemies(Agent seer, Agent medium){
		List<Agent> ret;
		if( seer==null && medium==null ) ret= enemies;
		else if( seer==null ) ret=getEnemiesOfMedium(medium);
		else if( medium==null) ret=getEnemiesOfSeer(seer);
		else ret= getEnemies(seer,medium);
		if( ret==null ) ret=new ArrayList<Agent>();
		ret.retainAll(alivePlayers);
		return ret;
	}

	public List<Agent> getEnemiesOfMedium(Agent medium){
		//mediumが霊能だった時の敵確定メンバを返す。mediumが客観的にみて敵である場合はnullを返す。
		if( enemies.contains(medium) ) return null;
		if( !mediumCandidates.contains(medium)){
			//System.err.println("Error: "+medium+" is not contained in medium");
		}
		List<Agent> ret=new ArrayList<Agent>(enemies);
		List<Agent> fakeSeers=getFakeSeersOfMedium(medium);
		for(Agent s: fakeSeers) if(!ret.contains(s)) ret.add(s);
		return ret;
	}
	
	public List<Agent> getEnemiesOfSeer( Agent seer ){
		//seerが占いだった時の敵確定メンバを返す。seerが客観的にみて敵である場合はnullを返す。
		if( enemies.contains(seer) ){
			return null;
		}
		if( !seerCandidates.contains(seer) ){
			//System.err.println("Error: "+seer+" is not contained in seer");
		}
		List<Agent> ret=new ArrayList<Agent>(enemies);
		for( Agent s: seerCandidates){
			if( s!= seer&&!ret.contains(s) ) ret.add(s);
		}
		List<Agent> fakeMediums=getFakeMediumsOfSeer(seer);
		for( Agent m: fakeMediums){
			if(!ret.contains(m)) ret.add(m);
		}
		return ret;
	}
	
	public List<Agent> getAliveEnemiesOfSeer(Agent seer){
		//seerが占いだった時の生存している敵確定メンバを返す。
		List<Agent> ret=getEnemiesOfSeer(seer);
		ret.retainAll(alivePlayers);
		return ret;
	}

	public List<Agent> getAliveEnemies(){
		List<Agent> ret=new ArrayList<Agent>(enemies);
		ret.retainAll(alivePlayers);
		return ret;
	}
	
	public List<Agent> getAliveMediums(){
		List<Agent> ret=new ArrayList<Agent>(mediumCandidates);
		ret.retainAll(alivePlayers);
		return ret;
	}
	public List<Agent> getAliveSeers(){
		List<Agent> ret=new ArrayList<Agent>(seerCandidates);
		ret.retainAll(alivePlayers);
		return ret;
	}
	
	public boolean linePossible(Agent s, Agent m){
		// s-mラインが成立しうるかを返す
		if( enemies.contains(s) || enemies.contains(m)) return false; //そもそもどっちかが破綻
		// 結果が食い違っている
		for( int i=1; i<=playerSize; i++ ){
			if( seerTable[s.getAgentIdx()][i] != null && mediumTable[m.getAgentIdx()][i]!=null){
				if( seerTable[s.getAgentIdx()][i]!=mediumTable[m.getAgentIdx()][i]){
					return false;
				}
			}
		}
		// seer-mラインでの人狼の数に矛盾がある
		List<Agent> lineEnemy=getEnemiesOfSeer(s);
		List<Agent> tmpEnemy=getEnemiesOfMedium(m);
		if( lineEnemy == null )lineEnemy = new ArrayList<Agent>();
		if( tmpEnemy == null )tmpEnemy = new ArrayList<Agent>();

		for( Agent a: tmpEnemy) if(!lineEnemy.contains(a) ) lineEnemy.add(a);
		if(lineEnemy.size() >= 5) return false;
		
		// seerから見た時に狂人が2人以上いる
		int n_possessed=0;
		for( Agent a: lineEnemy){
			if( seerTable[s.getAgentIdx()][a.getAgentIdx()]==Species.HUMAN){
				n_possessed++;
			}
		}
		if(n_possessed>=2) return false;
		
		return true;
	}
	public List<Agent> getFakeMediumsOfSeer(Agent seer){
		List<Agent> ret=new ArrayList<Agent>();
		for( Agent m: mediumCandidates){
			if( seerTable[seer.getAgentIdx()][m.getAgentIdx()]==Species.WEREWOLF){
				ret.add(m);
				continue;
			}
			for( int i=1; i<=playerSize;i++){
				if( seerTable[seer.getAgentIdx()][i]!=null && mediumTable[m.getAgentIdx()][i]!=null ){
					if( seerTable[seer.getAgentIdx()][i]!=mediumTable[m.getAgentIdx()][i]){
						ret.add(m);
						break;
					}
				}
			}
		}
		return ret;
	}
	
	public List<Agent> getFakeSeersOfMedium(Agent medium){
		List<Agent> ret=new ArrayList<Agent>();
		for( Agent s: seerCandidates){
			for( int i=1; i<=playerSize;i++){
				if( seerTable[s.getAgentIdx()][i]!=null && mediumTable[medium.getAgentIdx()][i]!=null){
					if( seerTable[s.getAgentIdx()][i]!=mediumTable[medium.getAgentIdx()][i]){
						ret.add(s);
					}
				}
			}
		}
		return ret;
	}
	public List<Agent> getPossessedOfSeer(Agent seer){
		//seer視点で狂人確定になっている人を返す。
		List<Agent> ret =new ArrayList<Agent>();
		if( getEnemiesOfSeer(seer)!=null){
		for( Agent enemy: getEnemiesOfSeer(seer) ){
			if( seerTable[seer.getAgentIdx()][enemy.getAgentIdx()] == Species.HUMAN){
				ret.add(enemy);
			}
		}
		}
		return ret;
	}
	public List<Agent> getMediumCandidatesOfSeer(Agent seer){
		List<Agent> ret=new ArrayList<Agent>(mediumCandidates);
		ret.removeAll(getFakeMediumsOfSeer(seer));
		return ret;
	}
	public List<Agent> getSeerCandidatesOfMedium(Agent medium){
		List<Agent> ret=new ArrayList<Agent>(seerCandidates);
		ret.removeAll(getFakeSeersOfMedium(medium));
		return ret;
	}
	public void print(){
		System.out.println("Seer: ");
		for( Agent s: seerCandidates){
			System.out.print(s.getAgentIdx()+": ");
			for( int i=1; i<=playerSize; i++){
				if( seerTable[s.getAgentIdx()][i] == null )System.out.print("-");
				else if( seerTable[s.getAgentIdx()][i] == Species.WEREWOLF )System.out.print("X");
				else if( seerTable[s.getAgentIdx()][i] == Species.HUMAN )System.out.print("O");
			}
			System.out.println();
		}
		System.out.println("Medium: ");
		for( Agent m:mediumCandidates){
			System.out.print(m.getAgentIdx()+": ");
			for( int i=1; i<=playerSize; i++){
				if( mediumTable[m.getAgentIdx()][i] == null )System.out.print("-");
				else if( mediumTable[m.getAgentIdx()][i] == Species.WEREWOLF )System.out.print("X");
				else if( mediumTable[m.getAgentIdx()][i] == Species.HUMAN )System.out.print("O");
			}
			System.out.println();
		}
		System.out.println("Enemies: ");
		for( Agent e:enemies){
			System.out.print(e.getAgentIdx()+", ");
		}
		System.out.println();
		System.out.println("Alives: " );
			for(Agent a:alivePlayers){
				System.out.print(a.getAgentIdx()+", ");
			}
		
		System.out.println();
		System.out.println("Grays: " );
			for(Agent a:grayPlayers){
				System.out.print(a.getAgentIdx()+", ");
			}
		
		System.out.println();

	}
	
}
