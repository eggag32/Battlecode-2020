package BrutalPigeonBot;

import battlecode.common.*;
import java.util.*;

public class DesignSchool extends RobotPlayer{
	static void runDesignSchool() throws GameActionException {
        System.out.println(numberDA);
        if (numberDA < 24 && rc.getTeamSoup() > 220){
            for (Direction dir : directions) if(valid(rc.getLocation().add(dir))){
                if (tryBuild(RobotType.LANDSCAPER, dir)){
                    int[] message = new int[7];
                    message[5] = sec1; //for security
                    message[6] = sec2; //for security
                    message[0] = 4; //type
                    message[1] = 5442543;
                    message[2] = 23342353;
                    message[3] = 234554654;
                    message[4] = 872464;
                    int cst;
                    if (numMess > 10) cst = Math.min(rc.getTeamSoup(), averageEnemyMessageCost + 1);
                    else cst = Math.min(rc.getTeamSoup(), 1);
                    if (cst > 0) rc.submitTransaction(message, cst);
                }
            }
        }
        if (turnCount == 1) {
            int number = rc.getRoundNum();
            for (int i = 2; i <= 400; i++) {
                if (number <= i) break;
                Transaction[] tr = rc.getBlock(number - i);
                for (Transaction t : tr) {
                    int[] message = t.getMessage();
                    if (message[5] == sec1 && message[6] == sec2) {
                        if (message[0] == 4) {
                            numberDA++;
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
                    if (message[0] == 4) {
                        numberDA++;
                    }
                } else {
                    numMess++;
                    averageEnemyMessageCost = (averageEnemyMessageCost + t.getCost()) / numMess;
                }
            }
        }
    }
}
