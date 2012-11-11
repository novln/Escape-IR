package fr.escape.game.scenario;

import fr.escape.app.Foundation;
import fr.escape.game.entity.EntityContainer;
import fr.escape.game.entity.ships.ShipFactory;
import fr.escape.resources.scenario.ScenarioLoader;

public final class Earth extends AbstractStage {

	public Earth(ShipFactory factory, EntityContainer container) {
		
		super(container);
	
		Scenario e1 = Foundation.RESOURCES.getScenario(ScenarioLoader.EARTH_1, factory);
		
		getWaitingScenario().put(e1.getStart(), e1);
		
//		getWaitingScenario().put(Integer.valueOf(2), new Scenario() {
//			
//			@Override
//			public int getStart() {
//				return 2;
//			}
//			
//			@Override
//			public boolean hasFinished() {
//				return false;
//			}
//			
//			@Override
//			public void action(int time) {
//				System.out.println(time+" "+this);
//			}
//			
//			public String toString() {
//				return getID()+" -> "+getStart();
//			}
//
//			@Override
//			public int getID() {
//				return 2;
//			}
//			
//		});
	}
}
