package org.fagu.fmv.mymedia.classify.duplicate;

import java.util.Collection;
import java.util.Collections;

import org.fagu.fmv.mymedia.utils.ScannerHelper.Answer;


/**
 * @author f.agu
 * @created 19 mars 2025 14:56:48
 */
public enum YesNoAlways implements Answer {

	YES {

		@Override
		public String getValue() {
			return "Yes";
		}

		@Override
		public Collection<String> getVariants() {
			return Collections.singleton("y");
		}
	},

	NO {

		@Override
		public String getValue() {
			return "No";
		}

		@Override
		public Collection<String> getVariants() {
			return Collections.singleton("n");
		}
	},

	YES_ALWAYS {

		@Override
		public String getValue() {
			return "yes-AlwAys";
		}

		@Override
		public Collection<String> getVariants() {
			return Collections.singleton("a");
		}

	},

	NO_ALWAYS {

		@Override
		public String getValue() {
			return "nO-always";
		}

		@Override
		public Collection<String> getVariants() {
			return Collections.singleton("o");
		}

	},

	COMPARE {

		@Override
		public String getValue() {
			return "Compare";
		}

		@Override
		public Collection<String> getVariants() {
			return Collections.singleton("c");
		}

	}
}
