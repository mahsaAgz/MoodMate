; Rules for sleep assessment
(defrule check-sleep-factors
    (declare (salience 86))
    (need-second-factors (user_id ?id) (need TRUE))
    (sleepiness (user_id ?id) (sleepy TRUE))
=>
    (printout t crlf "Checking sleep factors for secondary analysis..." crlf))

; Rule to analyze sleep quality when user is sleepy
(defrule analyze-sleep-quality
    (declare (salience 85))
    (need-second-factors (user_id ?id) (need TRUE))
    (sleepiness (user_id ?id) (sleepy TRUE))
    (sleep-quality (user_id ?id) 
                  (satisfaction ?sat)
                  (sleep-time ?st)
                  (wake-time ?wt))
=>
    ; Determine quality message based on satisfaction level
    (bind ?quality-message
        (if (= ?sat 0) 
            then "Your sleep quality is very good, but you're feeling sleepy now. "
            else (if (= ?sat 2)
                    then "Your sleep quality is fairly good, though you're experiencing sleepiness. "
                    else (if (= ?sat 1)
                            then "Your sleep quality needs improvement. "
                            else (if (= ?sat 0)
                                    then "Your sleep quality is poor. "
                                    else "Unable to determine sleep quality. ")))))
            
    (bind ?schedule-message
        (str-cat "Your current sleep schedule is from " ?st " to " ?wt ". "))
        
    (bind ?recommendation
        (if (= ?sat 3) 
            then "Consider taking a short power nap."
            else (if (= ?sat 2)
                    then "Try to maintain consistent sleep-wake times."
                    else (if (= ?sat 1)
                            then "Focus on sleep hygiene and consistent bedtime routine."
                            else (if (= ?sat 0)
                                    then "Consider consulting a healthcare provider about your sleep quality."
                                    else "Try to identify what affects your sleep quality.")))))
            
    (assert (sleep-recommendation 
        (user_id ?id)
        (message (str-cat ?quality-message ?schedule-message ?recommendation))))
        
    (printout t "Sleep Quality Analysis Complete" crlf))

; Rule when user is not sleepy
(defrule analyze-non-sleepy
    (declare (salience 84))
    (need-second-factors (user_id ?id) (need TRUE))
    (sleepiness (user_id ?id) (sleepy FALSE))
=>
    (assert (sleep-recommendation 
        (user_id ?id)
        (message "Your alertness is good. Keep maintaining your current sleep schedule.")))
    
    (printout t "Sleep Analysis Complete" crlf))

; Rule to print sleep recommendation
(defrule print-sleep-recommendation
    (declare (salience 83))  ; Lower than analysis rules
    ?rec <- (sleep-recommendation (user_id ?id) (message ?m))
    (not (printed-sleep-recommendation (user_id ?id)))  ; Prevent multiple prints
=>
    (assert (printed-sleep-recommendation (user_id ?id)))
    (printout t crlf "Sleep Recommendation:" crlf)
    (printout t ?m crlf))

; Rule to check if physical activity assessment is needed
(defrule check-physical-activity
    (declare (salience 82))
    (need-second-factors (user_id ?id) (need TRUE))
    (activity (user_id ?id) (has-activity TRUE))
=>
    (printout t crlf "Checking physical activity factors..." crlf))

; Rule for no physical activity
(defrule evaluate-no-activity
    (declare (salience 81))
    (need-second-factors (user_id ?id) (need TRUE))
    (activity (user_id ?id) (has-activity FALSE))
=>
    (assert (physical-activity-recommendation 
        (user_id ?id)
        (message "Consider incorporating some light physical activity into your day. Even a short walk can help improve your mood.")))
    (printout t "Physical Activity Analysis Complete" crlf))


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

; Rule to calculate appetite score
(defrule calculate-appetite-score
    (declare (salience 78))
    (need-second-factors (user_id ?id) (need TRUE))
    ?appetite <- (appetite-status (user_id ?id) (option ?opt))
    (not (appetite-score-calculated (user_id ?id)))  ; Only calculate once
=>
    ; Calculate score based on option
    (bind ?score
        (if (eq ?opt "3")    ; No change
            then 3
            else (if (or (eq ?opt "2a") (eq ?opt "2b"))  ; Somewhat changed
                    then 2
                    else (if (or (eq ?opt "1a") (eq ?opt "1b"))  ; Much changed
                            then 1
                            else 0))))  ; No appetite/Constant craving
    
    ; Modify the fact with calculated score
    (modify ?appetite (score ?score))
    
    ; Assert control fact to prevent multiple calculations
    (assert (appetite-score-calculated (user_id ?id)))
    
    ; Assert recommendation based on score
    (if (= ?score 3)
        then (assert (appetite-recommendation 
                (user_id ?id)
                (message "Your appetite is stable, which is good for maintaining healthy eating patterns.")))
        else (if (= ?score 2)
                then (assert (appetite-recommendation 
                        (user_id ?id)
                        (message "Your appetite has changed slightly. Monitor your eating patterns and try to maintain regular meal times.")))
                else (if (= ?score 1)
                        then (assert (appetite-recommendation 
                                (user_id ?id)
                                (message "Significant changes in appetite can affect your well-being. Consider consulting a healthcare provider.")))
                        else (assert (appetite-recommendation 
                                (user_id ?id)
                                (message "Your appetite patterns are concerning. Please speak with a healthcare provider about your eating patterns."))))))
                                
    ; Print the score
    (printout t "Appetite Score: " ?score " out of 3" crlf))

; Rule to calculate macronutrient score
(defrule calculate-macronutrient-score
    (declare (salience 77))
    (need-second-factors (user_id ?id) (need TRUE))
    ?intake <- (macronutrient-intake (user_id ?id)
                                    (carbs ?c)      
                                    (protein ?p)    
                                    (fat ?f)        
                                    (minerals ?m)   
                                    (vitamins ?v)   
                                    (water ?w))    
    (not (macronutrient-score-calculated (user_id ?id)))
=>
    ; Calculate carbs score
    (bind ?carbs-score
        (if (< ?c 20) 
            then 30    
            else (if (< ?c 40) 
                    then 60    
                    else (if (<= ?c 60) 
                            then 100  
                            else (if (<= ?c 80) 
                                    then 70   
                                    else 40)))))

    ; Calculate protein score
    (bind ?protein-score
        (if (< ?p 20) 
            then 30
            else (if (< ?p 40) 
                    then 60
                    else (if (<= ?p 60) 
                            then 100
                            else (if (<= ?p 80) 
                                    then 70
                                    else 40)))))

    ; Calculate fat score
    (bind ?fat-score
        (if (< ?f 20) 
            then 30
            else (if (< ?f 40) 
                    then 60
                    else (if (<= ?f 60) 
                            then 100
                            else (if (<= ?f 80) 
                                    then 70
                                    else 40)))))

    ; Calculate water score
    (bind ?water-score
        (if (< ?w 20) 
            then 30
            else (if (< ?w 40) 
                    then 60
                    else (if (<= ?w 60) 
                            then 100
                            else (if (<= ?w 80) 
                                    then 70
                                    else 40)))))

    ; Calculate minerals score
    (bind ?minerals-score
        (if (< ?m 20) 
            then 30
            else (if (< ?m 40) 
                    then 60
                    else (if (<= ?m 60) 
                            then 100
                            else (if (<= ?m 80) 
                                    then 70
                                    else 40)))))

    ; Calculate vitamins score
    (bind ?vitamins-score
        (if (< ?v 20) 
            then 30
            else (if (< ?v 40) 
                    then 60
                    else (if (<= ?v 60) 
                            then 100
                            else (if (<= ?v 80) 
                                    then 70
                                    else 40)))))

    ; Calculate total score
    (bind ?total-score (round (/ (+ (* 3 ?carbs-score) 
                                   (* 3 ?protein-score) 
                                   (* 3 ?fat-score)
                                   (* 3 ?water-score)
                                   ?minerals-score
                                   ?vitamins-score) 
                                16)))
    
    (assert (macronutrient-score-calculated (user_id ?id)))
    (modify ?intake (score ?total-score))
    
    ; Print analysis
(printout t "Nutrient Analysis:" crlf)
(printout t "Carbohydrates: " 
    (if (< ?c 40) 
        then "Currently low - Try to include more whole grains, fruits, and starchy vegetables in your meals for sustained energy"
        else (if (> ?c 60) 
                then "Higher than needed - Consider reducing portions of rice, bread, and other starchy foods"
                else "Good balance - Keep maintaining this level of carbohydrate intake")) crlf)

(printout t "Protein: " 
    (if (< ?p 40) 
        then "Currently low - Include more lean meats, fish, eggs, tofu, or legumes to support your body's needs"
        else (if (> ?p 60) 
                then "Higher than needed - Consider moderating your protein portions while maintaining variety"
                else "Good balance - You're getting a healthy amount of protein")) crlf)

(printout t "Fat: " 
    (if (< ?f 40) 
        then "Currently low - Try adding healthy fats like avocados, nuts, olive oil, or fatty fish to your diet"
        else (if (> ?f 60) 
                then "Higher than needed - Consider reducing portions of high-fat foods while keeping healthy fats in your diet"
                else "Good balance - You're maintaining a healthy fat intake")) crlf)

(printout t "Water: " 
    (if (< ?w 40) 
        then "Currently low - Try carrying a water bottle, setting reminders to drink, or eating more water-rich foods"
        else (if (> ?w 60) 
                then "Higher than needed - While staying hydrated is good, excessive water intake isn't necessary"
                else "Good balance - You're maintaining good hydration habits")) crlf)

(printout t "Minerals: " 
    (if (< ?m 40) 
        then "Currently low - Include more leafy greens, nuts, seeds, and whole grains for better mineral intake"
        else (if (> ?m 60) 
                then "Higher than needed - If you're taking supplements, consider reviewing them with a healthcare provider"
                else "Good balance - You're getting a good variety of minerals")) crlf)

(printout t "Vitamins: " 
    (if (< ?v 40) 
        then "Currently low - Try adding more colorful fruits and vegetables to increase your vitamin intake"
        else (if (> ?v 60) 
                then "Higher than needed - If you're taking supplements, consider reviewing your intake with a healthcare provider"
                else "Good balance - You're getting a good mix of vitamins from your diet")) crlf)

(printout t crlf "Final Nutrition Score: " ?total-score "%" crlf))

; Rule to evaluate meal patterns
(defrule evaluate-meal-patterns
    (declare (salience 76))  ; After macronutrient evaluation
    (need-second-factors (user_id ?id) (need TRUE))
    ?meal <- (meal-info (user_id ?id) (meals-per-day ?m))
    (not (meal-pattern-evaluated (user_id ?id)))
=>
    ; Calculate base meal score based on the number of meals per day
(bind ?meal-score
    (if (= ?m 1)
        then 30   ; 1 meal
        else 
        (if (= ?m 2)
            then 60   ; 2 meals
            else 
            (if (and (>= ?m 3) (<= ?m 5))
                then 90   ; 3-5 meals
                else 
                (if (and (>= ?m 6) (<= ?m 7))
                    then 60   ; 6-7 meals
                    else 30)))))  ; More than 7 meals



    ; Assert meal pattern evaluation to prevent duplicate processing
    (assert (meal-pattern-evaluated (user_id ?id)))
    (modify ?meal (meal-score ?meal-score))
    
    ; Print meal pattern analysis
    (printout t crlf "Meal Pattern Analysis:" crlf)
    (printout t "Current meal frequency: " ?m " meals per day" crlf)
    (if (< ?m 2)
        then (printout t "Warning: Eating less than 2 meals per day can:"
                      crlf "- Affect your metabolism and energy levels"
                      crlf "- Make it harder to get all necessary nutrients"
                      crlf "- Lead to overeating when you do eat"
                      crlf "Recommendation: Try to gradually increase to 3 meals per day"
                      crlf "Start by adding a small breakfast or lunch" crlf))
    (if (= ?m 2)
        then (printout t "Having only 2 meals per day:"
                      crlf "- May not provide consistent energy throughout the day"
                      crlf "- Could lead to larger portions at each meal"
                      crlf "Recommendation: Consider adding a third balanced meal"
                      crlf "If time is an issue, even a nutritious snack can help" crlf))
    (if (and (>= ?m 3) (<= ?m 5))
        then (printout t "Excellent meal pattern:"
                      crlf "- Regular eating schedule helps maintain stable energy levels"
                      crlf "- Allows for better nutrient distribution throughout the day"
                      crlf "- Supports healthy metabolism"
                      crlf "Keep maintaining this consistent pattern" crlf))
    (if (and (>= ?m 6) (<= ?m 7))
        then (printout t "Frequent eating pattern:"
                      crlf "- While small frequent meals work for some, watch portion sizes"
                      crlf "- Consider if any meals are actually snacks"
                      crlf "- Monitor overall daily calorie intake"
                      crlf "Recommendation: Try to consolidate into 3-4 proper meals if possible" crlf))
    (if (> ?m 7)
        then (printout t "Frequent eating pattern:"
                      crlf "- Eating more than 7 times per day might indicate unhealthy grazing"
                      crlf "- Watch portion sizes and caloric intake"
                      crlf "Recommendation: Gradually consolidate into fewer meals to avoid overeating" crlf))
    (printout t "Meal Pattern Score: " ?meal-score "%" crlf))

(defrule calculate-food-score
    (declare (salience 75))
    ?meal <- (meal-info (user_id ?id) (meal-score ?meal-score))
    ?appetite <- (appetite-status (user_id ?id) (score ?appetite-score))
    ?nutrient <- (macronutrient-intake (user_id ?id) (score ?nutrient-score))
    (not (food-score (user_id ?id)))  ; Ensure score is calculated only once
=>
    ; Calculate the total raw score
    (bind ?raw-score (+ (* 0.4 ?nutrient-score)  ; Macronutrient contributes 40%
                        (* 0.3 ?meal-score)      ; Meal pattern contributes 30%
                        (* 0.3 (* ?appetite-score 33.33)))) ; Appetite contributes 30%

    ; Normalize raw score to 0-100
    (bind ?normalized-score (min 100 (max 0 (round ?raw-score))))

    ; Assert the final food score
    (assert (food-score (user_id ?id)
                        (total-score ?normalized-score)
                        (appetite-score ?appetite-score)
                        (nutrient-score ?nutrient-score)
                        (meal-score ?meal-score)))

    ; Print the analysis
    (printout t "Food Score Analysis for user " ?id ":" crlf)
    (printout t "Final Food Score (Normalized): " ?normalized-score "%" crlf))

