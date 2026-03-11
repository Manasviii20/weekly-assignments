

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Ecommerce {

    // productId -> stockCount
    private Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // productId -> waiting list (FIFO)
    private Map<String, LinkedHashMap<Integer, Long>> waitingLists = new ConcurrentHashMap<>();

    // constructor: initialize with product and stock
    public Ecommerce(String productId, int initialStock) {
        inventory.put(productId, new AtomicInteger(initialStock));
        waitingLists.put(productId, new LinkedHashMap<>());
    }

    // check stock availability
    public String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) return "Product not found";
        return productId + " → " + stock.get() + " units available";
    }

    // purchase item safely
    public synchronized String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) return "Product not found";

        // atomic decrement
        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success! User " + userId + " purchased. " + remaining + " units remaining.";
        } else {
            // add to waiting list
            LinkedHashMap<Integer, Long> waitingList = waitingLists.get(productId);
            waitingList.put(userId, System.currentTimeMillis());
            return "Stock unavailable. User " + userId + " added to waiting list, position #" + waitingList.size();
        }
    }

    // view waiting list
    public List<Integer> getWaitingList(String productId) {
        LinkedHashMap<Integer, Long> waitingList = waitingLists.get(productId);
        if (waitingList == null) return Collections.emptyList();
        return new ArrayList<>(waitingList.keySet());
    }

    // main demo
    public static void main(String[] args) {
        Ecommerce manager = new Ecommerce("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB"));

        // simulate purchases
        for (int i = 1; i <= 105; i++) {
            System.out.println(manager.purchaseItem("IPHONE15_256GB", i));
        }

        System.out.println("Waiting list: " + manager.getWaitingList("IPHONE15_256GB"));
    }
}
