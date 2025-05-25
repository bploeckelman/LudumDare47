package lando.systems.ld47.leaderboard;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.ui.GameHud;

public class LeaderboardService {

    private static final float scoreRefreshInterval = 20f;

//    private static final String url = "https://lando.systems/ld47-leaderboard";
//    private static final Array<LeaderboardScore> placeholderScores = new Array<>();

//    private final Json json;
//    private final HttpRequestBuilder requester;

    private final GameScreen gameScreen;
    private boolean isInitialized = false;
    private float scoreRefreshTimer;

    private final Array<LeaderboardScore> db_dump_scores_20250525 = new Array<>();

    public LeaderboardService(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
//        this.json = new Json();
//        this.json.setOutputType(JsonWriter.OutputType.json);
//        this.requester = new HttpRequestBuilder();
        this.scoreRefreshTimer = scoreRefreshInterval;
//        populatePlaceholderScores();
        populateScoresSnapshot();
    }

    public void update(float dt) {
        // only need to get scores once
        if (isInitialized) return;

        scoreRefreshTimer -= dt;
        if (scoreRefreshTimer <= 0f) {
            scoreRefreshTimer = scoreRefreshInterval;
            getScores();
        }
    }

    public void postScore(String name, int score) {
//        Net.HttpRequest request = requester.newRequest()
//                .method(Net.HttpMethods.POST)
//                .url(url)
//                .header("Content-Type", "application/json")
//                .content(json.toJson(new LeaderboardScore(name, score)))
//                .build();
//        Gdx.net.sendHttpRequest(request, new PostResponseListener());
    }

    public void getScores() {
//        Net.HttpRequest request = requester.newRequest()
//                .method(Net.HttpMethods.GET)
//                .url(url)
//                .build();
//        GameHud hud = (gameScreen == null) ? null : gameScreen.gameHud;
//        Gdx.net.sendHttpRequest(request, new GetScoreResponseListener(json, hud));
        GameHud hud = (gameScreen == null) ? null : gameScreen.gameHud;
        if (hud != null) {
            hud.updateScores(db_dump_scores_20250525);
            isInitialized = true;
        }
    }

    // ------------------------------------------------------------------------

//    private final String[] names = new String[] {
//            "Brian", "Other Brian", "Doug", "Pete", "Matt", "Luke", "Jeffrey",
//            "Cherry", "Asuka", "Tom Bombadil", "Fancy Lad", "Vlad", "Tenchu",
//            "Richard Alpert", "Terrence McKenna", "Derek Yu", "Grumpy Cat",
//            "Philosoraptor", "Ozric Tentacles", "Cloud Strife"
//    };
//
//    public String getRandomName() {
//        return names[MathUtils.random(0, names.length - 1)];
//    }
//
//    private void populatePlaceholderScores() {
//        LeaderboardService.placeholderScores.clear();
//        float minScore = 300;
//        float maxScore = 20000;
//        for (int i = 0; i < 20; ++i) {
//            LeaderboardService.placeholderScores.add(new LeaderboardScore(getRandomName(), (int) MathUtils.random(minScore, maxScore)));
//        }
//    }
//
//    // ------------------------------------------------------------------------
//
//    // NOTE: not much to do with the 'post score' response
//    public static class PostResponseListener implements Net.HttpResponseListener {
//
//        @Override
//        public void handleHttpResponse(Net.HttpResponse httpResponse) {
//            // TODO: maybe pop up a message box or something if things fail? (or just a toast so its out of the way)
//        }
//
//        @Override
//        public void failed(Throwable t) {}
//
//        @Override
//        public void cancelled() {}
//
//    }
//
//    public static class GetScoreResponseListener implements Net.HttpResponseListener {
//
//        private final Json json;
//        private final GameHud hud;
//
//        public GetScoreResponseListener(Json json, GameHud hud) {
//            this.json = json;
//            this.hud = hud;
//        }
//
//        @Override
//        public void handleHttpResponse(Net.HttpResponse httpResponse) {
//            int responseStatus = httpResponse.getStatus().getStatusCode();
//            if (responseStatus != 200) {
//                if (hud != null) {
//                    hud.updateScores(LeaderboardService.placeholderScores);
//                }
//                return;
//            }
//
//            String responseBody = httpResponse.getResultAsString();
//            if (responseBody.isEmpty()) return;
//
//            Array<LeaderboardScore> scores = json.fromJson(Array.class, LeaderboardScore.class, responseBody);
//            if (scores != null && hud != null) {
////                Gdx.app.log("leaderboard-json", "deserialized scoes:\n" + scores.toString("\n"));
//                hud.updateScores(scores);
//            }
//        }
//
//        @Override
//        public void failed(Throwable t) {
//            // TODO: pop up a message box or toast saying we couldn't fetch scores?
//            if (hud != null) {
//                hud.updateScores(LeaderboardService.placeholderScores);
//            }
//        }
//
//        @Override
//        public void cancelled() {}
//
//    }

    /**
     * NOTE(brian): deprecated the high score service after migrating to new VM 2025-05-25
     *  these are the scores listed in the database as of that date and this is setup
     *  to hardcode these scores going forward so we don't have to run the ld47-leaderboard.jar
     *  and maintain a mysql server and db for this app anymore.
     */
    public void populateScoresSnapshot() {
        db_dump_scores_20250525.clear();
        db_dump_scores_20250525.add(new LeaderboardScore("pete", 7000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("cherry", 12400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("CHong", 16600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("MOOMIN", 32200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("FLURRY", 3000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymous", 2400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymous", 600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("eL FURIAS", 2200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("ssssssssss", 2400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Best", 3800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("madison", 10200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("alek", 11800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("No Grapes", 10800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("madison", 29000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Minibetrayal", 14000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("probably doug", 4000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("nexus", 9400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("tyrell", 10800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Siddharth", 1200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("slapshot", 6200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("so close", 10000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("topwise", 8800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymuse", 11400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("ludumite", 7200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("next up", 8600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("oh dear why", 13000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("harris one", 17000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("cascadia", 14600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("groceries", 16600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("craig", 5600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Marco", 3800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Hadesfury", 17600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Andidebob", 7000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("UGLYSWEDISHFISH", 17600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("MeCubed", 21600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("    Steamdr4gon", 5800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Party Lead Vi", 32000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("marcusuke", 2600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymous", 6400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("ragnar", 11200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("s", 6600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("alex", 6200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("rash", 17600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Randy", 9600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("quaaludum dare", 25000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("sanojian", 2800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("gladys", 13600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("skleembof", 1600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("skleembof", 2400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("glo", 24800, false));
        db_dump_scores_20250525.add(new LeaderboardScore("glo", 3000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("SYNONYMOUS", 14600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Sage", 20600, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Zeekza", 40200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("ironman is a lie", 200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymous", 6200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymous", 1000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("anonymous", 3000, false));
        db_dump_scores_20250525.add(new LeaderboardScore("mp And DG", 2200, false));
        db_dump_scores_20250525.add(new LeaderboardScore("Marky mark", 8400, false));
        db_dump_scores_20250525.add(new LeaderboardScore("douG", 8200, false));
    }
}
