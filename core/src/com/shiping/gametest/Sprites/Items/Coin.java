package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Player;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 28/3/16.
 */
public class Coin extends Item {
    private int amount;
    private Animation textureAnimation;
    private float stateTimer;

    public Coin(PlayScreen screen, float x, float y, Player player) {
        super(screen, x, y);

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(new Texture("PNGPack.png"), i*64, 70, 64, 64 ));
        }

        textureAnimation = new Animation(0.1f, frames);
        setRegion(textureAnimation.getKeyFrame(0.1f));
        // to do ANIMATION
        if (player.getScore() / 3 < 200) {
            amount = 200;
        } else amount = player.getScore() / 3;
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(24 / TechiesWorld.PPM);
        fdef.filter.categoryBits = TechiesWorld.COIN_BIT;
        fdef.filter.maskBits = TechiesWorld.WALL_BIT |
                TechiesWorld.PLAYER_BIT ;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void contact(Player player) {
        destroy();
        player.addScore(this);
    }

    @Override
    public void update(float dt) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }
}