package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.TechiesWorld;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Player;

/**
 * Created by shiping on 2/3/16.
 */
public class Mine extends Item {

    public enum State { PLACED, TRANSITION, ARMED, HIDDEN, EXPLODING }
    private State currentState;
    private State previousState;
    private float stateTime;

    private TextureRegion placedTexture;
    private TextureRegion transitionTexture;
    private TextureRegion armedTexture;
    private Animation explosion;

    public Mine(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        Texture texture = new Texture("PNGPack.png");
        placedTexture = new TextureRegion((texture), 0, 0, 70, 70);
        transitionTexture = new TextureRegion((texture), 70, 0, 70, 70);
        armedTexture = new TextureRegion((texture), 140, 0, 70, 70);

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // get explosion animation frames and add to explosion Animation
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(new Texture("explode(64x64).png"), i*64, 0, 64, 64));
        }
        explosion = new Animation(0.1f, frames);

        // clear frames
        frames.clear();

        currentState = previousState = State.PLACED;

        setRegion(placedTexture);
    }


    @Override
    public void defineItem() {
        Vector2 position;
        if (currentState == State.ARMED) {
            position = body.getPosition();
            world.destroyBody(body);
        } else {
            position = new Vector2(getX(),getY());
        }
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(24 / TechiesWorld.PPM, 24/ TechiesWorld.PPM);
        if (currentState == State.ARMED) {
            fdef.filter.categoryBits = TechiesWorld.MINE_BIT;
        } else {
            fdef.filter.categoryBits = TechiesWorld.NOTHING_BIT;
        }
        fdef.filter.maskBits = TechiesWorld.MINE_BIT |
                TechiesWorld.WALL_BIT |
                TechiesWorld.PLAYER_BIT ;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case PLACED:
                region = placedTexture;
                break;
            case TRANSITION:
                region = transitionTexture;
                break;
            case ARMED:
                region = armedTexture;
                break;
            case HIDDEN:
                region = null;
                break;
            case EXPLODING:
                region = explosion.getKeyFrame(stateTime, true);
                break;
            default:
                region = armedTexture;
                break;
        }

        stateTime = currentState == previousState? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void contact(Player player) {
        if (currentState == State.ARMED) {
            currentState = State.EXPLODING;
            player.setPlayerDead();
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setRegion(getFrame(dt));
        if (currentState == State.PLACED && stateTime > 0.5) {
            currentState = State.TRANSITION;
        } else if (currentState == State.TRANSITION && stateTime > 2) {
            currentState = State.ARMED;
            defineItem();
        } else if (currentState == State.EXPLODING & stateTime > 0.5) {
            destroy();
        }
        // update sprite to correspond with position of b2body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

    }

    public State getState() {
        return currentState;
    }
}
