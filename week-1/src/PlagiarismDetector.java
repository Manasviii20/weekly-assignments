public class PlagiarismDetector {

    // Entry: n-gram -> set of document IDs
    private java.util.HashMap<String, java.util.Set<String>> ngramIndex = new java.util.HashMap<>();

    // analyze a document and add its n-grams to the index
    public void analyzeDocument(String docId, String text, int n) {
        String[] words = text.split("\\s+");
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            String ngram = sb.toString().trim();

            ngramIndex.computeIfAbsent(ngram, k -> new java.util.HashSet<>()).add(docId);
        }
        System.out.println("Analyzed " + docId + " → Extracted " + (words.length - n + 1) + " n-grams");
    }

    // compare two documents by counting matching n-grams
    public double compareDocuments(String docA, String docB) {
        int matches = 0;
        int total = 0;

        for (java.util.Map.Entry<String, java.util.Set<String>> entry : ngramIndex.entrySet()) {
            java.util.Set<String> docs = entry.getValue();
            if (docs.contains(docA)) {
                total++;
                if (docs.contains(docB)) {
                    matches++;
                }
            }
        }

        double similarity = total == 0 ? 0 : (matches * 100.0 / total);
        System.out.println("Found " + matches + " matching n-grams between " + docA + " and " + docB);
        System.out.println("Similarity: " + similarity + "%");
        return similarity;
    }

    // demo
    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        // sample documents
        String doc1 = "the quick brown fox jumps over the lazy dog";
        String doc2 = "the quick brown fox leaps over the lazy dog";
        String doc3 = "an unrelated essay with different words entirely";

        detector.analyzeDocument("essay_123", doc1, 5);
        detector.analyzeDocument("essay_089", doc2, 5);
        detector.analyzeDocument("essay_092", doc3, 5);

        detector.compareDocuments("essay_123", "essay_089"); // should show high similarity
        detector.compareDocuments("essay_123", "essay_092"); // should show low similarity
    }
}