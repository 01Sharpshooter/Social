SELECT * FROM main.t_messages mess
WHERE mess.id IN(
	SELECT MAX(m.id) FROM main.t_messages m
	WHERE(m.receiverid = 1 OR m.senderid = 1)
	GROUP BY (m.receiverid+m.senderid))
ORDER BY mess.id DESC;