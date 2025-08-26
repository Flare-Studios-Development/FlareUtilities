package gg.flarestudios.flareUtilities;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import gg.flarestudios.flareUtilities.inventorySaver.InventorySaver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandsRegistrar {
    // Defining argument for suggesting player in save/load command.
    Argument<?> noSelectorSuggestions = new PlayerArgument("target")
            .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                    Bukkit.getOnlinePlayers().toArray(new Player[0])
            ));

    // Defining argument for suggesting a string -clear in save command.
    Argument<?> clear = new StringArgument("clear")
            .replaceSuggestions(ArgumentSuggestions.strings("-clear"));

    // Building the /flare command
    public void buildCommands() {
        new CommandAPICommand("flare").withPermission("flareutilities.command.flare")
                .withSubcommand(new CommandAPICommand("inv").withPermission("flareutilities.command.flare.inv")
                        .withSubcommands(
                                new CommandAPICommand("save").withPermission("flareutilities.command.flare.inv.save")
                                .withOptionalArguments(noSelectorSuggestions).withOptionalArguments(clear)
                                        .executesPlayer((player, commandArguments) -> {
                                            Player target = (Player) commandArguments.get("target");
                                            // save inventory of the sender, when not defining a target.
                                            if (commandArguments.get("target") == null && commandArguments.get("clear") == null) {
                                                new InventorySaver().saveInventory(player);
                                            }
                                            // save inventory of the sender, when not defining a target.
                                            if (commandArguments.get("target") == null && commandArguments.get("clear") != null) {
                                                new InventorySaver().saveInventory(player);
                                                player.getInventory().clear();
                                            }
                                            // Save inventory of the defined target without clearing it.
                                            if (commandArguments.get("target") != null && commandArguments.get("clear") == null) {
                                                new InventorySaver().saveInventory(target);
                                            }
                                            // Save inventory of the defined target with clearing it after.
                                            if (commandArguments.get("target") != null && commandArguments.get("clear") != null) {
                                                new InventorySaver().saveInventory(target);
                                                assert target != null;
                                                target.getInventory().clear();
                                            }
                                        }).executesConsole((sender, commandArguments) -> {
                                            Player target = (Player) commandArguments.get("target");
                                            // Throw an error when omitting the target from the console.
                                            if (commandArguments.get("target") == null) {
                                                PaperPluginLogger.getLogger("FlareUtilities").severe("You must add a target for using this command from the console!");
                                            }
                                            // Save inventory of the defined target without clearing it.
                                            if (commandArguments.get("target") != null && commandArguments.get("clear") == null) {
                                                new InventorySaver().saveInventory(target);
                                                // Ignore NPE risk on getName, it's checked in the command to be an online player.
                                                PaperPluginLogger.getLogger("FlareUtilities").info("Saved " + target.getName() + "'s inventory.");
                                            }
                                            // Save inventory of the defined target with clearing it after.
                                            if (commandArguments.get("target") != null && commandArguments.get("clear") != null) {
                                                new InventorySaver().saveInventory(target);
                                                // Ignore NPE risk on getName, it's checked in the command to be an online player.
                                                PaperPluginLogger.getLogger("FlareUtilities").info("Saved " + target.getName() + "'s inventory and cleared it afterwards.");
                                                target.getInventory().clear();
                                            }
                                        }),
                                new CommandAPICommand("load").withPermission("flareutilities.command.flare.inv.load")
                                .withOptionalArguments(noSelectorSuggestions)
                                        .executesPlayer((player, commandArguments) -> {
                                            Player target = (Player) commandArguments.get("target");
                                            // Load inventory of the sender, when not defining a target.
                                            if (commandArguments.get("target") == null) {
                                                new InventorySaver().loadAll(player);
                                            }
                                            // Load inventory of the defined target.
                                            if (commandArguments.get("target") != null) {
                                                new InventorySaver().loadAll(target);
                                            }
                                        }).executesConsole((sender, commandArguments) -> {
                                            Player target = (Player) commandArguments.get("target");
                                            // Load inventory of the sender, when not defining a target.
                                            if (commandArguments.get("target") == null) {
                                                PaperPluginLogger.getLogger("FlareUtilities").severe("You must add a target for using this command from the console!");
                                            }
                                            // Load inventory of the defined target.
                                            if (commandArguments.get("target") != null) {
                                                new InventorySaver().loadAll(target);
                                                assert target != null;
                                                PaperPluginLogger.getLogger("FlareUtilities").info("Loaded " + target.getName() + "'s inventory.");
                                            }
                                        }))).register();
        PaperPluginLogger.getLogger("FlareUtilities").info("Registered commands!");
    }
}
