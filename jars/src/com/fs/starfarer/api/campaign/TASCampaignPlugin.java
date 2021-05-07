package com.fs.starfarer.api.campaign;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.impl.campaign.TASAICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;


public class TASCampaignPlugin extends BaseCampaignPlugin {

	@Override
	public PluginPick<AICoreOfficerPlugin> pickAICoreOfficerPlugin(String commodityId) {
		if (Commodities.OMEGA_CORE.equals(commodityId) ||
				Commodities.ALPHA_CORE.equals(commodityId) ||
				Commodities.BETA_CORE.equals(commodityId) ||
				Commodities.GAMMA_CORE.equals(commodityId)) {
			return new PluginPick<AICoreOfficerPlugin>(new TASAICoreOfficerPluginImpl(), PickPriority.MOD_GENERAL);
		}
		return null;
	}

}








