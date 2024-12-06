(defrule suggest-unified-therapy-anxiety
    (anxiety-assessment (user_id ?id) (risk-level ?risk))
    (profile-input (user_id ?id) 
                  (mbti ?m)
                  (gender ?g)
                  (hobbies ?hobbies))
    (not (therapy-suggestion (user_id ?id) (condition "anxiety")))
=>
    (suggest-therapy ?id ?m ?g ?hobbies ?risk "anxiety" 
        " to reduce anxiety and manage stress"))

(defrule suggest-unified-therapy-depression
    (depression-assessment (user_id ?id) (risk-level ?risk))
    (profile-input (user_id ?id) 
                  (mbti ?m)
                  (gender ?g)
                  (hobbies ?hobbies))
    (not (therapy-suggestion (user_id ?id) (condition "depression")))
=>
    (suggest-therapy ?id ?m ?g ?hobbies ?risk "depression" 
        " to lift mood and increase motivation"))

(defrule suggest-unified-therapy-bipolar
    (bipolar-assessment (user_id ?id) (risk-level ?risk))
    (profile-input (user_id ?id) 
                  (mbti ?m)
                  (gender ?g)
                  (hobbies ?hobbies))
    (not (therapy-suggestion (user_id ?id) (condition "bipolar")))
=>
    (suggest-therapy ?id ?m ?g ?hobbies ?risk "bipolar" 
        " to stabilize emotions and maintain balance"))

(defrule suggest-unified-therapy-sad
    (sad-assessment (user_id ?id) (risk-level ?risk))
    (profile-input (user_id ?id) 
                  (mbti ?m)
                  (gender ?g)
                  (hobbies ?hobbies))
    (not (therapy-suggestion (user_id ?id) (condition "SAD")))
=>
    (suggest-therapy ?id ?m ?g ?hobbies ?risk "SAD" 
        " to combat seasonal effects"))

(defrule suggest-unified-therapy-eating-disorder
    (eating-disorder-assessment (user_id ?id) 
                              (risk-level ?risk)
                              (pattern-type ?ed-type))
    (profile-input (user_id ?id) 
                  (mbti ?m)
                  (gender ?g)
                  (hobbies ?hobbies))
    (not (therapy-suggestion (user_id ?id) (condition "eating-disorder")))
=>
    (suggest-therapy ?id ?m ?g ?hobbies ?risk "eating-disorder" 
        " to develop healthy coping mechanisms"))

(deffunction suggest-therapy (?id ?m ?g ?hobbies ?risk ?condition ?msg-suffix)
    ; Extract MBTI dimensions
    (bind ?ei (sub-string 1 1 ?m))
    (bind ?ns (sub-string 2 2 ?m))
    (bind ?ft (sub-string 3 3 ?m))
    (bind ?pj (sub-string 4 4 ?m))

    ; Introvert-specific suggestions
    (if (eq ?ei "I") then
        (if (and ?hobbies (neq (str-index "art" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition ?condition)
                    (activity_type "solo-art-therapy")
                    (reasoning (str-cat "Creative expression in a private setting" ?msg-suffix))
                    (mbti_factor "Aligned with introverted need for solitary activities")
                    (severity ?risk))))
        
        (if (and ?hobbies (neq (str-index "collection" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition ?condition)
                    (activity_type "collecting-therapy")
                    (reasoning (str-cat "Structured solo activity" ?msg-suffix))
                    (mbti_factor "Supports introverted focus on personal interests")
                    (severity ?risk))))
        
        (if (and ?hobbies (neq (str-index "relax" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition ?condition)
                    (activity_type "meditation-therapy")
                    (reasoning (str-cat "Quiet reflection and mindfulness" ?msg-suffix))
                    (mbti_factor "Perfect for introverted self-reflection")
                    (severity ?risk)))))

    ; Extrovert-specific suggestions
    (if (eq ?ei "E") then
        (if (and ?hobbies (neq (str-index "social" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition ?condition)
                    (activity_type "group-therapy")
                    (reasoning (str-cat "Social interaction in a therapeutic setting" ?msg-suffix))
                    (mbti_factor "Matches extraverted need for social engagement")
                    (severity ?risk))))
        
        (if (and ?hobbies (neq (str-index "performance" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition ?condition)
                    (activity_type "drama-therapy")
                    (reasoning (str-cat "Expression through performance arts" ?msg-suffix))
                    (mbti_factor "Utilizes extraverted energy constructively")
                    (severity ?risk))))
        
        (if (and ?hobbies (neq (str-index "entertainment" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition ?condition)
                    (activity_type "social-recreation")
                    (reasoning (str-cat "Enjoyable group activities" ?msg-suffix))
                    (mbti_factor "Supports extraverted social preferences")
                    (severity ?risk))))))


; Rule to print the suggestions
(defrule print-unified-therapy
    ?suggestion <- (therapy-suggestion (user_id ?id)
                                     (condition ?c)
                                     (activity_type ?a)
                                     (reasoning ?r)
                                     (mbti_factor ?m)
                                     (severity ?s))
=>
    (printout t crlf "=== Personalized Therapy Suggestion ===" crlf)
    (printout t "For condition: " ?c " (Severity: " ?s ")" crlf)
    (printout t "Suggested activity: " ?a crlf)
    (printout t "Why this might work for you: " ?r crlf)
    (printout t "MBTI consideration: " ?m crlf)
    (printout t "=========================================" crlf))
    
    
    
(defrule suggest-general-wellness
    (profile-input (user_id ?id) 
                  (mbti ?m)
                  (gender ?g)
                  (hobbies ?hobbies))
    ; Check that no disorder assessments exist
    (not (anxiety-assessment (user_id ?id)))
    (not (depression-assessment (user_id ?id)))
    (not (bipolar-assessment (user_id ?id)))
    (not (sad-assessment (user_id ?id)))
    (not (eating-disorder-assessment (user_id ?id)))
    (not (therapy-suggestion (user_id ?id) (condition "wellness")))
=>
    ; Extract MBTI dimensions
    (bind ?ei (sub-string 1 1 ?m))
    (bind ?ns (sub-string 2 2 ?m))
    (bind ?ft (sub-string 3 3 ?m))
    (bind ?pj (sub-string 4 4 ?m))

    ; General wellness message
    (bind ?msg-suffix " to maintain mental wellness and emotional balance")

    ; Introvert-specific wellness suggestions
    (if (eq ?ei "I") then
        (if (and ?hobbies (neq (str-index "art" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition "wellness")
                    (activity_type "creative-expression")
                    (reasoning "Regular creative activities help process emotions and maintain mental clarity")
                    (mbti_factor "Suits your preference for independent activities")
                    (severity "preventive"))))
        
        (if (and ?hobbies (neq (str-index "relax" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition "wellness")
                    (activity_type "mindfulness-practice")
                    (reasoning "Daily mindfulness can prevent stress buildup and maintain emotional balance")
                    (mbti_factor "Aligns with your need for quiet reflection")
                    (severity "preventive")))))

    ; Extrovert-specific wellness suggestions
    (if (eq ?ei "E") then
        (if (and ?hobbies (neq (str-index "social" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition "wellness")
                    (activity_type "social-wellness")
                    (reasoning "Regular social interaction helps maintain positive mental health")
                    (mbti_factor "Matches your need for social engagement")
                    (severity "preventive"))))
        
        (if (and ?hobbies (neq (str-index "entertainment" ?hobbies) FALSE)) then
            (assert (therapy-suggestion
                    (user_id ?id)
                    (condition "wellness")
                    (activity_type "recreational-activities")
                    (reasoning "Regular enjoyable activities maintain positive mood")
                    (mbti_factor "Supports your social energy")
                    (severity "preventive")))))

    ; Physical wellness suggestions
    (if (and ?hobbies (neq (str-index "sports" ?hobbies) FALSE)) then
        (assert (therapy-suggestion
                (user_id ?id)
                (condition "wellness")
                (activity_type "physical-wellness")
                (reasoning "Regular physical activity promotes overall mental health")
                (mbti_factor "Provides structured physical outlet")
                (severity "preventive"))))

    ; Nature-based wellness
    (if (and ?hobbies (neq (str-index "outdoor" ?hobbies) FALSE)) then
        (assert (therapy-suggestion
                (user_id ?id)
                (condition "wellness")
                (activity_type "nature-connection")
                (reasoning "Regular time in nature helps maintain mental balance")
                (mbti_factor "Natural environment for stress prevention")
                (severity "preventive"))))

    ; Creative wellness
    (if (and ?hobbies (neq (str-index "DIY" ?hobbies) FALSE)) then
        (assert (therapy-suggestion
                (user_id ?id)
                (condition "wellness")
                (activity_type "creative-wellness")
                (reasoning "Creative projects provide mental stimulation and satisfaction")
                (mbti_factor "Engaging way to maintain mental wellness")
                (severity "preventive"))))

    ; Gaming for mental agility
    (if (and ?hobbies (neq (str-index "game" ?hobbies) FALSE)) then
        (assert (therapy-suggestion
                (user_id ?id)
                (condition "wellness")
                (activity_type "mental-agility")
                (reasoning "Strategic games help maintain cognitive sharpness")
                (mbti_factor "Mental exercise through enjoyable activities")
                (severity "preventive"))))

    ; Culinary wellness
    (if (and ?hobbies (neq (str-index "cooking" ?hobbies) FALSE)) then
        (assert (therapy-suggestion
                (user_id ?id)
                (condition "wellness")
                (activity_type "culinary-wellness")
                (reasoning "Cooking can be a mindful, nurturing practice")
                (mbti_factor "Combines creativity with self-care")
                (severity "preventive"))))

    ; Travel for perspective
    (if (and ?hobbies (neq (str-index "travel" ?hobbies) FALSE)) then
        (assert (therapy-suggestion
                (user_id ?id)
                (condition "wellness")
                (activity_type "exploration-wellness")
                (reasoning "New experiences broaden perspective and maintain mental flexibility")
                (mbti_factor "Supports personal growth through exploration")
                (severity "preventive")))))

; Additional rule to print wellness-specific messages
(defrule print-wellness-suggestion
    ?suggestion <- (therapy-suggestion (user_id ?id)
                                     (condition "wellness")
                                     (activity_type ?a)
                                     (reasoning ?r)
                                     (mbti_factor ?m)
                                     (severity "preventive"))
=>
    (printout t crlf "=== Wellness Lifestyle Suggestion ===" crlf)
    (printout t "Suggested activity: " ?a crlf)
    (printout t "Benefits: " ?r crlf)
    (printout t "Personal fit: " ?m crlf)
    (printout t "=======================================" crlf))