; Rule to detect severe appetite changes
(defrule detect-severe-appetite-change
    (declare (salience 75))
    (appetite-status (user_id ?id) 
                    (option ?opt&:(or (eq ?opt "0a") (eq ?opt "0b")))  ; No appetite or constant craving
                    (score ?score))
    (not (appetite-pattern-assessed (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type (if (eq ?opt "0a") then "restrictive" else "binge"))
        (evidence "Severe appetite disturbance - complete loss or constant craving")
        (recommendation "Immediate clinical evaluation recommended for eating patterns")))
    (assert (appetite-pattern-assessed (user_id ?id))))

; Rule to detect restrictive eating patterns
(defrule detect-restrictive-eating
    (declare (salience 75))
    (meal-info (user_id ?id) (meals-per-day ?m&:(< ?m 2)))  ; Very few meals
    (macronutrient-intake (user_id ?id) 
                         (score ?score&:(< ?score 40)))      ; Low nutrient intake
    (not (restrictive-pattern-assessed (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "severe")
        (pattern-type "anorexic")
        (evidence (str-cat "Severely restricted eating - " ?m " meals per day with low nutrient intake"))
        (recommendation "Urgent clinical evaluation needed for potential anorexia")))
    (assert (restrictive-pattern-assessed (user_id ?id))))

; Rule to detect binge eating patterns
(defrule detect-binge-eating
    (declare (salience 75))
    (or (meal-info (user_id ?id) (meals-per-day ?m&:(> ?m 7)))  ; Frequent meals
        (appetite-status (user_id ?id) (option "0b")))          ; Constant craving
    (macronutrient-intake (user_id ?id) 
                         (score ?score&:(> ?score 80)))         ; High nutrient intake
    (not (binge-pattern-assessed (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "binge")
        (evidence "Frequent eating episodes with high intake")
        (recommendation "Clinical evaluation recommended for binge eating disorder")))
    (assert (binge-pattern-assessed (user_id ?id))))

; Rule to detect irregular eating patterns
(defrule detect-irregular-eating
    (declare (salience 74))
    (meal-info (user_id ?id) (meals-per-day ?m))
    (appetite-status (user_id ?id) (option ?opt))
    (or (and (< ?m 3) (> ?m 0))                               ; Too few meals
        (> ?m 6)                                              ; Too many meals
        (or (eq ?opt "1a") (eq ?opt "1b")))                  ; Significant appetite changes
    (not (irregular-pattern-assessed (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type "irregular")
        (evidence "Irregular meal patterns and appetite changes")
        (recommendation "Monitor eating patterns and consider clinical consultation")))
    (assert (irregular-pattern-assessed (user_id ?id))))

; Rule to print eating disorder assessment with specific recommendations
(defrule print-eating-disorder-assessment
    (declare (salience 73))
    ?assessment <- (eating-disorder-assessment (user_id ?id)
                                             (risk-level ?risk)
                                             (pattern-type ?type)
                                             (evidence ?ev)
                                             (recommendation ?rec))
=>
    (printout t crlf "=== Eating Pattern Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Pattern Type: " ?type crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Recommendation: " ?rec crlf)
    
    ; Print specific pattern-based recommendations
    (if (eq ?type "restrictive")
        then (printout t crlf "Additional Recommendations:"
                      crlf "- Work with a healthcare provider to develop a safe meal plan"
                      crlf "- Consider keeping a food diary"
                      crlf "- Join a support group for eating disorders"
                      crlf "- Gradually increase meal frequency" crlf))
    
    (if (eq ?type "binge")
        then (printout t crlf "Additional Recommendations:"
                      crlf "- Identify trigger situations"
                      crlf "- Practice mindful eating"
                      crlf "- Establish regular meal times"
                      crlf "- Consider cognitive behavioral therapy" crlf))
    
    (if (eq ?type "irregular")
        then (printout t crlf "Additional Recommendations:"
                      crlf "- Establish a regular eating schedule"
                      crlf "- Plan meals in advance"
                      crlf "- Monitor hunger and fullness cues"
                      crlf "- Keep a food and mood journal" crlf))
                      
    (printout t "======================================" crlf))