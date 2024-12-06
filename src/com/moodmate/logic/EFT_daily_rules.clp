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

; Rule to extract month from day (YYYYMMDD -> YYYYMM)
(deffunction extract-month (?day)
    (return (div ?day 100)))

; Rule to initialize monthly emotion totals
(defrule initialize-monthly-emotion-totals
    (declare (salience 89))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d) 
                          (emotion-name ?ename))
    (not (monthly-emotion-total (user_id ?id) 
                               (month =(extract-month ?d)) 
                               (emotion-name ?ename)))
=>
    (assert (monthly-emotion-total (user_id ?id) 
                                 (month (extract-month ?d)) 
                                 (emotion-name ?ename))))

; Rule to sum up daily emotions into monthly totals
(defrule sum-monthly-emotions
    (declare (salience 88))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name ?ename)
                          (avg-percentage ?p)
                          (reading-count ?rc))
    ?mtotal <- (monthly-emotion-total (user_id ?id) 
                                    (month =(extract-month ?d)) 
                                    (emotion-name ?ename)
                                    (sum ?sum)
                                    (count ?count))
    (not (processed-monthly-reading (user_id ?id) 
                                  (day ?d)
                                  (emotion-name ?ename)))
=>
    (modify ?mtotal 
        (sum (+ ?p ?sum))
        (count (+ 1 ?count)))
    (assert (processed-monthly-reading (user_id ?id) 
                                     (day ?d)
                                     (emotion-name ?ename)))
    (printout t "Adding monthly reading for " ?ename " from day " ?d 
              ": " ?p "% (Monthly Total: " (+ ?p ?sum) 
              ", Days: " (+ 1 ?count) ")" crlf))

; Rule to calculate monthly averages
(defrule calculate-monthly-emotion-average
    (declare (salience 87))
    ?mtotal <- (monthly-emotion-total (user_id ?id)
                                    (month ?m)
                                    (emotion-name ?ename)
                                    (sum ?sum)
                                    (count ?count&:(> ?count 0)))
    (not (monthly-emotion-summary (user_id ?id) 
                                (month ?m) 
                                (emotion-name ?ename)))
=>
    (bind ?avg (/ ?sum ?count))
    (assert (monthly-emotion-summary 
        (user_id ?id)
        (month ?m)
        (emotion-name ?ename)
        (avg-percentage ?avg)
        (reading-count ?count)))
    (printout t "Month " ?m " average for " ?ename ": " 
              (round ?avg) "% (from " ?count " days)" crlf))