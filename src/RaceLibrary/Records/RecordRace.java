/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Records;

/**
 *
 * @author Brian
 */
public class RecordRace {

    public int      resultid;
    public int      raceid;
    public int      heat;
    public int      lane;
    public int      racerid;
    public int      carid;
    public String   lastname;
    public String   firstname;
    public int      groupid;
    public int      picid;
    public double   time;
    public int      place;
    public int      points;
    public double   speed;

    public RecordRace() {
        clear();
    }

    public void clear() {

        resultid        = 0;
        raceid          = 0;
        heat            = 0;
        lane            = 0;
        racerid         = 0;
        carid           = 0;
        lastname        = "";
        firstname       = "";
        groupid         = 0;
        picid           = 0;
        time            = 0.0;
        place           = 0;
        points          = 0;
        speed           = 0.0;
    }

}
