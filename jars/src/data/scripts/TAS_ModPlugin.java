package data.scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.TAS_CampaignPlugin;
import com.fs.starfarer.api.impl.campaign.TAS_AICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;

import data.hullmods.TAS_Automated;

public class TAS_ModPlugin extends BaseModPlugin {
	@Override
	public void onGameLoad(boolean newGame) {
		Global.getSector().registerPlugin(new TAS_CampaignPlugin());

		TAS_Automated.acceptedAiCoreIds = Arrays.asList(TAS_Automated.defaultAcceptedAiCoreIds);
		try {
			JSONArray acceptedCustom = Global.getSettings().getJSONArray("TAS_acceptedAIcores");

			List<String> acceptedTemp = new ArrayList<>();
			for (int i = 0; i < acceptedCustom.length(); i++) {
				acceptedTemp.add(acceptedCustom.getString(i));
			}

			TAS_Automated.acceptedAiCoreIds = acceptedTemp;
		} catch (JSONException e) {
		}

		if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
			TAS_AICoreOfficerPluginImpl.OMEGA_POINTS = 1f;
			try {
				TAS_AICoreOfficerPluginImpl.OMEGA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("default");
				TAS_AICoreOfficerPluginImpl.OMEGA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("omega_core");
			} catch (JSONException e) {
			}

			TAS_AICoreOfficerPluginImpl.ALPHA_POINTS = 0.75f;
			try {
				TAS_AICoreOfficerPluginImpl.ALPHA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("default");
				TAS_AICoreOfficerPluginImpl.ALPHA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("alpha_core");
			} catch (JSONException e) {
			}

			TAS_AICoreOfficerPluginImpl.BETA_POINTS = 0.5f;
			try {
				TAS_AICoreOfficerPluginImpl.BETA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("default");
				TAS_AICoreOfficerPluginImpl.BETA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("beta_core");
			} catch (JSONException e) {
			}

			TAS_AICoreOfficerPluginImpl.GAMMA_POINTS = 0.25f;
			try {
				TAS_AICoreOfficerPluginImpl.GAMMA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("default");
				TAS_AICoreOfficerPluginImpl.GAMMA_POINTS = (float) Global.getSettings()
						.getJSONObject("TAS_AIcoreShipWeightMultipliers").getDouble("gamma_core");
			} catch (JSONException e) {
			}
		}
	}
}
