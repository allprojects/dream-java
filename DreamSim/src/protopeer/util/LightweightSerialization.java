package protopeer.util;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;

class TestClass2 implements Serializable {
	boolean booleanField;
}

class TestClass implements Serializable {
	public int intField;

	public TestClass2 t2Field;

	public transient byte byteField;
}

public class LightweightSerialization {

	public enum ClassIDType {
		BYTE, SHORT, INT, LONG
	};

	private static final Logger logger = Logger.getLogger(LightweightSerialization.class);

	private HashMap<Class<?>, Number> class2classID;

	private HashMap<Number, Class<?>> classID2class;

	private static LightweightSerialization singleton = new LightweightSerialization(ClassIDType.SHORT);

	private ClassIDType classIDType;

	public LightweightSerialization(ClassIDType classIDType) {
		this.classIDType = classIDType;
	}

	private Number createNewClassIDInstanceFromLong(long value) {
		switch (classIDType) {
		case BYTE:
			return new Byte((byte) value);
		case SHORT:
			return new Short((short) value);
		case INT:
			return new Integer((int) value);
		case LONG:
			return new Long((long) value);
		}
		return null;
	}

	private void writeObjectType(DataOutputStream out, Class<?> clazz) throws IOException {
		if (clazz == null) {
			// write the null object type
			writeClassID(out, 0);
		} else {
			Number classID = getClassID(clazz);
			if (classID == null) {
				throw new LightweightSerializationException("Class ID not known for class " + clazz.getCanonicalName());
			}
			writeClassID(out, classID);
		}
	}

	private Class<?> readObjectType(DataInputStream in) throws IOException {
		Number classID = readClassID(in);
		// check for null classID
		if (classID.longValue() == 0) {
			return null;
		} else {
			Class<?> clazz = getClass(classID);
			if (clazz == null) {
				throw new LightweightSerializationException("Class ID " + classID.longValue() + " not recognized");
			}
			return clazz;
		}
	}

	private void writeClassID(DataOutputStream out, Number number) throws IOException {
		switch (classIDType) {
		case BYTE:
			out.writeByte(number.byteValue());
			break;
		case SHORT:
			out.writeShort(number.shortValue());
			break;
		case INT:
			out.writeInt(number.intValue());
			break;
		case LONG:
			out.writeLong(number.longValue());
			break;
		}
	}

	private Number readClassID(DataInputStream in) throws IOException {
		switch (classIDType) {
		case BYTE:
			return in.readByte();
		case SHORT:
			return in.readShort();
		case INT:
			return in.readInt();
		case LONG:
			return in.readLong();
		}
		return null;
	}

	public void loadMapFromFile(String filename) {
		try {
			class2classID = new HashMap<Class<?>, Number>();
			classID2class = new HashMap<Number, Class<?>>();
			LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(filename)));
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				StringTokenizer tokenizer = new StringTokenizer(line, "\t");
				long classID = Long.parseLong(tokenizer.nextToken());
				String className = tokenizer.nextToken();
				Class<?> clazz = null;
				try {
					clazz = resolveClassForName(className);
				} catch (ClassNotFoundException e) {
				}
				if (clazz != null) {
					Number classIDObject = createNewClassIDInstanceFromLong(classID);
					class2classID.put(clazz, classIDObject);
					classID2class.put(classIDObject, clazz);
				}
			}
		} catch (Exception e) {
			throw new LightweightSerializationException(e);
		}
	}

	private Class<?> getClass(Object id) {
		return classID2class.get(id);
	}

	private Number getClassID(Class<?> clazz) {
		return class2classID.get(clazz);
	}

	private static List<String> getClassNamesForBuiltinTypes() {
		LinkedList<String> classNames = new LinkedList<String>();
		classNames.add(Boolean.class.getName());
		classNames.add(Byte.class.getName());
		classNames.add(Short.class.getName());
		classNames.add(Integer.class.getName());
		classNames.add(Long.class.getName());
		classNames.add(Float.class.getName());
		classNames.add(Double.class.getName());
		classNames.add(Character.class.getName());
		classNames.add(String.class.getName());
		classNames.add(boolean.class.getName());
		classNames.add(byte.class.getName());
		classNames.add(short.class.getName());
		classNames.add(int.class.getName());
		classNames.add(long.class.getName());
		classNames.add(float.class.getName());
		classNames.add(double.class.getName());
		classNames.add(char.class.getName());
		classNames.add(java.lang.Class.class.getName());
		classNames.add(java.util.HashSet.class.getName());
		classNames.add(java.util.HashMap.class.getName());
		classNames.add(java.util.Vector.class.getName());
		classNames.add(java.util.LinkedList.class.getName());
		classNames.add(java.util.TreeSet.class.getName());
		classNames.add(java.util.TreeMap.class.getName());
		classNames.add(java.net.InetSocketAddress.class.getName());
		classNames.add(java.net.Inet4Address.class.getName());
		return classNames;
	}

	private static List<String> getClassNamesForPackage(String packageDirectory, String packageName)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		File startDir = new File(packageDirectory);
		LinkedList<String> classNames = new LinkedList<String>();
		ClassPathTraversal.findSerializableClasses(ClassPathTraversal.getClassNamesFromDirectory(classNames, startDir,
				packageName));
		return classNames;
	}

	private static void generateTypeMap(String mapFilename, List<String> classNames) throws FileNotFoundException {
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(mapFilename));
			long classID = 1;
			for (String className : classNames) {
				out.println(classID + "\t" + className);
				classID++;
			}
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static LightweightSerialization getSingleton() {
		return singleton;
	}

	private Object getWrappedInstaceOfPrimitiveType(Class<?> clazz) {
		if (clazz == byte.class) {
			return new Byte((byte) 0);
		} else if (clazz == short.class) {
			return new Short((short) 0);
		} else if (clazz == char.class) {
			return new Character('\0');
		} else if (clazz == int.class) {
			return new Integer(0);
		} else if (clazz == long.class) {
			return new Long(0);
		} else if (clazz == float.class) {
			return new Float(0.0);
		} else if (clazz == double.class) {
			return new Double(0.0);
		} else if (clazz == boolean.class) {
			return new Boolean(false);
		}
		return null;
	}

	private Object newInstanceBestEffort(Class<?> clazz) {
		Object out = null;
		try {
			out = clazz.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		if (out == null) {
			Constructor<?> foundConstructor = null;
			// try to find the nullary constructor
			for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
				constructor.setAccessible(true);
				foundConstructor = constructor;
				if (constructor.getParameterTypes().length == 0) {
					break;
				}
			}
			try {
				// call the found Constructor with null or zero args and zeros
				// for primitive types
				Class<?>[] paramTypes = foundConstructor.getParameterTypes();
				Object[] params = new Object[paramTypes.length];
				for (int i = 0; i < params.length; i++) {
					if (paramTypes[i].isPrimitive()) {
						params[i] = getWrappedInstaceOfPrimitiveType(paramTypes[i]);
					}
				}
				foundConstructor.setAccessible(true);
				out = foundConstructor.newInstance(params);
			} catch (Exception e) {
				throw new LightweightSerializationException("Best effort instantiation failed for: "
						+ clazz.getCanonicalName(), e);
			}
		}
		return out;
	}

	public void serializeObject(DataOutputStream out, Object object) throws IOException {
		writeObjectType(out, object == null ? null : object.getClass());
		if (object != null) {
			serializeObject(out, object, object.getClass());
		}
	}

	public Object deserializeObject(DataInputStream in) throws IOException {
		Class<?> clazz = readObjectType(in);
		if (clazz == null) {
			return null;
		} else {
			return deserializeObject(in, clazz);
		}
	}

	public void serializeMap(DataOutputStream out, Map<?, ?> map) throws IOException {
		out.writeInt(map.size());
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			serializeObject(out, entry.getKey());
			serializeObject(out, entry.getValue());
		}
	}

	public void deserializeMap(DataInputStream in, Map<?, ?> map) throws IOException {
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			Object key = deserializeObject(in);
			Object value = deserializeObject(in);
			((Map) map).put(key, value);
		}
	}

	public void serializeCollection(DataOutputStream out, Collection<?> collection) throws IOException {
		out.writeInt(collection.size());
		for (Object object : collection) {
			serializeObject(out, object);
		}

	}

	public void deserializeCollection(DataInputStream in, Collection<?> collection) throws IOException {
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			((Collection) collection).add(deserializeObject(in));
		}
	}

	public void serializeCollection(DataOutputStream out, Collection<?> collection, Class<?> elementType)
			throws IOException {
		out.writeInt(collection.size());
		for (Object object : collection) {
			serializeObject(out, object, elementType);
		}
	}

	public void deserializeCollection(DataInputStream in, Collection<?> collection, Class<?> elementType)
			throws IOException {
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			((Collection) collection).add(deserializeObject(in, elementType));
		}
	}

	public void serializeArray(DataOutputStream out, Object array) throws IOException {
		if (!array.getClass().isArray())
			throw new LightweightSerializationException("attempting to serialize non-array with serializeArray");
		writeObjectType(out, array.getClass().getComponentType());
		out.writeInt(Array.getLength(array));
		for (int i = 0; i < Array.getLength(array); i++) {
			serializeObject(out, Array.get(array, i));
		}
	}

	public Object deserializeArray(DataInputStream in) throws IOException {
		Class<?> componentType = readObjectType(in);
		int size = in.readInt();
		Object out = Array.newInstance(componentType, size);
		for (int i = 0; i < size; i++) {
			Array.set(out, i, deserializeObject(in));
		}
		return out;
	}

	public void serializeArray(DataOutputStream out, Object array, Class<?> elementType) throws IOException {
		if (!array.getClass().isArray())
			throw new LightweightSerializationException("attempting to serialize non-array with serializeArray");
		writeObjectType(out, array.getClass().getComponentType());
		out.writeInt(Array.getLength(array));
		for (int i = 0; i < Array.getLength(array); i++) {
			serializeObject(out, Array.get(array, i), elementType);
		}
	}

	public Object deserializeArray(DataInputStream in, Class<?> elementType) throws IOException {
		Class<?> componentType = readObjectType(in);
		int size = in.readInt();
		Object out = Array.newInstance(componentType, size);
		for (int i = 0; i < size; i++) {
			Array.set(out, i, deserializeObject(in, elementType));
		}
		return out;
	}

	public void serializeObject(DataOutputStream out, Object object, Class<?> objectType) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("serializeObject, objectType: " + objectType.getCanonicalName());
		}
		try {
			if (!objectType.isPrimitive()) {
				if (object == null) {
					out.writeBoolean(false);
					return;
				}
				out.writeBoolean(true);
			}

			if (object instanceof LightweightSerializable) {
				// check if the Object defines its own lightweight serialization
				((LightweightSerializable) object).serialize((LightweightObjectOutputStream) out);
			} else if (objectType == Byte.class || objectType == byte.class) {
				out.writeByte(((Byte) object).byteValue());
			} else if (objectType == Short.class || objectType == short.class) {
				out.writeShort(((Short) object).shortValue());
			} else if (objectType == Character.class || objectType == char.class) {
				out.writeChar(((Character) object).charValue());
			} else if (objectType == Integer.class || objectType == int.class) {
				out.writeInt(((Integer) object).intValue());
			} else if (objectType == Long.class || objectType == long.class) {
				out.writeLong(((Long) object).longValue());
			} else if (objectType == Float.class || objectType == float.class) {
				out.writeFloat(((Float) object).floatValue());
			} else if (objectType == Double.class || objectType == double.class) {
				out.writeDouble(((Double) object).doubleValue());
			} else if (objectType == Boolean.class || objectType == boolean.class) {
				out.writeBoolean(((Boolean) object).booleanValue());
			} else if (objectType == String.class) {
				out.writeUTF((String) object);
			} else if (objectType.isEnum()) {
				out.writeInt(((Enum<?>) object).ordinal());
			} else if (objectType.isArray()) {
				serializeArray(out, object);
			} else if (Collection.class.isAssignableFrom(objectType)) {
				serializeCollection(out, (Collection<?>) object);
			} else if (Map.class.isAssignableFrom(objectType)) {
				serializeMap(out, (Map<?, ?>) object);
			} else if (Class.class.isAssignableFrom(objectType)) {
				out.writeUTF(((Class<?>) object).getCanonicalName());
			} else {
				if (objectType.isPrimitive()) {
					throw new LightweightSerializationException("we missed some primitive type serialization: "
							+ objectType.getCanonicalName());
				} else if (objectType.isArray()) {
					throw new LightweightSerializationException(
							"Objects containing arrays must implement their own lightweight serialization.");
				} else {
					while (objectType != Object.class) {
						for (Field field : objectType.getDeclaredFields()) {
							if ((field.getModifiers() & Modifier.TRANSIENT) == 0
									&& (field.getModifiers() & Modifier.STATIC) == 0) {
								field.setAccessible(true);
								if (field.getType().isEnum() || field.getType().isPrimitive()
										|| field.getType().isArray()
										|| Collection.class.isAssignableFrom(field.getType())
										|| Map.class.isAssignableFrom(field.getType())
										|| Class.class.isAssignableFrom(field.getType())) {
									serializeObject(out, field.get(object), field.getType());
								} else {
									serializeObject(out, field.get(object));
								}
							}
						}
						objectType = objectType.getSuperclass();
					}
				}
			}
		} catch (SecurityException e) {
			throw new LightweightSerializationException(e);
		} catch (IllegalArgumentException e) {
			throw new LightweightSerializationException(e);
		} catch (IllegalAccessException e) {
			throw new LightweightSerializationException(e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("serializeObject, serialization OK, " + objectType.getCanonicalName());
		}
	}

	public Object deserializeObject(DataInputStream in, Class<?> objectType) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("deserializeObject, objectType: " + objectType.getCanonicalName());
		}

		if (!objectType.isPrimitive()) {
			// check the null object flag
			if (!in.readBoolean()) {
				return null;
			}
		}

		Object out = null;
		try {
			// check if class has custom lightweight serialization
			if (LightweightSerializable.class.isAssignableFrom(objectType)) {
				out = newInstanceBestEffort(objectType);
				if (out == null) {
					logger.error("Failed to create the object instance");
				}
				((LightweightSerializable) out).deserialize((LightweightObjectInputStream) in);
			} else if (objectType == Byte.class || objectType == byte.class) {
				out = new Byte(in.readByte());
			} else if (objectType == Short.class || objectType == short.class) {
				out = new Short(in.readShort());
			} else if (objectType == Character.class || objectType == char.class) {
				out = new Character(in.readChar());
			} else if (objectType == Integer.class || objectType == int.class) {
				out = new Integer(in.readInt());
			} else if (objectType == Long.class || objectType == long.class) {
				out = new Long(in.readLong());
			} else if (objectType == Float.class || objectType == float.class) {
				out = new Float(in.readFloat());
			} else if (objectType == Double.class || objectType == double.class) {
				out = new Double(in.readDouble());
			} else if (objectType == Boolean.class || objectType == boolean.class) {
				out = new Boolean(in.readBoolean());
			} else if (objectType == String.class) {
				out = in.readUTF();
			} else if (objectType.isEnum()) {
				int ordinal = in.readInt();
				out = Array.get(objectType.getMethod("values").invoke(null), ordinal);
			} else if (objectType.isArray()) {
				out = deserializeArray(in);
			} else if (Collection.class.isAssignableFrom(objectType)) {
				out = newInstanceBestEffort(objectType);
				deserializeCollection(in, (Collection<?>) out);
			} else if (Map.class.isAssignableFrom(objectType)) {
				out = newInstanceBestEffort(objectType);
				deserializeMap(in, (Map<?, ?>) out);
			} else if (Class.class.isAssignableFrom(objectType)) {
				String className = in.readUTF();
				out = resolveClassForName(className);
			} else {
				if (objectType.isPrimitive()) {
					throw new LightweightSerializationException("Shouldn't happen, primitive class: "
							+ objectType.getCanonicalName());
				} else {
					out = newInstanceBestEffort(objectType);
					while (objectType != Object.class) {
						for (Field field : objectType.getDeclaredFields()) {
							field.setAccessible(true);
							if ((field.getModifiers() & Modifier.TRANSIENT) == 0
									&& (field.getModifiers() & Modifier.STATIC) == 0) {
								Object fieldValue;
								if (field.getType().isEnum() || field.getType().isArray()
										|| field.getType().isPrimitive()
										|| Collection.class.isAssignableFrom(field.getType())
										|| Map.class.isAssignableFrom(field.getType())
										|| Class.class.isAssignableFrom(field.getType())) {
									fieldValue = deserializeObject(in, field.getType());
								} else {
									fieldValue = deserializeObject(in);
								}
								field.set(out, fieldValue);
							}
						}
						objectType = objectType.getSuperclass();
					}
				}
			}
		} catch (SecurityException e) {
			throw new LightweightSerializationException(e);
		} catch (IllegalArgumentException e) {
			throw new LightweightSerializationException(e);
		} catch (IllegalAccessException e) {
			throw new LightweightSerializationException(e);
		} catch (InvocationTargetException e) {
			throw new LightweightSerializationException(e);
		} catch (NoSuchMethodException e) {
			throw new LightweightSerializationException(e);
		} catch (ClassNotFoundException e) {
			throw new LightweightSerializationException(e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("deserializeObject, deserialization OK, " + objectType.getCanonicalName());
		}
		return out;
	}

	public byte[] serializeObject(Object object) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1);
		DataOutputStream out = new LightweightObjectOutputStream(byteOut);
		try {
			serializeObject(out, object);
			out.close();
			byteOut.close();
		} catch (IOException e) {
			logger.error(e);
		}
		return byteOut.toByteArray();
	}

	public Object deserializeObject(byte[] bytes) throws IOException {
		DataInputStream in = new LightweightObjectInputStream(new ByteArrayInputStream(bytes));
		return deserializeObject(in);
	}

	public static void generateTypeMap() {
		try {
			List<String> classNames = LightweightSerialization.getClassNamesForBuiltinTypes();
			/*classNames.addAll(LightweightSerialization.getClassNamesForPackage(
					"c:\\_home\\jbuilder_workspace\\protopeer\\bin\\protopeer", "protopeer"));
			classNames.addAll(LightweightSerialization.getClassNamesForPackage(
					"c:\\_home\\jbuilder_workspace\\protopeer_apps\\bin\\protopeer", "protopeer"));
			LightweightSerialization.generateTypeMap("conf\\LightweightSerialization.map", classNames);*/
			classNames.addAll(LightweightSerialization.getClassNamesForPackage(
					"/home/julien/workspace/GloveU/bin/protopeer", "protopeer"));
			classNames.addAll(LightweightSerialization.getClassNamesForPackage(
					"/home/julien/workspace/GloveU/bin/gloveu", "gloveu"));
			LightweightSerialization.generateTypeMap("conf/LightweightSerialization.map", classNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testLightweightSerialization() throws Exception {
		TestClass test = new TestClass();
		TestClass2 test2 = new TestClass2();
		test.byteField = 12;
		test2.booleanField = true;
		// test.t2Field = test2;
		test.intField = -282;
		byte[] ser = LightweightSerialization.getSingleton().serializeObject(test);
		TestClass testDeser = (TestClass) LightweightSerialization.getSingleton().deserializeObject(ser);
		System.out.println(ser);
	}

	public static void testArraySerialization() throws Exception {
		int[] array = new int[8];
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		LightweightSerialization.getSingleton().serializeArray(dataOut, array);
	}

	public static void testBooleanSerialization() throws Exception {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		dataOut.writeBoolean(false);
		dataOut.writeBoolean(true);
		dataOut.writeBoolean(false);
		dataOut.writeBoolean(true);
		dataOut.close();
		System.out.println(byteOut.toByteArray().length);
	}

	public static void testLightweightStreams() throws Exception {
		int[] array = new int[8];
		for (int i = 0; i < array.length; i++) {
			array[i] = i * 2;
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		LightweightObjectOutputStream dataOut = new LightweightObjectOutputStream(byteOut);
		dataOut.writeArray(array);
		byte[] bytes = byteOut.toByteArray();

		ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		LightweightObjectInputStream dataIn = new LightweightObjectInputStream(byteIn);
		int[] arrayIn = (int[]) dataIn.readArray();

		for (int i = 0; i < arrayIn.length; i++) {
			System.out.println(arrayIn[i]);
		}
	}

	public static void testMaps() throws Exception {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(1, 0.5);
		map.put(10, 0.25);
		map.put(100, 0.125);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		LightweightObjectOutputStream dataOut = new LightweightObjectOutputStream(byteOut);
		LightweightSerialization.getSingleton().serializeMap(dataOut, map);
		byte[] bytes = byteOut.toByteArray();

		ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		LightweightObjectInputStream dataIn = new LightweightObjectInputStream(byteIn);
		HashMap<Integer, Double> map2 = new HashMap<Integer, Double>();
		LightweightSerialization.getSingleton().deserializeMap(dataIn, map2);

		for (Map.Entry<Integer, Double> entry : map2.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	/**
	 * Workaround the inability of Class.forName to return Class instances for
	 * primitive types
	 * 
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> resolveClassForName(String name) throws ClassNotFoundException {
		if (name.equals("boolean")) {
			return boolean.class;
		} else if (name.equals("int")) {
			return int.class;
		} else if (name.equals("float")) {
			return float.class;
		} else if (name.equals("double")) {
			return double.class;
		} else if (name.equals("byte")) {
			return byte.class;
		} else if (name.equals("char")) {
			return char.class;
		} else if (name.equals("long")) {
			return long.class;
		} else if (name.equals("short")) {
			return short.class;
		} else {
			return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
		}
	}

	public static void main(String[] args) {
		try {
			LightweightSerialization.generateTypeMap();
			// LightweightSerialization.getSingleton().loadMapFromFile("conf\\LightweightSerialization.map");
			// testBooleanSerialization();
			// testLightweightSerialization();
			// testLightweightStreams();
			// testMaps();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
