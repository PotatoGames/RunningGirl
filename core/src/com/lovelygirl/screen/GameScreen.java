package com.lovelygirl.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.lovelygirl.handle.Box2DContactListener;
import com.lovelygirl.main.MainGame;
import com.lovelygirl.sprite.Diamond;
import com.lovelygirl.sprite.Flame;
import com.lovelygirl.sprite.Protagonist;
import com.lovelygirl.sprite.Star;
import com.lovelygirl.sprite.Tree;

import static com.lovelygirl.handle.Constant.*;
import static com.lovelygirl.main.MainGame.*;

public class GameScreen extends GirlScreen {
	// ����ʱ����Ⱦ����
	private boolean Box2DDebug = true;
	// �����������
	private World world;
	// ��������������Ⱦ��
	private Box2DDebugRenderer box2dRender;
	// ��������������Ⱦ���
	private OrthographicCamera box2dCamera;
	// �������������
	private Box2DContactListener bcl;
	// ����
	Body body;
	// ������ͼ
	private TiledMap tileMap;
	// ������Ƭ��С
	private float tileSize;
	// ������ͼ���
	private float mapWidth;
	// ������ͼ�߶�
	private float mapHeight;
	// ������ͼ��Ⱦ��
	private OrthogonalTiledMapRenderer mapRender;
	// ��ͼ���
	public static int level;
	// ��������
	private Protagonist protagonist;
	// ������Ⱦ״̬ʱ��
	private float statetime;
	// ������������
	private Array<Star> stars;
	// ������������
	private Array<Flame> flames;
	// ������ʯ
	private Diamond diamond;
	// ��������
	private Tree tree;

	public GameScreen(MainGame game) {
		super(game);
		// ʵ��������������������
		bcl = new Box2DContactListener();
		// ʵ������������
		world = new World(new Vector2(0, -9.81f), true);
		// ��������������������
		world.setContactListener(bcl);
		// ʵ��������������Ⱦ��
		box2dRender = new Box2DDebugRenderer();
		// ʵ���������������
		box2dCamera = new OrthographicCamera();
		// ��������Ӿ�
		box2dCamera.setToOrtho(false, ViewPort_WIDTH / RATE, ViewPort_HEIGHT / RATE);
		// ��ʼ����Ϸ�����
		this.init();
	}

	public void init() {
		// ��������
		createActor();
		// ������ͼ
		createMap();
		// ��������
		createStar();
		// ��������
		createFlame();
		// ʵ������ʯ
		diamond = new Diamond(protagonist);
		//ʵ������������
		tree = new Tree();
	}
	
	private void createFlame() {
		// ʵ���������������
		flames = new Array<Flame>();
		// ������������
		MapLayer ml = tileMap.getLayers().get("flame");
		// �����Ϊ�ռ�����
		if(ml == null) return;
		// ���������ʽ
		BodyDef bodyDef = new BodyDef();
		// �����������
		bodyDef.type = BodyType.StaticBody;
		// ʵ����Բ��ͼ��
		CircleShape shape = new CircleShape();
		// ����Բ��ͼ�ΰ뾶
		shape.setRadius(5 / RATE);
		// ʵ�����о�
		FixtureDef fixtureDef = new FixtureDef();
		// ��ͼ�������
		fixtureDef.shape = shape;
		// ���ø���Ϊ������
		fixtureDef.isSensor = true;
		// �趨����������ײ����
		fixtureDef.filter.categoryBits = FLAME;
		// �趨����Ŀ����ײ����
		fixtureDef.filter.maskBits = PLAYER;
		// ������������
		for(MapObject mo : ml.getObjects()) {
			// ����x,y������
			float x = 0;
			float y = 0;
			// ����ͼ�����
			if(mo instanceof EllipseMapObject) {
				EllipseMapObject emo = (EllipseMapObject) mo;
				// ��ֵ�������x��y����
				x = emo.getEllipse().x / RATE;
				y = emo.getEllipse().y / RATE;
			}
			// �趨����λ��
			bodyDef.position.set(x, y);
			// ʵ��������
			Body body = world.createBody(bodyDef);
			// ʵ�����о߲����趨�о��û�����
			body.createFixture(fixtureDef).setUserData("flame");
			// ʵ�����������
			Flame f = new Flame(body);
			// �趨�����û�����
			body.setUserData(f);
			// ��ӻ�����󵽻�������
			flames.add(f);
		}
		shape.dispose();
	}
	
	private void createActor() {
		// ���������ʽ����
		BodyDef bodyDef = new BodyDef();
		// �����о�
		FixtureDef fixtureDef = new FixtureDef();
		// ʵ���������
		PolygonShape shape = new PolygonShape();
		// ��������
		body = world.createBody(bodyDef);
		/*
		 * ����������ģ��
		 */
		// ����λ��
		bodyDef.position.set(60 / RATE, 200 / RATE);
		// �����������
		bodyDef.type = BodyType.DynamicBody;
		// �趨ˮƽ�����ٶ�
		bodyDef.linearVelocity.set(1.0f, 0);
		// ��������
		body = world.createBody(bodyDef);
		// �趨������״
		shape.setAsBox(13 / RATE, 18 / RATE);
		// ��������о������
		fixtureDef.shape = shape;
		// �趨������������ײ����
		fixtureDef.filter.categoryBits = PLAYER;
		// �趨������Ŀ����ײ����
		fixtureDef.filter.maskBits = BLOCK_RED | STAR | FLAME;
		// �����о�
		body.createFixture(fixtureDef).setUserData("box");
		/*
		 * ����������foot
		 */
		// ���ô�������״
		shape.setAsBox(13 / RATE, 2 / RATE, new Vector2(0, -18 / RATE), 0);
		// �������о���ͼ�ΰ�
		fixtureDef.shape = shape;
		// ���ô�����������ײ����
		fixtureDef.filter.categoryBits = PLAYER;
		// ���ô�����Ŀ����ײ����
		fixtureDef.filter.maskBits = BLOCK_RED;
		// �趨�ø����Ƿ���Ϊ������
		fixtureDef.isSensor = true;
		// �ﶨ���弰�о߲��������û�����
		body.createFixture(fixtureDef).setUserData("foot");
		// ʵ��������
		protagonist = new Protagonist(body);
	}
	
	private void createMap() {
		// ���ص�ͼ
		try {
			// ʵ������ͼ
			tileMap = new TmxMapLoader().load("maps/" + "level" + level
					+ ".tmx");
		} catch (Exception e) {
			// �쳣��ӡ��Ϣ
			System.out.println("�����ҵ�level" + level + ".tmx�ļ�");
			// �˳�Ӧ��
			Gdx.app.exit();
		}
		// ʵ������ͼ��Ⱦ��
		mapRender = new OrthogonalTiledMapRenderer(tileMap);
		// ��ֵ��ͼ��Ƭ��С
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
		// ��ֵ��ͼ���
		mapWidth = tileMap.getProperties().get("width", Integer.class);
		// ��ֵ��ͼ�߶�
		mapHeight = tileMap.getProperties().get("height", Integer.class);
		// ����ͼ��
		TiledMapTileLayer layer;
		// ��ȡ��ɫ��ͼͼ��
		layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
		// �󶨸������ɫ��ͼͼ��
		createMapLayer(layer, BLOCK_RED);
		// ��ȡ��ɫ��ͼͼ��
		layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
		// �󶨸�������ɫ��ͼ
		createMapLayer(layer, BLOCK_GREEN);
		// ��ȡ��ɫ��ͼ
		layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
		// �󶨸�������ɫ��ͼ
		createMapLayer(layer, BLOCK_BLUE);
		
	}
	private void createMapLayer(TiledMapTileLayer layer, short bits){
		// ʵ����������ʽ������
		BodyDef bodyDef = new BodyDef();
		// ʵ�����о���ʽ������
		FixtureDef fixtureDef = new FixtureDef();
		// �������е�Ԫ��,row�У�col��
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {

				// ��ȡcell
				Cell cell = layer.getCell(col, row);
				if (cell == null)
					continue;
				if (cell.getTile() == null)
					continue;

				// ����body ���оߣ�
				bodyDef.type = BodyType.StaticBody;
				bodyDef.position.set(
						(col + 0.5f) * tileSize / RATE, 
						(row + 0.5f) * tileSize / RATE);
				ChainShape cs = new ChainShape();
				//������ʽͼ�Σ����Ҵ���������ͼ������
				Vector2[] v = new Vector2[3];
				v[0] = new Vector2(-tileSize / 2 / RATE,-tileSize / 2 / RATE);
				v[1] = new Vector2(-tileSize / 2 / RATE,  tileSize / 2 / RATE );
				v[2] = new Vector2(tileSize/ 2 / RATE,  tileSize / 2 / RATE);
				// ������ʽͼ��
				cs.createChain(v);
				// ���ûָ���Ϊ0
				fixtureDef.friction = 0;
				// �󶨼о�����ʽͼ��
				fixtureDef.shape = cs;
				// ����ͼ�εĹ���������
				fixtureDef.filter.categoryBits = bits;
				// �趨��ͼĬ����ײ����
				fixtureDef.filter.maskBits = PLAYER;
				// ���ô�����
				fixtureDef.isSensor = false;
				// ������ͼ����
				world.createBody(bodyDef).createFixture(fixtureDef);
			}
		}
	}

	private void createStar(){
		
		// �����ܲ�������
		stars = new Array<Star>();
		// ��ȡ�����
		MapLayer ml = tileMap.getLayers().get("heart");
		// �����Ϊ�գ�ֱ�ӷ���
		if(ml == null) return;
		// ʵ�������Ǹ��嶨��
		BodyDef bodyDef = new BodyDef();
		// �趨��������
		bodyDef.type = BodyType.StaticBody;
		// ʵ�����о�
		FixtureDef fixtureDef = new FixtureDef();
		// ʵ����Բ��ͼ�ζ���
		CircleShape shape = new CircleShape();
		// ����Բ�θ���뾶
		shape.setRadius(8 / RATE);
		// ͼ�θ���󶨼о�
		fixtureDef.shape = shape;
		// �趨��ǰ����Ϊ������
		fixtureDef.isSensor = true;
		// �趨��ǰ���屾����ײ����
		fixtureDef.filter.categoryBits = STAR;
		// �趨��ǰ����Ŀ����ײ����
		fixtureDef.filter.maskBits = PLAYER;
		// ����������ж���
		for (MapObject mo: ml.getObjects()) {
			// ���Ƕ���X������
			float x = 0;
			// ���Ƕ���Y������
			float y = 0;
			// ��ȡ����x,y����
			if (mo instanceof EllipseMapObject) {
				EllipseMapObject emo = (EllipseMapObject) mo;
				x = emo.getEllipse().x / RATE;
				y = emo.getEllipse().y / RATE;
			}
			// �趨����λ��
			bodyDef.position.set(x, y);
			// ʵ�������Ǹ���
			Body body = world.createBody(bodyDef);
			// �����о�
			body.createFixture(fixtureDef).setUserData("heart");
			// ʵ��������
			Star s = new Star(body);
			// ������ǵ���ǰ����
			stars.add(s);
			// �趨�����û�����
			body.setUserData(s);
		}
	}
	
	private void switchDiamond() {

		// ��ȡ�ײ����崫������Ŀ����ײ����
		Filter filter = protagonist.getBody().getFixtureList().get(1)
				.getFilterData();
		// ���չ���ȥ��Ŀ����ײ����
		short bits = filter.maskBits;

		// ���ײ����帳ֵ���������ԣ�ʹ�������Ǹ�����������ײ��
		if (bits == BLOCK_RED) {
			// ��ɫĿ��תΪ��ɫľ������
			bits = BLOCK_GREEN;
		} else if (bits == BLOCK_GREEN) {
			// ��ɫľ������תΪ��ɫľ������
			bits = BLOCK_BLUE;
		} else if (bits == BLOCK_BLUE) {
			// ��ɫľ������תΪ��ɫľ������
			bits = BLOCK_RED;
		}
		// �ײ�����������Ŀ����ײ���Ը�ֵΪbits
		filter.maskBits = bits;
		// ��bits�������Ǹ���ײ�����������
		protagonist.getBody().getFixtureList().get(1).setFilterData(filter);

		// ���������ϲ��ָ���Ŀ����ײ����Ϊ����������
		bits |= STAR | FLAME;
		// ���ù���ȥĿ����ײ����
		filter.maskBits = bits;
		// ���������ϰ벿�ָ�����ײ����
		protagonist.getBody().getFixtureList().get(0).setFilterData(filter);
	}
	
	@Override
	public void update(float dt) {
		// �����������
		handleInput();
		// ����
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// ������������״̬
		world.step(dt, 6, 2);
		
		Array<Body> bodies = bcl.getRemoveBodies();
		for (int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			stars.removeValue((Star) b.getUserData(), true);
			world.destroyBody(b);
			protagonist.collectStars();
			// ���ųԵ��������
			Sound s = assetManager.get("audio/diamond.ogg");
			s.play();
		}
		bodies.clear();
		
		//�ж����ǹ���
		if (protagonist.getBody().getPosition().x * RATE > mapWidth * tileSize) {
			// ���Źؿ�ѡ����Ч
			Sound s = assetManager.get("audio/select.wav");
			s.play();
			// ����ͨ�س�ʼ����ʼ��������Ԫ��
			game.mainScreen.init();
			// ������������Ʊ���Ϊ�٣������ƿ�ʼ����
			MainScreen.Debug = false;
			// �ö���ǰ��Ϸ������
			game.setScreen(MainGame.mainScreen);
		}
		
		if(protagonist.getBody().getPosition().y < 0){
			// ������ײ������Ч
			Sound s = assetManager.get("audio/contact.wav");
			s.play();
			// �����ǵ����ͼ�⣬��ʼ����ʼ��������Ԫ��
			game.mainScreen.init();
			// ������������Ʊ���Ϊ�棬���ƿ�ʼ����
			game.mainScreen.Debug = true;
			// �ö���ǰ��Ϸ������
			game.setScreen(game.mainScreen);
		}
		if(protagonist.getBody().getLinearVelocity().x < 0.001f){
			// ������ײ������Ч
			Sound s = assetManager.get("audio/contact.wav");
			s.play();
			// ��������ײ��ľ�壬�����ٶ�Ϊ0ʱ���ж�������������ʼ����Ϸ������Ԫ��
			game.mainScreen.init();
			// ������������Ʊ���Ϊ�棬������Ϸ��ʼ����
			game.mainScreen.Debug = true;
			// ��ʼ����ǰ��Ϸ����������Դ
			this.init();
			// ָ����ǰ��Ϸ������
			game.setScreen(game.mainScreen);
		}
		
		if(bcl.isContactFlame()){
			// ������ײľ����Ч
			Sound s = assetManager.get("audio/contact.wav");
			s.play();
			// �����������棬������������ʼ��������������Դ
			game.mainScreen.init();
			// ������Ϸ��������Ʊ���Ϊ�棬������Ϸ������
			game.mainScreen.Debug = true;
			//���ö���ǰ��Ϸ������
			game.setScreen(MainGame.mainScreen);
		}
	}

	@Override
	public void handleInput() {

		if (Gdx.input.isKeyJustPressed(Keys.Z)
				|| (Gdx.input.justTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 2)) {
			if(bcl.isOnPlatform()){
				protagonist.getBody().applyForceToCenter(0, 250, true);
			}
		}

		if (Gdx.input.isKeyJustPressed(Keys.X)
				|| ((Gdx.input.justTouched() && Gdx.input.getX() < Gdx.graphics.getWidth() / 2))) {
			switchDiamond();
		}
	}
	
	private void adjustCamera() {
		// ���ͶӰê��С������Ӿ��һ��ʱ�����������ƶ����
		if (camera.position.x < camera.viewportWidth / 2) {
			camera.position.x = camera.viewportWidth / 2;
		}
		// �����ê��X��������ڵ�ͼ���ʱ�����������ƶ����
		if (camera.position.x > (tileMap.getProperties().get("width",Integer.class) * tileSize)
				- camera.viewportWidth / 2) {
			camera.position.x = (tileMap.getProperties().get("width",Integer.class) * tileSize)
					- camera.viewportWidth / 2;
		}
	}

	private void adjustBox2DCamera() {
		// ������ʱ�����ê��X������С������Ӿ��ȵ�һ��ʱ���������ƶ����
		if (box2dCamera.position.x < box2dCamera.viewportWidth / 2) {
			box2dCamera.position.x = box2dCamera.viewportWidth / 2;
		}
		// ������ʱ�����ê��X�������������������ʱ���������ƶ����
		if (box2dCamera.position.x > (tileMap.getProperties().get("width",Integer.class)
				/ RATE * tileSize) - box2dCamera.viewportWidth / 2) {
			box2dCamera.position.x = (tileMap.getProperties().get("width",Integer.class)
					/ RATE * tileSize)- box2dCamera.viewportWidth / 2;
		}
	}
	

	@Override
	public void render(float delta) {
		// ������Ϸ�߼�
		update(delta);
		// �������ͶӰ����ê��λ��
		camera.position.set(protagonist.getPosition().x * RATE + ViewPort_WIDTH
				/ 4, ViewPort_HEIGHT / 2, 0);
		// ��������������
		adjustCamera();
		// �������״̬
		camera.update();
		// ���û��Ʊ��������ΪUI���
		batch.setProjectionMatrix(uiCam.combined);
		// ���Ʊ���
		tree.render(batch);
		//��Ⱦʱ��
		statetime +=delta;
		// ���þ��黭�ʻ�ͼ����
		batch.setProjectionMatrix(camera.combined);
		// ��Ⱦ����
		protagonist.render(batch, statetime);
		// ��Ⱦ����
		for (int i = 0; i < stars.size; i++) {
			// ��ȡ������ÿ�����ǲ��һ���
			stars.get(i).render(batch, statetime);
		}
		// ��Ⱦ����
		for (int i = 0; i < flames.size; i++) {
			// ��ȡ������ÿ��������󣬲��һ���
			flames.get(i).render(batch, statetime);
		}
		// ���þ��黭�ʻ��ƾ���
		batch.setProjectionMatrix(uiCam.combined);
		// ������ʯ
		diamond.render(batch);
		// ���õ�ͼ��Ⱦ���
		mapRender.setView(camera);
		// ��Ⱦ��ͼ
		mapRender.render();
		if (Box2DDebug) {
			// ����Box2D���ͶӰ��ê��
			box2dCamera.position.set(protagonist.getPosition().x
					+ ViewPort_WIDTH / 4 / RATE, ViewPort_HEIGHT / 2 / RATE, 0);
			// ����Box2D���
			adjustBox2DCamera();
			// ��������ʱ����Ⱦ���״̬
			box2dCamera.update();
			// ��Ⱦ��������
			box2dRender.render(world, box2dCamera.combined);
		}
	}
}
