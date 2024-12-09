; Rule to track sleep pattern persistence
(defrule track-sleep-pattern-persistence
    (declare (salience 66))
    ?pattern1 <- (sleep-pattern (user_id ?id)
                               (pattern-type ?type)
                               (date2 ?d1)
                               (persistence ?p1))
    ?pattern2 <- (sleep-pattern (user_id ?id)
                               (pattern-type ?type)
                               (date1 ?d1)
                               (date2 ?d2))
    (not (processed-persistence (user_id ?id)
                              (day1 ?d1)
                              (day2 ?d2)))
=>
    (bind ?new-persistence (+ ?p1 1))
    (assert (sleep-pattern
        (user_id ?id)
        (date1 ?d1)
        (date2 ?d2)
        (persistence ?new-persistence)
        (pattern-type ?type)))
    (assert (processed-persistence
        (user_id ?id)
        (day1 ?d1)
        (day2 ?d2)))
    (printout t "Sleep pattern " ?type " persists for " ?new-persistence " days" crlf))

; Rule to detect severe insomnia pattern
(defrule detect-severe-insomnia
    (declare (salience 65))
    (sleep-score-trend (user_id ?id)
                       (date ?d1)
                       (total-score ?s1&:(< ?s1 40)))
    (sleep-score-trend (user_id ?id)
                       (date ?d2&:(= ?d2 (+ ?d1 1)))
                       (total-score ?s2&:(< ?s2 40)))
    (sleep-score-trend (user_id ?id)
                       (date ?d3&:(= ?d3 (+ ?d2 1)))
                       (total-score ?s3&:(< ?s3 40)))
    (sleep-score-trend (user_id ?id)
                       (date ?d4&:(= ?d4 (+ ?d3 1)))
                       (total-score ?s4&:(< ?s4 40)))
    (sleep-score-trend (user_id ?id)
                       (date ?d5&:(= ?d5 (+ ?d4 1)))
                       (total-score ?s5&:(< ?s5 40)))
    (not (sleep-disorder-assessment (user_id ?id)))
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "severe")
        (pattern-type "insomnia")
        (confidence 95)
        (evidence (str-cat "Severe sleep disturbance - consistently very poor sleep scores (<40) for 5+ consecutive days"))
        (recommendation "Immediate sleep specialist evaluation needed - Extended pattern of severe sleep disturbance detected"))))

; Rule to detect persistent moderate insomnia
(defrule detect-persistent-moderate-insomnia
    (declare (salience 64))
    (sleep-score-trend (user_id ?id)
                       (date ?d1)
                       (total-score ?s1&:(< ?s1 50)))
    (sleep-score-trend (user_id ?id)
                       (date ?d2&:(= ?d2 (+ ?d1 1)))
                       (total-score ?s2&:(< ?s2 50)))
    (sleep-score-trend (user_id ?id)
                       (date ?d3&:(= ?d3 (+ ?d2 1)))
                       (total-score ?s3&:(< ?s3 50)))
    (not (sleep-disorder-assessment (user_id ?id)))
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "insomnia")
        (confidence 85)
        (evidence (str-cat "Persistent poor sleep - consecutive sleep scores below 50 for 3+ days"))
        (recommendation "Clinical sleep evaluation recommended - Pattern suggests chronic sleep disturbance"))))

; Rule to detect chronic irregular sleep
(defrule detect-chronic-irregular-sleep
    (declare (salience 64))
    ?score1 <- (sleep-score-trend (user_id ?id)
                                 (date ?d1)
                                 (total-score ?s1))
    ?score2 <- (sleep-score-trend (user_id ?id)
                                 (date ?d2&:(= ?d2 (+ ?d1 1)))
                                 (total-score ?s2))
    ?score3 <- (sleep-score-trend (user_id ?id)
                                 (date ?d3&:(= ?d3 (+ ?d2 1)))
                                 (total-score ?s3))
    (test (and (> (abs (- ?s1 ?s2)) 30) (> (abs (- ?s2 ?s3)) 30)))  ; Large variations
    (not (sleep-disorder-assessment (user_id ?id)))
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "irregular")
        (confidence 90)
        (evidence (str-cat "Chronic irregular sleep pattern - significant daily variations over 3+ days"))
        (recommendation "Sleep specialist consultation needed - Persistent irregular sleep patterns detected"))))

; Rule to detect early sleep issues
(defrule detect-early-sleep-issues
    (declare (salience 63))
    ?score1 <- (sleep-score-trend (user_id ?id)
                                 (date ?d1)
                                 (total-score ?s1&:(< ?s1 60)))
    ?score2 <- (sleep-score-trend (user_id ?id)
                                 (date ?d2&:(= ?d2 (+ ?d1 1)))
                                 (total-score ?s2&:(< ?s2 60)))
    (not (sleep-disorder-assessment (user_id ?id)))
=>
    (assert (sleep-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type "disturbed")
        (confidence 75)
        (evidence (str-cat "Early signs of sleep disturbance - suboptimal sleep scores for consecutive days"))
        (recommendation "Monitor sleep patterns and consider sleep hygiene evaluation if issues persist"))))

; Rule to print sleep disorder assessment
(defrule print-sleep-disorder-assessment
    (declare (salience 62))
    ?assessment <- (sleep-disorder-assessment (user_id ?id)
                                            (risk-level ?risk)
                                            (pattern-type ?type)
                                            (confidence ?conf)
                                            (evidence ?ev)
                                            (recommendation ?rec))
=>
    (printout t crlf "=== Sleep Disorder Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Pattern Type: " ?type crlf)
    (printout t "Confidence: " ?conf "%" crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Clinical Recommendations: " ?rec crlf)
    (printout t "==========================================" crlf))