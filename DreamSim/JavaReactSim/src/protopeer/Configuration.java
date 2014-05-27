package protopeer;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * This class handles the parsing and loading of the *.conf files. A *.conf file
 * conforms to the Java properties file format and consists of (key,value)
 * pairs. Each key specifies the public non-static fields of this
 * <code>Configuration</code> object and the corresponding value is the value
 * assigned to the field.
 * 
 * <br/> Note that the <code>Configuration</code> class itself does not
 * contain any public non-static fields and needs to be subclassed to be useful.
 * 
 */
public class Configuration {

	private static final Logger logger = Logger.getLogger(Configuration.class);

	/**
	 * This function is called whenever the <code>Configuration</code> needs
	 * to create a concrete instance of a <code>fieldType</code> from a string
	 * representation (<code>value</code>) and assign it to the
	 * <code>field</code>.
	 * 
	 * The supported <code>fieldType</code>s are: <code>int</code>,
	 * <code>double</code>, <code>boolean</code>,<code>String</code>,<code>java.net.InetAddress</code>
	 * and all <code>enum</code>s. Subclasses can override this method to
	 * add support for additional <code>fieldType</code>s.
	 * 
	 * @param value
	 *            the string representation
	 * @param fieldType
	 * @param field
	 * @throws ConfigurationException
	 */
	protected void parseValue(String value, Class<?> fieldType, Field field) throws ConfigurationException {
		try {
			if (fieldType.isEnum()) {
				field.set(this, Enum.valueOf((Class<Enum>) fieldType, value));
			} else if (fieldType.equals(InetAddress.class)) {
				field.set(this, InetAddress.getByName(value));
			} else if (fieldType.equals(String.class)) {
				field.set(this, value);
			} else if (fieldType.equals(int.class)) {
				field.set(this, new Integer(value));
			} else if (fieldType.equals(long.class)) {
				field.set(this, new Long(value));
			} else if (fieldType.equals(double.class)) {
				field.set(this, new Double(value));
			} else if (fieldType.equals(boolean.class)) {
				if (!value.equals("true") && !value.equals("false")) {
					throw new ConfigurationException("The boolean field: " + field.getName()
							+ " should either be 'true' or 'false', it is: " + value);
				}
				field.set(this, new Boolean(value.equals("true")));
			} else {
				throw new ConfigurationException("Unsupported type " + fieldType.getCanonicalName()
						+ " in params loader, field name: " + field.getName() + " read value: " + value);
			}
		} catch (ConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigurationException("Problem parsing the field: " + field.getName(), e);
		}
	}

	private HashSet<Field> getFields() {
		HashSet<Field> fields = new HashSet<Field>();
		for (Field field : this.getClass().getFields()) {
			int modifiers = field.getModifiers();
			if (((modifiers & Modifier.PUBLIC) != 0) && ((modifiers & Modifier.STATIC) == 0)) {
				fields.add(field);
			}
		}
		return fields;
	}

	/**
	 * 
	 * @param properties
	 *            the <code>Properties</code> object that contains the (<b>key</b>,
	 *            <b>value</b>) pairs with which this
	 *            <code>Configuration</code> should be initialized. Each
	 *            <b>key</b> is a string that must match one of the public
	 *            non-static fields of this <code>Configuration</code> object
	 *            and the <b>value</b> is a string representation of the value
	 *            which is parsed and assigned to the field using the protected
	 *            <code> parseValue</code> method. The
	 *            <code>loadFromProperties</code> method requires a one-to-one
	 *            relationship between the fields specified in the
	 *            <code>properties</code> argument and the public static
	 *            fields in this object, otherwise an
	 *            <code>ConfigurationException</code> is thrown.
	 * 
	 * @throws ConfigurationException
	 */
	public void loadFromProperties(Properties properties) throws ConfigurationException {
		Set<Field> fields = getFields();

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			Field field = null;
			try {
				field = this.getClass().getField(key);
			} catch (NoSuchFieldException e) {
				throw new ConfigurationException("Field not found in the " + this.getClass().getCanonicalName()
						+ " class: " + key);
			}
			parseValue(value, field.getType(), field);
			fields.remove(field);
		}

		if (!fields.isEmpty()) {
			String uninitializedFields = "";
			for (Field field : fields) {
				uninitializedFields += field.getName() + " ";
			}
			throw new ConfigurationException("The following fields do not appear in the .conf: " + uninitializedFields);
		}
	}

	/**
	 * Loads the *.conf file into this <code>Configuration</code> object. The
	 * *.conf is expected to be in the standard java properties format. The
	 * (key,value) pairs are interpreted as in the
	 * <code>loadFromProperties</code> method.
	 * 
	 * @param filename
	 *            the name of the loaded file
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
					logger.error("", e);
				}
			}
		}
	}

}