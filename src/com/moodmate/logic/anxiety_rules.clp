; Rule to detect severe anxiety (high fear/confusion, persistent)
; Rule to detect severe persistent anxiety
(defrule assess-severe-anxiety
    (declare (salience 75))
    ; High levels of fear for extended period
    (emotional-pattern (user_id ?id)
                      (pattern-type "anxious")
                      (intensity "severe")
                      (persistence ?p1&:(>= ?p1 14)))  ; Two weeks of severe symptoms
    ; With significant confusion/cognitive symptoms
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "confused")
                          (avg-percentage ?c&:(> ?c 30)))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "severe")
        (evidence (str-cat "Severe anxiety persisting for " ?p1 " days with significant cognitive symptoms (" ?c "% confusion)"))
        (recommendation "Immediate professional intervention is strongly recommended. Symptoms suggest severe and persistent anxiety, potentially impacting cognitive function. A licensed mental health professional can provide tailored support and care."))))

; Rule to detect chronic anxiety-depression comorbidity
(defrule assess-anxiety-depression-comorbid
    (declare (salience 75))
    ; Sustained moderate-severe anxiety
    (emotional-pattern (user_id ?id)
                      (pattern-type "anxious")
                      (persistence ?p1&:(>= ?p1 7)))  ; At least a week
    ; Concurrent depression pattern
    (emotional-pattern (user_id ?id)
                      (pattern-type "depressive")
                      (persistence ?p2&:(>= ?p2 7)))  ; Also sustained
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "high")
        (evidence (str-cat "Concurrent anxiety for " ?p1 " days and depressive patterns for " ?p2 " days"))
        (recommendation "Clinical assessment is highly recommended. The concurrent patterns of anxiety and depressive symptoms may indicate a mixed anxiety-depressive condition. Early evaluation can facilitate appropriate and effective treatment options."))))

; Rule to detect persistent moderate anxiety
(defrule assess-persistent-moderate-anxiety
    (declare (salience 75))
    ; Moderate anxiety persisting
    (emotional-pattern (user_id ?id)
                      (pattern-type "anxious")
                      (intensity "moderate")
                      (persistence ?p&:(>= ?p 14)))  ; Two weeks of moderate symptoms
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(and (> ?sc 30) (<= ?sc 60))))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "high")
        (evidence (str-cat "Persistent moderate anxiety (" ?sc "%) for " ?p " days"))
        (recommendation "A professional consultation is advised. Persistent moderate anxiety with noticeable fear patterns may align with generalized anxiety. A mental health professional can help clarify and address these concerns"))))

; Rule to detect anxiety with chronic irritability
(defrule assess-anxious-irritability
    (declare (salience 74))
    ; Sustained anxiety
    (emotional-pattern (user_id ?id)
                      (pattern-type "anxious")
                      (persistence ?p1&:(>= ?p1 7)))
    ; With consistent irritability
    (emotional-pattern (user_id ?id)
                      (pattern-type "irritable")
                      (persistence ?p2&:(>= ?p2 7)))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "high")
        (evidence (str-cat "Anxiety pattern for " ?p1 " days with sustained irritability for " ?p2 " days"))
        (recommendation "Professional evaluation is recommended. Sustained anxiety accompanied by chronic irritability may suggest an anxiety disorder with additional features. Early intervention can improve emotional regulation and well-being."))))

; Rule to detect fluctuating mild anxiety
(defrule assess-mild-anxiety
    (declare (salience 74))
    ; Mild but persistent anxiety
    (emotional-pattern (user_id ?id)
                      (pattern-type "anxious")
                      (intensity "mild")
                      (persistence ?p&:(>= ?p 7)))
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(and (> ?sc 20) (<= ?sc 30))))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "moderate")  ; Upgraded to moderate if persistent
        (evidence (str-cat "Mild but persistent anxiety (" ?sc "%) for " ?p " days"))
        (recommendation "Clinical consultation is encouraged. Persistent mild anxiety, though manageable, may benefit from proactive strategies such as stress management, therapy, or lifestyle adjustments to prevent escalation."))))

; Rule to print anxiety assessment
(defrule print-anxiety-assessment
    (declare (salience 73))
    ?assessment <- (anxiety-assessment (user_id ?id)
                                     (risk-level ?risk)
                                     (evidence ?ev)
                                     (recommendation ?rec))
=>
    (printout t crlf "=== Anxiety Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Recommendation: " ?rec crlf)
    (printout t "=================================" crlf))