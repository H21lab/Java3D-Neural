import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


public class Neuron {
static Random generator = new Random();
	
	private ArrayList<Link> links;
	private float bias;
	private float amplitude;
	
	private float y;
	private float x;	
	private float e;

	
	private float eWeightsSum;
	//private float errorBias;
	private float instability;
	public static float t = 0.0f;	// time
	
	// learning algorithm
	int meshOffset;				// first x neurons not used
	ArrayList<Neuron> mesh;	// whole universe of neurons
	HistoryVector Y;				// history - output vector
	HistoryVector E;				// history - error vector

	float errorDispersion;	// how the neuron is trained. 0.0f = fully trained
	int counter;
	static final int learningFrame = 300;
	
	// error propagation
	private int connections; 	// number of connected neurons to this neuron
	

	private int _e;				// actual number of received errors from neurons
	
	// visualization
	int posX;
	int posY;
	
	// lock
	int lock;
	
	
	public int getLock() {
    	return lock;
    }
	public void setLock(int lock) {
    	this.lock = lock;
    }
	public int getPosX() {
    	return posX;
    }
	public void setPosX(int posX) {
    	this.posX = posX;
    }
	public int getPosY() {
    	return posY;
    }
	public void setPosY(int posY) {
    	this.posY = posY;
    }
	public float getE() {
    	return e;
    }
	public HistoryVector getEVector() {
    	return E;
    }
	/*public float getErrorBias() {
    	return errorBias;
    }
	public void setErrorBias(float errorBias) {
    	this.errorBias = errorBias;
    }*/
	public float getBias() {
    	return bias;
    }
	public void setBias(float bias) {
    	this.bias = bias;
    }
	public float getAmplitude() {
    	return amplitude;
    }
	public void setAmplitude(float amplitude) {
    	this.amplitude = amplitude;
    }
	public float getEWeightsSum() {
    	return eWeightsSum;
    }
	public void setEWeightsSum(float weightsSum) {
    	eWeightsSum = weightsSum;
    }
	public void addEWeightsSum(float weightsSum) {
    	eWeightsSum += Math.abs(weightsSum);
    }
	public int getConnections() {
    	return connections;
    }
	public void setConnections(int connections) {
    	this.connections = connections;
    }
	public void addConnections() {
		connections++;
	}
	public void removeConnections() {
		connections--;
	}
	public float getInstability() {
    	return instability;
    }
	public void setInstability(float instability) {
    	this.instability = instability;
    }
	public void addInstability(float instability) {
    	this.instability = this.instability*0.99f + 0.01f*Math.abs(instability);
    }
	
	public Neuron(int offset, ArrayList<Neuron> mesh, int pX, int pY) {
		super();
		meshOffset = offset;
		links = new ArrayList<Link>();
		bias = 0.5f;
		amplitude = 0.0f;
		y = 0.5f;
		Y = new HistoryVector();
		e = 0.0f;
		eWeightsSum = 0.0f;
		E = new HistoryVector();
		//errorBias = 0.0f;
		_e = 0;
		connections = 0;
		this.mesh = mesh;
		errorDispersion = 1.0f;
		counter = generator.nextInt(learningFrame);
		instability = 1.0f;
		
		posX = pX;
		posY = pY;
		
		lock = 0;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getY() {
		return y;
	}
	
	public float getSquareDistance(Neuron n) {
		return (posX - n.getPosX())*(posX - n.getPosX()) + (posY - n.getPosY())*(posY - n.getPosY()); 
	}
	
	public HistoryVector getYVector() {
		return Y;
	}
	public float getErrorDispersion() {
		return errorDispersion;
	}
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	private float activationFunction(float f) {
		if (amplitude != 0.0f){
			return 1.0f/(1.0f + (float)Math.exp((double)-1.0f*(f - bias)/(2.0f*amplitude)));
		} else {
			return 1.0f/(1.0f + (float)Math.exp((double)-1.0f*(f - bias)));
		}
	}
	
	public void calculate() {
		
		// erase the errors
		// add the result to output vector
		
		//errorDispersion = errorDispersion + 0.001f*(Math.abs(e));
		//if (errorDispersion > 1.0f) {
		//	errorDispersion = 1.0f;
		//}
		
		//System.out.println(errorDispersion);	
		//if (eWeightsSum != 0.0f) {		
			//System.out.println(eWeightsSum);			
		errorDispersion = errorDispersion*0.9f + 0.1f*Math.abs(e);
		//}			
		//System.out.println(e);	
		
		
		instability = instability*0.9f;
		
		// history
		int _E;
		if (e /*- errorBias*/ > 0.0f) {
			_E = 1;
		} else {
			_E = 0;
		}
		E.push(_E);
		_e = 0;	
		//errorBias = errorBias + 0.01f * (e - errorBias);		
		e = 0.0f;
		
		eWeightsSum = 0.0f;
		
		x = 0.0f;
		if (links.size() == 0) {
			x = bias;
		}
		for (int i = 0; i < links.size(); i ++) {
			x += links.get(i).getNeuron().getY()*links.get(i).getWeight(); 			
		}		
		
		y = activationFunction(x);
		
			

//System.out.println(y + " " + bias + " " + amplitude);		
		// add the result to output vector
		Y.push((int)Math.round(y));
	}
	public void addError(float error) {		
		this.e += error;
		_e++;
	}
	public void setError(float error) {	
		this.e = error;
		_e = connections;
	}
	public void setErrorDispersion(float ed) {
		errorDispersion = ed;
	}

	public void setLink(float weight, Neuron neuron) {
		// do not add already existing link
		for (int i = 0; i < links.size(); i++) {
			if (links.get(i).getNeuron() == neuron) {
				//links.get(i).setWeight(links.get(i).getWeight() + weight);
				//addInstability(weight);
				return;
			}
		}
		// create link
		links.add(new Link(weight, neuron));
		neuron.addConnections();
		addInstability(weight);
	}
	
	public boolean propagateError(boolean forced) {
		// only if the error is complete
		if ( (forced /*&& _e != connections*/) || (_e >= connections && !forced /*&& connections != 0*/)) {			
			//e = e /(float)_e;
			
			if (eWeightsSum != 0.0f) {
				e = e/eWeightsSum;
			}
		/*	if (e > 1.0f) {
				e = 1.0f;
			} else if (e < -1.0f) {
				e = -1.0f;
			}
		*/
//System.out.print(this + " ");
//System.out.println("error = " + e);	
//System.out.println(Integer.toBinaryString(E));
//System.out.println(posX + " " + posY);				

			for (int i = 0; i < links.size(); i ++) {
				
//System.out.println("     " + links.get(i).getNeuron().getPosX() + " " + links.get(i).getNeuron().getPosY());					
//System.out.println(links.get(i).getWeight() + " ");				
				links.get(i).getNeuron().addError(1.0f*e*links.get(i).getWeight());
				links.get(i).getNeuron().addEWeightsSum(links.get(i).getWeight());

			}			
			
//System.out.println(e);
		
			return true;
		}
		return false;
	}
	
	public void learn() {

		counter --;

		if (lock == 0) {

			
			
			for (int i = 0; i < links.size(); i ++) {
				float weight = links.get(i).getWeight();
				//weight = (0.9f + 0.1f*(1.0f - errorDispersion)) * weight + 0.1f*errorDispersion* weight * (1.0f - e); 
				/*if (weight*links.get(i).getNeuron().getY() < 0.0f) {
					//weight = (0.5f + 0.5f*(1.0f - errorDispersion*links.get(i).getNeuron().getErrorDispersion()))*weight + 0.5f*errorDispersion*links.get(i).getNeuron().getErrorDispersion()*weight * (1.0f - e);
					weight = weight + 0.01f*(e)*weight;
				} else {
					//weight = (0.5f + 0.5f*(1.0f - errorDispersion*links.get(i).getNeuron().getErrorDispersion()))*weight - 0.5f*errorDispersion*links.get(i).getNeuron().getErrorDispersion()*weight * (1.0f - e);
					weight = weight - 0.01f*(e)*weight;
				}*/
				
				// back propagation
				if (connections > 0 && e != 0.0f) {
					float rate = 0.0001f + 1.0f*(links.get(i).getNeuron().getInstability());
					weight = weight + rate*(e)*links.get(i).getNeuron().getY()*(activationFunction(x) * (1-activationFunction(x)));
					links.get(i).setWeight(weight); 
					addInstability(weight);
				}
				
				//if (Math.abs(links.get(i).getWeight()) < 0.001f) {
				//	links.get(i).getNeuron().removeConnections();
				//	links.remove(i); 
				//}
			}	
		
			
			if (counter < 0) {	
			
				int _i = -1;		// -1 do not learn
				float _r = 0.0f;
				
		/*		if (e != 0.0f) {	
					for(int i = meshOffset; i < mesh.size(); i++) {
						float r = HistoryVector.corelate(mesh.get(i).getYVector(), E);
//System.out.println(r);							
						if ((Math.abs(r) > _r) && (mesh.get(i) != this) && (mesh.get(i).getErrorDispersion() <= 1.0f) && (connections > 0) && (getSquareDistance(mesh.get(i)) < 100.0f*100.0f) && (mesh.get(i).getPosX() < posX)) {
							//_r = HistoryVector.countZeroBits(r);
							_i = i;	
							
			
							float weight;
							float rate = 0.001f + 0.1f*(1.0f - mesh.get(i).getInstability())*r;//*r;
							
							
							//weight = e*rate;
							weight = rate*(e)*mesh.get(i).getY()*(activationFunction(x) * (1-activationFunction(x)));
							
							
							
							setLink(weight, mesh.get(_i));
							//errorDispersion = errorDispersion*0.99f;
						}
					}
				}
		*/
				
	//System.out.println(errorDispersion);
		
		//if (e != 0.0f) {
		//	System.out.println(e);
		//}
//		if (Y.getValue() != 0) {
//			System.out.println(Integer.toBinaryString(Y.getValue()));
//		}
				
	
		/*		if (_i != -1 && _r > 0 ) {
					
					float weight;
					float rate = 0.1f;
		
					if (HistoryVector.countZeroBits(Y.getValue()) > 16) {
						weight = e*rate; //*getErrorDispersion();
					} else {
						weight = -e*rate; // *getErrorDispersion();
					}
					
					setLink(weight, mesh.get(_i));
					
				}
		*/
			
				counter = generator.nextInt(learningFrame);
				
				
	//System.out.println(getErrorDispersion() + " " + e);
				
				//stress, create random new link
//				if (getErrorDispersion() > 0.01f) {
//					int idx = generator.nextInt(mesh.size() - meshOffset) + meshOffset;
//					setLink(0.1f*errorDispersion*((float)generator.nextInt(2000)/(float)1000 - 1.0f), mesh.get(idx));
//					errorDispersion = errorDispersion*0.9f;
//					mesh.get(idx).setErrorDispersion(mesh.get(idx).getErrorDispersion()*0.9f);
//				}
				
				// if neuron has no output connected
				if (connections == 0 || amplitude < 0.01f) {
					for(int i = meshOffset; i < mesh.size(); i++) {
						if (/*mesh.get(i).getAmplitude() > 0.01f && mesh.get(i).getInstability() < 0.3f &&*/ (getSquareDistance(mesh.get(i)) < 100.0f*100.0f) && (mesh.get(i).getPosX() < posX) && mesh.get(i) != this) {
//System.out.println(mesh.get(i).getAmplitude() + " " + mesh.get(i).getY());

						
							float weight = 0.2f*((float)generator.nextInt(2000)/(float)1000 - 1.0f);
							setLink(weight, mesh.get(i));
							addInstability(weight);
						}
					}
				} else {
					for(int i = meshOffset; i < mesh.size(); i++) {
						if (/*mesh.get(i).getAmplitude() > 0.01f && mesh.get(i).getInstability() < 0.3f &&*/ (getSquareDistance(mesh.get(i)) < 100.0f*100.0f) && (mesh.get(i).getPosX() < posX) && mesh.get(i) != this) {
//System.out.println(mesh.get(i).getAmplitude() + " " + mesh.get(i).getY());

						
							float weight = 0.2f*(e)*mesh.get(i).getY()*(activationFunction(x) * (1-activationFunction(x)));
							setLink(weight, mesh.get(i));
							addInstability(weight);
						}
					}
				}
				
			}	
			
			// suppress weights
			if (links.size() > 0 && getErrorDispersion() > 0.0f && connections == 0) { 
				for (int i = 0; i < links.size(); i ++) {
					float weight = links.get(i).getWeight();
					//weight = weight * (0.999f + 0.001f*(1.0f - errorDispersion*links.get(i).getNeuron().getErrorDispersion())); 
					//weight = weight * (0.999f + 0.001f *Math.abs(1.0f - e));
					//weight = weight * (0.99f + 0.01f *(y*(1.0f-e)));
					float _w = weight;
					weight = weight * (0.999f + 0.001f *(2.0f*links.get(i).getNeuron().getAmplitude()));

					addInstability(Math.abs(_w-weight));
					links.get(i).setWeight(weight); 
					
					if (Math.abs(links.get(i).getWeight()*links.get(i).getNeuron().getAmplitude()) < 0.001f) {
						links.get(i).getNeuron().removeConnections();
						links.get(i).setNeuron(null);
						links.set(i, null);
						links.remove(i);
					}
					
				}	
				//errorDispersion = 0.0f;
			}	
			
		} else {
//			if (Y.getValue() != 0) {
//				System.out.println(y);
//				System.out.println(errorBias);
//				System.out.println(Integer.toBinaryString(Y.getValue()));
//			}	
		}  
		
		// move bias
		//bias = (0.999f + 0.001f*(1.0f - Math.abs(e)))*bias + (0.001f)*(y)*Math.abs(e);
		//amplitude = (0.9f + 0.1f*(1.0f - Math.abs(e)))*amplitude + 0.1f*Math.abs(y - bias)*Math.abs(e);
		

		/*float _b = bias;
		bias = (0.999f)*bias + (0.001f)*(y);
		addStability(Math.abs(bias - _b));

		float _a = amplitude;
		amplitude = (0.999f)*amplitude + 0.001f*Math.abs(y - bias);		
		addStability(Math.abs(amplitude - _a));*/
		
		
		if (connections > 0) {
			float s;
			s = 0.01f*(getInstability() + Math.abs(e));
			bias = (1.0f - s)*bias + s*(y);
			
			s = 0.01f*(getInstability() + Math.abs(e));
			amplitude = (1.0f - s)*amplitude + s*Math.abs(y - bias);		
			
			addInstability(s*Math.abs(y));
			addInstability(s*Math.abs(y - bias));
		} else {
			float s;
			s = 0.01f;
			bias = (1.0f - s)*bias + s*(y);
			s = 0.1f;
			amplitude = (1.0f - s)*amplitude + s*Math.abs(y - bias);		
			
			addInstability(s*Math.abs(y));
			addInstability(s*Math.abs(y - bias));
		}
		
		//System.out.println(amplitude + " " + bias + " "+ y);
		
	}
}
