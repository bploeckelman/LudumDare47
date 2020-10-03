package lando.systems.ld47;

import lando.systems.ld47.entities.Tetrad;
import lando.systems.ld47.screens.GameScreen;

public class GameState {

    public GameScreen gameScreen;
    public Assets assets;

    private Tetrad next;

    public GameState(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.assets = gameScreen.assets;
        popNext();
    }

    public Tetrad popNext() {
        Tetrad tetrad = next;
        next = new Tetrad(gameScreen.game);
        return tetrad;
    }

    // for hud
    public Tetrad getNext() {
        return next;
    }
}
