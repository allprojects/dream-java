package dream.examples.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DependencyVisualization {

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

	private static Map<String, Set<String>> addHostVar(Map<String, Set<String>> map, String hv) {
		String[] temp = hv.split("@", 2);
		String var = temp[0];
		String host = temp[1];
		if (!map.containsKey(host))
			map.put(host, new HashSet<String>());
		map.get(host).add(var);
		return map;
	}

	private static String toDot(Map<String, Collection<String>> nodes, String dir, String ranksep) {
		String s = "digraph G {\n\tdir=" + dir + ";\n\tranksep=" + ranksep + ";\n";
		Map<String, Set<String>> hostVars = new HashMap<>();
		for (String str : nodes.keySet()) {
			addHostVar(hostVars, str);
			for (String str2 : nodes.get(str)) {
				addHostVar(hostVars, str2);
			}
		}
		for (String host : hostVars.keySet()) {
			s += "subgraph cluster_" + host + " {\n\tnode [style=filled];\n";
			for (String var : hostVars.get(host)) {
				s += "\t\"" + var + "@" + host + "\";\n";
			}
			s += "\tlabel = \"" + host + "\";\n\tcolor=blue\n}\n\n";
		}
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
