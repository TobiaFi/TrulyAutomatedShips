package data.scripts;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.AICoreOfficerPluginImpl;

import data.hullmods.TAS_Automated;

public class TAS_ModPlugin extends BaseModPlugin {

    public void onGameLoad(boolean newGame) {

        //Sets accepted cores for compatibility with other mods
        TAS_Automated.acceptedAICoreIds = Arrays.asList(TAS_Automated.defaultAcceptedAICoreIds);
        try {
            JSONArray acceptedCustom = Global.getSettings().getJSONArray("TAS_acceptedAICoreIds");

            List<String> acceptedTemp = new ArrayList<>();
            for (int i = 0; i < acceptedCustom.length(); i++) {
                acceptedTemp.add(acceptedCustom.getString(i));
            }

            TAS_Automated.acceptedAICoreIds = acceptedTemp;
        } catch (JSONException ignore) {
        }

        //Sets new values for the multipliers in AICoreOfficerPluginImpl
        try {
            AICoreOfficerPluginImpl.OMEGA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("default");
            AICoreOfficerPluginImpl.OMEGA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("omega_core");
        } catch (JSONException ignore) {
        }

        try {
            AICoreOfficerPluginImpl.ALPHA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("default");
            AICoreOfficerPluginImpl.ALPHA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("alpha_core");
        } catch (JSONException ignore) {
        }

        try {
            AICoreOfficerPluginImpl.BETA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("default");
            AICoreOfficerPluginImpl.BETA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("beta_core");
        } catch (JSONException ignore) {
        }

        try {
            AICoreOfficerPluginImpl.GAMMA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("default");
            AICoreOfficerPluginImpl.GAMMA_MULT = (float) Global.getSettings()
                    .getJSONObject("TAS_AICoreShipWeightReduction").getDouble("gamma_core");
        } catch (JSONException ignore) {
        }
    }
}
