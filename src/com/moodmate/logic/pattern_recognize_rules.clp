(defrule detect-manic-pattern
    (declare (salience 85))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "happy")
                          (avg-percentage ?h&:(> ?h 60)))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "sad")
                          (avg-percentage ?s&:(< ?s 20)))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "angry")
                          (avg-percentage ?a&:(< ?a 20)))
=>
    (bind ?intensity 
        (if (> ?h 80) 
            then "severe"
            else (if (> ?h 70)
                    then "moderate"
                    else "mild")))
    (bind ?description 
        (str-cat "Happiness: " ?h "%, Low negative emotions (Sad: " ?s "%, Angry: " ?a "%)"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "manic")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected manic pattern (" ?intensity ") - " ?description crlf))

 ; Rule to detect depressive patterns
(defrule detect-depressive-pattern
    (declare (salience 85))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "happy")
                          (avg-percentage ?h&:(< ?h 20)))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "sad")
                          (avg-percentage ?s))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "angry")
                          (avg-percentage ?a))
    (test (> (+ ?s ?a) 60))
=>
    (bind ?negative-emotions (+ ?s ?a))
    (bind ?intensity
        (if (> ?negative-emotions 80)
            then "severe"
            else (if (> ?negative-emotions 70)
                    then "moderate"
                    else "mild")))
    (bind ?description 
        (str-cat "Low happiness: " ?h "%, High negative emotions (Total: " ?negative-emotions "%)"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "depressive")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected depressive pattern (" ?intensity ") - " ?description crlf))

; Rule to detect mixed mood patterns
(defrule detect-mixed-mood-pattern
    (declare (salience 85))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "happy")
                          (avg-percentage ?h&:(> ?h 40)))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "sad")
                          (avg-percentage ?s&:(> ?s 20)))
=>
    (bind ?intensity
        (if (and (> ?h 60) (> ?s 40))
            then "severe"
            else (if (and (> ?h 50) (> ?s 30))
                    then "moderate"
                    else "mild")))
    (bind ?description 
        (str-cat "Coexisting elevated happiness (" ?h "%) and sadness (" ?s "%)"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "mixed-mood")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected mixed mood pattern (" ?intensity ") - " ?description crlf))

 Rule to detect irritable patterns
(defrule detect-irritable-pattern
    (declare (salience 85))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "angry")
                          (avg-percentage ?a&:(> ?a 50)))
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "happy")
                          (avg-percentage ?h&:(< ?h 20)))
=>
    (bind ?intensity
        (if (> ?a 70)
            then "severe"
            else (if (> ?a 60)
                    then "moderate"
                    else "mild")))
    (bind ?description 
        (str-cat "High anger (" ?a "%) with low happiness (" ?h "%)"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "irritable")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected irritable pattern (" ?intensity ") - " ?description crlf))

; Rule to detect mood switches
(defrule detect-mood-switch
    (declare (salience 83))
    (emotional-pattern (user_id ?id) 
                      (day ?d1) 
                      (pattern-type ?p1)
                      (intensity ?i1))
    (emotional-pattern (user_id ?id) 
                      (day ?d2&:(= ?d2 (+ ?d1 1))) 
                      (pattern-type ?p2&~?p1)
                      (intensity ?i2))
    (not (processed-switch (user_id ?id) (day1 ?d1) (day2 ?d2)))
=>
    (assert (processed-switch (user_id ?id) (day1 ?d1) (day2 ?d2)))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d2)
        (pattern-type "switch")
        (intensity (if (and (eq ?i1 "severe") (eq ?i2 "severe"))
                      then "severe"
                      else "moderate"))
        (description (str-cat "Switched from " ?p1 " to " ?p2))))
    (printout t "Detected mood switch on day " ?d2 " from " ?p1 " to " ?p2 crlf))

; Rule to detect pattern persistence
(defrule detect-pattern-persistence
    (declare (salience 84))
    ?pattern1 <- (emotional-pattern (user_id ?id) 
                                  (pattern-type ?type) 
                                  (day ?d1)
                                  (intensity ?i1)
                                  (persistence ?p1&:(numberp ?p1)))  ; Ensure p1 is a number
    (emotional-pattern (user_id ?id) 
                      (pattern-type ?type) 
                      (day ?d2&:(= ?d2 (+ ?d1 1))))  ; Consecutive days
    (not (processed-persistence (user_id ?id)
                              (day1 ?d1)
                              (day2 ?d2)
                              (pattern-type ?type)))
=>
    (bind ?new-persistence (+ ?p1 1))
    (modify ?pattern1 
        (persistence ?new-persistence)
        (intensity (if (> ?new-persistence 2) 
                      then "severe"
                      else (if (> ?new-persistence 1)
                              then "moderate"
                              else ?i1))))
    (assert (processed-persistence (user_id ?id)
                                 (day1 ?d1)
                                 (day2 ?d2)
                                 (pattern-type ?type)))
    (printout t "Pattern " ?type " persists for " ?new-persistence " days" crlf))

