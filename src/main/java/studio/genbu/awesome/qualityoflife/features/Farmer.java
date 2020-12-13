package studio.genbu.awesome.qualityoflife.features;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import studio.genbu.awesome.qualityoflife.QualityOfLife;

public class Farmer {

  private QualityOfLife main;
  private ConfigurationSection config;
  private Set<Material> plantVariations = new HashSet<Material>();
  
  public Farmer(QualityOfLife qualityOfLife, ConfigurationSection config) {
    // 引数から設定を読み込む
    this.main = qualityOfLife;
    this.config = config;
    
    // 設定から原木と葉の種類を取得
    for (String plantName: this.config.getStringList("plants")) {
      plantVariations.add(Material.valueOf(plantName));
    }
  }

  public void replant(Block block) {

    BlockState blockState = block.getState();
    BlockData blockData = blockState.getBlockData();

    if (blockData instanceof Ageable) {
      Ageable ageable = (Ageable)blockData;

      if(ageable.getAge() >= ageable.getMaximumAge()) {
        
        Material material = block.getType();

        new BukkitRunnable(){
          @Override
          public void run() {
            if (
                block.getType() == Material.AIR
                && block.getRelative(BlockFace.DOWN).getBlockData() instanceof Farmland
            ) {
              block.setType(material);
            }
          }
        }.runTaskLater(main, 20);
      }
    }
  }

  public boolean isPlant(Block block) {
    return plantVariations.contains(block.getType());
  }

}
