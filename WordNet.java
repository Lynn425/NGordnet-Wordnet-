package ngordnet.main;

import ngordnet.ngrams.NGramMap;

import java.util.List;
import java.util.Set;

public class WordNet {
    private Graph graph;
    private String synsetFile;
    private String hyponymFile;

    public WordNet(String synsetFile, String hyponymFile) {
        this.synsetFile = synsetFile;
        this.hyponymFile = hyponymFile;
        graph = new Graph(hyponymFile, synsetFile);
    }

    public Set<String> findHyponyms(List<String> words) {
        return graph.findHyponyms(words);
    }
}
