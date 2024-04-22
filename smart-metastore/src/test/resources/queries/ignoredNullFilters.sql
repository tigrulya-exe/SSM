SELECT *
FROM tableName
WHERE ((nonNullColumn1 LIKE (:nonNullColumn1)) AND ((nonNullColumn2 > (:nonNullColumn2))))
LIMIT 12
