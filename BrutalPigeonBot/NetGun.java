package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class NetGun extends RobotPlayer{
	static void runNetGun() throws GameActionException {
		RobotInfo[] RI = rc.senseNearbyRobots();
		for (RobotInfo ri : RI) {
			if (ri.team == rc.getTeam().opponent() && ri.type == RobotType.DELIVERY_DRONE && rc.canShootUnit(ri.ID) && rc.isReady()) {
				rc.shootUnit(ri.ID); //shoot 'em all
				break;
			}
		}
	}
}
