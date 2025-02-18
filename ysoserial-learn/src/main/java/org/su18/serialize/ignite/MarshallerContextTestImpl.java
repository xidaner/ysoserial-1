package org.su18.serialize.ignite;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.MarshallerContextImpl;
import org.apache.ignite.plugin.PluginProvider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author su18
 */
public class MarshallerContextTestImpl extends MarshallerContextImpl {

	/**
	 *
	 */
	private static final ConcurrentMap<Integer, String> map = new ConcurrentHashMap<>();

	/**
	 *
	 */
	private final Collection<String> excluded;

	/**
	 * Initializes context.
	 *
	 * @param plugins  Plugins.
	 * @param excluded Excluded classes.
	 */
	public MarshallerContextTestImpl(List<PluginProvider> plugins, Collection<String> excluded) {
		super(plugins, null);

		this.excluded = excluded;
	}

	/**
	 * Initializes context.
	 *
	 * @param plugins Plugins.
	 */
	public MarshallerContextTestImpl(List<PluginProvider> plugins) {
		this(plugins, null);
	}

	/**
	 * Initializes context.
	 */
	public MarshallerContextTestImpl() {
		this(null);
	}

	/**
	 * @return Internal map.
	 */
	public ConcurrentMap<Integer, String> internalMap() {
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean registerClassName(
			byte platformId,
			int typeId,
			String clsName,
			boolean failIfUnregistered
	) throws IgniteCheckedException {
		if (excluded != null && excluded.contains(clsName))
			return false;

		String oldClsName = map.putIfAbsent(typeId, clsName);

		if (oldClsName != null && !oldClsName.equals(clsName))
			throw new IgniteCheckedException("Duplicate ID [id=" + typeId + ", oldClsName=" + oldClsName + ", clsName=" +
					clsName + ']');

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClassName(
			byte platformId,
			int typeId
	) throws ClassNotFoundException, IgniteCheckedException {
		String clsName = map.get(typeId);

		return (clsName == null) ? super.getClassName(platformId, typeId) : clsName;
	}
}

