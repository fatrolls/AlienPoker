package server;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import settings.PokerSettings;

public class DbConnectionPool
{
  public static PoolingDataSource dataSource = null;

  public static void main(String[] args)
  {
    System.out.println("Loading underlying JDBC driver.");
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    System.out.println("Done.");

    System.out.println("Setting up driver.");
    try {
      setupDriver(args[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Done.");

    Connection conn = null;
    Statement stmt = null;
    ResultSet rset = null;
    try
    {
      System.out.println("Creating connection.");
      conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
      System.out.println("Creating statement.");
      stmt = conn.createStatement();
      System.out.println("Executing statement.");
      rset = stmt.executeQuery(args[1]);
      System.out.println("Results:");
      int numcols = rset.getMetaData().getColumnCount();
      while (rset.next()) {
        for (int i = 1; i <= numcols; i++) {
          System.out.print("\t" + rset.getString(i));
        }
        System.out.println("");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        rset.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        stmt.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try
    {
      printDriverStats();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try
    {
      shutdownDriver();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Connection getDbConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public static void init()
    throws Exception
  {
    String driverClassName = PokerSettings.getString("jdbsdriver");
    String connUrl = PokerSettings.getString("connurl");

    Class.forName(driverClassName);

    setupDriver(connUrl);
  }

  public static GenericObjectPool.Config loadConfig()
    throws Exception
  {
    GenericObjectPool.Config config = new GenericObjectPool.Config();

    maxActive = Integer.parseInt(PokerSettings.getString("pool.maxActive"));
    maxIdle = Integer.parseInt(PokerSettings.getString("pool.maxIdle"));
    maxWait = Long.parseLong(PokerSettings.getString("pool.maxWait"));

    minEvictableIdleTimeMillis = Long.parseLong(PokerSettings.getString("pool.minEvictableIdleTimeMillis"));
    minIdle = Integer.parseInt(PokerSettings.getString("pool.minIdle"));
    numTestsPerEvictionRun = Integer.parseInt(PokerSettings.getString("pool.numTestsPerEvictionRun"));
    softMinEvictableIdleTimeMillis = Long.parseLong(PokerSettings.getString("pool.softMinEvictableIdleTimeMillis"));
    testOnBorrow = Boolean.getBoolean(PokerSettings.getString("pool.testOnBorrow"));
    testOnReturn = Boolean.getBoolean(PokerSettings.getString("pool.testOnReturn"));
    testWhileIdle = Boolean.getBoolean(PokerSettings.getString("pool.testWhileIdle"));
    timeBetweenEvictionRunsMillis = Long.parseLong(PokerSettings.getString("pool.timeBetweenEvictionRunsMillis"));
    whenExhaustedAction = Byte.parseByte(PokerSettings.getString("pool.whenExhaustedAction"));

    return config;
  }

  public static void setupDriver(String connectURI)
    throws Exception
  {
    GenericObjectPool connectionPool = new GenericObjectPool(null, loadConfig());
    connectionPool.setWhenExhaustedAction(2);

    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);

    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

    dataSource = new PoolingDataSource(connectionPool);
  }

  public static void closeConnection(Connection con)
  {
    if (con != null)
      try {
        con.close();
      }
      catch (SQLException e)
      {
      }
  }

  public static void printDriverStats() throws Exception
  {
    PoolingDriver driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
    ObjectPool connectionPool = driver.getConnectionPool("example");

    System.out.println("NumActive: " + connectionPool.getNumActive());
    System.out.println("NumIdle: " + connectionPool.getNumIdle());
  }

  public static void shutdownDriver() throws Exception {
    PoolingDriver driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
    driver.closePool("example");
  }
}