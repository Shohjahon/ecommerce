package sample.constant;

/**
 * Shoh Jahon tomonidan 7/31/2019 da qo'shilgan.
 */
public interface Query {
    public static final String create_salesman = "CREATE TABLE IF NOT EXISTS  salesman (\n" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  full_name VARCHAR(100),\n" +
            "  phone_number VARCHAR(50),\n" +
            "  address VARCHAR(100)\n" +
            ");";

    public static final String create_product_type = "CREATE TABLE IF NOT EXISTS  product_types(\n" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  product_type VARCHAR(50)\n" +
            ");";

    public static final String create_product = "CREATE TABLE IF NOT EXISTS products(\n" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  product_name VARCHAR(100),\n" +
            "  id_product_type INTEGER,\n" +
            "  description VARCHAR(100),\n" +
            "  CONSTRAINT fk_product_key FOREIGN KEY(id_product_type) REFERENCES product_types(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED\n" +
            ");";

    public static final String create_sales_record = "CREATE TABLE IF NOT EXISTS sales_records(\n" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "id_product INTEGER,\n" +
            "id_salesman INTEGER,\n" +
            "input_price REAL,\n" +
            "output_price REAL,\n" +
            "sold_date DATE," +
            "sell_coefficent REAL DEFAULT 30," +
            "image_body BLOB," +
            "quantity INTEGER,\n" +
            "CONSTRAINT fk_id_product FOREIGN KEY (id_product) REFERENCES products(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,\n" +
            "CONSTRAINT fk_id_salesman FOREIGN KEY (id_salesman) REFERENCES salesman(id) ON DELETE CASCADE\n" +
            ");";

}
