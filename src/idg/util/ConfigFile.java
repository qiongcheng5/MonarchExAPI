package idg.util;

import java.io.IOException;
import java.util.Properties;


/**
 * @author Qiong Cheng
 *
 * To retrieve the configuration
 */
public class ConfigFile {
    
    private static String pBaseDir = "baseDir";
        
    private static boolean init = false;
    static Properties props = new Properties();

    private ConfigFile() {
    }

    public static void init() {
        if (init) {
            return;
        }
        init = true;
        try {
            System.out.println(ConfigFile.class.getClassLoader().toString());
            props.load(ConfigFile.class.getResourceAsStream("config.properties"));
        } catch (Exception e) {
            System.out.println("Error message: config.properties file doesnot exist.\n"+ e.getMessage()+ "\n[in ConfigFile.class]");
        }      
        String cdir = getBaseDir();
        if (idg.util.Common.isDebug) System.setProperty("user.dir", cdir); 
        
    }

    public static String getProperty(String str) {
        init();
        return props.getProperty(str);
    }

    public static String getProperty(String str, String sdefault) {
        init();
        String value = props.getProperty(str);
        if (value != null) return value;
        else return sdefault;
    }

    
    public static String getBaseDir() {
        String basedir = getProperty(pBaseDir);
        if ( basedir.endsWith("/") || basedir.endsWith("\\")){
        	basedir = basedir.substring(0, basedir.length()-1 );
        }
        return basedir;
    }
    
   
    public static boolean isLoaded(){
    	return init;
    }
    
}
