package studio.genbu.awesome.qualityoflife;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import studio.genbu.awesome.qualityoflife.events.BlockEvents;
import studio.genbu.awesome.qualityoflife.managers.CommandsManager;

public final class QualityOfLife extends JavaPlugin {

  private final String PLUGIN_NAME = "Quality of Life";

  public FileConfiguration config;

  /**
   * プラグインを有効化する際に呼び出されるメソッド.
   */
  @Override
  public void onEnable() {
    saveDefaultConfig();
    config = getConfig();

    this.getCommand("qualityoflife").setExecutor(new CommandsManager(this));
    getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
    getLogger().info(String.format("%s を有効化しました。", PLUGIN_NAME));
  }

  /**
   * プラグインを無効化する際に呼び出されるメソッド.
   */
  @Override
  public void onDisable() {
    getLogger().info(String.format("%s を無効化しました。", PLUGIN_NAME));
  }
  
}
