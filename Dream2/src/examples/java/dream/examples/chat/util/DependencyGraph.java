package dream.examples.chat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class DependencyGraph {

	private static File writeAndOpen(String data, String filenamePrefix, String filenameSuffix) {
		File file = null;
		try {
			file = File.createTempFile(filenamePrefix, filenameSuffix);

			FileOutputStream fos = new java.io.FileOutputStream(file);
			fos.write(data.getBytes("UTF-8"));
			fos.close();
			java.awt.Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	private static String toDot(Map<String, Collection<String>> nodes) {
		return toDot(nodes, "forward", "1.0");
	}

	private static String toDot(Map<String, Collection<String>> nodes, String dir, String ranksep) {
		String s = "digraph G {\n\tdir=" + dir + ";\n\tranksep=" + ranksep + ";\n";
		for (Entry<String, Collection<String>> e : nodes.entrySet()) {
			for (String e2 : e.getValue()) {
				s += "\t\"" + e.getKey() + "\" -> \"" + e2 + "\" [dir=" + dir + "];\n";
			}
		}
		s += "}";
		return s;
	}

	public static void show() {
		Map<String, Collection<String>> graph = dream.common.utils.DependencyGraph.instance.getGraph();
		System.out.println(graph);
		writeAndOpen(toDot(graph), "temp", ".dot");
	}
}
