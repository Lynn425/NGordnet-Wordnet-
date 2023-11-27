package ngordnet.main;

import java.io.*;
import java.util.*;

public class Graph {
    private List<List<String>> nodes; // synsets

    private HashMap<Integer, List<Integer>> wordnet; // graph

    public Graph(String hyponyms, String synsets) {
        addNodes(synsets);
        addEdge(hyponyms);
    }

    /*
    add nodes to the graph, using adjacent list. the index of list is the identity of nodes.
     */
    private void addNodes(String synsets) {
        int lineNum = getFileLineNum(synsets);
        nodes = new ArrayList<>(lineNum);
        for (int i = 0; i < lineNum; i++) {
            nodes.add(new ArrayList<>());
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(synsets));
            for (int i = 0; i < lineNum; i++) {
                String line = reader.readLine();
                String[] words = line.split(",")[1].split(" ");
                for (String word : words) {
                    nodes.get(i).add(word);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    add edges among nodes
     */
    private void addEdge(String hyponyms) {
        this.wordnet = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(hyponyms));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] item = line.split(",");
                int parent = Integer.parseInt(item[0]);
                List<Integer> children = new ArrayList<>();
                for (int i = 1; i < item.length; i++) {
                    children.add(Integer.valueOf(item[i]));
                }
                if (wordnet.containsKey(parent)) {
                    wordnet.get(parent).addAll(children);
                } else {
                    wordnet.put(parent, children);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    find all synsets that contain the given word
     */
    public List<Integer> findSynsets(String word) {
        List<Integer> parents = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).contains(word)) {
                parents.add(i);
            }
        }
        return parents;
    }

    /*
    find hyponyms with the given word
     */
    public Set<String> findHyponymsOfWord(String word) {
        List<Integer> wordSynsets = findSynsets(word);
        Set<Integer> synsets = new HashSet<>();
        for (int synset : wordSynsets) {
            synsets.addAll(findChildren(synset));
        }
        Set<String> words = new TreeSet<>();
        for (int synset : synsets) {
            words.addAll(this.nodes.get(synset));
        }
        return words;
    }

    /*
    find hyponyms with the given words
     */
    public Set<String> findHyponyms(List<String> list) {
        if (list.isEmpty()) {
            return null;
        }
        int index = 0;
        String word = list.get(0);
        index++;
        Set<String> commonHyponyms = findHyponymsOfWord(word);
        while (index < list.size()) {
            String otherWord = list.get(index);
            index++;
            Set<String> otherHyponyms = findHyponymsOfWord(otherWord);
            commonHyponyms.retainAll(otherHyponyms);
        }
        return commonHyponyms;
    }

    /*
    find all the children of the given node recursively, including the node itself
     */
    public Set<Integer> findChildren(Integer node) {
        Set<Integer> children = new HashSet<>();
        findChildrenHelper(node, children);
        children.add(node);
        return children;
    }

    /*
    help function of findChildren
     */
    public void findChildrenHelper(Integer node, Set<Integer> children) {
        if (!wordnet.containsKey(node)) {
            children.add(node);
        }
        if (wordnet.containsKey(node)) {
            for (Integer child : wordnet.get(node)) {
                findChildrenHelper(child, children);
                children.add(child);
            }
        }
    }

    /*
    return the number of lines of the given file
     */
    public static int getFileLineNum(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))) {
            lineNumberReader.skip(Long.MAX_VALUE);
            return lineNumberReader.getLineNumber();
        } catch (IOException e) {
            return -1;
        }
    }
}
