(assert (user-input (username "john") (password "pass123")))
(assert (profile-input (user_id 1) (name "Alice") (gender 1)
                       (age 25) (mbti "unknown") (hobbies "art relax social collection")
                       (notification-frequency 2)))

(assert (mbti-answer (user_id 1) (dimension "EI") (question_id 1) (score 3)))
(assert (mbti-answer (user_id 1) (dimension "EI") (question_id 2) (score -2)))
(assert (mbti-answer (user_id 1) (dimension "SN") (question_id 1) (score 4)))
(assert (mbti-answer (user_id 1) (dimension "SN") (question_id 2) (score 1)))
(assert (mbti-answer (user_id 1) (dimension "TF") (question_id 1) (score -3)))
(assert (mbti-answer (user_id 1) (dimension "TF") (question_id 2) (score -4)))
(assert (mbti-answer (user_id 1) (dimension "JP") (question_id 1) (score 5)))
(assert (mbti-answer (user_id 1) (dimension "JP") (question_id 2) (score -1)))

(assert (user-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "happy") (intensity 60)))
(assert (user-emotion (user_id 1)(day 20241206) (hour 8) (emotion-name "sad") (intensity 40)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "angry") (intensity 80)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "scared") (intensity 10)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "confused") (intensity 80)))

(assert (user-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "happy") (intensity 30)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "sad") (intensity 80)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "angry") (intensity 80)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "scared") (intensity 10)))
(assert (user-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "confused") (intensity 40)))



(assert (normalized-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "happy") (percentage 85)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "sad") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "angry") (percentage 5)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "scared") (percentage 10)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 8) (emotion-name "confused") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "happy") (percentage 80)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "sad") (percentage 10)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "angry") (percentage 5)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "scared") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241206) (hour 10) (emotion-name "confused") (percentage 5)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 8) (emotion-name "happy") (percentage 10)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 8) (emotion-name "sad") (percentage 60)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 8) (emotion-name "angry") (percentage 30)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 8) (emotion-name "scared") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 8) (emotion-name "confused") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 10) (emotion-name "happy") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 10) (emotion-name "sad") (percentage 60)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 10) (emotion-name "angry") (percentage 25)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 10) (emotion-name "scared") (percentage 10)))
(assert (normalized-emotion (user_id 1) (day 20241207) (hour 10) (emotion-name "confused") (percentage 5)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 8) (emotion-name "happy") (percentage 8)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 8) (emotion-name "sad") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 8) (emotion-name "angry") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 8) (emotion-name "scared") (percentage 60)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 8) (emotion-name "confused") (percentage 32)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 10) (emotion-name "happy") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 10) (emotion-name "sad") (percentage 70)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 10) (emotion-name "angry") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 10) (emotion-name "scared") (percentage 0)))
(assert (normalized-emotion (user_id 1) (day 20241208) (hour 10) (emotion-name "confused") (percentage 30)))



(assert (trigger-status (user_id 1) (has-trigger FALSE)))
(assert (rses-level (user_id 1) (level "moderate")))

(assert (self-image-answer (user_id 1) (question_id 1) (answer 3)))
(assert (self-image-answer (user_id 1) (question_id 2) (answer 4)))
(assert (self-image-answer (user_id 1) (question_id 3) (answer 2)))
(assert (self-image-answer (user_id 1) (question_id 4) (answer 1)))
(assert (self-image-answer (user_id 1) (question_id 5) (answer 4)))
(assert (self-image-answer (user_id 1) (question_id 6) (answer 3)))
(assert (self-image-answer (user_id 1) (question_id 7) (answer 2)))
(assert (self-image-answer (user_id 1) (question_id 8) (answer 1)))
(assert (self-image-answer (user_id 1) (question_id 9) (answer 3)))
(assert (self-image-answer (user_id 1) (question_id 10) (answer 4)))

(assert (sleepiness (user_id 1) (sleepy FALSE)))
; Based on satisfaction level (0-3):
; 3 = Very good quality -> recommend power nap
; 2 = Fairly good -> recommend consistent schedule
; 1 = Needs improvement -> recommend sleep hygiene
; 0 = Poor quality -> recommend medical consultation
;(assert (sleep-quality 
 ;   (user_id 1)
  ;  (satisfaction 2)    
   ; (sleep-time "00:30")
    ;(wake-time "09:00")
    ;(sleep-decimal 0.5)
    ;(wake-decimal 9)))

(assert (activity (user_id 1) (has-activity FALSE)))
;(assert (physical-activity  ; Example 1: Short duration
 ;   (user_id 1)
  ;  (has-activity TRUE)
  ;  (duration 100)            
   ; (intensity "light")))

(assert (appetite-status (user_id 1) (option "3")))
(assert (macronutrient-intake 
    (user_id 1)
    (carbs 30)       ; Lower than needed
    (protein 45)     ; Good amount
    (fat 50)         ; Good amount
    (minerals 40)    ; Borderline
    (vitamins 45)    ; Good amount
    (water 20)))     ; Too low

(assert (meal-info (user_id 1) (meals-per-day 2)))  ; Low frequency    

(assert (daily-weather (user_id 1) (day 1) (condition "cloudy") (temperature 5)))

(assert (daily-weather (user_id 1) (day 2) (condition "rainy") (temperature 8)))


(assert (depression-assessment 
    (user_id 1)
    (risk-level "moderate")
    (evidence "Elevated depression levels with physical symptoms")
    (recommendation "Consider professional consultation")))

(assert (food-score-trend (user_id 1) (date 20241204) (total-score 80)))
(assert (food-score-trend (user_id 1) (date 20241205) (total-score 10)))
(assert (food-score-trend (user_id 1) (date 20241206) (total-score 10)))

(assert (sleep-score-trend (user_id 1) (date 20241204) (total-score 40)))
(assert (sleep-score-trend (user_id 1) (date 20241205) (total-score 10)))
(assert (sleep-score-trend (user_id 1) (date 20241206) (total-score 10)))
