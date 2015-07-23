package javareact.financial;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javareact.common.Consts;
import javareact.common.types.Var;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

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
