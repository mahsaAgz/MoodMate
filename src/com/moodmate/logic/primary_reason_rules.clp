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

; Rules for different combinations
(defrule trigger-yes-rses-high
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger true))
    (rses-level (user_id ?id) (level "high"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "It's great that you have high self-esteem! Reflecting on this trigger might help you learn more about what challenges you and how you can grow stronger from it.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Your confidence is your strength—use it to address this trigger constructively and focus on maintaining a positive mindset.")))
    (assert (recommendation 
        (user_id ?id)
        (message "You seem resilient! Consider journaling about this experience to process your emotions and reinforce your self-image."))))

(defrule trigger-yes-rses-moderate
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger true))
    (rses-level (user_id ?id) (level "moderate"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "Your balanced self-perception can help you navigate this trigger. Remember that it's normal to feel challenged sometimes.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Consider talking to a friend or writing down your thoughts. Your moderate self-awareness suggests you can process this experience constructively.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Take small steps to address this trigger. Your balanced self-image is a good foundation for working through challenges."))))

(defrule trigger-yes-rses-low
	(declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger true))
    (rses-level (user_id ?id) (level "low"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "Even though this trigger has affected you, remember that setbacks don't define your worth. Try focusing on one small, positive action today.")))
    (assert (recommendation 
        (user_id ?id)
        (message "It's okay to feel shaken by this situation. Speaking to someone you trust might help you gain perspective and build your self-esteem.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Be kind to yourself. Triggers can be tough, but recognizing them is a step toward growth. Practice self-compassion and celebrate small wins."))))

(defrule trigger-no-rses-high
    (declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger false))  ; Corrected this line
    (rses-level (user_id ?id) (level "high"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "Your high self-esteem shows! Without a trigger present, take this time to appreciate how you've been managing your emotions effectively.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Enjoy the calm moments and consider reinforcing your positivity by engaging in an activity that brings you joy or helps others.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Your self-image is strong, which is a fantastic foundation. Use this peaceful time to set goals that align with your strengths."))))

(defrule trigger-no-rses-low
	(declare (salience 90))
    (trigger-status (user_id ?id) (has-trigger false))
    (rses-level (user_id ?id) (level "low"))
=>
    (assert (recommendation 
        (user_id ?id)
        (message "This is a good time to focus on yourself. Reflecting on positive experiences or achievements might help strengthen your self-esteem.")))
    (assert (recommendation 
        (user_id ?id)
        (message "You're in a calm space, so take a moment to practice self-affirmation or mindfulness to nurture your self-worth.")))
    (assert (recommendation 
        (user_id ?id)
        (message "Without a trigger affecting you, this could be an opportunity to explore activities that help you feel accomplished and valued."))))

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

