package game.playerclub.dirtypoints;

import game.Desk;
import game.Player;
import java.math.BigDecimal;
import org.apache.log4j.Logger;
import utils.CommonLogger;

public class DeskPointsCounter
{
  public static final int RAKED_HANDS_COUNT = 20;
  public static final BigDecimal FIRST_LEVEL = new BigDecimal(50).setScale(2, 5);
  public static final BigDecimal SECOND_LEVEL = new BigDecimal(10).setScale(2, 5);
  public static final BigDecimal THIRD_LEVEL = new BigDecimal(3).setScale(2, 5);
  public static final BigDecimal FOURTH_LEVEL = new BigDecimal(2).setScale(2, 5);
  public static final BigDecimal FIFTH_LEVEL = new BigDecimal(0.5D).setScale(2, 5);
  public static final BigDecimal SIXTH_LEVEL = new BigDecimal(0.25D).setScale(2, 5);

  public static final BigDecimal FIRST_LEVEL_SUM = new BigDecimal(28).setScale(2, 5);
  public static final BigDecimal SECOND_LEVEL_SUM = new BigDecimal(18).setScale(2, 5);
  public static final BigDecimal THIRD_LEVEL_SUM = new BigDecimal(12).setScale(2, 5);
  public static final BigDecimal FOURTH_LEVEL_SUM = new BigDecimal(6).setScale(2, 5);
  public static final BigDecimal FIFTH_LEVEL_SUM = new BigDecimal(4).setScale(2, 5);

  public static void calculatePoints(DesksDirtyPoints desksDirtyPoints, Desk desk)
  {
    if (desk.getMoneyType() == 0)
      synchronized (desksDirtyPoints) {
        if (desksDirtyPoints.getRakeCount() >= 20) {
          desksDirtyPoints.setRakeCount(0);

          if (desk.getMinBet().compareTo(FIRST_LEVEL) >= 0) {
            desksDirtyPoints.getPlayer().increaseDirtyPoints(FIRST_LEVEL_SUM);
            if (CommonLogger.getLogger().isDebugEnabled())
              CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current dirty points increased to " + FIRST_LEVEL_SUM);
          } else if (desk.getMinBet().compareTo(SECOND_LEVEL) >= 0) {
            desksDirtyPoints.getPlayer().increaseDirtyPoints(SECOND_LEVEL_SUM);
            if (CommonLogger.getLogger().isDebugEnabled())
              CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current dirty points increased to " + SECOND_LEVEL_SUM);
          } else if (desk.getMinBet().compareTo(THIRD_LEVEL) >= 0) {
            desksDirtyPoints.getPlayer().increaseDirtyPoints(THIRD_LEVEL_SUM);
            if (CommonLogger.getLogger().isDebugEnabled())
              CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current dirty points increased to " + THIRD_LEVEL_SUM);
          } else if (desk.getMinBet().compareTo(FOURTH_LEVEL) >= 0) {
            desksDirtyPoints.getPlayer().increaseDirtyPoints(FOURTH_LEVEL_SUM);
            if (CommonLogger.getLogger().isDebugEnabled())
              CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current dirty points increased to " + FOURTH_LEVEL_SUM);
          } else if (desk.getMinBet().compareTo(SIXTH_LEVEL) >= 0) {
            desksDirtyPoints.getPlayer().increaseDirtyPoints(FIFTH_LEVEL_SUM);
            if (CommonLogger.getLogger().isDebugEnabled()) {
              CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current dirty points increased to " + FIFTH_LEVEL_SUM);
            }
          }
          else if (CommonLogger.getLogger().isDebugEnabled()) {
            CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current dirty points increased to 0 (low blinds table) ");
          }

          new Thread(new DirtyPointsSaver(desksDirtyPoints.getPlayer())).start();
        }
        else if (CommonLogger.getLogger().isDebugEnabled()) {
          CommonLogger.getLogger().debug(desksDirtyPoints.getPlayer().getLogin() + " current desk rakes count " + desksDirtyPoints.getRakeCount());
        }
      }
  }
}