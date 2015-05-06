package com.lovelygirl.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lovelygirl.main.MainGame;

public class Tree {
	// ������������
	private Texture texture;

	public Tree() {
		// ʵ������������
		texture = MainGame.assetManager.get("images/tree.png");
	}

	public void render(SpriteBatch batch) {
		// ��ʼ����
		batch.begin();
		// ���Ʊ���
		batch.draw(texture, 0, 0);
		// ��������
		batch.end();
	}
}
