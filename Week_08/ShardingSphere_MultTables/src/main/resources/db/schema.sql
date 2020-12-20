DROP TABLE IF EXISTS `order_info`;

CREATE TABLE IF NOT EXISTS `order_info` (
  `order_id` bigint(20) unsigned NOT NULL,
  `user_id` bigint(20) NOT NULL ,
  `price` decimal(20) unsigned NOT NULL ,
  `create_time` bigint(20) unsigned NOT NULL ,
  `update_time` bigint(20) unsigned NOT NULL ,
  PRIMARY KEY (`order_id`)
);