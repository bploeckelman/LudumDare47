package lando.systems.ld47.ui;

import lando.systems.ld47.GameState;

public class NextUI extends HoldUI {

    public NextUI(GameState gameState, float x, float y, String text) {
        super(gameState, x, y, text);
    }

    @Override
    public void update(float dt) {
        hold = gameState.viewNext();
        super.update(dt);
    }

    public void punchBox() {
        // play sound and animate!
        gameState.popNext();
        super.punchBox();
    }
}
