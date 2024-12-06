(deftemplate user-input
    (slot username)
    (slot email)
    (slot password)
)

(deftemplate validation-result
    (slot valid)
    (slot message)
)
(deftemplate email-validation-result
    (slot valid)
    (slot message)
)
(deftemplate password-validation-result
    (slot valid)
    (slot message)
)
(deftemplate sign-in-input
    (slot username)
    (slot password)
)
(deftemplate sign-in-result
    (slot valid)
    (slot message)
)

(deftemplate user-record
    (slot username)
    (slot password)
)


(deftemplate profile-input
    (slot user_id)              ;; Integer, unique identifier
    (slot name)                 ;; String, user name
    (slot gender)               ;; Integer, 0 for male, 1 for female, 2 for other
    (slot age)                  ;; Integer, age in years
    (slot mbti (default nil))   ;; String, one of 16 MBTI types or "unknown"
    (slot hobbies)              ;;list of hobbies
    (slot notification-frequency (default 1))) ;; Integer, hours between notifications

(deftemplate mbti-answer
    (slot user_id)       ;; Integer, references profile-input user_id
    (slot dimension)     ;; String, MBTI dimension: EI, SN, TF, JP
    (slot question_id)   ;; Integer, question identifier
    (slot score))        ;; Integer, score for the question (-5 to 5)

(deftemplate self-image-answer
    (slot user_id)       ;; Integer, references profile-input user_id
    (slot question_id)   ;; Integer, question identifier
    (slot answer))       ;; Integer, answer for the question (1 to 4)

(deftemplate user-emotion
    (slot user_id)
    (slot day)           ; Add day
    (slot hour)          ; Add hour
    (slot emotion-name)
    (slot intensity (type INTEGER) (default 0)))

(deftemplate normalized-emotion
    (slot user_id)
    (slot day)           ; Add day
    (slot hour)          ; Add hour
    (slot emotion-name)
    (slot percentage))

; Template to track running totals and counts
(deftemplate emotion-total
    (slot user_id)
    (slot day)
    (slot emotion-name)
    (slot sum (default 0))
    (slot count (default 0)))

; Template to track processed readings
(deftemplate processed-reading
    (slot user_id)
    (slot day)
    (slot hour)
    (slot emotion-name))

; Add template for daily summary
(deftemplate daily-emotion-summary
    (slot user_id)
    (slot day)
    (slot emotion-name)
    (slot avg-percentage)
    (slot reading-count))

(deftemplate total-intensity
    (slot user_id)
    (slot day)           ; Add day
    (slot hour)          ; Add hour
    (slot value))

(deftemplate trigger-status
    (slot user_id)
    (slot has-trigger))  ; TRUE or FALSE

(deftemplate rses-level
    (slot user_id)    
    (slot level))     ; "high", "low", "moderate"

(deftemplate recommendation
    (slot user_id)
    (slot message))

; Template for RSES (Rosenberg Self-Esteem Scale)
(deftemplate rses-score
    (slot user_id)
    (slot score)
    (slot level))  ; "low", "moderate", "high"

(deftemplate need-second-factors
    (slot user_id)
    (slot need)) ; TRUE or FALSE

(deftemplate printed-recommendation
    (slot user_id))

(deftemplate sleepiness
    (slot user_id)
    (slot sleepy)) ; TRUE or FALSE

(deftemplate sleep-quality
    (slot user_id)
    (slot satisfaction)    ; 0-3 (0=very poor, 3=very good)
    (slot sleep-time)      ; e.g., "23:30" (for display)
    (slot wake-time)       ; e.g., "07:00" (for display)
    (slot sleep-decimal)   ; e.g., 23.5 (for calculation)
    (slot wake-decimal)    ; e.g., 7.0 (for calculation)
    (slot score))          ; final calculated score 0-100

(deftemplate sleep-score-calculated
	(slot user_id))

(deftemplate sleep-recommendation
    (slot user_id)
    (slot message))
    
(deftemplate printed-sleep-recommendation
    (slot user_id))
 
(deftemplate activity
    (slot user_id)
    (slot has-activity)) ; TRUE or FALSE
   
; Template for physical activity
(deftemplate physical-activity
    (slot user_id)
    (slot has-activity (type SYMBOL))    ; TRUE or FALSE
    (slot duration (type INTEGER))       ; in minutes
    (slot intensity)                     ; "high", "moderate", "light"
    (slot score (type INTEGER) (default 0)))

; Add a template for tracking score calculation
(deftemplate activity-score-calculated
    (slot user_id))

; Template for physical activity recommendation
(deftemplate physical-activity-recommendation
    (slot user_id)
    (slot message))

; Template for tracking printed recommendations
(deftemplate printed-activity-recommendation
    (slot user_id))
    
; Template for tracking appetite score calculation
(deftemplate appetite-score-calculated
    (slot user_id))
    
; Templates for food scoring
(deftemplate appetite-status
    (slot user_id)
    (slot option)        ; "3", "2a", "2b", "1a", "1b", "0a", "0b"
    (slot score))        ; calculated score 0-3

(deftemplate food-recommendation
    (slot user_id)
    (slot message))

(deftemplate macronutrient-score-calculated
    (slot user_id))

(deftemplate macronutrient-intake
    (slot user_id)
    (slot carbs)         ; percentage 0-100
    (slot protein)       ; percentage 0-100
    (slot fat)           ; percentage 0-100
    (slot minerals)      ; percentage 0-100
    (slot vitamins)      ; percentage 0-100
    (slot water)         ; percentage 0-100
    (slot score (type INTEGER) (default 0)))    ; percentage 0-100

(deftemplate meal-info
    (slot user_id)
    (slot meals-per-day)
    (slot meal-score))    
    
(deftemplate meal-pattern-evaluated
    (slot user_id))
    
(deftemplate food-score
    (slot user_id)
    (slot total-score)        ; normalized 0-100
    (slot appetite-score)     ; component scores
    (slot nutrient-score)
    (slot meal-score))

(deftemplate food-warning
    (slot user_id)
    (slot warning-type)   ; "macronutrient" or "meal"
    (slot message))

; Update the emotional-pattern template to include a default value for persistence
(deftemplate emotional-pattern
    (slot user_id)
    (slot day)
    (slot pattern-type)      ; "manic", "depressive", "mixed", etc.
    (slot intensity)         ; "mild", "moderate", "severe"
    (slot persistence (type INTEGER) (default 1))  ; Added default value
    (slot description))      ; detailed description of why this pattern was detected

(deftemplate processed-switch
    (slot user_id)
    (slot day1)
    (slot day2))

(deftemplate counter
    (slot user_id)
    (slot type)
    (slot count))

(deftemplate processed-pattern
    (slot user_id)
    (slot pattern-type)
    (slot day)
    (slot intensity))
    
; Template to track processed persistence checks
(deftemplate processed-persistence
    (slot user_id)
    (slot day1)
    (slot day2)
    (slot pattern-type))

; Template for bipolar assessment
(deftemplate bipolar-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot confidence)        ; 0-100% 
    ; how certain we are about the risk assessment based on the available evidence
    (slot evidence)          ; Description of evidence
    (slot recommendation))   ; Clinical recommendations

; Template for depression assessment
(deftemplate depression-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot confidence)        ; 0-100%
    (slot evidence)          ; Description of evidence
    (slot recommendation))   ; Clinical recommendations

; Template for anxiety assessment
(deftemplate anxiety-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot evidence)          ; Description of evidence
    (slot recommendation))   ; Clinical recommendations

; Template for daily weather
(deftemplate daily-weather
    (slot user_id)
    (slot day)
    (slot condition)     ; "sunny", "cloudy", "rainy", "snowy"
    (slot temperature))  ; temperature in Celsius

; Template for SAD assessment
(deftemplate sad-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot evidence)          ; Description of evidence
    (slot recommendation))   ; Clinical recommendations

; Templates for assessment control
(deftemplate appetite-pattern-assessed
    (slot user_id))

(deftemplate restrictive-pattern-assessed
    (slot user_id))

(deftemplate binge-pattern-assessed
    (slot user_id))

(deftemplate irregular-pattern-assessed
    (slot user_id))

; Template for eating disorder assessment
(deftemplate eating-disorder-assessment
    (slot user_id)
    (slot risk-level)        ; "low", "moderate", "high", "severe"
    (slot pattern-type)      ; "anorexic", "bulimic", "binge", "restrictive"
    (slot evidence)
    (slot recommendation))

(deftemplate therapy-suggestion
    (slot user_id)
    (slot condition)     ; "anxiety", "depression", "bipolar", "SAD", "eating-disorder"
    (slot activity_type) ; Based on hobbies
    (slot reasoning)     ; Why this suggestion matches their profile
    (slot mbti_factor)   ; How it aligns with their MBTI
    (slot severity))     ; Match with risk level from assessments


