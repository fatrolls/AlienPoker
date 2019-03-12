package processors;

public class RequestCommandProcessorsFactory
{
  public static final String CMD_HELLO = "HELLO";
  public static final String CMD_UNDEFINED = "UNDEFINED";
  public static final String CMD_LOGIN = "LOGIN";
  public static final String CMD_DESKS = "DESKS";
  public static final String CMD_PRIVATEDESKS = "PRIVATEDESKS";
  public static final String CMD_QUIT = "QUIT";
  public static final String CMD_PROFILE = "PROFILE";
  public static final String CMD_JOIN = "JOIN";
  public static final String CMD_DESK = "DESK";
  public static final String CMD_LEAVEDESK = "LEAVEDESK";
  public static final String CMD_SMALLBLIND = "SBLIND";
  public static final String CMD_BIGBLIND = "BBLIND";
  public static final String CMD_CALL = "CALL";
  public static final String CMD_RAISE = "RAISE";
  public static final String CMD_CHECK = "CHECK";
  public static final String CMD_BET = "BET";
  public static final String CMD_FOLD = "FOLD";
  public static final String CMD_OWNER = "OWNER";
  public static final String CMD_CARDS = "CARDS";
  public static final String CMD_GAMERESULT = "GAMERESULT";
  public static final String CMD_SHOW_CARDS = "SHOWCARDS";
  public static final String CMD_CREATEPROFILE = "CREATEPROFILE";
  public static final String CMD_UPDATEPROFILE = "UPDATEPROFILE";
  public static final String CMD_UPDATEEMAIL = "UPDATEEMAIL";
  public static final String CMD_UPDATEPASSWORD = "UPDATEPASSWORD";
  public static final String CMD_CHATADD = "CHATADD";
  public static final String CMD_CHATTIME = "CHATTIME";
  public static final String CMD_CHATREAD = "CHATREAD";
  public static final String CMD_SITOUT = "SITOUT";
  public static final String CMD_ANTE = "ANTE";
  public static final String CMD_BRING_IN = "BRINGIN";
  public static final String CMD_RESTOREPASSWORD = "RESTOREPASSWORD";
  public static final String CMD_MOREMONEY = "MOREMONEY";
  public static final String CMD_GETPROFILE = "GETPROFILE";
  public static final String CMD_JOINWAITINGLIST = "JOINWAITINGLIST";
  public static final String CMD_CHECKWAITINGLIST = "CHECKWAITINGLIST";
  public static final String CMD_REMOVEFROMWAITINGLIST = "REMOVEFROMWAITINGLIST";
  public static final String CMD_DISCARD = "DISCARD";
  public static final String CMD_CHECKCOMBINATION = "CHECKCOMBINATION";
  public static final String CMD_POSTCARDS = "POSTCARDS";
  public static final String CMD_TOURNAMENT_INFO = "GETTOURNAMENTINFO";
  public static final String CMD_TT_INFO = "GETTINFO";
  public static final String CMD_GETTEAMPLAYERS = "GETTEAMPLAYERS";
  public static final String CMD_TOURNAMENTS_INFO = "GETTOURNAMENTSINFO";
  public static final String CMD_JOINTOURNAMENT = "JOINTOURNAMENT";
  public static final String CMD_UNJOINTOURNAMENT = "UNJOINTOURNAMENT";
  public static final String CMD_MAKETELL = "MAKETELL";
  public static final String CMD_SAVEAVATAR = "SAVEAVATAR";
  public static final String CMD_LOADAVATAR = "LOADAVATAR";
  public static final String CMD_CHECKLOCKS = "CHECKLOCKS";
  public static final String CMD_SKIPLOCKS = "SKIPLOCKS";
  public static final String CMD_PLAYER_TOURNAMENT = "GETPLAYERTOURNAMENT";
  public static final String CMD_SEARCHUSER = "SEARCHUSER";
  public static final String CMD_GETSITANDGOINFO = "GETSITANDGOINFO";
  public static final String CMD_GETTOTALSTATS = "GETTOTALSSTATS";
  public static final String CMD_BARORDER = "BARORDER";
  public static final String CMD_GETPLAYERSTATS = "GETPLAYERSTATS";
  public static final String CMD_CLEARPLAYERSTATS = "CLEARPLAYERSTATS";
  public static final String CMD_CHECKTOURNAMENTSEATS = "CHECKTOURNAMENTSEATS";
  public static final String CMD_ADDON = "ADDON";
  public static final String CMD_REBUYS = "REBUYS";
  public static final String CMD_GETSHOP = "GETSHOP";
  public static final String CMD_GETPLAYERWARDOBES = "GETPLAYERWARDOBES";
  public static final String CMD_BUYWARDOBE = "BUYWARDOBE";
  public static final String CMD_WEARWARDOBE = "WEARWARDOBE";
  public static final String CMD_UNWEARWARDOBE = "UNWEARWARDOBE";
  public static final String CMD_WHATISWEARED = "WHATISWEARED";
  public static final String CMD_CREATE_PRIVATE_DESK = "CREATEPRIVATEDESK";
  public static final String CMD_SAVENOTE = "SAVENOTE";
  public static final String CMD_SAVERATING = "SAVERATING";
  public static final String CMD_GETNOTE = "GETNOTE";
  public static final String CMD_BLOCKCHAT = "BLOCKCHAT";
  public static final String CMD_UNBLOCKCHAT = "UNBLOCKCHAT";
  public static final String CMD_COLORFLOP = "COLORFLOP";
  public static final String CMD_JOINPLAYERSCLUB = "JOINPLAYERSCLUB";
  public static final String CMD_SETDEPOSITLIMIT = "SETDEPOSITLIMIT";
  public static final String CMD_DROPDESK = "DROPDESK";
  public static final String CMD_GETSESSIONSTATS = "GETSESSIONSTATS";
  public static final String CMD_ADDBUDDY = "ADDBUDDY";
  public static final String CMD_BLOCKBUDDY = "BLOCKBUDDY";
  public static final String CMD_UNBLOCKBUDDY = "UNBLOCKBUDDY";
  public static final String CMD_INVITEBUDDY = "INVITEBUDDY";
  public static final String CMD_REMOVEBUDDY = "REMOVEBUDDY";
  public static final String CMD_CHECKBUDDYLIST = "CHECKBUDDYLIST";
  public static final String CMD_GETBUDDYLIST = "GETBUDDYLIST";
  public static final String CMD_CREATE_TEAM = "CREATETEAM";
  public static final String CMD_HIDEMEFROMSEARCH = "HIDEMEFROMSEARCH";
  public static final String CMD_GETFEEDBACK = "GETFEEDBACK";
  public static final String CMD_POSTFEEDBACK = "POSTFEEDBACK";
  public static final String CMD_SHUTDOWNIMMEDIATELLY = "SHUTDOWNIMMEDIATELLY";
  public static final String CMD_NOTICE = "NOTICE";
  public static final String CMD_GET = "GET";
  private static RequestCommandProcessorsFactory factory = new RequestCommandProcessorsFactory();

  public static RequestCommandProcessorsFactory getFactory()
  {
    return factory;
  }

  public RequestCommandProcessor getProcessor(String command)
  {
    RequestCommandProcessor commandProcessor;
    RequestCommandProcessor commandProcessor;
    if (command.equals("LOGIN")) {
      commandProcessor = new LoginCommandProcessor();
    }
    else
    {
      RequestCommandProcessor commandProcessor;
      if (command.equals("PROFILE")) {
        commandProcessor = new ProfileCommandProcessor();
      }
      else
      {
        RequestCommandProcessor commandProcessor;
        if (command.equals("QUIT")) {
          commandProcessor = new QuitCommandProcessor();
        }
        else
        {
          RequestCommandProcessor commandProcessor;
          if (command.equals("DESKS")) {
            commandProcessor = new DesksCommandProcessor();
          }
          else
          {
            RequestCommandProcessor commandProcessor;
            if (command.equals("JOIN")) {
              commandProcessor = new JoinCommandProcessor();
            }
            else
            {
              RequestCommandProcessor commandProcessor;
              if (command.equals("DESK")) {
                commandProcessor = new DeskCommandProcessor();
              }
              else
              {
                RequestCommandProcessor commandProcessor;
                if (command.equals("LEAVEDESK")) {
                  commandProcessor = new LeaveDeskCommandProcessor();
                }
                else
                {
                  RequestCommandProcessor commandProcessor;
                  if (command.equals("SBLIND")) {
                    commandProcessor = new SmallBlindCommandProcessor();
                  }
                  else
                  {
                    RequestCommandProcessor commandProcessor;
                    if (command.equals("BBLIND")) {
                      commandProcessor = new BigBlindCommandProcessor();
                    }
                    else
                    {
                      RequestCommandProcessor commandProcessor;
                      if (command.equals("RAISE")) {
                        commandProcessor = new RaiseCommandProcessor();
                      }
                      else
                      {
                        RequestCommandProcessor commandProcessor;
                        if (command.equals("CALL")) {
                          commandProcessor = new CallCommandProcessor();
                        }
                        else
                        {
                          RequestCommandProcessor commandProcessor;
                          if (command.equals("CHECK")) {
                            commandProcessor = new CheckCommandProcessor();
                          }
                          else
                          {
                            RequestCommandProcessor commandProcessor;
                            if (command.equals("BET")) {
                              commandProcessor = new BetCommandProcessor();
                            }
                            else
                            {
                              RequestCommandProcessor commandProcessor;
                              if (command.equals("FOLD")) {
                                commandProcessor = new FoldCommandProcessor();
                              }
                              else
                              {
                                RequestCommandProcessor commandProcessor;
                                if (command.equals("CARDS")) {
                                  commandProcessor = new CardsCommandProcessor();
                                }
                                else
                                {
                                  RequestCommandProcessor commandProcessor;
                                  if (command.equals("GAMERESULT")) {
                                    commandProcessor = new GameResultCommandProcessor();
                                  }
                                  else
                                  {
                                    RequestCommandProcessor commandProcessor;
                                    if (command.equals("SHOWCARDS")) {
                                      commandProcessor = new ShowCardsCommandProcessor();
                                    }
                                    else
                                    {
                                      RequestCommandProcessor commandProcessor;
                                      if (command.equals("CREATEPROFILE")) {
                                        commandProcessor = new CreateProfileCommandProcessor();
                                      }
                                      else
                                      {
                                        RequestCommandProcessor commandProcessor;
                                        if (command.equals("UPDATEPROFILE")) {
                                          commandProcessor = new UpdateProfileCommandProcessor();
                                        }
                                        else
                                        {
                                          RequestCommandProcessor commandProcessor;
                                          if (command.equals("UPDATEPASSWORD")) {
                                            commandProcessor = new UpdatePasswordCommandProcessor();
                                          }
                                          else
                                          {
                                            RequestCommandProcessor commandProcessor;
                                            if (command.equals("CHATADD")) {
                                              commandProcessor = new ChatAddCommandProcessor();
                                            }
                                            else
                                            {
                                              RequestCommandProcessor commandProcessor;
                                              if (command.equals("CHATTIME")) {
                                                commandProcessor = new ChatTimeCommandProcessor();
                                              }
                                              else
                                              {
                                                RequestCommandProcessor commandProcessor;
                                                if (command.equals("OWNER")) {
                                                  commandProcessor = new OwnerCommandProcessor();
                                                }
                                                else
                                                {
                                                  RequestCommandProcessor commandProcessor;
                                                  if (command.equals("CHATREAD")) {
                                                    commandProcessor = new ChatReadCommandProcessor();
                                                  }
                                                  else
                                                  {
                                                    RequestCommandProcessor commandProcessor;
                                                    if (command.equals("SITOUT")) {
                                                      commandProcessor = new SitOutCommandProcessor();
                                                    }
                                                    else
                                                    {
                                                      RequestCommandProcessor commandProcessor;
                                                      if (command.equals("ANTE")) {
                                                        commandProcessor = new AnteCommandProcessor();
                                                      }
                                                      else
                                                      {
                                                        RequestCommandProcessor commandProcessor;
                                                        if (command.equals("BRINGIN")) {
                                                          commandProcessor = new BringInCommandProcessor();
                                                        }
                                                        else
                                                        {
                                                          RequestCommandProcessor commandProcessor;
                                                          if (command.equals("RESTOREPASSWORD")) {
                                                            commandProcessor = new RestorePasswordCommandProcessor();
                                                          }
                                                          else
                                                          {
                                                            RequestCommandProcessor commandProcessor;
                                                            if (command.equals("MOREMONEY")) {
                                                              commandProcessor = new MoreMoneyCommandProcessor();
                                                            }
                                                            else
                                                            {
                                                              RequestCommandProcessor commandProcessor;
                                                              if (command.equals("GETPROFILE")) {
                                                                commandProcessor = new GetProfileCommandProcessor();
                                                              }
                                                              else
                                                              {
                                                                RequestCommandProcessor commandProcessor;
                                                                if (command.equals("JOINWAITINGLIST")) {
                                                                  commandProcessor = new JoinWaitingListCommandProcessor();
                                                                }
                                                                else
                                                                {
                                                                  RequestCommandProcessor commandProcessor;
                                                                  if (command.equals("CHECKWAITINGLIST")) {
                                                                    commandProcessor = new CheckWaitingListCommandProcessor();
                                                                  }
                                                                  else
                                                                  {
                                                                    RequestCommandProcessor commandProcessor;
                                                                    if (command.equals("REMOVEFROMWAITINGLIST")) {
                                                                      commandProcessor = new RemoveFromWaitingListCommandProcessor();
                                                                    }
                                                                    else
                                                                    {
                                                                      RequestCommandProcessor commandProcessor;
                                                                      if (command.equals("GETTOTALSSTATS")) {
                                                                        commandProcessor = new GetTotalStatsCommandProcessor();
                                                                      }
                                                                      else
                                                                      {
                                                                        RequestCommandProcessor commandProcessor;
                                                                        if (command.equals("BARORDER")) {
                                                                          commandProcessor = new BarOrderCommandProcessor();
                                                                        }
                                                                        else
                                                                        {
                                                                          RequestCommandProcessor commandProcessor;
                                                                          if (command.equals("CHECKTOURNAMENTSEATS")) {
                                                                            commandProcessor = new CheckTournamentSeatsCommandProcessor();
                                                                          }
                                                                          else
                                                                          {
                                                                            RequestCommandProcessor commandProcessor;
                                                                            if (command.equals("ADDON")) {
                                                                              commandProcessor = new AddonCommandProcessor();
                                                                            }
                                                                            else
                                                                            {
                                                                              RequestCommandProcessor commandProcessor;
                                                                              if (command.equals("REBUYS")) {
                                                                                commandProcessor = new ReBuysCommandProcessor();
                                                                              }
                                                                              else
                                                                              {
                                                                                RequestCommandProcessor commandProcessor;
                                                                                if (command.equals("GETPLAYERSTATS")) {
                                                                                  commandProcessor = new GetPlayerStatsCommandProcessor();
                                                                                }
                                                                                else
                                                                                {
                                                                                  RequestCommandProcessor commandProcessor;
                                                                                  if (command.equals("CLEARPLAYERSTATS")) {
                                                                                    commandProcessor = new ClearPlayerStatsCommandProcessor();
                                                                                  }
                                                                                  else
                                                                                  {
                                                                                    RequestCommandProcessor commandProcessor;
                                                                                    if (command.equals("GETSESSIONSTATS")) {
                                                                                      commandProcessor = new GetSessionStatsCommandProcessor();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                      RequestCommandProcessor commandProcessor;
                                                                                      if (command.equals("ADDBUDDY")) {
                                                                                        commandProcessor = new AddBuddyCommandProcessor();
                                                                                      }
                                                                                      else
                                                                                      {
                                                                                        RequestCommandProcessor commandProcessor;
                                                                                        if (command.equals("BLOCKBUDDY")) {
                                                                                          commandProcessor = new BlockBuddyCommandProcessor();
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                          RequestCommandProcessor commandProcessor;
                                                                                          if (command.equals("UNBLOCKBUDDY")) {
                                                                                            commandProcessor = new UnBlockBuddyCommandProcessor();
                                                                                          }
                                                                                          else
                                                                                          {
                                                                                            RequestCommandProcessor commandProcessor;
                                                                                            if (command.equals("INVITEBUDDY")) {
                                                                                              commandProcessor = new InviteBuddyCommandProcessor();
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                              RequestCommandProcessor commandProcessor;
                                                                                              if (command.equals("REMOVEBUDDY")) {
                                                                                                commandProcessor = new RemoveBuddyCommandProcessor();
                                                                                              }
                                                                                              else
                                                                                              {
                                                                                                RequestCommandProcessor commandProcessor;
                                                                                                if (command.equals("CHECKBUDDYLIST")) {
                                                                                                  commandProcessor = new CheckBuddyListCommandProcessor();
                                                                                                }
                                                                                                else
                                                                                                {
                                                                                                  RequestCommandProcessor commandProcessor;
                                                                                                  if (command.equals("GETBUDDYLIST")) {
                                                                                                    commandProcessor = new GetBuddyListCommandProcessor();
                                                                                                  }
                                                                                                  else
                                                                                                  {
                                                                                                    RequestCommandProcessor commandProcessor;
                                                                                                    if (command.equals("GETFEEDBACK")) {
                                                                                                      commandProcessor = new GetFeedBackCommandProcessor();
                                                                                                    }
                                                                                                    else
                                                                                                    {
                                                                                                      RequestCommandProcessor commandProcessor;
                                                                                                      if (command.equals("POSTFEEDBACK")) {
                                                                                                        commandProcessor = new PostFeedBackCommandProcessor();
                                                                                                      }
                                                                                                      else
                                                                                                      {
                                                                                                        RequestCommandProcessor commandProcessor;
                                                                                                        if (command.equals("GETSHOP")) {
                                                                                                          commandProcessor = new GetShopCommandProcessor();
                                                                                                        }
                                                                                                        else
                                                                                                        {
                                                                                                          RequestCommandProcessor commandProcessor;
                                                                                                          if (command.equals("BUYWARDOBE")) {
                                                                                                            commandProcessor = new BuyWardobeCommandProcessor();
                                                                                                          }
                                                                                                          else
                                                                                                          {
                                                                                                            RequestCommandProcessor commandProcessor;
                                                                                                            if (command.equals("GETPLAYERWARDOBES")) {
                                                                                                              commandProcessor = new GetPlayerWardobesCommandProcessor();
                                                                                                            }
                                                                                                            else
                                                                                                            {
                                                                                                              RequestCommandProcessor commandProcessor;
                                                                                                              if (command.equals("WEARWARDOBE")) {
                                                                                                                commandProcessor = new WearItemCommandProcessor();
                                                                                                              }
                                                                                                              else
                                                                                                              {
                                                                                                                RequestCommandProcessor commandProcessor;
                                                                                                                if (command.equals("UNWEARWARDOBE")) {
                                                                                                                  commandProcessor = new UnWearItemCommandProcessor();
                                                                                                                }
                                                                                                                else
                                                                                                                {
                                                                                                                  RequestCommandProcessor commandProcessor;
                                                                                                                  if (command.equals("WHATISWEARED")) {
                                                                                                                    commandProcessor = new WhatIsWearedCommandProcessor();
                                                                                                                  }
                                                                                                                  else
                                                                                                                  {
                                                                                                                    RequestCommandProcessor commandProcessor;
                                                                                                                    if (command.equals("HIDEMEFROMSEARCH")) {
                                                                                                                      commandProcessor = new HideMeFromSearchCommandProcessor();
                                                                                                                    }
                                                                                                                    else
                                                                                                                    {
                                                                                                                      RequestCommandProcessor commandProcessor;
                                                                                                                      if (command.equals("GETTEAMPLAYERS")) {
                                                                                                                        commandProcessor = new GetTeamPlayersCommandProcessor();
                                                                                                                      }
                                                                                                                      else
                                                                                                                      {
                                                                                                                        RequestCommandProcessor commandProcessor;
                                                                                                                        if (command.equals("CREATETEAM")) {
                                                                                                                          commandProcessor = new CreateTeamCommandProcessor();
                                                                                                                        }
                                                                                                                        else
                                                                                                                        {
                                                                                                                          RequestCommandProcessor commandProcessor;
                                                                                                                          if (command.equals("GETTOURNAMENTINFO")) {
                                                                                                                            commandProcessor = new GetTournamentInfoCommandProcessor();
                                                                                                                          }
                                                                                                                          else
                                                                                                                          {
                                                                                                                            RequestCommandProcessor commandProcessor;
                                                                                                                            if (command.equals("GETTOURNAMENTSINFO")) {
                                                                                                                              commandProcessor = new GetTournamentsInfoCommandProcessor();
                                                                                                                            }
                                                                                                                            else
                                                                                                                            {
                                                                                                                              RequestCommandProcessor commandProcessor;
                                                                                                                              if (command.equals("JOINTOURNAMENT")) {
                                                                                                                                commandProcessor = new JoinTournamentCommandProcessor();
                                                                                                                              }
                                                                                                                              else
                                                                                                                              {
                                                                                                                                RequestCommandProcessor commandProcessor;
                                                                                                                                if (command.equals("UNJOINTOURNAMENT")) {
                                                                                                                                  commandProcessor = new UnjoinTournamentCommandProcessor();
                                                                                                                                }
                                                                                                                                else
                                                                                                                                {
                                                                                                                                  RequestCommandProcessor commandProcessor;
                                                                                                                                  if (command.equals("GETPLAYERTOURNAMENT")) {
                                                                                                                                    commandProcessor = new GetPlayerTournamentCommandProcessor();
                                                                                                                                  }
                                                                                                                                  else
                                                                                                                                  {
                                                                                                                                    RequestCommandProcessor commandProcessor;
                                                                                                                                    if (command.equals("GETSITANDGOINFO")) {
                                                                                                                                      commandProcessor = new GetSitAndGoInfoCommandProcessor();
                                                                                                                                    }
                                                                                                                                    else
                                                                                                                                    {
                                                                                                                                      RequestCommandProcessor commandProcessor;
                                                                                                                                      if (command.equals("MAKETELL")) {
                                                                                                                                        commandProcessor = new MakeTellCommandProcessor();
                                                                                                                                      }
                                                                                                                                      else
                                                                                                                                      {
                                                                                                                                        RequestCommandProcessor commandProcessor;
                                                                                                                                        if (command.equals("SAVEAVATAR")) {
                                                                                                                                          commandProcessor = new SaveAvatarCommandProcessor();
                                                                                                                                        }
                                                                                                                                        else
                                                                                                                                        {
                                                                                                                                          RequestCommandProcessor commandProcessor;
                                                                                                                                          if (command.equals("LOADAVATAR")) {
                                                                                                                                            commandProcessor = new LoadAvatarCommandProcessor();
                                                                                                                                          }
                                                                                                                                          else
                                                                                                                                          {
                                                                                                                                            RequestCommandProcessor commandProcessor;
                                                                                                                                            if (command.equals("CHECKLOCKS")) {
                                                                                                                                              commandProcessor = new CheckLocksCommandProcessor();
                                                                                                                                            }
                                                                                                                                            else
                                                                                                                                            {
                                                                                                                                              RequestCommandProcessor commandProcessor;
                                                                                                                                              if (command.equals("SKIPLOCKS")) {
                                                                                                                                                commandProcessor = new SkipLocksCommandProcessor();
                                                                                                                                              }
                                                                                                                                              else
                                                                                                                                              {
                                                                                                                                                RequestCommandProcessor commandProcessor;
                                                                                                                                                if (command.equals("SEARCHUSER")) {
                                                                                                                                                  commandProcessor = new SearchUserCommandProcessor();
                                                                                                                                                }
                                                                                                                                                else
                                                                                                                                                {
                                                                                                                                                  RequestCommandProcessor commandProcessor;
                                                                                                                                                  if (command.equals("CREATEPRIVATEDESK")) {
                                                                                                                                                    commandProcessor = new CreatePrivateDeskCommandProcessor();
                                                                                                                                                  }
                                                                                                                                                  else
                                                                                                                                                  {
                                                                                                                                                    RequestCommandProcessor commandProcessor;
                                                                                                                                                    if (command.equals("PRIVATEDESKS")) {
                                                                                                                                                      commandProcessor = new PrivateDesksCommandProcessor();
                                                                                                                                                    }
                                                                                                                                                    else
                                                                                                                                                    {
                                                                                                                                                      RequestCommandProcessor commandProcessor;
                                                                                                                                                      if (command.equals("SAVENOTE")) {
                                                                                                                                                        commandProcessor = new SaveNoteCommandProcessor();
                                                                                                                                                      }
                                                                                                                                                      else
                                                                                                                                                      {
                                                                                                                                                        RequestCommandProcessor commandProcessor;
                                                                                                                                                        if (command.equals("SAVERATING")) {
                                                                                                                                                          commandProcessor = new SaveRatingCommandProcessor();
                                                                                                                                                        }
                                                                                                                                                        else
                                                                                                                                                        {
                                                                                                                                                          RequestCommandProcessor commandProcessor;
                                                                                                                                                          if (command.equals("GETNOTE")) {
                                                                                                                                                            commandProcessor = new GetNoteCommandProcessor();
                                                                                                                                                          }
                                                                                                                                                          else
                                                                                                                                                          {
                                                                                                                                                            RequestCommandProcessor commandProcessor;
                                                                                                                                                            if (command.equals("UPDATEEMAIL")) {
                                                                                                                                                              commandProcessor = new UpdateEmailCommandProcessor();
                                                                                                                                                            }
                                                                                                                                                            else
                                                                                                                                                            {
                                                                                                                                                              RequestCommandProcessor commandProcessor;
                                                                                                                                                              if (command.equals("BLOCKCHAT")) {
                                                                                                                                                                commandProcessor = new BlockChatCommandProcessor();
                                                                                                                                                              }
                                                                                                                                                              else
                                                                                                                                                              {
                                                                                                                                                                RequestCommandProcessor commandProcessor;
                                                                                                                                                                if (command.equals("UNBLOCKCHAT")) {
                                                                                                                                                                  commandProcessor = new UnBlockChatCommandProcessor();
                                                                                                                                                                }
                                                                                                                                                                else
                                                                                                                                                                {
                                                                                                                                                                  RequestCommandProcessor commandProcessor;
                                                                                                                                                                  if (command.equals("COLORFLOP")) {
                                                                                                                                                                    commandProcessor = new ColorFlopCommandProcessor();
                                                                                                                                                                  }
                                                                                                                                                                  else
                                                                                                                                                                  {
                                                                                                                                                                    RequestCommandProcessor commandProcessor;
                                                                                                                                                                    if (command.equals("JOINPLAYERSCLUB")) {
                                                                                                                                                                      commandProcessor = new JoinPlayersClubCommandProcessor();
                                                                                                                                                                    }
                                                                                                                                                                    else
                                                                                                                                                                    {
                                                                                                                                                                      RequestCommandProcessor commandProcessor;
                                                                                                                                                                      if (command.equals("SETDEPOSITLIMIT")) {
                                                                                                                                                                        commandProcessor = new SetDepositLimitCommandProcessor();
                                                                                                                                                                      }
                                                                                                                                                                      else
                                                                                                                                                                      {
                                                                                                                                                                        RequestCommandProcessor commandProcessor;
                                                                                                                                                                        if (command.equals("GETTINFO")) {
                                                                                                                                                                          commandProcessor = new GetTTInfoCommandProcessor();
                                                                                                                                                                        }
                                                                                                                                                                        else
                                                                                                                                                                        {
                                                                                                                                                                          RequestCommandProcessor commandProcessor;
                                                                                                                                                                          if (command.equals("DROPDESK")) {
                                                                                                                                                                            commandProcessor = new DropDeskCommandProcessor();
                                                                                                                                                                          }
                                                                                                                                                                          else
                                                                                                                                                                          {
                                                                                                                                                                            RequestCommandProcessor commandProcessor;
                                                                                                                                                                            if (command.equals("SHUTDOWNIMMEDIATELLY")) {
                                                                                                                                                                              commandProcessor = new ShutdownImmediatellyCommandProcessor();
                                                                                                                                                                            }
                                                                                                                                                                            else
                                                                                                                                                                            {
                                                                                                                                                                              RequestCommandProcessor commandProcessor;
                                                                                                                                                                              if (command.equals("NOTICE")) {
                                                                                                                                                                                commandProcessor = new NoticeCommandProcessor();
                                                                                                                                                                              }
                                                                                                                                                                              else
                                                                                                                                                                              {
                                                                                                                                                                                RequestCommandProcessor commandProcessor;
                                                                                                                                                                                if (command.equals("GET")) {
                                                                                                                                                                                  commandProcessor = new GetProcessor();
                                                                                                                                                                                }
                                                                                                                                                                                else
                                                                                                                                                                                {
                                                                                                                                                                                  commandProcessor = new UndefinedCommandProcessor();
                                                                                                                                                                                }
                                                                                                                                                                              }
                                                                                                                                                                            }
                                                                                                                                                                          }
                                                                                                                                                                        }
                                                                                                                                                                      }
                                                                                                                                                                    }
                                                                                                                                                                  }
                                                                                                                                                                }
                                                                                                                                                              }
                                                                                                                                                            }
                                                                                                                                                          }
                                                                                                                                                        }
                                                                                                                                                      }
                                                                                                                                                    }
                                                                                                                                                  }
                                                                                                                                                }
                                                                                                                                              }
                                                                                                                                            }
                                                                                                                                          }
                                                                                                                                        }
                                                                                                                                      }
                                                                                                                                    }
                                                                                                                                  }
                                                                                                                                }
                                                                                                                              }
                                                                                                                            }
                                                                                                                          }
                                                                                                                        }
                                                                                                                      }
                                                                                                                    }
                                                                                                                  }
                                                                                                                }
                                                                                                              }
                                                                                                            }
                                                                                                          }
                                                                                                        }
                                                                                                      }
                                                                                                    }
                                                                                                  }
                                                                                                }
                                                                                              }
                                                                                            }
                                                                                          }
                                                                                        }
                                                                                      }
                                                                                    }
                                                                                  }
                                                                                }
                                                                              }
                                                                            }
                                                                          }
                                                                        }
                                                                      }
                                                                    }
                                                                  }
                                                                }
                                                              }
                                                            }
                                                          }
                                                        }
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return commandProcessor;
  }
}