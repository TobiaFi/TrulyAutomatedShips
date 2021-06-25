package com.fs.starfarer.api.impl.campaign.skills;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.FleetTotalItem;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TAS_BaseSkillEffectDescription extends BaseSkillEffectDescription {

	@Override
	public void addAutomatedThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
		if (USE_RECOVERY_COST) {
			if (isInCampaign()) {
				float op = TAS_GetAutomatedPoints(data, cStats);
				info.addPara(indent + "Maximum at %s or less total automated ship points*, your fleet's total is %s ",
						0f, tc, hc,
						"" + (int) AUTOMATED_POINTS_THRESHOLD,
						"" + (int)Math.round(op));
			} else {
				info.addPara(indent + "Maximum at %s or less total automated ship points* for fleet",
						0f, tc, hc,
						"" + (int) AUTOMATED_POINTS_THRESHOLD);
			}
			return;
		}
		if (isInCampaign()) {
			float op = TAS_GetAutomatedPoints(data, cStats);
			String opStr = "points";
			if (op == 1) opStr = "point";
			info.addPara(indent + "Maximum at %s or less total automated ship points* in fleet, your fleet has %s " + opStr,
					0f, tc, hc,
					"" + (int) AUTOMATED_POINTS_THRESHOLD,
					"" + (int)Math.round(op));
		} else {
			info.addPara(indent + "Maximum at %s or less total automated ship points* in fleet",
					0f, tc, hc,
					"" + (int) AUTOMATED_POINTS_THRESHOLD);
		}
	}

	@Override
	protected float computeAndCacheThresholdBonus(FleetDataAPI data, MutableCharacterStatsAPI cStats,
			String key, float maxBonus, ThresholdBonusType type) {
		if (data == null) return maxBonus;
		if (cStats.getFleet() == null) return maxBonus;

		Float bonus = (Float) data.getCacheClearedOnSync().get(key);
		if (bonus != null) return bonus;

		float currValue = 0f;
		float threshold = 1f;

		if (type == ThresholdBonusType.FIGHTER_BAYS) {
			currValue = getNumFighterBays(data);
			threshold = FIGHTER_BAYS_THRESHOLD;
		} else if (type == ThresholdBonusType.OP) {
			currValue = getTotalCombatOP(data, cStats);
			threshold = OP_THRESHOLD;
		} else if (type == ThresholdBonusType.OP_LOW) {
			currValue = getTotalCombatOP(data, cStats);
			threshold = OP_LOW_THRESHOLD;
		} else if (type == ThresholdBonusType.OP_ALL_LOW) {
			currValue = getTotalOP(data, cStats);
			threshold = OP_ALL_LOW_THRESHOLD;
		} else if (type == ThresholdBonusType.MILITARIZED_OP) {
			currValue = getMilitarizedOP(data, cStats);
			threshold = MILITARIZED_OP_THRESHOLD;
		} else if (type == ThresholdBonusType.PHASE_OP) {
			currValue = getPhaseOP(data, cStats);
			threshold = PHASE_OP_THRESHOLD;
		} else if (type == ThresholdBonusType.AUTOMATED_POINTS) {
			currValue = TAS_GetAutomatedPoints(data, cStats);
			threshold = AUTOMATED_POINTS_THRESHOLD;
		}

		bonus = getThresholdBasedRoundedBonus(maxBonus, currValue, threshold);

		data.getCacheClearedOnSync().put(key, bonus);
		return bonus;
	}

	@Override
	public FleetTotalItem getAutomatedPointsTotal() {
		final CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		final MutableCharacterStatsAPI stats = Global.getSector().getPlayerStats();
		FleetTotalItem item = new FleetTotalItem();
		item.label = "Automated ships";
		item.value = "" + (int) TAS_BaseSkillEffectDescription.TAS_GetAutomatedPoints(fleet.getFleetData(), stats);
		item.sortOrder = 350;
		
		item.tooltipCreator = getTooltipCreator(new TooltipCreatorSkillEffectPlugin() {
			public void addDescription(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
				float opad = 10f;
				tooltip.addPara("The total deployment points of all the automated ships in your fleet, "
						+ "with additional points for ships controlled by AI cores.", 0f);
			}
			public List<FleetMemberPointContrib> getContributors() {
				return TAS_GetAutomatedPointsDetail(fleet.getFleetData(), stats);
			}
		});
		
		return item;
	}

	public static float TAS_GetAutomatedPoints(FleetDataAPI data, MutableCharacterStatsAPI stats) {
		float points = 0f;
		for (FleetMemberAPI curr : data.getMembersListCopy()) {
			float pts = getPoints(curr, stats);
			if (curr.isMothballed()) continue;
			if (!Misc.isAutomated(curr)) continue;
			if (curr.getCaptain().isAICore()) {
				pts *= 1 - curr.getCaptain().getMemoryWithoutUpdate().getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE);
			}
			points += pts;
		}
		return Math.round(points);
	}
	
	public static List<FleetMemberPointContrib> TAS_GetAutomatedPointsDetail(FleetDataAPI data, MutableCharacterStatsAPI stats) {
		List<FleetMemberPointContrib> result = new ArrayList<BaseSkillEffectDescription.FleetMemberPointContrib>();
		for (FleetMemberAPI curr : data.getMembersListCopy()) {
			if (curr.isMothballed()) continue;
			if (!Misc.isAutomated(curr)) continue;
			
			float pts = getPoints(curr, stats);
			if (curr.getCaptain().isAICore()) {
				pts *= 1 - curr.getCaptain().getMemoryWithoutUpdate().getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE);
			}
			result.add(new FleetMemberPointContrib(curr, Math.round(pts)));
		}
		return result;
	}

}
