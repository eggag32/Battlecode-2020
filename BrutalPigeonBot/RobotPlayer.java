package BrutalPigeonBot;
import battlecode.common.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };

    static Direction[] directions1 = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
            Direction.CENTER
    };

    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;
    static int height, width;
    static int averageEnemyMessageCost = 0, numMess = 0;
    static int sec1 = 28276523, sec2 = 78363534, sec3 = 648562, sec4 = 42452;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;

        turnCount = 0;
        height = rc.getMapHeight();
        width = rc.getMapWidth();
        bad = new boolean[width + 1][height + 1]; //for the wall stuff
        taken = new boolean[width + 1][height + 1];
        gd = new boolean[width + 1][height + 1]; //for the wall stuff
        alsobad = new boolean[width + 1][height + 1]; //for the wall stuff
        vis = new boolean[width + 1][height + 1]; //for the soup stuff
        hasSoup = new int[width + 1][height + 1]; //for the soup stuff, starts with 0, right?
        vs = new int[width + 1][height + 1]; //for the soup stuff, starts with 0, right?
        hasRefinery = new boolean[width + 1][height + 1]; //for the refinery stuff
        hasDA = new boolean[width + 1][height + 1]; //for the wall stuff
        hasDF = new boolean[width + 1][height + 1]; //for the wall stuff
        nosoup = new boolean[width + 1][height + 1]; //for the wall stuff
        haveEarlyVap = new boolean[width + 1][height + 1];
        explored = new boolean[10];

        while (true) {
            turnCount += 1;
            try {
                switch (rc.getType()) {
                    case HQ:
                        HQ.runHQ();
                        break;
                    case MINER:
                        Miner.runMiner();
                        break;
                    case REFINERY:
                        Refinery.runRefinery();
                        break;
                    case VAPORATOR:
                        Vaporator.runVaporator();
                        break;
                    case DESIGN_SCHOOL:
                        DesignSchool.runDesignSchool();
                        break;
                    case FULFILLMENT_CENTER:
                        FulfillmentCenter.runFulfillmentCenter();
                        break;
                    case LANDSCAPER:
                        Landscaper.runLandscaper();
                        break;
                    case DELIVERY_DRONE:
                        DeliveryDrone.runDeliveryDrone();
                        break;
                    case NET_GUN:
                        NetGun.runNetGun();
                        break;
                }
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static int numberMiners = 0;
    static int minersDemand = 6; //for the dirt people academy and start
    static boolean wallBuilt = false;

    static boolean[][] bad, hasRefinery, vis, hasDA, hasDF, nosoup, alsobad, gd, taken;
    static boolean[] explored;
    static int[][] hasSoup, vs;
    static boolean[][] haveEarlyVap;
    static Direction soupdir;
    static MapLocation goal, HQLoc;
    static String occupation = "none";
    static int cnt = 0, cornersExplored = 0, countDA = 0, prev = -1, countDF = 0;
    static ArrayList<MapLocation> refineries = new ArrayList<>(); //store all known refineries here (refinery messages are type 5)
    static ArrayList<MapLocation> soups = new ArrayList<>(); //store all known refineries here (refinery messages are type 5)
    static int numVap = 0, numDF = 0;
    static int waiting = Integer.MAX_VALUE;
    static boolean building = false, ngn = false;
    static int earlyVap = 0;

    static boolean valid(MapLocation m) {
        return ((m.x >= 0 && m.x < width) && (m.y >= 0 && m.y < height));
    }

    static void navigate(MapLocation Goal) throws GameActionException{
        if(!rc.isReady()) return;
        Direction next1 = rc.getLocation().directionTo(Goal);
        if(!tryMove(next1)){
            Direction l = next1.rotateLeft(), r = next1.rotateRight();
            for(int i = 0; i < 5; i++){
                System.out.println(l + " " + r);
                MapLocation ml = rc.getLocation().add(l);
                if(valid(ml)) if((((rc.getRoundNum() - vs[ml.x][ml.y]) > 7) || (vs[ml.x][ml.y] == 0)) && tryMove(l)) break;
                ml = rc.getLocation().add(r);
                if(valid(ml)) if((((rc.getRoundNum() - vs[ml.x][ml.y]) > 7) || (vs[ml.x][ml.y] == 0)) && tryMove(r)) break;
                l = l.rotateLeft();
                r = r.rotateRight();
            }
        }
    }

    static int numberDA = 0; //the number of Dirt people

    static String status = "going to HQ"; //also can be "mining dirt", "going to HQ", or "building the wall" (lol)
    static Direction dirtdir = Direction.CENTER, digdir = Direction.CENTER;
    static boolean adjacent = false, knowHQ = false, dontKnow = true;
    static int numMoves = 0;
    static int willFlood = 0;
    static ArrayList<MapLocation> posLoc = new ArrayList<>();
    static ArrayList<MapLocation> miners = new ArrayList<>();
    static boolean haveRequest = false, mining = false, digging = false;
    static MapLocation dest;
    static  boolean foundIt = false;

    static String DroneStatus = "going to HQ";
    static MapLocation theirHQLoc;
    static boolean knowTheirHQ = false, flg = false, holding = false;
    static int stage = 0;
    static boolean wait = false;
    static int lastWait = 0;
    static ArrayList<MapLocation> land = new ArrayList<>();

    static void DroneNav(MapLocation Goal) throws GameActionException{
        if(!rc.isReady()) return;
        Direction next1 = rc.getLocation().directionTo(Goal);
        if(!tryFly(next1)){
            Direction l = next1.rotateLeft(), r = next1.rotateRight();
            for(int i = 0; i < 5; i++){
                MapLocation ml = rc.getLocation().add(l);
                if(valid(ml)) if((((rc.getRoundNum() - vs[ml.x][ml.y]) > 7) || (vs[ml.x][ml.y] == 0)) && tryFly(l)) break;
                ml = rc.getLocation().add(r);
                if(valid(ml)) if((((rc.getRoundNum() - vs[ml.x][ml.y]) > 7) || (vs[ml.x][ml.y] == 0)) && tryFly(r)) break;
                l = l.rotateLeft();
                r = r.rotateRight();
            }
        }
    }

    static boolean tryMove(Direction dir) throws GameActionException {
        MapLocation mv = rc.getLocation().add(dir);
        if (valid(mv) && rc.canSenseLocation(mv) && rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(mv)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    static boolean tryFly(Direction dir) throws GameActionException { //for drone movement
        MapLocation mv = rc.getLocation().add(dir);
        if(valid(mv) && rc.canSenseLocation(mv) && rc.isReady() && rc.canMove(dir)){
            rc.move(dir);
            return true;
        }
        return false;
    }

    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (valid(rc.getLocation().add(dir)) && rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    static boolean tryMine(Direction dir) throws GameActionException {
        MapLocation loc = rc.getLocation().add(dir);
        if (valid(loc) && rc.canSenseLocation(loc) && rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    static boolean tryRefine(Direction dir) throws GameActionException {
        if (valid(rc.getLocation().add(dir)) && rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
}
