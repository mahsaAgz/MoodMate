; Rule to check sleep factors for secondary analysis
(defrule check-sleep-factors
    (declare (salience 86))
    (need-second-factors (user_id ?id) (need TRUE))
    (sleepiness (user_id ?id) (sleepy TRUE))
=>
    (printout t crlf "Checking sleep factors for secondary analysis..." crlf))

; Rule for non-sleepy users
(defrule analyze-non-sleepy
    (declare (salience 84))
    (sleepiness (user_id ?id) (sleepy FALSE))
    (not (sleep-score-calculated (user_id ?id)))
=>
	(assert (sleep-quality
        (user_id ?id)
        (satisfaction 3) ; Highest satisfaction
        (sleep-time 0)   ; No specific sleep time
        (wake-time 0)    ; No specific wake time
        (sleep-decimal 0) ; No specific decimal representation of sleep time
        (wake-decimal 0) ; No specific decimal representation of wake time
        (score 100)))    ; Maximum score
    (assert (sleep-score-calculated (user_id ?id)))
    (assert (sleep-recommendation 
        (user_id ?id)
        (message "Sleep Score: 100/100. Your alertness is good - keep maintaining your current sleep schedule.")))
    (printout t "Sleep Analysis Complete" crlf))

; Rule to analyze sleep quality and generate recommendation
(defrule analyze-sleep-quality-and-recommend
    (declare (salience 85))
    (sleepiness (user_id ?id) (sleepy TRUE))
    ?sleep <- (sleep-quality (user_id ?id) 
                          (satisfaction ?sat)
                          (sleep-time ?st)
                          (wake-time ?wt)
                          (sleep-decimal ?sd)
                          (wake-decimal ?wd))
    (not (sleep-score-calculated (user_id ?id)))
=>
    ; Calculate duration
    (bind ?duration 
        (if (< ?wd ?sd)
            then (+ (- 24 ?sd) ?wd)
            else (- ?wd ?sd)))
    
    ; Calculate scores
    (bind ?satisfaction-score (* (/ ?sat 3) 40))
    
    (bind ?duration-score
        (if (< ?duration 6) 
            then 20
            else (if (< ?duration 7)
                    then 30
                    else (if (<= ?duration 9)
                            then 40
                            else 25))))
    
    ; Calculate midpoint
    (bind ?midpoint 
        (if (< ?wd ?sd)
            then (/ (+ (- 24 ?sd) ?wd) 2)
            else (/ (+ ?sd ?wd) 2)))
    
    (bind ?timing-score
        (if (and (>= ?sd 22.0) (<= ?sd 23.5)
                 (>= ?wd 6.0) (<= ?wd 7.5))
            then 20
            else (if (and (>= ?sd 21.0) (<= ?sd 24.0)
                         (>= ?wd 5.0) (<= ?wd 8.0))
                    then 15
                    else 10)))
    
    (bind ?midpoint-score
        (if (and (>= ?midpoint 3.0) (<= ?midpoint 4.0))
            then 20
            else (if (and (>= ?midpoint 2.5) (<= ?midpoint 4.5))
                    then 15
                    else 10)))
    
    ; Calculate total raw score
    (bind ?raw-total-score (+ ?satisfaction-score ?duration-score ?timing-score ?midpoint-score))

    ; Normalize the score to 0–100
    (bind ?max-score 120) ; Adjust this if the maximum possible score changes
    (bind ?total-score (round (* (/ ?raw-total-score ?max-score) 100)))
    
    ; Generate message using consistent format
    (bind ?message 
        (str-cat "Sleep Score: " ?total-score "/100. "
            (if (< ?duration 6) 
                then "Try getting at least 7 hours of sleep by going to bed earlier."
                else (if (> ?duration 9) 
                        then "Consider reducing your sleep duration to 8 hours for optimal energy levels."
                        else (if (not (and (>= ?sd 21.0) (<= ?sd 24.0) (>= ?wd 5.0) (<= ?wd 8.0)))
                                then "Adjust your sleep schedule to go to bed between 22:00-23:00 and wake up between 6:00-7:00."
                                else (if (< ?satisfaction-score 30)
                                        then "Improve your sleep quality by maintaining a consistent bedtime routine and optimizing your sleep environment."
                                        else (if (not (and (>= ?midpoint 3.0) (<= ?midpoint 4.0)))
                                                then "Shift your sleep schedule to achieve a mid-sleep time between 3:00-4:00 AM for better circadian alignment."
                                                else "Your sleep patterns are excellent - maintain your current routine.")))))))
    
    ; Assert facts and print output
    (modify ?sleep (score ?total-score))
    (assert (sleep-score-calculated (user_id ?id)))
    (assert (sleep-recommendation 
        (user_id ?id)
        (message ?message)))
    
    (printout t crlf ?message crlf))
