package protopeer.network;

import java.io.*;

/**
 * An abstract peer address in a {@link NetworkInterfaceFactory}. The implementor of the <code>NetworkInterfaceFactory</code> must also provide
 * an implementation of the <code>NetworkAddress</code>. The <code>NetworkAddress</code> must implement its own <code>clone</code> method
 * (must deep clone) as well as the <code>hashCode</code> and <code>equals</code> methods. 
 * 
 * 
 *
 */
public abstract class NetworkAddress implements Serializable, Cloneable {
		
	public abstract long toLongValue();
	
	public NetworkAddress clone() {		
		try {
			return (NetworkAddress)super.clone();
		} catch (CloneNotSupportedException e) {					
			return null;
		}				
	}
}
