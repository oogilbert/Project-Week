package mrg;

import robocode.*;
import robocode.util.Utils;

/*
 * Linear targeting works by assuming the opponent will continue at their current speed and direction.
 * We then use some trigonometry to calculate where a bullet will intersect with their path.
 */

public class LinearTargeting extends AdvancedRobot{
	public void run() {
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearing = e.getBearingRadians() + getHeadingRadians(); //The opponent's angle from our position
		
		setTurnRadarRightRadians(2.0 * Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians())); //Radar lock
		
		double lateralVelocity = e.getVelocity() * Math.sin(e.getHeadingRadians() - absoluteBearing); //The lateral component of the opponent's velocity vector
		double aimOffset = Math.asin(lateralVelocity / Rules.getBulletSpeed(2.0)); //The angle we need to adjust our aim by to hit the opponent
		
		setTurnGunRightRadians(Utils.normalRelativeAngle(absoluteBearing + aimOffset - getGunHeadingRadians()));
		setFire(2.0);
	}
}
