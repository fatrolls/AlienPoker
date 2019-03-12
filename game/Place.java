package game;

import defaultvalues.DefaultValue;
import game.messages.PrivateStateMessagesList;
import game.stats.PlaceSessionStats;
import game.stats.PlayersStats;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import utils.xml.XMLTag;

public class Place
{
  static Logger log = Logger.getLogger(Place.class);
  private int number;
  private Player player;
  private final PlaceSessionStats placeSessionStats = new PlaceSessionStats();
  private BigDecimal amount;
  private BigDecimal startAmount;
  private BigDecimal stakingAmount;
  private BigDecimal stakingAmountCache;
  private BigDecimal allInPretendedAmount;
  private boolean sittingOut;
  private boolean fold;
  private boolean dealer;
  private boolean showCards;
  private boolean allIn;
  private BigDecimal allInAmount;
  private long lastActionTime;
  private Card discardedCard;
  private int sitOutsQty;
  private boolean makeLock;
  private boolean canPostCards;
  private ArrayList cards;
  private PrivateStateMessagesList messages;
  public static final String OUT_PARAM_CARDS = "CARDS";
  private boolean discon;

  public int hashCode()
  {
    return number;
  }

  public void updateLastActionTime()
  {
    lastActionTime = System.currentTimeMillis();
  }

  public boolean isAllIn()
  {
    return allIn;
  }

  public BigDecimal getAllInAmount()
  {
    return allInAmount;
  }

  public void markAsAllIn(BigDecimal paramBigDecimal)
  {
    allInAmount = paramBigDecimal;
    allIn = true;
  }

  public void unmarkAsAllIn()
  {
    allIn = false;
    allInAmount = new BigDecimal(0);
    allInPretendedAmount = new BigDecimal(0);
  }

  public void acceptShowCards()
  {
    showCards = true;
  }

  public boolean isAcceptShowCards()
  {
    return showCards;
  }

  public boolean canMakeLock()
  {
    return makeLock;
  }

  public void setCanMakeLock(boolean paramBoolean)
  {
    makeLock = paramBoolean;
  }

  public XMLTag getCardsXMLTag()
  {
    XMLTag localXMLTag = new XMLTag("CARDS");
    synchronized (cards)
    {
      Card localCard;
      for (Iterator localIterator = cards.iterator(); localIterator.hasNext(); localXMLTag.addNestedTag(localCard.toXMLTag())) {
        localCard = (Card)localIterator.next();
      }
    }
    return localXMLTag;
  }

  public boolean isFold()
  {
    return fold;
  }

  public void unmarkAsFold()
  {
    fold = false;
  }

  public void markAsFold()
  {
    fold = true;
  }

  public PrivateStateMessagesList getStateMessagesList()
  {
    return messages;
  }

  public ArrayList getCards()
  {
    return cards;
  }

  public void clearStateMessages()
  {
    synchronized (messages)
    {
      messages.clear();
    }
  }

  public void deleteMessage(int paramInt)
  {
  }

  public String getStateMessagesXML()
    throws UnsupportedEncodingException
  {
    return messages.toXML();
  }

  public boolean isActive()
  {
    if (sittingOut)
      return false;
    if (fold)
      return false;
    return player != null;
  }

  public Place(int paramInt)
  {
    number = 0;
    player = null;
    amount = DefaultValue.ZERO_BIDECIMAL;
    startAmount = DefaultValue.ZERO_BIDECIMAL;
    stakingAmount = DefaultValue.ZERO_BIDECIMAL;
    stakingAmountCache = DefaultValue.ZERO_BIDECIMAL;
    allInPretendedAmount = DefaultValue.ZERO_BIDECIMAL;
    sittingOut = true;
    fold = false;
    dealer = false;
    showCards = false;
    allIn = false;
    allInAmount = DefaultValue.ZERO_BIDECIMAL;
    lastActionTime = 0L;
    sitOutsQty = 0;
    makeLock = false;
    canPostCards = false;
    cards = new ArrayList();
    messages = new PrivateStateMessagesList();
    discon = false;
    number = paramInt;
  }

  public boolean isDiller()
  {
    return dealer;
  }

  public void unsetIsDealer()
  {
    dealer = false;
  }

  public void setAsDealer()
  {
    dealer = true;
  }

  public void addCard(Card paramCard)
  {
    synchronized (cards)
    {
      cards.add(paramCard);
    }
  }

  public int getNumber()
  {
    return number;
  }

  public void unmarkAsSittingOut()
  {
    sittingOut = false;
  }

  public void markAsSittingOut()
  {
    sittingOut = true;
  }

  public boolean isSittingOut()
  {
    return sittingOut;
  }

  public void incDeskAmount(BigDecimal paramBigDecimal)
  {
    amount = amount.add(paramBigDecimal).setScale(2, 5);
  }

  public void seatPlayer(Player paramPlayer, BigDecimal paramBigDecimal, boolean paramBoolean)
  {
    synchronized (this)
    {
      player = paramPlayer;
      amount = paramBigDecimal;
      startAmount = paramBigDecimal;
      sittingOut = paramBoolean;
      fold = false;
    }
    placeSessionStats.clear();
  }

  public void seatPlayer(Player paramPlayer, BigDecimal paramBigDecimal)
  {
    seatPlayer(paramPlayer, paramBigDecimal, false);
  }

  public void setAmount(BigDecimal paramBigDecimal)
  {
    amount = paramBigDecimal;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public Player getPlayer()
  {
    return player;
  }

  public void free()
  {
    placeSessionStats.clear();
    synchronized (this)
    {
      if (player != null)
        PlayersStats.updatePlayerStats(player);
      player = null;
      sittingOut = false;
      fold = false;
      discon = false;
      dealer = false;
      showCards = false;
      sitOutsQty = 0;
      clearStateMessages();
    }
  }

  public void unsetShowCards()
  {
    showCards = false;
  }

  public boolean isBusy()
  {
    return player != null;
  }

  public boolean isFree()
  {
    return player == null;
  }

  public boolean decAmount(BigDecimal paramBigDecimal)
  {
    if (paramBigDecimal.floatValue() < amount.floatValue())
    {
      amount = amount.subtract(paramBigDecimal).setScale(2, 5);
      return true;
    }

    return false;
  }

  public BigDecimal getStakingAmount()
  {
    return stakingAmount;
  }

  public void setStakingAmount(BigDecimal paramBigDecimal)
  {
    if (paramBigDecimal.floatValue() > 0.0F)
    {
      stakingAmountCache = stakingAmountCache.add(paramBigDecimal.subtract(stakingAmount));
    }

    if (player != null) {
      log.info(player.getLogin() + " = thisstakingamount: " + stakingAmount.floatValue() + " newstaking: " + paramBigDecimal.floatValue() + " stakingcache: " + stakingAmountCache.floatValue());
    }
    stakingAmount = paramBigDecimal;
  }

  public boolean equals(Object paramObject)
  {
    Place localPlace = (Place)paramObject;
    return (localPlace.getNumber() == getNumber()) && (localPlace.getPlayer() == getPlayer());
  }

  public BigDecimal getAllInPretendedAmount()
  {
    return allInPretendedAmount;
  }

  public void setAllInPretendedAmount(BigDecimal paramBigDecimal)
  {
    allInPretendedAmount = paramBigDecimal;
  }

  public Card getDiscardedCard()
  {
    return discardedCard;
  }

  public void setDiscardedCard(Card paramCard)
  {
    discardedCard = paramCard;
  }

  public boolean isCanPostCards()
  {
    return canPostCards;
  }

  public void setCanPostCards(boolean paramBoolean)
  {
    canPostCards = paramBoolean;
  }

  public int getSitOutsQty()
  {
    return sitOutsQty;
  }

  public void incSitOutsQty()
  {
    synchronized (this)
    {
      sitOutsQty += 1;
    }
  }

  public void clearSitOutsQty()
  {
    synchronized (this)
    {
      sitOutsQty = 0;
    }
  }

  public PlaceSessionStats getPlaceSessionStats()
  {
    return placeSessionStats;
  }

  public boolean isDiscon()
  {
    return discon;
  }

  public void unsetDiscon()
  {
    discon = false;
  }

  public void setAsDiscon()
  {
    discon = true;
  }

  public BigDecimal getStartAmount()
  {
    return startAmount;
  }

  public void setStartAmount(BigDecimal paramBigDecimal)
  {
    startAmount = paramBigDecimal;
  }

  public BigDecimal getStakingAmountCache()
  {
    return stakingAmountCache;
  }

  public void setStakingAmountCache(BigDecimal paramBigDecimal)
  {
    stakingAmountCache = paramBigDecimal;
  }
}