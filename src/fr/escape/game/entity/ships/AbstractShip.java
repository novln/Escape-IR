package fr.escape.game.entity.ships;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fr.escape.app.Foundation;
import fr.escape.app.Graphics;
import fr.escape.game.User;
import fr.escape.game.entity.CoordinateConverter;
import fr.escape.game.entity.Entity;
import fr.escape.game.entity.EntityContainer;
import fr.escape.game.entity.bonus.Bonus;
import fr.escape.game.entity.weapons.Weapon;
import fr.escape.game.entity.weapons.shot.Shot;
import fr.escape.graphics.AnimationTexture;

//TODO Comment
public abstract class AbstractShip implements Ship {
	
	private static final int PLAYER_MASK = NPC_TYPE | SHOT_TYPE | BONUS_TYPE;
	private static final int INVULNERABILITY_MASK = 0x0001 | BONUS_TYPE;
	private static final int LEFTLOOP = 2;
	private static final int RIGHTLOOP = 1;
	
	private static final String TAG = AbstractShip.class.getSimpleName();
	
	private Body body;
	private final BodyDef bodyDef;
	private final FixtureDef fixture;
	private final List<Weapon> weapons;
	private final boolean isPlayer;
	
	private final EntityContainer econtainer;
	
	private final AnimationTexture coreShip;
	
	private final Random random;
	
	private int activeWeapon;
	private boolean isWeaponLoaded;
	private boolean executeLeftLoop;
	private boolean executeRightLoop;

	private int angle;
	private int life;
	
	public AbstractShip(BodyDef bodyDef, FixtureDef fixture, List<Weapon> weapons, boolean isPlayer, int life, EntityContainer container, AnimationTexture textures) {
		
		this.bodyDef = Objects.requireNonNull(bodyDef);
		this.fixture = Objects.requireNonNull(fixture);
		this.weapons = Objects.requireNonNull(weapons);
		this.isPlayer = isPlayer;
		
		this.econtainer = Objects.requireNonNull(container);
		
		this.coreShip = Objects.requireNonNull(textures);
		
		this.activeWeapon = 0;
		this.isWeaponLoaded = false;
		this.executeLeftLoop = false;
		this.executeRightLoop = false;
		
		this.life = life;
		
		this.random = new Random();
	}
	
	@Override
	public boolean isPlayer() {
		return isPlayer;
	}
	
	@Override
	public void damage(int value) {
		
		life -= value;
		
		if(life <= 0) {
			Foundation.ACTIVITY.error(TAG, "A Ship has been destroy.");
			this.toDestroy();
		}
	}
	
	@Override
	public Weapon getActiveWeapon() {
		return weapons.get(activeWeapon);
	}
	
	@Override
	public List<Weapon> getAllWeapons() {
		return weapons;
	}
	
	@Override
	public void setActiveWeapon(int which) {
		
		if(which < 0 || which >= weapons.size()) {
			throw new IndexOutOfBoundsException();
		}
		
		getActiveWeapon().unload();
		
		this.isWeaponLoaded = false;
		this.activeWeapon = which;
	}
	
	@Override
	public BodyDef getBodyDef() {
		return bodyDef;
	}
	
	@Override
	public Body getBody() {
		return body;
	}
	
	@Override
	public void setBody(Body body) {
		this.body = body;
	}
	
	@Override
	public void createBody(World world) {
		if(body == null) {
			body = world.createBody(bodyDef);
			body.createFixture(fixture);
			body.setUserData(this);
		}
	}
	
	public int getRadius() {
		return coreShip.getHeight() / 2;
	}
	
	@Override
	public float getX() {
		if(isPlayer && body == null) System.err.println("Player body null !!!");
		return body.getPosition().x;
	}
	
	@Override
	public float getY() {
		return body.getPosition().y;
	}
	
	@Override
	public void draw(Graphics graphics) {
		
		int x = CoordinateConverter.toPixelX(getX()) - (coreShip.getWidth() / 2);
		int y = CoordinateConverter.toPixelY(getY()) - (coreShip.getHeight() / 2);
			
		graphics.draw(coreShip, x, y, x + coreShip.getWidth(), y + coreShip.getHeight(), angle);
		//graphics.draw(getEdge(), Color.RED);
		
		//graphics.draw(Shapes.createCircle(CoordinateConverter.toPixelX(getX()),CoordinateConverter.toPixelY(getY()),CoordinateConverter.toPixelX(body.getFixtureList().getShape().m_radius)), Color.CYAN);
	}
	
	@Override
	public void update(Graphics graphics, long delta) {
		
		if(executeRightLoop || executeLeftLoop) {
			
			if(executeRightLoop) {
				coreShip.forward();
			} else {
				coreShip.reverse();
			}
			
			if(coreShip.hasNext()) {
				coreShip.next();
			} else {
				coreShip.rewind();
				executeLeftLoop = false;
				executeRightLoop = false;
			}
		}
		
		draw(graphics);
		getActiveWeapon().update(graphics, delta);
		
		if(!econtainer.isInside(getEdge())) {
			econtainer.edgeReached(this);
		}
	}
	
	@Override
	public void rotateBy(int angle) {
		this.setRotation(this.angle + angle);
	}
	
	@Override
	public void setRotation(int angle) {
		angle = angle % 360;
		this.angle = angle;
	}
	
	public int getAngle() {
		return this.angle;
	}
	
	@Override
	public boolean isWeaponLoaded() {
		return isWeaponLoaded;
	}
	
	@Override
	public boolean loadWeapon() {
		
		Weapon activeWeapon = getActiveWeapon();
		
		if(activeWeapon.load(getX(), getY() - CoordinateConverter.toMeterY(coreShip.getHeight()))) {
			isWeaponLoaded = true;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean reloadWeapon(int which, int number) {
		return Objects.requireNonNull(weapons.get(which)).reload(number);
	}
	
	@Override
	public boolean fireWeapon() {
		setActiveWeapon(random.nextInt(3));
		return loadWeapon() && fireWeapon(new float[]{0.0f, 0.0f, 5.0f});
	}
	
	@Override
	public boolean fireWeapon(float[] velocity) {
		
		Weapon activeWeapon = getActiveWeapon();
		
		if(activeWeapon.fire(velocity, isPlayer)) {
			isWeaponLoaded = false;
			return true;
		}
		
		return false;
	}
	
	@Override
	public void toDestroy() {
		if(!isPlayer) {
			Foundation.ACTIVITY.post(new Runnable() {
				@Override
				public void run() {
					econtainer.pushBonus(getX(), getY());
				}
			});
			econtainer.destroy(this);
		}
		//econtainer.destroy(this);
	}
	
	@Override
	public void moveTo(float x, float y) {
		if(body.isActive()) {
			body.setLinearVelocity(new Vec2(x - getX(), y - getY()));
		}
	}
	
	private void setInvulnerable(boolean invulnerable) {
		getBody().getFixtureList().m_filter.maskBits = (invulnerable)?INVULNERABILITY_MASK:PLAYER_MASK;
	}
	
	private void doLooping(float[] velocity) {
		
		int mode = (int) velocity[3];
		switch (mode) {
			case RIGHTLOOP: {
				setInvulnerable(true);
				executeRightLoop = true;
				if(velocity[0] <= 0) {
					velocity[3] = 0.0f;
				} else {
					velocity[0] -= 2.0f;
				}
				break;
			}
			case LEFTLOOP: {
				setInvulnerable(true);
				executeLeftLoop = true;
				if(velocity[0] <= 0) {
					velocity[3] = 0.0f;
				} else {
					velocity[0] -= 2.0f;
				}
				break;
			}
			default: {
				coreShip.rewind();
				executeLeftLoop = false;
				executeRightLoop = false;
				setInvulnerable(false);
				break;
			}
		}

	}
	
	@Override
	public void moveBy(float[] velocity) {
		
		if(body.isActive()) {			
			
			Shot shot = getActiveWeapon().getShot();
			Graphics graphics = Foundation.GRAPHICS;
			
			int x = CoordinateConverter.toPixelX(body.getPosition().x);
			int y = CoordinateConverter.toPixelY(body.getPosition().y);
			int radius = getRadius();
			
			if(isPlayer() && (x <= radius || x >= graphics.getWidth() - radius || y <= (graphics.getHeight() * 2) / 3 + radius || y >= graphics.getHeight() - radius)) {
				velocity[0] = 0.1f;
				velocity[1] *= -1;
				velocity[2] *= -1;
			}
			
			float[] tmp = Arrays.copyOfRange(velocity, 0, velocity.length);
			
			doLooping(velocity);
			
			if(velocity[0] > 0) {
				
				body.setLinearVelocity(new Vec2(velocity[1], velocity[2]));
				velocity[0] -= Math.abs(Math.max(Math.abs(velocity[1]), Math.abs(velocity[2])));
				
			} else {
				body.setLinearVelocity(new Vec2(0, 0));
				velocity[1] = 0.0f;
				velocity[2] = 0.0f;
			}
			
			if(shot != null) {
				tmp[0] = 0.0f;
				shot.moveBy(tmp);
			}

		}
	}
	
	public void receive(int message) {
		
	}
	
	@Override
	public Rectangle getEdge() {
		int x = CoordinateConverter.toPixelX(getX());
		int y = CoordinateConverter.toPixelY(getY());
		
		return new Rectangle(x - (coreShip.getWidth() / 2), y - (coreShip.getHeight() / 2), coreShip.getWidth(), coreShip.getHeight());
	}
	
	@Override
	public void collision(User user, int whoami, Entity e, int whois) {
		
		if(isPlayer && whois != BONUS_TYPE) {
			Foundation.ACTIVITY.error(TAG, "Hit, player lost a life.");
			user.removeOneLife();
		}
		
		switch(whois) {
			case SHOT_TYPE: { 
				
				Foundation.ACTIVITY.debug(TAG, "Player or NPC hit by Shot.");
				
				Shot shot = (Shot) e;
				shot.receive(Shot.MESSAGE_HIT);
				
				this.damage(shot.getDamage());
				
				break;
			}
			case BONUS_TYPE: {
				if(isPlayer) {
					
					Foundation.ACTIVITY.debug(TAG, "Player hit by Bonus.");
					
					Bonus bonus = (Bonus) e;
					user.addBonus(bonus.getWeapon(), bonus.getNumber());
					
					e.toDestroy();
					
				}
				break;
			}
			case NPC_TYPE: {
				
				Foundation.ACTIVITY.debug(TAG, "Player hit by NPC.");
				
				Ship ship = (Ship) e;
				
				ship.damage(1);
				this.damage(1);
				
				break;
			}
			case PLAYER_TYPE: {
				Foundation.ACTIVITY.debug(TAG, "Hit, player lost a life.");
				Foundation.ACTIVITY.debug(TAG, "NPC hit by Player.");
				break;
			}
			default: { 
				Foundation.ACTIVITY.error(TAG, "Unknown touch contact {"+this+", "+e+"}");
				break;
			}
		}
		
	}
	
	@Override
	public boolean reset(World world) {
		
		// TODO Reset Ship Armor
		setBody(null);
		createBody(world);
		
		// Reset All Weapons
		for(Weapon w : getAllWeapons()) {
			if(!w.reset()) {
				return false;
			}
		}
		
		return true;
	}
	
}