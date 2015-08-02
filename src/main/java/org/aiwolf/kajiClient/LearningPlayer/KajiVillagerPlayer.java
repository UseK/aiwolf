package org.aiwolf.kajiClient.LearningPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.kajiClient.lib.Pattern;
import org.aiwolf.kajiClient.LearningPlayer.AbstractKajiBasePlayer;

public class KajiVillagerPlayer extends AbstractKajiBasePlayer {



	@Override
	public void setVoteTarget() {
		setVoteTargetTemplate(myPatterns);
	}

	@Override
	public String getJudgeText() {
		return null;
	}

	@Override
	public String getComingoutText() {
		return null;
	}



}
