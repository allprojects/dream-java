package dream.financial;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import dream.common.Consts;
import dream.common.datatypes.Var;
import dream.locking.TokenServiceLauncher;
import dream.server.ServerLauncher;

public class InputModel {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;
	
	public static void main(String[] args) {
		new InputModel().start();
	}
	
	public void start() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();
		
		Consts.hostName = "InputModel";
    	
		Var<Integer> marketIndex = new Var<>("marketIndex", 1);
    	Var<Integer> stockOpts = new Var<>("stockOpts", 1);
    	Var<Integer> news = new Var<>("news", 1);
    
    	Random random = new Random();

	    while (true) {
	      marketIndex.set(random.nextInt(100));
	      stockOpts.set(random.nextInt(100));
	      news.set(random.nextInt(100));
	      
	      System.out.println("New values: " + marketIndex.get() + ", " + stockOpts.get() + ", " + news.get());

	      try {
	        Thread.sleep(1000);
	      } catch (InterruptedException e) {
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final void startTokenServiceIfNeeded() {
		if (!tokenServiceStarted) {
			String serverAddress = "reds-tcp:localhost:9000";
			Set<String> addresses = new HashSet<String>();
			addresses.add(serverAddress);
			TokenServiceLauncher.start(addresses);
			tokenServiceStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
