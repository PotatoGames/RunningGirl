package com.lovelygirl.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.lovelygirl.main.MainGame;

public class Flame extends GameSprite {
	// 实例化火焰纹理
	private Texture tex;

	public Flame(Body body) {
		super(body);
		// 火焰纹理赋值
		tex = MainGame.assetManager.get("images/flame.png", Texture.class);
		// 火焰纹理数组
		TextureRegion[] region = TextureRegion.split(tex, 32, 32)[0];
		// 实例化动画
		setAnimation(region, 1 / 8f);
		// 动画宽度
		width = region[0].getRegionWidth();
		// 动画高度
		height = region[0].getRegionHeight();
	}
}
