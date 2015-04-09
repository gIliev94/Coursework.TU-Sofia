INSERT INTO `warehouse`.`products` (`id`,`group`,`brand`,`model`,`quantity`,`price`)
VALUES	(NULL,"Hladilnici","ARISTON","A80",1000,250),
	(NULL,"Peralni","MIELLE","AQUA",1000,355.5),
	(NULL,"Hladilnici","ARIA","SECOND",1000,440.99),
	(NULL,"Hladilnici","SHOCK","3300",1000,80),
	(NULL,"Hladilnici","CORONA","A+",1000,52.5);

INSERT INTO `warehouse`.`customers` (`id`,`name`)
VALUES	(NULL,"Ivan Mihaylov"),
	(NULL,"Georgi Jelqzkov"),
	(NULL,"Kaloqn Metodiev");

INSERT INTO `warehouse`.`discounts` (`id`,`name`,`required_quantity`)
VALUES	(NULL,"No Discount",NULL),
	(NULL,"Otstupka 5%",20),
	(NULL,"Otstupka 10%",40),
	(NULL,"Otstupka 15%",60),
	(NULL,"Otstupka 20%",80);

INSERT INTO `warehouse`.`orders`(`customer_id` ,`product_id` ,`order_quantity`,`discount_id`) 
VALUES	(1,2,20,1),
	(2,1,40,2),
	(3,3,60,3),
	(1,5,7,0),	
	(3,4,80,4);