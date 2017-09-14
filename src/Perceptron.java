import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;


public class Perceptron {
	static Random generator = new Random();
	private Vector<Float> x;
	private Vector<Float> w;
	private float y;
	private Vector<Perceptron> linksRight;
	private Vector<Perceptron> linksLeft;
	
	// learning algorithm
	private Vector<Float> e;
	private float b;
	
	
	// history of errors and weights
	LinkedList<Float> _e;
	LinkedList<Vector<Float>> _w;
	LinkedList<Float> _b;
	
	final static int HISTORY_SIZE = 90;
	
	public float getB() {
    	return b;
    }

	public void setB(float b) {
    	this.b = b;
    }

	public Vector<Float> getX() {
    	return x;
    }

	public void setX(Vector<Float> x) {
    	this.x = x;
    }
	
	public void setX(float x) {
    	this.x = new Vector<Float>();
    	this.x.add(x);
    }
	
	public void setIndexedX(Perceptron originator, float x) {
		this.x.set(this.linksLeft.indexOf(originator), x);
	}
	
	public Vector<Float> getW() {
    	return w;
    }

	public void setW(Vector<Float> w) {
    	this.w = w;
    }

	public Perceptron(int dim, Vector<Perceptron> l) {
		b = -0.0f;
		w = new Vector<Float>(dim);
		x = new Vector<Float>(dim);
		for (int i = 0; i < dim; i++) {
			w.add((float)generator.nextInt(2000)/1000.0f - 1.0f);
			x.add(0.0f);
		}
		
		_e = new LinkedList <Float>();
		_w = new LinkedList <Vector<Float>>();
		_b = new LinkedList <Float>();
		
		
		linksRight = l;
		e = new Vector<Float>();
		
		if (l != null) {
			for (int i = 0; i < l.size(); i++) {
				e.add(null);
			}
		} else {
			e.add(null);
		}
		
		linksLeft = new Vector<Perceptron>();
		if(linksRight != null) {
			for (int i = 0; i < linksRight.size(); i++) {
				linksRight.get(i).leftLink(this);
			}
		}
	}
	
	public void leftLink(Perceptron left) {
		linksLeft.add(left);
	}
	
	public float calculate() {
		y = 0.0f;
		for (int i = 0; i < x.size(); i ++) {
			y += x.get(i).floatValue() * w.get(i).floatValue(); 
			x.set(i, null);
			
  //System.out.print(" " + w.get(i));
		}
  //System.out.print("\n");
		
		y += b;
		
		// activation function
		y = 1.0f/(1.0f + (float)Math.exp((double)-1.0f*y));
		
		if (linksRight != null) {
			for (int i = 0; i < linksRight.size(); i++) {
				linksRight.get(i).setIndexedX(this, y);
			}
		}
	
  //System.out.println("y = " + y);
		return y;
	}
	
	
	public void setError(Perceptron originator, float error) {
		
		if (originator != null) {
			e.set(linksRight.indexOf(originator), error);
		} else {
			e.set(0, error);
		}
	}
	
	
	// propagate the error to one layer back
	public void propagateError() {
		float error = 0.0f;
		for (int i = 0; i < e.size(); i ++) {
			error += e.get(i)*e.get(i);
		}
		error = 0.5f * error;
		if (error > 1.0f) {
			error = 1.0f;
		}

		// history
		_e.push(error);
		Vector<Float> __w = new Vector<Float>();
		for (int i = 0; i < w.size(); i ++) {
			__w.add(w.get(i));
		}
		_w.push(__w);
		_b.push(b);
		if (_e.size() > HISTORY_SIZE) {
			_e.pollLast();
		}
		if (_w.size() > HISTORY_SIZE + 1) {
			_w.pollLast();
		}
		if (_b.size() > HISTORY_SIZE + 1) {
			_b.pollLast();
		}
		//
		
		for (int i = 0; i < linksLeft.size(); i ++) {
			linksLeft.get(i).setError(this, error*Math.abs(w.get(i)));
		}
	}
	
	public void learn() {
		// process	
	
//System.out.println(error);		
		float weight;
		
		float _f = 1.0f;
		int _i = 0;
		
 //System.out.print("_e =");			
		for (int i = 0; i < _e.size(); i++) {
 //System.out.print(" " + _e.get(i));				
			if (_e.get(i)/**((HISTORY_SIZE - i)/(float)HISTORY_SIZE)*/ <= _f) {
				_f = _e.get(i);
				_i = i;
			}
		}
 //System.out.print("\n");	
		
		
		
		float deltaError = 1.0f*_e.get(0); 
		
		for (int i = 0; i < _e.size(); i ++) {
			deltaError += _e.get(i)*_e.get(i)*((HISTORY_SIZE - i)/(float)HISTORY_SIZE);
		}
		
		
		_i = 0;
		
		// w = _w.get(_i);
		float beta = 1.0f;
		
		for (int i = 0; i < w.size(); i ++) {
			weight = w.get(i);
			
			if (_i + 1 < _e.size() && _i + 1 < _w.size() && (_w.get(_i).get(i) - _w.get(_i + 1).get(i)) != 0.0f) {
				//deltaError = (_e.get(0) - _e.get(_i));
				
				weight = weight + (_w.get(_i).get(i) - weight)*beta*deltaError;

				//weight = (weight + _w.get(_i).get(i))/2.0f;

//System.out.println(weight + " " + (- beta*(_e.get(_i) - _e.get(_i + 1))/(_w.get(_i).get(i) - _w.get(_i + 1).get(i))));	
				//weight = weight - beta*deltaError/(_w.get(_i).get(i) - _w.get(_i + 1).get(i));
				

			}
			
			w.set(i, weight);
		}

	
		//b = b + (_b.get(_i) - b)*1.0f*deltaError;
		
		
		float alfa = 0.1f;
		//int _indx = generator.nextInt(w.size());
		for (int _indx = 0; _indx < w.size(); _indx++) {
			weight = w.get(_indx)*(1.0f - alfa*deltaError) + alfa*deltaError*((float)generator.nextInt(2000)/1000.0f - 1.0f);
			if (weight > 1.0f) {
				weight = 1.0f;
			} else if (weight < -1.0f) {
				weight = - 1.0f;
			}
			w.set(_indx, weight);
		} 
		
		
		// modify bias
		//alfa = 1.0f;
		//b = b*(1.0f - alfa*deltaError) + alfa*deltaError*((float)generator.nextInt(2000)/1000.0f - 1.0f);
		//System.out.println(b);
		
		// normalize weights
		/*float s = 0.0f;
		for (int __indx = 0; __indx < w.size(); __indx++) {
			s += w.get(__indx)*w.get(__indx);
		}
		s = (float)Math.sqrt((float)s);
		for (int __indx = 0; __indx < w.size(); __indx++) {
			w.set(__indx, w.get(__indx)/s);
		}
		 */
	
		// alter links
		
		
	}
}
