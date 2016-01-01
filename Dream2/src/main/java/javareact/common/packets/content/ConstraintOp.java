package javareact.common.packets.content;

public enum ConstraintOp {
	ANY {
		@Override
		public final String toString() {
			return "ANY";
		}
	},
	EQ {
		@Override
		public final String toString() {
			return "=";
		}
	},
	DF {
		@Override
		public final String toString() {
			return "!=";
		}
	},
	GT {
		@Override
		public final String toString() {
			return ">";
		}
	},
	LT {
		@Override
		public final String toString() {
			return "<";
		}
	},
	IN {
		@Override
		public final String toString() {
			return "contains";
		}
	},
	SW {
		@Override
		public final String toString() {
			return "starts with";
		}
	},
	EW {
		@Override
		public final String toString() {
			return "ends with";
		}
	}
}
