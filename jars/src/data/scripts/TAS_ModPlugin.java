package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.TASCampaignPlugin;

public class TAS_ModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().registerPlugin(new TASCampaignPlugin());
    }
}
