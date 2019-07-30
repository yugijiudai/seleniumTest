/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : selenium

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 30/07/2019 14:16:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for demo_test
-- ----------------------------
DROP TABLE IF EXISTS `demo_test`;
CREATE TABLE `demo_test`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号(必填,相当于步骤,默认从1开始)',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '相关描述(必填)',
  `model` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '步骤模块(必填)',
  `elementAction` enum('CLICK','SEND_KEYS','SWITCH_TO_FRAME','CLEAR','HOVER','GET_TEXT','ALERT','WAIT','RUN_SCRIPT','REFRESH','SWITCH_WINDOW','SWITCH_MY_FRAME','RUN_METHOD') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '查找这个元素后操作的动作(必填)',
  `clickAction` enum('JS','API','BY_TAG_TYPE','RIGHT_CLICK','DOUBLE_CLICK','') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '点击使用的方法',
  `element` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '要查找的元素(非必填)',
  `findType` enum('ID','NAME','CLASS_NAME','TAG_NAME','XPATH','LINK_TEXT','CSS_SELECTOR') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '元素查询的方式(非必填)',
  `ext` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预留字段',
  `valid` enum('Y','N') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Y' COMMENT '是否有效(必填)',
  `callBack` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行回调',
  `script` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `wait` int(5) NULL DEFAULT NULL COMMENT '自定义查询这个dom节点需要等待的时间(非必填,单位:秒)',
  `retry` int(5) NULL DEFAULT NULL COMMENT '自定义查询这个dom节点重试次数(非必填)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'demo' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_test
-- ----------------------------
INSERT INTO `demo_test` VALUES (1, '点击登录', '什么都不做直接点登录', 'CLICK', 'API', 'btn', 'ID', NULL, 'Y', '{className: \'com.lml.selenium.demo.asserts.DemoAssert\', methodName: \'assertTestLogin01\', args:[\'用户不存在\']}', NULL, NULL, NULL);
INSERT INTO `demo_test` VALUES (2, '输入用户名haha', '输入错密码', 'SEND_KEYS', NULL, 'name', 'ID', 'haha', 'Y', NULL, NULL, NULL, NULL);
INSERT INTO `demo_test` VALUES (3, '输入密码1', '输入错密码', 'SEND_KEYS', NULL, 'pass', 'ID', '1', 'Y', NULL, NULL, NULL, NULL);
INSERT INTO `demo_test` VALUES (4, '点击登录', '输入错密码', 'CLICK', 'API', 'btn', 'ID', NULL, 'Y', '{className: \'com.lml.selenium.demo.asserts.DemoAssert\', methodName: \'assertTestLogin02\', args:[\'密码错误\']}', NULL, NULL, NULL);
INSERT INTO `demo_test` VALUES (5, '输入用户名', '登录成功', 'SEND_KEYS', NULL, '#name', 'CSS_SELECTOR', 'lml', 'Y', NULL, NULL, NULL, NULL);
INSERT INTO `demo_test` VALUES (6, '输入密码', '登录成功', 'SEND_KEYS', NULL, '//input[@id=\'pass\']', 'XPATH', '111111', 'Y', NULL, NULL, NULL, NULL);
INSERT INTO `demo_test` VALUES (7, '点击登录', '登录成功', 'CLICK', 'API', 'btn', 'ID', NULL, 'Y', '{className: \'com.lml.selenium.demo.asserts.DemoAssert\', methodName: \'assertTestLogin03\', args:[\'登录成功\']}', NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
