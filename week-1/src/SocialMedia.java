import java.util.*;

public class SocialMedia {

    private UsernameChecker usernameChecker;

    // constructor (preferred over initializer block)
    public SocialMedia() {
        usernameChecker = new UsernameChecker();
    }

    // demo method
    public void runDemo() {
        System.out.println("john_doe available? " + usernameChecker.checkAvailability("john_doe"));
        System.out.println("jane_smith available? " + usernameChecker.checkAvailability("jane_smith"));

        System.out.println("Suggestions for john_doe: " + usernameChecker.suggestAlternatives("john_doe"));

        usernameChecker.checkAvailability("admin");
        usernameChecker.checkAvailability("admin");
        usernameChecker.checkAvailability("admin");

        System.out.println("Most attempted: " + usernameChecker.getMostAttempted());
    }

    // main method
    public static void main(String[] args) {
        SocialMedia app = new SocialMedia();
        app.runDemo();
    }
}

// Separate class for username logic
class UsernameChecker {

    private Map<String, Integer> users = new HashMap<>();
    private Map<String, Integer> attempts = new HashMap<>();

    public UsernameChecker() {
        users.put("john_doe", 1);
        users.put("admin", 2);
        users.put("user123", 3);
    }

    // check availability
    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    // suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String modified = username.replace("_", ".");
        if (!users.containsKey(modified)) {
            suggestions.add(modified);
        }

        return suggestions;
    }

    // get most attempted username
    public String getMostAttempted() {
        String maxUser = "";
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser.isEmpty() ? "No attempts yet" : maxUser + " (" + maxCount + " attempts)";
    }
}