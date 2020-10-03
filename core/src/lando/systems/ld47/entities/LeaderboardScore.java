package lando.systems.ld47.entities;

public class LeaderboardScore implements Comparable{
    public long id;
    public String name;
    public long score;

    public LeaderboardScore(){
        name = "";
        score = 0;
    }

    public LeaderboardScore(long score) {
        name = "";
        this.score = score;
    }

    public LeaderboardScore(long id, String name, long score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public boolean nameComplete() {
        return name.length() >= 3;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof LeaderboardScore){
            LeaderboardScore other = (LeaderboardScore)o;
            if (other.score < score) return -1;
            if (other.score == score) return 0;
            return 1;
        }
        return 0;
    }
}