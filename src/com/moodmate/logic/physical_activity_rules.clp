; Rule to check if physical activity assessment is needed
(defrule check-physical-activity
    (declare (salience 82))
    (activity (user_id ?id) (has-activity TRUE))
=>
    (printout t crlf "Checking physical activity factors..." crlf))

; Rule for no physical activity
(defrule evaluate-no-activity
    (declare (salience 81))
    (activity (user_id ?id) (has-activity FALSE))
    (not (activity-score-calculated (user_id ?id))) 
=>
    ; Assert a physical-activity fact with the lowest score
    (assert (physical-activity
        (user_id ?id)
        (has-activity FALSE)
        (duration 0)
        (intensity "none")
        (score 0))) ; Lowest possible score

    (assert (activity-score-calculated (user_id ?id)))
    (assert (physical-activity-recommendation 
        (user_id ?id)
        (message "Physical Activity Score: 0/100. Consider incorporating some light physical activity into your day. Even a short walk can help improve your mood.")))

    (printout t "No physical activity detected. Physical activity fact asserted with lowest score and recommendation." crlf))


; Rule to calculate physical activity score
(defrule calculate-activity-score
    (declare (salience 80))
    (need-second-factors (user_id ?id) (need TRUE))
    ?activity <- (physical-activity (user_id ?id) 
                                  (has-activity TRUE)
                                  (duration ?dur)
                                  (intensity ?int))
    (not (activity-score-calculated (user_id ?id)))
=>
    ; Calculate base score
    (bind ?score 0)
    
    (if (< ?dur 10)
        then 
        (bind ?score 0)
        else 
        (if (< ?dur 15)
            then 
            (bind ?score 
                (if (eq ?int "high") 
                    then 3
                    else (if (eq ?int "moderate")
                            then 2
                            else 1)))
            else 
            (if (< ?dur 30)
                then 
                (bind ?score 
                    (if (eq ?int "high")
                        then 4
                        else (if (eq ?int "moderate")
                                then 3
                                else 2)))
                else 
                (if (< ?dur 60)
                    then 
                    (bind ?score 
                        (if (eq ?int "high")
                            then 4
                            else (if (eq ?int "moderate")
                                    then 4
                                    else 3)))
                    else 
                    (bind ?score 4)))))  ; Maximum score for duration >= 60 mins regardless of intensity
    
    ; Normalize score to 0-100
    (bind ?normalized-score (* (/ ?score 4) 100))
    
    ; Modify fact with calculated score
    (modify ?activity (score ?normalized-score))
    
    ; Assert control fact to prevent multiple calculations
    (assert (activity-score-calculated (user_id ?id)))
    
    ; Generate recommendation based on normalized score
    (if (= ?normalized-score 0)
        then 
        (assert (physical-activity-recommendation 
            (user_id ?id)
            (message "Your physical activity duration is too short. Try to exercise for at least 10 minutes continuously.")))
        else 
        (if (< ?normalized-score 50)
            then 
            (assert (physical-activity-recommendation 
                (user_id ?id)
                (message "You've made a start with physical activity. Try to gradually increase your duration and intensity.")))
            else 
            (if (< ?normalized-score 75)
                then 
                (assert (physical-activity-recommendation 
                    (user_id ?id)
                    (message "Good effort with physical activity! Consider increasing either duration or intensity.")))
                else 
                (assert (physical-activity-recommendation 
                    (user_id ?id)
                    (message "Excellent physical activity level! Keep maintaining this level. Your duration and intensity are ideal for health benefits."))))))
    
    (printout t "Physical Activity Score: " ?normalized-score "%" crlf))


; Rule to print physical activity recommendation
(defrule print-activity-recommendation
    (declare (salience 79))
    ?rec <- (physical-activity-recommendation (user_id ?id) (message ?m))
    (not (printed-activity-recommendation (user_id ?id)))
=>
    (assert (printed-activity-recommendation (user_id ?id)))
    (printout t crlf "Physical Activity Recommendation:" crlf)
    (printout t ?m crlf))

