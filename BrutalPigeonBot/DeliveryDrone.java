package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class DeliveryDrone extends RobotPlayer{
	static void runDeliveryDrone() throws GameActionException {
        if (turnCount == 1) {
            int number = rc.getRoundNum();
            for (int i = 2; i <= 700; i++) {
                if (number <= i) break;
                Transaction[] tr = rc.getBlock(number - i);
                for (Transaction t : tr) {
                    int[] message = t.getMessage();
                    if (message[5] == sec1 && message[6] == sec2) {
                        if (message[0] == 2) {
                            HQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                            knowHQ = true;
                            for (Direction dir : directions){
                                MapLocation nLoc = HQLoc.add(dir);
                                if(valid(nLoc)) bad[nLoc.x][nLoc.y] = true;
                            }
                        }
                        else if(message[0] == 15){ //the location of their HQ
                            theirHQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                            knowTheirHQ = true;
                        }
                        else if(message[0] == 30) taken[message[1] - sec3][message[2] - sec4] = true;
                        else if(message[0] == 27){
                            haveRequest = true;
                            land.add(new MapLocation(message[1] - sec3, message[2] - sec4));
                        }
                    } else {
                        numMess++;
                        averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                    }
                }
            }
        }
        for (int i = 1; i <= 1; i++) {
            if (rc.getRoundNum() <= i) break;
            Transaction[] tr = rc.getBlock(rc.getRoundNum() - i);
            for (Transaction t : tr) {
                int[] message = t.getMessage();
                if (message[5] == sec1 && message[6] == sec2) {
                    if (message[0] == 2) {
                        HQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                        knowHQ = true;
                        for (Direction dir : directions) {
                            MapLocation nLoc = HQLoc.add(dir);
                            if(valid(nLoc)) bad[nLoc.x][nLoc.y] = true;
                        }
                    }
                    else if(message[0] == 15){ //the location of their HQ
                        theirHQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                        knowTheirHQ = true;
                    }
                    else if(message[0] == 16){
                        lastWait = rc.getRoundNum();
                    }
                    else if(message[0] == 30) taken[message[1] - sec3][message[2] - sec4] = true;
                    else if(message[0] == 27){
                        haveRequest = true;
                        land.add(new MapLocation(message[1] - sec3, message[2] - sec4));
                    }
                } else {
                    numMess++;
                    averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                }
            }
        }
        System.out.println(DroneStatus);
        //System.out.println(knowHQ);
        if(wait && (rc.getRoundNum() - lastWait) > 20){
            wait = false; //attack!
        }
        if(DroneStatus == "going to land"){
            if(rc.getLocation().isAdjacentTo(dest) && rc.isReady()){
                //pick that dude up
                RobotInfo[] robots = rc.senseNearbyRobots();
                for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()) {
                    if (robots[i].type == RobotType.LANDSCAPER && robots[i].location.x == dest.x && robots[i].location.y == dest.y) {
                       if(rc.canPickUpUnit(robots[i].ID) && rc.isReady()){
                           rc.pickUpUnit(robots[i].ID);
                           holding = true;
                           cnt = 0;
                           DroneStatus = "going to HQ";
                       }
                    }
                }
            }
            DroneNav(dest);
            if(cnt > 80){
                DroneStatus = "going to HQ";
                cnt = 0;
            }
            cnt++;
        }
        if(DroneStatus == "going to HQ" && knowHQ){
            if(haveRequest){
                for(MapLocation ml : land) if(!taken[ml.x][ml.y]){
                    //tell others about it
                    int[] message = new int[7];
                    message[5] = sec1; //for security
                    message[6] = sec2; //for security
                    message[0] = 30; //type
                    message[1] = ml.x + sec3;
                    message[2] = ml.y + sec4;
                    message[3] = 234554654;
                    message[4] = 872464;
                    int cst;
                    if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                    else cst = Math.min(rc.getTeamSoup(), 1);
                    if (cst > 0) rc.submitTransaction(message, cst);
                    DroneStatus = "going to land";
                    dest = ml;
                    cnt = 0;
                }
            }
            DroneNav(HQLoc);
            System.out.println(rc.getRoundNum());
            if(rc.getRoundNum() > 2150 && !holding){ //get the surrounding guys
                RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED);
                for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()){
                    if(robots[i].type == RobotType.LANDSCAPER && !bad[robots[i].location.x][robots[i].location.y]){
                        if(rc.canPickUpUnit(robots[i].ID)){
                            rc.pickUpUnit(robots[i].ID);
                            holding = true;
                            break;
                        }
                    }
                }
            }
            if(!holding && rc.getRoundNum() >= 2220){
                System.out.println(knowTheirHQ);
                if(!knowTheirHQ) {
                    DroneStatus = "searching for the enemy HQ";
                    stage = 0;
                    goal = new MapLocation(HQLoc.x, height - HQLoc.y - 1);
                    cnt = 0;
                }
                else{
                    DroneStatus = "going to the enemy HQ"; //we already know where to go
                    cnt = 0;
                }
            }
            if(holding && rc.getRoundNum() >= 2230){
                System.out.println(knowTheirHQ);
                if(!knowTheirHQ) {
                    DroneStatus = "searching for the enemy HQ";
                    stage = 0;
                    goal = new MapLocation(HQLoc.x, height - HQLoc.y - 1);
                    cnt = 0;
                }
                else{
                    DroneStatus = "going to the enemy HQ"; //we already know where to go
                    cnt = 0;
                }
            }
            if(DroneStatus == "going to HQ") cnt++;
        }
        else if(DroneStatus == "searching for the enemy HQ"){
            System.out.println(stage);
            if(knowTheirHQ){
                DroneStatus = "going to the enemy HQ";
                cnt = 0;
            }
            if(stage == 0){
                if(rc.getLocation().distanceSquaredTo(goal) <= 8){
                    stage = 1;
                    goal = new MapLocation(width - HQLoc.x - 1, height - HQLoc.y - 1);
                    cnt = 0;
                }
            }
            else if(stage == 1){
                if(rc.getLocation().distanceSquaredTo(goal) <= 8){
                    stage = 2;
                    goal = new MapLocation(width - HQLoc.x - 1, HQLoc.y);
                    cnt = 0;
                }
            }
            DroneNav(goal);
            Team enemy = rc.getTeam().opponent();
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam().opponent()){
                if(robots[i].type == RobotType.HQ){
                    int[] message = new int[7];
                    message[5] = sec1; //for security
                    message[6] = sec2; //for security
                    message[0] = 15; //type
                    message[1] = robots[i].location.x + sec3;
                    message[2] = robots[i].location.y + sec4;
                    message[3] = 234554654;
                    message[4] = 872464;
                    int cst;
                    if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                    else cst = Math.min(rc.getTeamSoup(), 1);
                    if (cst > 0) {
                        rc.submitTransaction(message, cst);
                    }
                }
            }
            if(DroneStatus == "searching for the enemy HQ") cnt++;
        }
        else if(DroneStatus == "going to the enemy HQ" && knowTheirHQ){
            if(rc.getLocation().distanceSquaredTo(theirHQLoc) <= 40 && !flg){
                wait = true;
                int[] message = new int[7];
                message[5] = sec1; //for security
                message[6] = sec2; //for security
                message[0] = 16; //we are waiting
                message[1] = 345324 + sec3;
                message[2] = 742490 + sec4;
                message[3] = 234554654;
                message[4] = 872464;
                int cst;
                if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                else cst = Math.min(rc.getTeamSoup(), 1);
                if (cst > 0) rc.submitTransaction(message, cst);
                flg = true;
            }
            if(!wait){
                DroneNav(theirHQLoc);
                if (rc.getLocation().distanceSquaredTo(theirHQLoc) <= 8) {
                    if(!holding) {
                        DroneStatus = "picking up their landscapers";
                        cnt = 0;
                    }
                    else{
                        DroneStatus = "putting on wall";
                        cnt = 0;
                    }
                }
                if (DroneStatus == "going to the enemy HQ") cnt++;
            }
        }
        else if(DroneStatus == "putting on wall"){
            if(rc.isReady()) {
                for (Direction dir : directions)
                    if (valid(rc.getLocation().add(dir))) {
                        MapLocation ml = rc.getLocation().add(dir);
                        if (ml.isAdjacentTo(theirHQLoc)) {
                            if (rc.isReady() && rc.canDropUnit(dir)){
                                rc.dropUnit(dir);
                                holding = false;
                                DroneStatus = "picking up their landscapers";
                                cnt = 0;
                            }
                        }
                    }
                DroneNav(theirHQLoc);
            }
        }
        else if(DroneStatus == "picking up their landscapers"){
            Team enemy = rc.getTeam().opponent();
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);
            for(int i = 0; i < robots.length; i++){
                if(robots[i].type == RobotType.LANDSCAPER){
                    rc.pickUpUnit(robots[i].getID());
                    DroneStatus = "dropping them off";
                    goal = HQLoc;
                    cnt = 0;
                    break;
                }
            }
            if(DroneStatus == "picking up their landscapers") cnt++;
            if(cnt > 10){
                DroneStatus = "going to HQ";
                cnt = 0;
            }
        }
        else if(DroneStatus == "dropping them off"){
            for(Direction dir : directions){
                MapLocation ml = rc.getLocation().add(dir);
                if(valid(ml) && rc.canSenseLocation(ml) && rc.senseFlooding(ml) && rc.isReady()){
                    rc.dropUnit(dir);
                    cnt = 0;
                    DroneStatus = "going to the enemy HQ";
                    flg = false;
                }
            }
            if(DroneStatus == "dropping them off"){
                cnt++;
                Direction next1 = rc.getLocation().directionTo(goal);
                if(next1 == Direction.CENTER){
                    if(goal == HQLoc && knowTheirHQ) goal = theirHQLoc;
                    else if(goal == theirHQLoc && knowHQ) goal = HQLoc;
                }
                DroneNav(goal);
            }
        }
    }
}
