package agent.aiwolf.yao;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class YaoGameInfo {
	// ���ɂƂ��ċq�ϓI�Ɋm�肵�Ă�����݂̂��i�[����N���X�B
	
	private int playerSize; //�v���C���[��
	
	private List<Agent> allPlayers=new ArrayList<Agent>();
	private List<Agent> alivePlayers=new ArrayList<Agent>(); // �������Ă���v���C���[���X�g
	private List<Agent> attackedPlayers=new ArrayList<Agent>(); // �P�����ꂽ�v���C���[���X�g
	private List<Agent> executedPlayers=new ArrayList<Agent>(); // ���Y�����v���C���[���X�g
	private List<Agent> grayPlayers=new ArrayList<Agent>();	// ���S�O���[�̃��X�g
	
	private Role claimTable[][]; //�e�l���e�l���ǂ̖�E���Ǝv���Ă���̂��̃e�[�u��
	private List<Agent> seerCandidates = new ArrayList<Agent>();    // �肢��⃊�X�g
	private List<Agent> seerCOs=new ArrayList<Agent>(); // �肢CO�������l�̃��X�g
	private Species seerTable[][]; //�肢���ʂ��i�[����e�[�u��
	private List<Agent> mediumCandidates = new ArrayList<Agent>();  // ��\��⃊�X�g
	private List<Agent> mediumCOs=new ArrayList<Agent>(); // ��\CO�������l�̃��X�g
	private Species mediumTable[][];//��\���ʂ��i�[����e�[�u��
	private List<Agent> guardCandidates = new ArrayList<Agent>();   // ��l��⃊�X�g
	private List<Agent> enemies = new ArrayList<Agent>();		    // 100% �l�T�w�c�Ɣ��������l�̃��X�g

	private VoteClaimHistory voteHistory;							// �e�l�̓��[�咣�̗���
	
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
			//������E�x��͖������ɓG�Ƃ݂Ȃ�
			enemies.add(target);
		}
	}

	
	public int getMaxWolves(Agent seer){
		//seer���_�̘T�ő吔�𐔂���
		//�肢���ʂ��T�̐l�̎��S�Ґ��𐔂���
		int n_wolves=3;
		for( Agent a: allPlayers){
			if( !getEnemiesOfSeer(seer).contains(a) ){
				if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]==Species.WEREWOLF){
					n_wolves--;
				}
			}
		}
		//�����p�������ŉ\�ȘT�̐��̍ő�l
		n_wolves=Math.min(n_wolves,(alivePlayers.size()-1)/2);

		//�肢���_�œG�Ɗm�肵�Ă���l�̒��Ŏ��S�Ґ�����v�Z����B
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
		//Agent���_�� p�����Y���Ă��l�܂Ȃ����ǂ����𔻒肷��B
		if( enemies.contains(seer) ) return true;
		int n_remainWolves=getMaxWolves(seer);
		//p���T�ł��邩�A2�l�ڈȏ�̓G�ł���΃Z�[�t�ł���B(�l���M���M������̋��l�݂�ɂ��ς݂́c�l���Ȃ�)
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
		//seer���_��p���T�ł���\����Ԃ�
		//�����͋q�ϓI�ɑ���Ȃ��Ƃ������Ƃ���ŉ������̂ł��܂肱�̃N���X�ɂ���͓̂K�؂Ŗ�����������Ȃ��B
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
				if( linePossible(seer,p) ){	return 25.0;} //���C���͂Ȃ����Ă�̂�
				else return 50.0; //��\�R�ȏ�̓������Ă�͂��Ȃ̂łƂ肠�������X�U���炢�Ō��Ă�����
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
			//������E�x��͖������ɓG�Ƃ݂Ȃ�
			enemies.add(target);
		}
	}
	
	public void checkContradiction(){
		//�j�]�`�F�b�N�B
		//�������u�P����ɔ����o�����j�]�v�͊܂܂Ȃ�attackedUpdate�ŏ������Ă�B
		//�����ł̃`�F�b�N�͎�ɐl���̒��K������Ȃ��j�]�ł���B
		//TODO:��l�QCO�ȏ�ɐ����Ή��o���Ă��Ȃ��B
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
		//seer���j�]���Ă��Ȃ����`�F�b�N����
		if(impliedDevineUpdate(seer)==false) return true;
		return false;
	}
	public boolean impliedDevineUpdate(Agent seer){
		//�󋵓I�ɐ肢���_�Ŕ����͂����肵�Ă���Ƃ����⊮����B���������true�A�����������false��Ԃ��B
		//case1. �G�̒��ŋ��l����l�͂����肵�Ă���ꍇ�A����ӊO�̓G�͘T�m��
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
		//case2. �T���S�I�悵�Ă���ꍇ�A����ӊO�̐l�͑��m��(��ʓI�ȏꍇ)
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
		//�@case3. �����Ă���T��������M���M���ł������ꍇ�A����ӊO�̐����Ă�͑��m��
		if( n_aliveWolves>=(alivePlayers.size()-1)/2){
			for( Agent a: alivePlayers){
				if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]!= Species.WEREWOLF){
					seerTable[seer.getAgentIdx()][a.getAgentIdx()]=Species.HUMAN;
				}
			}
		}
		// case4. �����Ă���G��������M���M���ł������ꍇ�A����ӊO�̐����Ă���l�͑��m��
		if( n_aliveEnemies>=(alivePlayers.size()+1)/2){
			for( Agent a: alivePlayers){
				if( seerTable[seer.getAgentIdx()][a.getAgentIdx()]!= Species.WEREWOLF){
					seerTable[seer.getAgentIdx()][a.getAgentIdx()]=Species.HUMAN;
				}
			}
		}
		// case5. �����Ă���l�̒��Ő���Ă��Ȃ��l����l�����c���Ă��Ȃ��ꍇ�A���̐l�͘T�m��
		List<Agent> aliveGrays=getAliveGraysOfSeer(seer);
		if( aliveGrays.size() == 0 ) return false;
		if( aliveGrays.size() == 1 ){
			seerTable[seer.getAgentIdx()][aliveGrays.get(0).getAgentIdx()]=Species.HUMAN;
		}
		return true;
	}
	public boolean impliedInquestedUpdate(Agent medium){
		//�󋵓I�ɗ�\���_�Ŕ����͂����肵�Ă���Ƃ����⊮����B���������true�A�����������false��Ԃ��B
		//�Ƃ����֐���p�ӂ������A�肢�ƈႢ�����͕⊮�o����Ƃ��낪���܂�Ȃ������ł���B
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
		// medium���j�]���Ă��Ȃ����ǂ������`�F�b�N����
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
		
		// �P����ɘT���o�����肢�͔j�]�B
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
		//medium����\���������̓G�m�胁���o��Ԃ��Bmedium���q�ϓI�ɂ݂ēG�ł���ꍇ��null��Ԃ��B
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
		//seer���肢���������̓G�m�胁���o��Ԃ��Bseer���q�ϓI�ɂ݂ēG�ł���ꍇ��null��Ԃ��B
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
		//seer���肢���������̐������Ă���G�m�胁���o��Ԃ��B
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
		// s-m���C�������������邩��Ԃ�
		if( enemies.contains(s) || enemies.contains(m)) return false; //���������ǂ��������j�]
		// ���ʂ��H������Ă���
		for( int i=1; i<=playerSize; i++ ){
			if( seerTable[s.getAgentIdx()][i] != null && mediumTable[m.getAgentIdx()][i]!=null){
				if( seerTable[s.getAgentIdx()][i]!=mediumTable[m.getAgentIdx()][i]){
					return false;
				}
			}
		}
		// seer-m���C���ł̐l�T�̐��ɖ���������
		List<Agent> lineEnemy=getEnemiesOfSeer(s);
		List<Agent> tmpEnemy=getEnemiesOfMedium(m);
		if( lineEnemy == null )lineEnemy = new ArrayList<Agent>();
		if( tmpEnemy == null )tmpEnemy = new ArrayList<Agent>();

		for( Agent a: tmpEnemy) if(!lineEnemy.contains(a) ) lineEnemy.add(a);
		if(lineEnemy.size() >= 5) return false;
		
		// seer���猩�����ɋ��l��2�l�ȏア��
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
		//seer���_�ŋ��l�m��ɂȂ��Ă���l��Ԃ��B
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
