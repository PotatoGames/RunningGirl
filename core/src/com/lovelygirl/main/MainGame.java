package com.lovelygirl.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lovelygirl.screen.GameScreen;
import com.lovelygirl.screen.MainScreen;

public class MainGame extends Game {
	// ��Ϸ��ǩ
	public static final String TAG = "GameTest";
	// �����ǩ
	public static final String TITLE = "LovelyGirl";
	// �Ӿ���
	public static final int ViewPort_WIDTH = 320;
	// �Ӿ�߶�
	public static final int ViewPort_HEIGHT = 240;
	// ���������С
	public static final int Scale = 2;
	// ���黭��
	private SpriteBatch batch;
	// �������
	private OrthographicCamera camera;
	// �ؼ����
	private OrthographicCamera uiCam;
	// ����������
	public static MainScreen mainScreen;
	// ������Ϸ����
	public static GameScreen gameScreen;
	// ������Դ������
	public static AssetManager assetManager;

	@Override
	public void create() {
		// ʵ������Դ������
		assetManager = new AssetManager();
		// Ԥ������Ϸ����
		assetManager.load("images/girl.png", Texture.class);
		assetManager.load("images/stars.png", Texture.class);
		assetManager.load("images/diamond.png", Texture.class);
		assetManager.load("images/flame.png", Texture.class);
		assetManager.load("images/select.atlas", TextureAtlas.class);
		assetManager.load("images/bg.png", Texture.class);
		assetManager.load("images/tree.png", Texture.class);
		assetManager.load("images/start.png", Texture.class);
		// Ԥ������Ϸ��Ч
		assetManager.load("audio/music.ogg", Music.class);
		assetManager.load("audio/switch.wav", Sound.class);
		assetManager.load("audio/diamond.ogg", Sound.class);
		assetManager.load("audio/contact.wav", Sound.class);
		assetManager.load("audio/jump.wav", Sound.class);
		assetManager.load("audio/select.wav", Sound.class);
		// ������Ϸ��Դ
		assetManager.finishLoading();
		// ������Ϸ��������
		Music music = assetManager.get("audio/music.ogg");
		// ѭ��������Ϸ��������
		music.play();

		// ʵ�������黭��
		batch = new SpriteBatch();
		// ʵ�����������
		camera = new OrthographicCamera();
		// ��������Ӿ�
		camera.setToOrtho(false, ViewPort_WIDTH, ViewPort_HEIGHT);
		// ʵ�����ؼ����
		uiCam = new OrthographicCamera();
		// ���ÿؼ�����Ӿ�
		uiCam.setToOrtho(false, ViewPort_WIDTH, ViewPort_HEIGHT);
		// ʵ����������
		mainScreen = new MainScreen(this);
		// ʵ������Ϸ����
		gameScreen = new GameScreen(this);
		// �ö���Ϸ����
		setScreen(mainScreen);
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public OrthographicCamera getUiCam() {
		return uiCam;
	}
}
