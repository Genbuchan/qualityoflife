package studio.genbu.awesome.qualityoflife.commands;

import org.bukkit.command.CommandSender;

import studio.genbu.awesome.qualityoflife.QualityOfLife;

public class Reload {

  private QualityOfLife main;
  
  public Reload(QualityOfLife qualityOfLife) {
    this.main = qualityOfLife;
  }

  public boolean reloadPlugin(CommandSender sender) {
    if (sender.hasPermission("qualityoflife.commands.reload")) {
      main.reloadConfig();
      return true;
    }
    return false;
  }

}
