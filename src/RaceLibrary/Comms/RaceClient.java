/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RaceLibrary.Comms;

import java.net.*;
import java.io.*;

/**
 *
 * @author Brian
 */
public class RaceClient extends Thread {

    private static final boolean DEBUG = false;

    private Socket socket = null;

    protected String        hostIP;
    protected int           hostPort;

    private   PrintWriter       out;
    private   BufferedReader    in;

    protected RaceProtocol      raceProtocol;

    public RaceClient() {

        hostIP = null;
        hostPort = RaceServer.RACEPORT;

        raceProtocol = new RaceProtocol();

    }

    public synchronized void setAddress(String ip) {
        hostIP = ip;
        closeSocket();
    }

    public synchronized int getCurrentCmd() {
        return raceProtocol.getLastCmd();
    }

    public synchronized String getRaceTitle(int r) {
        return raceProtocol.getRaceTitle(r);
    }

    public synchronized String getStartingGate() {
        return raceProtocol.getStartingGate();
    }

    public synchronized String getRosterData(int r,int c) {
        return raceProtocol.getRosterData(r, c);
    }

    public synchronized int getRosterDataInt(int r,int c) {
        int value=0;
        try {
            value = Integer.valueOf(raceProtocol.getRosterData(r, c));
        }
        catch (Exception ex) {

        }
        return value;
    }

    public synchronized int getNRoster() {
        return raceProtocol.getNRoster();
    }


    public synchronized String getScheduleData(int r,int c) {
        return raceProtocol.getScheduleData(r, c);
    }

    public synchronized int getScheduleDataInt(int r,int c) {
        int value=0;
        try {
            value = Integer.valueOf(raceProtocol.getScheduleData(r, c));
        }
        catch (Exception ex) {

        }
        return value;
    }

    public synchronized int getNSchedule() {
        return raceProtocol.getNSchedule();
    }


    public synchronized String getRaceData(int r,int c) {
        return raceProtocol.getRaceData(r, c);
    }

    public synchronized int getRaceDataInt(int r,int c) {
        int value=0;
        try {
            value = Integer.valueOf(raceProtocol.getRaceData(r, c));
        }
        catch (Exception ex) {
            value = 0;
        }
        return value;
    }

    public synchronized int getNRace() {
        return raceProtocol.getNRace();
    }

    public synchronized int getRaceStatus() {
        return raceProtocol.getRaceStatus();
    }

    public synchronized String getResultsData(int r,int c) {
        return raceProtocol.getResultsData(r, c);
    }

    public synchronized int getNResults() {
        return raceProtocol.getNResults();
    }


    @Override
    public void run() {

        System.out.println("RaceLibrary.Comms.RaceClient Started");

        boolean loop = true;
        while (loop) {

            if (socket==null) {
                openSocket();
            }
            else {
                readSocket();
            }

            try {
                sleep(50);
            } catch (InterruptedException ex) {
            }
        }

        System.out.println("RaceLibrary.Comms.RaceClient Stopped");

    }

    protected void openSocket() {
        if (hostIP == null) return;

        try {
            socket = new Socket(hostIP, hostPort);
        } catch (UnknownHostException ex) {
            socket = null;
        } catch (IOException ex) {
            socket = null;
        }
        if (socket == null) return;

        System.out.println("RaceClient - Socket Open  IP:"+hostIP);

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException ex) {

        }


    }

    protected void closeSocket() {

        //raceProtocol.clear();

        if (socket==null) return;

        System.out.println("RaceClient - Socket Closed  IP:"+hostIP);
        try {
            out.close();
            in.close();
            socket.close();

            socket = null;
            out    = null;
            in     = null;
        }
        catch (IOException ex) {
        }
     }

    protected void readSocket() {
        try {
	    String inputLine;

            //raceProtocol.clear();

            inputLine = in.readLine();
            if (inputLine == null) {
                closeSocket();      // error - socket close
                return;
            }

            //if (DEBUG) System.out.println("RaceClient - input =" + inputLine);
            raceProtocol.parseInput(inputLine);
	} catch (IOException e) {
            System.out.println("RaceClient - Socket Read Error");
	    // Socket Error - close and reopen
            closeSocket();
	}
    }

    protected synchronized void sendOutput(String outLine) {
        if (socket == null) return;

        out.println(outLine);
    }

}
