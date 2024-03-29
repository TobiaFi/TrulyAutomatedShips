package com.fs.starfarer.api.impl.campaign.skills;

import java.util.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.FleetTotalItem;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.json.JSONException;

public class TAS_BaseSkillEffectDescription extends BaseSkillEffectDescription {

    //Includes the modifier based on hull size. mult becomes a percentage reduction, which gets divided by hullSizeMod.
    //This scales AP reduction with hull size
    public static float getAutomatedPoints(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float points = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (!Misc.isAutomated(curr)) continue;
            float mult = curr.getCaptain().getMemoryWithoutUpdate().getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT);
            float hullSizeMod = 1f;
            String hullSize = curr.getHullSpec().getHullSize().toString().toLowerCase();
            try {
                hullSizeMod = (float) Global.getSettings().getJSONObject("TAS_hullSizeModifiers").getDouble(hullSize);
            } catch (JSONException ignore) {
            }
            points += Math.round(getPoints(curr, stats) * (1 - mult / hullSizeMod));
        }
        return Math.round(points);
    }

    //Includes the modifier based on hull size. mult becomes a percentage reduction, which gets divided by hullSizeMod.
    //This scales AP reduction with hull size
    public static List<FleetMemberPointContrib> getAutomatedPointsDetail(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        List<FleetMemberPointContrib> result = new ArrayList<>();
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (!Misc.isAutomated(curr)) continue;
            float mult = curr.getCaptain().getMemoryWithoutUpdate().getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT);
            float hullSizeMod = 1f;
            String hullSize = curr.getHullSpec().getHullSize().toString().toLowerCase();
            try {
                hullSizeMod = (float) Global.getSettings().getJSONObject("TAS_hullSizeModifiers").getDouble(hullSize);
            } catch (JSONException ignore) {
            }
            result.add(new FleetMemberPointContrib(curr, Math.round(getPoints(curr, stats) * (1 - mult / hullSizeMod))));
        }
        return result;
    }

    //Only change to this method is the text of the tooltip specifying that AI cores reduce AP
    public FleetTotalItem getAutomatedPointsTotal() {
        final CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        final MutableCharacterStatsAPI stats = Global.getSector().getPlayerStats();
        FleetTotalItem item = new FleetTotalItem();
        item.label = "Automated ships";
        item.value = "" + (int) getAutomatedPoints(fleet.getFleetData(), stats);
        item.sortOrder = 350;

        item.tooltipCreator = getTooltipCreator(new TooltipCreatorSkillEffectPlugin() {
            public void addDescription(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("The total deployment points of all the automated ships in your fleet, "
                        + "with a reduction for ships controlled by AI cores.", 0f);
            }

            public List<FleetMemberPointContrib> getContributors() {
                return getAutomatedPointsDetail(fleet.getFleetData(), stats);
            }
        });

        return item;
    }

    //Unchanged, but required to calculate proper AP values in the skill menu
    public void addAutomatedThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        if (USE_RECOVERY_COST) {
            if (isInCampaign()) {
                float op = getAutomatedPoints(data, cStats);
                info.addPara(indent + "Maximum at %s or less total automated ship points*, your fleet's total is %s ",
                        0f, tc, hc,
                        "" + (int) AUTOMATED_POINTS_THRESHOLD,
                        "" + Math.round(op));
            } else {
                info.addPara(indent + "Maximum at %s or less total automated ship points* for fleet",
                        0f, tc, hc,
                        "" + (int) AUTOMATED_POINTS_THRESHOLD);
            }
            return;
        }
        if (isInCampaign()) {
            float op = getAutomatedPoints(data, cStats);
            String opStr = "points";
            if (op == 1) opStr = "point";
            info.addPara(indent + "Maximum at %s or less total automated ship points* in fleet, your fleet has %s " + opStr,
                    0f, tc, hc,
                    "" + (int) AUTOMATED_POINTS_THRESHOLD,
                    "" + Math.round(op));
        } else {
            info.addPara(indent + "Maximum at %s or less total automated ship points* in fleet",
                    0f, tc, hc,
                    "" + (int) AUTOMATED_POINTS_THRESHOLD);
        }
    }

    //Unchanged, but required for proper CR calculation
    @Override
    protected float computeAndCacheThresholdBonus(MutableShipStatsAPI stats,
                                                         String key, float maxBonus, ThresholdBonusType type) {
        FleetDataAPI data = getFleetData(stats);
        MutableCharacterStatsAPI cStats = getCommanderStats(stats);
        return computeAndCacheThresholdBonus(data, cStats, key, maxBonus, type);
    }

    //Unchanged, but required for proper CR calculation
    @Override
    protected float computeAndCacheThresholdBonus(FleetDataAPI data, MutableCharacterStatsAPI cStats,
                                                  String key, float maxBonus, ThresholdBonusType type) {
//		if (key.equals("pc_peak")) {
//			System.out.println("efwfwefwe");
//		}
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
        } else if (type == ThresholdBonusType.OP_ALL) {
            currValue = getTotalOP(data, cStats);
            threshold = OP_ALL_THRESHOLD;
        } else if (type == ThresholdBonusType.MILITARIZED_OP) {
            currValue = getMilitarizedOP(data, cStats);
            threshold = MILITARIZED_OP_THRESHOLD;
        } else if (type == ThresholdBonusType.PHASE_OP) {
            currValue = getPhaseOP(data, cStats);
            threshold = PHASE_OP_THRESHOLD;
        } else if (type == ThresholdBonusType.AUTOMATED_POINTS) {
            currValue = getAutomatedPoints(data, cStats);
            threshold = AUTOMATED_POINTS_THRESHOLD;
        }

        bonus = getThresholdBasedRoundedBonus(maxBonus, currValue, threshold);

        data.getCacheClearedOnSync().put(key, bonus);
        return bonus;
    }

}
