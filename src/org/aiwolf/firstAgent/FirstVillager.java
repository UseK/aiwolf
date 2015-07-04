package org.aiwolf.firstAgent;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.print.attribute.standard.PrinterLocation;

import org.aiwolf.client.base.player.AbstractVillager;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;

public class FirstVillager extends AbstractVillager {

	int readTalkNum = 0;
	List<Agent> divinedWhiteList = new ArrayList<Agent>();
	GameInfo gameInfo;
	List<List<Vote>> votesEachDay = new ArrayList<>();
	VillageSideThought thought;



	@Override
	public void initialize(GameInfo gameInfo,
			org.aiwolf.common.net.GameSetting gameSetting) {
		this.gameInfo = gameInfo;
		thought = new VillageSideThought(gameInfo);
	}

	@Override
	public void dayStart() {
		readTalkNum = 0;
		thought.removeDeadAgent(gameInfo);
		votesEachDay.add(gameInfo.getVoteList());
		thought.responseVote(gameInfo);
	}

	@Override
	public void update(org.aiwolf.common.net.GameInfo gameInfo) {
		super.update(gameInfo);
		this.gameInfo = gameInfo;
		List<Talk> talkList = gameInfo.getTalkList();
		for (int i = readTalkNum; i < talkList.size(); i++) {
			Talk talk = talkList.get(i);
			Utterance utterance = new Utterance(talk.getContent());
			switch (utterance.getTopic()) {
			case AGREE:
				break;
			case ATTACK:
				break;
			case COMINGOUT:
				thought.responseComingout(utterance, talk);
				break;
			case DISAGREE:
				break;
			case DIVINED:
				thought.responseDivination(utterance, talk);
				break;
			case ESTIMATE:
				break;
			case GUARDED:
				break;
			case INQUESTED:
				break;
			case SKIP:
				break;
			case VOTE:
				break;
			default:
				break;
			}
			readTalkNum++;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public String talk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent vote() {
		return thought.getAgentToVote();
	}
}
