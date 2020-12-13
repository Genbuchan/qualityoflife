package studio.genbu.awesome.qualityoflife.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import studio.genbu.awesome.qualityoflife.QualityOfLife;
import studio.genbu.awesome.qualityoflife.commands.Reload;

public class CommandsManager implements CommandExecutor {

  private QualityOfLife main;
  private Reload reload;

  public CommandsManager(QualityOfLife qualityOfLife) {
    this.main = qualityOfLife;
    this.reload = new Reload(this.main);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    String[] defaultMessage = {
      "§a§l============ Quality of Life ============",
      " §a/qualifyoflife reload - プラグインを再読み込み"
    };

    if (args.length != 0) {
      switch (args[0]) {
        case "reload":
          reload.reloadPlugin();
          sender.sendMessage("§aQualify of Life の設定ファイルを再読み込みしました。");
          return true;
        default:
          break;
      }
    }
    
    sender.sendMessage(defaultMessage);
    return true;

  }

}
