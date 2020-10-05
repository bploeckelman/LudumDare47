package lando.systems.ld47.ui;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.GameState;

public class NextUI extends HoldUI {

    public NextUI(GameState gameState, float x, float y, String text) {
        super(gameState, x, y, text);
    }

    @Override
    public void update(float dt) {
        hold = gameState.viewNext();
        // DO NOT CALL SUPER - or you will pop this as well on Hold pressed
        hold.scale = .7f;
        hold.center(center);
    }

    @Override
    public void hit() {
        super.hit();
        gameState.popNext();
    }
}
