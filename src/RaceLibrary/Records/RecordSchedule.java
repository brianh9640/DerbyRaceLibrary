/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Records;

/**
 *
 * @author Brian
 */
public class RecordSchedule {

    public int      heat;
    public int      lanes;
    public String   name[];
    public int      carid[];

    private static final int MAX_LANES = 10;


    public RecordSchedule() {
        name  = new String[MAX_LANES];
        carid = new int[MAX_LANES];
        clear();
    }

    public void clear() {
        heat            = 0;
        lanes           = 0;
        for (int n=0;n<MAX_LANES;n++) {
            name[n]            = "";
            carid[n]           = 0;
        }
    }

}
