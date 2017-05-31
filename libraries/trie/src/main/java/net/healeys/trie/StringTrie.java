package net.healeys.trie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class StringTrie implements Trie {

	private final Node rootNode;

	public StringTrie() {
		rootNode = new Node();
	}

	private StringTrie(InputStream in) throws IOException {
		this(in, null);
	}

	private StringTrie(InputStream in, TransitionMap transitionMap) throws IOException {
		rootNode = new Node(in, transitionMap);
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
		private final Queue<Integer> positions;

		public StringSolution(String word, Queue<Integer> positions) {
			this.word = word;
			this.positions = positions;
		}

		@Override
		public String getWord() {
			return word;
		}

		@Override
		public int getMask() {
			return 0;
		}

		public Queue<Integer> getPositions() {
			return positions;
		}
	}

/*

	@Override
	public LinkedHashMap<String, Solution> solver(TransitionMap transitionMap, WordFilter filter) {
		LinkedHashMap<String, Solution> solutions = new LinkedHashMap<>();

		for (int y = 0; y < transitionMap.getSize(); y ++ ) {
			for (int x = 0; x < transitionMap.getSize(); x ++ ) {
				int position = y * transitionMap.getWidth() + x;
				StringTrie.Node childNode = rootNode.maybeChildAt(transitionMap.valueAt(position));
				if (childNode != null) {
					recursiveSolver(solutions, childNode, transitionMap, x, y, filter,  0);
				}
			}
		}

		return solutions;
	}

	private void recursiveSolver(LinkedHashMap<String, Solution> solutions, StringTrie.Node node, TransitionMap transitionMap, int x, int y, WordFilter filter, int depth) {
		if (node.usWord() || node.ukWord()) {
			solutions.put(node)
		}
		for (int toY = 0; toY < transitionMap.getSize(); toY ++ ) {
			for (int toX = 0; toX < transitionMap.getSize(); toX ++ ) {
				if (toX == x && toY == y || !transitionMap.canTransition(x, y, toX, toY)) {
					continue;
				}

				String letterAt = transitionMap.valueAt(toY * transitionMap.getWidth() + toX);

			}
		}
	}

*/

	private void recursiveSolver(
			TransitionMap transitions,
			WordFilter wordFilter,
			StringTrie.Node node,
			int pos,
			Set<Integer> usedPositions,
			StringBuilder prefix,
			LinkedHashMap<String, Solution> solutions) {

		if (node.usWord() || node.ukWord()) {
			String w = new String(prefix);
			if(wordFilter == null || wordFilter.isWord(w)) {
				// TODO: the positions used by this solution need to be kept properly.
				solutions.put(w, new StringSolution(w, new LinkedList<Integer>()));
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

				// TODO: I think this is to be removed and replaced with just prefix.append(valueAt).
				if(valueAt.toLowerCase().equals("q")) {
					prefix.append('u');
				}

				recursiveSolver(transitions, wordFilter, nextNode, toPosition, usedPositions, prefix, solutions);

				// TODO: I think we can use a regular string rather than a string builder, but I can
				//       also see how this reduces allocations greatly.
				prefix.deleteCharAt(prefix.length() - 1);
				if(valueAt.toLowerCase().equals("q")) {
					prefix.deleteCharAt(prefix.length() - 1);
				}
			}
		}

		usedPositions.remove(pos);
	}

	@Override
	public LinkedHashMap<String, Solution> solver(TransitionMap transitions, WordFilter filter) {

		LinkedHashMap<String, Solution> solutions = new LinkedHashMap<>();
		StringBuilder prefix = new StringBuilder(transitions.getSize() + 1);

		for(int i=0; i < transitions.getSize(); i ++) {
			String value = transitions.valueAt(i);
			StringTrie.Node nextNode = rootNode.maybeChildAt(value);
			if (nextNode == null) {
				continue;
			}

			prefix.append(value);
			if(value.toLowerCase().startsWith("q")) {
				prefix.append('u');
			}

			recursiveSolver(transitions, filter, nextNode, i, new HashSet<Integer>(), prefix, solutions);

			prefix.deleteCharAt(prefix.length() - 1);
			if(value.toLowerCase().startsWith("q")) {
				prefix.deleteCharAt(prefix.length() - 1);
			}
		}

		return solutions;
	}

	private static class Node implements TrieNode {

		private final Map<String, Node> children = new HashMap<>();

		private boolean isUsWord;
		private boolean isUkWord;

		private Node() {

		}

		private Node(InputStream in, TransitionMap transitionMap) throws IOException {
			DataInputStream input = new DataInputStream(in);

			isUkWord = input.readBoolean();
			isUsWord = input.readBoolean();

			int numChildren = input.readShort();

			if (numChildren > 0) {
				List<String> childStrings = new ArrayList<>(numChildren);
				List<Boolean> keepString = new ArrayList<>(numChildren);
				for (int i = 0; i < numChildren; i++) {
					int length = input.readByte();

					byte[] bytes = new byte[length];
					input.readFully(bytes);

					String string = new String(bytes);
					boolean shouldKeep = transitionMap == null;
					if (transitionMap != null) {
						for (int j = 0; j < transitionMap.getSize(); j++) {
							if (string.toLowerCase().equals(transitionMap.valueAt(j).toLowerCase())) {
								shouldKeep = true;
								break;
							}
						}
					}

					keepString.add(i, shouldKeep);
					childStrings.add(i, string);
				}

				for (int i = 0; i < numChildren; i++) {
					// Need to read the node regardless of whether we end up keeping it. This is to
					// ensure that we traverse the InputStream in the right order.
					Node childNode = new Node(input, transitionMap);
					if (keepString.get(i)) {
						children.put(childStrings.get(i), childNode);
					}
				}
			}
		}

		@Override
		public void writeNode(OutputStream out) throws IOException {
			DataOutputStream output = new DataOutputStream(out);

			output.writeBoolean(isUkWord);
			output.writeBoolean(isUsWord);

			output.writeShort(children.size());
			Set<Map.Entry<String, Node>> entries = children.entrySet();
			for (Map.Entry<String, Node> entry : entries) {
				String character = entry.getKey();
				output.writeByte(character.length());
				output.writeBytes(character);
			}

			for (Map.Entry<String, Node> entry : entries) {
				entry.getValue().writeNode(out);
			}
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

		// TODO: Remove dependency on Locale.ENGLISH.
		// TODO: Refactor special handling of "Q" into interface for other Locales to use.
		private String getCharAt(String word, int position) {
			String character = Character.toString(word.charAt(position)).toLowerCase(Locale.ENGLISH);
			if (character.equals("q") && word.length() > position && Character.toString(word.charAt(position + 1)).toLowerCase(Locale.ENGLISH).equals("u")) {
				return "qu";
			}
			return Character.toString(word.charAt(position)).toLowerCase(Locale.ENGLISH);
		}

		private Node maybeChildAt(String word, int position) {
			return children.get(getCharAt(word, position));
		}

		private Node maybeChildAt(String childChar) {
			return children.get(childChar.toLowerCase());
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
