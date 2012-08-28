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
public class RaceServerThread extends Thread {
    private static final boolean DEBUG = true;

    private Socket socket = null;
    private RaceServer server;
    private int id;

    private boolean updateRequested;

    RaceProtocol raceProtocol;

    PrintWriter out;
    BufferedReader in;

    public RaceServerThread(Socket socket,RaceServer server,int id) {
	//super("ServerThread");
	this.socket     = socket;
        this.server     = server;
        this.id         = id;

        out             = null;

        updateRequested = true;
        raceProtocol = new RaceProtocol();
    }

    @Override
    public void run() {

        if (DEBUG) System.out.println("RaceLibrary.Comms.RaceServerThread Started   ID="+String.valueOf(id));
        
        try {
	    out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	    String inputLine;

	    while ((inputLine = in.readLine()) != null) {
                if (DEBUG) System.out.println(inputLine);
                raceProtocol.parseInput(inputLine);

                int cmd = raceProtocol.getLastCmd();
                switch (cmd) {
                    default :
                        break;
                    case RaceProtocol.PAGE_UPDATE :
                        updateRequested = true;
                        break;
                }
                
	    }
	    out.close();
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    //e.printStackTrace();
	}
        socket = null;
        out    = null;
        if (DEBUG) System.out.println("RaceLibrary.Comms.RaceServerThread Finished");
        server.clientFinished(id);
    }

    public synchronized void sendMessage(String msg) {
        if (out==null) return;

        out.println(msg);
    }

    public synchronized int getUnitType() {
        return raceProtocol.getUnitType();
    }

    public synchronized boolean getUpdateRequested() {
        boolean tmp = updateRequested;
        updateRequested = false;
        return tmp;
    }

}
