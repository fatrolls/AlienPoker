package server;

import adminprocessors.AdminAddDeskCommandProcessor;
import adminprocessors.AdminAddTeamTournamentCommandProcessor;
import adminprocessors.AdminAddTournamentCommandProcessor;
import adminprocessors.AdminBlockPlayerCommandProcessor;
import adminprocessors.AdminDeleteDeskCommandProcessor;
import adminprocessors.AdminDeleteTournamentCommandProcessor;
import adminprocessors.AdminDeskPlayersCommandProcessor;
import adminprocessors.AdminDesksCommandProcessor;
import adminprocessors.AdminGetTotalStatsCommandProcessor;
import adminprocessors.AdminGlobalMessageCommandProcessor;
import adminprocessors.AdminIncreaseAmountCommandProcessor;
import adminprocessors.AdminPlayerCommandProcessor;
import adminprocessors.AdminPlayersCommandProcessor;
import adminprocessors.AdminTournamentInfoCommandProcessor;
import adminprocessors.AdminTournamentsCommandProcessor;
import adminprocessors.AdminUpdateProfileCommandProcessor;
import java.io.PrintStream;
import org.apache.xmlrpc.WebServer;

public class AdminServer
{
  public static int PORT;

  public static void startAdminSocketServer()
  {
    WebServer server = new WebServer(PORT);

    server.addHandler("AdminDeskPlayersCommandProcessor", new AdminDeskPlayersCommandProcessor());
    server.addHandler("AdminAddDeskCommandProcessor", new AdminAddDeskCommandProcessor());
    server.addHandler("AdminDeleteDeskCommandProcessor", new AdminDeleteDeskCommandProcessor());
    server.addHandler("AdminDesksCommandProcessor", new AdminDesksCommandProcessor());
    server.addHandler("AdminPlayersCommandProcessor", new AdminPlayersCommandProcessor());
    server.addHandler("AdminBlockPlayerCommandProcessor", new AdminBlockPlayerCommandProcessor());
    server.addHandler("AdminGetTotalStatsCommandProcessor", new AdminGetTotalStatsCommandProcessor());
    server.addHandler("AdminPlayerCommandProcessor", new AdminPlayerCommandProcessor());
    server.addHandler("AdminIncreaseAmountCommandProcessor", new AdminIncreaseAmountCommandProcessor());
    server.addHandler("AdminUpdateProfileCommandProcessor", new AdminUpdateProfileCommandProcessor());

    server.addHandler("AdminTournamentsCommandProcessor", new AdminTournamentsCommandProcessor());
    server.addHandler("AdminTournamentInfoCommandProcessor", new AdminTournamentInfoCommandProcessor());
    server.addHandler("AdminDeleteTournamentCommandProcessor", new AdminDeleteTournamentCommandProcessor());
    server.addHandler("AdminAddTournamentCommandProcessor", new AdminAddTournamentCommandProcessor());
    server.addHandler("AdminAddTeamTournamentCommandProcessor", new AdminAddTeamTournamentCommandProcessor());
    server.addHandler("AdminGlobalMessageCommandProcessor", new AdminGlobalMessageCommandProcessor());

    server.start();
    System.out.println("Admin Server started...");
  }

  public static void setServerPort(int port)
  {
    PORT = port;
  }
}