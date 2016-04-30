# MySQL-Front 5.1  (Build 3.35)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: localhost    Database: robotdigger
# ------------------------------------------------------
# Server version 5.5.28

USE `robotdigger`;

#
# Source for table uploadfile
#

DROP TABLE IF EXISTS `uploadfile`;
CREATE TABLE `uploadfile` (
  `extractnum` varchar(6) NOT NULL,
  `filename` varchar(50) NOT NULL,
  `filepath` varchar(200) NOT NULL,
  `uploadtime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `username` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`extractnum`),
  KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table uploadfile
#
LOCK TABLES `uploadfile` WRITE;
/*!40000 ALTER TABLE `uploadfile` DISABLE KEYS */;

INSERT INTO `uploadfile` VALUES ('017552','RobotDiggerResult_微信_20130609094911.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/017552','2013-06-09 14:42:51','administrator');
INSERT INTO `uploadfile` VALUES ('055262','RobotDiggerResult_UC浏览器_20130609093423.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/055262','2013-06-09 14:39:55','admin');
INSERT INTO `uploadfile` VALUES ('138480','RobotDiggerResult_微信_20130609092715.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/138480','2013-06-09 14:38:10','a');
INSERT INTO `uploadfile` VALUES ('273666','RobotDiggerResult_神庙逃亡2_20130609104828.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/273666','2013-06-09 14:44:56','administrator');
INSERT INTO `uploadfile` VALUES ('278466','RobotDiggerResult_水果忍者_20130609094704.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/278466','2013-06-09 14:44:02','administrator');
INSERT INTO `uploadfile` VALUES ('310886','RobotDiggerResult_水果忍者_20130609094704.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/310886','2013-06-09 14:43:20','administrator');
INSERT INTO `uploadfile` VALUES ('563971','RobotDiggerResult_墨迹天气_20130609092736.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/563971','2013-06-09 14:34:25','a');
INSERT INTO `uploadfile` VALUES ('672951','RobotDiggerResult_微信_20130609094911.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/672951','2013-06-09 14:42:22','administrator');
INSERT INTO `uploadfile` VALUES ('993424','RobotDiggerResult_?360卫士_20130609093327.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/993424','2013-06-09 14:40:30','admin');
/*!40000 ALTER TABLE `uploadfile` ENABLE KEYS */;
UNLOCK TABLES;

#
#  Foreign keys for table uploadfile
#

ALTER TABLE `uploadfile`
ADD CONSTRAINT `uploadfile_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
