package mrg;

import robocode.*;
import robocode.util.Utils;

/*
 * Example of tracking the opponent's energy level and moving when they might have fired
 */

public class BulletDodging extends AdvancedRobot{
	double enemyEnergy = 100; //Robots start the round with 100 energy
	int direction = 1;
	public void run() {
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = e.getBearingRadians() + getHeadingRadians(); //The opponent's angle from our position
		setTurnRadarRightRadians(2.0 * Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians())); //Radar lock
		
		setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians())); //Turn perpendicular to the opponent
		
		//Find the difference between the opponents energy last turn and their energy this turn. If that is in range for bullet powers, then dodge.
		double energyChange = enemyEnergy - e.getEnergy();
		enemyEnergy = e.getEnergy();
		if(energyChange > 0 && energyChange <= 3.0) {
			setAhead(40 * direction);
		}
	}
	//Reverse direction when we hit the wall
	public void onHitWall(HitWallEvent e) {
		direction = -direction;
	}
}
