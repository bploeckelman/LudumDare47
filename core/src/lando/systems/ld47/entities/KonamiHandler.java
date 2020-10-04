package lando.systems.ld47.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import lando.systems.ld47.Audio;
import lando.systems.ld47.Game;

public class KonamiHandler extends InputAdapter {

    private int[] sequence = new int[]{Input.Keys.UP, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.B, Input.Keys.A, Input.Keys.B, Input.Keys.A};
    private int index = 0;

    private Game game;
    public KonamiHandler(Game game) {
        this.game = game;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (index >= sequence.length) index = 0;
        if (sequence[index] == keyCode) {
            if (++index == sequence.length) {
                game.audio.playSound(Audio.Sounds.cha_ching);
                game.idkfa = true;
                index = 0;
            }
        } else {
            index = 0;
        }
        return false;
    }
}