package org.aiwolf.glyClient.reinforcementLearning;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.aiwolf.common.data.Role;
import org.aiwolf.glyClient.lib.PossessedFakeRoleChanger;
import org.aiwolf.glyClient.lib.WolfFakeRoleChanger;

public class LearningData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 783381753058154993L;

	private int learningDataNumber; // 複数のインスタンスを別々に保管するための番号
	/** SceneのHash値とQvaluesのマップ１ */
	private int playNum = 0;

	private Map<Integer, Qvalues> sceneMap = new TreeMap<Integer, Qvalues>();

	private Map<COtimingNeo, Double> seerCO = new HashMap<COtimingNeo, Double>(),
			mediumCO = new HashMap<COtimingNeo, Double>(),
			possessedCO = new HashMap<COtimingNeo, Double>(),
			wolfCO = new HashMap<COtimingNeo, Double>();
	private Map<WolfFakeRoleChanger, Double> wolfFakeRoleChanger = new HashMap<WolfFakeRoleChanger, Double>();
	private Map<PossessedFakeRoleChanger, Double> possessedFakeRoleChanger = new HashMap<PossessedFakeRoleChanger, Double>();

	/*
	 * possessedFakeSeerCO = new HashMap<Integer, Double>(),
	 * possessedFakeMediumCO = new HashMap<Integer, Double>(), wolfFakeSeerCO =
	 * new HashMap<Integer, Double>(), wolfFakeMediumCO = new HashMap<Integer,
	 * Double>();
	 * 
	 * private Map<Role, Double> possessedFakeRole = new HashMap<Role,
	 * Double>(); private Map<WolfRolePattern, Double> wolfRolePattern = new
	 * HashMap<WolfRolePattern, Double>();
	 */

	private static Map<Integer, LearningData> instance = new HashMap<Integer, LearningData>();

	// private static LearningData instance = new LearningData();

	public void LDStart() {
		if (playNum != 0) {
			return;
		}
		try {
			// (7)FileInputStreamオブジェクトの生成
			FileInputStream inFile = new FileInputStream("LDdata_"
					+ learningDataNumber + ".txt");
			// (8)ObjectInputStreamオブジェクトの生成
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			// (9)オブジェクトの読み込み
			// System.out.println("LDStart:読み込み開始  " +
			// System.currentTimeMillis());
			LearningData ld = (LearningData) inObject.readObject();
			instance.put(ld.learningDataNumber, ld);
			// System.out.println("LDStart:読み込み終了  " +
			// System.currentTimeMillis());

			inFile.close();
			inObject.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void LDStart(int readDataNum) {
		if (playNum != 0) {
			return;
		}
		try {
			int preLearningNumber = learningDataNumber;
			// (7)FileInputStreamオブジェクトの生成
			FileInputStream inFile = new FileInputStream("LDdata_"
					+ readDataNum + ".txt");
			// (8)ObjectInputStreamオブジェクトの生成
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			// (9)オブジェクトの読み込み
			System.out.println("LDStart:読み込み開始  " + System.currentTimeMillis());
			LearningData ld = (LearningData) inObject.readObject();
			ld.learningDataNumber = preLearningNumber;
			instance.put(ld.learningDataNumber, ld);
			System.out.println("LDStart:読み込み終了  " + System.currentTimeMillis());

			inFile.close();
			inObject.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// TODO 自動生成されたメソッド・スタブ

	}

	private LearningData(int ldNumber) {
		learningDataNumber = ldNumber;

		for (int i = 0; i < 6; i++) {
			for (int v = 0; v < 2; v++)
				for (int w = 0; w < 2; w++)
					for (int f = 0; f < 2; f++)
						for (int a = 0; a < 2; a++) {
							boolean vote = v == 0 ? true : false;
							boolean wolf = w == 0 ? true : false;
							boolean found = f == 0 ? true : false;
							boolean against = a == 0 ? true : false;
							COtimingNeo cot = new COtimingNeo(i, vote, wolf,
									found, against);
							seerCO.put(cot, 0.0);
							mediumCO.put(cot, 0.0);
							possessedCO.put(cot, 0.0);
							wolfCO.put(cot, 0.0);
						}
		}

		List<Role> fakes = WolfFakeRoleChanger.getFakeroles();
		int size = fakes.size();
		for (int i = 0; i < size; i++)
			for (int w = 0; w < size; w++)
				for (int ev = 0; ev < size; ev++)
					for (int es = 0; es < size; es++)
						for (int em = 0; em < size; em++)
							for (int sc = 0; sc < size; sc++)
								for (int mc = 0; mc < size; mc++)
									for (int iv = 0; iv < size; iv++) {
										Role ini = fakes.get(i);
										Role wol = fakes.get(w);
										Role evi = fakes.get(ev);
										Role ese = fakes.get(es);
										Role eme = fakes.get(em);
										Role sco = fakes.get(sc);
										Role mco = fakes.get(mc);
										Role ivo = fakes.get(iv);
										WolfFakeRoleChanger wfrc = new WolfFakeRoleChanger();
										wfrc.setInitial(ini);
										wfrc.setExistVillagerWolf(evi);
										wfrc.setExistSeerWolf(ese);
										wfrc.setExistMediumWolf(eme);
										/*
										 * wfrc.setSeerCO(sco);
										 * wfrc.setMediumCO(mco);
										 */wolfFakeRoleChanger.put(wfrc, 0.0);
									}

		for (int i = 0; i < size; i++) {
			Role ini = fakes.get(i);
			PossessedFakeRoleChanger pfrc = new PossessedFakeRoleChanger();
			pfrc.setInitial(ini);
			possessedFakeRoleChanger.put(pfrc, 0.0);
		}
		/*
		 * Role[] roles = {Role.VILLAGER, Role.SEER, Role.MEDIUM}; for(Role r:
		 * roles){ possessedFakeRole.put(r, 0.0); } for(WolfRolePattern wrp:
		 * WolfRolePattern.values()){ wolfRolePattern.put(wrp, 0.0); }
		 */

	}

	public void LDFinish(int version) {
		try {
			int num = learningDataNumber + version * 1000;

			// 出力用のbyteOutputStreamを準備
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();

			// とりあえずObjectをそのまま出力
			// 　ObjectOutputStreamオブジェクトの生成
			ObjectOutputStream outObject = new ObjectOutputStream(byteOs);

			// LearningDataのObjectをそのまま出力
			outObject.writeObject(this);
			// オブジェクト出力ストリームのクローズ
			outObject.close();
			byteOs.close();

			// objectのbyte列をBase64エンコードする
			String writeStr = Base64.getEncoder().encodeToString(
					byteOs.toByteArray());
			// FileOutputStreamオブジェクトの生成
			FileOutputStream outFile = new FileOutputStream("LDdata_" + num
					+ ".txt");
			outFile.write(writeStr.getBytes());

			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
			// TODO: handle exception
		}
	}

	public void LDFinish() {
		this.LDFinish(learningDataNumber);
	}

	public static synchronized LearningData getInstance(int ldNum) {
		if (!instance.containsKey(ldNum)) {
			instance.put(ldNum, new LearningData(ldNum));
		}
		return instance.get(ldNum);
	}

	public Qvalues getQvalue(int hash) {
		if (sceneMap.containsKey(hash)) {
			return sceneMap.get(hash);
		} else {
			sceneMap.put(hash, new Qvalues());
			return sceneMap.get(hash);
		}
	}

	// getter and setter
	public Map<Integer, Qvalues> getSceneMap() {
		return sceneMap;
	}

	public void setSceneMap(Map<Integer, Qvalues> sceneMap) {
		this.sceneMap = sceneMap;
	}

	/*
	 * public Map<Integer, Double> getPossessedFakeSeerCO() { return
	 * possessedFakeSeerCO; }
	 * 
	 * public void setPossessedFakeSeerCO(Map<Integer, Double>
	 * possessedFakeSeerCO) { this.possessedFakeSeerCO = possessedFakeSeerCO; }
	 * 
	 * public Map<Integer, Double> getPossessedFakeMediumCO() { return
	 * possessedFakeMediumCO; }
	 * 
	 * public void setPossessedFakeMediumCO(Map<Integer, Double>
	 * possessedFakeMediumCO) { this.possessedFakeMediumCO =
	 * possessedFakeMediumCO; }
	 * 
	 * public Map<Integer, Double> getWolfFakeSeerCO() { return wolfFakeSeerCO;
	 * }
	 * 
	 * public void setWolfFakeSeerCO(Map<Integer, Double> wolfFakeSeerCO) {
	 * this.wolfFakeSeerCO = wolfFakeSeerCO; }
	 * 
	 * public Map<Integer, Double> getWolfFakeMediumCO() { return
	 * wolfFakeMediumCO; }
	 * 
	 * public void setWolfFakeMediumCO(Map<Integer, Double> wolfFakeMediumCO) {
	 * this.wolfFakeMediumCO = wolfFakeMediumCO; }
	 */
	public int getLearningDataNumber() {
		return learningDataNumber;
	}

	public int getPlayNum() {
		return playNum;
	}

	public void setPlayNum(int playNum) {
		this.playNum = playNum;
	}

	public Map<COtimingNeo, Double> getSeerCO() {
		return seerCO;
	}

	public Map<COtimingNeo, Double> getMediumCO() {
		return mediumCO;
	}

	public Map<COtimingNeo, Double> getPossessedCO() {
		return possessedCO;
	}

	public Map<COtimingNeo, Double> getWolfCO() {
		return wolfCO;
	}

	public Map<WolfFakeRoleChanger, Double> getWolfFakeRoleChanger() {
		return wolfFakeRoleChanger;
	}

	public Map<PossessedFakeRoleChanger, Double> getPossessedFakeRoleChanger() {
		return possessedFakeRoleChanger;
	}

	public static Map<Integer, LearningData> getInstance() {
		return instance;
	}

}
