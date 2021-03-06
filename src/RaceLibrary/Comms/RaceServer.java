/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Comms;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Brian
 */
public class RaceServer extends Thread {

    private static final boolean DEBUG = true;

    ServerSocket serverSocket;
    RaceServerThread client[];

    public static final int RACEPORT                    = 8765;
    public static final int RACE_MAX_CLIENTS            = 20;

    RaceProtocol raceProtocol;

    protected int       statusStartingGate;

    public RaceServer() {
        serverSocket = null;
        client = new RaceServerThread[RACE_MAX_CLIENTS];
        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            client[n] = null;
        }

        raceProtocol = new RaceProtocol();
        statusStartingGate = -1;
    }

    public void clearStatus() {
        statusStartingGate = -1;
    }

    public void close() {
        if (serverSocket != null) try {
            serverSocket.close();
        } catch (IOException ex) {
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverSocket = null;
    }

    @Override
    public void run() {
        boolean listening = true;
        close();

        System.out.println("RaceLibrary.Comms.RaceServer Started");

        try {
            serverSocket = new ServerSocket(RACEPORT);
        } catch (IOException ex) {
            return;
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (listening) {
            try {
                Socket socket = serverSocket.accept();
                newClientThread(socket);
            } catch (IOException ex) {
                listening = false;
                //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("RaceLibrary.Comms.RaceServer Finished");

    }

    private void newClientThread(Socket socket) {

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] == null) {
                client[n] = new RaceServerThread(socket,this,n);
                client[n].start();
                return;
            }
        }
        System.out.println("RaceLibrary.Comms.RaceServer --- No Clients Threads Available");
    }

    public void clientFinished(int n) {
        if (n < 0 || n > RACE_MAX_CLIENTS) return;
        client[n] = null;
    }

    public synchronized boolean updatedRequested() {
        boolean tmp = false;

        // if (DEBUG) System.out.println("RaceServer - updatedRequested() called");

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                if (client[n].getUpdateRequested()) tmp = true;
            }
        }
        return tmp;
    }

    public synchronized void sendDisplayCommand(int cmd) {

        String msg = raceProtocol.getCmdString(cmd);
        if (DEBUG) System.out.println("RaceServer - sendDisplayCommand() : " + msg);
        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                client[n].sendMessage(msg);
            }
        }
    }

    public synchronized void sendTitleData(int record,String title) {

        String msg = raceProtocol.getTitleString(record,title);

        //if (DEBUG) System.out.println("RaceServer - sendTitleData() : " + msg);

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                //if (DEBUG) System.out.println("RaceServer - client[n] n=" + n);
                client[n].sendMessage(msg);
            }
        }
        //if (DEBUG) System.out.println("RaceServer - sendTitleData() - finished ");
    }

    public synchronized void sendRosterData(int record,String name,int carid,double weight,int pass) {

        String msg = raceProtocol.getRosterString(record,name,carid,weight,pass);

        //if (DEBUG) System.out.println("RaceServer - sendRosterData() : " + msg);

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                //if (DEBUG) System.out.println("RaceServer - client[n] n=" + n);
                client[n].sendMessage(msg);
            }
        }
        //if (DEBUG) System.out.println("RaceServer - sendRosterData() - finished ");
    }

    public synchronized void sendScheduleData(int record,int heat,int lanes,String name[],int carid[]) {

        String msg = raceProtocol.getScheduleString(record,heat,lanes,name,carid);

        //if (DEBUG) System.out.println("RaceServer - sendScheduleData() : " + msg);

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                //if (DEBUG) System.out.println("RaceServer - client[n] n=" + n);
                client[n].sendMessage(msg);
            }
        }
        //if (DEBUG) System.out.println("RaceServer - sendScheduleData() - finished ");
    }

    public synchronized void sendRaceData(int record,int heat,int heats,int lane,String first,String last,int carid,int place,double time,double speed) {

        String msg = raceProtocol.getRaceString(record,heat,heats,lane,first,last,carid,place,time,speed);

        //if (DEBUG) System.out.println("RaceServer - sendRaceData() : " + msg);

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                //if (DEBUG) System.out.println("RaceServer - client[n] n=" + n);
                client[n].sendMessage(msg);
            }
        }
        //if (DEBUG) System.out.println("RaceServer - sendRaceData() - finished ");
    }

    public synchronized void sendResultsData(int record,int place,String name,int carid,int points,double avgTime,double bestTime) {

        String msg = raceProtocol.getResultsString(record,place,name,carid,points,avgTime,bestTime);

        //if (DEBUG) System.out.println("RaceServer - sendResultsData() : " + msg);

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                //if (DEBUG) System.out.println("RaceServer - client[n] n=" + n);
                client[n].sendMessage(msg);
            }
        }
        //if (DEBUG) System.out.println("RaceServer - sendRaceData() - finished ");
    }

    public synchronized void sendStartingGate(int status) {
        if (status == statusStartingGate) return;
        
        statusStartingGate = status;
        String msg = raceProtocol.getStartingGateString(status);

        //if (DEBUG) System.out.println("RaceServer - sendResultsData() : " + msg);

        for (int n=0;n<RACE_MAX_CLIENTS;n++) {
            if (client[n] != null) {
                //if (DEBUG) System.out.println("RaceServer - client[n] n=" + n);
                client[n].sendMessage(msg);
            }
        }

    }

}
