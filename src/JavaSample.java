
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.Label;
import java.util.Random;
import java.util.Vector;

import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.*;

/**
 * Java Sample
 * @author M.Kacer
 */
public class JavaSample extends JFrame implements Runnable {

    JFrame jFrame;
    SimpleUniverse universe;
    BranchGroup rootGroup;
    Plane plane;
    Sphere ball;
    TransformGroup transformGroup;
    TransformGroup tBall;
    double angle = 0.0d;
    float t = 0.0f;
    
    static Random generator = new Random();
    float c = 0.0f;
    
    float posX = 0.0f, posY = 0.0f, posZ = 0.0f;
    NeuralNetwork neuralNetwork;
    
    
    public static boolean running = false;

    public void init() {
    	createUniverse();
        createObjects();
        createLights();
        createCamera();
        createMouseRotator();

        universe.addBranchGraph(rootGroup);
        
        
        // create neural network
        Vector<Integer> dim = new Vector<Integer>();
        dim.add(16);
        //dim.add(2);
        dim.add(16*16 + 2);
        dim.add(2);
        neuralNetwork = new NeuralNetwork(dim);
        
        MyCanvas3D c = (MyCanvas3D)universe.getCanvas();
        c.setNetwork(neuralNetwork);
    }

    private void createUniverse() {
    	
    	
        
    	
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        MyCanvas3D canvas = new MyCanvas3D(config);
        //this.add(canvas, BorderLayout.CENTER);
        
        universe = new SimpleUniverse(canvas);
        getContentPane().add(canvas, BorderLayout.CENTER);
        
        this.setTitle("JavaSample v2.0");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,800,600);
        this.setVisible(true);

       
        
    	//universe = new SimpleUniverse();
        
        //Viewer viewer = universe.getViewer();
        //viewer.set
        
        //jFrame = universe.getViewer().getJFrame(0);
        //jFrame.add(canvas, BorderLayout.CENTER);
        
        // jFrame.setSize(800, 600);
       // jFrame.setTitle("JavaSample v2.0");

        rootGroup = new BranchGroup();
        rootGroup.setCapability(BranchGroup.ALLOW_DETACH);
        rootGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        rootGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        rootGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        //universe.getViewer().getView().setSceneAntialiasingEnable(true);
    }


    
    private void createObjects() {

        // Set up the texture map
        TextureLoader loader = new TextureLoader("texture.jpg", "RGB", new Container());

        Texture texture = loader.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));


        // Set up the texture attributes
        // could be REPLACE, BLEND or DECAL instead of MODULATE

        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        Appearance ap = new Appearance();
        ap.setTexture(texture);
        ap.setTextureAttributes(texAttr);
        ap.setMaterial(new Material(new Color3f(0.2f, 0.2f, 0.2f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(1.0f, 1.0f, 1.0f), new Color3f(0.9f, 0.9f, 0.9f), 45.0f));
        PolygonAttributes pat = new PolygonAttributes();
        pat.setCullFace(PolygonAttributes.CULL_NONE);
        ap.setPolygonAttributes(pat);
        
        // create plane
        plane = new Plane();
        plane.setAppearance(ap);

        // create ball
        Appearance apBall = new Appearance();
        apBall.setMaterial(new Material(new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.2f, 0.2f, 0.2f), new Color3f(1.0f, 1.0f, 1.0f), 45.0f));
        int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
        ball = new Sphere(0.02f, primflags, 30, apBall);

        tBall = new TransformGroup();
        tBall.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tBall.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tBall.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        tBall.addChild(ball);

       
        
        transformGroup = new TransformGroup();

        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        transformGroup.addChild(plane);
        transformGroup.addChild(tBall);

        rootGroup.addChild(transformGroup);
    }

    private void createLights() {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        Color3f ambientLightColour = new Color3f(1.0f, 1.0f, 1.0f);
        AmbientLight ambientLight = new AmbientLight(ambientLightColour);
        ambientLight.setInfluencingBounds(bounds);
        Color3f directionLightColour = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f directionLightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight directionLight = new DirectionalLight(directionLightColour, directionLightDir);
        directionLight.setInfluencingBounds(bounds);
        rootGroup.addChild(ambientLight);
        rootGroup.addChild(directionLight);
    }

    private void createCamera() {
        universe.getViewingPlatform().setNominalViewingTransform();
        ViewingPlatform ourView;
        ourView = universe.getViewingPlatform();
        Transform3D locator = new Transform3D();
        locator.lookAt(new Point3d(0.0, 0.0, 2.5), new Point3d(0.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0));
        locator.invert();
        ourView.getViewPlatformTransform().setTransform(locator);
        
    }

    private void createMouseRotator() {
        MyMouseRotate behavior = new MyMouseRotate();
        behavior.setTransformGroup(universe.getViewingPlatform().getViewPlatformTransform());
        rootGroup.addChild(behavior);
        behavior.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }

    public void run() {

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            
            Transform3D t1 = new Transform3D();
            t1.setRotation(new AxisAngle4d(0.0f, 1.0f, 0.0f, angle));
            transformGroup.setTransform(t1);

            
            posY = Plane.function(posX, posZ, t);
            
                 
            
            //angle += 0.003d;
            //if (JavaSample.running) {
            	t += 0.5f/30.0f;
            //} else if(c > 0.5f){
            //	t = ((float)generator.nextInt(6280))/1000.0f;
            //	c = 0.0f;
            //}
            
            c += 0.5f/30.f;
            	
            if (t > 6.28f) {
            	t-=6.28f;
            	System.gc();
            }
            	
            Transform3D t2 = new Transform3D();
            t2.setTranslation(new Vector3d(posX, posY + 0.015f, posZ));
            tBall.setTransform(t2);
            
            Vector<Float> y = new Vector<Float>();

            for (int i = 0; i < 5; i++) {
	            Vector<Float> x = new Vector<Float>();
	            
	            for (float _z = -0.5f; _z <= 0.5f; _z += 0.25f) {
	            	for (float _x = -0.5f; _x <= 0.5f; _x += 0.25f) {
	            		x.add(new Float(Plane.function(_x, _z, t) + 0.5f));  
	            		//System.out.println(Plane.function(_x, posZ, t) + 0.5f);
	            	}
	            }
	            //x.add(new Float(posX));   
	            //x.add(new Float(posZ)); 
	            //x.add(new Float(posY)); 
	            y = neuralNetwork.calculate(x);
	            
	            
	            float maxYforX = -1.0f, maxYforZ = -1.0f;
	            float maxForX = 0.0f, maxForZ = 0.0f;
	            for (float _z = -0.5f; _z <= 0.5f; _z += 0.01f) {
	            	if (maxYforZ < Plane.function(posX, _z, t)) {
	            		maxYforZ = Plane.function(posX, _z, t);
	            		maxForZ = _z;
	            	}
	            }
	            for (float _x = -0.5f; _x <= 0.5f; _x += 0.01f) {
	            	if (maxYforX < Plane.function(_x, posZ, t)) {
	            		maxYforX = Plane.function(_x, posZ, t);
	            		maxForX = _x;
	            	}
	            }
	            
	            Vector <Float> errors = new Vector<Float>();
	           
	            
	            /*
	             * float error = 0.0f;
	            if (y.get(0) >= 0.5f && (maxForX - posX) < 0.0f) {
	            	error = 1.0f;
	            } else if (y.get(0) <= 0.5f && (maxForX - posX) > 0.0f){
	            	error = 1.0f;
	            }     
	            errors.add(error);
	            */
	           
	            
	            errors.add(2.0f*(maxForX - posX));
	            errors.add(2.0f*(maxForZ - posZ));
	            
	            //errors.add(Math.abs(1.0f - posY - 0.8f));
	            //errors.add(Math.abs(1.0f - posY - 0.8f));
	            if (!JavaSample.running) {
	            	neuralNetwork.learn(errors);
	            }
	 //           System.out.println(x.get(0) + " " + y.get(0) + " " /*+ y.get(1) + " "*/ + errors.get(0) /*+ " " + errors.get(1)*/);
	        }
 			//posX += 0.1f*(y.get(0)-0.5f);
 			//posZ += 0.005f*(y.get(1)-0.5f);
 			
            posX = 1.0f*y.get(0)-0.5f;
 			posZ = 1.0f*y.get(1)-0.5f;
 			
            
 			if (posX < -0.5f) {
 				posX = -0.5f;
 			} else if (posX > 0.5f) {
 				posX = 0.5f;
 			}
 			if (posZ < -0.5f) {
 				posZ = -0.5f;
 			} else if (posZ > 0.5f) {
 				posZ = 0.5f;
 			}
            
            plane.updateData(t);
            
        }
    }

    public static void main(String[] args) {
        JavaSample javaSample = new JavaSample();
        javaSample.init();

        Thread mainLoop = new Thread(javaSample);

        mainLoop.start();

    }
}
