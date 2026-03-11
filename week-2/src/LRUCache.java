import java.util.*;

class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private final int capacity;
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}

class MultiLevelCache {
    private LRUCache<String, String> L1 = new LRUCache<>(10000);
    private Map<String, String> L2 = new HashMap<>(); // simulate SSD
    private Map<String, String> L3 = new HashMap<>(); // simulate DB

    public String getVideo(String id) {
        if (L1.containsKey(id)) return "L1 HIT: " + L1.get(id);
        if (L2.containsKey(id)) {
            String data = L2.get(id);
            L1.put(id, data); // promote
            return "L2 HIT: " + data;
        }
        String data = L3.getOrDefault(id, "DB fetch");
        L2.put(id, data);
        return "L3 HIT: " + data;
    }
}