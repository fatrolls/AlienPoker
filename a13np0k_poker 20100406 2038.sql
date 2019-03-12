-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.35-community


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema al13np0k_poker
--

CREATE DATABASE IF NOT EXISTS al13np0k_poker;
USE al13np0k_poker;

--
-- Definition of table `buddies_list`
--

DROP TABLE IF EXISTS `buddies_list`;
CREATE TABLE `buddies_list` (
  `owner` int(10) unsigned NOT NULL,
  `buddy` int(10) unsigned NOT NULL,
  `blocked` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `buddies_list`
--

/*!40000 ALTER TABLE `buddies_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `buddies_list` ENABLE KEYS */;


--
-- Definition of table `desk_users`
--

DROP TABLE IF EXISTS `desk_users`;
CREATE TABLE `desk_users` (
  `desk_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `place` int(10) unsigned NOT NULL,
  `amount` float(5,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desk_users`
--

/*!40000 ALTER TABLE `desk_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `desk_users` ENABLE KEYS */;


--
-- Definition of table `desks`
--

DROP TABLE IF EXISTS `desks`;
CREATE TABLE `desks` (
  `desk_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `d_name` varchar(45) NOT NULL,
  `d_money_type` int(10) unsigned NOT NULL,
  `d_limit_type` int(10) unsigned NOT NULL,
  `d_min_bet` float(5,2) NOT NULL,
  `d_max_bet` float(5,2) NOT NULL,
  `d_ante` float(5,2) NOT NULL,
  `d_bring_in` float(5,2) NOT NULL,
  `d_poker_type` int(10) unsigned NOT NULL,
  `d_min_amount` float(5,2) NOT NULL,
  `d_rate_limit` float(5,2) NOT NULL,
  `d_rake` float(5,2) NOT NULL,
  `d_places` int(10) unsigned NOT NULL,
  `d_private` int(10) unsigned NOT NULL,
  `d_owner` int(10) unsigned NOT NULL,
  `d_password` varchar(45) NOT NULL,
  `d_speed` int(10) unsigned NOT NULL,
  PRIMARY KEY (`desk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desks`
--

/*!40000 ALTER TABLE `desks` DISABLE KEYS */;
/*!40000 ALTER TABLE `desks` ENABLE KEYS */;


--
-- Definition of table `feedback_topics`
--

DROP TABLE IF EXISTS `feedback_topics`;
CREATE TABLE `feedback_topics` (
  `topic_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `topic_name` varchar(45) NOT NULL,
  PRIMARY KEY (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `feedback_topics`
--

/*!40000 ALTER TABLE `feedback_topics` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback_topics` ENABLE KEYS */;


--
-- Definition of table `hand`
--

DROP TABLE IF EXISTS `hand`;
CREATE TABLE `hand` (
  `last` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`last`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hand`
--

/*!40000 ALTER TABLE `hand` DISABLE KEYS */;
/*!40000 ALTER TABLE `hand` ENABLE KEYS */;


--
-- Definition of table `notes`
--

DROP TABLE IF EXISTS `notes`;
CREATE TABLE `notes` (
  `player_owner` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `player_to` int(10) unsigned NOT NULL,
  `message` varchar(255) NOT NULL,
  `rating` int(10) unsigned NOT NULL,
  `chat` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`player_owner`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `notes`
--

/*!40000 ALTER TABLE `notes` DISABLE KEYS */;
/*!40000 ALTER TABLE `notes` ENABLE KEYS */;


--
-- Definition of table `player_game_stats`
--

DROP TABLE IF EXISTS `player_game_stats`;
CREATE TABLE `player_game_stats` (
  `player_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `session_start` datetime NOT NULL,
  `session_games` int(10) unsigned NOT NULL,
  `games_won` double(5,2) NOT NULL,
  `showdowns_count` int(10) unsigned NOT NULL,
  `showdowns_won` double(5,2) DEFAULT NULL,
  `flop_seen` double(5,2) NOT NULL,
  `win_if_flop_seen` double(5,2) NOT NULL,
  `betting_count` int(10) unsigned NOT NULL,
  `fold` double(5,2) NOT NULL,
  `check` double(5,2) NOT NULL,
  `call` double(5,2) NOT NULL,
  `bet` double(5,2) NOT NULL,
  `raise` double(5,2) NOT NULL,
  `re_raise` double(5,2) NOT NULL,
  `folds_count` int(10) unsigned NOT NULL,
  `fold_pre_flop` double(5,2) NOT NULL,
  `fold_after_flop` double(5,2) NOT NULL,
  `fold_after_turn` double(5,2) NOT NULL,
  `fold_after_river` double(5,2) NOT NULL,
  `no_fold` double(5,2) NOT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `player_game_stats`
--

/*!40000 ALTER TABLE `player_game_stats` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_game_stats` ENABLE KEYS */;


--
-- Definition of table `player_wardobe`
--

DROP TABLE IF EXISTS `player_wardobe`;
CREATE TABLE `player_wardobe` (
  `id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `state` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `player_wardobe`
--

/*!40000 ALTER TABLE `player_wardobe` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_wardobe` ENABLE KEYS */;


--
-- Definition of table `players_club`
--

DROP TABLE IF EXISTS `players_club`;
CREATE TABLE `players_club` (
  `user_id` int(10) unsigned NOT NULL,
  `rating` int(10) unsigned NOT NULL,
  `points` double(5,2) NOT NULL,
  `reg_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `players_club`
--

/*!40000 ALTER TABLE `players_club` DISABLE KEYS */;
/*!40000 ALTER TABLE `players_club` ENABLE KEYS */;


--
-- Definition of table `team_players`
--

DROP TABLE IF EXISTS `team_players`;
CREATE TABLE `team_players` (
  `team_id` int(10) unsigned NOT NULL,
  `playerId` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `team_players`
--

/*!40000 ALTER TABLE `team_players` DISABLE KEYS */;
/*!40000 ALTER TABLE `team_players` ENABLE KEYS */;


--
-- Definition of table `teams`
--

DROP TABLE IF EXISTS `teams`;
CREATE TABLE `teams` (
  `team_id` int(10) unsigned NOT NULL,
  `tour_id` int(10) unsigned NOT NULL,
  `team_leader` int(10) unsigned NOT NULL,
  `team_name` varchar(45) NOT NULL,
  `num` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `teams`
--

/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;


--
-- Definition of table `tournaments`
--

DROP TABLE IF EXISTS `tournaments`;
CREATE TABLE `tournaments` (
  `tour_id` int(10) unsigned NOT NULL,
  `tour_name` varchar(45) NOT NULL,
  `tour_type` varchar(45) NOT NULL,
  `tour_is_freeroll` int(10) unsigned NOT NULL,
  `tour_freeroll_pool` float(5,2) NOT NULL,
  `tour_sub_type` int(10) unsigned NOT NULL,
  `tour_poker` int(10) unsigned NOT NULL,
  `tour_limit` int(10) unsigned NOT NULL,
  `tour_money` int(10) unsigned NOT NULL,
  `tour_beg_amount` float(5,2) NOT NULL,
  `tour_max_bet` float(5,2) NOT NULL,
  `tour_min_bet` float(5,2) NOT NULL,
  `tour_date` datetime NOT NULL,
  `tour_reg_start` datetime NOT NULL,
  `tour_buy_in` double(5,2) NOT NULL,
  `tour_level_duration` int(10) unsigned NOT NULL,
  `tour_time_on_level` int(10) unsigned NOT NULL,
  `tour_break_period` int(10) unsigned NOT NULL,
  `tour_break_length` int(10) unsigned NOT NULL,
  `tour_max_at_table` int(10) unsigned NOT NULL,
  `tour_re_buys` int(10) unsigned NOT NULL,
  `tour_addons` int(10) unsigned NOT NULL,
  `tour_addons_amount` float(5,2) NOT NULL,
  `tour_rebuys_amount` float(5,2) NOT NULL,
  `tour_fee` float(5,2) NOT NULL,
  `tour_min_pls_to_start` int(10) unsigned NOT NULL,
  `tour_speed` int(10) unsigned NOT NULL,
  `tour_teams_qty` int(10) unsigned NOT NULL,
  `tour_players_in_team` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tournaments`
--

/*!40000 ALTER TABLE `tournaments` DISABLE KEYS */;
/*!40000 ALTER TABLE `tournaments` ENABLE KEYS */;


--
-- Definition of table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `us_login` varchar(45) NOT NULL,
  `us_password` varchar(45) NOT NULL,
  `us_amount` float(5,2) NOT NULL,
  `us_real_amount` float(5,2) NOT NULL,
  `us_city` varchar(45) NOT NULL,
  `us_country` varchar(45) NOT NULL,
  `us_email` varchar(45) NOT NULL,
  `us_fname` varchar(45) NOT NULL,
  `us_lname` varchar(45) NOT NULL,
  `us_avatar` varchar(45) NOT NULL,
  `us_player_status` tinyint(3) unsigned NOT NULL,
  `us_address` varchar(45) NOT NULL,
  `us_phone` varchar(45) NOT NULL,
  `us_state` varchar(45) NOT NULL,
  `us_zip` varchar(45) NOT NULL,
  `us_deposit_limit` float(5,2) NOT NULL,
  `us_gender` int(10) unsigned NOT NULL,
  `us_birthday` date NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;


--
-- Definition of table `wardobes`
--

DROP TABLE IF EXISTS `wardobes`;
CREATE TABLE `wardobes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `price` decimal(5,2) NOT NULL,
  `title` varchar(45) NOT NULL,
  `type` int(10) unsigned NOT NULL,
  `gender` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `wardobes`
--

/*!40000 ALTER TABLE `wardobes` DISABLE KEYS */;
/*!40000 ALTER TABLE `wardobes` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
