package org.aiwolf.glyClient.LearningPlayer;


public class VillagerPlayer extends AbstractBasePlayer {
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
