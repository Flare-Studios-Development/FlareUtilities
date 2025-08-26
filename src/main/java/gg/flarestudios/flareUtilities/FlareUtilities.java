package gg.flarestudios.flareUtilities;

import gg.flarestudios.flareUtilities.inventorySaver.*;
import dev.jorel.commandapi.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class FlareUtilities extends JavaPlugin {

    @Override
    public void onLoad() {
        // Initialize CommandAPI
        this.getLogger().info("FlareUtilities initializing CommandAPI...");
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false));

        // Initialize InventorySaver
        new InventorySaver();

        // Register Inventory Saver Commands
        new CommandsRegistrar().buildCommands();

        // Fully loaded
        this.getLogger().info("Plugin fully loaded.");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandAPI.onEnable();
        this.getLogger().info("Hello :)");
        this.getLogger().info("Plugin enabled.");
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CommandAPI.onDisable();
        this.getLogger().info("Plugin disabled.");
    }
}