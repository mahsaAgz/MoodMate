; Validation rule for emotion intensity
(defrule validate-emotion-intensity
    (declare (salience 93))
    (user-emotion (intensity ?i))
    (test (or (< ?i 0) (> ?i 100)))
=>
    (assert (validation-result 
        (valid FALSE)
        (message "Emotion intensity must be between 0 and 100"))))
        
; Calculate total intensity with user_id
(defrule calculate-total-intensity
    (declare (salience 92))
    ?user-emotion <- (user-emotion (user_id ?id) (day ?d) (hour ?h))
    (not (total-intensity (user_id ?id) (day ?d) (hour ?h)))  ; Fixed the not syntax

    (or (and (not (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "happy")))
             (bind ?hi 0))
        (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "happy") (intensity ?hi)))
    (or (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "sad") (intensity ?si))
        (and (not (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "sad")))
             (bind ?si 0)))
    (or (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "angry") (intensity ?ai))
        (and (not (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "angry")))
             (bind ?ai 0)))
    (or (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "scared") (intensity ?sci))
        (and (not (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "scared")))
             (bind ?sci 0)))
    (or (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "confused") (intensity ?ci))
        (and (not (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name "confused")))
             (bind ?ci 0)))
=>
    ; Calculate total using the matched values
    (bind ?total (+ ?hi ?si ?ai ?sci ?ci))
                    
    (assert (total-intensity (user_id ?id) (day ?d) (hour ?h) (value ?total)))
    (printout t "Total intensity for user " ?id ": " ?total crlf))
    
; Calculate EFT percentages with user_id
(defrule normalize-emotions
    (declare (salience 91))
    (total-intensity (user_id ?id) (day ?d) (hour ?h) (value ?total))
    (user-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name ?ename) (intensity ?i))
    (not (normalized-emotion (user_id ?id) (day ?d) (hour ?h) (emotion-name ?ename)))  ; Don't normalize twice
    (test (> ?total 0))  ; Prevent division by zero
=>
    (bind ?percentage (* (/ ?i ?total) 100))
    (assert (normalized-emotion 
        (user_id ?id) 
        (day ?d) 
        (hour ?h)
        (emotion-name ?ename)
        (percentage ?percentage)))
    (printout t "User " ?id  " day " ?d " hour " ?h" emotion " ?ename " normalized: " (round ?percentage) "%" crlf))

(defrule find-normalized-emotions
    ?f <- (normalized-emotion (user_id ?uid) (day ?d) (hour ?hour) 
           (emotion-name ?emotion-name) (percentage ?percentage))
    =>
    (return ?hour ?emotion-name ?percentage))