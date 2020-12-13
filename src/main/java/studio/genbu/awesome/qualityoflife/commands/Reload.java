package studio.genbu.awesome.qualityoflife.commands;

import studio.genbu.awesome.qualityoflife.QualityOfLife;

public class Reload {

  private QualityOfLife main;
  
  public Reload(QualityOfLife qualityOfLife) {
    this.main = qualityOfLife;
  }

  public void reloadPlugin() {
    main.reloadConfig();
  }

}
