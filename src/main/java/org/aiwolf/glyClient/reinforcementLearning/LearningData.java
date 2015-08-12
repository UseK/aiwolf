package org.aiwolf.glyClient.reinforcementLearning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonObject;

import org.aiwolf.common.data.Role;
import org.aiwolf.glyClient.lib.PossessedFakeRoleChanger;
import org.aiwolf.glyClient.lib.WolfFakeRoleChanger;

public class LearningData implements Serializable {
	private static final long serialVersionUID = 1L;
	private int learningDataNumber; // 複数のインスタンスを別々に保管するための番号
	/** SceneのHash値とQvaluesのマップ１ */
	private int playNum = 0;

	// 学習結果として保存すべき値？
	private Map<Integer, Qvalues> sceneMap = new TreeMap<Integer, Qvalues>();
	private Map<COtimingNeo, Double> seerCO = new HashMap<COtimingNeo, Double>();
	private Map<COtimingNeo, Double> mediumCO = new HashMap<COtimingNeo, Double>();
	private Map<COtimingNeo, Double> possessedCO = new HashMap<COtimingNeo, Double>();
	private Map<COtimingNeo, Double> wolfCO = new HashMap<COtimingNeo, Double>();
	private Map<WolfFakeRoleChanger, Double> wolfFakeRoleChanger = new HashMap<WolfFakeRoleChanger, Double>();
	private Map<PossessedFakeRoleChanger, Double> possessedFakeRoleChanger = new HashMap<PossessedFakeRoleChanger, Double>();
	// 保存すべき値はここまで

	private static Map<Integer, LearningData> instance = new HashMap<Integer, LearningData>();

	private byte[] mapToByteArray(@SuppressWarnings("rawtypes") Map map) {
		ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
		ObjectOutputStream outObject;
		try {
			outObject = new ObjectOutputStream(byteOs);
			outObject.writeObject(map);
			outObject.close();
			byteOs.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return byteOs.toByteArray();
	}
	
	private JsonObject store() {
		// 保存すべき値のbyte列を取得する
		String encSceneMap = Base64.getEncoder().encodeToString(
				mapToByteArray(sceneMap));
		String encSeerCO = Base64.getEncoder().encodeToString(
				mapToByteArray(seerCO));
		String encMediumCO = Base64.getEncoder().encodeToString(
				mapToByteArray(mediumCO));
		String encPossessedCO = Base64.getEncoder().encodeToString(
				mapToByteArray(possessedCO));
		String encWolfCO = Base64.getEncoder().encodeToString(
				mapToByteArray(wolfCO));
		String encWolfFakeRoleChanger = Base64.getEncoder().encodeToString(
				mapToByteArray(wolfFakeRoleChanger));
		String encPossessedFakeRoleChanger = Base64.getEncoder()
				.encodeToString(mapToByteArray(possessedFakeRoleChanger));

		JsonObject result = Json.createObjectBuilder()
				.add("sceneMap", encSceneMap).add("seerCO", encSeerCO)
				.add("mediumCO", encMediumCO)
				.add("possessedCO", encPossessedCO).add("wolfCO", encWolfCO)
				.add("wolfFakeRoleChanger", encWolfFakeRoleChanger)
				.add("possessedFakeRoleChanger", encPossessedFakeRoleChanger)
				.build();

		return result;
	}
	
	@SuppressWarnings("unchecked")
	private boolean load(JsonObject jsonObj){
		String encSceneMap = jsonObj.getJsonString("sceneMap").getString();
		String encSeerCO = jsonObj.getJsonString("seerCO").getString();
		String encMediumCO = jsonObj.getJsonString("mediumCO").getString();
		String encPossessedCO = jsonObj.getJsonString("possessedCO").getString();
		String encWolfCO = jsonObj.getJsonString("wolfCO").getString();
		String encWolfFakeRoleChanger = jsonObj.getJsonString("wolfFakeRoleChanger").getString();
		String encPossessedFakeRoleChanger = jsonObj.getJsonString("possessedFakeRoleChanger").getString();
		
		byte[] sceneMapBytes = Base64.getDecoder().decode(encSceneMap);
		byte[] seerCOBytes = Base64.getDecoder().decode(encSeerCO);
		byte[] mediumCOBytes = Base64.getDecoder().decode(encMediumCO);
		byte[] possessedCOBytes = Base64.getDecoder().decode(encPossessedCO);
		byte[] wolfCOBytes = Base64.getDecoder().decode(encWolfCO);
		byte[] wolfFakeRoleChangerBytes = Base64.getDecoder().decode(encWolfFakeRoleChanger);
		byte[] possessedFakeRoleChangerBytes = Base64.getDecoder().decode(encPossessedFakeRoleChanger);
		
		
		ByteArrayInputStream bInputStream = null;
		ObjectInputStream inObject = null;
		try {
			// sceneMapの読み込み
			bInputStream = new ByteArrayInputStream(sceneMapBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.sceneMap = (Map<Integer, Qvalues>)inObject.readObject();
			inObject.close();
			bInputStream.close();
			
			// seerCOの読み込み
			bInputStream = new ByteArrayInputStream(seerCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.seerCO = (Map<COtimingNeo, Double>)inObject.readObject();
			inObject.close();
			bInputStream.close();
			
			// mediumCOの読み込み
			bInputStream = new ByteArrayInputStream(mediumCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.mediumCO = (Map<COtimingNeo, Double>)inObject.readObject();
			inObject.close();
			bInputStream.close();

			// possessedCOの読み込み
			bInputStream = new ByteArrayInputStream(possessedCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.possessedCO = (Map<COtimingNeo, Double>)inObject.readObject();
			inObject.close();
			bInputStream.close();
			
			// wolfCOの読み込み
			bInputStream = new ByteArrayInputStream(wolfCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.wolfCO = (Map<COtimingNeo, Double>)inObject.readObject();
			inObject.close();
			bInputStream.close();

			// wolfFakeRoleChangerの読み込み
			bInputStream = new ByteArrayInputStream(wolfFakeRoleChangerBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.wolfFakeRoleChanger = (Map<WolfFakeRoleChanger, Double>)inObject.readObject();
			inObject.close();
			bInputStream.close();
			
			// possessedFakeRoleChangerの読み込み
			bInputStream = new ByteArrayInputStream(possessedFakeRoleChangerBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.possessedFakeRoleChanger = (Map<PossessedFakeRoleChanger, Double>)inObject.readObject();
			inObject.close();
			bInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	

	public void LDStart() {
		if (playNum != 0) {
			return;
		}
		LDStart(learningDataNumber);
	}

	public void LDStart(int readDataNum) {
		if (playNum != 0) {
			return;
		}
		try {
			System.out.println("LDStart:読み込み開始  " + System.currentTimeMillis());
			int preLearningNumber = learningDataNumber;

			// File内容を一括して読み込み
			Path inputFilePath = FileSystems.getDefault().getPath(
					"LDdata_" + readDataNum + ".txt");
			List<String> lines = Files.readAllLines(inputFilePath);
			StringBuilder strBuilder = new StringBuilder();
			for( String line: lines )
				strBuilder.append(line);
			StringReader strReader = new StringReader(strBuilder.toString());
			JsonObject jsonObj = Json.createReader(strReader).readObject();
			
			this.load(jsonObj);
			
			this.learningDataNumber = preLearningNumber;
			instance.put(this.learningDataNumber, this);
			System.out.println("LDStart:読み込み終了  " + System.currentTimeMillis());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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

			String writeStr = store().toString();
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
