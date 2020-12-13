package studio.genbu.awesome.qualityoflife.utils;

import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


public class ToolUtil {
  
  public static int getDamageUnbreakableTool(Random random, int level) {
    int rand = random.nextInt(101);

    if (rand <= 100 / (level + 1)) {
      return 1;
    }
    return 0;
  }

  public static boolean isDamageableItemBroken(ItemStack item) {

    ItemMeta itemMeta = item.getItemMeta();

    if (itemMeta instanceof Damageable) {
      
      Damageable damageableItem = (Damageable)itemMeta;

      if (damageableItem.getDamage() > item.getType().getMaxDurability()) {
        return true;
      }

    }
    return false;
  }

}
