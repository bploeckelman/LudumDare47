package lando.systems.ld47.leaderboard;

public class LeaderboardScore implements Comparable<LeaderboardScore> {

    public String name;
    public int score;
    public boolean currentUser;

    public LeaderboardScore() {
        this.name = "";
        this.score = 0;
        this.currentUser = false;
    }

    public LeaderboardScore(int score) {
        this.name = "";
        this.score = score;
        this.currentUser = false;
    }

    public LeaderboardScore(String name, int score) {
        this.name = name;
        this.score = score;
        this.currentUser = false;
    }

    public LeaderboardScore(String name, int score, boolean currentUser) {
        this.name = name;
        this.score = score;
        this.currentUser = currentUser;
    }

    public long getScore() { return score; }
    public String getName() { return name; }
    public boolean isCurrentUser() { return currentUser; }

    public void setScore(int score) { this.score = score; }
    public void setName(String name) { this.name = name; }
    public void setCurrentUser(Boolean currentUser) { this.currentUser = currentUser; }

    public boolean nameComplete() {
        return name.length() >= 3;
    }

    @Override
    public int compareTo(LeaderboardScore other) {
        if (other.getScore() < getScore()) return -1;
        if (other.getScore() == getScore()) {
            if (isCurrentUser()) {
                return -1;
            }
            return 0;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "[Score: " + getName() + ", " + getScore() + "]";
    }

}
