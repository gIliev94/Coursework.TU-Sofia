INSERT INTO `warehouse`.`products` (`id`,`group`,`brand`,`model`,`quantity`,`price`)
VALUES(NULL,"Hladilnici","ARISTON","A80",994,250),
	(NULL,"Peralni","MIELLE","AQUA",1000,355.5),
	(NULL,"Pechki","ARIA","SECOND",1000,440.99),
	(NULL,"Mikrovulnovi","SHOCK","3300",1000,80),
	(NULL,"Absorbatori","CORONA","A++",1000,52.5),
	(NULL,"Hladilnici","AVANARD","+CC",0,300);

INSERT INTO `warehouse`.`customers` (`id`,`name`)
VALUES(NULL,"Ivan Mihaylov"),
	(NULL,"Georgi Jelqzkov"),
	(NULL,"Kaloqn Metodiev");

INSERT INTO `warehouse`.`discounts` (`id`,`name`,`required_quantity`)
VALUES	(0,"No Discount",NULL),
	(1,"Otstupka 5%",20),
	(2,"Otstupka 10%",40),
	(3,"Otstupka 15%",60),
	(4,"Otstupka 20%",80);

