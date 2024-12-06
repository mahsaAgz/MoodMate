; Rule to detect severe anxiety (high fear/confusion, persistent)
(defrule assess-severe-anxiety
    (declare (salience 75))
    ; High levels of fear
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 60)))
    ; Also significant confusion
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "confused")
                          (avg-percentage ?c&:(> ?c 30)))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "severe")
        (evidence (str-cat "High fear levels (" ?sc "%) with significant confusion (" ?c "%)"))
        (recommendation "Immediate clinical evaluation recommended for severe anxiety disorder"))))

; Rule to detect anxiety with depression
(defrule assess-anxiety-depression-comorbid
    (declare (salience 75))
    ; Moderate to high fear
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 40)))
    ; With significant sadness
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "sad")
                          (avg-percentage ?s&:(> ?s 40)))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "high")
        (evidence (str-cat "Coexisting anxiety (" ?sc "%) and depression (" ?s "%)"))
        (recommendation "Clinical evaluation recommended for anxiety with depressive features"))))

; Rule to detect persistent moderate anxiety
(defrule assess-persistent-moderate-anxiety
    (declare (salience 75))
    ; Moderate fear levels persisting
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(and (> ?sc 30) (<= ?sc 60))))
    (emotional-pattern (user_id ?id)
                      (pattern-type "anxious")
                      (persistence ?p&:(> ?p 5)))  ; Persists more than 5 days
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "high")
        (evidence (str-cat "Persistent moderate anxiety levels for " ?p " days"))
        (recommendation "Clinical evaluation recommended for persistent anxiety"))))

; Rule to detect anxiety with irritability
(defrule assess-anxious-irritability
    (declare (salience 74))
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 30)))
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "angry")
                          (avg-percentage ?a&:(> ?a 30)))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "moderate")
        (evidence (str-cat "Anxiety (" ?sc "%) with significant irritability (" ?a "%)"))
        (recommendation "Consider clinical consultation for anxiety with irritability"))))

; Rule to detect mild anxiety
(defrule assess-mild-anxiety
    (declare (salience 74))
    (daily-emotion-summary (user_id ?id)
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(and (> ?sc 20) (<= ?sc 30))))
    (not (anxiety-assessment (user_id ?id)))
=>
    (assert (anxiety-assessment
        (user_id ?id)
        (risk-level "low")
        (evidence (str-cat "Mild anxiety levels (" ?sc "%)"))
        (recommendation "Monitor anxiety levels and practice stress management techniques"))))

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