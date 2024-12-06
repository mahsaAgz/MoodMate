(deftemplate sleep-pattern
    (slot user_id)
    (slot total-days (default 0))         ; Number of days tracked
    (slot total-satisfaction (default 0)) ; Sum of daily satisfaction scores
    (slot total-score (default 0))        ; Sum of daily sleep scores
    (slot avg-satisfaction)               ; Calculated average satisfaction
    (slot avg-score))                     ; Calculated average sleep score

(deftemplate sleep-long-term-rec
	 (slot user_id)
	 (slot message))
	
(defrule update-sleep-pattern
    (declare (salience 90))
    (sleep-quality (user_id ?id) 
                   (satisfaction ?s) 
                   (score ?score))
    ?pattern <- (sleep-pattern (user_id ?id)
                               (total-days ?days)
                               (total-satisfaction ?total-s)
                               (total-score ?total-score))
=>
    (bind ?new-days (+ ?days 1))
    (bind ?new-total-satisfaction (+ ?total-s ?s))
    (bind ?new-total-score (+ ?total-score ?score))
    (modify ?pattern 
        (total-days ?new-days)
        (total-satisfaction ?new-total-satisfaction)
        (total-score ?new-total-score)
        (avg-satisfaction (/ ?new-total-satisfaction ?new-days))
        (avg-score (/ ?new-total-score ?new-days)))
    (printout t "Updated sleep pattern for user " ?id ": Average satisfaction = "
              (/ ?new-total-satisfaction ?new-days) ", Average score = "
              (/ ?new-total-score ?new-days) crlf))

(defrule detect-long-term-sleep-issues
    (declare (salience 85))
    (sleep-pattern (user_id ?id)
                   (avg-satisfaction ?avg-s&:(< ?avg-s 2))  ; Poor satisfaction
                   (avg-score ?avg-score&:(< ?avg-score 50))) ; Low average score
    (not (recommendation (user_id ?id)
                         (message ?m&:(contains$ ?m "long-term sleep issues"))))
=>
    (bind ?recommendation (str-cat "Your average sleep satisfaction is " ?avg-s
                                   " and your average sleep score is " ?avg-score
                                   ". This indicates long-term sleep issues. "
                                   "Consider maintaining a consistent sleep schedule and consulting a healthcare professional."))
    (assert (sleep-long-term-rec
        (user_id ?id)
        (message ?recommendation)))
    (printout t "User " ?id ": Long-term sleep issues detected. " ?recommendation crlf))
