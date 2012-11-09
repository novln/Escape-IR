package fr.escape.game.entity.weapons.shot;

import java.util.Objects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import fr.escape.game.entity.EntityContainer;

public final class ShotFactory {
	
	//private static final Texture MISSILE_SHOT_TEXTURE = Foundation.resources.getTexture(TextureLoader.WEAPON_MISSILE_SHOT);
	//private static final Texture SHIBOLEET_SHOT_TEXTURE = Foundation.resources.getTexture(TextureLoader.WEAPON_SHIBOLEET_SHOT);

	public static Shot createBlackholeShot(World world, float x, float y, EntityContainer ec) {
		Objects.requireNonNull(world);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(x, y);
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.userData = "BlackHole";
		
		CircleShape shape = new CircleShape();
		shape.m_radius = 0.2f;
		
		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.density = 0.5f;
		fixture.friction = 0.3f;       
		fixture.restitution = 0.5f;
		
		Body body = world.createBody(bodyDef);
		body.createFixture(fixture);
		
		return new BlackHoleShot(body, ec, ec);
	}
	
	public static Shot createFireBallShot(EntityContainer ec) {
		return new Fireball(ec, ec);
	}
	
//	public static Shot createMissileShot(EntityContainer ec) {
//		return new AbstractShot(MISSILE_SHOT_TEXTURE, ec) {
//
//			@Override
//			public void receive(int message) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void update(Graphics graphics, long delta) {
//				
//				draw(graphics);
//				
//				Foundation.activity.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						moveBy(0, 3);
//						rotateBy(1);
//					}
//
//				});
//			}
//			
//		};
//	}
//	
//	public static Shot createShiboleetShot(EntityContainer ec) {
//		
//		return new AbstractShot(SHIBOLEET_SHOT_TEXTURE, ec) {
//
//			@Override
//			public void receive(int message) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void update(Graphics graphics, long delta) {
//				
//				draw(graphics);
//				
//				Foundation.activity.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						moveBy(0, 3);
//						rotateBy(1);
//					}
//
//				});
//			}
//			
//		};
//	}
}
