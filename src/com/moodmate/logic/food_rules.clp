; Rule to calculate appetite score
(defrule calculate-appetite-score
    (declare (salience 78))
    ?appetite <- (appetite-status (user_id ?id) (option ?opt))
    (not (appetite-score-calculated (user_id ?id)))
=>
    (bind ?score
    (if (eq ?opt "3")
        then 3
        else 
            (if (or (eq ?opt "2a") (eq ?opt "2b"))
                then 2
                else 
                    (if (or (eq ?opt "1a") (eq ?opt "1b"))
                        then 1
                        else 0))))

    (modify ?appetite (score ?score))
    (assert (appetite-score-calculated (user_id ?id)))
    (printout t "Appetite Score: " ?score " out of 3" crlf))

; Rule to calculate macronutrient score
(defrule calculate-macronutrient-score
    (declare (salience 77))
    ?intake <- (macronutrient-intake (user_id ?id)
                                    (carbs ?c)
                                    (protein ?p)
                                    (fat ?f)
                                    (minerals ?m)
                                    (vitamins ?v)
                                    (water ?w))
    (not (macronutrient-score-calculated (user_id ?id)))
=>
    ; Calculate individual scores (keeping the same scoring logic)
    (bind ?carbs-score
        (if (< ?c 20) then 30
            else (if (< ?c 40) then 60
                else (if (<= ?c 60) then 100
                    else (if (<= ?c 80) then 70
                        else 40)))))

    (bind ?protein-score
        (if (< ?p 20) then 30
            else (if (< ?p 40) then 60
                else (if (<= ?p 60) then 100
                    else (if (<= ?p 80) then 70
                        else 40)))))

    (bind ?fat-score
        (if (< ?f 20) then 30
            else (if (< ?f 40) then 60
                else (if (<= ?f 60) then 100
                    else (if (<= ?f 80) then 70
                        else 40)))))

    (bind ?water-score
        (if (< ?w 20) then 30
            else (if (< ?w 40) then 60
                else (if (<= ?w 60) then 100
                    else (if (<= ?w 80) then 70
                        else 40)))))

    (bind ?minerals-score
        (if (< ?m 20) then 30
            else (if (< ?m 40) then 60
                else (if (<= ?m 60) then 100
                    else (if (<= ?m 80) then 70
                        else 40)))))

    (bind ?vitamins-score
        (if (< ?v 20) then 30
            else (if (< ?v 40) then 60
                else (if (<= ?v 60) then 100
                    else (if (<= ?v 80) then 70
                        else 40)))))

    (bind ?total-score (round (/ (+ (* 3 ?carbs-score) 
                                   (* 3 ?protein-score) 
                                   (* 3 ?fat-score)
                                   (* 3 ?water-score)
                                   ?minerals-score
                                   ?vitamins-score) 
                                16)))
    
    (assert (macronutrient-score-calculated (user_id ?id)))
    (modify ?intake (score ?total-score)))

; Rule to evaluate meal patterns
(defrule evaluate-meal-patterns
    (declare (salience 76))
    ?meal <- (meal-info (user_id ?id) (meals-per-day ?m))
    (not (meal-pattern-evaluated (user_id ?id)))
=>
    (bind ?meal-score
        (if (= ?m 1) then 30
            else (if (= ?m 2) then 60
                else (if (and (>= ?m 3) (<= ?m 5)) then 90
                    else (if (and (>= ?m 6) (<= ?m 7)) then 60
                        else 30)))))

    (assert (meal-pattern-evaluated (user_id ?id)))
    (modify ?meal (meal-score ?meal-score)))

; Rule to calculate final food score and generate single recommendation
(defrule calculate-food-score-and-recommend
    (declare (salience 70))
   	?meal <- (meal-info (user_id ?id) (meals-per-day ?m) (meal-score ?meal-score))
    ?appetite <- (appetite-status (user_id ?id) (score ?appetite-score))
    ?nutrient <- (macronutrient-intake (user_id ?id) (score ?nutrient-score))
    (not (food-score (user_id ?id)))
=>
    ; Calculate total score
    (bind ?raw-score (+ (* 0.4 ?nutrient-score)
                        (* 0.3 ?meal-score)
                        (* 0.3 (* ?appetite-score 33.33))))
    
    (bind ?normalized-score (min 100 (max 0 (round ?raw-score))))
    
    ; Generate meal pattern message
    (bind ?meal-pattern-msg
        (if (= ?m 1) 
            then "Try increasing from 1 meal to at least 3 meals per day."
            else (if (= ?m 2)
                    then "Consider adding another meal to reach 3 meals daily."
                    else (if (and (>= ?m 3) (<= ?m 5))
                            then "Good job maintaining 3-5 meals per day."
                            else (if (and (>= ?m 6) (<= ?m 7))
                                    then "Consider consolidating your 6-7 meals into 3-5 main meals."
                                    else "Try reducing your frequent meals to 3-5 structured meals daily.")))))

    ; Generate recommendation message
    (bind ?recommendation-msg
        (if (= ?appetite-score 0)
            then (if (< ?normalized-score 40)
                    then (format nil "Urgent medical consultation needed for appetite loss. %s When appetite improves." ?meal-pattern-msg)
                    else (if (< ?normalized-score 60)
                            then (format nil "Consult healthcare provider about appetite. %s As recommended by your doctor." ?meal-pattern-msg)
                            else (format nil "Despite good nutrition, see doctor for appetite changes. %s" ?meal-pattern-msg)))
            else (if (= ?appetite-score 1)
                    then (if (< ?normalized-score 40)
                            then (format nil "Start with small, frequent, nutrient-dense meals. %s" ?meal-pattern-msg)
                            else (if (< ?normalized-score 60)
                                    then (format nil "Gradually increase portions while %s" ?meal-pattern-msg)
                                    else (format nil "Maintain good nutrition while %s" ?meal-pattern-msg)))
                    else (if (= ?appetite-score 2)
                            then (if (< ?normalized-score 40)
                                    then (format nil "Despite mild appetite changes, %s For better nutrition." ?meal-pattern-msg)
                                    else (if (< ?normalized-score 60)
                                            then (format nil "Work on meal timing - %s" ?meal-pattern-msg)
                                            else (format nil "Good nutrition - %s While monitoring appetite." ?meal-pattern-msg)))
                            else (if (< ?normalized-score 40)
                                    then (format nil "With normal appetite, focus on better nutrition. %s" ?meal-pattern-msg)
                                    else (if (< ?normalized-score 60)
                                            then (format nil "Add more variety to meals. %s" ?meal-pattern-msg)
                                            else (format nil "Excellent appetite - %s" ?meal-pattern-msg)))))))

    (bind ?full-message (format nil "Food Score: %d/100. %s" ?normalized-score ?recommendation-msg))

    ; Assert the results
    (assert (food-score (user_id ?id)
                       (total-score ?normalized-score)
                       (appetite-score ?appetite-score)
                       (nutrient-score ?nutrient-score)
                       (meal-score ?meal-score)))
    
    (assert (food-recommendation 
        (user_id ?id)
        (message ?full-message)))
    
    (printout t crlf ?full-message crlf))
