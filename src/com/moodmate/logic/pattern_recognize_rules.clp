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

; Rule to detect anxiety patterns
(defrule detect-anxiety-pattern
    (declare (salience 85))
    ; High fear/scared emotion
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 30)))
    ; Often with confusion
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "confused")
                          (avg-percentage ?c))
    ; And possibly with some sadness
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "sad")
                          (avg-percentage ?s))
    ; Calculate total anxiety-related emotions
    (test (>= (+ ?sc ?c) 40))  ; Combined anxiety indicators
=>
    (bind ?anxiety-level (+ ?sc (* 0.5 ?c)))  ; Weighted calculation with confusion as partial factor
    (bind ?intensity
        (if (> ?anxiety-level 70)
            then "severe"
            else (if (> ?anxiety-level 50)
                    then "moderate"
                    else "mild")))
    (bind ?description 
        (str-cat "Fear level: " ?sc "%, Confusion: " ?c "%, Sadness: " ?s "%"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "anxious")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected anxiety pattern (" ?intensity ") - " ?description crlf))

; Rule to detect panic/acute anxiety pattern
(defrule detect-panic-pattern
    (declare (salience 85))
    ; Very high fear
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 60)))
    ; With significant confusion
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "confused")
                          (avg-percentage ?c&:(> ?c 20)))
    ; And low happiness
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "happy")
                          (avg-percentage ?h&:(< ?h 20)))
=>
    (bind ?intensity "severe")  ; Panic is always considered severe
    (bind ?description 
        (str-cat "Acute anxiety - High fear (" ?sc "%), confusion (" ?c "%), low happiness (" ?h "%)"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "anxious")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected acute anxiety/panic pattern - " ?description crlf))

; Rule to detect anxious-irritable pattern
(defrule detect-anxious-irritable-pattern
    (declare (salience 85))
    ; Moderate to high fear
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "scared")
                          (avg-percentage ?sc&:(> ?sc 30)))
    ; With significant anger
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "angry")
                          (avg-percentage ?a&:(> ?a 30)))
    ; And low happiness
    (daily-emotion-summary (user_id ?id) 
                          (day ?d)
                          (emotion-name "happy")
                          (avg-percentage ?h&:(< ?h 30)))
=>
    (bind ?intensity
        (if (> (+ ?sc ?a) 80)
            then "severe"
            else (if (> (+ ?sc ?a) 60)
                    then "moderate"
                    else "mild")))
    (bind ?description 
        (str-cat "Anxious-irritable state - Fear: " ?sc "%, Anger: " ?a "%, Low happiness: " ?h "%"))
    (assert (emotional-pattern
        (user_id ?id)
        (day ?d)
        (pattern-type "anxious")
        (intensity ?intensity)
        (persistence 1)
        (description ?description)))
    (printout t "Day " ?d ": Detected anxious-irritable pattern (" ?intensity ") - " ?description crlf))

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

(deftemplate processed-persistence
    (slot user_id)
    (slot day1)
    (slot day2)
    (slot pattern-type))

(defrule detect-pattern-persistence
    (declare (salience 84))
    ; Find first pattern
    ?pattern1 <- (emotional-pattern (user_id ?id) 
                                  (pattern-type ?type) 
                                  (day ?d1)
                                  (intensity ?i1)
                                  (persistence ?p1&:(numberp ?p1)))
    ; Find consecutive day pattern
    ?pattern2 <- (emotional-pattern (user_id ?id) 
                                  (pattern-type ?type) 
                                  (day ?d2))
    ; Test if dates are consecutive by comparing as integers
    (test (= (+ (integer ?d1) 1) (integer ?d2)))
    ; Ensure we haven't processed this pair
    (not (processed-persistence (user_id ?id)
                              (day1 ?d1)
                              (day2 ?d2)
                              (pattern-type ?type)))
=>
    (bind ?new-persistence (+ ?p1 1))
    
    ; Create a new pattern for the next day with updated persistence
    (assert (emotional-pattern
        (user_id ?id)
        (pattern-type ?type)
        (day ?d2)
        (intensity (if (>= ?new-persistence 7) 
                      then "severe"
                      else (if (>= ?new-persistence 4)
                            then "moderate"
                            else (if (>= ?new-persistence 2)
                                  then "mild"
                                  else ?i1))))
        (persistence ?new-persistence)))
    
    ; Mark this pair as processed
    (assert (processed-persistence 
        (user_id ?id)
        (day1 ?d1)
        (day2 ?d2)
        (pattern-type ?type)))
    
    ; Retract the original patterns to prevent re-matching
    (retract ?pattern1)
    (retract ?pattern2)
    
    (printout t "Pattern " ?type " persists for " ?new-persistence " days (Days " ?d1 " to " ?d2 ")" crlf))
; Rule to clean up processed patterns
(defrule cleanup-processed-persistence
    (declare (salience 82))
    ?old <- (processed-persistence (user_id ?id)
                                 (day1 ?d1)
                                 (day2 ?d2))
    (emotional-pattern (user_id ?id)
                      (day ?d3&:(> ?d3 (+ ?d2 1))))
=>
    (retract ?old))