package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class Landscaper extends RobotPlayer{
	static void runLandscaper() throws GameActionException {
        if (turnCount == 1) {
            int number = rc.getRoundNum();
            for (int i = 2; i <= 700; i++) {
                if (number <= i) break;
                Transaction[] tr = rc.getBlock(number - i);
                for (Transaction t : tr) {
                    int[] message = t.getMessage();
                    if (message[5] == sec1 && message[6] == sec2) {
                        if(message[0] == 20) if(!taken[message[1] - sec3][message[2] - sec4]){
                            haveRequest = true;
                            miners.add(new MapLocation(message[1] - sec3, message[2] - sec4));
                            //dest = new MapLocation(message[1] - sec3, message[2] - sec4);
                        }
                        if(message[0] == 23) if(!taken[message[1] - sec3][message[2] - sec4]){
                            haveRequest = true;
                            miners.add(new MapLocation(message[1] - sec3, message[2] - sec4));
                            //dest = new MapLocation(message[1] - sec3, message[2] - sec4);
                        }
                        if (message[0] == 2) {
                            HQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                            knowHQ = true;
                            for (Direction dir : directions) {
                                MapLocation nLoc = HQLoc.add(dir);
                                System.out.println(dir);
                                if(valid(nLoc)){
                                    bad[nLoc.x][nLoc.y] = true; //don't build anything on here, since we will build the wall here
                                    for(Direction dir1 : directions){
                                        MapLocation nLoc1 = nLoc.add(dir1);
                                        if(valid(nLoc1)){
                                            alsobad[nLoc1.x][nLoc1.y] = true;
                                            gd[nLoc1.x][nLoc1.y] = true;
                                        }
                                    }
                                }
                                MapLocation new1 = HQLoc.add(Direction.NORTH).add(Direction.NORTH);
                                if(valid(new1)){
                                    posLoc.add(new1);
                                    gd[new1.x][new1.y] = false;
                                }
                                new1 = HQLoc.add(Direction.EAST).add(Direction.EAST);
                                if(valid(new1)){
                                    posLoc.add(new1);
                                    gd[new1.x][new1.y] = false;
                                }
                                new1 = HQLoc.add(Direction.WEST).add(Direction.WEST);
                                if(valid(new1)){
                                    posLoc.add(new1);
                                    gd[new1.x][new1.y] = false;
                                }
                                new1 = HQLoc.add(Direction.SOUTH).add(Direction.SOUTH);
                                if(valid(new1)){
                                    posLoc.add(new1);
                                    gd[new1.x][new1.y] = false;
                                }
                            }
                        }
                        else if(message[0] == 17){
                            willFlood = message[1] - sec3;
                        }
                        else if(message[0] == 21){
                            int one = message[1] - sec3;
                            int two = message[2] - sec4;
                            if(!taken[one][two]){
                                taken[one][two] = true;
                                MapLocation mpl = new MapLocation(one, two);
                                miners.remove(mpl);
                            }
                            if(miners.size() == 0) haveRequest = false;
                        }
                        else if(message[0] == 15){ //the location of their HQ
                            theirHQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                            knowTheirHQ = true;
                        }
                        else if(message[0] == 101){
                            wallBuilt = true;
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
                    if(message[0] == 20) if(!taken[message[1] - sec3][message[2] - sec4]){
                        haveRequest = true;
                        miners.add(new MapLocation(message[1] - sec3, message[2] - sec4));
                        //dest = new MapLocation(message[1] - sec3, message[2] - sec4);
                    }
                    if(message[0] == 23) if(!taken[message[1] - sec3][message[2] - sec4]){
                        haveRequest = true;
                        miners.add(new MapLocation(message[1] - sec3, message[2] - sec4));
                        //dest = new MapLocation(message[1] - sec3, message[2] - sec4);
                    }
                    if (message[0] == 2) {
                        HQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                        knowHQ = true;
                        for (Direction dir : directions) {
                            MapLocation nLoc = HQLoc.add(dir);
                            System.out.println(dir);
                            if(valid(nLoc)){
                                bad[nLoc.x][nLoc.y] = true; //don't build anything on here, since we will build the wall here
                                for(Direction dir1 : directions){
                                    MapLocation nLoc1 = nLoc.add(dir1);
                                    if(valid(nLoc1)){
                                        alsobad[nLoc1.x][nLoc1.y] = true;
                                        gd[nLoc1.x][nLoc1.y] = true;
                                    }
                                }
                            }
                            MapLocation new1 = HQLoc.add(Direction.NORTH).add(Direction.NORTH);
                            if(valid(new1)){
                                posLoc.add(new1);
                                gd[new1.x][new1.y] = false;
                            }
                            new1 = HQLoc.add(Direction.EAST).add(Direction.EAST);
                            if(valid(new1)){
                                posLoc.add(new1);
                                gd[new1.x][new1.y] = false;
                            }
                            new1 = HQLoc.add(Direction.WEST).add(Direction.WEST);
                            if(valid(new1)){
                                posLoc.add(new1);
                                gd[new1.x][new1.y] = false;
                            }
                            new1 = HQLoc.add(Direction.SOUTH).add(Direction.SOUTH);
                            if(valid(new1)){
                                posLoc.add(new1);
                                gd[new1.x][new1.y] = false;
                            }
                        }
                    }
                    else if(message[0] == 17){
                        willFlood = message[1] - sec3;
                    }
                    else if(message[0] == 21){
                        int one = message[1] - sec3;
                        int two = message[2] - sec4;
                        if(!taken[one][two]){
                            taken[one][two] = true;
                            MapLocation mpl = new MapLocation(one, two);
                            miners.remove(mpl);
                        }
                        if(miners.size() == 0) haveRequest = false;
                    }
                    else if(message[0] == 15){ //the location of their HQ
                        theirHQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                        knowTheirHQ = true;
                    }
                    else if(message[0] == 101) wallBuilt = true;
                } else {
                    numMess++;
                    averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                }
            }
        }
        System.out.println(status + cnt);
        vs[rc.getLocation().x][rc.getLocation().y] = rc.getRoundNum();
        if(status == "emergency digging"){
            if(cnt % 2 == 0) rc.digDirt(rc.getLocation().directionTo(HQLoc));
            else rc.depositDirt(Direction.CENTER);
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()) {
                if(robots[i].type == RobotType.HQ && robots[i].dirtCarrying == 0){
                    status = "building the wall";
                    cnt = 0;
                }
            }
            cnt++;
        }
        if(status == "going to miner"){
            if(rc.canSenseLocation(rc.getLocation()) & rc.senseElevation(rc.getLocation()) >= 25){
                status = "pick me up";
                cnt = 0;
            }
            if(rc.getLocation().isAdjacentTo(dest) && rc.isReady()){
                boolean[][] occupb = new boolean[width + 5][height + 5];
                boolean[][] occup = new boolean[width + 5][height + 5];
                RobotInfo[] robots = rc.senseNearbyRobots();
                for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()) {
                    if(robots[i].type == RobotType.NET_GUN || robots[i].type == RobotType.VAPORATOR || robots[i].type == RobotType.FULFILLMENT_CENTER) occupb[robots[i].location.x][robots[i].location.y] = true;
                    occup[robots[i].location.x][robots[i].location.y] = true;
                }
                if(rc.getDirtCarrying() == 0) mining = true;
                if(mining){
                    for(Direction dir : directions) if(valid(rc.getLocation().add(dir))){
                        if(rc.isReady() && rc.canDigDirt(dir) && !rc.getLocation().add(dir).isAdjacentTo(dest) && !occup[rc.getLocation().add(dir).x][rc.getLocation().add(dir).y]) rc.digDirt(dir);
                    }
                    if(rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) mining = false;
                }
                if(rc.getRoundNum() % 2 == 0){
                    navigate(dest);
                    cnt++;
                }
                else {
                    //put dirt uniformly
                    Direction put = Direction.CENTER;
                    int best = Integer.MAX_VALUE;
                    for (Direction dir : directions1) {
                        MapLocation ml = rc.getLocation().add(dir);
                        if (valid(ml) && rc.canSenseLocation(ml) && ml.isAdjacentTo(dest) && !occupb[rc.getLocation().add(dir).x][rc.getLocation().add(dir).y]) {
                            int cur = rc.senseElevation(ml);
                            if (cur < best) {
                                best = cur;
                                put = dir;
                            }
                        }
                    }
                    if (rc.isReady() && rc.senseElevation(rc.getLocation().add(put)) < 25) rc.depositDirt(put);
                }
            }
            else{
                navigate(dest);
                cnt++;
            }
        }
        if (status == "going to HQ" && knowHQ) {
            goal = HQLoc;
            if (adjacent) {
                status = "mining dirt";
                cnt = 0;
            } else {
                for (Direction dir : directions){
                    MapLocation ml = rc.getLocation().add(dir);
                    if (valid(ml) && ml.x == HQLoc.x && ml.y == HQLoc.y) {
                        adjacent = true;
                    }
                }
                if (!adjacent) {
                    navigate(goal);
                    cnt++;
                }
            }
            if(wallBuilt){
                if(haveRequest){
                    for(MapLocation ml : miners) if(!taken[ml.x][ml.y]){
                        System.out.println("weird");
                        status = "going to miner";
                        dest = ml;
                        cnt = 0;
                        //notify everyone type 21
                        int[] message = new int[7];
                        message[5] = sec1; //for security
                        message[6] = sec2; //for security
                        message[0] = 21; //type
                        message[1] = ml.x + sec3;
                        message[2] = ml.y + sec4;
                        message[3] = 234554654;
                        message[4] = 872464;
                        int cst;
                        if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                        else cst = Math.min(rc.getTeamSoup(), 1);
                        if (cst > 0) rc.submitTransaction(message, cst);
                        break;
                    }
                }
                else{
                    status = "helping build";
                    dontKnow = true;
                    cnt = 0;
                }
            }
            if(rc.getRoundNum() >= (willFlood - 20)){
                status = "helping build";
                cnt = 0;
            }
            if(cnt > 80){
                System.out.println("cnt: " + cnt);
                System.out.println("Have? " + haveRequest);
                if(haveRequest){
                    for(MapLocation ml : miners) if(!taken[ml.x][ml.y]){
                        System.out.println("weird");
                        status = "going to miner";
                        dest = ml;
                        cnt = 0;
                        //notify everyone type 21
                        int[] message = new int[7];
                        message[5] = sec1; //for security
                        message[6] = sec2; //for security
                        message[0] = 21; //type
                        message[1] = ml.x + sec3;
                        message[2] = ml.y + sec4;
                        message[3] = 234554654;
                        message[4] = 872464;
                        int cst;
                        if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                        else cst = Math.min(rc.getTeamSoup(), 1);
                        if (cst > 0) rc.submitTransaction(message, cst);
                        break;
                    }
                }
                else{
                    status = "helping build";
                    dontKnow = true;
                    cnt = 0;
                }
            }
        } else if (status == "mining dirt") {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()) {
                if(robots[i].type == RobotType.HQ && robots[i].dirtCarrying > 0){
                    //emergency mode!
                    status = "emergency digging";
                    cnt = 0;
                }
            }
            if (rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) {
                status = "building the wall";
                cnt = 0;
            } else {
                if(dontKnow) {
                    for(MapLocation ml : posLoc){
                        Direction dir = rc.getLocation().directionTo(ml);
                        if(rc.isReady() && rc.canDigDirt(dir) && rc.getLocation().isAdjacentTo(ml)){
                            digdir = dir;
                            dontKnow = false;
                            break;
                        }
                    }
                }
                else if(rc.isReady()) rc.digDirt(digdir);
            }
        } else if (status == "building the wall") {
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()) {
                if(robots[i].type == RobotType.HQ && robots[i].dirtCarrying > 0){
                    //emergency mode!
                    status = "emergency digging";
                    cnt = 0;
                }
            }
            if(rc.canSenseLocation(rc.getLocation()) && rc.senseElevation(rc.getLocation()) <= 500 && rc.getRoundNum() <= (willFlood - 100)){
                if(rc.canDepositDirt(Direction.CENTER) && rc.isReady()) rc.depositDirt(Direction.CENTER);
            }
            else {
                Direction put = Direction.CENTER;
                int best = Integer.MAX_VALUE;
                for (Direction dir : directions1) {
                    MapLocation ml = rc.getLocation().add(dir);
                    if (valid(ml) && rc.canSenseLocation(ml) && bad[ml.x][ml.y]) {
                        int cur = rc.senseElevation(ml);
                        if(cur < best){
                            best = cur;
                            put = dir;
                        }
                    }
                }
                if(rc.isReady()) rc.depositDirt(put);
            }
            if (rc.getDirtCarrying() == 0) status = "mining dirt"; //so they will resupply right from the wall
        }
        else if(status == "helping build"){
            if(rc.getRoundNum() > 2140){
                status = "pick me up1";
                cnt = 0;
            }
           //we station ourselves near the wall and put dirt on it
            if(!gd[rc.getLocation().x][rc.getLocation().y]){
                if(cnt > 60 && haveRequest){
                    for(MapLocation ml : miners) if(!taken[ml.x][ml.y]){
                        System.out.println("weird");
                        status = "going to miner";
                        dest = ml;
                        cnt = 0;
                        //notify everyone type 21
                        int[] message = new int[7];
                        message[5] = sec1; //for security
                        message[6] = sec2; //for security
                        message[0] = 21; //type
                        message[1] = ml.x + sec3;
                        message[2] = ml.y + sec4;
                        message[3] = 234554654;
                        message[4] = 872464;
                        int cst;
                        if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                        else cst = Math.min(rc.getTeamSoup(), 1);
                        if (cst > 0) rc.submitTransaction(message, cst);
                        break;
                    }
                }
                else navigate(HQLoc);
            }
            else{
                //either put dirt or dig it, right?
                if(rc.getDirtCarrying() > 0){
                    if(rc.canSenseLocation(rc.getLocation()) && rc.senseElevation(rc.getLocation()) <= 90){
                        if(rc.canDepositDirt(Direction.CENTER) && rc.isReady()) rc.depositDirt(Direction.CENTER);
                    }
                    else {
                        Direction put = Direction.CENTER;
                        int best = Integer.MAX_VALUE;
                        for (Direction dir : directions1) {
                            MapLocation ml = rc.getLocation().add(dir);
                            if (valid(ml) && rc.canSenseLocation(ml) && bad[ml.x][ml.y]) {
                                int cur = rc.senseElevation(ml);
                                if(cur < best){
                                    best = cur;
                                    put = dir;
                                }
                            }
                        }
                        if(rc.isReady()) rc.depositDirt(put);
                    }
                }
                else{
                    boolean[][] occup = new boolean[width + 5][height + 5];
                    RobotInfo[] robots = rc.senseNearbyRobots();
                    for(int i = 0; i < robots.length; i++) if(robots[i].team == rc.getTeam()) {
                        occup[robots[i].location.x][robots[i].location.y] = true;
                    }
                    for (Direction dir : directions) {
                        if (valid(rc.getLocation().add(dir))) {
                            MapLocation ml = rc.getLocation().add(dir);
                            if (!bad[ml.x][ml.y] && (!(ml.x == HQLoc.x && ml.y == HQLoc.y)) && !gd[ml.x][ml.y] && !occup[ml.x][ml.y]) {
                                if (rc.canDigDirt(dir) && rc.isReady()) {
                                    rc.digDirt(dir);
                                    digdir = dir;
                                }
                            }
                        }
                    }
                }
            }
            if(status == "helping build") cnt++;
        }
        else if(status == "pick me up"){
            if(cnt == 0){
                //tell everyone that you are ready to be picked up
                //message of type 27 with location
                int[] message = new int[7];
                message[5] = sec1; //for security
                message[6] = sec2; //for security
                message[0] = 27; //type
                message[1] = rc.getLocation().x + sec3;
                message[2] = rc.getLocation().y + sec4;
                message[3] = 234554654;
                message[4] = 872464;
                int cst;
                if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                else cst = Math.min(rc.getTeamSoup(), 1);
                if (cst > 0) rc.submitTransaction(message, cst);
            }
            if(!knowTheirHQ){
                int number = rc.getRoundNum();
                for (int i = 1; i <= 600; i++) {
                    if (number <= i) break;
                    Transaction[] tr = rc.getBlock(number - i);
                    for (Transaction t : tr) {
                        int[] message = t.getMessage();
                        if (message[5] == sec1 && message[6] == sec2) {
                            if(message[0] == 15){ //the location of their HQ
                                theirHQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                                knowTheirHQ = true;
                            }
                        } else {
                            numMess++;
                            averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                        }
                    }
                }
            }
            if(knowTheirHQ && rc.getLocation().isAdjacentTo(theirHQLoc) && rc.isReady()){
                if(rc.getDirtCarrying() == 0){
                    digging = true;
                }
                if(digging){
                    for(Direction dir : directions) if(valid(rc.getLocation().add(dir))){
                        MapLocation digg = rc.getLocation().add(dir);
                        if(rc.canDigDirt(dir) && rc.isReady() && (!((digg.x == theirHQLoc.x) && (digg.y == theirHQLoc.y)))) rc.digDirt(dir);
                    }
                    if(rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) digging = false;
                }
                else{
                    Direction dir1 = rc.getLocation().directionTo(theirHQLoc);
                    if(rc.canDepositDirt(dir1)) rc.depositDirt(dir1);
                }
            }
            cnt++;
        }
        else if(status == "pick me up1"){
            if(!knowTheirHQ){
                int number = rc.getRoundNum();
                for (int i = 1; i <= 600; i++) {
                    if (number <= i) break;
                    Transaction[] tr = rc.getBlock(number - i);
                    for (Transaction t : tr) {
                        int[] message = t.getMessage();
                        if (message[5] == sec1 && message[6] == sec2) {
                            if(message[0] == 15){ //the location of their HQ
                                theirHQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                                knowTheirHQ = true;
                            }
                        } else {
                            numMess++;
                            averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                        }
                    }
                }
            }
            if(knowTheirHQ && rc.getLocation().isAdjacentTo(theirHQLoc) && rc.isReady()){
                if(rc.getDirtCarrying() == 0){
                    digging = true;
                }
                if(digging){
                    for(Direction dir : directions) if(valid(rc.getLocation().add(dir))){
                        MapLocation digg = rc.getLocation().add(dir);
                        if(rc.canDigDirt(dir) && rc.isReady() && (!((digg.x == theirHQLoc.x) && (digg.y == theirHQLoc.y)))) rc.digDirt(dir);
                    }
                    if(rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) digging = false;
                }
                else{
                    Direction dir1 = rc.getLocation().directionTo(theirHQLoc);
                    if(rc.canDepositDirt(dir1)) rc.depositDirt(dir1);
                }
            }
            cnt++;
        }
    }
}
