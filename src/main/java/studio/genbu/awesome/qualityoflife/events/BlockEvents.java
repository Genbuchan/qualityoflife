package studio.genbu.awesome.qualityoflife.events;

import java.util.Random;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import studio.genbu.awesome.features.Farmer;
import studio.genbu.awesome.features.Lumberjack;
import studio.genbu.awesome.qualityoflife.QualityOfLife;

public class BlockEvents implements Listener {

  // private QualityOfLife main;
  private Lumberjack lumberjack;
  private Farmer farmer;
  private Random random;
  
  public BlockEvents(QualityOfLife main) {
    // this.main = main;
    this.random = new Random();
    lumberjack = new Lumberjack(main.getConfig(), this.random);
    farmer = new Farmer(main, main.getConfig());
  }

  /**
   * ブロックを破壊した際に呼ばれるイベント.
   * @param event BlockBreakEvent
   */
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();

    if (lumberjack.isLog(block) && !player.isSneaking()) {
      Set<Block> tree = lumberjack.getTree(block);
      if (!tree.isEmpty()) {
        event.setCancelled(true);
        lumberjack.breakTreeByPlayer(tree, player);
      }
    }

    if (
        farmer.isPlant(block) &&
        !player.isSneaking() &&
        player.getGameMode() != GameMode.CREATIVE
    ) {
      farmer.replant(block);
    }

  }
}
