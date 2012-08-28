/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Records;

/**
 *
 * @author Brian
 */
public class RecordRoster {

    public int      racerid;
    public int      carid;
    public String   lastname;
    public String   firstname;
    public int      groupid;
    public int      picid;
    public double   weight;
    public int      pass;

    public RecordRoster() {
        clear();
    }

    public void clear() {
        racerid         = 0;
        carid           = 0;
        lastname        = "";
        firstname       = "";
        groupid         = 0;
        picid           = 0;
        weight          = 0.0;
        pass            = 0;
    }


}
