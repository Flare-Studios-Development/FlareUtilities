package gg.flarestudios.flareUtilities.inventorySaver;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandsRegistrar {

    Argument<?> noSelectorSuggestions = new PlayerArgument("target")
            .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                    Bukkit.getOnlinePlayers().toArray(new Player[0])
            ));

    Argument<?> clear = new StringArgument("clear")
            .replaceSuggestions(ArgumentSuggestions.strings("-clear"));


    public void buildCommands() {
        new CommandAPICommand("flare")
                .withSubcommand(new CommandAPICommand("inv")
                        .withSubcommands(
                                new CommandAPICommand("save")
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
                                                PaperPluginLogger.getLogger("FlareUtilities").info("Saved " + target.getName() + "'s inventory.");
                                            }
                                            // Save inventory of the defined target with clearing it after.
                                            if (commandArguments.get("target") != null && commandArguments.get("clear") != null) {
                                                new InventorySaver().saveInventory(target);
                                                assert target != null;
                                                PaperPluginLogger.getLogger("FlareUtilities").info("Saved " + target.getName() + "'s inventory and cleared it afterwards.");
                                                target.getInventory().clear();
                                            }
                                        }),
                                new CommandAPICommand("load")
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
