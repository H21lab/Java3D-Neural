import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleFanArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Vector3f;


public class Plane extends Shape3D implements GeometryUpdater{
	final static int w = 32;
	final static int h = 32;
	private float time;
	
	
	public static float function(float x, float y, float t) {
		return 0.00f*(float)Math.sin(3.0f*3.14*x + y + t*0.5f) + 0.03f*(float)Math.cos(3.0f*3.14*y + t*1.0f) - x*x + (0.1f*(float)Math.cos(2.5f*Math.cos((double)t)+10.0f*x));
	}
	
	private static Vector3f normal(float x, float y, float t) {
		float s = 0.001f;	// step
		
		Vector3f p1 = new Vector3f(x, function(x, y, t), y);
		Vector3f p2 = new Vector3f(x + s, function(x + s, y, t), y);
		Vector3f p3 = new Vector3f(x, function(x, y + s, t), y + s);
		
		
		Vector3f u = p2;
		u.sub(p1);
		
		Vector3f v = p3;
		v.sub(p1);
		
		u.cross(v, u);
		u.normalize();
		
		return u;
	}
	
	private static IndexedTriangleFanArray getTFA() {
		int i, indx;
    	
    	float[] tex = new float[w*h*2];
    	float[] pts = new float[w*h*3];
    	float[] nor = new float[w*h*3];
    	int[] stripCounts = new int[(w - 1)*(h - 1)];
    	int[] indexes = new int[(w - 1)*(h - 1)*4];
    	
    	
      	for (int y = 0; y < h; y++) {
    		for (int x = 0; x < w; x++) {
    			i = y * w + x;
    			
    			float X, Y, W = w, H = h;
    			float U, V;
    			
    			
    			X = (float)x/W - 0.5f;
    			Y = (float)y/H - 0.5f;
    			U = X + 0.5f;
    			V = Y + 0.5f;
    			tex[i*2] = U;
    			tex[i*2 + 1] = V;
    			
    			pts[i*3] = X;
    			pts[i*3 + 1] = function(X, Y, 0.0f);
    			pts[i*3 + 2] = Y;
    			
    			Vector3f n = normal(X, Y, 0.0f);
    			nor[i*3] = n.getX();
    			nor[i*3 + 1] = n.getY();
    			nor[i*3 + 2] = n.getZ();
    			
				if ( (y < h - 1) && (x < w - 1)) {
    				indx = y * (w - 1) + x;
    				stripCounts[indx] = 4;
    				indexes[indx*4] = i;
    				indexes[indx*4 + 1] = i + w; 
    				indexes[indx*4 + 2] = (i + 1) + w;
    				indexes[indx*4 + 3] = i + 1;   				    				
    			}
    				
    		}
    	}
        
      	IndexedTriangleFanArray tfa = new IndexedTriangleFanArray(w*h, GeometryArray.COORDINATES | GeometryArray.NORMALS |GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.BY_REFERENCE, w*h*4, stripCounts);
      	tfa.setCoordRefFloat(pts);
      	tfa.setCoordinateIndices(0, indexes);
      	tfa.setNormalRefFloat(nor);
      	tfa.setNormalIndices(0, indexes);
      	tfa.setTexCoordRefFloat(0, tex);
      	tfa.setTextureCoordinateIndices(0, 0, indexes);
      	
      	tfa.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
      	tfa.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
      	
      	return tfa;
	}
	
	Plane() {    
        super(getTFA());
      
        IndexedTriangleFanArray ga = (IndexedTriangleFanArray)this.getGeometry();
        ga.updateData(this);
        time = 0.0f;
     
	}
	
	public void updateData(Geometry geometry){
		IndexedTriangleFanArray tfa = (IndexedTriangleFanArray)geometry;
		
		float[] vertices = tfa.getCoordRefFloat();
		float[] nor = tfa.getNormalRefFloat();
        
        
        for (int i = 1; i < vertices.length; i+=3) {
    		vertices[i] = function(vertices[i - 1], vertices[i + 1], time);
        }
    	
    	
    	for (int i = 0; i < tfa.getVertexCount(); i++) {
    		Vector3f n = normal(vertices[i*3], vertices[i*3 + 2], time);
    		nor[i*3] = n.getX();
    		nor[i*3 + 1] = n.getY();
    		nor[i*3 + 2] = n.getZ();
    	}
    	
    	tfa.setCoordRefFloat(vertices);
    }
	
	public void updateData(float t) {
		this.time = t;
		this.updateData(this.getGeometry());
	}

}
