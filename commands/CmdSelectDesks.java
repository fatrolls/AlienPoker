package commands;

import game.Desk;
import game.Game;
import game.PlacesList;
import game.speed.GameSpeed;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import server.XMLFormatable;
import tournaments.Tournament;
import tournaments.team.TeamTournament;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class CmdSelectDesks
  implements XMLFormatable
{
  public static final int FILTER_ANNOUNCED = 1;
  public static final int FILTER_REGISTERING = 2;
  public static final int FILTER_RUNNING = 4;
  public static final int FILTER_FINISHED = 8;
  private boolean onlyPrivate = false;

  public int NOT_A_TOURNAMENT = 0;

  private ArrayList desks = null;
  private ArrayList selected = new ArrayList();

  private ArrayList pokerTypes = new ArrayList();
  private ArrayList limitTypes = new ArrayList();
  private ArrayList moneyTypes = new ArrayList();
  private ArrayList speedTypes = new ArrayList();

  private ArrayList emptyTypes = new ArrayList();

  private int tournament = NOT_A_TOURNAMENT;
  private int tournamentSubType = 1;

  private ArrayList tournamentStatus = new ArrayList();
  public static final String OUT_PARAM_DESKS = "DESKS";
  public static final String OUT_PARAM_COUNT = "COUNT";
  private static final String OUT_PARAM_TOURNAMENTS = "TOURNAMENTS";
  private static final String OUT_TAG_TOURNAMENT = "TOURNAMENT";
  private static final String OUT_PARAM_ID = "TID";
  private static final String OUT_PARAM_NAME = "NAME";
  private static final String OUT_PARAM_BUY_IN = "BUYIN";
  private static final String OUT_PARAM_RAKE = "RAKE";
  private static final String OUT_PARAM_BUY_IN_NUMBER = "BUYIND";
  private static final String OUT_PARAM_STATUS = "STATUS";
  private static final String OUT_PARAM_DESK = "ID";
  private static final String OUT_PARAM_STATUS_NUMBER = "STATUSD";
  private static final String OUT_PARAM_STARTS = "STARTS";

  public void finish()
  {
    selected.clear();
    pokerTypes.clear();
    limitTypes.clear();
    moneyTypes.clear();
    speedTypes.clear();
    emptyTypes.clear();
    tournamentStatus.clear();
  }

  public int getCount() {
    return selected.size();
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("pokerTypes :");
    for (int i = 0; i < pokerTypes.size(); i++) {
      buffer.append(pokerTypes.get(i).toString()).append("|");
    }
    buffer.append(" limitTypes :");
    for (int i = 0; i < limitTypes.size(); i++) {
      buffer.append(limitTypes.get(i).toString()).append("|");
    }
    buffer.append(" moneyTypes :");
    for (int i = 0; i < moneyTypes.size(); i++) {
      buffer.append(moneyTypes.get(i).toString()).append("|");
    }
    buffer.append(" speedTypes :");
    for (int i = 0; i < speedTypes.size(); i++) {
      buffer.append(speedTypes.get(i).toString()).append("|");
    }
    buffer.append(" emptyTypes :");
    for (int i = 0; i < emptyTypes.size(); i++) {
      buffer.append(emptyTypes.get(i).toString()).append("|");
    }
    buffer.append(" tournamentStatus :");
    for (int i = 0; i < tournamentStatus.size(); i++) {
      buffer.append(tournamentStatus.get(i).toString()).append("|");
    }

    return buffer.toString();
  }

  public int select()
  {
    selected.clear();
    int count = 0;

    int pokerTypeSize = pokerTypes.size();
    int limitTypeSize = limitTypes.size();
    int moneyTypeSize = moneyTypes.size();
    int speedTypeSize = speedTypes.size();
    int emptyTypeSize = emptyTypes.size();

    Iterator iter = desks.iterator();
    while (iter.hasNext()) {
      Desk desk = (Desk)iter.next();
      if (desk.isPrivateDesk() != onlyPrivate)
        continue;
      boolean canAdd;
      if (pokerTypeSize > 0) {
        boolean canAdd = false;
        for (int i = 0; i < pokerTypeSize; i++) {
          Integer pokerType = (Integer)pokerTypes.get(i);
          if (pokerType.intValue() == desk.getPokerType()) {
            canAdd = true;
            break;
          }

        }

        if (canAdd) {
          if (limitTypeSize > 0) {
            canAdd = false;
            for (int i = 0; i < limitTypeSize; i++) {
              Integer limitType = (Integer)limitTypes.get(i);
              if (limitType.intValue() == desk.getLimitType()) {
                canAdd = true;
                break;
              }
            }
          } else {
            canAdd = true;
          }

        }

        if (canAdd) {
          if (moneyTypeSize > 0) {
            canAdd = false;
            for (int i = 0; i < moneyTypeSize; i++) {
              Integer moneyType = (Integer)moneyTypes.get(i);
              if (moneyType.intValue() == desk.getMoneyType()) {
                canAdd = true;
                break;
              }
            }
          } else {
            canAdd = true;
          }

        }

        if (canAdd) {
          if (speedTypeSize > 0) {
            canAdd = false;
            for (int i = 0; i < speedTypeSize; i++) {
              Integer speedType = (Integer)speedTypes.get(i);
              if (speedType.intValue() == desk.getGame().getGameSpeed().getType()) {
                canAdd = true;
                break;
              }
            }
          } else {
            canAdd = true;
          }

        }

        if (canAdd) {
          if (emptyTypeSize > 0)
          {
            int allPlacesCount = desk.getPlacesList().size();
            int currentplayerCount = desk.getPlayersCount();
            canAdd = false;

            for (int i = 0; i < emptyTypeSize; i++) {
              Integer emptyType = (Integer)emptyTypes.get(i);
              int emptyInt = emptyType.intValue();
              if ((emptyInt == 1) && (allPlacesCount - currentplayerCount == 0)) {
                canAdd = true;
                break;
              }if ((emptyInt == 0) && (currentplayerCount == 0)) {
                canAdd = true;
                break;
              }
              if ((emptyInt != 2) || (currentplayerCount <= 0) || (allPlacesCount - currentplayerCount == 0))
                continue;
              canAdd = true;
              break;
            }
          }
          else
          {
            canAdd = true;
          }
        }
      }
      else
      {
        canAdd = true;
      }

      if (canAdd) {
        selected.add(desk);
        count++;
      }

    }

    return count;
  }

  public ArrayList getSelectedDesks()
  {
    return selected;
  }

  public void addPokerType(int gameType)
  {
    pokerTypes.add(new Integer(gameType));
  }

  public void setDesks(ArrayList desks) {
    this.desks = desks;
  }

  public String toXML() throws UnsupportedEncodingException {
    if (NOT_A_TOURNAMENT == tournament)
    {
      StringBuffer desksbuff = new StringBuffer();
      Iterator it = selected.iterator();
      while (it.hasNext()) {
        Desk d = (Desk)it.next();
        desksbuff.append(d.toXML()).append('\n');
      }

      XMLDoc xmlDoc = new XMLDoc();
      XMLTag tag = xmlDoc.startTag("DESKS");
      tag.addParam("COUNT", getCount());
      tag.setTagContent(desksbuff.toString());

      String xml = xmlDoc.toString();
      xmlDoc.invalidate();

      return xml;
    }

    switch (tournament) {
    case 1:
      return multitournamentDesksToXML();
    case 5:
      return multitournamentDesksToXML();
    case 2:
      return minitournamentDesksToXML();
    case 4:
      return teamtournamentDesksToXML();
    case 3:
    }return "";
  }

  private String teamtournamentDesksToXML()
  {
    StringBuffer desksbuff = new StringBuffer();
    int count = 0;
    ArrayList tournaments = Tournament.getTournamentsByTypeAndSubType(tournament, tournamentSubType);
    Iterator it = tournaments.iterator();

    while (it.hasNext()) {
      TeamTournament t = (TeamTournament)it.next();
      if (t != null)
      {
        boolean skip = true;
        if (tournamentStatus.size() > 0) {
          for (int i = 0; (i < tournamentStatus.size()) && (skip); i++) {
            int selStatus = ((Integer)tournamentStatus.get(i)).intValue();
            switch (selStatus) {
            case 1:
              if (t.getStatus() != 0) continue;
              skip = false; break;
            case 2:
              if (t.getStatus() != 1) continue;
              skip = false; break;
            case 4:
              if ((t.getStatus() == 0) || (t.getStatus() == 1) || (t.getStatus() == 3) || (t.getStatus() == 4)) continue;
              skip = false; break;
            case 8:
              if ((t.getStatus() != 3) && (t.getStatus() != 4)) continue;
              skip = false; break;
            case 3:
            case 5:
            case 6:
            case 7:
            default:
              skip = false;
            }
          }

          if (skip)
          {
            continue;
          }
        }

        XMLDoc xmlDoc = new XMLDoc();
        XMLTag tag = xmlDoc.startTag("TOURNAMENT");

        tag.addParam("TID", t.getID());
        tag.addParam("NAME", t.getName());
        tag.addParam("PTYPE", t.getGame());
        tag.addParam("LTYPE", t.getGameType());
        tag.addParam("BUYIND", t.getBuyIn().floatValue());
        tag.addParam("BUYIN", t.getBuyIn().toString());
        tag.addParam("RAKE", t.getFee().toString());
        tag.addParam("TQTY", t.getTeamsQty());
        tag.addParam("PLST", t.getPlayersInTeam());
        synchronized (t.getPlayersList()) {
          tag.addParam("PLAYERS", t.getPlayersList().size());
        }
        tag.addParam("STATUS", t.convertTournamentStatusToString(t.getStatus()));
        tag.addParam("STATUSD", t.getStatus());
        tag.addParam("STARTS", "" + t.getBeginDate().getTime());

        tag.addParam("MTYPE", t.getMoneyType());
        tag.addParam("MINBET", "3");
        tag.addParam("MAXBET", "6");
        tag.addParam("PLACES", "100");

        tag.addParam("AVG_POT", "100");
        tag.addParam("FLOP_PERCENT", "100");
        tag.addParam("HANDS_PER_HOUR", "100");
        tag.addParam("WAITING_PLAYERS", "100");
        tag.addParam("MINAMOUNT", "10");
        tag.addParam("SPEED", t.getSpeedType());

        desksbuff.append(xmlDoc.toString()).append('\n');
        xmlDoc.invalidate();
        count++;
      }

    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("TOURNAMENTS");

    tag.addParam("COUNT", count);
    tag.setTagContent(desksbuff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();
    return xml;
  }

  private String minitournamentDesksToXML()
  {
    StringBuffer desksbuff = new StringBuffer();
    int count = 0;
    ArrayList tournaments = Tournament.getTournamentsByTypeAndSubType(tournament, tournamentSubType);
    Iterator it = tournaments.iterator();

    while (it.hasNext()) {
      Tournament t = (Tournament)it.next();
      if (t != null)
      {
        boolean skip = true;
        if (tournamentStatus.size() > 0) {
          for (int i = 0; (i < tournamentStatus.size()) && (skip); i++) {
            int selStatus = ((Integer)tournamentStatus.get(i)).intValue();
            switch (selStatus) {
            case 1:
              if (t.getStatus() != 0) continue;
              skip = false; break;
            case 2:
              if ((t.getStatus() < 11) || (t.getStatus() > 20)) continue;
              skip = false; break;
            case 4:
              if (((t.getStatus() >= 11) && (t.getStatus() <= 20)) || (t.getStatus() == 0) || (t.getStatus() == 3) || (t.getStatus() == 4)) continue;
              skip = false; break;
            case 8:
              if ((t.getStatus() != 3) && (t.getStatus() != 4)) continue;
              skip = false; break;
            case 3:
            case 5:
            case 6:
            case 7:
            default:
              skip = false;
            }

          }

          if (skip)
          {
            continue;
          }
        }
        ArrayList desksList = t.getDesksList();
        Desk d = null;
        synchronized (desksList) {
          if (desksList.size() > 0) {
            d = (Desk)desksList.get(0);
          }
        }

        if (d != null) {
          XMLDoc xmlDoc = new XMLDoc();
          XMLTag tag = xmlDoc.startTag("TOURNAMENT");

          tag.addParam("TID", t.getID());
          tag.addParam("NAME", t.getName());
          tag.addParam("PTYPE", d.getPokerType());
          tag.addParam("LTYPE", d.getLimitType());
          tag.addParam("ID", d.getID());
          tag.addParam("BUYIND", t.getBuyIn().floatValue());
          tag.addParam("BUYIN", t.getBuyIn().toString());
          tag.addParam("RAKE", t.getFee().toString());
          tag.addParam("MPTS", t.getMinStartPlayersCount());
          tag.addParam("PLAYERS", d.getPlayersCount());
          tag.addParam("WAITING_PLAYERS", "0");
          tag.addParam("STATUS", t.convertTournamentStatusToString(t.getStatus()));
          tag.addParam("STATUSD", t.getStatus());
          tag.addParam("MTYPE", d.getMoneyType());
          tag.addParam("MINBET", d.getMinBet().floatValue());
          tag.addParam("MAXBET", d.getMaxBet().floatValue());
          tag.addParam("PLACES", d.getPlacesList().size());
          tag.addParam("PLAYERS", d.getPlayersCount());
          tag.addParam("AVG_POT", d.getAveragePot().floatValue());
          tag.addParam("FLOP_PERCENT", d.getFlopPercent().floatValue());
          tag.addParam("HANDS_PER_HOUR", d.getHandsPerHour());
          tag.addParam("WAITING_PLAYERS", d.getWaitingPlayersCount());
          tag.addParam("MINAMOUNT", d.getMinAmount().floatValue());
          tag.addParam("SPEED", t.getSpeedType());

          desksbuff.append(xmlDoc.toString()).append('\n');
          xmlDoc.invalidate();
          count++;
        }
      }

    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("TOURNAMENTS");

    tag.addParam("COUNT", count);
    tag.setTagContent(desksbuff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();
    return xml;
  }

  private String multitournamentDesksToXML() {
    StringBuffer desksbuff = new StringBuffer();
    int count = 0;
    ArrayList tournaments = Tournament.getTournamentsByTypeAndSubType(tournament, tournamentSubType);
    Iterator it = tournaments.iterator();

    while (it.hasNext()) {
      Tournament t = (Tournament)it.next();
      if (t != null)
      {
        boolean skip = true;
        if (tournamentStatus.size() > 0) {
          for (int i = 0; (i < tournamentStatus.size()) && (skip); i++) {
            int selStatus = ((Integer)tournamentStatus.get(i)).intValue();
            switch (selStatus) {
            case 1:
              if (t.getStatus() != 0) continue;
              skip = false; break;
            case 2:
              if (t.getStatus() != 1) continue;
              skip = false; break;
            case 4:
              if ((t.getStatus() == 0) || (t.getStatus() == 1) || (t.getStatus() == 3) || (t.getStatus() == 4)) continue;
              skip = false; break;
            case 8:
              if ((t.getStatus() != 3) && (t.getStatus() != 4)) continue;
              skip = false; break;
            case 3:
            case 5:
            case 6:
            case 7:
            default:
              skip = false;
            }
          }

          if (skip)
          {
            continue;
          }
        }

        XMLDoc xmlDoc = new XMLDoc();
        XMLTag tag = xmlDoc.startTag("TOURNAMENT");

        tag.addParam("TID", t.getID());
        tag.addParam("NAME", t.getName());
        tag.addParam("PTYPE", t.getGame());
        tag.addParam("LTYPE", t.getGameType());
        tag.addParam("BUYIND", t.getBuyIn().floatValue());
        tag.addParam("BUYIN", t.getBuyIn().toString());
        tag.addParam("RAKE", t.getFee().toString());
        tag.addParam("MPTS", t.getMinStartPlayersCount());
        tag.addParam("FRRL", t.isFreeRoll() ? 1 : 0);
        synchronized (t.getPlayersList()) {
          tag.addParam("PLAYERS", t.getPlayersList().size());
        }
        tag.addParam("STATUS", t.convertTournamentStatusToString(t.getStatus()));
        tag.addParam("STATUSD", t.getStatus());
        tag.addParam("STARTS", "" + t.getBeginDate().getTime());

        tag.addParam("MTYPE", t.getMoneyType());
        tag.addParam("MINBET", "3");
        tag.addParam("MAXBET", "6");
        tag.addParam("PLACES", "100");

        tag.addParam("AVG_POT", "100");
        tag.addParam("FLOP_PERCENT", "100");
        tag.addParam("HANDS_PER_HOUR", "100");
        tag.addParam("WAITING_PLAYERS", "100");
        tag.addParam("MINAMOUNT", "10");
        tag.addParam("SPEED", t.getSpeedType());

        desksbuff.append(xmlDoc.toString()).append('\n');
        xmlDoc.invalidate();
        count++;
      }

    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("TOURNAMENTS");

    tag.addParam("COUNT", count);
    tag.setTagContent(desksbuff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();
    return xml;
  }

  public void addLimitType(int limitType)
  {
    limitTypes.add(new Integer(limitType));
  }

  public int getTournament() {
    return tournament;
  }

  public void setTournament(int tournament) {
    this.tournament = tournament;
  }

  public int getTournamentSubType() {
    return tournamentSubType;
  }

  public void setTournamentSubType(int tournamentSubType) {
    this.tournamentSubType = tournamentSubType;
  }

  public ArrayList getTournamentStatus() {
    return tournamentStatus;
  }

  public void setTournamentStatus(ArrayList tournamentStatus) {
    this.tournamentStatus = tournamentStatus;
  }

  public void setPokerTypes(ArrayList pokerTypes) {
    this.pokerTypes = pokerTypes;
  }

  public void setLimitTypes(ArrayList limitTypes) {
    this.limitTypes = limitTypes;
  }

  public void setMoneyTypes(ArrayList moneyTypes) {
    this.moneyTypes = moneyTypes;
  }

  public void setSpeedTypes(ArrayList speedTypes) {
    this.speedTypes = speedTypes;
  }

  public void setEmptyTypes(ArrayList emptyTypes) {
    this.emptyTypes = emptyTypes;
  }

  public ArrayList getEmptyTypes() {
    return emptyTypes;
  }

  public ArrayList getSpeedTypes() {
    return speedTypes;
  }

  public ArrayList getMoneyTypes() {
    return moneyTypes;
  }

  public ArrayList getLimitTypes() {
    return limitTypes;
  }

  public ArrayList getPokerTypes() {
    return pokerTypes;
  }

  public void setOnlyPrivate(boolean onlyPrivate) {
    this.onlyPrivate = onlyPrivate;
  }
}