/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Records;

/**
 *
 * @author Brian
 */
public class RaceResults implements Cloneable {
        public static final int MAX_RESULTS = 200;

        public  int         racerID[];
        public  String      name[];
        public  int         carID[];
        public  int         place[];
        public  int         sumPoints[];
        public  double      avgTime[];
        public  double      bestTime[];

        public  int         nRacers;

        public RaceResults() {
            nRacers = 0;

            racerID         = new int[MAX_RESULTS];
            name            = new String[MAX_RESULTS];
            carID           = new int[MAX_RESULTS];
            place           = new int[MAX_RESULTS];
            sumPoints       = new int[MAX_RESULTS];
            avgTime         = new double[MAX_RESULTS];
            bestTime        = new double[MAX_RESULTS];
        }

        public void add(int id,String rname,int cid,int sum,double avg,double best) {
            racerID[nRacers]    = id;
            name[nRacers]       = rname;
            carID[nRacers]      = cid;
            sumPoints[nRacers]  = sum;
            avgTime[nRacers]    = avg;
            bestTime[nRacers]   = best;
        }

        public Object clone() {
            try {
              return super.clone();
            }
            catch( CloneNotSupportedException e ) {
              return null;
            }
        }

}
