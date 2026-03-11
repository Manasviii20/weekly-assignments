public class AnalyticsDashboard {
    private java.util.HashMap<String, Integer> pageViews = new java.util.HashMap<>();
    private java.util.HashMap<String, java.util.HashSet<String>> uniqueVisitors = new java.util.HashMap<>();
    private java.util.HashMap<String, Integer> trafficSources = new java.util.HashMap<>();
    public AnalyticsDashboard() {
        // background thread to refresh dashboard every 5 seconds
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    getDashboard();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    // process incoming event
    public synchronized void processEvent(String url, String userId, String source) {
        // count page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // track unique visitors
        uniqueVisitors.computeIfAbsent(url, k -> new java.util.HashSet<>()).add(userId);

        // count traffic sources
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public synchronized void getDashboard() {
        System.out.println("=== Real-Time Dashboard ===");

        java.util.PriorityQueue<java.util.Map.Entry<String, Integer>> pq =
                new java.util.PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        pq.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");
        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            java.util.Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int uniques = uniqueVisitors.getOrDefault(url, new java.util.HashSet<>()).size();
            System.out.println(rank + ". " + url + " - " + views + " views (" + uniques + " unique)");
            rank++;
        }

        // Traffic sources
        System.out.println("Traffic Sources:");
        for (java.util.Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("===========================");
    }

    public static void main(String[] args) throws Exception {
        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/sports/championship", "user_789", "direct");
        dashboard.processEvent("/sports/championship", "user_123", "google");

        Thread.sleep(12000);
    }
}