package protopeer.measurement;

import java.io.*;
import java.util.*;
import protopeer.*;

public class MeasurementConfiguration {

	private HashSet<Enum<?>> storeValues = new HashSet<Enum<?>>();

	private HashSet<Enum<?>> disabled = new HashSet<Enum<?>>();

	private static MeasurementConfiguration singleton;

	public MeasurementConfiguration(){
		try {
			this.loadFromFile("conf/measurement.conf");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static MeasurementConfiguration getSingleton() {
		if (singleton == null) {
			singleton = new MeasurementConfiguration();
		}
		return singleton;
	}

	/**
	 * 
	 * 
	 * @param enumValue
	 *            has the format "canonical_classname.enum_value" (with the dot
	 *            as the delimiter)
	 */
	private static Enum<?> getEnum(String enumString) throws ConfigurationException {
		// split around the last dot
		int dotIndex = enumString.lastIndexOf('.');
		if (dotIndex == -1) {
			throw new ConfigurationException("Syntax error, expected a enum_class.value, instead found: " + enumString);
		}
		String prefix = enumString.substring(0, dotIndex);
		String suffix = enumString.substring(dotIndex + 1, enumString.length());
		try {
			Class<?> clazz = Class.forName(prefix);
			if (clazz.isEnum()) {
				boolean found = false;
				for (Object enumConst : clazz.getEnumConstants()) {
					if (enumConst.toString().equals(suffix)) {
						found = true;
						return (Enum<?>) enumConst;
					}
				}
				if (!found) {
					throw new ConfigurationException("The enum constant " + suffix + " does not exist in enum "
							+ prefix);
				}
			} else {
				throw new ConfigurationException("The class: " + prefix + " is not an enum");
			}

		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Looks like that enum does not exist: " + prefix, e);
		}
		return null;
	}

	/**
	 * Loads the configuration from the Properties object.
	 * 
	 * @param properties
	 * @throws ConfigurationException
	 */
	public void loadFromProperties(Properties properties) throws ConfigurationException {
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			// split around the last dot
			int dotIndex = key.lastIndexOf('.');
			if (dotIndex == -1) {
				throw new ConfigurationException("Syntax error, expected tag.option=value, insted found: " + key + "="
						+ value);
			}
			String prefix = key.substring(0, dotIndex);
			String suffix = key.substring(dotIndex + 1, key.length());
			Enum<?> enumm = getEnum(prefix);
			if (suffix.equals("storeValues")) {
				if (value.equals("true")) {
					storeValues.add(enumm);
				} else if (value.equals("false")) {
					storeValues.remove(enumm);
				} else {
					throw new ConfigurationException("Expecting true/false, found " + value + " when parsing " + key
							+ "=" + value);
				}
			} else if (suffix.equals("enabled")) {
				if (value.equals("false")) {
					disabled.add(enumm);
				} else if (value.equals("true")) {
					disabled.remove(enumm);
				} else {
					throw new ConfigurationException("Expecting true/false, found " + value + " when parsing " + key
							+ "=" + value);
				}
			} else {
				throw new ConfigurationException("Unrecognized option " + suffix + " when parsing " + key + "=" + value);
			}
		}
	}

	boolean isStoreValues(Enum<?> tag) {
		return storeValues.contains(tag);
	}
	
	boolean isEnabled(Enum<?> tag) {
		return !disabled.contains(tag);
	}

	/**
	 * Loads the configuration from a file in the Java Properties format
	 * (key=value pairs)
	 * 
	 * @param filename
	 * @throws ConfigurationException
	 */
	public void loadFromFile(String filename) throws ConfigurationException {
		InputStream propertiesStream = null;
		try {
			propertiesStream = new FileInputStream(filename);
			Properties properties = new Properties();
			properties.load(propertiesStream);
			loadFromProperties(properties);
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException(e);
		} finally {
			if (propertiesStream != null) {
				try {
					propertiesStream.close();
				} catch (IOException e) {
					throw new ConfigurationException(e);
				}
			}
		}
	}

	public enum MyTags {
		ALPHA, BETA, GAMMA
	}

	public static void main(String[] args) {
		try {
			// Class<?> clazz =
			// Class.forName("protopeer.measurement.MeasurementConfiguration$MyTags");
			// System.out.println(clazz.isEnum());
			// for (Object obj : clazz.getEnumConstants()) {
			// System.out.println(obj.getClass().getCanonicalName());
			// System.out.println(obj);
			// }

			// Enum<?>
			// enumm=getEnum("protopeer.measurement.MeasurementConfiguration.MyTags.BETA");
			// System.out.println(enumm.ordinal());
			// System.out.println(enumm.toString());
			// System.out.println(enumm.name());
			// System.out.println(enumm.getClass().getCanonicalName());
			// System.out.println(enumm.equals(MyTags.BETA));
			// System.out.println(enumm.equals(MyTags.ALPHA));

			MeasurementConfiguration measurementConfiguration = new MeasurementConfiguration();
			measurementConfiguration.loadFromFile("conf/measurement.conf");
			System.out.println(measurementConfiguration.isStoreValues(MyTags.BETA));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
