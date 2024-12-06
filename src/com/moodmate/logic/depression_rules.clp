; Rule to detect severe depression (persistent severe depressive patterns)
(defrule assess-severe-depression
    (declare (salience 75))
    ; Find persistent severe depressive pattern
    (emotional-pattern (user_id ?id) 
                      (pattern-type "depressive")
                      (intensity "severe")
                      (persistence ?p1&:(> ?p1 7)))  ; Persists more than 7 days
    (not (depression-assessment (user_id ?id)))
=>
    (assert (depression-assessment
        (user_id ?id)
        (risk-level "severe")
        (confidence 90)
        (evidence "Severe depressive pattern persisting over a week")
        (recommendation "Immediate clinical evaluation recommended for major depressive disorder"))))

; Rule to detect moderate-severe depression
(defrule assess-moderate-severe-depression
    (declare (salience 75))
    ; Find moderate to severe depressive pattern with some persistence
    (emotional-pattern (user_id ?id) 
                      (pattern-type "depressive")
                      (intensity ?i&:(or (eq ?i "moderate") (eq ?i "severe")))
                      (persistence ?p1&:(> ?p1 3)))  ; Persists more than 3 days
    (not (depression-assessment (user_id ?id)))
=>
    (assert (depression-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 80)
        (evidence "Moderate to severe depressive pattern persisting over 3 days")
        (recommendation "Clinical evaluation recommended for depressive disorder"))))

; Rule to detect persistent apathy
(defrule assess-apathy-depression
    (declare (salience 75))
    ; Find persistent apathy pattern
    (emotional-pattern (user_id ?id)
                      (pattern-type "apathy")
                      (persistence ?p1&:(> ?p1 5)))  ; Persists more than 5 days
    (not (depression-assessment (user_id ?id)))
=>
    (assert (depression-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 75)
        (evidence "Persistent apathy pattern detected")
        (recommendation "Clinical evaluation recommended for potential depression with apathy"))))

; Rule to detect mixed anxiety and depression
(defrule assess-anxious-depression
    (declare (salience 75))
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity ?i&:(or (eq ?i "moderate") (eq ?i "severe"))))
    ; High levels of scared/confused emotions indicating anxiety
    (daily-emotion-summary (user_id ?id)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 30)))
    (not (depression-assessment (user_id ?id)))
=>
    (assert (depression-assessment
        (user_id ?id)
        (risk-level "high")
        (confidence 85)
        (evidence "Depression with significant anxiety features")
        (recommendation "Clinical evaluation recommended for depression with anxiety features"))))

; Rule to detect moderate depression risk
(defrule assess-moderate-depression
    (declare (salience 74))
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity "moderate")
                      (persistence ?p1&:(<= ?p1 3)))  ; Less than 3 days
    (not (depression-assessment (user_id ?id)))
=>
    (assert (depression-assessment
        (user_id ?id)
        (risk-level "moderate")
        (confidence 70)
        (evidence "Moderate depressive pattern detected")
        (recommendation "Monitor mood and consider clinical consultation if symptoms persist"))))

; Rule to detect mild depression risk
(defrule assess-mild-depression
    (declare (salience 74))
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (intensity "mild"))
    (not (depression-assessment (user_id ?id)))
=>
    (assert (depression-assessment
        (user_id ?id)
        (risk-level "low")
        (confidence 60)
        (evidence "Mild depressive pattern detected")
        (recommendation "Continue monitoring mood patterns. Practice self-care and stress management."))))

; Rule to print depression assessment
(defrule print-depression-assessment
    (declare (salience 73))
    ?assessment <- (depression-assessment (user_id ?id)
                                        (risk-level ?risk)
                                        (confidence ?conf)
                                        (evidence ?ev)
                                        (recommendation ?rec))
=>
    (printout t crlf "=== Depression Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Confidence: " ?conf "%" crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Recommendation: " ?rec crlf)
    (printout t "===================================" crlf))