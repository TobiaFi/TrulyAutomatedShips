package com.fs.starfarer.api.impl.campaign.skills;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.hullmods.Automated;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TAS_AutomatedShips extends AutomatedShips {
    public static class Level0 implements DescriptionSkillEffect {
        public Color getTextColor() {
            return null;
        }

        //Since all info on AP calculation is relegated to the hullmod tooltip, skill description always stays the same
        public String getString() {
            return "*The total automated ship points are equal to the deployment points cost of all automated ships in the fleet. "
                    + "Installing an AI core on a ship will reduce its automated points and increase its maintenance costs. "
                    + "Details are found in the tooltip for the Automated Ship hullmod on the refit screen."
                    + "Ships with AI cores do not contribute to the deployment point distribution.";
        }

        //Highlights include relevant details
        public String[] getHighlights() {
            return new String[]{"automated ship points", "Automated Ship", "do not contribute to the deployment point distribution"};
        }

        //Removed one "h" compared to original
        public Color[] getHighlightColors() {
            Color h = Misc.getDarkHighlightColor();
            Color bad = Misc.getNegativeHighlightColor();
            bad = Misc.setAlpha(bad, 200);
            return new Color[]{h, h, bad};
        }
    }

    public static class Level1 extends TAS_BaseSkillEffectDescription implements ShipSkillEffect, FleetTotalSource {
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

            String partially = "";
            String penalty = "" + Math.round(Automated.MAX_CR_PENALTY * 100f) + "%%";
            if ((int) crBonus < 100f) partially = "partially ";
            info.addPara("+%s combat readiness (maximum: %s); " + partially + "offsets built-in " + penalty + " penalty", 0f, hc, hc,
                    "" + (int) crBonus + "%",
                    "" + (int) MAX_CR_BONUS + "%");
            addAutomatedThresholdInfo(info, data, stats);
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_SHIPS;
        }
    }

    public static class Level2 extends TAS_BaseSkillEffectDescription implements CharacterStatsSkillEffect {
        @Override
        public void apply(MutableCharacterStatsAPI stats, String id, float level) {
            if (stats.isPlayerStats()) {
                Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
            }
        }

        @Override
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