package org.fagu.fmv.soft;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftTester;


/**
 * @author Oodrive
 * @author f.agu
 * @created 20 mars 2025 11:19:23
 */
public class LogSearchListener implements SearchListener {

	private final Consumer<String> logger;

	public LogSearchListener(Consumer<String> logger) {
		this.logger = Objects.requireNonNull(logger);
	}

	@Override
	public void eventPreSearch(String softName, Collection<Locator> locators, SoftTester tester, Properties searchProperties) {
		logger.accept(
				new StringBuilder()
						.append("Pre-search ").append(softName).append(" with locators ")
						.append(locators.stream().map(Locator::toString).collect(Collectors.joining(", ")))
						.toString());
	}

	@Override
	public void eventStartLocator(String softName, Locator locator, SoftTester tester, Properties searchProperties) {
		logger.accept(
				new StringBuilder()
						.append("Start locator ").append(locator.toString())
						.append(" for ").append(softName)
						.toString());
	}

	@Override
	public void eventAddSoftFound(String softName, Locator locator, SoftFound softFound) {
		logger.accept(
				new StringBuilder()
						.append("Soft ").append(softName)
						.append(" found with locator ").append(locator.toString())
						.toString());
	}

}
