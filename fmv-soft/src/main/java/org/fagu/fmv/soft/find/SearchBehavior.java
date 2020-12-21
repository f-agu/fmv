package org.fagu.fmv.soft.find;

/**
 * @author Oodrive
 * @author f.agu
 * @created 21 déc. 2020 14:13:25
 */
public class SearchBehavior {

	private static final SearchBehavior CACHE_EMPTY = new SearchBehavior(false, false);

	private static final SearchBehavior CACHE_VERSION = new SearchBehavior(true, false);

	private static final SearchBehavior CACHE_DATE = new SearchBehavior(false, true);

	private static final SearchBehavior CACHE_VERSION_DATE = new SearchBehavior(true, true);

	// ------------------------------------------

	public static class SearchBehaviorBuilder {

		private boolean useVersionPattern;

		private boolean useDatePattern;

		private SearchBehaviorBuilder() {}

		public SearchBehaviorBuilder useVersionPattern(boolean useVersionPattern) {
			this.useVersionPattern = useVersionPattern;
			return this;
		}

		public SearchBehaviorBuilder useDatePattern(boolean useDatePattern) {
			this.useDatePattern = useDatePattern;
			return this;
		}

		public SearchBehavior build() {
			if(useVersionPattern) {
				return useDatePattern ? CACHE_VERSION_DATE : CACHE_VERSION;
			}
			return useDatePattern ? CACHE_DATE : CACHE_EMPTY;
		}

	}

	// ------------------------------------------

	private final boolean useVersionPattern;

	private final boolean useDatePattern;

	private SearchBehavior(boolean useVersionPattern, boolean useDatePattern) {
		this.useVersionPattern = useVersionPattern;
		this.useDatePattern = useDatePattern;
	}

	// private SearchBehavior(SearchBehaviorBuilder builder) {
	// this.useVersionPattern = builder.useVersionPattern;
	// this.useDatePattern = builder.useDatePattern;
	// }

	public static SearchBehaviorBuilder builder() {
		return new SearchBehaviorBuilder();
	}

	public static SearchBehavior empty() {
		return CACHE_EMPTY;
	}

	public static SearchBehavior onlyVersion() {
		return CACHE_VERSION;
	}

	public static SearchBehavior versionAndDate() {
		return CACHE_VERSION_DATE;
	}

	public boolean isUseDatePattern() {
		return useDatePattern;
	}

	public boolean isUseVersionPattern() {
		return useVersionPattern;
	}

}
