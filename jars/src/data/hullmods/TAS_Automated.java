package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.Arrays;

public class TAS_Automated extends BaseHullMod {

	public static float MAX_CR_PENALTY = 1f;
	public String[] acceptedAiCoreIds = {"omega_core", "alpha_core", "beta_core", "gamma_core"};

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMinCrewMod().modifyMult(id, 0);
		stats.getMaxCrewMod().modifyMult(id, 0);

		if (isInPlayerFleet(stats)) {
			stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Automated ship penalty");
		}

		if (stats.getFleetMember() != null && !stats.getFleetMember().getCaptain().isDefault() && Arrays.asList(acceptedAiCoreIds).contains(stats.getFleetMember().getCaptain().getAICoreId())) {
			float maintenanceMult = 0f;
			float hullSizeMult = 1f;
			String aiCoreId = stats.getFleetMember().getCaptain().getAICoreId();

			switch (hullSize) {
				case DESTROYER:
					hullSizeMult = 1.25f;
					break;
				case CRUISER:
					hullSizeMult = 1.5f;
					break;
				case CAPITAL_SHIP:
					hullSizeMult = 2f;
			}

			switch (aiCoreId) {
				case "omega_core":
					maintenanceMult = 2f;
					break;
				case "alpha_core":
					maintenanceMult = 1.5f;
					break;
				case "beta_core":
					maintenanceMult = 1f;
					break;
				case "gamma_core":
					maintenanceMult = 0.5f;
			}

			maintenanceMult *= hullSizeMult;
			stats.getSuppliesPerMonth().modifyMult(id, 1 + maintenanceMult);
		}


	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

		ship.setInvalidTransferCommandTarget(true);

	}



	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (isInPlayerFleet(ship)) {
			float opad = 10f;
			if (Arrays.asList(acceptedAiCoreIds).contains(ship.getCaptain().getAICoreId())) {
				float maintenanceMult = 0f;
				float hullSizeMult = 1f;
				String hullSizeString = "freighter";
				String aiCoreArticle = "a";
				String aiCoreId = ship.getCaptain().getAICoreId();

				switch (hullSize) {
					case DESTROYER:
						hullSizeMult = 1.25f;
						hullSizeString = "destroyer";
						break;
					case CRUISER:
						hullSizeMult = 1.5f;
						hullSizeString = "cruiser";
						break;
					case CAPITAL_SHIP:
						hullSizeMult = 2f;
						hullSizeString = "capital";
				}

				switch (aiCoreId) {
					case "omega_core":
						maintenanceMult = 2f;
						aiCoreArticle = "an";
						break;
					case "alpha_core":
						maintenanceMult = 1.5f;
						aiCoreArticle = "an";
						break;
					case "beta_core":
						maintenanceMult = 1f;
						break;
					case "gamma_core":
						maintenanceMult = 0.5f;
				}
				tooltip.addPara("Automated ships require specialized equipment and expertise to maintain. In a " +
					"fleet lacking these, they're virtually useless, with their maximum combat " +
					"readiness being reduced by %s.\n\nDue to this ship being outfitted with " + aiCoreArticle + " %s, its " +
					"monthly supply cost is increased by a base of %s, multiplied by %s to account for the %s size hull.\n" +
					"Monthly supply cost is increased by a total of %s.", opad, Misc.getHighlightColor(),
					"" + (int)Math.round(MAX_CR_PENALTY * 100f) + "%", ship.getCaptain().getName().getFullName(), "" + (int)Math.round(maintenanceMult * 100f) + "%",
					"" + hullSizeMult, hullSizeString, "" + (int)Math.round(maintenanceMult * 100f * hullSizeMult) + "%");
			} else {
				tooltip.addPara("Automated ships require specialized equipment and expertise to maintain. In a " +
								"fleet lacking these, they're virtually useless, with their maximum combat " +
								"readiness being reduced by %s.", opad, Misc.getHighlightColor(),
						"" + (int)Math.round(MAX_CR_PENALTY * 100f) + "%");
			}
		}
	}
}
