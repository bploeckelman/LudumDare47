package lando.systems.ld47.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class LeaderboardService {

    private final Json json;
    private final HttpRequestBuilder requester;

    public LeaderboardService() {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.requester = new HttpRequestBuilder();
    }

    public void postScore(String name, long score) {
        Net.HttpRequest request = requester.newRequest()
                .method(Net.HttpMethods.POST)
                .url("http://localhost:8080/")
                .header("Content-Type", "application/json")
                .content(json.toJson(new Score(name, score)))
                .build();
        Gdx.net.sendHttpRequest(request, new ResponseListener());
    }

    public void getScores() {
        Net.HttpRequest request = requester.newRequest()
                .method(Net.HttpMethods.GET)
                .url("http://localhost:8080/")
                .build();
        // TODO: the response listener should update the leaderboard hud when it receives a successful response
        Gdx.net.sendHttpRequest(request, new ResponseListener());
    }

    private String[] names = new String[] { "Brian", "Other Brian", "Doug", "Pete", "Matt", "Luke", "Jeffrey", "Cherry", "Asuka", "Tom Bombadil", "Fancy Lad", "Tenchu: Stealth Assassin"};
    public String getRandomName() {
        return names[MathUtils.random(0, names.length - 1)];
    }

    // ------------------------------------------------------------------------

    public static class ResponseListener implements Net.HttpResponseListener {

        @Override
        public void handleHttpResponse(Net.HttpResponse httpResponse) {
            Gdx.app.log("leaderboard","response: " + httpResponse.getStatus().getStatusCode() + " " + httpResponse.getResultAsString());
        }

        @Override
        public void failed(Throwable t) {
            Gdx.app.log("leaderboard", "request failed: " + t.getMessage());
        }

        @Override
        public void cancelled() {
            Gdx.app.log("leaderboard", "request cancelled");
        }
    }

}
