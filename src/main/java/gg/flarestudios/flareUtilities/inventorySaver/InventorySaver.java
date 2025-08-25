package gg.flarestudios.flareUtilities.inventorySaver;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class InventorySaver {

    //Defining the folders
    private final File playerDataFolder;
    private final File backupFolder;

    public InventorySaver(){
        // Init
        this.playerDataFolder = new File("plugins/FlareUtilities/Modules/InventorySaver/playerdata");
        this.backupFolder = new File(playerDataFolder, "backups");

        if (!playerDataFolder.exists() && !playerDataFolder.mkdirs()){
            PaperPluginLogger.getLogger("FlareUtilities").severe("Failed to create data folder!");
        }

        if (!backupFolder.exists() && !backupFolder.mkdirs()){
            PaperPluginLogger.getLogger("FlareUtilities").severe("Failed to create backup folder!");
        }
    }

    public void saveInventory(Player player){
        File file = getFile(player);

        if (file.exists()){
            createVersionedBackup(player, file);
        }
        YamlConfiguration configuration = new YamlConfiguration();

        configuration.set("Player", player.getName());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        configuration.set("Modified on", time);
        ItemStack[] armor = player.getInventory().getArmorContents();
        configuration.set("Head", armor.length > 3 ? armor[3] : null);
        configuration.set("Chest", armor.length > 2 ? armor[3] : null);
        configuration.set("Legs", armor.length > 1 ? armor[3] : null);
        configuration.set("Feet", armor.length > 0 ? armor[3] : null);

        configuration.set("Inventory", player.getInventory().getContents());
        configuration.set("EnderChest", player.getEnderChest().getContents());

        try {
            configuration.save(file);
        } catch (IOException e) {
            PaperPluginLogger.getLogger("FlareUtilities").severe("Failed to safe inventory for " + player.getName() + "!");
        }
    }

    private void createVersionedBackup(Player player, File file){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File(backupFolder,player.getUniqueId() + "/" + date);
        if (!dateFolder.exists() && !dateFolder.mkdirs()){
            PaperPluginLogger.getLogger("FlareUtilities").severe("Failed to create inventory backup folder for " + player.getName() + "!");
        }
        int nextNumber = 1;
        while (new File(dateFolder, nextNumber + ".yml").exists()){
            nextNumber++;
    }
        File backupFile = new File(dateFolder, nextNumber + ".yml");
        try {
            Files.copy(file.toPath(), backupFile.toPath());
            PaperPluginLogger.getLogger("FlareUtilities").info("Created inventory backup for " + player.getName() + "!");
        } catch (IOException e) {
            PaperPluginLogger.getLogger("FlareUtilities").severe("Failed to create inventory backup for " + player.getName() + "!");
        }
    }

    private File getFile(Player player) {
        return new File(playerDataFolder, player.getUniqueId() + ".yml");
    }

    private YamlConfiguration getConfig(File file){
        if (!file.exists()){
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }
    public boolean hasSave(Player player) {
        return getFile(player).exists();
    }

    public void loadArmor(Player player){
        YamlConfiguration configuration = getConfig(getFile(player));
        if (configuration == null) return;

        player.getInventory().setHelmet(configuration.getItemStack("Head"));
        player.getInventory().setChestplate(configuration.getItemStack("Chest"));
        player.getInventory().setLeggings(configuration.getItemStack("Legs"));
        player.getInventory().setBoots(configuration.getItemStack("Feet"));
    }
    @SuppressWarnings("unchecked")
    public void loadInventory(Player player){
        YamlConfiguration configuration = getConfig(getFile(player));
        if (configuration == null) return;

        List<ItemStack> invList = (List<ItemStack>) configuration.get("Inventory");
        if (invList != null) {
            player.getInventory().setContents(invList.toArray(new ItemStack[0]));
        }
    }
    @SuppressWarnings("unchecked")
    public void loadEnderChest(Player player){
        YamlConfiguration configuration = getConfig(getFile(player));
        if (configuration == null) return;

        List<ItemStack> enderList = (List<ItemStack>) configuration.get("Inventory");
        if (enderList != null) {
            player.getInventory().setContents(enderList.toArray(new ItemStack[0]));
        }
    }
    public void loadAll(Player player){
        if (!hasSave(player)){
            PaperPluginLogger.getLogger("FlareUtilities").severe("Tried to load inventory data for " + player.getName() + ", but the player has no saved data!");
            return;
        }
        loadArmor(player);
        loadInventory(player);
        loadEnderChest(player);
    }

    public void restoreFromLastBackup(Player player) {
        File latestBackup = getLatestBackup(player);
        if (latestBackup == null) {
            PaperPluginLogger.getLogger("FlareUtilities").severe("No backup found for " + player.getName() + "!");
            return;
        }
        YamlConfiguration configuration = getConfig(latestBackup);
        if (configuration == null) return;

        player.getInventory().setHelmet(configuration.getItemStack("Head"));
        player.getInventory().setChestplate(configuration.getItemStack("Chest"));
        player.getInventory().setLeggings(configuration.getItemStack("Legs"));
        player.getInventory().setBoots(configuration.getItemStack("Feet"));

        @SuppressWarnings("unchecked")
        List<ItemStack> invList = (List<ItemStack>) configuration.get("Inventory");
        if (invList != null) {
            player.getInventory().setContents(invList.toArray(new ItemStack[0]));
        }

        @SuppressWarnings("unchecked")
        List<ItemStack> enderList = (List<ItemStack>) configuration.get("Inventory");
        if (enderList != null) {
            player.getInventory().setContents(enderList.toArray(new ItemStack[0]));
        }
        PaperPluginLogger.getLogger("FlareUtilities").info("Latest backup restored for " + player.getName() + "!");
    }

    private File getLatestBackup(Player player){
        File uuidFolder = new File(backupFolder, player.getUniqueId().toString());
        if (!uuidFolder.exists() || !uuidFolder.isDirectory()) return null;

        File[] dateFolders = uuidFolder.listFiles(File::isDirectory);
        if (dateFolders == null || dateFolders.length == 0) return null;

        Arrays.sort(dateFolders, Comparator.comparing(File::getName).reversed());

        for (File dateFolder : dateFolders){
            File[] backups = dateFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
            if (backups != null && backups.length > 0){
                Arrays.sort(backups, Comparator.comparing(File::getName).reversed());
                return backups[0];
            }
        }
        return null;
    }
}