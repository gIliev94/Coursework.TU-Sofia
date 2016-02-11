INSERT INTO `warehouse`.`products` (`id`,`group`,`brand`,`model`,`quantity`,`price`)
VALUES(NULL,"Fidges","ARISTON","A80",994,250),
	(NULL,"Dishwashers","MIELLE","AQUA",1000,355.5),
	(NULL,"Ovens","ARIA","SECOND",1000,440.99),
	(NULL,"Microwaves","SHOCK","3300",1000,80),
	(NULL,"Absorbatori","CORONA","A++",1000,52.5),
	(NULL,"Fridges","AVANARD","+CC",0,300);

INSERT INTO `warehouse`.`customers` (`id`,`name`)
VALUES(NULL,"Ivan Mihaylov"),
	(NULL,"Georgi Jelqzkov"),
	(NULL,"Kaloyan Metodiev");

INSERT INTO `warehouse`.`discounts` (`id`,`name`,`required_quantity`)
VALUES	(0,"No Discount",NULL),
	(1,"Discount 5%",20),
	(2,"Discount 10%",40),
	(3,"Discount 15%",60),
	(4,"Discount 20%",80);

