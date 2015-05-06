package com.lovelygirl.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lovelygirl.main.MainGame;

import static com.lovelygirl.handle.Constant.*;

public class Diamond {
	// 声明主角
	private Protagonist protagonist;
	// 声明钻石
	private TextureRegion[] diamonds;

	public Diamond(Protagonist protagonist) {
		// 主角赋值
		this.protagonist = protagonist;
		// 获取钻石纹理
		Texture tex = MainGame.assetManager.get("images/diamond.png");
		// 实例化钻石纹理
		diamonds = new TextureRegion[3];
		// 遍历钻石数组
		for (int i = 0; i < diamonds.length; i++) {
			// 实例化纹理
			diamonds[i] = new TextureRegion(tex, i * 16, 0, 16, 16);
		}
	}

	public void render(SpriteBatch batch) {
		// 获取主角目标碰撞属性
		short bits = protagonist.getBody().getFixtureList().first()
				.getFilterData().maskBits;
		// 开始绘制
		batch.begin();
		// 判断是否为红色木板，如果是就绘制红色钻石
		if ((bits & BLOCK_RED) != 0) {
			batch.draw(diamonds[0], 280, 200);
		}
		// 判断是否为绿色木板，如果是就绘制绿色钻石
		if ((bits & BLOCK_GREEN) != 0) {
			batch.draw(diamonds[1], 280, 200);
		}
		// 判断是否为蓝色木板，如果是就绘制蓝色钻石
		if ((bits & BLOCK_BLUE) != 0) {
			batch.draw(diamonds[2], 280, 200);
		}
		batch.end();
	}

}
