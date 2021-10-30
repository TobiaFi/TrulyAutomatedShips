package com.fs.starfarer.api.impl.campaign.skills;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.DescriptionSkillEffect;
import com.fs.starfarer.api.characters.FleetTotalItem;
import com.fs.starfarer.api.characters.FleetTotalSource;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.TAS_AICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TAS_AutomatedShips extends AutomatedShips {
	public static class Level0 implements DescriptionSkillEffect {
		@Override
		public String getString() {
			float alpha = TAS_AICoreOfficerPluginImpl.ALPHA_POINTS;
			float beta = TAS_AICoreOfficerPluginImpl.BETA_POINTS;
			float gamma = TAS_AICoreOfficerPluginImpl.GAMMA_POINTS;
			if (BaseSkillEffectDescription.USE_RECOVERY_COST)
				return "*The total \"automated ship points\" are equal to the recovery cost of "
						+ "all automated ships in the fleet. Installing an AI core on a ship will reduce its point total by "
						+ getHighlights()[0] + " for an Alpha Core, " + getHighlights()[1] + " for a Beta Core, and "
						+ getHighlights()[2] + " for a Gamma Core."
						+ "\nA ship with an AI core installed will have its monthly maintenance cost increased depending on hull size and core type.";
			else
				return "*The total \"automated ship points\" are equal to the ordnance points of "
						+ "all automated ships in the fleet, plus extra points for AI cores installed on any of the "
						+ "automated ships - " + alpha + " for an Alpha Core, " + beta + " for a Beta Core, and "
						+ gamma + " for a Gamma Core.";
		}

		@Override
		public Color[] getHighlightColors() {
			Color h = Misc.getHighlightColor();
			h = Misc.getDarkHighlightColor();
			return new Color[]{h, h, h};
		}

		@Override
		public String[] getHighlights() {
			if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
				int alpha = (int) (TAS_AICoreOfficerPluginImpl.ALPHA_POINTS * 100);
				int beta = (int) (TAS_AICoreOfficerPluginImpl.BETA_POINTS * 100);
				int gamma = (int) (TAS_AICoreOfficerPluginImpl.GAMMA_POINTS * 100);
				return new String[]{"" + alpha + "%", "" + beta + "%", "" + gamma + "%"};
			} else {
				int alpha = (int) TAS_AICoreOfficerPluginImpl.ALPHA_POINTS;
				int beta = (int) TAS_AICoreOfficerPluginImpl.BETA_POINTS;
				int gamma = (int) TAS_AICoreOfficerPluginImpl.GAMMA_POINTS;
				return new String[]{"" + alpha, "" + beta, "" + gamma};
			}

		}

		@Override
		public Color getTextColor() {
			return null;
		}
	}

	public static class Level1 extends TAS_BaseSkillEffectDescription implements ShipSkillEffect, FleetTotalSource {
		@Override
		public FleetTotalItem getFleetTotalItem() {
			return getAutomatedPointsTotal();
		}

		@Override
		public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
			if (Misc.isAutomated(stats)) {
				float crBonus = computeAndCacheThresholdBonus(stats, "auto_cr", MAX_CR_BONUS,
						ThresholdBonusType.AUTOMATED_POINTS);
				SkillSpecAPI skill = Global.getSettings().getSkillSpec(Skills.AUTOMATED_SHIPS);
				stats.getMaxCombatReadiness().modifyFlat(id, crBonus * 0.01f, skill.getName() + " skill");
			}
		}

		@Override
		public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
			stats.getMaxCombatReadiness().unmodifyFlat(id);
		}

		@Override
		public String getEffectDescription(float level) {
			return null;
		}

		@Override
		public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill, TooltipMakerAPI info,
				float width) {
			init(stats, skill);

			FleetDataAPI data = getFleetData(null);
			float crBonus = computeAndCacheThresholdBonus(data, stats, "auto_cr", MAX_CR_BONUS,
					ThresholdBonusType.AUTOMATED_POINTS);

			info.addPara("+%s combat readiness (maximum: %s)", 0f, hc, hc, "" + (int) crBonus + "%",
					"" + (int) MAX_CR_BONUS + "%");
			addAutomatedThresholdInfo(info, data, stats);

			// info.addSpacer(5f);
		}

		@Override
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.ALL_SHIPS;
		}
	}

	public static class Level2 extends AutomatedShips.Level2 {
	}
}