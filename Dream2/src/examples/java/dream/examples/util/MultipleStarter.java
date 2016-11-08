package dream.examples.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to start multiple classes in their own vm and ensure that all
 * are closed properly afterwards.
 * 
 * @author Tobias Becker
 */
public class MultipleStarter {

	private static final LinkedList<Pair<Class<?>, String[]>> queue = new LinkedList<>();
	private static List<Process> processes;

	public static void main(String[] args) {
		if (args.length < 1)
			throw new UnsupportedOperationException("You are not supposed to run this Class directly");
		LinkedList<Pair<Class<?>, String[]>> q = queueFromString(args[0]);
		processes = new ArrayList<>();
		for (Pair<Class<?>, String[]> p : q) {
			processes.add(NewJvmHelper.startNewJVM(p.getFirst(), p.getSecond()));
		}
		sleep(-1);
	}

	public static void addStartQueue(Class<?> c, String... args) {
		queue.add(new Pair<>(c, args));
	}

	public static void start() {
		NewJvmHelper.startNewJVM(MultipleStarter.class, queueToString());
	}

	private static void sleep(int time) {
		do {
			try {
				Thread.sleep(time == -1 ? 1000 : time);
				checkExit();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (time == -1);

	}

	private static void checkExit() {
		for (Process p : processes) {
			if (!p.isAlive()) {
				System.out.println(p.getClass().getSimpleName() + " closed ... exiting!");
				destr();
				System.exit(0);
			}
		}
	}

	private static void destr() {
		for (Process p : processes) {
			p.destroyForcibly();
		}
	}

	private static String queueToString() {
		String s = "";
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(queue);
			so.flush();
			s = new String(Base64.getEncoder().encode(bo.toByteArray()));
		} catch (Exception e) {
			System.err.println(e);
		}
		return s;
	}

	private static LinkedList<Pair<Class<?>, String[]>> queueFromString(String s) {
		try {
			byte b[] = Base64.getDecoder().decode(s.getBytes());
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			LinkedList<Pair<Class<?>, String[]>> obj = (LinkedList<Pair<Class<?>, String[]>>) si.readObject();
			return obj;
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}
}
