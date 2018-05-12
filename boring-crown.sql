CREATE DATABASE tj;

USE tj;

CREATE TABLE `USER` (
  `Userid` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(20) NOT NULL,
  `Password` varchar(20) NOT NULL,
  `Role` varchar(20) NOT NULL,
  `Permission` varchar(20) DEFAULT NULL,
  `Status` int NOT NULL,
  `AddTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`Userid`),
  UNIQUE KEY `UK_Username`(`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- insert into user(username, password, role, permission, status) values('jury1', '123456', 'jury', '1', '1000');
insert into user(username, password, role, status) values('admin', '123456', 'admin', '2000');


CREATE TABLE `SCORE` (
  `Juryid` int(11) NOT NULL,
  `Productid` int(11) NOT NULL,
  `Criteriaid` int(11) NOT NULL,
  `Score` double DEFAULT NULL,
  `AddTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`Juryid`, `Productid`, `Criteriaid`),
  KEY `IX_Juryid_Criteriaid_Productid`(`Juryid`, `Criteriaid`, `Productid`),
  KEY `IX_Criteriaid`(`Criteriaid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ABNORMAL` (
  `Productid` int(11) NOT NULL,
  `Criteriaid` int(11) NOT NULL,
  `Partgrade` double NOT NULL,
  `JuryidA` int(11) DEFAULT NULL,
  `ScoreA` double DEFAULT NULL,
  `JuryidB` int(11) DEFAULT NULL,
  `ScoreB` double DEFAULT NULL,
  `ScoreAdmin` double DEFAULT NULL,
  `AddTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`Productid`, `Criteriaid`),
  KEY `IX_Criteriaid_Productid`(`Criteriaid`, `Productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `CRITERIA` (
  `Criteriaid` int(11) NOT NULL AUTO_INCREMENT,
  `Content1` varchar(20) DEFAULT NULL,
  `Content2` varchar(40) DEFAULT NULL,
  `Content3` varchar(40) DEFAULT NULL,
  `Type` varchar(10) NOT NULL,
  `Partgrade` double NOT NULL,
  `Totalgrade` double NOT NULL,
  `Illustration` tinytext DEFAULT NULL,
  CHECK(`TYPE` in ('客观性', '主观性')),
  `AddTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`Criteriaid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
