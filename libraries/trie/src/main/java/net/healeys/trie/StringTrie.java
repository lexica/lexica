package net.healeys.trie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringTrie implements Trie {

	private final Node rootNode;

	public StringTrie() {
		rootNode = new Node();
	}

	private StringTrie(InputStream in) throws IOException {
		this(in, null);
	}

	/**
	 * Decides whether you can transition from one cell to another, purely based on whether both the
	 * source and destination cell are present on the board.
	 *
	 * It doesn't have enough information to figure out which words can and can't be done correctly.
	 * However it does have enough information to exclude large portions of a dictionary-sized
	 * trie very quickly, instead of spending time reading and parsing it.
     * Using this approximately halves the loading time in my basic tests.
	 */
	private static class CheapTransitionMap {

		private Map<String, Set<String>> transitions = new HashMap<>();

		CheapTransitionMap(TransitionMap transitionMap) {
			for (int fromPos = 0; fromPos < transitionMap.getSize(); fromPos ++) {

				String from = transitionMap.valueAt(fromPos);

				int fromX = fromPos % transitionMap.getWidth();
				int fromY = fromPos / transitionMap.getWidth();

				Set <String> transitionTo = new HashSet<>();
				for (int j = 0; j < transitionMap.getSize(); j ++) {
					String to = transitionMap.valueAt(j);
					if (transitionMap.valueAt(j).equals(to)) {
						int toX = j % transitionMap.getWidth();
						int toY = j / transitionMap.getWidth();
						if (transitionMap.canTransition(fromX, fromY, toX, toY)) {
							transitionTo.add(transitionMap.valueAt(j));
						}
					}
				}

				if (!transitions.containsKey(from)) {
					transitions.put(from, new HashSet<String>());
				}

				transitions.get(from).addAll(transitionTo);

			}
		}

		boolean contains(String from) {
			return transitions.containsKey(from);
		}

		boolean canTransition(String from, String to) {
			Set<String> transitionTo = transitions.get(from);
			return transitionTo != null && transitionTo.contains(to);
		}
	}

	private StringTrie(InputStream in, TransitionMap transitionMap) throws IOException {
		Set<String> availableStrings = new HashSet<>(transitionMap.getSize());
		for (int i = 0; i < transitionMap.getSize(); i ++) {
			availableStrings.add(transitionMap.valueAt(i));
		}
		rootNode = new Node(new DataInputStream(new BufferedInputStream(in)), new CheapTransitionMap(transitionMap), availableStrings, false, null, 0);
	}

	@Override
	public void addWord(String w, boolean usWord, boolean ukWord) {
		rootNode.addSuffix(w, 0, usWord, ukWord);
	}

	@Override
	public boolean isWord(String w, boolean usWord, boolean ukWord) {
		return rootNode.isWord(w, 0, usWord, ukWord);
	}

	@Override
	public boolean isWord(String word) {
		return rootNode.isAnyWord(word, 0);
	}

	@Override
	public void write(OutputStream out) throws IOException {
		rootNode.writeNode(out);
	}

	public static class StringSolution implements net.healeys.trie.Solution {

		private final String word;
		private final Integer[] positions;

		public StringSolution(String word, Integer[] positions) {
			this.word = word;
			this.positions = positions;
		}

		@Override
		public String getWord() {
			return word;
		}

		public Integer[] getPositions() {
			return positions;
		}
	}

	private void recursiveSolver(
			TransitionMap transitions,
			WordFilter wordFilter,
			StringTrie.Node node,
			int pos,
			Set<Integer> usedPositions,
			StringBuilder prefix,
			LinkedHashMap<String, Solution> solutions,
			List<Integer> solution) {

		if (node.usWord() || node.ukWord()) {
			String w = new String(prefix);
			if(wordFilter == null || wordFilter.isWord(w)) {
				Integer[] solutionArray = new Integer[solution.size()];
				solution.toArray(solutionArray);
				solutions.put(w, new StringSolution(w, solutionArray));
			}
		}

		if (node.isTail()) {
			return;
		}

		usedPositions.add(pos);

		int fromX = pos % transitions.getWidth();
		int fromY = pos / transitions.getWidth();

		for (int toX = 0; toX < transitions.getWidth(); toX ++) {
			for	(int toY = 0; toY < transitions.getWidth(); toY ++) {
				if (!transitions.canTransition(fromX, fromY, toX, toY)) {
					continue;
				}

				int toPosition = toX + transitions.getWidth() * toY;
				if (usedPositions.contains(toPosition)) {
					continue;
				}

				String valueAt = transitions.valueAt(toPosition);
				StringTrie.Node nextNode = node.maybeChildAt(valueAt);
				if (nextNode == null) {
					continue;
				}

				prefix.append(valueAt);

				solution.add(toPosition);
				recursiveSolver(transitions, wordFilter, nextNode, toPosition, usedPositions, prefix, solutions, solution);
				solution.remove(solution.size() - 1);

				prefix.delete(prefix.length() - valueAt.length(), prefix.length());
			}
		}

		usedPositions.remove(pos);
	}

	@Override
	public LinkedHashMap<String, Solution> solver(TransitionMap transitions, WordFilter filter) {

		long startTime = System.currentTimeMillis();
		LinkedHashMap<String, Solution> solutions = new LinkedHashMap<>();
		StringBuilder prefix = new StringBuilder(transitions.getSize() + 1);

		List<Integer> positions = new ArrayList<>(transitions.getSize());
		for(int i=0; i < transitions.getSize(); i ++) {
			String value = transitions.valueAt(i);
			StringTrie.Node nextNode = rootNode.maybeChildAt(value);
			if (nextNode == null) {
				continue;
			}

			prefix.append(value);
			positions.add(i);

			recursiveSolver(transitions, filter, nextNode, i, new HashSet<Integer>(), prefix, solutions, positions);

			positions.remove(positions.size() - 1);
			prefix.delete(prefix.length() - value.length(), prefix.length());
		}

		long totalTime = System.currentTimeMillis() - startTime;

		return solutions;
	}

	private static class Node implements TrieNode {

		private final Map<String, Node> children = new HashMap<>();

		private boolean isUsWord;
		private boolean isUkWord;

		private Node() {

		}

		private Node(DataInputStream input, CheapTransitionMap transitionMap, Set<String> availableStrings, boolean shouldSkip, String lastChar, int depth) throws IOException {

			int nodeSizeInBytes = input.readInt();

			if (shouldSkip) {
				input.skipBytes(nodeSizeInBytes);
				return;
			}

			isUkWord = input.readBoolean();
			isUsWord = input.readBoolean();

			int numChildren = input.readShort();

			if (numChildren > 0) {
				String[] childStrings = new String[numChildren];
				for (int i = 0; i < numChildren; i++) {
					int length = input.readByte();

					byte[] bytes = new byte[length];
					input.readFully(bytes);

					String string = new String(bytes);
					if (depth == 0 && transitionMap.contains(string) || depth > 0 && transitionMap.canTransition(lastChar, string)) {
						childStrings[i] = string;
					}
				}

				for (int i = 0; i < numChildren; i++) {
					// Need to read the node regardless of whether we end up keeping it. This is to
					// ensure that we traverse the InputStream in the right order.
					boolean shouldSkipChild = childStrings[i] == null;
					Node childNode = new Node(input, transitionMap, availableStrings, shouldSkipChild, childStrings[i], depth + 1);
					if (!shouldSkipChild) {
						children.put(childStrings[i], childNode);
					}
				}
			}
		}

		@Override
		public void writeNode(OutputStream output) throws IOException {

			ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
			DataOutputStream tempOutputData = new DataOutputStream(tempOutput);

			tempOutputData.writeBoolean(isUkWord);
			tempOutputData.writeBoolean(isUsWord);

			tempOutputData.writeShort(children.size());
			Set<Map.Entry<String, Node>> entries = children.entrySet();
			for (Map.Entry<String, Node> entry : entries) {
				String character = entry.getKey();
				tempOutputData.writeByte(character.length());
				tempOutputData.writeBytes(character);
			}

			for (Map.Entry<String, Node> entry : entries) {
				entry.getValue().writeNode(tempOutputData);
			}

			DataOutputStream outputData = new DataOutputStream(output);
			outputData.writeInt(tempOutput.size());
			outputData.write(tempOutput.toByteArray());
		}

		@Override
		public TrieNode addSuffix(String word, int currentPosition, boolean usWord, boolean ukWord) {
			Node child = ensureChildAt(word, currentPosition);

			if (currentPosition == word.length() - 1) {
				child.isUsWord |= usWord;
				child.isUkWord |= ukWord;
				return child;
			} else {
				return child.addSuffix(word, nextPosition(word, currentPosition), usWord, ukWord);
			}
		}

		private int nextPosition(String word, int currentPosition) {
			return currentPosition + getCharAt(word, currentPosition).length();
		}

		// TODO: Refactor special handling of "Q" into interface for other Locales to use.
		private String getCharAt(String word, int position) {
			String character = Character.toString(word.charAt(position));
			if (character.equals("q") && word.length() > position && Character.toString(word.charAt(position + 1)).equals("u")) {
				return "qu";
			}
			return character;
		}

		private Node maybeChildAt(String word, int position) {
			return children.get(getCharAt(word, position));
		}

		private Node maybeChildAt(String childChar) {
			return children.get(childChar);
		}

		private Node ensureChildAt(String word, int position) {
			String character = getCharAt(word, position);
			Node existingNode = maybeChildAt(word, position);
			if (existingNode == null) {
				Node node = new Node();
				children.put(character, node);
				return node;
			} else {
				return existingNode;
			}
		}

		@Override
		public boolean usWord() {
			return isUsWord;
		}

		@Override
		public boolean ukWord() {
			return isUkWord;
		}

		@Override
		public boolean isTail() {
			return children.size() == 0;
		}

		@Override
		public boolean isWord(String word, int currentPosition, boolean usWord, boolean ukWord) {
			if (currentPosition == word.length()) {
				return usWord && isUsWord || ukWord && isUkWord;
			}

			Node childNode = maybeChildAt(word, currentPosition);
			return childNode != null && childNode.isWord(word, nextPosition(word, currentPosition), usWord, ukWord);
		}

		private boolean isAnyWord(String word, int currentPosition) {
			if (currentPosition == word.length()) {
				return isUsWord || isUkWord;
			}

			Node childNode = maybeChildAt(word, currentPosition);
			return childNode != null && childNode.isAnyWord(word, nextPosition(word, currentPosition));
		}
	}

	public static class Deserializer implements net.healeys.trie.Deserializer<StringTrie> {
		@Override
		public StringTrie deserialize(InputStream stream, TransitionMap transitionMap, boolean usDict, boolean ukDict) throws IOException {
			return new StringTrie(stream, transitionMap);
		}

		@Override
		public StringTrie deserialize(InputStream stream) throws IOException {
			return new StringTrie(stream);
		}
	}

}
