package com.fs.starfarer.api.impl.campaign;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;

/**
 *
 */
public class TAS_AICoreOfficerPluginImpl extends AICoreOfficerPluginImpl {
	/**
	 * Extra points added to deployment recovery cost for computing the effect of
	 * the "Automated Ships" skill.
	 */
	public static float OMEGA_POINTS = 140f;
	public static float ALPHA_POINTS = 70f;
	public static float BETA_POINTS = 50f;
	public static float GAMMA_POINTS = 30f;

	static {
		if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
			OMEGA_POINTS = 1f;
			ALPHA_POINTS = 0.75f;
			BETA_POINTS = 0.5f;
			GAMMA_POINTS = 0.25f;
		}
	}

	@Override
	public PersonAPI createPerson(String aiCoreId, String factionId, Random random) {
		if (random == null) {
			random = new Random();
		}

		PersonAPI person = Global.getFactory().createPerson();
		person.setFaction(factionId);
		person.setAICoreId(aiCoreId);

		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(aiCoreId);

		person.getStats().setSkipRefresh(true);

		person.setName(new FullName(spec.getName(), "", Gender.ANY));
		float points = 0f;
		switch (aiCoreId) {
			case "omega_core": // assume it's not going to be integrated, no reason to do it - same as assuming
								// it's always integrated
				person.setPortraitSprite("graphics/portraits/characters/omega.png");
				person.getStats().setLevel(9);
				person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
				person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
				person.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 2);
				// person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
				person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
				person.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 2);
				person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
				person.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
				person.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
				person.getStats().setSkillLevel(Skills.OMEGA_ECM, 2);
				points = OMEGA_POINTS;
				break;
			case "alpha_core":
				person.setPortraitSprite("graphics/portraits/portrait_ai2b.png");
				person.getStats().setLevel(7);
				person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
				person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
				person.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 2);
				// person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
				person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
				person.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 2);
				person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
				points = ALPHA_POINTS;
				break;
			case "beta_core":
				person.setPortraitSprite("graphics/portraits/portrait_ai3b.png");
				person.getStats().setLevel(5);
				person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
				person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
				person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
				person.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 2);
				points = BETA_POINTS;
				break;
			case "gamma_core":
				person.setPortraitSprite("graphics/portraits/portrait_ai1b.png");
				person.getStats().setLevel(3);
				person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
				person.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 2);
				points = GAMMA_POINTS;
		}

		if (points != 0) {
			person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_VALUE, points);
		}

		person.setPersonality(Personalities.RECKLESS);
		person.setRankId(Ranks.SPACE_CAPTAIN);
		person.setPostId(null);

		person.getStats().setSkipRefresh(false);

		return person;
	}

}
