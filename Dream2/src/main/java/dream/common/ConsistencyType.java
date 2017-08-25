package dream.common;

public enum ConsistencyType {
	CAUSAL {
		@Override
		public final String toString() {
			return "Causal";
		}
	},
	SINGLE_SOURCE_GLITCH_FREE {
		@Override
		public final String toString() {
			return "Single_source_glitch_free";
		}
	},
	COMPLETE_GLITCH_FREE {
		@Override
		public final String toString() {
			return "Complete_glitch_free";
		}
	},
	ATOMIC {
		@Override
		public final String toString() {
			return "Atomic";
		}
	}
}
