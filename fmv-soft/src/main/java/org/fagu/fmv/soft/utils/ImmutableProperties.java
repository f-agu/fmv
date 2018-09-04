package org.fagu.fmv.soft.utils;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;


/**
 * Immutable version of <code>java.util.Properties</code> that is guaranteed to only contain string keys and values
 * 
 * From https://github.com/KualiCo/jute/blob/master/src/main/java/org/kuali/common/jute/collect/ImmutableProperties.java
 */
public final class ImmutableProperties extends Properties {

	private static final long serialVersionUID = 0;

	private static final Object MUTEX = new Object();

	private static final String UOE_MSG = "immutable properties cannot be changed";

	private static final ImmutableProperties EMPTY = new ImmutableProperties(new Properties());

	private ImmutableProperties(Properties mutable) {
		Objects.requireNonNull(mutable, "mutable");

		// Prevent anything from changing it until we are done
		synchronized(MUTEX) {
			// Extract only those keys where both the key and its corresponding value are strings
			Set<String> keys = mutable.stringPropertyNames();

			// If the sizes are different, it contains at least one key or value that is not a string
			if(keys.size() != mutable.size()) {
				throw new IllegalArgumentException("immutable properties only support strings");
			}

			// Copy every key/value pair - can't use putAll() since it calls put() which is now disabled
			for(String key : keys) {
				super.put(key, mutable.getProperty(key));
			}
		}
	}

	public static Properties of(String name, String value) {
		Properties props = new Properties();
		props.setProperty(name, value);
		return copyOf(props);
	}

	public static Properties of() {
		return EMPTY;
	}

	/**
	 * Create and return a new immutable properties object identical to the one passed in. If <code>properties</code> is
	 * already immutable, no new object is created, the <code>properties</code> object passed in as a method argument is
	 * what is returned.
	 *
	 * @throws NullPointerException if {@code properties} is null
	 */
	public static ImmutableProperties copyOf(Properties properties) {
		if(properties == null || properties.isEmpty()) {
			return EMPTY;
		}
		if(properties instanceof ImmutableProperties) {
			return (ImmutableProperties)properties;
		}
		return new ImmutableProperties(properties);
	}

	@Override
	public Object setProperty(String key, String value) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public void load(Reader reader) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public void load(InputStream inStream) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public void loadFromXML(InputStream in) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> t) {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(UOE_MSG);
	}

	@Override
	public Set<Object> keySet() {
		return Collections.unmodifiableSet(super.keySet());
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return Collections.unmodifiableSet(super.entrySet());
	}

	@Override
	public Collection<Object> values() {
		return Collections.unmodifiableCollection(super.values());
	}
}
