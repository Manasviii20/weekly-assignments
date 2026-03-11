public class DNSCache {

    // Entry class for cache
    static class DNSEntry {
        String domain;
        String ipAddress;
        long timestamp;     // when cached
        long expiryTime;    // TTL in ms

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.timestamp = System.currentTimeMillis();
            this.expiryTime = this.timestamp + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU cache using LinkedHashMap
    private java.util.LinkedHashMap<String, DNSEntry> cache;
    private int capacity;
    private int hits = 0;
    private int misses = 0;

    // ✅ Correct constructor
    public DNSCache(int capacity) {
        this.capacity = capacity;
        this.cache = new java.util.LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(java.util.Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        // background thread to clean expired entries
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // check every second
                    cleanExpired();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    // resolve domain
    public synchronized String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            return "Cache HIT → " + entry.ipAddress;
        } else {
            misses++;
            // simulate upstream DNS query (random IP for demo)
            String ip = "172.217.14." + (100 + (int)(Math.random() * 100));
            DNSEntry newEntry = new DNSEntry(domain, ip, 5); // TTL = 5s for demo
            cache.put(domain, newEntry);

            if (entry != null && entry.isExpired()) {
                return "Cache EXPIRED → Query upstream → " + ip;
            } else {
                return "Cache MISS → Query upstream → " + ip;
            }
        }
    }

    // clean expired entries
    private synchronized void cleanExpired() {
        java.util.Iterator<java.util.Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<String, DNSEntry> e = it.next();
            if (e.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    // stats
    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + hitRate + "%, Hits=" + hits + ", Misses=" + misses;
    }

    // demo
    public static void main(String[] args) throws Exception {
        DNSCache dns = new DNSCache(3);

        System.out.println(dns.resolve("google.com"));   // MISS
        Thread.sleep(200);
        System.out.println(dns.resolve("google.com"));   // HIT
        Thread.sleep(6000); // wait > TTL
        System.out.println(dns.resolve("google.com"));   // EXPIRED → MISS

        System.out.println(dns.resolve("yahoo.com"));    // MISS
        System.out.println(dns.resolve("bing.com"));     // MISS
        System.out.println(dns.resolve("duckduckgo.com")); // MISS → triggers LRU eviction

        System.out.println(dns.getCacheStats());
    }
}