
public class Link {
	private Neuron neuron;
	private float weight;
	
	public Link(float w, Neuron neuron) {
	    super();
	    this.neuron = neuron;
	    if (w > 1.0f) {
			w = 1.0f;
		} else if (w < -1.0f) {
			w = -1.0f;
		}
	    this.weight = w;
    }	
	public Neuron getNeuron() {
    	return neuron;
    }
	public void setNeuron(Neuron neuron) {
    	this.neuron = neuron;
    }
	public float getWeight() {
    	return weight;
    }
	public void setWeight(float w) {
		if (w > 1.0f) {
			w = 1.0f;
		} else if (w < -1.0f) {
			w = -1.0f;
		}
    	this.weight = w;
    }
}
