import java.awt.Color;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.Canvas3D;


public class MyCanvas3D extends Canvas3D{
	NeuralNetwork neuralNetwork;

	public MyCanvas3D(GraphicsConfiguration arg0) {
	    super(arg0);
	    // TODO Auto-generated constructor stub
    }

	public void preRender() {
		super.preRender();

		if (neuralNetwork != null) {
			neuralNetwork.draw(this.getGraphics2D());
		}
		
		this.getGraphics2D().flush(true);
	}
	
	void setNetwork(NeuralNetwork n) {
		neuralNetwork = n;
	}
}
