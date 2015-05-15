package protopeer.util;

import java.io.*;

public interface LightweightSerializable {

	public void serialize(LightweightObjectOutputStream out) throws IOException;

	public void deserialize(LightweightObjectInputStream in) throws IOException;

}
