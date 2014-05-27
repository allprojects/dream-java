package protopeer.util;

import java.io.*;
import java.util.*;

public class ClassPathTraversal {

	public static List<String> getClassNamesFromDirectory(List<String> classNamesFound, File directory,
			String packageName) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				getClassNamesFromDirectory(classNamesFound, file, packageName + "." + file.getName());
			} else {
				if (file.getName().endsWith(".class")) {
					StringBuffer className = new StringBuffer(file.getName());
					className.replace(className.lastIndexOf(".class"), className.length(), "");
					String name = packageName + "." + className.toString();
					classNamesFound.add(name);
				}
			}
		}
		return classNamesFound;
	}

	public static List<String> findSerializableClasses(List<String> classNames) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		LinkedList<String> serializableClassNames = new LinkedList<String>();
		for (String className : classNames) {			
			Class<?> clazz = LightweightSerialization.resolveClassForName(className);															
			if (Serializable.class.isAssignableFrom(clazz)) {				
				serializableClassNames.add(className);
			}
		}
		return serializableClassNames;
	}
}
