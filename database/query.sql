CREATE TABLE products (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    stock_method  VARCHAR(4) NOT NULL CHECK (stock_method IN ('FIFO','LIFO','CUMP')),
    unit          VARCHAR(20)
    ,min_stock     DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE stock_movements (
    id             SERIAL PRIMARY KEY,
    product_id     INTEGER NOT NULL REFERENCES products(id),
    movement_type  VARCHAR(3) NOT NULL CHECK (movement_type IN ('IN','OUT')),
    quantity       DECIMAL(10,2) NOT NULL,
    unit_price     DECIMAL(10,2),
    movement_date  DATE NOT NULL
);

CREATE TABLE pending_movements (
    id            SERIAL PRIMARY KEY,
    product_id    INTEGER NOT NULL REFERENCES products(id),
    movement_type VARCHAR(3) NOT NULL CHECK (movement_type IN ('IN','OUT')),
    quantity      DECIMAL(10,2) NOT NULL,
    unit_price    DECIMAL(10,2),
    movement_date DATE NOT NULL
);

UPDATE products SET min_stock = 3 WHERE id = 2;