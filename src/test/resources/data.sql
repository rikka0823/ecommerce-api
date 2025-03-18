--product
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('蘋果（澳洲）', 'FOOD', 'https://cdn.pixabay.com/photo/2016/11/30/15/00/apples-1872997_1280.jpg', 30, 10, '這是來自澳洲的蘋果！', '2022-03-19 17:00:00', '2022-03-22 18:00:00');
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('蘋果（日本北海道）', 'FOOD', 'https://cdn.pixabay.com/photo/2017/09/26/13/42/apple-2788662_1280.jpg', 300, 5, '這是來自日本北海道的蘋果！', '2022-03-19 18:30:00', '2022-03-19 18:30:00');
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('好吃又鮮甜的蘋果橘子', 'FOOD', 'https://cdn.pixabay.com/photo/2021/07/30/04/17/orange-6508617_1280.jpg', 10, 50, null, '2022-03-20 09:00:00', '2022-03-24 15:00:00');
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('Toyota', 'CAR', 'https://cdn.pixabay.com/photo/2014/05/18/19/13/toyota-347288_1280.jpg', 100000, 5, null, '2022-03-20 09:20:00', '2022-03-20 09:20:00');
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('BMW', 'CAR', 'https://cdn.pixabay.com/photo/2018/02/21/03/15/bmw-m4-3169357_1280.jpg', 500000, 3, '渦輪增壓，直列4缸，DOHC雙凸輪軸，16氣門', '2022-03-20 12:30:00', '2022-03-20 12:30:00');
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('Benz', 'CAR', 'https://cdn.pixabay.com/photo/2017/03/27/14/56/auto-2179220_1280.jpg', 600000, 2, null, '2022-03-21 20:10:00', '2022-03-22 10:50:00');
INSERT INTO product (product_name, category, image_url, price, stock, description, created_date, last_modified_date) VALUES ('Tesla', 'CAR', 'https://cdn.pixabay.com/photo/2021/01/15/16/49/tesla-5919764_1280.jpg', 450000, 5, '世界最暢銷的充電式汽車', '2022-03-21 23:30:00', '2022-03-21 23:30:00');

--user
INSERT INTO `user` (user_id, email, password, created_date, last_modified_date) VALUES (12,'test1@gmail.com','$2a$10$aUfRIOYPn/C2pwjLnz3iMuFgJW.5QSp2aoaj1cL1qMP/dYMsF0Woe','2025-02-28 14:26:27','2025-02-28 14:26:27');
INSERT INTO `user` (user_id, email, password, created_date, last_modified_date) VALUES (13,'test3@gmail.com','$2a$10$YBV0mwoLiZcx4H.O1crhzesqME/WAR12RZ4vsx3SMy2hFDe.JtMuC','2025-02-28 19:14:07','2025-02-28 19:14:07');
INSERT INTO `user` (user_id, email, password, created_date, last_modified_date) VALUES (14,'test2@gmail.com','$2a$10$cSADPsDTfXcYDjWzzusBHuiI33JYHmI3aFCfP7IF3AkhneRfDlzZS','2025-03-04 23:12:37','2025-03-04 23:12:37');

--order_item
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (36,14,1,1,30);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (37,14,3,1,10);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (38,14,4,1,100000);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (39,15,1,1,30);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (40,15,2,1,300);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (41,15,4,1,100000);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (42,16,1,1,30);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (43,16,1,1,30);
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, amount) VALUES (44,16,4,1,100000);

--orders
INSERT INTO orders (order_id, user_id, total_amount, created_date, last_modified_date) VALUES (14,13,100040,'2025-03-03 18:42:44','2025-03-03 18:42:44');
INSERT INTO orders (order_id, user_id, total_amount, created_date, last_modified_date) VALUES (15,13,100330,'2025-03-03 21:29:29','2025-03-03 21:29:29');
INSERT INTO orders (order_id, user_id, total_amount, created_date, last_modified_date) VALUES (16,13,100060,'2025-03-04 12:16:15','2025-03-04 12:16:15');

--role
INSERT INTO role (role_id, role_name) VALUES (1,'ROLE_ADMIN');
INSERT INTO role (role_id, role_name) VALUES (2,'ROLE_SELLER');
INSERT INTO role (role_id, role_name) VALUES (3,'ROLE_CUSTOMER');

--user_has_role
INSERT INTO user_has_role (user_has_role_id, user_id, role_id) VALUES (8,12,1);
INSERT INTO user_has_role (user_has_role_id, user_id, role_id) VALUES (9,13,2);
INSERT INTO user_has_role (user_has_role_id, user_id, role_id) VALUES (10,13,3);
INSERT INTO user_has_role (user_has_role_id, user_id, role_id) VALUES (11,14,3);