/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary;

/**
 *
 * @author Brian
 */
public class ScheduleRace {

    int nLanes;
    int nRacers;

//    int racerHeat[];
    int schedule[][];

    public ScheduleRace() {
        clear();
    }

    public void clear() {

        nLanes = 0;
        nRacers = 0;

        schedule = null;

    }

    public void define(int lanes,int racers) {
        nLanes      = lanes;
        nRacers     = racers;

        schedule = new int[nRacers][nLanes];
        
    }

    public int getRacerPos(int heat,int lane) {
        if (heat < 0 || heat >= nRacers) return -1;
        if (lane < 0 || lane >= nLanes) return -1;

        return schedule[heat][lane];
    }

    public void generate() {


        // first pick a "rough" candidate
        Double dtmp = new Double(nRacers);
        Double dtmp2 = new Double(nLanes+1);
        dtmp = dtmp / dtmp2;
        int candidate = dtmp.intValue();

        // just in case numCars = numLanes
        if (candidate < 1)
            candidate = 1;

        // refine the rough candidate, if necessary
        while (true)
        {
            if (candidate == 1) break;

            // the idea here is that we want to spread each car's heats throughout the chart
            // and any two heats for a car will be at least "candidate" or so apart
            int sumShifts = 0;

            for (int i=1; i<nLanes; i++)
                sumShifts = sumShifts + candidate + i - 1;

            if (sumShifts < (nRacers - candidate))
                break;            //       ^
                                  //       |
                candidate--;      //  we don't want car 1's last race to be too close
        }                         // to the end of the chart.  if it is, then some races
                                  // of some other cars will be too close together
                                  // because of wrap-around

        // okay, we'll use this value
        int initialPhaseShift = candidate;

        // determine generators
        int gens[] = new int[8-1];

        for (int i=1; i<nLanes; i++)
        {
            gens[i-1] = nRacers + 1 - i - initialPhaseShift;
        }

        // generate chart
        for (int i=0; i<nRacers; i++)
        {
            int car = i+1;
            schedule[i][0] = car;

            for (int j=1; j<nLanes; j++)
            {
                car = (car + gens[j-1]) % nRacers;
                if (car == 0) car = nRacers;

                schedule[i][j] = car;
            }

        }
    }


    public void print() {

        System.out.println("Race Schedule");

        for (int heat=0;heat<nRacers;heat++) {
            System.out.print(heat);
            System.out.print(":");
            for (int lane=0;lane<nLanes;lane++) {
                System.out.print("  ");
                System.out.print(schedule[heat][lane]);
            }
                System.out.println("  ");
        }

    }
}
