SELECT COUNT(*)
FROM testTable
WHERE (((gtColumn > (:gtColumn)) AND (ltColumn < (:ltColumn)) AND (gteColumn >= (:gteColumn)) AND (lteColumn <= (:lteColumn))) OR (anotherColumn = (:anotherColumn)) OR (listMember IN (:listMember)) OR (strColumn LIKE (:strColumn)))
