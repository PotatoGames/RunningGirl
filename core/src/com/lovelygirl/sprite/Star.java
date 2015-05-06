package com.lovelygirl.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.lovelygirl.main.MainGame;

public class Star extends GameSprite {
	// ������������
	private Texture tex;

	public Star(Body body) {
		super(body);
		// ʵ������������
		tex = MainGame.assetManager.get("images/stars.png", Texture.class);
		// ʵ�������Ƕ�������
		TextureRegion[] region = TextureRegion.split(tex, 24, 24)[0];
		// ʵ��������
		setAnimation(region, 1 / 12f);
	}
}