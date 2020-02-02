package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class Miner extends RobotPlayer{
	static void runMiner() throws GameActionException {
        System.out.println("DAs: " + countDA);
        System.out.println("I am " + occupation);
        vs[rc.getLocation().x][rc.getLocation().y] = rc.getRoundNum();
        if (rc.getRoundNum() >= 400 && bad[rc.getLocation().x][rc.getLocation().y]) { //at this point, just get out of the way
            Direction next1 = rc.getLocation().directionTo(HQLoc).opposite();
            if(!tryMove(next1)){
                Direction l = next1.rotateLeft(), r = next1.rotateRight();
                for(int i = 0; i < 5; i++){
                    MapLocation ml = rc.getLocation().add(l);
                    if(valid(ml)) if(((rc.getRoundNum() - vs[ml.x][ml.y]) > 7 || (vs[ml.x][ml.y] == 0)) && tryMove(l)) break;
                    ml = rc.getLocation().add(r);
                    if(valid(ml)) if(((rc.getRoundNum() - vs[ml.x][ml.y]) > 7 || (vs[ml.x][ml.y] == 0)) && tryMove(r)) break;
                    l = l.rotateLeft();
                    r = r.rotateRight();
                }
            }
        }
        if (turnCount == 1) HQLoc = new MapLocation(-1, -1); //not the real location
        if (occupation == "none") {
            if (cornersExplored < 4 && rc.getRoundNum() > 150 && rc.getRoundNum() < 200) { //only start exploring around the beginning of the game
                occupation = "exploring";
                int ch = 0;
                if (!explored[0]){
                    goal = new MapLocation(5, height - 5); //upper left
                    ch = 0;
                }
                else if (!explored[1]){
                    goal = new MapLocation(5, 5); //lower left
                    ch = 1;
                }
                else if (!explored[2]){
                    goal = new MapLocation(width - 5, height - 5); //upper right
                    ch = 2;
                }
                else{
                    goal = new MapLocation(width - 5, 0); //lower left
                    ch = 3;
                }
                cnt = 0;
                int[] message = new int[7];
                message[5] = sec1; //for security
                message[6] = sec2; //for security
                message[0] = 6; //type, automatically means we explored the next corner
                message[3] = 468265245;
                message[1] = ch + sec3;
                message[2] = 175642849; //don't try to reverse lol
                message[4] = 342355342;
                int cst;
                if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                else cst = Math.min(rc.getTeamSoup(), 1);
                if (cst > 0) {
                    rc.submitTransaction(message, cst);
                }
            } else if (countDA < 2 && rc.getRoundNum() > (10 + (Math.random() * 100)) && rc.getTeamSoup() > 200) { //if we already have some soup, build academies
                occupation = "building a DA";
                cnt = 0;
            }
            else if(numVap < 3 && rc.getRoundNum() > (350 + (Math.random() * 100) * 3)){
               occupation = "becoming free vap";
               cnt = 0;
            }
            else {
                occupation = "need to find soup";
                cnt = 0;
            }
        }
        else if(occupation == "becoming free vap"){
            boolean free = true;
            for(Direction dir : directions){
                if(!valid(rc.getLocation().add(dir))){
                    free = false;
                    break;
                }
                if(!rc.canMove(dir)){
                    free = false;
                    break;
                }
            }
            if(numVap < 2) {
                if (free && rc.getLocation().distanceSquaredTo(HQLoc) > 50) {
                    occupation = "vaporator builder";
                    int[] message = new int[7];
                    message[5] = sec1; //for security
                    message[6] = sec2; //for security
                    message[0] = 20;
                    message[3] = 468265245;
                    message[1] = rc.getLocation().x + sec3;
                    message[2] = rc.getLocation().y + sec4; //don't try to reverse lol
                    message[4] = 342355342;
                    int cst;
                    if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                    else cst = Math.min(rc.getTeamSoup(), 1);
                    if (cst > 0) {
                        rc.submitTransaction(message, cst);
                    }
                } else {
                    //move way from HQ
                    Direction next1 = rc.getLocation().directionTo(HQLoc).opposite();
                    if (!tryMove(next1)) {
                        Direction l = next1.rotateLeft(), r = next1.rotateRight();
                        for (int i = 0; i < 5; i++) {
                            MapLocation ml = rc.getLocation().add(l);
                            if (valid(ml))
                                if (((rc.getRoundNum() - vs[ml.x][ml.y]) > 7 || (vs[ml.x][ml.y] == 0)) && tryMove(l))
                                    break;
                            ml = rc.getLocation().add(r);
                            if (valid(ml))
                                if (((rc.getRoundNum() - vs[ml.x][ml.y]) > 7 || (vs[ml.x][ml.y] == 0)) && tryMove(r))
                                    break;
                            l = l.rotateLeft();
                            r = r.rotateRight();
                        }
                    }
                }
            }
            else{
                occupation = "none";
                cnt = 0;
            }
        }
        else if(occupation == "vaporator builder"){
            System.out.println(waiting);
            if(waiting > 100000) {
                for (Direction dir : directions) {
                    if (rc.canSenseLocation(rc.getLocation().add(dir)) && rc.senseElevation(rc.getLocation().add(dir)) >= 25)
                        waiting = 7;
                }
            }
            if(waiting == 0){
                for(Direction dir : directions) if(rc.isReady() && valid(rc.getLocation().add(dir)) && rc.canSenseLocation(rc.getLocation().add(dir)) && rc.senseElevation(rc.getLocation().add(dir)) >= 23) {
                    MapLocation new1 = rc.getLocation().add(dir);
                    if((new1.x + new1.y) % 2 == 0) tryBuild(RobotType.VAPORATOR, dir);
                    else if(rc.getTeamSoup() > 600) tryBuild(RobotType.FULFILLMENT_CENTER, dir);
                }
            }
            else waiting--;
        }
        else if (occupation == "going to soup") { //DONE
            if (goal.x == rc.getLocation().x && goal.y == rc.getLocation().y || (rc.canSenseLocation(rc.getLocation()) && rc.senseSoup(rc.getLocation()) > 0)) {
                soupdir = Direction.CENTER;
                occupation = "mining soup";
                cnt = 0;
            }
            if (occupation == "going to soup") {
                for (Direction dir : directions) {
                    MapLocation ml = rc.getLocation().add(dir);
                    if (valid(ml)) {
                        if (rc.canMineSoup(dir)) {
                            soupdir = dir;
                            occupation = "mining soup";
                            cnt = 0;
                            break;
                        }
                    }
                }
            }
            if (occupation != "mining soup") {
                navigate(goal);
                cnt++;
            }
            if (cnt == 40){
                occupation = "none"; //if no soup was found, it is probably unreachable
                nosoup[goal.x][goal.y] = true;
            }
        } else if (occupation == "refining") {
            boolean f = false;
            for (Direction dir : directions) {
                if (valid(rc.getLocation().add(dir))) {
                    tryRefine(dir);
                    f = true;
                    break;
                }
            }
            if (!f || rc.getSoupCarrying() == 0) {
                occupation = "none";
                cnt = 0;
            }
        } else if (occupation == "going to refinery") { //DONE!!!
            if (rc.getLocation().x == goal.x && rc.getLocation().y == goal.y) {
                occupation = "refining";
                cnt = 0;
            }
            for (Direction dir : directions)
                if (valid(rc.getLocation().add(dir))) {
                    if (tryRefine(dir)) {
                        occupation = "refining";
                        cnt = 0;
                        break;
                    }
                }
            if (occupation != "refining") {
                navigate(goal);
                cnt++;
            }
            if (cnt == 40) occupation = "none"; //if no soup was found, it is probably unreachable
            if (occupation != "refining") cnt++;
        } else if (occupation == "mining soup") {
            MapLocation souploc = rc.getLocation().add(soupdir);
            boolean cn = tryMine(soupdir);
            if(rc.canSenseLocation(souploc) && rc.senseSoup(souploc) == 0 && rc.getSoupCarrying() != RobotType.MINER.soupLimit){
                occupation = "need to find soup";
                cnt = 0;
            }
            else if (!cn || (rc.canSenseLocation(souploc) && rc.senseSoup(souploc) == 0) || (rc.getSoupCarrying() == RobotType.MINER.soupLimit)) {
                occupation = "finding a refinery";
                cnt = 0;
            }
            if (rc.canSenseLocation(souploc) && rc.senseSoup(souploc) == 0) {
                int ind = -1;
                int numb = 0;
                for (MapLocation ml : soups) {
                    if (ml.x == souploc.x && ml.y == souploc.y) {
                        ind = numb;
                        break;
                    }
                    numb++;
                }
                if (ind != -1) {
                    soups.remove(ind); //so that we don't try to go there again, send a message!
                    hasSoup[souploc.x][souploc.y] = 3; //no more soup here
                    int[] message = new int[7];
                    message[5] = sec1; //for security
                    message[6] = sec2; //for security
                    message[0] = 8; //no more soup here
                    message[3] = 468265245;
                    message[1] = souploc.x + sec3;
                    message[2] = souploc.y + sec4; //don't try to reverse lol
                    message[4] = 342355342;
                    int cst;
                    if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                    else cst = Math.min(rc.getTeamSoup(), 1);
                    if (cst > 0) {
                        rc.submitTransaction(message, cst);
                    }
                }
            }
        } else if (occupation == "finding a refinery") {
            int minDist = Integer.MAX_VALUE;
            MapLocation best = new MapLocation(-1, -1);
            System.out.println("HOW MANY? " + refineries.size());
            System.out.println("know hq?" + knowHQ);
            for (MapLocation ml : refineries) {
                int dist = rc.getLocation().distanceSquaredTo(ml);
                if (dist < minDist) {
                    minDist = dist;
                    best = ml;
                }
            }
            boolean f = false;
            if (minDist < 150) {
                goal = best;
                f = true;
            } else {
                for (Direction dir : directions)
                    if (dir != Direction.CENTER && valid(rc.getLocation().add(dir))) {
                        MapLocation loc = rc.getLocation().add(dir);
                        if (rc.canSenseLocation(loc) && !bad[loc.x][loc.y] && rc.senseSoup(loc) == 0 && !alsobad[loc.x][loc.y] && rc.getTeamSoup() > 210) { //it is bad if it is around the HQ
                            if (tryBuild(RobotType.REFINERY, dir)) {
                                //send a message of type 7, to inform other robots about this refinery, and add it to their lists
                                goal = loc;
                                //send the message
                                int[] message = new int[7];
                                message[5] = sec1; //for security
                                message[6] = sec2; //for security
                                message[0] = 7; //refinery message code
                                message[3] = 468265245;
                                message[1] = loc.x + sec3;
                                message[2] = loc.y + sec4; //don't try to reverse lol
                                message[4] = 342355342;
                                int cst;
                                if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                                else cst = Math.min(rc.getTeamSoup(), 1);
                                if (cst > 0) {
                                    rc.submitTransaction(message, cst);
                                    System.out.println("I built a refinery! " + loc.x + " " + loc.y);
                                }
                                f = true;
                                break;
                            }
                        }
                    }
            }
            if (f) {
                occupation = "going to refinery";
                cnt = 0;
            }
            if (cnt >= 10) {
                if (rc.getRoundNum() < 300 && knowHQ) {
                    occupation = "going to refinery";
                    goal = HQLoc;
                    cnt = 0;
                }
            }
            cnt++;
        }
        else if (occupation == "building a DA") {
            boolean f = false;
            for (Direction dir : directions) {
                MapLocation loca = rc.getLocation().add(dir);
                if (valid(loca) && rc.canSenseLocation(loca) && !bad[loca.x][loca.y] && cnt > 2 && !alsobad[loca.x][loca.y] && cnt > 5) {
                    if (tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                        occupation = "none";
                        cnt = 0;
                        countDA++;
                        hasDA[loca.x][loca.y] = true;
                        //send a message of type 9
                        int[] message = new int[7];
                        message[5] = sec1; //for security
                        message[6] = sec2; //for security
                        message[0] = 9; //type
                        message[1] = loca.x + sec3;
                        message[2] = loca.y + sec4;
                        message[3] = 1238943;
                        message[4] = 47238782;
                        int cst;
                        if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                        else cst = Math.min(rc.getTeamSoup(), 1);
                        if (cst > 0) rc.submitTransaction(message, cst);
                        f = true;
                        break;
                    }
                }
            }
            if (!f && knowHQ) {
                Direction next1 = rc.getLocation().directionTo(HQLoc);
                if(!tryMove(next1)){
                    Direction l = next1.rotateLeft(), r = next1.rotateRight();
                    for(int i = 0; i < 5; i++){
                        MapLocation ml = rc.getLocation().add(l);
                        if(valid(ml)) if(((rc.getRoundNum() - vs[ml.x][ml.y]) > 7 || (vs[ml.x][ml.y] == 0)) && tryMove(l)) break;
                        ml = rc.getLocation().add(r);
                        if(valid(ml)) if(((rc.getRoundNum() - vs[ml.x][ml.y]) > 7 || (vs[ml.x][ml.y] == 0)) && tryMove(r)) break;
                        l = l.rotateLeft();
                        r = r.rotateRight();
                    }
                }
                cnt++;
            }
            if(cnt > 10 || countDA >= 2) {
                occupation = "none";
                cnt = 0;
            }
        }
        else if (occupation == "exploring") { //DONE!!!
            Direction next1 = rc.getLocation().directionTo(goal);
            if (next1 == Direction.CENTER) {
                occupation = "none";
                cnt = 0;
            }
            else {
                navigate(goal);
                for (int i = Math.max(rc.getLocation().x - 5, 0); i <= Math.min(width - 1, rc.getLocation().x + 5); i++) {
                    for (int j = Math.max(rc.getLocation().y - 5, 0); j <= Math.min(height - 1, rc.getLocation().y + 5); j++)
                        if (valid(new MapLocation(i, j)) && rc.canSenseLocation(new MapLocation(i, j))) {
                            System.out.println(i + " " + j);
                            MapLocation m = new MapLocation(i, j);
                            int amtSoup = rc.senseSoup(m);
                            if (valid(m) && rc.canSenseLocation(m) && amtSoup > 0) {
                                minersDemand++;
                                int[] message = new int[7];
                                message[5] = sec1; //for security
                                message[6] = sec2; //for security
                                message[0] = 1; //type
                                message[1] = i + sec3; //the x of the soup
                                message[2] = j + sec4; //the y of the soup
                                message[3] = amtSoup; //how much soup, we obviously want more: MORE SOUP!
                                message[4] = 0; //don't need for this kind of message
                                int cst;
                                if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                                else cst = Math.min(rc.getTeamSoup(), 1);
                                if (cst > 0) rc.submitTransaction(message, cst);
                            }
                        }
                }
                if (cnt >= 20 || (rc.getLocation().x == goal.x && rc.getLocation().y == goal.y) || rc.getRoundNum() > 220) {
                    occupation = "none";
                    cnt = 0;
                }
                cnt++;
            }
        } else if (occupation == "need to find soup") {
            int best = Integer.MAX_VALUE;
            MapLocation ml = new MapLocation(-1, -1);
            for (MapLocation SOUP : soups) if(!nosoup[SOUP.x][SOUP.y]){
                int dist = rc.getLocation().distanceSquaredTo(SOUP); //there should be a method for it tho...
                if (dist < best) {
                    best = dist;
                    ml = SOUP;
                }
            }
            if(cnt >= 5) {
                if (best == Integer.MAX_VALUE) {
                    occupation = "none";
                    cnt = 0;
                }
            }
            if(best != Integer.MAX_VALUE){
                goal = ml;
                occupation = "going to soup";
                cnt = 0;
            }
            if(occupation == "need to find soup") cnt++;
        }
        if (turnCount == 1) {
            int number = rc.getRoundNum();
            for (int i = 2; i <= 700; i++) {
                if (number <= i) break;
                Transaction[] tr = rc.getBlock(number - i);
                for (Transaction t : tr) {
                    int[] message = t.getMessage();
                    if(message[5] == sec1 && message[6] == sec2){ //it is a valid message
                        if(message[0] == 1) {
                            if(message[3] >= 1) { //not worth going if there is too little soup
                                int one = message[1] - sec3;
                                int two = message[2] - sec4;
                                if (valid(new MapLocation(one, two)) && hasSoup[one][two] == 0) {
                                    MapLocation locsoup = new MapLocation(one, two);
                                    soups.add(locsoup);
                                    hasSoup[one][two] = 1;
                                }
                            }
                        } else if (message[0] == 2) {
                            HQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                            knowHQ = true;
                            for (Direction dir : directions) {
                                MapLocation nLoc = HQLoc.add(dir);
                                System.out.println(dir);
                                if(valid(nLoc)){
                                    bad[nLoc.x][nLoc.y] = true; //don't build anything on here, since we will build the wall here
                                    for(Direction dir1 : directions){
                                        MapLocation nLoc1 = nLoc.add(dir1);
                                        if(valid(nLoc1)) alsobad[nLoc1.x][nLoc1.y] = true;
                                    }
                                }
                            }
                        } else if (message[0] == 6) {
                            if(!explored[message[1] - sec3]) {
                                explored[message[1] - sec3] = true;
                                cornersExplored++;
                            }
                        } else if (message[0] == 7) {
                            if (!hasRefinery[message[1] - sec3][message[2] - sec4]) {
                                MapLocation mploc = new MapLocation(message[1] - sec3, message[2] - sec4);
                                refineries.add(mploc);
                                hasRefinery[message[1] - sec3][message[2] - sec4] = true;
                            }
                        } else if (message[0] == 8) {
                            int one = message[1] - sec3;
                            int two = message[2] - sec4;
                            hasSoup[one][two] = 3;
                        } else if (message[0] == 9) {
                            int one = message[1] - sec3;
                            int two = message[2] - sec4;
                            if (!hasDA[one][two]) {
                                hasDA[one][two] = true;
                                countDA++;
                            }
                        }
                        else if(message[0] == 12){
                            int one = message[1] - sec3;
                            int two = message[2] - sec4;
                            if (!hasDF[one][two]) {
                                hasDF[one][two] = true;
                                countDF++;
                            }
                        }
                        else if(message[0] == 20) numVap++;
                        else if(message[0] == 23) numDF++;
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
                    if (message[0] == 1) {
                        if (message[3] >= 1) {
                            int one = message[1] - sec3;
                            int two = message[2] - sec4;
                            if (hasSoup[one][two] == 0) {
                                MapLocation locsoup = new MapLocation(one, two);
                                soups.add(locsoup);
                                hasSoup[one][two] = 1;
                            }
                        }
                    } else if (message[0] == 2) {
                        HQLoc = new MapLocation(message[1] - sec3, message[2] - sec4);
                        knowHQ = true;
                        for (Direction dir : directions) {
                            MapLocation nLoc = HQLoc.add(dir);
                            if(valid(nLoc)){
                                bad[nLoc.x][nLoc.y] = true; //don't build anything on here, since we will build the wall here
                                for(Direction dir1 : directions){
                                    MapLocation nLoc1 = nLoc.add(dir1);
                                    if(valid(nLoc1)) alsobad[nLoc1.x][nLoc1.y] = true;
                                }
                            }
                        }
                    } else if (message[0] == 6) {
                        if(!explored[message[1] - sec3]) {
                            explored[message[1] - sec3] = true;
                            cornersExplored++;
                        }
                    } else if (message[0] == 7) {
                        if (!hasRefinery[message[1] - sec3][message[2] - sec4]) {
                            MapLocation mploc = new MapLocation(message[1] - sec3, message[2] - sec4);
                            refineries.add(mploc);
                            hasRefinery[message[1] - sec3][message[2] - sec4] = true;
                        }
                    } else if (message[0] == 8) {
                        int one = message[1] - sec3;
                        int two = message[2] - sec4;
                        hasSoup[one][two] = 3;
                    } else if (message[0] == 9) {
                        int one = message[1] - sec3;
                        int two = message[2] - sec4;
                        if (!hasDA[one][two]) {
                            hasDA[one][two] = true;
                            countDA++;
                        }
                    }
                    else if(message[0] == 12){
                        int one = message[1] - sec3;
                        int two = message[2] - sec4;
                        if (!hasDF[one][two]) {
                            hasDF[one][two] = true;
                            countDF++;
                        }
                    }
                    else if(message[0] == 20) numVap++;
                    else if(message[0] == 23) numDF++;
                } else {
                    numMess++;
                    averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                }
            }
        }
    }
}
