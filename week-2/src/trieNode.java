import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    PriorityQueue<Query> topQueries = new PriorityQueue<>(Comparator.comparingInt(q -> q.frequency));
    boolean isEnd;
}

class Query {
    String query;
    int frequency;
    Query(String q, int f) { query = q; frequency = f; }
}

class AutocompleteSystem {
    private TrieNode root = new TrieNode();
    private Map<String, Integer> freqMap = new HashMap<>();

    public void insert(String query, int frequency) {
        freqMap.put(query, freqMap.getOrDefault(query, 0) + frequency);
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            node.topQueries.add(new Query(query, freqMap.get(query)));
            if (node.topQueries.size() > 10) node.topQueries.poll(); // keep top 10
        }
        node.isEnd = true;
    }

    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return Collections.emptyList();
        }
        List<String> results = new ArrayList<>();
        for (Query q : node.topQueries) results.add(q.query + " (" + q.frequency + ")");
        return results;
    }
}