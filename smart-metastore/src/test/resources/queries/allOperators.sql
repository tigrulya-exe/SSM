SELECT id, anotherColumn
FROM testTable
WHERE (((gtColumn > (:gtColumn)) AND (ltColumn < (:ltColumn)) AND (gteColumn >= (:gteColumn)) AND (lteColumn <= (:lteColumn)) AND ((betweenInclusiveColumn >= (:betweenInclusiveColumn)) AND (betweenInclusiveColumn <= (:$_betweenInclusiveColumn1)))) OR (anotherColumn = (:anotherColumn)) OR (listMember IN (:listMember)) OR (strColumn LIKE (:strColumn)) OR ((betweenColumn > (:betweenColumn)) AND (betweenColumn < (:$_betweenColumn1))))
ORDER BY id ASC, ascColumn ASC, descColumn DESC
LIMIT 10
OFFSET 0
