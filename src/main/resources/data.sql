SET @n_category = 10;

INSERT INTO categories (name)
SELECT 'Cash'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Credit Card'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Debit Card'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Recurrence'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Bank Account'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Save'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Coupon'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Check'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Investments'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;

INSERT INTO categories (name)
SELECT 'Others'
WHERE (SELECT COUNT(*) FROM categories) < @n_category;