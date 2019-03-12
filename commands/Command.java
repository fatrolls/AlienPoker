package commands;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

public abstract class Command
{
  private Connection dbConn;

  public Connection getDbConnection()
  {
    return dbConn;
  }

  public void setDbConnection(Connection dbConn)
  {
    this.dbConn = dbConn;
  }

  public boolean execute() throws IOException
  {
    return false;
  }

  public ArrayList getList()
  {
    return null;
  }
}