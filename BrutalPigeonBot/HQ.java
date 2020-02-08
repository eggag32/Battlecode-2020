package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class HQ extends RobotPlayer{
	static void runHQ() throws GameActionException {
        if(turnCount == 1){
            int best = Integer.MAX_VALUE; //lowest elevation around the HQ
            for(Direction dir : directions){
                MapLocation ml = rc.getLocation().add(dir);
                if(valid(ml) && rc.canSenseLocation(ml)){
                    best = Math.min(rc.senseElevation(ml), best);
                }
            }
            int l = 0, r = 3500;
            for(int i = 0; i < 100; i++){
                int mid = (l + r) / 2;
                if(GameConstants.getWaterLevel(mid) > best) r = mid;
                else l = mid;
            }
            willFlood = Math.max(l, 500);
            int[] message = new int[7];
            message[5] = sec1;
            message[6] = sec2;
            message[0] = 17;
            message[1] = willFlood + sec3;
            message[2] = 4252 + sec4;
            message[3] = 78543846;
            message[4] = 9812464;
            int cst;
            if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
            else cst = Math.min(rc.getTeamSoup(), 1);
            if (cst > 0) rc.submitTransaction(message, cst);
        }
        else if(turnCount % 300 == 1){
            int[] message = new int[7];
            message[5] = sec1;
            message[6] = sec2;
            message[0] = 17;
            message[1] = willFlood + sec3;
            message[2] = 4252 + sec4;
            message[3] = 78543846;
            message[4] = 9812464;
            int cst;
            if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
            else cst = Math.min(rc.getTeamSoup(), 1);
            if (cst > 0) rc.submitTransaction(message, cst);
        }
        for (Direction dir : directions) {
            if (numberMiners >= minersDemand) break;
            if (tryBuild(RobotType.MINER, dir)) {
                numberMiners++;
            }
        }
        MapLocation loc = rc.getLocation();
        if (turnCount == 1) {
            for (int i = Math.max(loc.x - 8, 0); i <= Math.min(width - 1, loc.x + 8); i++) {
                for (int j = Math.max(loc.y - 8, 0); j <= Math.min(height - 1, loc.y + 8); j++) {
                    MapLocation m = new MapLocation(i, j);
                    if (rc.canSenseLocation(m) && rc.senseSoup(m) > 0) {
                        int amtSoup = rc.senseSoup(m);
                        int[] message = new int[7];
                        message[5] = sec1; //for security
                        message[6] = sec2; //for security
                        message[0] = 1; //type
                        message[1] = i + sec3; //the x of the soup (encode for security)
                        message[2] = j + sec4; //the y of the soup
                        message[3] = amtSoup; //how much soup, we obviously want more: MORE SOUP!
                        message[4] = 0; //don't need for this kind of message
                        int cst;
                        if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                        else cst = Math.min(rc.getTeamSoup(), 1);
                        if (cst > 0) {
                            rc.submitTransaction(message, cst);
                        }
                    }
                }
            }
        }
        if (turnCount % 300 == 1) {
            int[] message = new int[7];
            message[5] = sec1; //for security
            message[6] = sec2; //for security
            message[0] = 2; //type
            message[1] = rc.getLocation().x + sec3; //the x
            message[2] = rc.getLocation().y + sec4; //the y
            message[3] = 0;
            message[4] = 0; //don't need for this kind of message
            int cst;
            if (averageEnemyMessageCost != 0) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
            else cst = Math.min(rc.getTeamSoup(), 1);
            if (cst > 0) {
                rc.submitTransaction(message, cst);
            }
        }
        //we check our surroundings to see if the wall has been built and send the message to the helpers
        if(!wallBuilt) {
            int minHeight = Integer.MAX_VALUE;
            for (Direction dir : directions) if (valid(rc.getLocation().add(dir))) {
                //check the height of every valid surrounding location
                if (rc.canSenseLocation(rc.getLocation().add(dir))) {
                    minHeight = Math.min(minHeight, rc.senseElevation(rc.getLocation().add(dir)));
                }
            }
            if(minHeight >= 8){
                wallBuilt = true;
                int[] message = new int[7];
                message[5] = sec1; //for security
                message[6] = sec2; //for security
                message[0] = 101; //means the wall is finished
                message[1] = 2743 + sec3; //the x
                message[2] = 5743 + sec4; //the y
                message[3] = 0;
                message[4] = 0; //don't need for this kind of message
                int cst;
                if (averageEnemyMessageCost != 0) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                else cst = Math.min(rc.getTeamSoup(), 1);
                if (cst > 0) {
                    rc.submitTransaction(message, cst);
                }
            }
        }
        else {
            if (turnCount % 400 == 1) {
                int[] message = new int[7];
                message[5] = sec1; //for security
                message[6] = sec2; //for security
                message[0] = 101; //type
                message[1] = 2743 + sec3; //the x
                message[2] = 5743 + sec4; //the y
                message[3] = 0;
                message[4] = 0; //don't need for this kind of message
                int cst;
                if (averageEnemyMessageCost != 0) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                else cst = Math.min(rc.getTeamSoup(), 1);
                if (cst > 0) {
                    rc.submitTransaction(message, cst);
                }
            }
        }
        RobotInfo[] RI = rc.senseNearbyRobots();
        for (RobotInfo ri : RI) {
            if (ri.team == rc.getTeam().opponent() && ri.type == RobotType.DELIVERY_DRONE && rc.canShootUnit(ri.ID) && rc.isReady()) {
                rc.shootUnit(ri.ID); //shoot 'em all
                break;
            }
        }
    }
}
