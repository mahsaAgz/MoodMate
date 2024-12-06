; Rule to initialize emotion totals
(defrule initialize-emotion-totals
    (declare (salience 92))
    (normalized-emotion (user_id ?id) 
                       (day ?d) 
                       (emotion-name ?ename))
    (not (emotion-total (user_id ?id) 
                       (day ?d) 
                       (emotion-name ?ename)))
=>
    (assert (emotion-total (user_id ?id) 
                          (day ?d) 
                          (emotion-name ?ename) 
                          (sum 0) 
                          (count 0))))

; Rule to add up emotion percentages and count readings
(defrule sum-emotion-readings
    (declare (salience 91))
    (normalized-emotion (user_id ?id) 
                       (day ?d)
                       (hour ?h)
                       (emotion-name ?ename)
                       (percentage ?p))
    ?total <- (emotion-total (user_id ?id) 
                            (day ?d) 
                            (emotion-name ?ename)
                            (sum ?sum)
                            (count ?count))
    (not (processed-reading (user_id ?id) 
                          (day ?d)
                          (hour ?h)
                          (emotion-name ?ename)))  ; Check if reading was already processed
=>
    (modify ?total 
        (sum (+ ?p ?sum))
        (count (+ 1 ?count)))
    (assert (processed-reading (user_id ?id) 
                             (day ?d)
                             (hour ?h)
                             (emotion-name ?ename)))  ; Mark as processed
    (printout t "Adding reading for " ?ename ": " ?p "% (Total: " (+ ?p ?sum) ", Count: " (+ 1 ?count) ")" crlf))

; Rule to calculate daily averages
(defrule calculate-daily-emotion-average
    (declare (salience 90))
    ?total <- (emotion-total (user_id ?id)
                            (day ?d)
                            (emotion-name ?ename)
                            (sum ?sum)
                            (count ?count&:(> ?count 0)))
    (not (daily-emotion-summary (user_id ?id) (day ?d) (emotion-name ?ename)))
=>
    (bind ?avg (/ ?sum ?count))
    (assert (daily-emotion-summary 
        (user_id ?id)
        (day ?d)
        (emotion-name ?ename)
        (avg-percentage ?avg)
        (reading-count ?count)))
        
    (printout t "Day " ?d " average for " ?ename ": " 
              (round ?avg) "% (from " ?count " readings)" crlf))