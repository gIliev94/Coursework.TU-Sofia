DROP DATABASE IF EXISTS `warehouse`;

CREATE DATABASE `warehouse`;

CREATE TABLE `warehouse`.`products` (
	`id` TINYINT NOT NULL  AUTO_INCREMENT ,
	`group` VARCHAR( 100 ) NOT NULL ,
	`brand` VARCHAR( 30 ) NOT NULL ,
	`model` VARCHAR( 10 ) NOT NULL ,
	`quantity` SMALLINT NOT NULL ,
	`price` FLOAT( 2, 2 ) NOT NULL ,
	PRIMARY KEY ( `id` )
) ENGINE = InnoDB;

CREATE TABLE `warehouse`.`customers` (
	`id` TINYINT NOT NULL  AUTO_INCREMENT ,
	`name` VARCHAR( 255 ) NOT NULL ,
	PRIMARY KEY ( `id` )
) ENGINE = InnoDB;

CREATE TABLE `warehouse`.`discounts` (
	`id` TINYINT NOT NULL  AUTO_INCREMENT ,
	`name` VARCHAR( 50 ) NOT NULL ,
	`required_quantity`  SMALLINT NULL DEFAULT 0 ,
	PRIMARY KEY ( `id` )
) ENGINE = InnoDB;

CREATE TABLE `warehouse`.`orders` (
	`product_id` TINYINT NOT NULL,
	`order_quantity` SMALLINT NOT NULL ,
	`customer_id` TINYINT NOT NULL,
	`discount_id` TINYINT NULL DEFAULT 0,
	`reduced_price` FLOAT( 2, 2 ) NOT NULL ,
	FOREIGN KEY ( `customer_id` )
		REFERENCES `warehouse`.`customers`( `id` )
		ON DELETE CASCADE,
	FOREIGN KEY ( `product_id`)
		REFERENCES `warehouse`.`products`( `id` )
		ON DELETE CASCADE,	
	FOREIGN KEY ( `discount_id`)
		REFERENCES `warehouse`.`discounts`( `id` )
		ON DELETE CASCADE
) ENGINE = InnoDB;


