import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long time;
     Transaction(int id, int amt, String m, long t) {
        this.id = id; amount = amt; merchant = m; time = t;
    }
}

class FraudDetector {
    public List<int[]> findTwoSum(List<Transaction> txs, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();
        for (Transaction t : txs) {
            if (map.containsKey(target - t.amount)) {
                result.add(new int[]{map.get(target - t.amount).id, t.id});
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public List<Transaction> detectDuplicates(List<Transaction> txs) {
        Map<String, List<Transaction>> map = new HashMap<>();
        for (Transaction t : txs) {
            String key = t.amount + "_" + t.merchant;
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }
        List<Transaction> duplicates = new ArrayList<>();
        for (List<Transaction> list : map.values()) {
            if (list.size() > 1) duplicates.addAll(list);
        }
        return duplicates;
    }
}