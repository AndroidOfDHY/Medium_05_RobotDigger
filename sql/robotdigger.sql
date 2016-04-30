/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


USE `robotdigger`;
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
LOCK TABLES `uploadfile` WRITE;
/*!40000 ALTER TABLE `uploadfile` DISABLE KEYS */;

INSERT INTO `uploadfile` VALUES ('017552','RobotDiggerResult_Î¢ÐÅ_20130609094911.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/017552','2013-06-09 14:42:51','administrator');
INSERT INTO `uploadfile` VALUES ('055262','RobotDiggerResult_UCä¯ÀÀÆ÷_20130609093423.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/055262','2013-06-09 14:39:55','admin');
INSERT INTO `uploadfile` VALUES ('138480','RobotDiggerResult_Î¢ÐÅ_20130609092715.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/138480','2013-06-09 14:38:10','a');
INSERT INTO `uploadfile` VALUES ('227323','RobotDiggerResult_UCä¯ÀÀÆ÷_20130703224718.csv','C:\\Java\\apache-tomcat-7.0.23\\webapps\\RobotDigger_Web\\CSV/227323','2013-07-03 22:49:50','admin');
INSERT INTO `uploadfile` VALUES ('273666','RobotDiggerResult_ÉñÃíÌÓÍö2_20130609104828.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/273666','2013-06-09 14:44:56','administrator');
INSERT INTO `uploadfile` VALUES ('278466','RobotDiggerResult_Ë®¹ûÈÌÕß_20130609094704.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/278466','2013-06-09 14:44:02','administrator');
INSERT INTO `uploadfile` VALUES ('310886','RobotDiggerResult_Ë®¹ûÈÌÕß_20130609094704.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/310886','2013-06-09 14:43:20','administrator');
INSERT INTO `uploadfile` VALUES ('563971','RobotDiggerResult_Ä«¼£ÌìÆø_20130609092736.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/563971','2013-06-09 14:34:25','a');
INSERT INTO `uploadfile` VALUES ('604855','RobotDiggerResult_UCä¯ÀÀÆ÷_20130703230834.csv','C:\\Java\\apache-tomcat-7.0.23\\webapps\\RobotDigger_Web\\CSV/604855','2013-07-03 23:10:55','admin');
INSERT INTO `uploadfile` VALUES ('608905','RobotDiggerResult_UCä¯ÀÀÆ÷_20130704085029.csv','C:\\Java\\apache-tomcat-7.0.23\\webapps\\RobotDigger_Web\\CSV/608905','2013-07-04 08:52:14','admin');
INSERT INTO `uploadfile` VALUES ('672483','RobotDiggerResult_Î¢ÐÅ_20130609092715.csv','C:\\Java\\apache-tomcat-7.0.23\\webapps\\RobotDigger_Web\\CSV/672483','2013-06-30 14:57:54','a');
INSERT INTO `uploadfile` VALUES ('672951','RobotDiggerResult_Î¢ÐÅ_20130609094911.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/672951','2013-06-09 14:42:22','administrator');
INSERT INTO `uploadfile` VALUES ('753930','RobotDiggerResult_Íã¶¹¼Ô_20130705075228.csv','C:\\Java\\apache-tomcat-7.0.23\\webapps\\RobotDigger_Web\\CSV/753930','2013-07-05 07:55:31','administrator');
INSERT INTO `uploadfile` VALUES ('869890','RobotDiggerResult_UCä¯ÀÀÆ÷_20130703231334.csv','C:\\Java\\apache-tomcat-7.0.23\\webapps\\RobotDigger_Web\\CSV/869890','2013-07-03 23:15:17','admin');
INSERT INTO `uploadfile` VALUES ('993424','RobotDiggerResult_?360ÎÀÊ¿_20130609093327.csv','C:\\Java\\Tomcat 6.0\\webapps\\RobotDigger_Web\\CSV/993424','2013-06-09 14:40:30','admin');
/*!40000 ALTER TABLE `uploadfile` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `username` varchar(30) NOT NULL,
  `password` varchar(50) NOT NULL,
  `registrationtime` datetime NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` VALUES ('a','1','1899-12-29','fjhg1@163.com');
INSERT INTO `users` VALUES ('admin','admin','2013-06-07 14:06:11','fjhg1@163.com');
INSERT INTO `users` VALUES ('administrator','123456','2013-06-05 20:17:01','fjhg1@163.com');
INSERT INTO `users` VALUES ('mm','mm','2013-07-04 23:19:04','fjhg1@163.com');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

ALTER TABLE `uploadfile`
ADD CONSTRAINT `uploadfile_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
