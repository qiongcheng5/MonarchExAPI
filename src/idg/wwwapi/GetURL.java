/*
 * GetURL.java
 *
 *
 */

package idg.wwwapi;

/**
 *
 * @author Qiong Cheng
 */
import java.io.*;
import java.net.*;
import java.util.Vector;

/** This class does HTTP GET and writes the retrieved content to a local file */
public class GetURL {

    static final String FS = File.separator;
    
    /** This method does the actual GET   */
    public void get(String theUrl, String filename) throws IOException
    {
        try {
            URL gotoUrl = new URL(theUrl);
            InputStreamReader isr = new InputStreamReader(gotoUrl.openStream());
            BufferedReader in = new BufferedReader(isr);

            StringBuffer sb = new StringBuffer();
            String inputLine;
            boolean isFirst = true;
            
            //grab the contents at the URL
            while ((inputLine = in.readLine()) != null){
                sb.append(inputLine+"\r\n");
            }
            //write it locally
            createAFile(filename, sb.toString());
        }
        catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }

    //creates a local file
    
    public static void createAFile(String outfile, String content) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(outfile);
        DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);
        dataoutputstream.writeBytes(content);
        dataoutputstream.flush();
        dataoutputstream.close();
    }
}
