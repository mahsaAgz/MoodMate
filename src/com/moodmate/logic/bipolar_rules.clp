; Template for bipolar assessment
(deftemplate bipolar-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot confidence)        ; 0-100%
    (slot evidence)          ; Description of evidence
    (slot recommendation))   ; Clinical recommendations

; Rule to detect bipolar type I pattern with improved criteria
(defrule assess-bipolar-1
    (declare (salience 75))
    ; Find persistent severe manic pattern for at least 7 days
    (emotional-pattern (user_id ?id) 
                      (pattern-type "manic")
                      (intensity "severe")
                      (persistence ?p1&:(>= ?p1 7)))
    ; Find severe depressive pattern
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity "severe")
                      (persistence ?p2&:(>= ?p2 14)))  ; Added minimum duration for depression
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "severe")
        (confidence 95)
        (evidence (str-cat "Severe manic pattern for " ?p1 " days and severe depressive pattern for " ?p2 " days"))
        (recommendation "Immediate consultation with a mental health professional is strongly advised. The observed extended severe manic and depressive episodes are indicative of Bipolar I Disorder, and early intervention can significantly improve management and outcomes."))))

; Rule to detect bipolar type II pattern with improved criteria
(defrule assess-bipolar-2
    (declare (salience 75))
    ; No severe manic episodes
    (not (emotional-pattern (user_id ?id) 
                          (pattern-type "manic")
                          (intensity "severe")))
    ; Find hypomanic episode with minimum duration
    (emotional-pattern (user_id ?id)
                      (pattern-type "manic")
                      (intensity "moderate")
                      (persistence ?p1&:(>= ?p1 4)))
    ; Find severe depressive episode with minimum duration
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity "severe")
                      (persistence ?p2&:(>= ?p2 14)))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 90)
        (evidence (str-cat "Moderate manic (hypomanic) pattern for " ?p1 " days with severe depressive pattern for " ?p2 " days"))
        (recommendation "A comprehensive evaluation by a licensed mental health provider is recommended. The presence of hypomanic episodes alongside severe depressive patterns suggests Bipolar II Disorder. Early diagnosis can facilitate appropriate therapeutic support tailored to individual needs."))))

; Rule to detect rapid cycling - revised version
(defrule assess-rapid-cycling
    (declare (salience 75))
    ; Find first switch
    (emotional-pattern (user_id ?id)
                      (pattern-type "manic")
                      (day ?d1))
    ; Find second switch
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (day ?d2&:(and (> ?d2 ?d1) (< (- ?d2 ?d1) 365))))
    ; Find third switch
    (emotional-pattern (user_id ?id)
                      (pattern-type "manic")
                      (day ?d3&:(and (> ?d3 ?d2) (< (- ?d3 ?d1) 365))))
    ; Find fourth switch
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (day ?d4&:(and (> ?d4 ?d3) (< (- ?d4 ?d1) 365))))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "severe")
        (confidence 95)
        (evidence (str-cat "Four mood switches detected within " (- ?d4 ?d1) " days, indicating rapid cycling pattern"))
        (recommendation "Rapid cycling patterns, characterized by frequent mood shifts, require immediate attention from a mental health specialist. These patterns may indicate a complex form of bipolar disorder that benefits from specialized interventions. Prompt action is essential for effective management."))))

; Rule to detect moderate risk with extended monitoring
(defrule assess-moderate-bipolar-risk
    (declare (salience 74))
    (or (emotional-pattern (user_id ?id)
                          (pattern-type "manic")
                          (intensity "moderate")
                          (persistence ?p1&:(>= ?p1 3)))
        (emotional-pattern (user_id ?id)
                          (pattern-type "depressive")
                          (intensity "moderate")
                          (persistence ?p2&:(>= ?p2 7))))
    (not (bipolar-assessment (user_id ?id)))
=>
    (assert (bipolar-assessment
        (user_id ?id)
        (risk-level "moderate")
        (confidence 80)
        (evidence "Sustained moderate mood episodes detected requiring careful monitoring")
        (recommendation "A clinical evaluation is recommended within two weeks to further assess and address the detected moderate mood episodes. Regular mood tracking and lifestyle adjustments during this period can support preventive care and provide valuable insights for further diagnosis."))))
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