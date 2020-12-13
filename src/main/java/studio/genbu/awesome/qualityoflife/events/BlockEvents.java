package studio.genbu.awesome.qualityoflife.events;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import studio.genbu.awesome.qualityoflife.QualityOfLife;
import studio.genbu.awesome.qualityoflife.features.Farmer;
import studio.genbu.awesome.qualityoflife.features.Lumberjack;

public class BlockEvents implements Listener {

  private QualityOfLife main;
  private Lumberjack lumberjack;
  private Farmer farmer;
  private FileConfiguration config;
  private Random random;
  
  public BlockEvents(QualityOfLife main) {
    this.main = main;
    this.config = main.getConfig();
    this.random = new Random();
    lumberjack = new Lumberjack(main.getConfig(), this.random);
    farmer = new Farmer(main, this.config);
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
      Set<Block> stem = lumberjack.getStem(
          block, config.getInt(String.format("trees.%s.limit", block.getType().toString()))
      );
      if (!stem.isEmpty()) {
        Set<Block> leaves = new HashSet<Block>();

        for (Block stemBlock: stem) {
          leaves.addAll(lumberjack.getLeaves(stemBlock));
        }
        
        if (!leaves.isEmpty()) {
          lumberjack.breakTreeByPlayer(stem, leaves, event);
        }

      }
    }

    if (
        farmer.isPlant(block)
        && !player.isSneaking()
        && player.getGameMode() != GameMode.CREATIVE
    ) {
      farmer.replant(block);
    }

  }
}
