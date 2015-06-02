package protopeer.measurement;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * 
 * Stores the mappings from the set of measurment tags to their corresponding
 * <code>Aggregate</code>s. Measurement tags can be arbitrary
 * <code>Object</code>s. A <code>MeasurementLog</code> is tied to a specific
 * measurement epoch number (<code>epochNum</code>).
 * 
 * All measurement logs must be <code>Cloneable</code>,
 * <code>Serializable</code> and must have a consistent <code>hashCode</code>
 * and <code>equals</code> implementations.
 * 
 */
public class MeasurementLog implements Serializable, Cloneable {

	private static Logger logger = Logger.getLogger(MeasurementLog.class);

	// serializable
	private LinkedList dataLock = new LinkedList();

	private Map<MeasurementKey, Aggregate> key2aggregateMap = new HashMap<MeasurementKey, Aggregate>();

	private SortedMap<Integer, Map<MeasurementKey, Aggregate>> epoch2aggregateMap = new TreeMap<Integer, Map<MeasurementKey, Aggregate>>();

	/**
	 * Creates an empty <code>MeasurementLog</code>.
	 * 
	 */
	public MeasurementLog() {
	}

	private Aggregate getAggregateAllocate(MeasurementKey key, Map<MeasurementKey, Aggregate> map) {
		synchronized (dataLock) {
			Aggregate aggregate = map.get(key);
			if (aggregate == null) {
				aggregate = new Aggregate(key.isStoreValues());
				// skip allocation when MeasurementKey is disabled in
				// measurement.conf
				if (key.isEnabled()) {
					map.put(key, aggregate);
				}
			}
			return aggregate;
		}
	}

	private Map<MeasurementKey, Aggregate> getMapForEpoch(int epochNumber) {
		synchronized (dataLock) {
			Map<MeasurementKey, Aggregate> map = epoch2aggregateMap.get(epochNumber);
			if (map == null) {
				map = new HashMap<MeasurementKey, Aggregate>();
				epoch2aggregateMap.put(epochNumber, map);
			}
			return map;
		}
	}

	private Aggregate getAggregateAllocate(MeasurementKey key) {
		return getAggregateAllocate(key, key2aggregateMap);
	}

	private Aggregate getAggregateAllocate(int epochNumber, MeasurementKey key) {
		return getAggregateAllocate(key, getMapForEpoch(epochNumber));
	}

	private void logMeasurementKey(int epochNum, MeasurementKey key, double value) {
		if (!key.isEnabled()) {
			return;
		}

		synchronized (dataLock) {
			getAggregateAllocate(epochNum, key).addValue(value);
			getAggregateAllocate(key).addValue(value);
		}
	}

	/**
	 * Logs a <code>value</code> tagged with the set of <code>tags</code>.
	 * 
	 * @param epochNumber
	 * @param tags
	 * @param value
	 */
	public void logTagSet(int epochNumber, Set<Object> tags, double value) {
		logMeasurementKey(epochNumber, new MeasurementKey(tags), value);
	}

	/**
	 * Logs a <code>value</code> tagged a single <code>tag</code>.
	 * 
	 * @param epochNumber
	 * @param tag
	 * @param value
	 */
	public void log(int epochNumber, Object tag, double value) {
		logMeasurementKey(epochNumber, new MeasurementKey(tag), value);
	}

	/**
	 * Logs a <code>value</code> tagged a pair of tags.
	 * 
	 * @param epochNumber
	 * @param tag1
	 * @param tag2
	 * @param value
	 */
	public void log(int epochNumber, Object tag1, Object tag2, double value) {
		logMeasurementKey(epochNumber, new MeasurementKey(tag1, tag2), value);
	}

	/**
	 * Logs a <code>value</code> tagged a tag triple.
	 * 
	 * @param epochNumber
	 * @param tag1
	 * @param tag2
	 * @param value
	 * 
	 */
	public void log(int epochNumber, Object tag1, Object tag2, Object tag3, double value) {
		logMeasurementKey(epochNumber, new MeasurementKey(tag1, tag2, tag3), value);
	}

	/**
	 * Merges all the logged values from the <code>otherLog</code> into this
	 * log.
	 * 
	 * @param otherLog
	 */
	public void mergeWith(MeasurementLog otherLog) {
		synchronized (dataLock) {
			synchronized (otherLog.dataLock) {
				for (Map.Entry<MeasurementKey, Aggregate> entry : otherLog.key2aggregateMap.entrySet()) {
					getAggregateAllocate(entry.getKey()).mergeWith(entry.getValue());
				}

				for (Map.Entry<Integer, Map<MeasurementKey, Aggregate>> entry : otherLog.epoch2aggregateMap.entrySet()) {
					int epochNumber = entry.getKey();
					for (Map.Entry<MeasurementKey, Aggregate> entry2 : entry.getValue().entrySet()) {
						getAggregateAllocate(epochNumber, entry2.getKey()).mergeWith(entry2.getValue());
					}
				}
			}
		}
	}

	/**
	 * Merges all the logged values from the <code>otherLog</code> into this
	 * log, but only for a specified epoch number.
	 * 
	 * @param otherLog
	 *            the log to merge with
	 * @param epochNumber
	 *            the epoch number
	 */
	public void mergeWith(MeasurementLog otherLog, int epochNumber) {
		synchronized (dataLock) {
			synchronized (otherLog.dataLock) {
				if (!otherLog.dataForEpochExists(epochNumber)) {
					return;
				}
				for (Map.Entry<MeasurementKey, Aggregate> entry : otherLog.getMapForEpoch(epochNumber).entrySet()) {
					getAggregateAllocate(epochNumber, entry.getKey()).mergeWith(entry.getValue());
					getAggregateAllocate(entry.getKey()).mergeWith(entry.getValue());
				}
			}
		}
	}

	private boolean dataForEpochExists(int epochNumber) {
		return epoch2aggregateMap.containsKey(epochNumber);
	}
	
	private Aggregate getAggregateForMeasurementKey(MeasurementKey key) {
		return getAggregateAllocate(key);
	}

	private Aggregate getAggregateForMeasurementKeyByEpochNumber(int epochNumber, MeasurementKey key) {
		return getAggregateAllocate(epochNumber, key);
	}

	/**
	 * Gets an aggregate for a given tag, aggregated over all the measurement
	 * epochs.
	 * 
	 * @param tag
	 * @return
	 */
	public Aggregate getAggregate(Object tag) {
		return getAggregateForMeasurementKey(new MeasurementKey(tag)).clone();
	}

	/**
	 * Gets an aggregate for a pair of tags, aggregated over all the measurement
	 * epochs.
	 * 
	 * @param tag1
	 * @param tag2
	 * @return
	 */
	public Aggregate getAggregate(Object tag1, Object tag2) {
		return getAggregateForMeasurementKey(new MeasurementKey(tag1, tag2)).clone();
	}

	/**
	 * Gets an aggregate for a tag triple, aggregated over all the measurement
	 * epochs.
	 * 
	 * @param tag1
	 * @param tag2
	 * @param tag3
	 * @return
	 */
	public Aggregate getAggregate(Object tag1, Object tag2, Object tag3) {
		return getAggregateForMeasurementKey(new MeasurementKey(tag1, tag2, tag3)).clone();
	}

	/**
	 * Gets an aggregate for set of tags, aggregated over all the measurement
	 * epochs.
	 * 
	 * @param tags
	 * @return
	 */
	public Aggregate getAggregateForTagSet(Set<Object> tags) {
		return getAggregateForMeasurementKey(new MeasurementKey(tags)).clone();
	}

	/**
	 * Gets an aggregate for set of tags and a specific epoch number.
	 * 
	 * @param tags
	 * @param epochNumber
	 * @return
	 */
	public Aggregate getAggregateForTagSetByEpochNumber(int epochNumber, Set<Object> tags) {
		return getAggregateForMeasurementKeyByEpochNumber(epochNumber, new MeasurementKey(tags)).clone();
	}

	/**
	 * Gets an aggregate for a given tag and epochNumber
	 * 
	 * @param epochNumber
	 * @param tag
	 * 
	 * @return
	 */
	public Aggregate getAggregateByEpochNumber(int epochNumber, Object tag) {
		return getAggregateForMeasurementKeyByEpochNumber(epochNumber, new MeasurementKey(tag)).clone();
	}

	/**
	 * Gets an aggregate for a pair of tags and a specific epoch number.
	 * 
	 * @param epochNumber
	 * @param tag1
	 * @param tag2
	 * @return
	 */
	public Aggregate getAggregateByEpochNumber(int epochNumber, Object tag1, Object tag2) {
		return getAggregateForMeasurementKeyByEpochNumber(epochNumber, new MeasurementKey(tag1, tag2)).clone();
	}

	/**
	 * Gets an aggregate for a tag triple and a specific epoch number.
	 * 
	 * @param epochNumber
	 * @param tag1
	 * @param tag2
	 * @param tag3
	 * @return
	 */
	public Aggregate getAggregateByEpochNumber(int epochNumber, Object tag1, Object tag2, Object tag3) {
		return getAggregateForMeasurementKeyByEpochNumber(epochNumber, new MeasurementKey(tag1, tag2, tag3)).clone();
	}

	/**
	 * Returns all the tags ever logged that are
	 * <code><b>instanceof</b> tagType</code>
	 * 
	 * @param tagType
	 * @return
	 */
	public Set<Object> getTagsOfType(Class<?> tagType) {
		HashSet<Object> tags = new HashSet<Object>();
		synchronized (dataLock) {
			for (MeasurementKey key : key2aggregateMap.keySet()) {
				for (Object tag : key.getTags()) {
					if (tagType.isInstance(tag)) {
						tags.add(tag);
					}
				}
			}
		}
		return tags;
	}

	/**
	 * Returns all the tags ever logged whose class exactly equals
	 * <code>tagType</code>
	 * 
	 * @param tagType
	 * @return
	 */
	public Set<Object> getTagsOfExactType(Class<?> tagType) {
		HashSet<Object> tags = new HashSet<Object>();
		synchronized (dataLock) {
			for (MeasurementKey key : key2aggregateMap.keySet()) {
				for (Object tag : key.getTags()) {
					if (tag.getClass().equals(tagType)) {
						tags.add(tag);
					}
				}
			}
		}
		return tags;
	}

	/**
	 * Returns the epoch number of the earliest measurement logged.
	 * 
	 * @return
	 * 
	 * @throws java.util.NoSuchElementException if this log is empty
	 */
	public int getMinEpochNumber() {
		synchronized (dataLock) {			
			return epoch2aggregateMap.firstKey();
		}
	}

	/**
	 * Returns the epoch number of the latest measurement logged.
	 * 
	 * @return
	 * 
	 * @throws java.util.NoSuchElementException if this log is empty
	 */
	public int getMaxEpochNumber() {
		synchronized (dataLock) {
			return epoch2aggregateMap.lastKey();
		}
	}

	@Override
	public MeasurementLog clone() {
		try {
			MeasurementLog twin = (MeasurementLog) super.clone();
			synchronized (dataLock) {
				twin.key2aggregateMap = new HashMap<MeasurementKey, Aggregate>();
				twin.epoch2aggregateMap = new TreeMap<Integer, Map<MeasurementKey, Aggregate>>();
				for (Map.Entry<MeasurementKey, Aggregate> entry : this.key2aggregateMap.entrySet()) {
					twin.key2aggregateMap.put(entry.getKey().clone(), entry.getValue().clone());
				}
				for (Map.Entry<Integer, Map<MeasurementKey, Aggregate>> entry : this.epoch2aggregateMap.entrySet()) {
					twin.epoch2aggregateMap.put(entry.getKey(), new HashMap<MeasurementKey, Aggregate>());
					for (Map.Entry<MeasurementKey, Aggregate> entry2 : entry.getValue().entrySet()) {
						twin.key2aggregateMap.put(entry2.getKey().clone(), entry2.getValue().clone());
					}
				}
			}
			return twin;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Returns a measurement log containing only the subset of values stored in
	 * this log from the epochs from <code>startEpochNumber</code> till and
	 * excluding <code>endEpochNumber</code>.
	 * 
	 * 
	 * @param startEpochNumber
	 * @param endEpochNumber
	 * @return
	 */
	public MeasurementLog getSubLog(int startEpochNumber, int endEpochNumber) {
		MeasurementLog subLog = new MeasurementLog();
		synchronized (dataLock) {
			for (int epochNumber = startEpochNumber; epochNumber < endEpochNumber; epochNumber++) {
				subLog.mergeWith(this, epochNumber);
			}
		}
		return subLog;
	}

	/**
	 * Shrinks this log to contain only the values from the epochs from
	 * <code>startEpochNumber</code> till and excluding
	 * <code>endEpochNumber</code>.
	 * 
	 * @param startEpochNumber
	 * @param endEpochNumber
	 * @return
	 */

	public void shrink(int startEpochNumber, int endEpochNumber) {
		synchronized (dataLock) {
			MeasurementLog subLog = getSubLog(startEpochNumber, endEpochNumber);
			this.key2aggregateMap = subLog.key2aggregateMap;
			this.epoch2aggregateMap = subLog.epoch2aggregateMap;
		}
	}

	@Override
	public String toString() {
		synchronized (dataLock) {
			StringBuffer buffer = new StringBuffer("\n");
			for (Map.Entry<MeasurementKey, Aggregate> entry : key2aggregateMap.entrySet()) {
				buffer.append(entry.getKey());
				buffer.append(" -> ");
				buffer.append(entry.getValue());
				buffer.append("\n");
			}
			for (Map.Entry<Integer, Map<MeasurementKey, Aggregate>> entry : epoch2aggregateMap.entrySet()) {
				buffer.append("epoch ");
				buffer.append(entry.getKey());
				buffer.append("\n");
				for (Map.Entry<MeasurementKey, Aggregate> entry2 : entry.getValue().entrySet()) {
					buffer.append("\t");
					buffer.append(entry2.getKey());
					buffer.append(" -> ");
					buffer.append(entry2.getValue());
					buffer.append("\n");
				}
			}
			return buffer.toString();
		}
	}

}
