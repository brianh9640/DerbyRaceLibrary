/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Comms;

import RaceLibrary.RaceDatabase;
import RaceLibrary.RaceManager;
import RaceLibrary.Records.*;

import java.sql.*;

/**
 *
 * @author Brian
 */
public class RemoteCommUpdater extends Thread {

    private static final boolean DEBUG = true;

    protected int       flagUpdateTitle;
    protected int       flagUpdateRoster;
    protected int       flagUpdateSchedule;
    protected int       flagUpdateRace;
    protected int       flagUpdateResults;

    protected int       groupID;
    protected int       raceID;
    protected int       heatID;
    protected int       nHeats;
    protected int       nLanes;

    RaceDatabase        database;
    RaceServer          raceServer;

    int                 nRoster;
    RecordRoster        recordRoster[];

    int                 nSchedule;
    RecordSchedule      recordSchedule[];

    int                 nRace;
    RecordRace          recordRace[];

    RaceResults         raceResults;

    public RemoteCommUpdater() {

        groupID = 0;
        raceID  = 0;
        heatID  = 0;
        nHeats  = 0;

        nRoster = 0;
        nRace   = 0;

        raceResults = new RaceResults();
        
        updateAll();
    }

    public synchronized void updateAll() {
        if (DEBUG) System.out.println("RemoteCommUpdater - updateAll()");

        flagUpdateTitle     = -1;
        flagUpdateRoster    = -1;
        flagUpdateSchedule  = -1;
        flagUpdateRace      = -1;
        flagUpdateResults   = -1;

        if (raceServer != null) raceServer.clearStatus();

    }

    public synchronized void updateRoster() {
        if (DEBUG) System.out.println("RemoteCommUpdater - updateRoster()");

        flagUpdateRoster    = -1;

    }

    public synchronized void updateRace() {
        if (DEBUG) System.out.println("RemoteCommUpdater - updateRace()");

        flagUpdateRace    = -1;
    }

    public void setDatabase(RaceDatabase db) {
        database = db;
    }

    public void setRaceServer(RaceServer obj) {
        raceServer = obj;
    }

    public void setGroup(int g) {
        groupID = g;
    }

    public void setRace(int r) {
        raceID = r;
    }

    public void setHeatID(int h) {
        heatID = h;
    }

    public void setHeats(int h) {
        nHeats = h;
    }

    public void setLanes(int n) {
        nLanes = n;
    }

    public synchronized void newRaceResults(RaceResults rr) {
        if (rr == null) return;

        raceResults = rr;
        flagUpdateResults = -1;
    }

    @Override
    public void run() {

        if (DEBUG) System.out.println("RemoteCommUpdater - Thread Started");

        while (true) {

            if (raceServer.updatedRequested()) updateAll();

            remoteUpdateRoster();
            remoteUpdateSchedule();
            remoteUpdateRace();
            remoteUpdateResults();
            remoteUpdateTitle();

            try {
                if (processingUpdates()) this.sleep(40);
                else this.sleep(50);
            } catch (InterruptedException ex) {
            }
        }

    }

    private boolean processingUpdates() {

        if (flagUpdateRoster < nRoster) return true;

        return false;
    }

    protected void remoteUpdateTitle() {
        //if (DEBUG) System.out.println("RemoteCommUpdater - updateRoster()");

        if (database == null) return;

        if (flagUpdateTitle < 0) flagUpdateTitle = 0;

        if (flagUpdateTitle < 2) {
            flagUpdateTitle++;
            String title = "unknown";
            switch (flagUpdateTitle) {
                case 1: 
                    title = database.getRaceConfig(RaceDatabase.DB_CFGID_TITLE);
                    break;
                case 2:
                    title = database.getRaceConfig(RaceDatabase.DB_CFGID_ORG_NAME);
                    break;
            }
            raceServer.sendTitleData(flagUpdateTitle,title);
            
        }

    }

    protected void remoteUpdateRoster() {
        //if (DEBUG) System.out.println("RemoteCommUpdater - updateRoster()");

        if (database == null || groupID==0) return;

        if (flagUpdateRoster < 0) loadRoster();

        if (flagUpdateRoster < nRoster) sendRoster();

    }

    protected void remoteUpdateSchedule() {
        //if (DEBUG) System.out.println("RemoteCommUpdater - updateSchedule()");

        if (database == null || raceID==0) return;

        if (flagUpdateSchedule < 0) loadSchedule();

        if (flagUpdateSchedule < nSchedule) sendSchedule();

    }

    protected void remoteUpdateRace() {
        //if (DEBUG) System.out.println("RemoteCommUpdater - updateRace()");

        if (database == null || raceID==0) return;

        if (flagUpdateRace < 0) loadRace();

        if (flagUpdateRace < nRace) sendRace();

    }

    protected void remoteUpdateResults() {
        //if (DEBUG) System.out.println("RemoteCommUpdater - updateRoster()");

        if (database == null || groupID==0) return;

        if (flagUpdateResults < 0) flagUpdateResults = 0;

        if (flagUpdateResults < raceResults.nRacers) sendResults();

    }

    protected void loadRoster() {

        if (DEBUG) System.out.println("RemoteCommUpdater - loadRoster()");

        nRoster = database.getQueryCount("roster","groupid="+groupID);

        if (DEBUG) System.out.println("RemoteCommUpdater - groupID=" + groupID + "  n=" + nRoster);

        recordRoster = new RecordRoster[nRoster];

        String sql = "SELECT racerid,carid,lastname,firstname,groupid,picid,weight,pass " +
                     "FROM roster WHERE groupid=" + groupID + " ORDER BY lastname,firstname";

        ResultSet rs = database.execute(sql);
        if (rs == null) return;

        int r = 0;
        try {
            while (rs.next() && r < nRoster) {

                recordRoster[r]                 = new RecordRoster();
                recordRoster[r].racerid         = rs.getInt(1);
                recordRoster[r].carid           = rs.getInt(2);
                recordRoster[r].lastname        = rs.getString(3);
                recordRoster[r].firstname       = rs.getString(4);
                recordRoster[r].groupid         = rs.getInt(5);
                recordRoster[r].picid           = rs.getInt(6);
                recordRoster[r].weight          = rs.getDouble(7);
                recordRoster[r].pass            = rs.getInt(8);

                r++;
            }
        } catch (SQLException ex) {

        }

    }

    protected void loadSchedule() {
        String sql;
        ResultSet rs;

        //flagUpdateSchedule = 0;

        nSchedule = nHeats;
        if (nSchedule == 0) return;

        sql = "SELECT results.heat,results.lane,results.racerid,roster.carid,roster.lastname,roster.firstname " +
                     "FROM results,roster WHERE results.raceid=" + raceID + " AND results.racerid=roster.racerid " +
                     "ORDER BY results.heat,results.lane";

        if (DEBUG) System.out.println(sql);
        rs = database.execute(sql);
        if (rs == null) return;

        recordSchedule = new RecordSchedule[nSchedule];
        for (int n=0;n<nSchedule;n++) {
            recordSchedule[n] = new RecordSchedule();
            recordSchedule[n].heat = n+1;
            recordSchedule[n].lanes = nLanes;
        }

        try {
            while (rs.next()) {
                int index = rs.getInt(1) - 1;

                int laneIdx = rs.getInt(2) - 1;

                recordSchedule[index].name[laneIdx] = rs.getString(6) + " " + rs.getString(5);
                recordSchedule[index].carid[laneIdx] = rs.getInt(4);

            }
        } catch (SQLException ex) {

        }


    }

    protected void loadRace() {

        if (DEBUG) System.out.println("RemoteCommUpdater - loadRace()");

        nRace = database.getRaceConfigInt(RaceDatabase.DB_CFGID_NLANES);

        recordRace = new RecordRace[nRace];

        if (raceID == 0 || heatID == 0) {
            for (int n=0;n<nRace;n++) {
                recordRace[n] = new RecordRace();
            }
            return;
        }

        String sql = "SELECT results.resultid," +
                            "results.raceid," +
                            "results.heat," +
                            "results.lane," +
                            "results.racerid," +
                            "roster.carid," +
                            "roster.lastname," +
                            "roster.firstname," +
                            "roster.picid," +
                            "results.time," +
                            "results.place," +
                            "results.points," +
                            "results.speed " +
                     "FROM results,roster " +
                     "WHERE results.raceid=" + raceID +
                      " AND results.heat=" + heatID +
                      " AND results.racerid=roster.racerid " +
                     "ORDER BY results.lane";

        ResultSet rs = database.execute(sql);
        if (rs == null) {
            //recordRace      = null;
            return;
        }

        int r = 0;
        try {
            while (rs.next() && r < nRace) {

                recordRace[r]                   = new RecordRace();

                recordRace[r].resultid        = rs.getInt(1);
                recordRace[r].raceid          = rs.getInt(2);
                recordRace[r].heat            = rs.getInt(3);
                recordRace[r].lane            = rs.getInt(4);
                recordRace[r].racerid         = rs.getInt(5);
                recordRace[r].carid           = rs.getInt(6);
                recordRace[r].lastname        = rs.getString(7);
                recordRace[r].firstname       = rs.getString(8);
                recordRace[r].groupid         = 0;
                recordRace[r].picid           = rs.getInt(9);
                recordRace[r].time            = rs.getDouble(10);
                recordRace[r].place           = rs.getInt(11);
                recordRace[r].points          = rs.getInt(12);
                recordRace[r].speed           = rs.getDouble(13);

                r++;
            }
        } catch (SQLException ex) {

        }

    }


    protected void sendRoster() {
        //if (DEBUG) System.out.println("RemoteCommUpdater - sendRoster() - flagUpdateRoster="+flagUpdateRoster);
        int r = flagUpdateRoster;
        if (flagUpdateRoster < 0) {
            raceServer.sendRosterData(0,"",0,0.0,0);
            flagUpdateRoster = 0;
            return;
        }
        String name = recordRoster[r].firstname + " " + recordRoster[r].lastname;
        raceServer.sendRosterData(flagUpdateRoster+1,name,recordRoster[r].carid,recordRoster[r].weight,recordRoster[r].pass);
        flagUpdateRoster++;

        if (flagUpdateRoster == nRoster) raceServer.sendRosterData(999,"",0,0.0,0);
    }

    protected void sendSchedule() {
        int r = flagUpdateSchedule;
        if (flagUpdateSchedule < 0) {
            raceServer.sendScheduleData(0,0,0,null,null);
            flagUpdateSchedule = 0;
            return;
        }
        raceServer.sendScheduleData(flagUpdateSchedule+1,recordSchedule[r].heat,recordSchedule[r].lanes,recordSchedule[r].name,recordSchedule[r].carid);
        flagUpdateSchedule++;

        if (flagUpdateSchedule == nSchedule) raceServer.sendScheduleData(999,999,0,null,null);
        
    }

    protected void sendRace() {

        if (recordRace[0] == null) return;
        if (recordRace[0].place == 0) raceServer.sendRaceData(0,0,0,0,"","",0,0,0.0,0.0);
        else raceServer.sendRaceData(0,1,0,0,"","",0,0,0.0,0.0);
        
        for (int r=0;r<nRace;r++) {

            raceServer.sendRaceData(r+1,
                                    recordRace[r].heat,
                                    nHeats,
                                    recordRace[r].lane,
                                    recordRace[r].firstname,
                                    recordRace[r].lastname,
                                    recordRace[r].carid,
                                    recordRace[r].place,
                                    recordRace[r].time,
                                    recordRace[r].speed);

        }
        raceServer.sendRaceData(999,0,0,0,"","",0,0,0.0,0.0);

        flagUpdateRace = nRace;
    }

    protected void sendResults() {

        //sendResultsData(int record,int place,String name,int carid,int points,double avgTime,double bestTime)
        if (raceResults == null) return;
        if (flagUpdateResults == 0) raceServer.sendResultsData(0,0,"",0,0,0.0,0.0);

        int r = flagUpdateResults;
        raceServer.sendResultsData(r+1,
                                   raceResults.place[r],
                                   raceResults.name[r],
                                   raceResults.carID[r],
                                   raceResults.sumPoints[r],
                                   raceResults.avgTime[r],
                                   raceResults.bestTime[r]);

        flagUpdateResults++;

        if (flagUpdateResults == raceResults.nRacers) raceServer.sendResultsData(999,0,"",0,0,0.0,0.0);
    }
}
