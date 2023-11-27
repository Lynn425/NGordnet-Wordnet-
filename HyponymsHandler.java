package ngordnet.main;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;
import ngordnet.ngrams.TimeSeries;

import java.util.*;

public class HyponymsHandler extends NgordnetQueryHandler {

    private WordNet wm;
    private NGramMap map;

    public HyponymsHandler(WordNet wn, NGramMap ngm) {
        this.wm = wn;
        this.map = ngm;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        int k = q.k();
        Set<String> wordset = wm.findHyponyms(words);
        if (words.isEmpty()) {
            return " ";
        }
        if (k == 0) {
            return wordset.toString();
        } else {
            Map<String, Double> count = new HashMap<>();
            for (String word : wordset) {
                TimeSeries ts = map.countHistory(word, startYear, endYear);
                if (ts.size() != 0) {
                    double num = 0;
                    for (double val : ts.values()) {
                        num += val;
                    }
                    count.put(word, num);
                }
            }
            if (count.size() <= k) {
                List<String> keys = new ArrayList<>();
                List<String> keys2 = new ArrayList<>();
                List<String> ret = new ArrayList<>();
                for (String key : count.keySet()) {
                    keys.add(key);
                    keys2.add(key);
                }
                for (String key2 : keys2) {
                    String min = keys.get(0);
                    for (String key : keys) {
                        if (key.compareTo(min) < 0) {
                            min = key;
                        }
                    }
                    ret.add(min);
                    keys.remove(min);
                }
                return ret.toString();
            } else {
                Set<String> kWords = new HashSet<>();
                for (int i = 0; i < k; i++) {
                    double max = 0;
                    String maxWord = "";
                    for (String word : wordset) {
                        if (count.containsKey(word)) {
                            if (count.get(word) > max) {
                                max = count.get(word);
                                maxWord = word;
                            }
                        }
                    }
                    kWords.add(maxWord);
                    count.remove(maxWord);
                }
                List<String> keys = new ArrayList<>();
                List<String> keys2 = new ArrayList<>();
                List<String> ret = new ArrayList<>();
                for (String key : kWords) {
                    keys.add(key);
                    keys2.add(key);
                }
                for (String key2 : keys2) {
                    String min = keys.get(0);
                    for (String key : keys) {
                        if (key.compareTo(min) < 0) {
                            min = key;
                        }
                    }
                    ret.add(min);
                    keys.remove(min);
                }
                return ret.toString();
            }
        }
    }
}
