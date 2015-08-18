package org.aiwolf.glyClient.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.aiwolf.common.data.Role;

/**
 * changersに色んな状況下でどの役職を騙るかのデータを保存しておく
 * @author kajiwarakengo
 *
 */
public class WolfFakeRoleChanger implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final List<Role> fakeRoles = new ArrayList<Role>(){
		private static final long serialVersionUID = 1L;

		{
			add(Role.VILLAGER);
			add(Role.SEER);
			add(Role.MEDIUM);
		}
	};

	private Role initial = Role.VILLAGER; //最初に設定しておく役職
	private Role existVillagerWolf = Role.VILLAGER; //相方の人狼が村人を騙るといった時に騙る役職
	private Role existSeerWolf = Role.VILLAGER;
	private Role existMediumWolf = Role.VILLAGER;

	public WolfFakeRoleChanger() {
	}
	
	/**
	 * randomにchangerを取得する
	 * @return
	 */
	public static WolfFakeRoleChanger getRandomChanger(){
		WolfFakeRoleChanger newChanger = new WolfFakeRoleChanger();
		newChanger.initial = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		newChanger.existVillagerWolf = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		newChanger.existSeerWolf = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		newChanger.existMediumWolf = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
		return newChanger;
	}
	
	public Role getInitial() {
		return initial;
	}

	public void setInitial(Role initial) {
		this.initial = initial;
	}


	public Role getExistVillagerWolf() {
		return existVillagerWolf;
	}

	public void setExistVillagerWolf(Role existVillagerWolf) {
		this.existVillagerWolf = existVillagerWolf;
	}

	public Role getExistSeerWolf() {
		return existSeerWolf;
	}

	public void setExistSeerWolf(Role existSeerWolf) {
		this.existSeerWolf = existSeerWolf;
	}

	public Role getExistMediumWolf() {
		return existMediumWolf;
	}

	public void setExistMediumWolf(Role existMediumWolf) {
		this.existMediumWolf = existMediumWolf;
	}

	public static List<Role> getFakeroles() {
		return fakeRoles;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((existMediumWolf == null) ? 0 : existMediumWolf.hashCode());
		result = prime * result
				+ ((existSeerWolf == null) ? 0 : existSeerWolf.hashCode());
		result = prime
				* result
				+ ((existVillagerWolf == null) ? 0 : existVillagerWolf
						.hashCode());
		result = prime * result + ((initial == null) ? 0 : initial.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WolfFakeRoleChanger other = (WolfFakeRoleChanger) obj;
		if (existMediumWolf != other.existMediumWolf)
			return false;
		if (existSeerWolf != other.existSeerWolf)
			return false;
		if (existVillagerWolf != other.existVillagerWolf)
			return false;
		if (initial != other.initial)
			return false;
		return true;
	}
}