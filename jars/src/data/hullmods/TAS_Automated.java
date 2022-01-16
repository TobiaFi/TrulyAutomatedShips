package data.hullmods;

import java.util.List;

import org.json.JSONException;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TAS_Automated extends BaseHullMod {
    public static float MAX_CR_PENALTY = 1f;

    //These are used to prevent compatibility issues with other mods, preventing new core types from breaking the hullmod
    public static String[] defaultAcceptedAICoreIds = {"omega_core", "alpha_core", "beta_core", "gamma_core"};
    public static List<String> acceptedAICoreIds;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMinCrewMod().modifyMult(id, 0);
        stats.getMaxCrewMod().modifyMult(id, 0);

        if (isInPlayerFleet(stats)) {
            stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Automated ship penalty");
        }

        //Retrieves maintenance values for AI core and hull size and applies the total maintenance increase
        if (stats.getFleetMember() != null && !stats.getFleetMember().getCaptain().isDefault()
                && acceptedAICoreIds.contains(stats.getFleetMember().getCaptain().getAICoreId())) {
            float hullSizeMod = 1f;
            float maintenanceMult = 0f;
            String aiCoreId = stats.getFleetMember().getCaptain().getAICoreId();

            try {
                hullSizeMod = (float) Global.getSettings().getJSONObject("TAS_hullSizeModifiers")
                        .getDouble("default");
                hullSizeMod = (float) Global.getSettings().getJSONObject("TAS_hullSizeModifiers")
                        .getDouble(hullSize.name().toLowerCase());
            } catch (JSONException ignore) {
            }

            try {
                maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenanceMultipliers")
                        .getDouble("default");
                maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenanceMultipliers")
                        .getDouble(aiCoreId);
            } catch (JSONException ignore) {
            }

            maintenanceMult *= hullSizeMod;
            stats.getSuppliesPerMonth().modifyMult(id, 1 + maintenanceMult);
        }

    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.setInvalidTransferCommandTarget(true);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + Math.round(MAX_CR_PENALTY * 100f);
        return null;
    }

    //Tooltip includes detailed info on AP reduction and maintenance increase calculations
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width,
                                          boolean isForModSpec) {
        if (isInPlayerFleet(ship)) {
            float opad = 10f;
            if (acceptedAICoreIds.contains(ship.getCaptain().getAICoreId())) {
                float apReduction = 1f;
                float hullSizeMod = 1f;
                float maintenanceMult = 0f;
                String hullSizeString;
                String aiCoreId = ship.getCaptain().getAICoreId();

                try {
                    apReduction = (float) Global.getSettings().getJSONObject("TAS_AICoreShipWeightReduction")
                            .getDouble("default");
                    apReduction = (float) Global.getSettings().getJSONObject("TAS_AICoreShipWeightReduction")
                            .getDouble(aiCoreId);
                } catch (JSONException ignore) {
                }

                try {
                    hullSizeMod = (float) Global.getSettings().getJSONObject("TAS_hullSizeModifiers")
                            .getDouble("default");
                    hullSizeMod = (float) Global.getSettings().getJSONObject("TAS_hullSizeModifiers")
                            .getDouble(hullSize.name().toLowerCase());
                } catch (JSONException ignore) {
                }

                try {
                    maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenanceMultipliers")
                            .getDouble("default");
                    maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenanceMultipliers")
                            .getDouble(aiCoreId);
                } catch (JSONException ignore) {
                }

                switch (hullSize) {
                    case DESTROYER:
                        hullSizeString = "destroyer";
                        break;
                    case CRUISER:
                        hullSizeString = "cruiser";
                        break;
                    case CAPITAL_SHIP:
                        hullSizeString = "capital";
                        break;
                    case FRIGATE:
                        hullSizeString = "frigate";
                        break;
                    default:
                        hullSizeString = hullSize.name().toLowerCase();
                }

                tooltip.addPara("Automated ships require specialized equipment and expertise to maintain. In a "
                                + "fleet lacking these, they're virtually useless, with their maximum combat readiness being reduced by %s."
                                + "\n\nThe %s installed on this ship affects its total automated ship points and monthly maintenance:"
                                + "\n- Automated ship points are decreased by a base of %s, divided by %s to account for the %s size hull, for a total of %s."
                                + "\n- Monthly maintenance is increased by a base of %s, multiplied by %s to account for the %s size hull, for a total of %s supplies."
                                + "\n\nShip's automated points: %s.\nShip's monthly maintenance: %s supplies.",
                        opad, Misc.getHighlightColor(),
                        "" + Math.round(MAX_CR_PENALTY * 100f) + "%", ship.getCaptain().getName().getFullName(),
                        "" + Math.round(apReduction * 100f) + "%", "" + hullSizeMod, hullSizeString,
                        "" + Math.round(apReduction * 100f / hullSizeMod) + "%",
                        "" + Math.round(maintenanceMult * 100f) + "%", "" + hullSizeMod, hullSizeString,
                        "" + Math.round(maintenanceMult * 100f * hullSizeMod) + "%",
                        "" + Math.round(ship.getMutableStats().getSuppliesToRecover().base * (1 - apReduction / hullSizeMod)),
                        "" + Math.round(ship.getMutableStats().getSuppliesPerMonth().base * (1 + maintenanceMult * hullSizeMod)));
            } else {
                tooltip.addPara(
                        "Automated ships require specialized equipment and expertise to maintain. In a "
                                + "fleet lacking these, they're virtually useless, with their maximum combat "
                                + "readiness being reduced by %s.",
                        opad, Misc.getHighlightColor(), "" + Math.round(MAX_CR_PENALTY * 100f) + "%");
            }
        }
    }
}
