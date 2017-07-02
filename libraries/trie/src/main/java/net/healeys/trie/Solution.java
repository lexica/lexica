package net.healeys.trie;

public interface Solution {
	String getWord();

	Integer[] getPositions();

	class Default implements Solution {

		private final String word;
		private final Integer[] positions;

		public Default(String word, Integer[] positions) {
			this.word = word;
			this.positions = positions;
		}

		@Override
		public String getWord() {
			return this.word;
		}

		@Override
		public Integer[] getPositions() {
			return positions;
		}
	}
}