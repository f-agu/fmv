package org.fagu.fmv.soft.java;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.utils.SearchPropertiesHelper;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.version.VersionParserManager;


/**
 * @author f.agu
 */
public class JavaSoftProvider extends SoftProvider {

	private static final String PROP_VERSION_PATTERN = "soft.java.search.versionPattern";

	private static final String DEFAULT_PATTERN_VERSION = "(.*) version \"(.*)\"";

	public static final String NAME = "java";

	public JavaSoftProvider() {
		this(null);
	}

	public JavaSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(1, 8))));
	}

	JavaSoftProvider(String name, SoftPolicy softPolicy) {
		super(name, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(1, 8))));
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		final Pattern pattern = Pattern.compile(new SearchPropertiesHelper(searchProperties, getName())
				.getOrDefault(DEFAULT_PATTERN_VERSION, PROP_VERSION_PATTERN));
		return prepareSoftFoundFactory()
				.withParameters("-version")
				.parseVersion(line -> {
					Matcher matcher = pattern.matcher(line);
					return matcher.matches() ? VersionParserManager.parse(matcher.group(2)) : null;
				})
				.build();
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = new SoftLocator(getName()) {

			@Override
			protected List<Locator> getLocators(Locators loc) {
				List<Locator> list = super.getLocators(loc);
				Locators locators = createLocators();
				list.add(0, locators.byPropertyPath("java.home"));
				if(SystemUtils.IS_OS_WINDOWS) {
					ProgramFilesLocatorSupplier.with(list::add, locators)
							.find(programFile -> {
								List<File> files = new ArrayList<>();
								File[] javaFolders = programFile.listFiles(f -> "java".equalsIgnoreCase(f.getName()));
								if(javaFolders != null) {
									for(File folder : javaFolders) {
										File[] subFolders = folder.listFiles();
										if(subFolders != null) {
											for(File subFolder : subFolders) {
												files.add(subFolder);
												files.add(new File(subFolder, "bin"));
											}
										}
									}
								}
								return files;
							})
							.supplyIn();
				}
				return list;
			}
		};
		softLocator.setSoftPolicy(getSoftPolicy());
		softLocator.setEnvName("JAVA_HOME");
		return softLocator;
	}

	@Override
	public String getDownloadURL() {
		return "https://www.java.com/download/";
	}

}
