package GUI.RMS;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.microedition.rms.RecordComparator;

/** RMS Comparator*/
public class ServerlistRSComparator implements RecordComparator{
    
	private ByteArrayInputStream stream;
    private DataInputStream reader;

    public int compare(byte[] rec1, byte[] rec2) {
        long d1 = 0, d2 = 0;

        try {
            // Compares records, sorts them
            // in descending order

            //Retrieve ExpenseDate from the first record
            stream = new ByteArrayInputStream(rec1);
            reader = new DataInputStream(stream);
            //Get our date from the byte stream
            //in "number of milliseconds in epoch" format
            d1 = reader.readLong();

            //Retrieve ExpenseDate from the second record
            stream = new ByteArrayInputStream(rec2);
            reader = new DataInputStream(stream);
            //Get our date from the byte stream
            d2 = reader.readLong();
        }
        catch (Exception e) {
            // Debug
            System.out.println("RSComparator Exception:" + e.toString());
        }
        finally {
            if (d1 == d2) {
                return RecordComparator.EQUIVALENT;
            } 
            else if (d1 > d2) {
                return RecordComparator.PRECEDES;
            } 
            else {
                return RecordComparator.FOLLOWS;
            }
        }
    }
}