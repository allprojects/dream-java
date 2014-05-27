package protopeer.measurement;

import java.io.*;
import java.util.*;

class MeasurementKey implements Serializable, Cloneable {

	private Object[] tags;

	private MeasurementKey() {
	}

	public MeasurementKey(Set<Object> tags) {
		init(tags);
	}

	public MeasurementKey(Object tag) {
		HashSet<Object> tags = new HashSet<Object>();
		tags.add(tag);
		init(tags);
	}

	public MeasurementKey(Object tag1, Object tag2) {
		HashSet<Object> tags = new HashSet<Object>();
		tags.add(tag1);
		tags.add(tag2);
		init(tags);
	}

	public MeasurementKey(Object tag1, Object tag2, Object tag3) {
		HashSet<Object> tags = new HashSet<Object>();
		tags.add(tag1);
		tags.add(tag2);
		tags.add(tag3);
		init(tags);
	}

	public Object getTagOfType(Class<?> tagType) {
		for (Object tag : tags) {
			if (tagType.isInstance(tag)) {
				return tag;
			}
		}
		return null;
	}

	private void init(Set<Object> tags) {
		this.tags = new Object[tags.size()];
		int i = 0;
		for (Object tag : tags) {
			this.tags[i++] = tag;
		}
	}

	public Set<Object> getTags() {
		if (tags == null) {
			return null;
		}
		HashSet<Object> out = new HashSet<Object>();
		for (Object tag : tags) {
			out.add(tag);
		}
		return out;
	}

	public boolean isSuperset(MeasurementKey key) {
		for (int i = 0; i < key.tags.length; i++) {
			boolean found = false;
			for (int i2 = 0; i2 < this.tags.length; i2++) {
				if (this.tags[i2].equals(key.tags[i])) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public boolean isStoreValues() {
		// it's sufficient that only one tag is configured to storeValues
		for (Object tag : tags) {
			if (tag.getClass().isEnum()) {
				if (MeasurementConfiguration.getSingleton().isStoreValues((Enum<?>) tag)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isEnabled() {
		// all tags must be enabled for the measurementKey to be enabled
		for (Object tag : tags) {
			if (tag.getClass().isEnum()) {
				if (!MeasurementConfiguration.getSingleton().isEnabled((Enum<?>) tag)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public MeasurementKey clone() {
		try {
			MeasurementKey twin = (MeasurementKey) super.clone();
			twin.tags = new Object[this.tags.length];
			System.arraycopy(this.tags, 0, twin.tags, 0, this.tags.length);
			return twin;
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	@Override
	public String toString() {
		return Arrays.toString(tags);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((tags == null) ? 0 : Arrays.hashCode(tags));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MeasurementKey other = (MeasurementKey) obj;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!Arrays.equals(other.tags,tags))
			return false;
		return true;
	}

}
