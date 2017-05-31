package net.healeys.trie;

import java.io.IOException;
import java.io.InputStream;

public interface Deserializer<T extends Trie> {

    /**
     * Given an input stream that comprises of a full dictionary converted into a trie of some sort,
     * this deserializes a subset of the full trie. The subset contains only the letters in
     * {@param lettersToKeep}.
     */
    T deserialize(InputStream stream, TransitionMap transitionMap, boolean usDict, boolean ukDict) throws IOException;

    /**
     * Same as {@link #deserialize(InputStream, TransitionMap transitionMap, boolean usDict, boolean ukDict)} except it deserializes the entire trie.
     * @see #deserialize(InputStream, TransitionMap transitionMap, boolean usDict, boolean ukDict)
     */
    T deserialize(InputStream stream) throws IOException;

}
