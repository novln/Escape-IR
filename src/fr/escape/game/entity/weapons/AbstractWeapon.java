package fr.escape.game.entity.weapons;

import java.util.Objects;

import fr.escape.app.Graphics;
import fr.escape.game.entity.EntityContainer;
import fr.escape.game.entity.weapons.shot.Shot;
import fr.escape.game.entity.weapons.shot.ShotFactory;
import fr.escape.graphics.Texture;

public abstract class AbstractWeapon implements Weapon {
	
	private static final int MAX_AMMUNITION = 250;
	
	private final Texture drawable;
	private final EntityContainer container;
	private final ShotFactory factory;
	private final int defaultAmmunition;
	
	private int ammunition;
	private Shot shot;
	
	public AbstractWeapon(Texture texture, EntityContainer eContainer, ShotFactory sFactory, int defaultAmmunition) {
		this.drawable = Objects.requireNonNull(texture);
		this.container = Objects.requireNonNull(eContainer);
		this.factory = Objects.requireNonNull(sFactory);
		this.ammunition = defaultAmmunition;
		this.defaultAmmunition = defaultAmmunition;
	}

	@Override
	public Texture getDrawable() {
		return drawable;
	}

	public int getAmmunition() {
		return ammunition;
	}
	
	@Override
	public boolean isEmpty() {
		return getAmmunition() <= 0;
	}
	
	protected abstract Shot createShot(float x, float y);

	protected ShotFactory getFactory() {
		return factory;
	}
	
	@Override
	public boolean load(float x, float y) {
		if(!isEmpty() && shot == null) {
			
			shot = Objects.requireNonNull(createShot(x, y));
			shot.receive(Shot.MESSAGE_LOAD);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean reload(int number) {
		
		if(number <= 0) {
			return false;
		}
		
		int ammunition = number + this.ammunition;
		
		if(ammunition > MAX_AMMUNITION) {
			ammunition = MAX_AMMUNITION;
		}
		
		this.ammunition = ammunition;
		
		return true;
	}
	
	@Override
	public boolean unload() {
		if(shot != null) {
			shot.receive(Shot.MESSAGE_DESTROY);
			shot = null;
		}
		return false;
	}
	
	@Override
	public boolean fire(float[] velocity, boolean isPlayer) {

		if(shot != null) {
			
			// TODO
			shot.moveBy(velocity);
			shot.setFireMask(isPlayer);
			
			container.push(shot);
			shot.receive(Shot.MESSAGE_FIRE);
			
			// TODO Apply Speed and Angle
			shot.receive(Shot.MESSAGE_CRUISE);
			
			shot = null;
			ammunition--;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public Shot getShot() {
		return shot;
	}
	
	@Override
	public void update(Graphics graphics, long delta) {
		if(shot != null) {
			shot.update(graphics, delta);
		}
	}
	
	@Override
	public boolean reset() {
		ammunition = defaultAmmunition;
		return true;
	}
}
