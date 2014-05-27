package protopeer.util;

import java.io.*;
import java.util.*;

public class LightweightObjectOutputStream extends DataOutputStream {

	public LightweightObjectOutputStream(OutputStream out) {
		super(out);
	}

	public void writeObject(Object object) throws IOException {
		LightweightSerialization.getSingleton().serializeObject(this, object);
	}

	public void writeObject(Object object, Class<?> objectType) throws IOException {
		LightweightSerialization.getSingleton().serializeObject(this, object, objectType);
	}

	public void writeCollection(Collection<?> collection) throws IOException {
		LightweightSerialization.getSingleton().serializeCollection(this, collection);
	}

	public void writeCollection(Collection<?> collection, Class<?> elementType) throws IOException {
		LightweightSerialization.getSingleton().serializeCollection(this, collection,elementType);
	}

	public void writeArray(Object array) throws IOException {
		LightweightSerialization.getSingleton().serializeArray(this, array);
	}
	
	public void writeArray(Object array, Class<?> elementType) throws IOException {
		LightweightSerialization.getSingleton().serializeArray(this, array,elementType);
	}

}
