package net.healeys.trie;

public interface Solution {
	String getWord();

	int getMask();

	class Default implements Solution {

		private final String word;
		private final int mask;

		public Default(String word, int[] positions) {
			this.word = word;
			this.mask = calcMask(positions);
		}

		private static int calcMask(int[] positions) {
			int mask = 0;
			for (int position : positions) {
				mask |= 1 << position;
			}
			return mask;
		}

		@Override
		public String getWord() {
			return this.word;
		}

		// TODO: Remove this and replace with a collection of strings. Right now it is only used
		// to highlight a set of squares when showing the score overview.
		@Override
		public int getMask() {
			return this.mask;
		}
	}
}