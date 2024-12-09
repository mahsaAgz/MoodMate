(deftemplate processing-completed (slot user_id) (slot day1) (slot day2))
(deftemplate processed-persistence-file2
    (slot user_id)
    (slot day1)
    (slot day2)
    (slot pattern-type))
    
; Rule to detect initial restrictive pattern
(defrule detect-initial-restrictive
    (declare (salience 85))
    ?score1 <- (food-score-trend (user_id ?id)
                                (date ?d1)
                                (total-score ?s1&:(< ?s1 40)))
    ?score2 <- (food-score-trend (user_id ?id)
                                (date ?d2&:(= ?d2 (+ ?d1 1)))
                                (total-score ?s2&:(< ?s2 40)))
    (not (processed-persistence-file2 (user_id ?id)
                              (day1 ?d1)
                              (day2 ?d2)))
    (not (processing-completed (user_id ?id) (day1 ?d1) (day2 ?d2)))
=>
    (assert (processed-persistence-file2
        (user_id ?id)
        (day1 ?d1)
        (day2 ?d2)
        (pattern-type "restrictive")))
    (assert (processing-completed
        (user_id ?id)
        (day1 ?d1)
        (day2 ?d2)))
    (printout t "Initial restrictive eating pattern detected for days " ?d1 " to " ?d2 crlf))


; Rule to detect continued restrictive pattern
(defrule detect-continued-restrictive
    (declare (salience 84))
    ?prev <- (processed-persistence-file2 (user_id ?id)
                                  (day2 ?d1)
                                  (pattern-type "restrictive"))
    ?score <- (food-score-trend (user_id ?id)
                               (date ?d2&:(= ?d2 (+ ?d1 1)))
                               (total-score ?s&:(< ?s 40)))
    (not (processed-persistence-file2 (user_id ?id)
                              (day1 ?d1)
                              (day2 ?d2)))
=>
    (assert (processed-persistence-file2
        (user_id ?id)
        (day1 ?d1)
        (day2 ?d2)
        (pattern-type "restrictive")))
    (printout t "Restrictive pattern continues on day " ?d2 crlf))

; Rule to detect severe restrictive eating disorder
(defrule assess-severe-restrictive
    (declare (salience 65))
    (food-score-trend (user_id ?id)
                      (date ?d1)
                      (total-score ?s1&:(< ?s1 40)))
    ; Check for 5 consecutive days of low scores
    (food-score-trend (user_id ?id)
                      (date ?d2&:(= ?d2 (+ ?d1 1)))
                      (total-score ?s2&:(< ?s2 40)))
    (food-score-trend (user_id ?id)
                      (date ?d3&:(= ?d3 (+ ?d2 1)))
                      (total-score ?s3&:(< ?s3 40)))
    (food-score-trend (user_id ?id)
                      (date ?d4&:(= ?d4 (+ ?d3 1)))
                      (total-score ?s4&:(< ?s4 40)))
    (food-score-trend (user_id ?id)
                      (date ?d5&:(= ?d5 (+ ?d4 1)))
                      (total-score ?s5&:(< ?s5 40)))
    (not (eating-disorder-assessment (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "severe")
        (pattern-type "restrictive")
        (confidence 95)
        (evidence (str-cat "Severe restrictive eating pattern - consistently very low food scores (below 40) for 5+ consecutive days from " ?d1 " to " ?d5))
        (recommendation "Immediate clinical intervention is strongly recommended. A severe restrictive eating pattern has been observed with very low food scores consistently over 5+ consecutive days (from ?d1 to ?d5). Early professional support can help address this behavior and mitigate health risks effectively."))))

; Rule to detect moderate restrictive eating disorder
(defrule assess-moderate-restrictive
    (declare (salience 64))
    (food-score-trend (user_id ?id)
                      (date ?d1)
                      (total-score ?s1&:(< ?s1 40)))
    ; Check for 3 consecutive days of low scores
    (food-score-trend (user_id ?id)
                      (date ?d2&:(= ?d2 (+ ?d1 1)))
                      (total-score ?s2&:(< ?s2 40)))
    (food-score-trend (user_id ?id)
                      (date ?d3&:(= ?d3 (+ ?d2 1)))
                      (total-score ?s3&:(< ?s3 40)))
    (not (eating-disorder-assessment (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type "restrictive")
        (confidence 80)
        (evidence (str-cat "Moderate restrictive eating pattern - consistently low food scores (below 40) for 3+ consecutive days from " ?d1 " to " ?d3))
        (recommendation "A clinical evaluation is advised to address a moderate restrictive eating pattern observed over 3+ consecutive days (from ?d1 to ?d3). Early intervention can prevent further escalation and promote healthier eating habits."))))

; Rule to print eating disorder assessment
(defrule print-eating-disorder-assessment
    (declare (salience 63))
    ?assessment <- (eating-disorder-assessment (user_id ?id)
                                             (risk-level ?risk)
                                             (pattern-type ?type)
                                             (confidence ?conf)
                                             (evidence ?ev)
                                             (recommendation ?rec))
=>
    (printout t crlf "=== Eating Disorder Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Pattern Type: " ?type crlf)
    (printout t "Confidence: " ?conf "%" crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Clinical Recommendations: " ?rec crlf)
    (printout t "=========================================" crlf))