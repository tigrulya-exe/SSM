SELECT equalColumn, another, originalColumn
FROM tableName
WHERE ((((equalColumn > (:equalColumn)) AND (equalColumn < (:$_equalColumn1)) AND (another >= (:another)) AND (originalColumn = (:originalColumn))) OR (another <= (:$_another1)) OR (list IN (:list)) OR (anotherOriginalColumn = (:anotherOriginalColumn))) AND (another LIKE (:$_another2)))
LIMIT 777
