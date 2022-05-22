package mrg;
import robocode.*;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import robocode.util.Utils;

/*
 *  This robot was originally written to compete in a competition that only allowed robots under 750 codesize, so the code is not particularly nice to read.
 *  
 *  It aims by keeping a log of the opponents velocities and then searching that log for the most similar situation to the current situation. Then it plays forward
 *  the previous movements at the current position to find where it should aim. To see its predictions for the other robot, try enabling paint in the robot settings.
 *  
 *  It first tries orbiting around the opponent, then dodging back and forth, then moving randomly.
 */

public class ExtraCredit extends AdvancedRobot {
	final static int WALLSTICK = 120;
	final static int PREF_DISTANCE = 600;
	final static int MAX_MATCH_LENGTH = 40;
	final static double DIVE_PROTECTION = 1;
	final static int ANTI_RAM_DIST = 100;
	final static int BPOWER_MULT = 400;
	static int moveMode;
	static int enemyDamage;
	static double dir = moveMode = enemyDamage = 1;
	static double enemyEnergy;
	static double enemyGunHeat;
	static double distance;
	
	public void run(){
		if(moveMode == 1) setAllColors(new Color(50, 205, 50));
		enemyGunHeat = 0;
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
	public void onScannedRobot(ScannedRobotEvent e){ 
		double absBearing;
		double wallSmooth;
		int matchPosition;
		int matchLen = MAX_MATCH_LENGTH;
		
		if((enemyGunHeat-= 0.1) < 0 & (absBearing = enemyEnergy - (enemyEnergy = e.getEnergy())) >= 0.1 && absBearing <= 3) {
			dir *= moveMode == 2 ? -1 : 1;
			enemyGunHeat = 1 + absBearing / 5;
		}
		setTurnRadarRightRadians(2 * Utils.normalRelativeAngle((absBearing = e.getBearingRadians() + getHeadingRadians()) - getRadarHeadingRadians()) );

		eLog = String.valueOf((char) (e.getVelocity() * (Math.sin(wallSmooth = e.getHeadingRadians() - absBearing)))).concat(
				   String.valueOf((char) (e.getVelocity() * (Math.cos(wallSmooth))))).concat(eLog);   

		double diveTest = wallSmooth = absBearing - (Math.PI / 2 + ((distance = e.getDistance()) >= PREF_DISTANCE ? 0 : 0.4)) * dir;
		if (Math.random() < .04 && moveMode > 2 && distance > ANTI_RAM_DIST) dir=-dir;

		while (!new Rectangle2D.Double(19.0,19.0,762.0,562.0).contains(
				getX() + Math.sin(wallSmooth) * WALLSTICK, getY() + Math.cos(wallSmooth) * WALLSTICK)){
			wallSmooth = wallSmooth + dir * .1;	
			if (Math.random() < .01 && (moveMode > 2 || Math.abs(wallSmooth - diveTest) > DIVE_PROTECTION))
				dir=-dir;
		}
		setTurnRightRadians(Math.tan(wallSmooth = Utils.normalRelativeAngle(wallSmooth - getHeadingRadians())));
	    setAhead(100*(Math.abs(wallSmooth) > Math.PI/2 ?-1:1));
	    
		setFire(wallSmooth = Math.max(0.1, Math.min(3, distance < 100 ? 3 : Math.min(enemyEnergy / 4, Math.min(getEnergy() / 10, 1 + BPOWER_MULT / distance)))));

		while((matchPosition = eLog.indexOf(eLog.substring(0, (--matchLen)), 128)) < 0);
	    matchPosition += matchPosition % 2;
	    matchLen = 0;
	    diveTest = distance;
	    wallSmooth = (20 - 3 * wallSmooth);
		Graphics2D g= getGraphics();
		g.setColor(Color.green);
	    do { 
	    	absBearing += ((short) eLog.charAt(matchPosition-=2)) /  
	    			(diveTest +=((short) eLog.charAt(matchPosition + 1)));
	    	g.drawRect((int)(getX() + diveTest *Math.sin(absBearing))-18, (int)(getY() + diveTest* Math.cos(absBearing))-18, 36, 36);
	    }
	    while ((matchLen += wallSmooth) < diveTest && matchPosition >= 2);
		setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians()));
    }
	public void onHitByBullet(HitByBulletEvent e) {
		enemyEnergy += e.getPower() * 3;
		if(distance > 50 && enemyDamage++ / 2 >= (getRoundNum() + 1) + moveMode) {
			setAllColors(moveMode == 1 ? new Color(250,128,114) : new Color(135, 206, 235));
			moveMode++;
		}
		
	}
	public void onBulletHit(BulletHitEvent e) {
		enemyEnergy -= Rules.getBulletDamage(e.getBullet().getPower());
	}
	static String eLog = ""
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 1 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 1 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 1 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 1 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char)-1 + (char)-2 + (char)-3 + (char)-4 + (char)-5 + (char)-6 
			   + (char)-7 + (char)-8 + (char) 8 + (char) 7 + (char) 6 + (char) 5 
			   + (char) 4 + (char) 3 + (char) 2 + (char) 1 + (char) 0 + (char) 0
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 1 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 1 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 1 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 1 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 + (char) 0 
			   + (char)-1 + (char)-2 + (char)-3 + (char)-4 + (char)-5 + (char)-6 
			   + (char)-7 + (char)-8 + (char) 8 + (char) 7 + (char) 6 + (char) 5 
			   + (char) 4 + (char) 3 + (char) 2 + (char) 1 + (char) 0 + (char) 0
			   ;
}	
      				