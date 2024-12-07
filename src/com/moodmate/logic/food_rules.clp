; Rule to detect consistently low food scores - potential restrictive pattern
(defrule detect-restrictive-pattern
    (declare (salience 65))
    (not (restrictive-pattern-assessed (user_id ?id)))  ; Specific control fact
    ?score1 <- (food-score-trend (user_id ?id) 
                                (date ?date1)
                                (total-score ?s1&:(< ?s1 40)))
    ?score2 <- (food-score-trend (user_id ?id) 
                                (date ?date2)
                                (total-score ?s2&:(< ?s2 40)))
    (test (< ?date1 ?date2))  ; Ensure date1 is before date2
=>
    (assert (restrictive-pattern-assessed (user_id ?id)))  ; Assert control fact
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "restrictive")
        (evidence (str-cat "Consistently low food scores under 40 detected (" ?s1 ", " ?s2 ")"))
        (recommendation "Urgent clinical evaluation needed - pattern of restricted eating detected"))))
        
; Rule to detect highly erratic eating patterns
(defrule detect-erratic-pattern
    (declare (salience 65))
    (not (eating-disorder-assessment (user_id ?id) (pattern-type "irregular")))
    ?score1 <- (food-score-trend (user_id ?id) 
                                (date ?date1)
                                (total-score ?s1))
    ?score2 <- (food-score-trend (user_id ?id) 
                                (date ?date2&:(< ?date1 ?date2))  ; Ensure chronological order
                                (total-score ?s2))
    (test (> (abs (- ?s1 ?s2)) 40))   ; Large variation between scores
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type "irregular")
        (evidence (str-cat "Large variations in food scores detected (" ?s1 ", " ?s2 ")"))
        (recommendation "Clinical consultation recommended - significant eating pattern fluctuations observed"))))

; Rule to detect consistently excessive eating - potential binge pattern
(defrule detect-binge-pattern
    (declare (salience 65))
    (food-score-trend (user_id ?id) 
                     (total-score ?s1&:(> ?s1 80)))
    (food-score-trend (user_id ?id) 
                     (total-score ?s2&:(> ?s2 80)))
    (test (neq ?s1 ?s2))  ; Different scores to ensure different days
    (not (binge-pattern-assessed (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "binge")
        (evidence (str-cat "Consistently high food scores above 80 detected (" ?s1 ", " ?s2 ")"))
        (recommendation "Clinical evaluation recommended - potential binge eating pattern detected"))))

; Rule to store daily food scores for trend analysis
(defrule store-food-score-trend
    (declare (salience 69))
    (food-score (user_id ?id) (total-score ?score))
    ?date <- (get-current-date)  ; You'll need to assert current date in YYYYMMDD format
=>
    (assert (food-score-trend
        (user_id ?id)
        (date ?date)
        (total-score ?score))))