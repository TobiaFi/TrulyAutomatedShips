package com.fs.starfarer.api.impl.campaign.skills;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.DescriptionSkillEffect;
import com.fs.starfarer.api.characters.FleetTotalItem;
import com.fs.starfarer.api.characters.FleetTotalSource;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.TASAICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TASAutomatedShips {
	
	public static float MAX_CR_BONUS = 100f;

	public static class Level0 implements DescriptionSkillEffect {
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
						"\nA ship with an AI core installed will have its monthly maintenance cost increased by " +
						getHighlights()[3] + "/" + getHighlights()[4] + "/" + getHighlights()[5] + "."
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
			Color h; //= Misc.getHighlightColor();
			h = Misc.getDarkHighlightColor();
			return new Color[] {h, h, h, h, h, h};
		}
		public String[] getHighlights() {
			if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
				int alpha = (int) (TASAICoreOfficerPluginImpl.ALPHA_POINTS * 100);
				int beta = (int) (TASAICoreOfficerPluginImpl.BETA_POINTS * 100);
				int gamma = (int) (TASAICoreOfficerPluginImpl.GAMMA_POINTS * 100);
				int alphacost = alpha * 4;
				int betacost = beta * 4;
				int gammacost = gamma * 4;
				return new String [] {"-" + alpha + "%", "-" + beta + "%", "-" + gamma + "%", "+" + alphacost + "%", "+" + betacost + "%", "+" + gammacost + "%"};
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
		
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			if (Misc.isAutomated(stats)) {
				float crBonus = computeAndCacheThresholdBonus(stats, "auto_cr", MAX_CR_BONUS, ThresholdBonusType.AUTOMATED_POINTS);
				SkillSpecAPI skill = Global.getSettings().getSkillSpec(Skills.AUTOMATED_SHIPS);
				stats.getMaxCombatReadiness().modifyFlat(id, crBonus * 0.01f, skill.getName() + " skill");
			}
		}
			
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
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
	
	public static class Level2 extends BaseSkillEffectDescription implements CharacterStatsSkillEffect {

		public void apply(MutableCharacterStatsAPI stats, String id, float level) {
			if (stats.isPlayerStats()) {
				Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
			}
		}

		public void unapply(MutableCharacterStatsAPI stats, String id) {
			if (stats.isPlayerStats()) {
				Misc.getAllowedRecoveryTags().remove(Tags.AUTOMATED_RECOVERABLE);
			}
		}
		
		public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill, 
				TooltipMakerAPI info, float width) {
			init(stats, skill);
			info.addPara("Enables the recovery of some automated ships, such as derelict drones", hc, 0f);
			info.addPara("Automated ships can only be captained by AI cores", hc, 0f);
			info.addSpacer(5f);
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.FLEET;
		}
	}

}





