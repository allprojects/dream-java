package protopeer.util;

import java.io.*;
import java.util.*;

public class LightweightObjectInputStream extends DataInputStream {

	public LightweightObjectInputStream(InputStream in) {
		super(in);
	}

	public Object readObject() throws IOException {
		return LightweightSerialization.getSingleton().deserializeObject(this);
	}

	public Object readObject(Class<?> objectType) throws IOException {
		return LightweightSerialization.getSingleton().deserializeObject(this, objectType);
	}

	public void readCollection(Collection<?> collectionToAddTo) throws IOException {
		LightweightSerialization.getSingleton().deserializeCollection(this, collectionToAddTo);
	}

	public void readCollection(Collection<?> collectionToAddTo, Class<?> elementType) throws IOException {
		LightweightSerialization.getSingleton().deserializeCollection(this, collectionToAddTo, elementType);
	}

	public Object readArray() throws IOException {
		return LightweightSerialization.getSingleton().deserializeArray(this);
	}

	public Object readArray(Class<?> elementType) throws IOException {
		return LightweightSerialization.getSingleton().deserializeArray(this,elementType);
	}

}
