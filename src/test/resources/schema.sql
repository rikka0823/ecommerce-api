CREATE TABLE IF NOT EXISTS product
(
    product_id         INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    product_name       VARCHAR(128) NOT NULL,
    category           VARCHAR(32)  NOT NULL,
    image_url          VARCHAR(256) NOT NULL,
    price              INT          NOT NULL,
    stock              INT          NOT NULL,
    description        VARCHAR(1024),
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS `user` (
  user_id int PRIMARY KEY AUTO_INCREMENT,
  email varchar(256) NOT NULL UNIQUE,
  password varchar(256) DEFAULT NULL,
  created_date timestamp NOT NULL,
  last_modified_date timestamp NOT NULL,
  provider_user_id varchar(256) DEFAULT NULL UNIQUE,
  provider varchar(64) DEFAULT NULL,
  refresh_token varchar(1024) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    order_id           INT       PRIMARY KEY AUTO_INCREMENT,
    user_id            INT       NOT NULL,
    total_amount       INT       NOT NULL, -- 訂單總花費
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id      INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL, -- 商品數量
    amount        INT NOT NULL  -- 商品花費
);

CREATE TABLE IF NOT EXISTS role (
  role_id int NOT NULL AUTO_INCREMENT,
  role_name varchar(256) NOT NULL,
  PRIMARY KEY (role_id)
);

CREATE TABLE IF NOT EXISTS user_has_role (
  user_has_role_id int NOT NULL AUTO_INCREMENT,
  user_id int NOT NULL,
  role_id int NOT NULL,
  PRIMARY KEY (user_has_role_id)
);
