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
public class CommFX3U extends Thread {

    private static final boolean DEBUG = false;

    boolean     portOpen;
    private     Socket socket = null;

    String      hostIP;
    int         hostPort;
    byte        bufferInput[];

    private   OutputStream      out;
    private   InputStream       in;
    
    
    public CommFX3U() {
        hostIP = "10.10.10.10";
        hostPort = 5551;
        portOpen = false;
        
        bufferInput = new byte[2048];
        
    }

    public synchronized void setIP(String ip) {
        hostIP = ip;
        
        closeSocket();
    }

    public synchronized void open() {
        if (socket != null) closeSocket();
        openSocket();
    }
    
    public synchronized void close() {
        closeSocket();
    }
    
    @Override
    public void run() {

        System.out.println("RaceLibrary.Comms.CommFX3U Started");

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

        System.out.println("RaceLibrary.Comms.CommFX3U Stopped");

    }

    public synchronized int[] read(String address,int count) {
        int data[] = new int[count];

        if (socket == null) return null;

        String hexAddress = convertAddressFX3U(address);

        byte oBuffer[] = new byte[128];

        oBuffer[ 0] = 0x02;
        oBuffer[ 1] = 'E';
        oBuffer[ 2] = '0';

        oBuffer[ 3] = (byte) hexAddress.charAt(0);
        oBuffer[ 4] = (byte) hexAddress.charAt(1);
        oBuffer[ 5] = (byte) hexAddress.charAt(2);
        oBuffer[ 6] = (byte) hexAddress.charAt(3);
        oBuffer[ 7] = (byte) hexAddress.charAt(4);

        String scount = String.format("%1$04X", count*2);
        oBuffer[ 8] = (byte) scount.charAt(2);
        oBuffer[ 9] = (byte) scount.charAt(3);

        oBuffer[10] = 0x03;

        int sum = 0;
        for (int i=1;i<=10;i++) {
            sum += (int) oBuffer[i];
        }
        String ssum = String.format("%1$06X", sum);
        ssum = ssum.toUpperCase();
        int len = ssum.length();
        oBuffer[11] = (byte) ssum.charAt(len-2);
        oBuffer[12] = (byte) ssum.charAt(len-1);
        sendOutput(oBuffer,13);

        int msgReply = 4 + count * 4;

        int ndata = readInput(msgReply);
        if (ndata <= 0) return null;

        if (DEBUG) System.out.println("CommFX3U - readInput bytes = " + ndata);

        int offset = 0;
        for (int n=0;n<count;n++) {
            offset = 1 + n * 4;
            char cvalue[] = new char[4];
            cvalue[0] = (char) bufferInput[offset + 2];
            cvalue[1] = (char) bufferInput[offset + 3];
            cvalue[2] = (char) bufferInput[offset + 0];
            cvalue[3] = (char) bufferInput[offset + 1];
            String value = new String(cvalue);
            //System.out.println(" n = " + n + "  string = " + value);
            data[n] = parseHex(value);
        }

        return data;
    }
    
    protected int readInput(int msgLen) {

        int nbytes   = 0;
        int timeout = 2000;

        for (int c=0;c<50;c++) bufferInput[c] = 0;

        try {
            long starttime = System.currentTimeMillis();
            while (in.available() < msgLen) {
                long time = System.currentTimeMillis();
                if ((time - starttime) > timeout) {
                    System.out.println("CommFX3U - readInput Timeout  reqd=" + msgLen + "   available="+in.available());
                    return -2;
                }
            }
            nbytes = in.read(bufferInput);

        }
        catch (IOException ex) {
            System.out.println("CommFX3U - read input exception error");
            closeSocket();
            return -1;
        }

        return nbytes;
    }
    
    protected String convertAddressFX3U(String address) {
        String hexAddress = "00000";

        char type = address.charAt(0);
        int memStart  = 0;
        int memOffset = 0;

        switch (type) {
            case 'D' :
                memOffset = parseInt(address.substring(1));
                if (memOffset < 8000)   memStart  = 0x4000;  // D0 - D7999
                else                    memStart = 0x0e00;   // D8000
                memOffset *= 2;
                break;
            default :
                break;
        }
        int loc = memStart + memOffset;
        hexAddress = String.format("%1$05X", loc);

        //System.out.println("Address conversion " + address + " = " + hexAddress);

        return hexAddress;
    }

    protected int parseInt(String value) {
        
        int ivalue = 0;
        
        try {
            ivalue = Integer.parseInt(value);
        }
        catch (Exception ex) {
            ivalue = 0;
        }
        
        return ivalue;
    }

    protected int parseHex(String value) {

        int ivalue = 0;

        try {
            ivalue = Integer.valueOf(value, 16).intValue();;
        }
        catch (Exception ex) {
            ivalue = 0;
        }

        return ivalue;
    }
    
    
    
    protected void openSocket() {
        
        
        if (hostIP == null) return;

        try {
            socket = new Socket(hostIP,hostPort);
        } catch (IOException ex) {
            socket = null;
        }
        if (socket == null) return;

        if (DEBUG) System.out.println("CommFX3U - Socket Open  IP:"+hostIP);

        try {
            out = socket.getOutputStream();
            in  = socket.getInputStream();
        }
        catch (IOException ex) {
            if (DEBUG) System.out.println("CommFX3U - Streams Error  IP:"+hostIP);
        }

    }

    protected void closeSocket() {

        //raceProtocol.clear();

        if (socket==null) return;

        if (DEBUG) System.out.println("CommFX3U - Socket Closed  IP:"+hostIP);
        try {
            //out.close();
            //in.close();
            socket.close();

            socket = null;
            out    = null;
            in     = null;
        }
        catch (IOException ex) {
        }
     }

    protected void readSocket() {
        byte iBuffer[] = new byte[256];
        int  length = 256;
        int  count = 0;
        try {

            count = in.read(iBuffer);
            
            if (DEBUG) {
                System.out.print("CommFX3U - readSocket : count = " + count + " :");
                for (int n=0;n<count;n++) {
                    System.out.print( " " + Integer.toHexString(iBuffer[n] & 0xff));
                }
                System.out.println();
            }
	} catch (IOException e) {
	    // Socket Error - close and reopen
            // closeSocket();
	}
    }

    protected synchronized void sendOutput(byte outBuffer[],int length) {
        if (socket == null) return;

        try {
            //if (DEBUG) System.out.println("CommFX3U - Output Write - Bytes : " + length);            
            if (DEBUG) {
                System.out.print("CommFX3U - sendOutput : count = " + length + " :");
                for (int n=0;n<length;n++) {
                    System.out.print( " " + Integer.toHexString(outBuffer[n] & 0xff));
                }
                
                System.out.println();
            }
            out.write(outBuffer,0,length);
	} catch (IOException e) {
            if (DEBUG) System.out.println("CommFX3U - Output Write Error");
	    // Socket Error - close and reopen
	}
    }
    
    
    
}
