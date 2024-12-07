
; Rule to detect consistently poor sleep - potential insomnia
(defrule detect-poor-sleep-pattern
    (declare (salience 65))
    (not (sleep-disorder-assessment (user_id ?id)))
    ?score1 <- (sleep-score-trend (user_id ?id) 
                                 (date ?date1)
                                 (total-score ?s1&:(< ?s1 50)))  ; Poor sleep score
    ?score2 <- (sleep-score-trend (user_id ?id) 
                                 (date ?date2&:(= (- ?date2 ?date1) 1))  ; Consecutive days
                                 (total-score ?s2&:(< ?s2 50)))  ; Another poor sleep score
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "insomnia")
        (evidence (str-cat "Consistently poor sleep scores under 50 detected (" ?s1 ", " ?s2 ")"))
        (recommendation "Clinical sleep evaluation recommended - potential chronic insomnia detected"))))

; Rule to detect irregular sleep patterns
(defrule detect-irregular-sleep
    (declare (salience 65))
    (not (sleep-disorder-assessment (user_id ?id)))
    ?score1 <- (sleep-score-trend (user_id ?id) 
                                 (date ?date1)
                                 (total-score ?s1))
    ?score2 <- (sleep-score-trend (user_id ?id) 
                                 (date ?date2&:(= (- ?date2 ?date1) 1))  ; Consecutive days
                                 (total-score ?s2))
    (test (> (abs (- ?s1 ?s2)) 30))  ; Large variation between consecutive days
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type "irregular")
        (evidence (str-cat "High variability in sleep quality detected (" ?s1 ", " ?s2 ")"))
        (recommendation "Consider sleep hygiene evaluation - significant sleep pattern irregularity observed"))))

; Rule to detect oversleeping pattern
(defrule detect-oversleeping
    (declare (salience 65))
    (not (sleep-disorder-assessment (user_id ?id)))
    ?sleep1 <- (sleep-quality (user_id ?id) 
                             (sleep-decimal ?sd1)
                             (wake-decimal ?wd1))
    ?sleep2 <- (sleep-quality (user_id ?id) 
                             (sleep-decimal ?sd2)
                             (wake-decimal ?wd2))
    (test (and (> (abs (- ?wd1 ?sd1)) 9)   ; Sleep duration > 9 hours
               (> (abs (- ?wd2 ?sd2)) 9)))  ; On consecutive recordings
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type "hypersomnia")
        (evidence "Extended sleep duration over 9 hours detected repeatedly")
        (recommendation "Evaluate for possible hypersomnia or underlying health conditions"))))

; Rule to store daily sleep scores for trend analysis
(defrule store-sleep-score-trend
    (declare (salience 69))
    ?sleep <- (sleep-quality (user_id ?id) (score ?score))
    ?date <- (get-current-date)  ; Need to assert current date in YYYYMMDD format
=>
    (assert (sleep-score-trend
        (user_id ?id)
        (date ?date)
        (total-score ?score))))