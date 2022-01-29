/*
Navicat MySQL Data Transfer

Source Server         : yugi
Source Server Version : 50640
Source Host           : localhost:3306
Source Database       : selenium

Target Server Type    : MYSQL
Target Server Version : 50640
File Encoding         : 65001

Date: 2022-01-29 01:18:26
*/

SET
FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for demo_test
-- ----------------------------
DROP TABLE IF EXISTS `demo_test`;
CREATE TABLE `demo_test`
(
    `id`            int(11)                                                                                                                                           NOT NULL AUTO_INCREMENT COMMENT '编号(必填,相当于步骤,默认从1开始)',
    `description`   varchar(255)                                                                                                                                      NOT NULL COMMENT '相关描述(必填)',
    `model`         varchar(255)                                                                                                                                      NOT NULL COMMENT '步骤模块(必填)',
    `elementAction` enum ('CLICK','SEND_KEYS','CLEAR','HOVER','GET_TEXT','ALERT','WAIT','RUN_SCRIPT','REFRESH','SWITCH_WINDOW','SWITCH_MY_FRAME','RUN_METHOD','DRAG') NOT NULL COMMENT '查找这个元素后操作的动作(必填)',
    `clickAction`   enum ('JS','API','BY_TAG_TYPE','RIGHT_CLICK','DOUBLE_CLICK','')                                                                                            DEFAULT NULL COMMENT '点击使用的方法',
    `element`       varchar(2000)                                                                                                                                              DEFAULT NULL COMMENT '要查找的元素(非必填)',
    `findType`      enum ('ID','NAME','CLASS_NAME','TAG_NAME','XPATH','LINK_TEXT','CSS_SELECTOR')                                                                              DEFAULT NULL COMMENT '元素查询的方式(非必填)',
    `ext`           varchar(2000)                                                                                                                                              DEFAULT NULL COMMENT '预留字段',
    `valid`         enum ('Y','N')                                                                                                                                    NOT NULL DEFAULT 'Y' COMMENT '是否有效(必填)',
    `callBack`      varchar(255)                                                                                                                                               DEFAULT NULL COMMENT '执行回调',
    `wait`          int(8)                                                                                                                                                     DEFAULT NULL COMMENT '自定义查询这个dom节点需要等待的时间(非必填,单位:毫秒)',
    `retry`         int(5)                                                                                                                                                     DEFAULT NULL COMMENT '自定义查询这个dom节点重试次数(非必填)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='demo';

-- ----------------------------
-- Records of demo_test
-- ----------------------------
INSERT INTO `demo_test`
VALUES ('1', '点击登录', '什么都不做直接点登录', 'CLICK', 'API', 'btn', 'ID', null, 'Y',
        '{className: \'com.lml.selenium.demo.asserts.DemoAssert\', methodName: \'assertTestDemo01\', args:[\'用户不存在\']}', null, null);
INSERT INTO `demo_test`
VALUES ('2', '输入用户名haha', '输入错密码', 'SEND_KEYS', null, 'name', 'ID', 'haha', 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('3', '输入密码1', '输入错密码', 'SEND_KEYS', null, 'pass', 'ID', '1', 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('4', '点击登录', '输入错密码', 'CLICK', 'API', 'btn', 'ID', null, 'Y',
        '{className: \'com.lml.selenium.demo.asserts.DemoAssert\', methodName: \'assertTestDemo02\', args:[\'密码错误\']}', null, null);
INSERT INTO `demo_test`
VALUES ('5', '输入用户名', '登录成功', 'SEND_KEYS', null, '#name', 'CSS_SELECTOR', 'lml', 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('6', '输入密码', '登录成功', 'SEND_KEYS', null, '//input[@id=\'pass\']', 'XPATH', '111111', 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('7', '点击登录', '登录成功', 'CLICK', 'API', 'btn', 'ID', null, 'Y', '{className: \'com.lml.selenium.demo.asserts.DemoAssert\', methodName: \'assertTestDemo03\'}', null, null);
INSERT INTO `demo_test`
VALUES ('8', '输入alert脚本', '登录成功', 'RUN_SCRIPT', null, null, null, '{\'script\': \'alert(111)\'}', 'Y', null, '2000', null);
INSERT INTO `demo_test`
VALUES ('9', '点击alert', '登录成功', 'ALERT', null, null, null, null, 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('10', '切换到iframe', 'iframeSelf', 'SWITCH_MY_FRAME', null, '', null, '{\"type\": \"self\", \"url\": \"http://127.0.0.1:8080/iframe.html\"}', 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('12', '双击iframe里的按钮', 'iframeSelf', 'CLICK', 'DOUBLE_CLICK', 'dbClick', 'ID', null, 'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('13', '运行js脚本把dom带回去', 'iframeSelf', 'RUN_SCRIPT', null, null, null,
        '{\"script\": \"return document.getElementsByClassName(\'clzDiv\')\", \"callFn\": {\"className\": \"com.lml.selenium.demo.asserts.DemoAssert\", \"methodName\": \"assertTestDemo04\", \"args\":[\"我是div1,我是div2\"]}} }',
        'Y', null, null, null);
INSERT INTO `demo_test`
VALUES ('14', '点击alert', 'iframe', 'RUN_SCRIPT', null, null, null, '{\'script\': \'return document.getElementById(\"iframeBtn\")\'}', 'N', null, null, null);
