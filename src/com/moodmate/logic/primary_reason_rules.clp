(defrule calculate-rses-score
    (declare (salience 91))

    (self-image-answer (user_id ?id) (question_id 1) (answer ?a1))
    (self-image-answer (user_id ?id) (question_id 2) (answer ?a2))
    (self-image-answer (user_id ?id) (question_id 3) (answer ?a3))
    (self-image-answer (user_id ?id) (question_id 4) (answer ?a4))
    (self-image-answer (user_id ?id) (question_id 5) (answer ?a5))
    (self-image-answer (user_id ?id) (question_id 6) (answer ?a6))
    (self-image-answer (user_id ?id) (question_id 7) (answer ?a7))
    (self-image-answer (user_id ?id) (question_id 8) (answer ?a8))
    (self-image-answer (user_id ?id) (question_id 9) (answer ?a9))
    (self-image-answer (user_id ?id) (question_id 10) (answer ?a10))
=>
    (bind ?score (+ ?a1 
                    (- 5 ?a2) 
                    ?a3 
                    ?a4 
                    (- 5 ?a5) 
                    (- 5 ?a6) 
                    ?a7 
                    (- 5 ?a8) 
                    (- 5 ?a9) 
                    ?a10))
    
    ; Determine level based on score
    (bind ?level (if (< ?score 15) then "low"
                     else (if (> ?score 25) then "high"
                             else "moderate")))
    
    ; Assert both score and level facts
    (assert (rses-score 
        (user_id ?id)
        (score ?score)
        (level ?level)))
    
    (assert (rses-level
        (user_id ?id)
        (level ?level)))
        
    (printout t "RSES Score for user " ?id ": " ?score crlf)
    (printout t "RSES Level: " ?level crlf))

; Rules for different combinations - simplified to one recommendation each
(defrule trigger-yes-rses-high
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger true))
    (rses-level (user_id ?id) (level "high"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "It's great that you have high self-esteem! Use your confidence to address this trigger constructively and maintain a positive mindset."))))

(defrule trigger-yes-rses-moderate
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger true))
    (rses-level (user_id ?id) (level "moderate"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "Your balanced self-perception can help you navigate this trigger. Remember that it's normal to feel challenged sometimes."))))

(defrule trigger-yes-rses-low
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger true))
    (rses-level (user_id ?id) (level "low"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "Even though this trigger has affected you, remember that setbacks don't define your worth. Try focusing on one small, positive action today."))))

(defrule trigger-no-rses-high
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger false))
    (rses-level (user_id ?id) (level "high"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "Your high self-esteem shows! Take this time to appreciate how you've been managing your emotions effectively and set goals that align with your strengths."))))

(defrule trigger-no-rses-moderate
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger false))
    (rses-level (user_id ?id) (level "moderate"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "With your balanced outlook, this is a good time to engage in activities that reinforce your self-worth and personal growth."))))

(defrule trigger-no-rses-low
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger false))
    (rses-level (user_id ?id) (level "low"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "This is a good time to focus on yourself. Take a moment to practice self-affirmation or mindfulness to nurture your self-worth."))))

; Check for moderate RSES and no trigger for second factors
(defrule check-second-factors
    (declare (salience 89))
    (trigger-status (user_id ?id) (has-trigger false))
    (rses-level (user_id ?id) (level "moderate"))
=>
    (assert (need-second-factors (user_id ?id) (need TRUE)))
    (printout t "RSES is moderate and no trigger present. Checking secondary factors..." crlf))

; Print recommendation with user_id
(defrule print-recommendation
    (declare (salience 87))
    (recommendation (user_id ?id) (message ?m))
    (not (printed-recommendation (user_id ?id)))
=>
    (assert (printed-recommendation (user_id ?id)))
    (printout t crlf "Recommendation for user " ?id ":" crlf)
    (printout t ?m crlf))