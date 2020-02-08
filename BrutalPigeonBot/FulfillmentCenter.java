package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class FulfillmentCenter extends RobotPlayer{
	static void runFulfillmentCenter() throws GameActionException {
		for(Direction dir : directions) if(rc.getTeamSoup() > 610){
			tryBuild(RobotType.DELIVERY_DRONE, dir);
		}
	}
}
