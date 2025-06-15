package com.myogoo.extendedterminal.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class ETReiPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "Extended Terminal";
    }
}
