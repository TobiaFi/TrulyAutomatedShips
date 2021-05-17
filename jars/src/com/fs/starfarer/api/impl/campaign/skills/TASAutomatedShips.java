package com.fs.starfarer.api.impl.campaign.skills;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.TASAICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TASAutomatedShips extends AutomatedShips {

	public static class Level0 implements DescriptionSkillEffect {

		@Override
		public String getString() {
			float alpha = TASAICoreOfficerPluginImpl.ALPHA_POINTS;
			float beta = TASAICoreOfficerPluginImpl.BETA_POINTS;
			float gamma = TASAICoreOfficerPluginImpl.GAMMA_POINTS;
			if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
				return "*The total \"automated ship points\" are equal to the recovery cost of " +
						"all automated ships in the fleet. Installing an AI core on a ship will reduce its point total by " +
						getHighlights()[0] + " for an Alpha Core, " +
						getHighlights()[1] + " for a Beta Core, and " +
						getHighlights()[2] + " for a Gamma Core." +
						"\nA ship with an AI core installed will have its monthly maintenance cost increased depending on hull size and core type."
						;
			} else {
				return "*The total \"automated ship points\" are equal to the ordnance points of " +
						"all automated ships in the fleet, plus extra points for AI cores installed on any of the " +
						"automated ships - " + 
						alpha + " for an Alpha Core, " +			
						beta + " for a Beta Core, and " +			
						gamma + " for a Gamma Core."
						;
			}
		}

		public Color[] getHighlightColors() {
			Color h = Misc.getHighlightColor();
			h = Misc.getDarkHighlightColor();
			return new Color[] {h, h, h};
		}

		@Override
		public String[] getHighlights() {
			if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
				int alpha = (int) (TASAICoreOfficerPluginImpl.ALPHA_POINTS * 100);
				int beta = (int) (TASAICoreOfficerPluginImpl.BETA_POINTS * 100);
				int gamma = (int) (TASAICoreOfficerPluginImpl.GAMMA_POINTS * 100);
				return new String [] {"" + alpha + "%", "" + beta + "%", "" + gamma + "%"};
			} else {
				int alpha = (int) TASAICoreOfficerPluginImpl.ALPHA_POINTS;
				int beta = (int) TASAICoreOfficerPluginImpl.BETA_POINTS;
				int gamma = (int) TASAICoreOfficerPluginImpl.GAMMA_POINTS;
				return new String [] {"" + alpha, "" + beta, "" + gamma};
			}

		}
		public Color getTextColor() {
			return null;
		}
	}

	public static class Level1 extends TASBaseSkillEffectDescription implements ShipSkillEffect, FleetTotalSource {

		public FleetTotalItem getFleetTotalItem() {
			return getAutomatedPointsTotal();
		}

		public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
			if (Misc.isAutomated(stats)) {
				float crBonus = computeAndCacheThresholdBonus(stats, "auto_cr", MAX_CR_BONUS, ThresholdBonusType.AUTOMATED_POINTS);
				SkillSpecAPI skill = Global.getSettings().getSkillSpec(Skills.AUTOMATED_SHIPS);
				stats.getMaxCombatReadiness().modifyFlat(id, crBonus * 0.01f, skill.getName() + " skill");
			}
		}

		public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
			stats.getMaxCombatReadiness().unmodifyFlat(id);
		}

		public String getEffectDescription(float level) {
			return null;
		}

		public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill,
											TooltipMakerAPI info, float width) {
			init(stats, skill);

			FleetDataAPI data = getFleetData(null);
			float crBonus = computeAndCacheThresholdBonus(data, stats, "auto_cr", MAX_CR_BONUS, ThresholdBonusType.AUTOMATED_POINTS);

			info.addPara("+%s combat readiness (maximum: %s)", 0f, hc, hc,
					"" + (int) crBonus + "%",
					"" + (int) MAX_CR_BONUS + "%");
			addAutomatedThresholdInfo(info, data, stats);

			//info.addSpacer(5f);
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.ALL_SHIPS;
		}
	}

	public static class Level2 extends AutomatedShips.Level2 {}

}