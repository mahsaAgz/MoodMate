; Template for bipolar assessment
(deftemplate bipolar-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot confidence)        ; 0-100%
    (slot evidence)          ; Description of evidence
    (slot recommendation))   ; Clinical recommendations

; Rule to detect bipolar type I pattern (severe manic + depressive episodes)
(defrule assess-bipolar-1
    (declare (salience 75))
    ; Find severe manic episode
    (emotional-pattern (user_id ?id) 
                      (pattern-type "manic")
                      (intensity "severe")
                      (persistence ?p1&:(> ?p1 3)))  ; Persists more than 3 days
    ; Find depressive episode
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity ?di&:(or (eq ?di "moderate") (eq ?di "severe"))))
    (not (bipolar-assessment (user_id ?id)))  ; Haven't assessed yet
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 85)
        (evidence "Severe manic episode lasting more than 3 days with depressive episode")
        (recommendation "Urgent clinical evaluation recommended for potential Bipolar I Disorder"))))

; Rule to detect bipolar type II pattern (hypomanic + severe depressive)
(defrule assess-bipolar-2
    (declare (salience 75))
    ; No severe manic episodes
    (not (emotional-pattern (user_id ?id) 
                          (pattern-type "manic")
                          (intensity "severe")))
    ; Find hypomanic episode
    (emotional-pattern (user_id ?id)
                      (pattern-type "manic")
                      (intensity "moderate"))
    ; Find severe depressive episode
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity "severe"))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 80)
        (evidence "Hypomanic episode with severe depressive episode")
        (recommendation "Clinical evaluation recommended for potential Bipolar II Disorder"))))

; Rule to detect rapid cycling bipolar
(defrule assess-rapid-cycling
    (declare (salience 75))
    ; First switch
    (emotional-pattern (user_id ?id)
                      (pattern-type "switch")
                      (day ?d1))
    ; Second switch at a different time
    (emotional-pattern (user_id ?id)
                      (pattern-type "switch")
                      (day ?d2&:(> ?d2 ?d1)))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "severe")
        (confidence 90)
        (evidence "Multiple mood switches detected indicating rapid cycling")
        (recommendation "Immediate clinical evaluation recommended for rapid cycling bipolar disorder"))))

; Rule to detect mixed features
(defrule assess-mixed-features
    (declare (salience 75))
    (emotional-pattern (user_id ?id)
                      (pattern-type "mixed-mood")
                      (intensity "severe"))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 75)
        (evidence "Severe mixed mood episodes detected")
        (recommendation "Clinical evaluation recommended for bipolar disorder with mixed features"))))

; Rule to detect moderate risk
(defrule assess-moderate-bipolar-risk
    (declare (salience 74))
    (or (emotional-pattern (user_id ?id)
                          (pattern-type "manic")
                          (intensity "moderate"))
        (emotional-pattern (user_id ?id)
                          (pattern-type "depressive")
                          (intensity "moderate")))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "moderate")
        (confidence 60)
        (evidence "Moderate mood episodes detected")
        (recommendation "Monitor mood patterns and consider clinical consultation"))))

; Rule to print bipolar assessment
(defrule print-bipolar-assessment
    (declare (salience 73))
    ?assessment <- (bipolar-assessment (user_id ?id)
                                     (risk-level ?risk)
                                     (confidence ?conf)
                                     (evidence ?ev)
                                     (recommendation ?rec))
=>
    (printout t crlf "=== Bipolar Disorder Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Confidence: " ?conf "%" crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Recommendation: " ?rec crlf)
    (printout t "=====================================" crlf))