package studio.genbu.awesome.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
public class BlockUtil {
  
  public static void breakBlock(Block block) {
    block.setType(Material.AIR);
    block.getState().update();
  }

}
