package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TASAutomated extends BaseHullMod {

	public static float MAX_CR_PENALTY = 1f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMinCrewMod().modifyMult(id, 0);
		stats.getMaxCrewMod().modifyMult(id, 0);

		if (isInPlayerFleet(stats)) {
			stats.getMaxCombatReadiness().modifyFlat(id, -MAX_CR_PENALTY, "Automated ship penalty");
		}

		if (stats.getFleetMember() != null) {
			float MAINTENANCE_MULT = 1f;

			switch (stats.getFleetMember().getCaptain().getNameString()) {
				case "Omega Core":
					MAINTENANCE_MULT = 5f;
					break;
				case "Alpha Core":
					MAINTENANCE_MULT = 4f;
					break;
				case "Beta Core":
					MAINTENANCE_MULT = 3f;
					break;
				case "Gamma Core":
					MAINTENANCE_MULT = 2f;
			}

			stats.getSuppliesPerMonth().modifyMult(id, MAINTENANCE_MULT);
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
			tooltip.addPara("Automated ships require specialized equipment and expertise to maintain. In a " +
					"fleet lacking these, they're virtually useless, with their maximum combat " +
					"readiness being reduced by %s.", opad, Misc.getHighlightColor(),
					"" + (int)Math.round(MAX_CR_PENALTY * 100f) + "%");
		}
	}
}
