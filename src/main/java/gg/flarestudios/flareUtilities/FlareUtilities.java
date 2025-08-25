package gg.flarestudios.flareUtilities;

import gg.flarestudios.flareUtilities.inventorySaver.*;
import dev.jorel.commandapi.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class FlareUtilities extends JavaPlugin {

    private InventorySaver inventorySaver;
    public InventorySaver getInventorySaver(){
        return inventorySaver;
    }

    @Override
    public void onLoad() {
        // Initialize CommandAPI
        this.getLogger().info("FlareUtilities initializing CommandAPI...");
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false));

        // Register Commands
        this.getLogger().info("FlareUtilities building commands...");
        new CommandsRegistrar().buildCommands();

        // Initialize InventorySaver
        this.inventorySaver = new InventorySaver();

        // Fully loaded
        this.getLogger().info("FlareUtilities fully loaded.");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandAPI.onEnable();
        this.getLogger().info("Hello");
        this.getLogger().info("FlareUtilities enabled.");
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CommandAPI.onDisable();
        this.getLogger().info("FlareUtilities disabled.");
    }
}