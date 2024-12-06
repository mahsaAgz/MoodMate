(defrule validate-profile-basic
    (declare (salience 98))
    ?input <- (profile-input (name ?name) (age ?age) (gender ?gender))
    (not (profile-validated))  ; Add this line to prevent multiple firings
    (test (neq ?name ""))
    (test (> ?age 0))
    (test (<= ?age 120))
    (test (or (eq ?gender 0)
              (eq ?gender 1) 
              (eq ?gender 2)))
=>
    (assert (profile-validated))  ; Add this line to indicate validation is done
    (printout t "Basic profile validated successfully for user: " ?name crlf))

(defrule check-mbti
    (declare (salience 97))
    ?input <- (profile-input (user_id ?id) (name ?name) (mbti ?mbti))
    (not (mbti-checked))  ; Control fact
    (test (member$ ?mbti (create$ "INTJ" "ENTJ" "INFJ" "ENFJ" 
                                 "INTP" "ENTP" "INFP" "ENFP" 
                                 "ISTJ" "ESTJ" "ISFJ" "ESFJ" 
                                 "ISTP" "ESTP" "ISFP" "ESFP" "unknown")))
=>
    (assert (mbti-checked))  ; Prevent multiple firings
    (if (eq ?mbti "unknown") 
        then (printout t "MBTI is unknown for user: " ?name ". Assigning test for MBTI classification." crlf))
    (if (neq ?mbti "unknown") 
        then (printout t "MBTI provided for user: " ?name " is valid: " ?mbti crlf)))

(defrule calculate-mbti
    (declare (salience 96)) ;; Ensure MBTI is calculated before printing
    ?profile <- (profile-input (user_id ?id) (mbti "unknown"))
    (mbti-answer (user_id ?id) (dimension "EI") (question_id 1) (score ?q1))
    (mbti-answer (user_id ?id) (dimension "EI") (question_id 2) (score ?q2))
    (mbti-answer (user_id ?id) (dimension "SN") (question_id 1) (score ?q3))
    (mbti-answer (user_id ?id) (dimension "SN") (question_id 2) (score ?q4))
    (mbti-answer (user_id ?id) (dimension "TF") (question_id 1) (score ?q5))
    (mbti-answer (user_id ?id) (dimension "TF") (question_id 2) (score ?q6))
    (mbti-answer (user_id ?id) (dimension "JP") (question_id 1) (score ?q7))
    (mbti-answer (user_id ?id) (dimension "JP") (question_id 2) (score ?q8))
=>
    (bind ?EI (if (> (+ ?q1 ?q2) 0) then "E" else "I"))
    (bind ?SN (if (> (+ ?q3 ?q4) 0) then "S" else "N"))
    (bind ?TF (if (> (+ ?q5 ?q6) 0) then "T" else "F"))
    (bind ?JP (if (> (+ ?q7 ?q8) 0) then "J" else "P"))
    (bind ?mbti (str-cat ?EI ?SN ?TF ?JP))
    (modify ?profile (mbti ?mbti))
    (printout t "Calculated MBTI for user " ?id ": " ?mbti crlf))


(defrule update-profile
    (declare (salience 99))  ; Run this first
    ?old <- (profile-input (user_id ?id))
    ?new <- (profile-input (user_id ?id))
    (test (neq ?old ?new))
=>
    (retract ?old)
    (bind ?freq (fact-slot-value ?new notification-frequency))
    (printout t "Updated notification frequency to: " ?freq " hours" crlf))

(defrule print-profile
    (declare (salience 94))
    ?profile <- (profile-input (user_id ?id)
                             (name ?name)
                             (gender ?gender)
                             (age ?age)
                             (mbti ?mbti)
                             (hobbies ?hobbies)
                             (notification-frequency ?nf))
=>
    (bind ?hobbies-str (if (eq ?hobbies nil) then "none" else ?hobbies))
    (bind ?nf-str (if (eq ?nf nil) then "not set" else (str-cat ?nf " hours")))
    
    (printout t "User Profile for ID: " ?id crlf
              "Name: " ?name crlf
              "Gender: " ?gender crlf
              "Age: " ?age crlf
              "MBTI: " ?mbti crlf
              "Hobbies: " ?hobbies-str crlf
              "Notification Frequency: " ?nf-str crlf crlf))
