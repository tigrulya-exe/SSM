SELECT id, anotherColumn
FROM testTable
WHERE (((gtColumn > (:gtColumn)) AND (ltColumn < (:ltColumn)) AND (gteColumn >= (:gteColumn)) AND (lteColumn <= (:lteColumn))) OR (anotherColumn = (:anotherColumn)) OR (listMember IN (:listMember)) OR (strColumn LIKE (:strColumn)))
LIMIT 10
OFFSET 0
ORDER BY id ASC, ascColumn ASC, descColumn DESC
