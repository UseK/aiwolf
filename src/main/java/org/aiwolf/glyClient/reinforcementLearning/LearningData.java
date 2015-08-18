package org.aiwolf.glyClient.reinforcementLearning;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.json.Json;
import javax.json.JsonObject;

import org.aiwolf.common.data.Role;
import org.aiwolf.glyClient.lib.PossessedFakeRoleChanger;
import org.aiwolf.glyClient.lib.WolfFakeRoleChanger;

public class LearningData implements Serializable {
	private static final long serialVersionUID = 1L;
	private int learningDataNumber; // 複数のインスタンスを別々に保管するための番号
	/** SceneのHash値とQvaluesのマップ */
	private int playNum = 0;

	// 学習結果として保存すべき値？
	// 村から取得すべき？
	private Map<Integer, Qvalues> sceneMap = new TreeMap<Integer, Qvalues>();
	// 占いから取得
	private Map<COtimingNeo, Double> seerCO = new HashMap<COtimingNeo, Double>();
	// 霊能から取得
	private Map<COtimingNeo, Double> mediumCO = new HashMap<COtimingNeo, Double>();
	// 狂人から取得
	private Map<COtimingNeo, Double> possessedCO = new HashMap<COtimingNeo, Double>();
	// 人狼から取得
	private Map<COtimingNeo, Double> wolfCO = new HashMap<COtimingNeo, Double>();
	private Map<WolfFakeRoleChanger, Double> wolfFakeRoleChanger = new HashMap<WolfFakeRoleChanger, Double>();
	// 狂人から取得
	private Map<PossessedFakeRoleChanger, Double> possessedFakeRoleChanger = new HashMap<PossessedFakeRoleChanger, Double>();
	// 保存すべき値はここまで

	private static Map<Integer, LearningData> instance;

	static {
		// ここで最終的なインスタンスを格納するhashmapを初期化する
		instance = new HashMap<Integer, LearningData>();

		// インスタンスの準備
		// 素材の各インスタンス
		LearningData seerData = new LearningData(0);
		LearningData mediumData = new LearningData(0);
		LearningData bodyguardData = new LearningData(0);
		LearningData possessedData = new LearningData(0);
		LearningData werewolfData = new LearningData(0);
		LearningData villeagerData = new LearningData(0);
		// 統合先のインスタンス
		LearningData defaultData = new LearningData(0);

		URL seerFile = LearningData.class.getResource("seer_0.zip");
		URL mediumFile = LearningData.class.getResource("medium_0.zip");
		URL bodyguardFile = LearningData.class.getResource("bodyguard_0.zip");
		URL possessedFile = LearningData.class.getResource("possessed_0.zip");
		URL werewolfFile = LearningData.class.getResource("werewolf_0.zip");
		URL villeagerFile = LearningData.class.getResource("villeager_0.zip");

		try {
			
			// 占いの読み込み
			seerFile.getContent();
			JsonObject seerJson = loadJson(seerFile.openStream());
			seerData.load(seerJson);
			System.err.println("Seer: sceneMapSize: "
					+ seerData.getSceneMap().size());
			System.err.println("Seer: seerCoSize: "
					+ seerData.getSeerCO().size());
			System.err.println("Seer: mediumCoize: "
					+ seerData.getMediumCO().size());
			System.err.println("Seer: possessedCoSize: "
					+ seerData.getPossessedCO().size());
			System.err.println("Seer: wolfCoSize: "
					+ seerData.getWolfCO().size());
			System.err.println("Seer: wolfFakeRoleChangerSize: "
					+ seerData.getWolfFakeRoleChanger().size());
			System.err.println("Seer: possessedFakeRoleChangerSize: "
					+ seerData.getPossessedFakeRoleChanger().size());
			defaultData.seerCO = seerData.getSeerCO();
			

			// 霊能の読み込み
			mediumFile.getContent();
			JsonObject mediumJson = loadJson(mediumFile.openStream());
			mediumData.load(mediumJson);
			
			System.err.println("Medium: sceneMapSize: "
					+ mediumData.getSceneMap().size());
			System.err.println("Medium: seerCoSize: "
					+ mediumData.getSeerCO().size());
			System.err.println("Medium: mediumCoize: "
					+ mediumData.getMediumCO().size());
			System.err.println("Medium: possessedCoSize: "
					+ mediumData.getPossessedCO().size());
			System.err.println("Medium: wolfCoSize: "
					+ mediumData.getWolfCO().size());
			System.err.println("Medium: wolfFakeRoleChangerSize: "
					+ mediumData.getWolfFakeRoleChanger().size());
			System.err.println("Medium: possessedFakeRoleChangerSize: "
					+ mediumData.getPossessedFakeRoleChanger().size());
			defaultData.mediumCO = mediumData.getMediumCO();

			/*
			// 狩人の読み込み
			bodyguardFile.getContent();
			JsonObject bodyguardJson = loadJson(bodyguardFile.openStream());
			bodyguardData.load(bodyguardJson);
			System.err.println("Bodyguard: sceneMapSize: "
					+ bodyguardData.getSceneMap().size());
			System.err.println("Bodyguard: seerCoSize: "
					+ bodyguardData.getSeerCO().size());
			System.err.println("Bodyguard: mediumCoize: "
					+ bodyguardData.getMediumCO().size());
			System.err.println("Bodyguard: possessedCoSize: "
					+ bodyguardData.getPossessedCO().size());
			System.err.println("Bodyguard: wolfCoSize: "
					+ bodyguardData.getWolfCO().size());
			System.err.println("Bodyguard: wolfFakeRoleChangerSize: "
					+ bodyguardData.getWolfFakeRoleChanger().size());
			System.err.println("Bodyguard: possessedFakeRoleChangerSize: "
					+ bodyguardData.getPossessedFakeRoleChanger().size());
*/
			// 狂人の読み込み
			possessedFile.getContent();
			JsonObject possessedJson = loadJson(possessedFile.openStream());
			possessedData.load(possessedJson);
			System.err.println("Possessed: sceneMapSize: "
					+ possessedData.getSceneMap().size());
			System.err.println("Possessed: seerCoSize: "
					+ possessedData.getSeerCO().size());
			System.err.println("Possessed: mediumCoize: "
					+ possessedData.getMediumCO().size());
			System.err.println("Possessed: possessedCoSize: "
					+ possessedData.getPossessedCO().size());
			System.err.println("Possessed: wolfCoSize: "
					+ possessedData.getWolfCO().size());
			System.err.println("Possessed: wolfFakeRoleChangerSize: "
					+ possessedData.getWolfFakeRoleChanger().size());
			System.err.println("Possessed: possessedFakeRoleChangerSize: "
					+ possessedData.getPossessedFakeRoleChanger().size());
			defaultData.possessedCO = possessedData.getPossessedCO();
			defaultData.possessedFakeRoleChanger = possessedData
					.getPossessedFakeRoleChanger();

			// 人狼の読み込み
			werewolfFile.getContent();
			JsonObject werewolfJson = loadJson(werewolfFile.openStream());
			werewolfData.load(werewolfJson);
			System.err.println("Werewolf: sceneMapSize: "
					+ werewolfData.getSceneMap().size());
			System.err.println("Werewolf: seerCoSize: "
					+ werewolfData.getSeerCO().size());
			System.err.println("Werewolf: mediumCoize: "
					+ werewolfData.getMediumCO().size());
			System.err.println("Werewolf: werewolfCoSize: "
					+ werewolfData.getPossessedCO().size());
			System.err.println("Werewolf: wolfCoSize: "
					+ werewolfData.getWolfCO().size());
			System.err.println("Werewolf: wolfFakeRoleChangerSize: "
					+ werewolfData.getWolfFakeRoleChanger().size());
			System.err.println("Werewolf: werewolfFakeRoleChangerSize: "
					+ werewolfData.getPossessedFakeRoleChanger().size());
			defaultData.wolfCO = werewolfData.getWolfCO();
			defaultData.wolfFakeRoleChanger = werewolfData
					.getWolfFakeRoleChanger();

			// 村人の読み込み
			villeagerFile.getContent();
			JsonObject villeagerJson = loadJson(villeagerFile.openStream());
			villeagerData.load(villeagerJson);
			System.err.println("Villager: sceneMapSize: "
					+ villeagerData.getSceneMap().size());
			System.err.println("Villager: seerCoSize: "
					+ villeagerData.getSeerCO().size());
			System.err.println("Villager: mediumCoize: "
					+ villeagerData.getMediumCO().size());
			System.err.println("Villager: werewolfCoSize: "
					+ villeagerData.getPossessedCO().size());
			System.err.println("Villager: wolfCoSize: "
					+ villeagerData.getWolfCO().size());
			System.err.println("Villager: wolfFakeRoleChangerSize: "
					+ villeagerData.getWolfFakeRoleChanger().size());
			System.err.println("Villager: werewolfFakeRoleChangerSize: "
					+ villeagerData.getPossessedFakeRoleChanger().size());
			defaultData.sceneMap = villeagerData.getSceneMap();

			// データの統合
			instance.put(0, defaultData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static synchronized JsonObject loadJson(InputStream inputStream)
			throws IOException {
		final int READ_BUF_SIZE = 45000000;
		byte[] readBuf = new byte[READ_BUF_SIZE];
		ZipInputStream zipInputStream = new ZipInputStream(
				new BufferedInputStream(inputStream));
		@SuppressWarnings("unused")
		ZipEntry entry = zipInputStream.getNextEntry();
		// ByteArrayOutputStream outStream = new ByteArrayOutputStream(100000000);
		int readed = 0;

		for (;;) {
			int readSize = zipInputStream.read(readBuf, readed, READ_BUF_SIZE-readed);
			if (readSize < 0)
				break;
			readed += readSize;
			// System.out.println("readed: " + readed);
			// outStream.write(readBuf, 0, readSize);
		}
		System.out.println("readSize: " + readed);
		// outStream.flush();
		// outStream.close();
		zipInputStream.close();

		// ASCII文字列しか無いはずなので，UTF-8でも問題無いはず．
		
		InputStream inStream = new ByteArrayInputStream(readBuf, 0, readed);
		JsonObject jsonObj = Json.createReader(inStream).readObject();
		inStream.close();
		
		return jsonObj;
	}

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
	private boolean load(JsonObject jsonObj) {
		String encSceneMap = jsonObj.getJsonString("sceneMap").getString();
		String encSeerCO = jsonObj.getJsonString("seerCO").getString();
		String encMediumCO = jsonObj.getJsonString("mediumCO").getString();
		String encPossessedCO = jsonObj.getJsonString("possessedCO")
				.getString();
		String encWolfCO = jsonObj.getJsonString("wolfCO").getString();
		String encWolfFakeRoleChanger = jsonObj.getJsonString(
				"wolfFakeRoleChanger").getString();
		String encPossessedFakeRoleChanger = jsonObj.getJsonString(
				"possessedFakeRoleChanger").getString();

		byte[] sceneMapBytes = Base64.getDecoder().decode(encSceneMap);
		byte[] seerCOBytes = Base64.getDecoder().decode(encSeerCO);
		byte[] mediumCOBytes = Base64.getDecoder().decode(encMediumCO);
		byte[] possessedCOBytes = Base64.getDecoder().decode(encPossessedCO);
		byte[] wolfCOBytes = Base64.getDecoder().decode(encWolfCO);
		byte[] wolfFakeRoleChangerBytes = Base64.getDecoder().decode(
				encWolfFakeRoleChanger);
		byte[] possessedFakeRoleChangerBytes = Base64.getDecoder().decode(
				encPossessedFakeRoleChanger);

		ByteArrayInputStream bInputStream = null;
		ObjectInputStream inObject = null;
		try {
			// sceneMapの読み込み
			bInputStream = new ByteArrayInputStream(sceneMapBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.sceneMap = (Map<Integer, Qvalues>) inObject.readObject();
			inObject.close();
			bInputStream.close();

			// seerCOの読み込み
			bInputStream = new ByteArrayInputStream(seerCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.seerCO = (Map<COtimingNeo, Double>) inObject.readObject();
			inObject.close();
			bInputStream.close();

			// mediumCOの読み込み
			bInputStream = new ByteArrayInputStream(mediumCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.mediumCO = (Map<COtimingNeo, Double>) inObject.readObject();
			inObject.close();
			bInputStream.close();

			// possessedCOの読み込み
			bInputStream = new ByteArrayInputStream(possessedCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.possessedCO = (Map<COtimingNeo, Double>) inObject.readObject();
			inObject.close();
			bInputStream.close();

			// wolfCOの読み込み
			bInputStream = new ByteArrayInputStream(wolfCOBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.wolfCO = (Map<COtimingNeo, Double>) inObject.readObject();
			inObject.close();
			bInputStream.close();

			// wolfFakeRoleChangerの読み込み
			bInputStream = new ByteArrayInputStream(wolfFakeRoleChangerBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.wolfFakeRoleChanger = (Map<WolfFakeRoleChanger, Double>) inObject
					.readObject();
			inObject.close();
			bInputStream.close();

			// possessedFakeRoleChangerの読み込み
			bInputStream = new ByteArrayInputStream(
					possessedFakeRoleChangerBytes);
			inObject = new ObjectInputStream(bInputStream);
			this.possessedFakeRoleChanger = (Map<PossessedFakeRoleChanger, Double>) inObject
					.readObject();
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
			System.out.println("LDStart: ID: " + readDataNum + ", 読み込み開始: "
					+ System.currentTimeMillis());
			int preLearningNumber = learningDataNumber;

			final int READ_BUF_SIZE = 1048676;
			byte[] readBuf = new byte[READ_BUF_SIZE];
			String baseFileName = "LDdata_" + readDataNum;
			File zipFile = new File(baseFileName + ".zip");
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(new FileInputStream(zipFile)));
			@SuppressWarnings("unused")
			ZipEntry entry = zipInputStream.getNextEntry();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();

			for (;;) {
				int readSize = zipInputStream.read(readBuf, 0, READ_BUF_SIZE);
				if (readSize < 0)
					break;
				outStream.write(readBuf, 0, readSize);
			}
			outStream.flush();
			outStream.close();
			zipInputStream.close();

			// ASCII文字列しか無いはずなので，UTF-8でも問題無いはず．
			String jsonStr = new String(outStream.toByteArray(), "UTF-8");

			StringReader strReader = new StringReader(jsonStr);
			JsonObject jsonObj = Json.createReader(strReader).readObject();

			this.load(jsonObj);

			this.learningDataNumber = preLearningNumber;
			instance.put(this.learningDataNumber, this);
			System.out.println("LDStart: ID: " + readDataNum + ", 読み込み終了: "
					+ System.currentTimeMillis());

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

	public void LDFinish(int learningDataNumber) {
		try {
			// int num = learningDataNumber + version * 1000;

			String writeStr = store().toString();
			// 出力ファイルのベース名
			String baseFileName = "LDdata_" + learningDataNumber;
			// FileOutputStreamオブジェクトの生成
			ZipOutputStream outFile = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(baseFileName
							+ ".zip")));
			final ZipEntry entry = new ZipEntry(baseFileName + ".dat");
			outFile.putNextEntry(entry);
			outFile.write(writeStr.getBytes());
			outFile.closeEntry();
			outFile.finish();

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