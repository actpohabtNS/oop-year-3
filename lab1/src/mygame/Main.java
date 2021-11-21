package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * Lab1 - simulate cyllinder rolling on inclined plane
 */
public class Main extends SimpleApplication {

  public static void main(String args[]) {
    Main app = new Main();
    app.start();
  }

  /** Prepare the Physics Application State (jBullet) */
  private BulletAppState bulletAppState;

  /** Prepare Materials */
  Material wall_mat;
  Material stone_mat;
  Material floor_mat;
  Material cylinder_mat;

  /** Prepare geometries and physical nodes for bricks and cannon balls. */
  private RigidBodyControl          ball_phy;
  private static final Sphere       sphere;
  private RigidBodyControl          floor_phy;
  private static final Box          floor;
  private RigidBodyControl          cylinder_phy;
  private static final Cylinder     cylinder;

  static {
    /** Initialize the cannon ball geometry */
    sphere = new Sphere(32, 32, 0.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    /** Initialize the floor geometry */
    floor = new Box(20f, 0.1f, 5f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
    
    cylinder = new Cylinder(32, 32, 0.5f, 4, true);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
  }

  @Override
  public void simpleInitApp() {
    /** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

    /** Configure cam to look at scene */
    cam.setLocation(new Vector3f(0, 4f, 6f));
    cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
    /** Add InputManager action: Left click triggers shooting. */
    inputManager.addMapping("shoot",
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(actionListener, "shoot");
    
    inputManager.addMapping("spawnCylinder",
            new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    inputManager.addListener(actionListener, "spawnCylinder");
    
    inputManager.addMapping("rotateClockwise",
            new KeyTrigger(KeyInput.KEY_PGUP));
    inputManager.addListener(actionListener, "rotateClockwise");
    
    inputManager.addMapping("rotateAnticlockwise",
            new KeyTrigger(KeyInput.KEY_PGDN));
    inputManager.addListener(actionListener, "rotateAnticlockwise");
    /** Initialize the scene, materials, and physics space */
    initMaterials();
    initFloor();
    initCrossHairs();
  }

  private static final float ROTATE_AMOUNT = 5;
  
  /**
   * Every time the shoot action is triggered, a new cannon ball is produced.
   * The ball is set up to fly from the camera position in the camera direction.
   */
  private final ActionListener actionListener = new ActionListener() {
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (!keyPressed) {
          switch(name) {
            case "shoot":
                makeCannonBall();
                break;
            case "spawnCylinder":
                spawnCylinder();
                break;
            case "rotateClockwise":
                rotateFloor(ROTATE_AMOUNT);
                break;
            case "rotateAnticlockwise":
                rotateFloor(-ROTATE_AMOUNT);
                break;
          }
      }
    }
  };

  /** Initialize the materials used in this scene. */
  public void initMaterials() {

    stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key2.setGenerateMips(true);
    Texture tex2 = assetManager.loadTexture(key2);
    stone_mat.setTexture("ColorMap", tex2);

    floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    floor_mat.setTexture("ColorMap", tex3);
    
    cylinder_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key4 = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
    key4.setGenerateMips(true);
    Texture tex4 = assetManager.loadTexture(key4);
    cylinder_mat.setTexture("ColorMap", tex4);
  }

  /** Make a solid floor and add it to the scene. */
  public void initFloor() {
    Geometry floor_geo = new Geometry("Floor", floor);
    floor_geo.setMaterial(floor_mat);
    floor_geo.setLocalTranslation(0, 2f, -10f);
    
    floor_geo.rotate(0, 0, 20*FastMath.DEG_TO_RAD);
    
    this.rootNode.attachChild(floor_geo);
    /* Make the floor physical with mass 0.0f! */
    floor_phy = new RigidBodyControl(0.0f);
    floor_geo.addControl(floor_phy);
    bulletAppState.getPhysicsSpace().add(floor_phy);
  }
  /**
   * Rotate floor in y axis for specified degrees
   * @param deg 
   */
  public void rotateFloor(float deg) {
    Spatial floor_geo = this.rootNode.getChild("Floor");
    floor_geo.rotate(0, 0, deg*FastMath.DEG_TO_RAD);
    this.rootNode.attachChild(floor_geo);
    floor_geo.addControl(floor_phy);
    bulletAppState.getPhysicsSpace().add(floor_phy);
  }

  /** This method creates one individual physical cannon ball.
   * By default, the ball is accelerated and flies
   * from the camera position in the camera direction.*/
   public void makeCannonBall() {
    /** Create a cannon ball geometry and attach to scene graph. */
    Geometry ball_geo = new Geometry("cannon ball", sphere);
    ball_geo.setMaterial(stone_mat);
    rootNode.attachChild(ball_geo);
    /** Position the cannon ball  */
    ball_geo.setLocalTranslation(cam.getLocation());
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(1f);
    /** Add physical ball to physics space. */
    ball_geo.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    /** Accelerate the physcial ball to shoot it. */
    ball_phy.setLinearVelocity(cam.getDirection().mult(25));
  }
   
  public void spawnCylinder() {
    Geometry cylinder_geo = new Geometry("Cylinder", cylinder);
    cylinder_geo.setMaterial(cylinder_mat);
    cylinder_geo.setLocalTranslation(15f, 8.5f, -10f);
    
    this.rootNode.attachChild(cylinder_geo);
    cylinder_phy = new RigidBodyControl(10f);
    cylinder_geo.addControl(cylinder_phy);
    bulletAppState.getPhysicsSpace().add(cylinder_phy);
  }
  
  /** A plus sign used as crosshairs to help the player with aiming.*/
  protected void initCrossHairs() {
    guiNode.detachAllChildren();
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+");        // fake crosshairs :)
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
      settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
    guiNode.attachChild(ch);
  }
}
