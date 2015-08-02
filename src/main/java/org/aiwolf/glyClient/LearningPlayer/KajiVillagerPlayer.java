package org.aiwolf.glyClient.LearningPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.glyClient.LearningPlayer.AbstractKajiBasePlayer;
import org.aiwolf.glyClient.lib.Pattern;

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
