package mrg;

import robocode.*;
import robocode.util.Utils;

/*
 * Most good robots have a few things in common:
 * 1. They extend AdvancedRobot and only use non-blocking methods such as setAhead so that they can move, scan their opponent, and shoot at the same time
 * 2. They fire at their opponent whenever possible
 * 3. They spend a lot of time moving perpendicularly to their opponent
 * 
 * This robot does all three of these things. It therefore might be a good base for you to try improving off of.
 * 
 */

public class AdvancedRobotExample extends AdvancedRobot{

	public void run() {
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = e.getBearingRadians() + getHeadingRadians(); //The opponent's angle from our position
		setTurnRadarRightRadians(2.0 * Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians())); //Radar lock
		
		setTurnRightRadians(Utils.normalRelativeAngle(absoluteBearing + Math.PI / 2 - getHeadingRadians())); //Turn perpendicular to the opponent
		//A simple random movement
		if(getDistanceRemaining() == 0) {
			setAhead(200 - 400 * Math.random());
		}
		
		setTurnGunRightRadians(Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians())); //Aim directly at the opponent
		setFire(3.0);
	}
}
