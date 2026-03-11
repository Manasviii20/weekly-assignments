import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class TokenBucket {
    private final int maxTokens;
    private final int refillRate; // tokens per hour
    private AtomicInteger tokens;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        long tokensToAdd = (elapsed / 3600000) * refillRate; // per hour
        if (tokensToAdd > 0) {
            tokens.set(Math.min(maxTokens, tokens.get() + (int) tokensToAdd));
            lastRefillTime = now;
        }
    }

    public int getRemainingTokens() {
        refill();
        return tokens.get();
    }
}

class RateLimiter {
    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public boolean checkRateLimit(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                id -> new TokenBucket(1000, 1000));
        return bucket.allowRequest();
    }

    public int getRemaining(String clientId) {
        return buckets.getOrDefault(clientId, new TokenBucket(1000, 1000)).getRemainingTokens();
    }
}