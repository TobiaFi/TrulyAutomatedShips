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
	public static String[] defaultAcceptedAiCoreIds = {"omega_core", "alpha_core", "beta_core", "gamma_core"};

	public static float MAX_CR_PENALTY = 1f;
	public static List<String> acceptedAiCoreIds;

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMinCrewMod().modifyMult(id, 0);
		stats.getMaxCrewMod().modifyMult(id, 0);

		if (isInPlayerFleet(stats)) {
			stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Automated ship penalty");
		}

		if (stats.getFleetMember() != null && !stats.getFleetMember().getCaptain().isDefault()
				&& acceptedAiCoreIds.contains(stats.getFleetMember().getCaptain().getAICoreId())) {
			float hullSizeMult = 1f;
			float maintenanceMult = 0f;
			String aiCoreId = stats.getFleetMember().getCaptain().getAICoreId();

			try {
				hullSizeMult = (float) Global.getSettings().getJSONObject("TAS_hullSizeMultipliers")
						.getDouble("default");
				hullSizeMult = (float) Global.getSettings().getJSONObject("TAS_hullSizeMultipliers")
						.getDouble(hullSize.name().toLowerCase());
			} catch (JSONException e) {
			}

			try {
				maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenaceMultipliers")
						.getDouble("default");
				maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenaceMultipliers")
						.getDouble(aiCoreId);
			} catch (JSONException e) {
			}

			maintenanceMult *= hullSizeMult;
			stats.getSuppliesPerMonth().modifyMult(id, 1 + maintenanceMult);
		}

	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setInvalidTransferCommandTarget(true);
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width,
			boolean isForModSpec) {
		if (isInPlayerFleet(ship)) {
			float opad = 10f;
			if (acceptedAiCoreIds.contains(ship.getCaptain().getAICoreId())) {
				float hullSizeMult = 1f;
				float maintenanceMult = 0f;
				String hullSizeString = "freighter";
				String aiCoreArticle = "a";
				String aiCoreId = ship.getCaptain().getAICoreId();

				try {
					hullSizeMult = (float) Global.getSettings().getJSONObject("TAS_hullSizeMultipliers")
							.getDouble("default");
					hullSizeMult = (float) Global.getSettings().getJSONObject("TAS_hullSizeMultipliers")
							.getDouble(hullSize.name().toLowerCase());
				} catch (JSONException e) {
				}

				try {
					maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenaceMultipliers")
							.getDouble("default");
					maintenanceMult = (float) Global.getSettings().getJSONObject("TAS_maintenaceMultipliers")
							.getDouble(aiCoreId);
				} catch (JSONException e) {
				}

				try {
					aiCoreArticle = Global.getSettings().getJSONObject("TAS_AIcoreArticles").getString("default");
					aiCoreArticle = Global.getSettings().getJSONObject("TAS_AIcoreArticles").getString(aiCoreId);
				} catch (JSONException e) {
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
					default:
						hullSizeString = hullSize.name().toLowerCase();
						break;
				}

				tooltip.addPara("Automated ships require specialized equipment and expertise to maintain. In a "
						+ "fleet lacking these, they're virtually useless, with their maximum combat "
						+ "readiness being reduced by %s.\n\nDue to this ship being outfitted with " + aiCoreArticle
						+ " %s, its "
						+ "monthly supply cost is increased by a base of %s, multiplied by %s to account for the %s size hull.\n"
						+ "Monthly supply cost is increased by a total of %s.", opad, Misc.getHighlightColor(),
						"" + Math.round(MAX_CR_PENALTY * 100f) + "%", ship.getCaptain().getName().getFullName(),
						"" + Math.round(maintenanceMult * 100f) + "%", "" + hullSizeMult, hullSizeString,
						"" + Math.round(maintenanceMult * 100f * hullSizeMult) + "%");
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
