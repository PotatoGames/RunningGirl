package com.lovelygirl.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.lovelygirl.main.MainGame;

public class Flame extends GameSprite {
	// ʵ������������
	private Texture tex;

	public Flame(Body body) {
		super(body);
		// ��������ֵ
		tex = MainGame.assetManager.get("images/flame.png", Texture.class);
		// ������������
		TextureRegion[] region = TextureRegion.split(tex, 32, 32)[0];
		// ʵ��������
		setAnimation(region, 1 / 8f);
		// �������
		width = region[0].getRegionWidth();
		// �����߶�
		height = region[0].getRegionHeight();
	}
}
