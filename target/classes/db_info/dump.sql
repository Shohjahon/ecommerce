CREATE DATABASE ecommerce;

SET NAMES 'utf8';
CREATE TABLE salesman (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(100),
  phone_number VARCHAR(50),
  address VARCHAR(100)
) DEFAULT CHARSET = utf8;

SET NAMES 'utf8';
CREATE TABLE product_types(
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  product_type VARCHAR(50)
) DEFAULT CHARSET = utf8;

SET NAMES 'utf8';
CREATE TABLE products(
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(100),
  id_product_type INTEGER,
  CONSTRAINT fk_product_key FOREIGN KEY(id_product_type) REFERENCES product_types(id) ON DELETE CASCADE
) DEFAULT CHARSET = utf8;

SET NAMES 'utf8';
CREATE TABLE sales_records(
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
id_product INTEGER,
id_salesman INTEGER,
input_price REAL,
output_price REAL,
CONSTRAINT fk_id_product FOREIGN KEY (id_product) REFERENCES products(id) ON DELETE CASCADE,
CONSTRAINT fk_id_salesman FOREIGN KEY (id_salesman) REFERENCES salesman(id) ON DELETE CASCADE
) DEFAULT CHARSET = utf8;