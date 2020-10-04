package lando.systems.ld47.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.ui.GameHud;

public class LeaderboardService {

    private static final String url = "https://lando.systems/ld47-leaderboard";
    private static final float scoreRefreshInterval = 20f;

    private final Json json;
    private final HttpRequestBuilder requester;
    private final GameScreen gameScreen;
    private float scoreRefreshTimer;

    public LeaderboardService(GameScreen gameScreen) {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.requester = new HttpRequestBuilder();
        this.gameScreen = gameScreen;
        this.scoreRefreshTimer = scoreRefreshInterval;
    }

    public void update(float dt) {
        scoreRefreshTimer -= dt;
        if (scoreRefreshTimer <= 0f) {
            scoreRefreshTimer = scoreRefreshInterval;
            getScores();
        }
    }

    public void postScore(String name, int score) {
        Net.HttpRequest request = requester.newRequest()
                .method(Net.HttpMethods.POST)
                .url(url)
                .header("Content-Type", "application/json")
                .content(json.toJson(new LeaderboardScore(name, score)))
                .build();
        Gdx.net.sendHttpRequest(request, new PostResponseListener());
    }

    public void getScores() {
        Gdx.app.log("leaderboard-get", "getting updated scores...");
        Net.HttpRequest request = requester.newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .build();
        Gdx.net.sendHttpRequest(request, new GetScoreResponseListener(json, gameScreen.gameHud));
        Gdx.app.log("leaderboard-get", "request sent.");
    }

    private final String[] names = new String[] { "Brian", "Other Brian", "Doug", "Pete", "Matt", "Luke", "Jeffrey", "Cherry", "Asuka", "Tom Bombadil", "Fancy Lad", "Tenchu: Stealth Assassin"};
    public String getRandomName() {
        return names[MathUtils.random(0, names.length - 1)];
    }

    // ------------------------------------------------------------------------

    // NOTE: not much to do with the 'post score' response
    public static class PostResponseListener implements Net.HttpResponseListener {

        // TODO: maybe pop up a message box or something if things fail? (or just a toast so its out of the way)

        @Override
        public void handleHttpResponse(Net.HttpResponse httpResponse) {}

        @Override
        public void failed(Throwable t) {}

        @Override
        public void cancelled() {}

    }

    public static class GetScoreResponseListener implements Net.HttpResponseListener {

        private final Json json;
        private final GameHud hud;

        public GetScoreResponseListener(Json json, GameHud hud) {
            this.json = json;
            this.hud = hud;
        }

        @Override
        public void handleHttpResponse(Net.HttpResponse httpResponse) {
            Gdx.app.log("leaderboard-get-response", "response received...");

            // TODO: add some sort of error handling in here? not sure if 'failed()' gets called for non-200 responses or just 'no responses'
            int responseStatus = httpResponse.getStatus().getStatusCode();
            if (responseStatus != 200) return;

            String responseBody = httpResponse.getResultAsString();
            if (responseBody.isEmpty()) return;
            Gdx.app.log("leaderboard-json", "preparing to deserialize scores....");

            Array<LeaderboardScore> scores = json.fromJson(Array.class, LeaderboardScore.class, responseBody);
            if (scores != null) {
                Gdx.app.log("leaderboard-json", "deserialized scores:\n" + scores.toString("\n"));
                hud.updateScores(scores);
            }
        }

        @Override
        public void failed(Throwable t) {
            // TODO - should probably handle this (by faking the data in the leaderboard or something?)
        }

        @Override
        public void cancelled() {
            // TODO - not sure when this actually gets called?
        }

    }

}
