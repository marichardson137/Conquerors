package engineTester;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import interaction.Collision;
import interaction.Ray;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.Window;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.KeyListener;
import toolbox.MousePicker;

public class MainGameLoop {
	
	Loader loader = new Loader();
	Random random = new Random(5666778); // seed
	
    // Lights
	List<Light> lights = new ArrayList<Light>();  
	List<Entity> lamps = new ArrayList<Entity>();    

	// Entities / Player
    RawModel playerM = OBJLoader.loadObjModel("cube", loader);
    TexturedModel playerTM = new TexturedModel(playerM, new ModelTexture(loader.loadTexture("white")));
    Player player = new Player(playerTM, new Vector3f(0,0,0),0,0,0,5);
    
    RawModel sphereM = OBJLoader.loadObjModel("sphere", loader);
    TexturedModel sphereTM = new TexturedModel(sphereM, new ModelTexture(loader.loadTexture("white")));
    Entity sphere = new Entity(sphereTM, new Vector3f(0,0,0),0,0,0,1);
    
    // NM Entities
    RawModel barrelM = NormalMappedObjLoader.loadOBJ("boulder", loader);
    TexturedModel barrelTM = new TexturedModel(barrelM, new ModelTexture(loader.loadTexture("boulder")));
    Entity barrel = new Entity(barrelTM, new Vector3f(50,10,-50),0,0,0,1);

    // Plants
    List<Entity> plants = new ArrayList<Entity>();
    RawModel plantM = OBJLoader.loadObjModel("fern", loader);
    TexturedModel plantTM = new TexturedModel(plantM, new ModelTexture(loader.loadTexture("fernAtlas")));
    
    // Trees
    List<Entity> trees = new ArrayList<Entity>();
    RawModel treeM = OBJLoader.loadObjModel("pine", loader);
    TexturedModel treeTM = new TexturedModel(treeM, new ModelTexture(loader.loadTexture("pine")));

    // Terrain
    Terrain terrain1;
    Terrain terrain2;
    Terrain terrain3;
    Terrain terrain4;
    
    // GUIs
    List <GuiTexture> guis = new ArrayList<GuiTexture>();
    GuiRenderer guiRenderer = new GuiRenderer(loader);
    
    // Camera
    Camera camera = new Camera(player);
    
    // Renderer
    MasterRenderer renderer = new MasterRenderer(loader, camera);

    // MousePicker
    MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
    
    // Texts
    FontType font = new FontType(loader.loadTexture("fonts/Monofonto"), new File("res/fonts/Monofonto.fnt"));
    GUIText text;

    public void start() {
    	
    	// Texts
    	TextMaster.init(loader);
    	text = new GUIText("This game is absolutely bonkers, and I cannot believe it!", 2, font, new Vector2f(0.5f, 0.5f), 0.25f, false); // text, size, font, translation, max-line-length, centered
    	text.setColor(1.0f, 1.0f, 1.0f);
    	
    	// Entity / Player
    	playerTM.getTexture().setReflectivity(1.0f);
    	playerTM.getTexture().setShineDamper(0.2f);
    	player.increasePosition(0.0f, player.getScale(), 0.0f);
    	player.setBounds(new Vector3f(player.getScale()*2,player.getScale()*2,player.getScale()*2));
    	
    	
    	// NM Entities 
    	barrelTM.getTexture().setReflectivity(0.5f);
    	barrelTM.getTexture().setShineDamper(1.0f);
    	barrelTM.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
    	
 
    	
    	 // Terrain
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        
        terrain1 = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap");
        terrain3 = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
        terrain4 = new Terrain(-1, 0, loader, texturePack, blendMap, "heightmap");
        Terrain[] terrainList = new Terrain[4];
        terrainList[0] = terrain1; terrainList[1] = terrain2; terrainList[2] = terrain3; terrainList[3] = terrain4;
        picker.uploadTerrains(terrainList);

        
    	// Lights
        Light sun = new Light(new Vector3f(1000, 1000, 1000), new Vector3f(252/350f, 212/350f, 64/350f));
        Light light1 = new Light(new Vector3f(0, 15, 70), new Vector3f(0.8f, 0.8f, 0.8f), new Vector3f(1, 0.01f, 0.002f));
        Light light2 = new Light(new Vector3f(70, 15, 0), new Vector3f(0, 0f, 4f), new Vector3f(1, 0.01f, 0.002f));
        lights.add(sun);
        lights.add(light1);
        lights.add(light2);
        
        RawModel lampM = OBJLoader.loadObjModel("lamp", loader);
        TexturedModel lampTM = new TexturedModel(lampM, new ModelTexture(loader.loadTexture("lamp")));
        lampTM.getTexture().setFakeLighting(true);
        for (int i=1; i<lights.size(); i++) {
        	Vector3f pos = lights.get(i).getPosition();
        	Entity lamp = new Entity(lampTM, new Vector3f(pos.x, pos.y - 15, pos.z) ,0,0,0,1);
        	lamps.add(lamp);
        }
        

    	//GUIs
        GuiTexture gui = new GuiTexture(loader.loadTexture("fern"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//        guis.add(gui);

    	
    	// Trees
    	for (int i=0; i<1500; i++) {
    		float rotationX = ((float) Math.random() - 0.5f) * 4f;
    		float rotationZ = ((float) Math.random() - 0.5f) * 4f;
    		float scale = ((float) Math.random() + 1.2f) * 0.6f;
    		float xPos = ((float) Math.random() - 0.5f) * Terrain.SIZE * 2;
    		float zPos = ((float) Math.random() - 0.5f) * Terrain.SIZE * 2;
    		Terrain terrain = determineTerrain(xPos, zPos);
    		float yPos = terrain.getHeightOfTerrain(xPos, zPos) - 1.0f;
    		Entity tree = new Entity(treeTM, new Vector3f(xPos, yPos, zPos),rotationX,0,rotationZ,scale);
    		trees.add(tree);
    	}
    	

    	// Plants
        plantTM.getTexture().setNumberOfRows(2);
        plantTM.getTexture().setTransparency(true);
//        plantTM.getTexture().setFakeLighting(true);
    	for (int i=0; i<1500; i++) {
    		float scale = ((float) Math.random() + 0.8f) * 0.6f;
    		float xPos = ((float) Math.random() - 0.5f) * Terrain.SIZE * 2;
    		float zPos = ((float) Math.random() - 0.5f) * Terrain.SIZE * 2;
    		Terrain terrain = determineTerrain(xPos, zPos);
    		float yPos = terrain.getHeightOfTerrain(xPos, zPos) - 1.0f;
    		int index = (int) (Math.random() * 4);
    		Entity plant = new Entity(plantTM, index, new Vector3f(xPos, yPos, zPos),0,0,0,scale);
    		plants.add(plant);
    	}
 
    }
   

	public void update() {
		
        processInput();
        
        player.move(determineTerrain(player.getPosition().x, player.getPosition().z));
        player.getBounds().update();
       
        camera.move();
        picker.update();
        
        Ray mouseRay = new Ray(camera.getPosition(), picker.getCurrentRay());
        float tempT = 200.0f;
        if (Collision.RayBox(player, mouseRay)) {
        	if (mouseRay.t < tempT) {
        		tempT = mouseRay.t;
        	}
        	// entity selection
        }
        
        sphere.setPosition(mouseRay.getOrigin().add(mouseRay.getDirection().mul(tempT), new Vector3f()));
        
   
        Vector3f terrainPoint = picker.getCurrentTerrainPoint();
        
//        if (terrainPoint != null) {
//        	lamps.get(0).setPosition(terrainPoint);
//        	lights.get(1).setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
//        }
        
        
        barrel.increaseRotation(0, -1.0f, 0);
        float t1 = (float) Math.sin(Window.getCurrentTime()) * 20f;
        float t2 = (float) Math.cos(Window.getCurrentTime()) * 20f;
        barrel.setPosition(new Vector3f(t1, t1 * 0.2f+ 10, t2));
        
//        Vector3f p1 = new Vector3f(0, 0, 0);
//        Vector3f p2 = new Vector3f(20, 0, 20);
//        barrel.setPosition(Interpolation.vectorLerp(p1, p2, t1));    

    
		renderer.processTerrain(terrain1);
		renderer.processTerrain(terrain2);
		renderer.processTerrain(terrain3);
		renderer.processTerrain(terrain4);
		
		renderer.processEntity(player);
		renderer.processEntity(sphere);

		renderer.processNormalMapEntity(barrel);
		
		for (int i=0; i<trees.size(); i++) {
			renderer.processEntity(trees.get(i));
		}
		for (int i=0; i<plants.size(); i++) {
			renderer.processEntity(plants.get(i));
		}
		for (int i=0; i<lamps.size(); i++) {
			renderer.processEntity(lamps.get(i));
		}

		renderer.render(lights, camera, new Vector4f(0, -1, 0, 100000));
		guiRenderer.render(guis);
		
    	// Text (last)
//    	TextMaster.render();
		         
        
        
	}
	
	public void processInput() {
		
		if (KeyListener.isKeyPressed(GLFW_KEY_F)) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // face (front, back, both), type (fill, line, point)
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
		
		if (KeyListener.isKeyPressed(GLFW_KEY_G)) {
			text.remove();
			text = new GUIText("Boi", 2, font, new Vector2f(0.5f, 0.5f), 0.25f, false);
			text.setColor(1.0f, 1.0f, 1.0f);
		}

		
	}
	
	public void stop() {
		
        loader.cleanUp();
        renderer.cleanUp();
        guiRenderer.cleanUp();
        TextMaster.cleanUp();
        
	}
	
	private Terrain determineTerrain(float worldX, float worldZ) {
        if ((worldX / Terrain.SIZE) >= 0 && (worldZ / Terrain.SIZE) < 0) {
            return terrain1;
        } else if ((worldX / Terrain.SIZE) < 0 && (worldZ / Terrain.SIZE) < 0) {
            return terrain2;
        } else if ((worldX / Terrain.SIZE) >= 0 && (worldZ / Terrain.SIZE) >= 0) {
            return terrain3;
        } else {
            return terrain4;
        }
	}
	
    public static void main(String args[]) {
    	Window window = Window.get();
    	window.run();
    }	

}
