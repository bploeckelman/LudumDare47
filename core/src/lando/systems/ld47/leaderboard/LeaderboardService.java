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
    private static final Array<LeaderboardScore> placeholderScores = new Array<>();

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
        populatePlaceholderScores();
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
        Net.HttpRequest request = requester.newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .build();
        GameHud hud = (gameScreen == null) ? null : gameScreen.gameHud;
        Gdx.net.sendHttpRequest(request, new GetScoreResponseListener(json, hud));
    }

    // ------------------------------------------------------------------------

    private final String[] names = new String[] {
            "Brian", "Other Brian", "Doug", "Pete", "Matt", "Luke", "Jeffrey",
            "Cherry", "Asuka", "Tom Bombadil", "Fancy Lad", "Vlad", "Tenchu",
            "Richard Alpert", "Terrence McKenna", "Derek Yu", "Grumpy Cat",
            "Philosoraptor", "Ozric Tentacles", "Cloud Strife"
    };
    public String getRandomName() {
        return names[MathUtils.random(0, names.length - 1)];
    }

    private void populatePlaceholderScores() {
        LeaderboardService.placeholderScores.clear();
        float minScore = 300;
        float maxScore = 20000;
        for (int i = 0; i < 20; ++i) {
            LeaderboardService.placeholderScores.add(new LeaderboardScore(getRandomName(), (int) MathUtils.random(minScore, maxScore)));
        }
    }

    // ------------------------------------------------------------------------

    // NOTE: not much to do with the 'post score' response
    public static class PostResponseListener implements Net.HttpResponseListener {

        @Override
        public void handleHttpResponse(Net.HttpResponse httpResponse) {
            // TODO: maybe pop up a message box or something if things fail? (or just a toast so its out of the way)
        }

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
            int responseStatus = httpResponse.getStatus().getStatusCode();
            if (responseStatus != 200) {
                if (hud != null) {
                    hud.updateScores(LeaderboardService.placeholderScores);
                }
                return;
            }

            String responseBody = httpResponse.getResultAsString();
            if (responseBody.isEmpty()) return;

            Array<LeaderboardScore> scores = json.fromJson(Array.class, LeaderboardScore.class, responseBody);
            if (scores != null && hud != null) {
//                Gdx.app.log("leaderboard-json", "deserialized scoes:\n" + scores.toString("\n"));
                hud.updateScores(scores);
            }
        }

        @Override
        public void failed(Throwable t) {
            // TODO: pop up a message box or toast saying we couldn't fetch scores?
            if (hud != null) {
                hud.updateScores(LeaderboardService.placeholderScores);
            }
        }

        @Override
        public void cancelled() {}

    }

}
