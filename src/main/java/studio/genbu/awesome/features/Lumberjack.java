package studio.genbu.awesome.features;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import studio.genbu.awesome.utils.BlockUtil;
import studio.genbu.awesome.utils.ToolUtil;

public class Lumberjack {
  
  private Random random;
  private ConfigurationSection config;
  private Set<Material> logsVariations = new HashSet<Material>();
  private Set<Material> leafVariations = new HashSet<Material>();

  public Lumberjack(ConfigurationSection config, Random random) {
    // 引数から設定を読み込む
    this.config = config;
    this.random = random;
    
    // 設定から原木と葉の種類を取得
    for (String treeName: this.config.getConfigurationSection("trees").getKeys(false)) {
      logsVariations.add(Material.valueOf(treeName));
      leafVariations.add(
        Material.valueOf(
          config.getString(String.format("trees.%s.leaves", treeName))
        )
      );
    }
  }

  // 木を取得
  public Set<Block> getTree(Block block) {
    Set<Block> treeBlocks = new HashSet<Block>();

    // 原木ブロックである場合
    if (isLog(block)) {
      // 木の幹を取得
      Set<Block> stem = getStem(
        block,
        config.getInt(String.format("trees.%s.limit", block.getType().toString()))
      );

      Set<Block> leaves = new HashSet<Block>();

      // 木の幹に隣接する葉を取得
      for (Block stemBlock: stem) {
        leaves.addAll(getLeaves(stemBlock));
      }

      // 幹と葉が両方とも存在する場合、両方の Set を結合する
      if (!stem.isEmpty() && !leaves.isEmpty()) {
        treeBlocks = Stream.concat(stem.stream(), leaves.stream())
          .distinct()
          .collect(Collectors.toSet());
      }
    }

    return treeBlocks;
  }

  // 幹を取得
  private Set<Block> getStem(Block block, int limit) {
    Set<Block> stemBlocks = new HashSet<Block>();
    // 原木である場合
    if (isLog(block)) {
      // 最初の原木を追加
      stemBlocks.add(block);
      // 隣接する原木を再帰的に取得
      stemBlocks = getStem(block, stemBlocks, limit, 0);
    }
    return stemBlocks;
  }

  // 幹を取得
  private Set<Block> getStem(Block block, Set<Block> stemBlocks, int limit, int count) {
    // 制限に到達するまで取得
    if (count < limit) {
      count++;
      // 上斜めを含めた周囲のブロックを取得
      Set<Block> relativeBlocks = getNextBlocksWithDiagonal(block);
      // 取得したブロックが原木ブロックかどうか判定
      for (Block searchedBlock: relativeBlocks) {
        if (isLog(searchedBlock) && !stemBlocks.contains(searchedBlock)) {
          stemBlocks.add(searchedBlock);
          // 次のブロックを再帰的に取得
          getStem(searchedBlock, stemBlocks, limit, count);
        }
      }
    }
    return stemBlocks;
  }

  // 葉を取得
  private Set<Block> getLeaves(Block block) {
    Set<Block> leaves = new HashSet<Block>();
    // 原点が原木かどうか判定
    if (isLog(block)) {
      // 周辺のブロックを取得
      for (Block aroundBlock: getNextBlocks(block)) {
        if (isLeaves(aroundBlock) && aroundBlock.getType() == getLeavesType(block.getType())) {
          leaves.addAll(getLeaves(
            aroundBlock,
            leaves,
            config.getInt(
              String.format("trees.%s.leaves-size",
              block.getType().toString()
              )
            ),
            0
          ));
        }
      }
    }

    return leaves;
  }

  private Set<Block> getLeaves(Block block, Set<Block> leaves, int limit, int count) {
    count++;
    if (count < limit) {
      for (Block aroundBlock: getAroundBlocks(block, 1)) {
        if (isLeaves(aroundBlock) && block.getType() == aroundBlock.getType()) {
          leaves.add(aroundBlock);
          getLeaves(aroundBlock, leaves, limit, count);
        }
      }
    }
    return leaves;
  }

  private Material getLeavesType(Material material) {
    return Material.valueOf(config.getString(String.format("trees.%s.leaves", material.toString())));
  }

  public Set<Block> getNextBlocks(Block block) {
    Set<Block> blocks = new HashSet<Block>();

    blocks.add(block.getRelative(BlockFace.UP));
    blocks.add(block.getRelative(BlockFace.DOWN));
    blocks.add(block.getRelative(BlockFace.NORTH));
    blocks.add(block.getRelative(BlockFace.EAST));
    blocks.add(block.getRelative(BlockFace.SOUTH));
    blocks.add(block.getRelative(BlockFace.WEST));

    return blocks;
  }

  public void breakTreeByPlayer(Set<Block> tree, Player player) {
    ItemStack item = player.getInventory().getItemInMainHand();

    if (player.getGameMode() == GameMode.CREATIVE && isAxe(item)) {

      for (Block block: tree) {
        BlockUtil.breakBlock(block);
      }

    } else if (isAxe(item) && item.getItemMeta() instanceof Damageable) {

      Damageable tool = (Damageable)item.getItemMeta();
      int enchantLevel = item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY);

      for (Block block: tree) {
        if (isLog(block)) {
          int damage = ToolUtil.getDamageUnbreakableTool(random, enchantLevel);
          block.breakNaturally(item);
          tool.setDamage(tool.getDamage() + damage);
        } else {
          block.breakNaturally();
        }
      }

      if (ToolUtil.isDamageableItemBroken(item)) {
        player.spawnParticle(Particle.ITEM_CRACK, player.getLocation(), 1, item);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
      } else {
        item.setItemMeta((ItemMeta)tool);
      }

      player.updateInventory();
    }
  }

  public Set<Block> getNextBlocksWithDiagonal(Block block) {
    Set<Block> blocks = new HashSet<Block>();

    for (int x = -1; x <= 1; x++) {
      for (int y = 0; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          blocks.add(block.getRelative(x, y, z));
        }
      }
    }

    blocks.add(block.getRelative(BlockFace.DOWN));
    blocks.add(block.getRelative(BlockFace.NORTH));
    blocks.add(block.getRelative(BlockFace.EAST));
    blocks.add(block.getRelative(BlockFace.SOUTH));
    blocks.add(block.getRelative(BlockFace.WEST));

    return blocks;
  }

  public Set<Block> getAroundBlocks(Block block, int radius) {
    Set<Block> blocks = new HashSet<Block>();

    for (int x = radius * -1; x <= radius; x++) {
      for (int y = radius * -1; y <= radius; y++) {
        for (int z = radius * -1; z <= radius; z++) {
          blocks.add(block.getRelative(x, y, z));
        }
      }
    }

    return blocks;
  }

  public boolean isAxe(ItemStack item) {
    
    return config.getStringList("axes").contains(item.getType().toString());

  }

  public boolean isLeaves(Block block) {
    return leafVariations.contains(block.getType());
  }

  public boolean isLog(Block block) {
    return logsVariations.contains(block.getType());
  }

}
