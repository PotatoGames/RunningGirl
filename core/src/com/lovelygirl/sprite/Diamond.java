package com.lovelygirl.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lovelygirl.main.MainGame;

import static com.lovelygirl.handle.Constant.*;

public class Diamond {
	// ��������
	private Protagonist protagonist;
	// ������ʯ
	private TextureRegion[] diamonds;

	public Diamond(Protagonist protagonist) {
		// ���Ǹ�ֵ
		this.protagonist = protagonist;
		// ��ȡ��ʯ����
		Texture tex = MainGame.assetManager.get("images/diamond.png");
		// ʵ������ʯ����
		diamonds = new TextureRegion[3];
		// ������ʯ����
		for (int i = 0; i < diamonds.length; i++) {
			// ʵ��������
			diamonds[i] = new TextureRegion(tex, i * 16, 0, 16, 16);
		}
	}

	public void render(SpriteBatch batch) {
		// ��ȡ����Ŀ����ײ����
		short bits = protagonist.getBody().getFixtureList().first()
				.getFilterData().maskBits;
		// ��ʼ����
		batch.begin();
		// �ж��Ƿ�Ϊ��ɫľ�壬����Ǿͻ��ƺ�ɫ��ʯ
		if ((bits & BLOCK_RED) != 0) {
			batch.draw(diamonds[0], 280, 200);
		}
		// �ж��Ƿ�Ϊ��ɫľ�壬����Ǿͻ�����ɫ��ʯ
		if ((bits & BLOCK_GREEN) != 0) {
			batch.draw(diamonds[1], 280, 200);
		}
		// �ж��Ƿ�Ϊ��ɫľ�壬����Ǿͻ�����ɫ��ʯ
		if ((bits & BLOCK_BLUE) != 0) {
			batch.draw(diamonds[2], 280, 200);
		}
		batch.end();
	}

}
