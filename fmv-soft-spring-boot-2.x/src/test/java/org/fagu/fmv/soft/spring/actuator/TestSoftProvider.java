package org.fagu.fmv.soft.spring.actuator;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;


/**
 * @author f.agu
 */
public class TestSoftProvider extends SoftProvider {

	private final Supplier<SoftFound> softFoundSupplier;

	public TestSoftProvider(String name, Supplier<SoftFound> softFoundSupplier) {
		super(name, null);
		this.softFoundSupplier = Objects.requireNonNull(softFoundSupplier);
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
		return (file, locator, softPolicy) -> softFoundSupplier.get();
	}

	@Override
	public SoftPolicy getSoftPolicy() {
		return null;
	}

	@Override
	public Soft search() {
		NavigableSet<SoftFound> softFounds = new TreeSet<>();
		SoftFound softFound = softFoundSupplier.get();
		if(softFound != null) {
			softFounds.add(softFound);
		}
		return new Soft(new Founds(getName(), softFounds, null, null), this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
