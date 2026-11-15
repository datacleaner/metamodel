-- Schema of the "dellstore2" sample database (pgfoundry Dell DVD Store),
-- matching the tables and columns asserted by the PostgreSQL integration
-- tests in metamodel-jdbc. Tables are intentionally left empty; the tests
-- that need data create their own.
--
-- References:
--   http://pgfoundry.org/projects/dbsamples/
--   https://linux.dell.com/dvdstore/

CREATE TABLE categories (
    category integer NOT NULL,
    categoryname character varying(50) NOT NULL
);

CREATE TABLE cust_hist (
    customerid integer NOT NULL,
    orderid integer NOT NULL,
    prod_id integer NOT NULL
);

CREATE TABLE customers (
    customerid serial PRIMARY KEY,
    firstname character varying(50) NOT NULL,
    lastname character varying(50) NOT NULL,
    address1 character varying(50) NOT NULL,
    address2 character varying(50),
    city character varying(50) NOT NULL,
    state character varying(50),
    zip integer,
    country character varying(50) NOT NULL,
    region smallint NOT NULL,
    email character varying(50),
    phone character varying(50),
    creditcardtype integer NOT NULL,
    creditcard character varying(50) NOT NULL,
    creditcardexpiration character varying(50) NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(50) NOT NULL,
    age smallint,
    income integer,
    gender character varying(1)
);

CREATE TABLE inventory (
    prod_id integer NOT NULL,
    quan_in_stock integer NOT NULL,
    sales integer NOT NULL
);

CREATE TABLE orderlines (
    orderlineid integer NOT NULL,
    orderid integer NOT NULL,
    prod_id integer NOT NULL,
    quantity smallint NOT NULL,
    orderdate date NOT NULL
);

CREATE TABLE orders (
    orderid serial PRIMARY KEY,
    orderdate date NOT NULL,
    customerid integer NOT NULL,
    netamount numeric(12,2) NOT NULL,
    tax numeric(12,2) NOT NULL,
    totalamount numeric(12,2) NOT NULL
);

CREATE TABLE products (
    prod_id serial NOT NULL,
    category integer NOT NULL,
    title character varying(50) NOT NULL,
    actor character varying(50) NOT NULL,
    price numeric(12,2) NOT NULL,
    special smallint,
    common_prod_id integer NOT NULL
);

CREATE TABLE reorder (
    prod_id integer NOT NULL,
    date_low date NOT NULL,
    quan_low smallint NOT NULL,
    date_reordered date NOT NULL,
    quan_reordered smallint NOT NULL,
    date_expected date NOT NULL
);

ALTER TABLE ONLY cust_hist
    ADD CONSTRAINT cust_hist_customerid_fkey FOREIGN KEY (customerid) REFERENCES customers(customerid);

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_customerid_fkey FOREIGN KEY (customerid) REFERENCES customers(customerid);
