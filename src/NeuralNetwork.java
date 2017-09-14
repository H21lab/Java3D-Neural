import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;


public class NeuralNetwork {

	ArrayList<Neuron> mesh;
	int input;
	ArrayList<Neuron> output;
	
	NeuralNetwork(Vector<Integer> dim) {
		input = dim.get(0);
		
		
		mesh = new ArrayList<Neuron>();
		for (int i = 0; i < dim.get(1); i++) {
			mesh.add(new Neuron(dim.get(0), mesh, (i/dim.get(0))*20 + 20, (i%dim.get(0))*20 + 20));
		}
		mesh.get(dim.get(1) - 2).setPosX(16*20 + 20);
		mesh.get(dim.get(1) - 2).setPosY(0*20 + 20);
		mesh.get(dim.get(1) - 1).setPosX(16*20 + 20);
		mesh.get(dim.get(1) - 1).setPosY(15*20 + 20);
		
		// create connection from mesh to input
		for (int i = 0; i < dim.get(0) ; i++) {
			mesh.get(i + input).setLink(1.0f, mesh.get(i));
			mesh.get(i + input).setLock(1);
		}
		
		//for (int i = 0; i < dim.get(0) ; i++) {
		//	mesh.get(i).setLock(1);
		//}
		
		output = new ArrayList<Neuron>();
		for (int i = 0; i < dim.get(2) ; i++) {
			output.add(mesh.get(mesh.size() - 1 - i));
			mesh.get(mesh.size() - 1 - i).setConnections(1);
		}
	}
	public Vector<Float> calculate(Vector<Float> x) {
		// set input to network
		for (int i = 0; i < input; i++) {
			mesh.get(i).setY(x.get(i));		
		}
		
		// calculate
		for (int i = input; i < mesh.size(); i++) {
			mesh.get(i).calculate();
			
//System.out.print(mesh.get(i).getLinks().size() + " ");
		}		
		
		Vector<Float> y = new Vector<Float>();
		// get output
		for (int i = 0; i < output.size(); i++) {
			y.add(output.get(i).getY());
		}
		
		
		return y;
	}
	public void learn(Vector<Float> errors) {
		// set error
		for (int i = 0; i < output.size(); i++) {
			output.get(i).setError(errors.get(i));
			
//System.out.println(errors.get(i));			
//System.out.println(Integer.toBinaryString(output.get(i).getE()) + " " + output.get(i).getErrorBias());
		}
		// propagate error
		/*
		int c = 0;	// prevent loop
		boolean propagate = false;
		do {
			for (int i = mesh.size() - 1; i >= input; i--) {
				if (mesh.get(i).propagateError(false) == false) {
					propagate = true;
				}
			}
			c++;
		} while (propagate && c < 10);
		*/
		
//System.out.println("-----------------");		
		// force the propagation
		for (int i = mesh.size() - 1; i >= input; i--) {
			mesh.get(i).propagateError(true);
		}
		
		// set error and dispersion for input to 0
		for (int i = 0; i < input*2; i++) {
			mesh.get(i).setError(0.0f);
			mesh.get(i).setErrorDispersion(0.0f);
		}
//		for (int i = input; i < input*2; i++) {
//			mesh.get(i).setError(0.0f);
//			mesh.get(i).setErrorDispersion(0.0f);
//		}
		
//System.out.print("\n");
		
		// learn
		for (int i = mesh.size() - 1; i >= input; i--) {
			mesh.get(i).learn();
		}
	}
	
	void draw(Graphics2D g) {
		
		for (int i = 0; i < mesh.size(); i++) {
			ArrayList<Link> links = mesh.get(i).getLinks();
			for (int l = 0; l < links.size(); l ++) {
				try {

					int c;
					//c = (int)(Math.abs(links.get(l).getWeight())*200.0f) + 50;
					//g.setColor(new Color(c, c, c));
					//g.drawLine(mesh.get(i).getPosX(), mesh.get(i).getPosY(), links.get(l).getNeuron().getPosX(), links.get(l).getNeuron().getPosY());
				
					c = (int)(links.get(l).getNeuron().getY()*Math.abs(links.get(l).getWeight())*255.0f);
					if (links.get(l).getWeight() > 0.0f) {
						g.setColor(new Color(0, c, 0));
					} else {
						g.setColor(new Color(c, 0, 0));
					}
					g.drawLine(mesh.get(i).getPosX(), mesh.get(i).getPosY(), links.get(l).getNeuron().getPosX(), links.get(l).getNeuron().getPosY());
				
					
					//c = (int)(links.get(l).getNeuron().getE()*links.get(l).getWeight()*255.0f);
					//if (c < 0) {
					//	g.setColor(new Color(0, 0, -c));
					//} else {
					//	g.setColor(new Color(0, 0, c));
					//}
					//g.drawLine(mesh.get(i).getPosX() + 1, mesh.get(i).getPosY() + 1, links.get(l).getNeuron().getPosX() + 1, links.get(l).getNeuron().getPosY() + 1);
					
				} catch (Exception e){
					
				}
			}	
		}
		
		for (int i = 0; i < mesh.size(); i++) {
			try {
				int c = (int)(mesh.get(i).getY()*255.0f);
				g.setColor(new Color(0, c, 0));
			    g.fillRect(mesh.get(i).getPosX(), mesh.get(i).getPosY(), 2, 2);
			} catch (Exception e){
				
			}
		}
		
		
		//for (int i = input*2; i < input*3; i++) {
		//	System.out.println(i + " " + mesh.get(i).getBias() + " " +  mesh.get(i).getY());
		//}
	}
}
