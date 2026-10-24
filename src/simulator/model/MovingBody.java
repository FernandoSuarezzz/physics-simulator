package simulator.model;

import simulator.misc.Vector2D;

public class MovingBody extends Body{
	
	private Vector2D acceleration;

	public MovingBody(String id, String gid, Vector2D p, Vector2D v, double m) throws IllegalArgumentException {
		super(id, gid, p, v, m);
		this.acceleration=new Vector2D();//Inicializo la aceleracion a 0, inicialmente
	}

	@Override
	void advance(double dt) {
		if(m!=0) {
			acceleration=f.scale(1.0/m);
			this.p=p.plus((v.scale(dt)).plus(acceleration.scale((dt*dt)*0.5)));
			this.v=v.plus(acceleration.scale(dt));
			
		}
	}

}
