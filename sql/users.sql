# Host: 127.0.0.1  (Version: 5.6.13)
# Date: 2013-11-18 08:17:07
# Generator: MySQL-Front 5.3  (Build 4.43)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "uploadfile"
#

DROP TABLE IF EXISTS `uploadfile`;
CREATE TABLE `uploadfile` (
  `extractnum` varchar(6) NOT NULL,
  `filename` varchar(50) NOT NULL,
  `filepath` varchar(200) NOT NULL,
  `uploadtime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `username` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`extractnum`),
  KEY `username` (`username`),
  CONSTRAINT `uploadfile_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "uploadfile"
#

INSERT INTO `uploadfile` VALUES ('017552','RobotDiggerResult_微信_20130609094911.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/017552','2013-06-09 14:42:51','administrator'),('055262','RobotDiggerResult_UC浏览器_20130609093423.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/055262','2013-06-09 14:39:55','admin'),('138480','RobotDiggerResult_微信_20130609092715.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/138480','2013-06-09 14:38:10','a'),('273666','RobotDiggerResult_神庙逃亡2_20130609104828.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/273666','2013-06-09 14:44:56','administrator'),('278466','RobotDiggerResult_水果忍者_20130609094704.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/278466','2013-06-09 14:44:02','administrator'),('310886','RobotDiggerResult_水果忍者_20130609094704.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/310886','2013-06-09 14:43:20','administrator'),('563971','RobotDiggerResult_墨迹天气_20130609092736.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/563971','2013-06-09 14:34:25','a'),('672951','RobotDiggerResult_微信_20130609094911.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/672951','2013-06-09 14:42:22','administrator'),('993424','RobotDiggerResult_?360卫士_20130609093327.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/993424','2013-06-09 14:40:30','admin');
