/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Comms;

/**
 *
 * @author Brian
 */


//
//
//  Protocol
//
//  Identify Command
//      DISP            Sending side is a Display type
//      HEAD            Sending side is a Head of Track type
//
//  Display Commands
//  10    TITL            Show Title Page
//  20    LIST            Show Roster Page
//  30    SCHE            Show Schedule Page
//  40    CURR            Show Current Race Page
//  50    WALL            Show All Racers Results Page
//  51    W1ST            Show 1st Place Winner Page
//  52    W2ND            Show 2nd Place Winner Page
//  53    W3RD            Show 3rd Place Winner Page
//  00    UPDT            Update Information on current page

//
//   RACE TITLE INFORMATION
//      T1;race title
//      T2;group name
//
//   STARTING GATE STATUS
//      GATE;xxxxx
//              UNKNOWN
//              READY
//              NOT READY
//
//   ROSTER DATA
//      R000    Will clear data table
//      Rxxx;Name;Car#;Weight;Pass
//      R999    Marks roster data complete
//
//   SCHEDULE DATA
//      S000    Will clear schedule data
//      Sxxx;Heat;Lanes;L1 Name;L1 Car#;L2 Name;L2 Car#;L3 Name;L3 Car#;L4 Name;L4 Car#
//      S999    Marks schedule data complete
//
//   CURRENT RACE DATA
//      L000;LIN    Clears race data and Line Up
//      L000;FIN    Clears race data and results
//      Lxxx;Heat;Heats;Lane;First Name;Last Name;Car#;Place;Time
//      L999    Marks lane data complete
//
//   RACE RESULTS
//      P000        Clears results data
//      Pxxx;Place;Name;Car#;Points;Avg Time;Fastest Time
//      P999        Marks results data complete
//
//

public class RaceProtocol {
    public static final boolean DEBUG             = true;

    public static final int UNIT_TYPE_DISPLAY                   = 100;
    public static final int UNIT_TYPE_TRACK_HEAD                = 200;

    public static final int PAGE_CHG_TITLE                      = 10;
    public static final int PAGE_CHG_ROSTER                     = 20;
    public static final int PAGE_CHG_SCHEDULE                   = 30;
    public static final int PAGE_CHG_RACE                       = 40;
    public static final int PAGE_CHG_RESULTS_ALL                = 50;
    public static final int PAGE_CHG_1ST_PLACE                  = 51;
    public static final int PAGE_CHG_2ND_PLACE                  = 52;
    public static final int PAGE_CHG_3RD_PLACE                  = 53;
    public static final int PAGE_UPDATE                         = 0;

    public static final int DATA_MAX_LINES                      = 200;
    public static final int DATA_LINE_ITEMS                     = 16;

    public static final int RACE_LINE_UP                        = 1;
    public static final int RACE_FINISHED                       = 2;

    public static final int RACE_GATE_UNKNOWN                   = 0;
    public static final int RACE_GATE_NOT_READY                 = 1;
    public static final int RACE_GATE_READY                     = 2;
    public static final int RACE_GATE_RACING                    = 3;

    protected int   nRoster;
    protected int   nSchedule;
    protected int   nRace;
    protected int   nResults;

    protected String dataTitle[];
    
    protected String dataGate;
    protected String dataRoster[][];
    protected String dataSchedule[][];
    protected String dataRace[][];
    protected String dataResults[][];
    protected int    raceStatus;

    protected int   nTmpRoster;
    protected int   nTmpSchedule;
    protected int   nTmpRace;
    protected int   nTmpResults;

    protected String tmpRoster[][];
    protected String tmpSchedule[][];
    protected String tmpRace[][];
    protected String tmpResults[][];
    protected int    tmpRaceStatus;

    protected int    lastCommand;
    protected int    unitType;

    public RaceProtocol() {
        nRoster         = 0;
        nSchedule       = 0;
        nRace           = 0;
        nResults        = 0;

        dataGate        = null;
        dataRoster      = null;
        dataSchedule    = null;
        dataRace        = null;
        dataResults     = null;

        dataTitle       = new String[4];
        dataTitle[1]    = "Race Desc";
        dataTitle[2]    = "Group Name";
        
        clear();
    }

    public void clear() {
        lastCommand     = 0;
        unitType        = UNIT_TYPE_DISPLAY;

        nTmpRoster         = 0;
        nTmpSchedule       = 0;
        nTmpRace           = 0;
        nTmpResults        = 0;

        tmpRoster      = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
        tmpSchedule    = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
        tmpRace        = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
        tmpResults     = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
    }

    public synchronized int getUnitType() {
        return unitType;
    }

    public synchronized int getLastCmd() {
        int cmd = lastCommand;
        lastCommand = 0;
        return cmd;
    }

    public synchronized String getStartingGate() {
        String tmp = "";

        if (dataGate == null) return tmp;

        return dataGate;
    }

    public synchronized String getRaceTitle(int r) {
        String tmp = "-";

        if (dataTitle==null || r < 1 || r > 2) return tmp;
        if (dataTitle[r] == null) return tmp;
        
        return dataTitle[r];
    }    
    
    public synchronized String getRosterData(int r,int c) {
        String tmp = "";

        if (dataRoster==null) return tmp;

        //if (DEBUG) System.out.println("r="+r+" c="+c+"   nRoster="+nRoster);
        if (r < 0 || r >=nRoster || c < 0 || c >= DATA_LINE_ITEMS) return tmp;

        //if (DEBUG) System.out.println("r="+r+" c="+c+"  data="+dataRoster[r][c]);

        return dataRoster[r][c];
    }

    public synchronized int getNRoster() {
        return nRoster;
    }

    public synchronized String getScheduleData(int r,int c) {
        String tmp = "";

        if (dataSchedule==null) return tmp;

        if (r < 0 || r >=nSchedule || c < 0 || c >= DATA_LINE_ITEMS) return tmp;

        return dataSchedule[r][c];
    }

    public synchronized int getNSchedule() {
        return nSchedule;
    }

    public synchronized String getRaceData(int r,int c) {
        String tmp = "";

        if (dataRace==null) return tmp;

        //if (DEBUG) System.out.println("r="+r+" c="+c+"   nRoster="+nRoster);
        if (r < 0 || r >=nRace || c < 0 || c >= DATA_LINE_ITEMS) return tmp;

        //if (DEBUG) System.out.println("r="+r+" c="+c+"  data="+dataRoster[r][c]);

        return dataRace[r][c];
    }

    public synchronized int getNRace() {
        //if (isNull(nRace)) return 1;
        return nRace;
    }

    public synchronized int getRaceStatus() {
        return raceStatus;
    }

    public synchronized String getResultsData(int r,int c) {
        String tmp = "";

        if (dataResults==null) return tmp;

        if (r < 0 || r >=nResults || c < 0 || c >= DATA_LINE_ITEMS) return tmp;

        return dataResults[r][c];
    }

    public synchronized int getNResults() {
        return nResults;
    }

    public synchronized void parseInput(String data) {

        lastCommand = 0;
        if (data.length() < 4) return;
        String cmd = data.substring(0,4);

        if ("DISP".equals(cmd)) {
            lastCommand = UNIT_TYPE_DISPLAY;
            unitType = UNIT_TYPE_DISPLAY;
            return;
        }
        if ("HEAD".equals(cmd)) {
            lastCommand = UNIT_TYPE_TRACK_HEAD;
            unitType = UNIT_TYPE_TRACK_HEAD;
            return;
        }
        if ("TITL".equals(cmd)) {
            lastCommand = PAGE_CHG_TITLE;
            return;
        }
        if ("LIST".equals(cmd)) {
            lastCommand = PAGE_CHG_ROSTER;
            return;
        }
        if ("SCHE".equals(cmd)) {
            lastCommand = PAGE_CHG_SCHEDULE;
            return;
        }
        if ("CURR".equals(cmd)) {
            lastCommand = PAGE_CHG_RACE;
            return;
        }
        if ("WALL".equals(cmd)) {
            lastCommand = PAGE_CHG_RESULTS_ALL;
            return;
        }
        if ("W1ST".equals(cmd)) {
            lastCommand = PAGE_CHG_1ST_PLACE;
            return;
        }
        if ("W2ND".equals(cmd)) {
            lastCommand = PAGE_CHG_2ND_PLACE;
            return;
        }
        if ("W3RD".equals(cmd)) {
            lastCommand = PAGE_CHG_3RD_PLACE;
            return;
        }
        if ("UPDT".equals(cmd)) {
            lastCommand = PAGE_UPDATE;
            return;
        }
                // STARTING GATE
        if ("GATE".equals(cmd)) {

            String tmp[] = parseDataLine(data);

            dataGate = new String(tmp[1]);
            if (DEBUG) System.out.println("GATE="+dataGate);
            if (DEBUG) System.out.println();

            return;
        }
        
                // RACE TITLES
        if ("T1".equals(data.substring(0,2))) {
            if (DEBUG) System.out.println("T1  data="+data);
            String tmp = data.substring(3);
            if (tmp != null) dataTitle[1] = tmp;
        }
        if ("T2".equals(data.substring(0,2))) {
            if (DEBUG) System.out.println("T1  data="+data);
            String tmp = data.substring(3);
            if (tmp != null) dataTitle[2] = tmp;
        }

                // ROSTER
        if ("R".equals(data.substring(0,1))) {

            int index = Integer.valueOf(data.substring(1,4));
            //if (DEBUG) System.out.println("  index="+index);
            if (index == 0) {
                nTmpRoster = 0;
                tmpRoster      = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
                return;
            }
            else if (index == 999) {
                nRoster     = nTmpRoster;
                dataRoster  = tmpRoster;
            }
            else {
                String tmp[] = parseDataLine(data);
                for (int i=0;i<DATA_LINE_ITEMS;i++) {
                    tmpRoster[nTmpRoster][i] = tmp[i];
                    //if (DEBUG) System.out.print(tmpRoster[nTmpRoster][i] + "##");
                }
                //if (DEBUG) System.out.println();
                nTmpRoster++;
            }

            return;
        }

                // SCHEDULE
        if ("S".equals(data.substring(0,1))) {

            int index = Integer.valueOf(data.substring(1,4));
            //if (DEBUG) System.out.println("  index="+index);
            if (index == 0) {
                nTmpSchedule = 0;
                tmpSchedule      = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
                return;
            }
            else if (index == 999) {
                nSchedule     = nTmpSchedule;
                dataSchedule  = tmpSchedule;
            }
            else {
                String tmp[] = parseDataLine(data);
                for (int i=0;i<DATA_LINE_ITEMS;i++) {
                    tmpSchedule[nTmpSchedule][i] = tmp[i];
                    //if (DEBUG) System.out.print(tmpSchedule[nTmpSchedule][i] + "##");
                }
                //if (DEBUG) System.out.println();
                nTmpSchedule++;
            }

            return;
        }

                // RACE RESULTS
        if ("L".equals(data.substring(0,1))) {

            int index = Integer.valueOf(data.substring(1,4));
            //if (DEBUG) System.out.println("  index="+index);
            String tmp[] = parseDataLine(data);
            if (index == 0) {
                nTmpRace = 0;
                tmpRaceStatus = 0;
                tmpRace      = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
                if (tmp[1].equals("LIN")) tmpRaceStatus = RACE_LINE_UP;
                if (tmp[1].equals("FIN")) tmpRaceStatus = RACE_FINISHED;
                return;
            }
            else if (index == 999) {
                nRace       = nTmpRace;
                dataRace    = tmpRace;
                raceStatus  = tmpRaceStatus;
            }
            else {
                for (int i=0;i<DATA_LINE_ITEMS;i++) {
                    tmpRace[nTmpRace][i] = tmp[i];
                    //if (DEBUG) System.out.print(tmpRace[nTmpRace][i] + "##");
                }
                //if (DEBUG) System.out.println();
                nTmpRace++;
            }

            return;
        }

        if ("P".equals(data.substring(0,1))) {

            int index = Integer.valueOf(data.substring(1,4));
            //if (DEBUG) System.out.println("  index="+index);
            if (index == 0) {
                nTmpResults = 0;
                tmpResults      = new String[DATA_MAX_LINES][DATA_LINE_ITEMS];
                return;
            }
            else if (index == 999) {
                nResults     = nTmpResults;
                dataResults  = tmpResults;
            }
            else {
                String tmp[] = parseDataLine(data);
                for (int i=0;i<DATA_LINE_ITEMS;i++) {
                    tmpResults[nTmpResults][i] = tmp[i];
                    //if (DEBUG) System.out.print(tmpResults[nTmpResults][i] + "##");
                }
                //if (DEBUG) System.out.println();
                nTmpResults++;
            }

            return;
        }

        return;
    }

    protected String[] parseDataLine(String data) {
        String value[] = new String[DATA_LINE_ITEMS];

        int i = 0;
        while (i < DATA_LINE_ITEMS) {
            value[i] = "";

            int idx = data.indexOf(';');
            if (idx >= 0) {
                value[i] = data.substring(0,idx);
                data = data.substring(idx+1);
            }
            else {
                if (data.length() > 0) value[i] = data;
                data = "";
            }

            i++;
        }

        return value;
    }


    public String getCmdString(int cmd) {
        String msg = "";

        switch(cmd) {
            default:
                msg = "????";
                break;
            case UNIT_TYPE_DISPLAY :
                msg = "DISP";
                break;
            case UNIT_TYPE_TRACK_HEAD :
                msg = "HEAD";
                break;
            case PAGE_CHG_TITLE :
                msg = "TITL";
                break;
            case PAGE_CHG_ROSTER :
                msg = "LIST";
                break;
            case PAGE_CHG_SCHEDULE :
                msg = "SCHE";
                break;
            case PAGE_CHG_RACE :
                msg = "CURR";
                break;
            case PAGE_CHG_RESULTS_ALL :
                msg = "WALL";
                break;
            case PAGE_CHG_1ST_PLACE :
                msg = "W1ST";
                break;
            case PAGE_CHG_2ND_PLACE :
                msg = "W2ND";
                break;
            case PAGE_CHG_3RD_PLACE :
                msg = "W3RD";
                break;
            case PAGE_UPDATE :
                msg = "UPDT";
                break;
        }

        return msg;
    }

    public synchronized String getTitleString(int sub,String title) {
        String msg = "";

        if (sub == 0) return "T0;";

//      Txxx;title
        msg = "T" + String.format("%1$01d", sub) + ";" + title;

        return msg;
    }

    public synchronized String getRosterString(int record,String name,int carid,double weight,int pass) {
        String msg = "";

        if (record == 0) return "R000";

//      Rxxx;Name;Car#;Weight;Pass
        msg = "R" + String.format("%1$03d", record) + ";" + name + ";" + carid + ";" + String.format("%1$4.2f",weight) + ";" + pass;

        return msg;
    }

    public synchronized String getScheduleString(int record,int heat,int lanes,String name[],int carid[]) {
        String msg = "";

        if (record == 0) return "S000";

//      Sxxx;Heat;Lanes;L1 Name;L1 Car#;L2 Name;L2 Car#;L3 Name;L3 Car#;L4 Name;L4 Car#
        msg = "S" + String.format("%1$03d", record) + ";" + heat + ";" + lanes;
        for (int n=0;n<lanes;n++) {
            msg = msg + ";" + name[n] + ";" + carid[n];
        }

        return msg;
    }

    public synchronized String getRaceString(int record,int heat,int heats,int lane,String first,String last,int carid,int place,double time,double speed) {
        String msg = "";

        if (record == 0) {
            if (heat==0) return "L000;LIN";
            else return "L000;FIN";
        }

//      Lxxx;Heat;Heats;Lane;First Name;Last Name;Car#;Place;Time
        msg = "L" + String.format("%1$03d", record) + ";" +
                    heat + ";" +
                    heats + ";" +
                    lane + ";" +
                    first + ";" +
                    last + ";" +
                    carid + ";" +
                    place + ";" +
                    String.format("%1$6.3f",time) + ";" +
                    String.format("%1$6.1f",speed);

        return msg;
    }

    public synchronized String getResultsString(int record,int place,String name,int carid,int points,double avgTime,double bestTime) {
        String msg = "";

        if (record == 0) return "P000";

//      Pxxx;Place;Name;Car#;Points;Avg Time;Fastest Time

        msg = "P" + String.format("%1$03d", record) + ";" +
                    place + ";" +
                    name + ";" +
                    carid + ";" +
                    points + ";" +
                    String.format("%1$6.3f",avgTime) + ";" +
                    String.format("%1$6.3f",bestTime);

        return msg;
    }

//      GATE;xxxxx
//              UNKNOWN
//              READY
//              NOT READY

    public synchronized String getStartingGateString(int status) {
        String msg = "";

        switch (status) {
            default :
            case RACE_GATE_UNKNOWN :
                msg = new String("GATE;UNKNOWN");
                break;
            case RACE_GATE_NOT_READY :
                msg = new String("GATE;SETUP");
                break;
            case RACE_GATE_READY :
                msg = new String("GATE;READY");
                break;
            case RACE_GATE_RACING :
                msg = new String("GATE;RACING");
                break;
        }
        return msg;
    }
    
}
