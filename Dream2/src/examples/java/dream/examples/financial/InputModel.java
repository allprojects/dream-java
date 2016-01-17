package dream.examples.financial;

import java.util.Random;

import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class InputModel {
  private boolean serverStarted = false;
  private boolean lockManagerStarted = false;

  public static void main(String[] args) {
    new InputModel().start();
  }

  public void start() {
    startServerIfNeeded();
    startLockManagerIfNeeded();

    Consts.hostName = "InputModel";

    final Var<Integer> marketIndex = new Var<>("marketIndex", 1);
    final Var<Integer> stockOpts = new Var<>("stockOpts", 1);
    final Var<Integer> news = new Var<>("news", 1);

    final Random random = new Random();

    while (true) {
      marketIndex.set(random.nextInt(100));
      stockOpts.set(random.nextInt(100));
      news.set(random.nextInt(100));

      System.out.println("New values: " + marketIndex.get() + ", " + stockOpts.get() + ", " + news.get());

      try {
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private final void startServerIfNeeded() {
    if (!serverStarted) {
      ServerLauncher.start();
      serverStarted = true;
    }
    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }

  private final void startLockManagerIfNeeded() {
    if (!lockManagerStarted) {
      LockManagerLauncher.start();
      lockManagerStarted = true;
    }
    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }
}
