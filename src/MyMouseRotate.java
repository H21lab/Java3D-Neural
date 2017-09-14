import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Enumeration;

import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;

class MyMouseRotate extends MouseRotate {
	private int id;
	private AWTEvent[] event;
	private WakeupCriterion wakeup;
	private WakeupCriterion[] mouseEvents;
	private WakeupOr mouseCriterion;

	private int mouseX;
	private int mouseY;
	
	private double alfa = 0.0;
	private double beta = 0.0;
	private double r = 2.5;
	
	public MyMouseRotate() {
		super();
		
		
	}

	public void initialize() {
		mouseEvents = new WakeupCriterion[5];
		mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
		mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
		mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
		mouseEvents[3] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
		mouseEvents[4] = new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL);
		mouseCriterion = new WakeupOr(mouseEvents);
		wakeupOn(mouseCriterion);
	}

	public void processStimulus(Enumeration criteria) {
		id = 0;
		while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				for (int i = 0; i < event.length; i++) {
					id = event[i].getID();

					if (id == MouseEvent.MOUSE_DRAGGED) {
						if (((MouseEvent) event[i]).getModifiers() == MouseEvent.BUTTON1_MASK) {
							
							
							int dX, dY;
							dX = ((MouseEvent) event[i]).getX() - mouseX;
							dY = ((MouseEvent) event[i]).getY() - mouseY;
							
							alfa -= dX/300.0;
							beta += dY/300.0;
							
							Transform3D locator = new Transform3D();

							Point3d eye = new Point3d(r*Math.sin(alfa)*Math.cos(beta), r*Math.sin(beta), r*Math.cos(alfa)*Math.cos(beta));
							Point3d center = new Point3d(0.0, 0.0, 0.0);
							Vector3d up = new Vector3d(0.0, Math.cos(beta), 0.0);
							
							
					        locator.lookAt(eye, center, up);
					        locator.invert();
					        transformGroup.setTransform(locator);
							
							
							mouseX = ((MouseEvent) event[i]).getX();
							mouseY = ((MouseEvent) event[i]).getY();
						}
					}

					else if (id == MouseEvent.MOUSE_PRESSED) {
						mouseX = ((MouseEvent) event[i]).getX();
						mouseY = ((MouseEvent) event[i]).getY();
						
						if ((((MouseEvent) event[i]).getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
							if (JavaSample.running) {
								JavaSample.running = false;
							} else {
								JavaSample.running = true;
							}
						}
							
					} else if (id == MouseEvent.MOUSE_WHEEL) {
						MouseWheelEvent e = (MouseWheelEvent)event[i];
						
						r += 0.005f*e.getUnitsToScroll();
						
						Transform3D locator = new Transform3D();

						Point3d eye = new Point3d(r*Math.sin(alfa)*Math.cos(beta), r*Math.sin(beta), r*Math.cos(alfa)*Math.cos(beta));
						Point3d center = new Point3d(0.0, 0.0, 0.0);
						Vector3d up = new Vector3d(0.0, Math.cos(beta), 0.0);
						
						
				        locator.lookAt(eye, center, up);
				        locator.invert();
				        transformGroup.setTransform(locator);
						
						mouseX = ((MouseEvent) event[i]).getX();
						mouseY = ((MouseEvent) event[i]).getY();
					}

					this.wakeupOn(new WakeupOr(mouseEvents));
				}
			}
		}
	}
}
