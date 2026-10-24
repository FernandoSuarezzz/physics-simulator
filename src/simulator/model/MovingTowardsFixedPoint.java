package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;


public class MovingTowardsFixedPoint implements ForceLaws{
	
	Vector2D c;
	double g;
	
	public MovingTowardsFixedPoint(Vector2D c,double g) throws IllegalArgumentException{
		if(c==null)throw new IllegalArgumentException("c cannot be null");
		if(g<=0)throw new IllegalArgumentException("g must be positive");
		this.c=c;
		this.g=g;
	}

	@Override
	public void apply(List<Body> bs) {
		for(Body b: bs) {					
			b.addForce((c.minus(b.getPosition())).direction().scale(b.getMass()*g));
		}
	}
	
	public String toString() {
		return "Moving towards "+c+" with constant acceleration "+g;		
	}
}
