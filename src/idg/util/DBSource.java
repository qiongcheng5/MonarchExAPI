package idg.util;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBSource extends Thread{

    static private DBSource instance = new DBSource(); 
    static private int clients;

    static private String default_db_name = ConfigFile.getProperty(Common.Monarch_DTO_DataScheme_ItemName_in_Configuration);

    private Vector drivers = new Vector();
    private Vector services = new Vector();
    private PrintWriter log;
    private Hashtable pools = new Hashtable();


    public static void main(String argv[]) {
        DBSource dbm = DBSource.getInstance();
        while (true) {
            System.out.print(dbm.getclients());
            try{sleep(500);}
            catch(Exception e){;};
        }
    }

    public int getclients() { return clients;};

    static  public DBSource getInstance() {
        clients++;
        return instance;
    }

    private DBSource() {
        init();
    }

    public void freeConnection(Connection con) {
        freeConnection(default_db_name,con);
    }

    public void freeConnection(String name, Connection con) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            pool.freeConnection(con);
        }
    }

    public Connection getConnection() {
        System.out.println("dbname = " + default_db_name);
        return getConnection(default_db_name);
    }

    public Connection getConnection(String name) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            log("Get connection:\'" + name + "\' succeed!");

            return pool.getConnection();
        }
        System.out.println("Get connection:\'" + name + "\' failed");
        log("Get connection:\'" + name + "\'failed");
        return null;
    }

    public Connection getConnection(String name, long time) {
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            return pool.getConnection(time);
        }

        return null;
    }

    public synchronized void release() {
        --clients;
        if (clients != 0) {
            return;
        }
        Enumeration allPools = pools.elements();
        while (allPools.hasMoreElements()) {
            DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
            pool.release();
        }
        Enumeration allDrivers = drivers.elements();
        while (allDrivers.hasMoreElements()) {
            Driver driver = (Driver) allDrivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log("unregister:" + driver.getClass().getName());
            }
            catch (SQLException e) {
                log(e, "unregister failed:" + driver.getClass().getName());
            }
        }
    }

    public synchronized void clearall() {
        clients = 0;
        release();
    }

    private void createPools(Properties props) {
        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            if (name.endsWith(".url")) {
                String poolName = name.substring(0, name.lastIndexOf("."));
                String url = props.getProperty(poolName + ".url");
                if (url == null) {
                    log("bad URL for:" + poolName);
                    continue;
                }
                String user = props.getProperty(poolName + ".user");
                String password = props.getProperty(poolName + ".password");
                String maxconn = props.getProperty(poolName + ".maxconn", "0");

                int max;
                try {
                    max = Integer.valueOf(maxconn).intValue();
                }
                catch (NumberFormatException e) {
                    log("bad max number: \"" + maxconn + " \" for: " + poolName);
                    max = 0;
                }
                DBConnectionPool pool =
                        new DBConnectionPool(poolName, url, user, password, max);
                pools.put(poolName, pool);
                System.out.println("pool created:" + poolName +  ", max=" + String.valueOf(max) + ", user=" + user + ", password=" + password + " url=" + url);
                log("pool created:" + poolName +  ":" + String.valueOf(max));
            }
        }
    }

    private void init() {

        InputStream is;
        is = getClass().getResourceAsStream("db.config");
        if (is == null){
            String confPath = idg.util.ConfigFile.getBaseDir() + "/config.properties";
            try{
                    is =  new FileInputStream(confPath);
            }
            catch(FileNotFoundException e){}
        }

        Properties dbProps = new Properties();
        try {
            dbProps.load(is);
        }
        catch (Exception e) {
            System.err.println("read property file failed " +
                    "check config.properties");
            return;
        }

        String logFile = dbProps.getProperty("logfile", "DBSource.log");//util.ConfigFile.getProperty("logfile", "DBSource.log");//dbProps.getProperty("logfile", "DBSource.log");

        try {
            log = new PrintWriter(new FileWriter(logFile, true), true);
        }
        catch (IOException e) {
            System.err.println("open log file failed: " + logFile);
            log = new PrintWriter(System.err);
        }
        //default_db_name = loadServer(dbProps);
        loadServers(dbProps);
        loadDrivers(dbProps);
        createPools(dbProps);
    }

    private String loadServer(Properties props) {
        String serverClasses = props.getProperty("Service");
        StringTokenizer st = new StringTokenizer(serverClasses);
        while (st.hasMoreElements()) {
            String service = st.nextToken().trim();
            System.out.println("service=" + service);
            return service;
        }
        return null;
    }
    
    private void loadServers(Properties props) {
        String serverClasses = props.getProperty("Service");
        StringTokenizer st = new StringTokenizer(serverClasses);
        while (st.hasMoreElements()) {
            String service = st.nextToken().trim();
            System.out.println("service=" + service);
            this.services.addElement(service);
        }
    }

    private void loadDrivers(Properties props) {
        String driverClasses = props.getProperty("drivers");
        StringTokenizer st = new StringTokenizer(driverClasses);
        while (st.hasMoreElements()) {
            String driverClassName = st.nextToken().trim();
            try {
                Driver driver = (Driver)
                Class.forName(driverClassName).newInstance();
                DriverManager.registerDriver(driver);
                drivers.addElement(driver);
                log("register Driver " + driverClassName);
            }
            catch (Exception e) {
                log("register driver failed: " +
                driverClassName + ", error: " + e);
            }
        }
    }

    private void log(String msg) {
        log.println(new Date() + ": " + msg);
    }


    private void log(Throwable e, String msg) {
        log.println(new Date() + ": " + msg);
        e.printStackTrace(log);
    }

    class DBConnectionPool {
        private int checkedOut;
        private Vector freeConnections = new Vector();
        private int maxConn;
        private String name;
        private String password;
        private String URL;
        private String user;

        public DBConnectionPool(String name, String URL, String user, String password,
        int maxConn) {
            this.name = name;
            this.URL = URL;
            this.user = user;
            this.password = password;
            this.maxConn = maxConn;
        }

        public synchronized void freeConnection(Connection con) {
            if ( con != null){
                freeConnections.addElement(con);
                checkedOut--;
                notifyAll();
            }
        }

        public synchronized Connection getConnection() {
            Connection con = null;
            if (freeConnections.size() > 0){
                con = (Connection) freeConnections.firstElement();
                freeConnections.removeElementAt(0);
                try{
                    if (con.isClosed()){
                        log("delete a invalid connection from :" + name);
                        con = getConnection();
                     }
                }
                catch (SQLException e) {
                    log("Delete an Invalid Connection From:" + name);
                    con = getConnection();
                }
            }
            else if (maxConn == 0 || checkedOut < maxConn){
                con = newConnection();
            }
            if (con != null){
                checkedOut++;
            }
            return con;
    }

        public synchronized Connection getConnection(long timeout) {
            long startTime = new Date().getTime();
            Connection con;
            while ((con = getConnection()) == null) {
                try {
                    wait(timeout);
                }
                catch (InterruptedException e) {}
                if ((new Date().getTime() - startTime) >= timeout) {
                    return null;
                }
            }
            return con;
        }

        public synchronized void release() {
            Enumeration allConnections = freeConnections.elements();
            while (allConnections.hasMoreElements()) {
                Connection con = (Connection) allConnections.nextElement();
                try {
                    con.close();
                    log("Close a Connection From:" + name);
                }
                catch (SQLException e) {
                    log(e, "Close Connection failed from:" + name);
                }
            }
            freeConnections.removeAllElements();
        }

        private Connection newConnection() {
            Connection con = null;
            try {
                if (user == null) {
                    System.out.println("url="+URL);
                    con = DriverManager.getConnection(URL);
                }
                else {
                    con = DriverManager.getConnection(URL, user, password);
                }
                log("Create a Connection From:" + name);
            }
            catch (SQLException e) {
                e.printStackTrace();
                log(e, "Create Connection Failed From: " + URL);
                return null;
            }
            return con;
        }
    }
}

