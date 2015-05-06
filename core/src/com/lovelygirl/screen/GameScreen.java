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
	// 物理时间渲染变量
	private boolean Box2DDebug = true;
	// 声明世界变量
	private World world;
	// 声明物理世界渲染器
	private Box2DDebugRenderer box2dRender;
	// 声明物理世界渲染相机
	private OrthographicCamera box2dCamera;
	// 声明刚体监听器
	private Box2DContactListener bcl;
	// 刚体
	Body body;
	// 声明地图
	private TiledMap tileMap;
	// 声明瓦片大小
	private float tileSize;
	// 声明地图宽度
	private float mapWidth;
	// 声明地图高度
	private float mapHeight;
	// 声明地图渲染器
	private OrthogonalTiledMapRenderer mapRender;
	// 地图编号
	public static int level;
	// 声明主角
	private Protagonist protagonist;
	// 动画渲染状态时间
	private float statetime;
	// 声明星星数组
	private Array<Star> stars;
	// 创建火焰数组
	private Array<Flame> flames;
	// 声明钻石
	private Diamond diamond;
	// 声明背景
	private Tree tree;

	public GameScreen(MainGame game) {
		super(game);
		// 实例化物理世界刚体监听器
		bcl = new Box2DContactListener();
		// 实例化物理世界
		world = new World(new Vector2(0, -9.81f), true);
		// 设置物理世界刚体监听器
		world.setContactListener(bcl);
		// 实例化物理引擎渲染器
		box2dRender = new Box2DDebugRenderer();
		// 实例化物理世界相机
		box2dCamera = new OrthographicCamera();
		// 设置相机视距
		box2dCamera.setToOrtho(false, ViewPort_WIDTH / RATE, ViewPort_HEIGHT / RATE);
		// 初始化游戏精灵等
		this.init();
	}

	public void init() {
		// 创建主角
		createActor();
		// 创建地图
		createMap();
		// 创建星星
		createStar();
		// 创建火焰
		createFlame();
		// 实例化钻石
		diamond = new Diamond(protagonist);
		//实例化背景树林
		tree = new Tree();
	}
	
	private void createFlame() {
		// 实例化火焰对象数组
		flames = new Array<Flame>();
		// 遍历火焰对象层
		MapLayer ml = tileMap.getLayers().get("flame");
		// 对象层为空即返回
		if(ml == null) return;
		// 定义刚体样式
		BodyDef bodyDef = new BodyDef();
		// 定义刚体类型
		bodyDef.type = BodyType.StaticBody;
		// 实例化圆形图形
		CircleShape shape = new CircleShape();
		// 设置圆形图形半径
		shape.setRadius(5 / RATE);
		// 实例化夹具
		FixtureDef fixtureDef = new FixtureDef();
		// 绑定图形与刚体
		fixtureDef.shape = shape;
		// 设置刚体为传感器
		fixtureDef.isSensor = true;
		// 设定刚体自身碰撞属性
		fixtureDef.filter.categoryBits = FLAME;
		// 设定刚体目标碰撞属性
		fixtureDef.filter.maskBits = PLAYER;
		// 遍历火焰对象层
		for(MapObject mo : ml.getObjects()) {
			// 对象x,y轴坐标
			float x = 0;
			float y = 0;
			// 遍历图层对象
			if(mo instanceof EllipseMapObject) {
				EllipseMapObject emo = (EllipseMapObject) mo;
				// 赋值火焰对象x，y坐标
				x = emo.getEllipse().x / RATE;
				y = emo.getEllipse().y / RATE;
			}
			// 设定刚体位置
			bodyDef.position.set(x, y);
			// 实例化刚体
			Body body = world.createBody(bodyDef);
			// 实例化夹具并且设定夹具用户数据
			body.createFixture(fixtureDef).setUserData("flame");
			// 实例化火焰对象
			Flame f = new Flame(body);
			// 设定刚体用户数据
			body.setUserData(f);
			// 添加火焰对象到火焰数组
			flames.add(f);
		}
		shape.dispose();
	}
	
	private void createActor() {
		// 定义刚体样式对象
		BodyDef bodyDef = new BodyDef();
		// 创建夹具
		FixtureDef fixtureDef = new FixtureDef();
		// 实例化多边形
		PolygonShape shape = new PolygonShape();
		// 创建刚体
		body = world.createBody(bodyDef);
		/*
		 * 创建正方形模型
		 */
		// 定义位置
		bodyDef.position.set(60 / RATE, 200 / RATE);
		// 定义刚体类型
		bodyDef.type = BodyType.DynamicBody;
		// 设定水平方向速度
		bodyDef.linearVelocity.set(1.0f, 0);
		// 创建刚体
		body = world.createBody(bodyDef);
		// 设定刚体形状
		shape.setAsBox(13 / RATE, 18 / RATE);
		// 绑定正方体夹具与刚体
		fixtureDef.shape = shape;
		// 设定正方形自身碰撞属性
		fixtureDef.filter.categoryBits = PLAYER;
		// 设定正方形目标碰撞属性
		fixtureDef.filter.maskBits = BLOCK_RED | STAR | FLAME;
		// 创建夹具
		body.createFixture(fixtureDef).setUserData("box");
		/*
		 * 创建传感器foot
		 */
		// 设置传感器形状
		shape.setAsBox(13 / RATE, 2 / RATE, new Vector2(0, -18 / RATE), 0);
		// 传感器夹具与图形绑定
		fixtureDef.shape = shape;
		// 设置传感器自身碰撞属性
		fixtureDef.filter.categoryBits = PLAYER;
		// 设置传感器目标碰撞属性
		fixtureDef.filter.maskBits = BLOCK_RED;
		// 设定该刚体是否作为传感器
		fixtureDef.isSensor = true;
		// 帮定刚体及夹具并且设置用户数据
		body.createFixture(fixtureDef).setUserData("foot");
		// 实例化主角
		protagonist = new Protagonist(body);
	}
	
	private void createMap() {
		// 加载地图
		try {
			// 实例化地图
			tileMap = new TmxMapLoader().load("maps/" + "level" + level
					+ ".tmx");
		} catch (Exception e) {
			// 异常打印信息
			System.out.println("不能找到level" + level + ".tmx文件");
			// 退出应用
			Gdx.app.exit();
		}
		// 实例化地图渲染器
		mapRender = new OrthogonalTiledMapRenderer(tileMap);
		// 赋值地图瓦片大小
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
		// 赋值地图宽度
		mapWidth = tileMap.getProperties().get("width", Integer.class);
		// 赋值地图高度
		mapHeight = tileMap.getProperties().get("height", Integer.class);
		// 声明图层
		TiledMapTileLayer layer;
		// 获取红色地图图层
		layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
		// 绑定刚体与红色地图图层
		createMapLayer(layer, BLOCK_RED);
		// 获取绿色地图图层
		layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
		// 绑定刚体与绿色地图
		createMapLayer(layer, BLOCK_GREEN);
		// 获取蓝色地图
		layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
		// 绑定刚体与蓝色地图
		createMapLayer(layer, BLOCK_BLUE);
		
	}
	private void createMapLayer(TiledMapTileLayer layer, short bits){
		// 实例化刚体样式定义类
		BodyDef bodyDef = new BodyDef();
		// 实例化夹具样式定义类
		FixtureDef fixtureDef = new FixtureDef();
		// 遍历所有单元格,row列，col行
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {

				// 获取cell
				Cell cell = layer.getCell(col, row);
				if (cell == null)
					continue;
				if (cell.getTile() == null)
					continue;

				// 创建body 及夹具，
				bodyDef.type = BodyType.StaticBody;
				bodyDef.position.set(
						(col + 0.5f) * tileSize / RATE, 
						(row + 0.5f) * tileSize / RATE);
				ChainShape cs = new ChainShape();
				//创建链式图形，并且处理刚体与地图的坐标
				Vector2[] v = new Vector2[3];
				v[0] = new Vector2(-tileSize / 2 / RATE,-tileSize / 2 / RATE);
				v[1] = new Vector2(-tileSize / 2 / RATE,  tileSize / 2 / RATE );
				v[2] = new Vector2(tileSize/ 2 / RATE,  tileSize / 2 / RATE);
				// 创建链式图形
				cs.createChain(v);
				// 设置恢复力为0
				fixtureDef.friction = 0;
				// 绑定夹具与链式图形
				fixtureDef.shape = cs;
				// 设置图形的过滤器属性
				fixtureDef.filter.categoryBits = bits;
				// 设定地图默认碰撞属性
				fixtureDef.filter.maskBits = PLAYER;
				// 设置传感器
				fixtureDef.isSensor = false;
				// 创建地图刚体
				world.createBody(bodyDef).createFixture(fixtureDef);
			}
		}
	}

	private void createStar(){
		
		// 创建萝卜的数组
		stars = new Array<Star>();
		// 获取对象层
		MapLayer ml = tileMap.getLayers().get("heart");
		// 对象层为空，直接返回
		if(ml == null) return;
		// 实例化星星刚体定义
		BodyDef bodyDef = new BodyDef();
		// 设定刚体类型
		bodyDef.type = BodyType.StaticBody;
		// 实例化夹具
		FixtureDef fixtureDef = new FixtureDef();
		// 实例化圆形图形对象
		CircleShape shape = new CircleShape();
		// 设置圆形刚体半径
		shape.setRadius(8 / RATE);
		// 图形刚体绑定夹具
		fixtureDef.shape = shape;
		// 设定当前刚体为传感器
		fixtureDef.isSensor = true;
		// 设定当前刚体本身碰撞属性
		fixtureDef.filter.categoryBits = STAR;
		// 设定当前刚体目标碰撞属性
		fixtureDef.filter.maskBits = PLAYER;
		// 遍历对象层中对象
		for (MapObject mo: ml.getObjects()) {
			// 星星对象X轴坐标
			float x = 0;
			// 星星对象Y轴坐标
			float y = 0;
			// 获取对象x,y坐标
			if (mo instanceof EllipseMapObject) {
				EllipseMapObject emo = (EllipseMapObject) mo;
				x = emo.getEllipse().x / RATE;
				y = emo.getEllipse().y / RATE;
			}
			// 设定刚体位置
			bodyDef.position.set(x, y);
			// 实例化星星刚体
			Body body = world.createBody(bodyDef);
			// 创建夹具
			body.createFixture(fixtureDef).setUserData("heart");
			// 实例化星星
			Star s = new Star(body);
			// 添加星星到当前数组
			stars.add(s);
			// 设定星星用户数据
			body.setUserData(s);
		}
	}
	
	private void switchDiamond() {

		// 获取底部刚体传感器的目标碰撞属性
		Filter filter = protagonist.getBody().getFixtureList().get(1)
				.getFilterData();
		// 接收过滤去的目标碰撞属性
		short bits = filter.maskBits;

		// 给底部刚体赋值过滤器属性，使其检测主角刚体与地面的碰撞。
		if (bits == BLOCK_RED) {
			// 红色目标转为绿色木板属性
			bits = BLOCK_GREEN;
		} else if (bits == BLOCK_GREEN) {
			// 绿色木板属性转为蓝色木板属性
			bits = BLOCK_BLUE;
		} else if (bits == BLOCK_BLUE) {
			// 蓝色木板属性转为红色木板属性
			bits = BLOCK_RED;
		}
		// 底部传感器刚体目标碰撞属性赋值为bits
		filter.maskBits = bits;
		// 刚bits赋给主角刚体底部传感器刚体
		protagonist.getBody().getFixtureList().get(1).setFilterData(filter);

		// 设置主角上部分刚体目标碰撞属性为火焰与星星
		bits |= STAR | FLAME;
		// 设置过滤去目标碰撞属性
		filter.maskBits = bits;
		// 设置主角上半部分刚体碰撞属性
		protagonist.getBody().getFixtureList().get(0).setFilterData(filter);
	}
	
	@Override
	public void update(float dt) {
		// 处理输入监听
		handleInput();
		// 清屏
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// 更新物理世界状态
		world.step(dt, 6, 2);
		
		Array<Body> bodies = bcl.getRemoveBodies();
		for (int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			stars.removeValue((Star) b.getUserData(), true);
			world.destroyBody(b);
			protagonist.collectStars();
			// 播放吃掉金币声音
			Sound s = assetManager.get("audio/diamond.ogg");
			s.play();
		}
		bodies.clear();
		
		//判断主角过关
		if (protagonist.getBody().getPosition().x * RATE > mapWidth * tileSize) {
			// 播放关卡选择音效
			Sound s = assetManager.get("audio/select.wav");
			s.play();
			// 主角通关初始化开始界面所有元素
			game.mainScreen.init();
			// 设置主界面绘制变量为假，不绘制开始界面
			MainScreen.Debug = false;
			// 置顶当前游戏主界面
			game.setScreen(MainGame.mainScreen);
		}
		
		if(protagonist.getBody().getPosition().y < 0){
			// 播放碰撞掉落音效
			Sound s = assetManager.get("audio/contact.wav");
			s.play();
			// 当主角掉落地图外，初始化开始界面所有元素
			game.mainScreen.init();
			// 设置主界面绘制变量为真，绘制开始界面
			game.mainScreen.Debug = true;
			// 置顶当前游戏主界面
			game.setScreen(game.mainScreen);
		}
		if(protagonist.getBody().getLinearVelocity().x < 0.001f){
			// 播放碰撞火焰音效
			Sound s = assetManager.get("audio/contact.wav");
			s.play();
			// 当主角碰撞到木板，横向速度为0时，判断主角死亡，初始化游戏主界面元素
			game.mainScreen.init();
			// 设置主界面绘制变量为真，绘制游戏开始界面
			game.mainScreen.Debug = true;
			// 初始化当前游戏界面所有资源
			this.init();
			// 指定当前游戏主界面
			game.setScreen(game.mainScreen);
		}
		
		if(bcl.isContactFlame()){
			// 播放碰撞木板音效
			Sound s = assetManager.get("audio/contact.wav");
			s.play();
			// 主角碰到火焰，主角死亡，初始化主界面所有资源
			game.mainScreen.init();
			// 设置游戏主界面绘制变量为真，绘制游戏主界面
			game.mainScreen.Debug = true;
			//　置顶当前游戏主界面
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
		// 相机投影锚点小于相机视距的一半时，不再向左移动相机
		if (camera.position.x < camera.viewportWidth / 2) {
			camera.position.x = camera.viewportWidth / 2;
		}
		// 当相机锚点X轴坐标大于地图宽度时，不再向右移动相机
		if (camera.position.x > (tileMap.getProperties().get("width",Integer.class) * tileSize)
				- camera.viewportWidth / 2) {
			camera.position.x = (tileMap.getProperties().get("width",Integer.class) * tileSize)
					- camera.viewportWidth / 2;
		}
	}

	private void adjustBox2DCamera() {
		// 当物理时间相机锚点X轴坐标小于相机视距宽度的一半时，不向左移动相机
		if (box2dCamera.position.x < box2dCamera.viewportWidth / 2) {
			box2dCamera.position.x = box2dCamera.viewportWidth / 2;
		}
		// 当物理时间相机锚点X轴坐标大于物理世界宽度时，不向右移动相机
		if (box2dCamera.position.x > (tileMap.getProperties().get("width",Integer.class)
				/ RATE * tileSize) - box2dCamera.viewportWidth / 2) {
			box2dCamera.position.x = (tileMap.getProperties().get("width",Integer.class)
					/ RATE * tileSize)- box2dCamera.viewportWidth / 2;
		}
	}
	

	@Override
	public void render(float delta) {
		// 更新游戏逻辑
		update(delta);
		// 设置相机投影矩阵锚点位置
		camera.position.set(protagonist.getPosition().x * RATE + ViewPort_WIDTH
				/ 4, ViewPort_HEIGHT / 2, 0);
		// 调整精灵绘制相机
		adjustCamera();
		// 更新相机状态
		camera.update();
		// 设置绘制背景的相机为UI相机
		batch.setProjectionMatrix(uiCam.combined);
		// 绘制背景
		tree.render(batch);
		//渲染时间
		statetime +=delta;
		// 设置精灵画笔绘图矩阵
		batch.setProjectionMatrix(camera.combined);
		// 渲染主角
		protagonist.render(batch, statetime);
		// 渲染星星
		for (int i = 0; i < stars.size; i++) {
			// 获取数组中每个星星并且绘制
			stars.get(i).render(batch, statetime);
		}
		// 渲染火焰
		for (int i = 0; i < flames.size; i++) {
			// 获取数组中每个火焰对象，并且绘制
			flames.get(i).render(batch, statetime);
		}
		// 设置精灵画笔绘制矩阵
		batch.setProjectionMatrix(uiCam.combined);
		// 绘制钻石
		diamond.render(batch);
		// 设置地图渲染相机
		mapRender.setView(camera);
		// 渲染地图
		mapRender.render();
		if (Box2DDebug) {
			// 设置Box2D相机投影的锚点
			box2dCamera.position.set(protagonist.getPosition().x
					+ ViewPort_WIDTH / 4 / RATE, ViewPort_HEIGHT / 2 / RATE, 0);
			// 调整Box2D相机
			adjustBox2DCamera();
			// 更新物理时间渲染相机状态
			box2dCamera.update();
			// 渲染物理世界
			box2dRender.render(world, box2dCamera.combined);
		}
	}
}
