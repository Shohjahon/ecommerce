#inset to salesmane
INSERT INTO salesman (full_name, phone_number, address)
VALUES ('Раҳматалийев Шоҳжаҳон','+998909023747','Фарғона вил, Бувайда тум');
INSERT INTO salesman (full_name, phone_number, address)
VALUES ('Акбаров Аҳроржон','+998989025747','Фарғона вил, Бувайда тум');
INSERT INTO salesman (full_name, phone_number, address)
VALUES ('Мамарайимов Даврон','+998977020117','Тошкент вил,  Ангрен шаҳри');


#insert to product_types
INSERT INTO product_types (product_type) VALUES ('Ручка');
INSERT INTO product_types (product_type) VALUES ('Дафтар');
INSERT INTO product_types (product_type) VALUES ('Қоғоз');
INSERT INTO product_types (product_type) VALUES ('Ўчиргич');

#inset to products
INSERT INTO products (product_name, id_product_type)
VALUES ('Alfa',1);
INSERT INTO products (product_name, id_product_type)
VALUES ('Commons',1);
INSERT INTO products (product_name, id_product_type)
VALUES ('Toshkent(32 varoqli)',2);
INSERT INTO products (product_name, id_product_type)
VALUES ('Akfa(96 varoqli)',2);
INSERT INTO products (product_name, id_product_type)
VALUES ('Снегурочка',3);
INSERT INTO products (product_name, id_product_type)
VALUES ('Белая Тайга',3);
INSERT INTO products (product_name, id_product_type)
VALUES ('Maped',4);
INSERT INTO products (product_name, id_product_type)
VALUES ('Red Bull',4);

#insert to sales_records
INSERT INTO sales_records (id_product, id_salesman, input_price, output_price)
VALUES (1,1,200,500);
INSERT INTO sales_records (id_product, id_salesman, input_price, output_price)
VALUES (3,1,1200,5500);
INSERT INTO sales_records (id_product, id_salesman, input_price, output_price)
VALUES (7,2,2200,4500);