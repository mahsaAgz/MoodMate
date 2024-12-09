; Rule to track eating pattern persistence
(defrule track-eating-pattern
    (declare (salience 66))
    ; Find consecutive low scores
    ?score1 <- (food-score-trend (user_id ?id)
                                (date ?date1)
                                (total-score ?s1))
    ?score2 <- (food-score-trend (user_id ?id)
                                (date ?date2&:(= ?date2 (+ ?date1 1)))
                                (total-score ?s2))
    (not (eating-pattern (user_id ?id)
                        (date1 ?date1)
                        (date2 ?date2)))
=>
    (assert (eating-pattern 
        (user_id ?id)
        (date1 ?date1)
        (date2 ?date2)
        (score1 ?s1)
        (score2 ?s2)
        (persistence 2)  ; Start with 2 days
        (pattern-type (if (and (< ?s1 40) (< ?s2 40)) 
                         then "restrictive"
                         else (if (and (> ?s1 80) (> ?s2 80))
                              then "binge"
                              else (if (> (abs (- ?s1 ?s2)) 40)
                                   then "irregular"
                                   else "normal")))))))
; Rule to track eating pattern persistence
(defrule track-eating-pattern-persistence
    (declare (salience 66))
    ?pattern1 <- (eating-pattern (user_id ?id)
                                (pattern-type ?type)
                                (date2 ?d1)
                                (persistence ?p1))
    ?pattern2 <- (eating-pattern (user_id ?id)
                                (pattern-type ?type)
                                (date1 ?d1)
                                (date2 ?d2))
    (not (processed-persistence (user_id ?id)
                              (day1 ?d1)
                              (day2 ?d2)))
=>
    (bind ?new-persistence (+ ?p1 1))
    
    ; Assert new pattern with updated persistence
    (assert (eating-pattern
        (user_id ?id)
        (date1 ?d1)
        (date2 ?d2)
        (persistence ?new-persistence)
        (pattern-type ?type)))
    
    ; Mark as processed
    (assert (processed-persistence
        (user_id ?id)
        (day1 ?d1)
        (day2 ?d2)))
    
    (printout t "Eating pattern " ?type " persists for " ?new-persistence " days" crlf))

; Rule to detect severe restrictive pattern
(defrule detect-severe-restrictive-pattern
    (declare (salience 65))
    ?pattern <- (eating-pattern (user_id ?id)
                               (pattern-type "restrictive")
                               (persistence ?p&:(>= ?p 7)))  ; Week or more
    (not (eating-disorder-assessment (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "severe")
        (pattern-type "restrictive")
        (confidence 95)
        (evidence (str-cat "Severe restrictive eating pattern persisting for " ?p " days"))
        (recommendation "Immediate clinical intervention is strongly recommended. The observed persistent pattern of severe food restriction over the past ?p days may indicate serious health risks. Professional evaluation can provide essential support and guidance."))))

; Rule to detect persistent binge pattern
(defrule detect-persistent-binge-pattern
    (declare (salience 65))
    ?pattern <- (eating-pattern (user_id ?id)
                               (pattern-type "binge")
                               (persistence ?p&:(>= ?p 5)))  ; 5+ days
    (not (eating-disorder-assessment (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "binge")
        (confidence 90)
        (evidence (str-cat "Binge eating pattern persisting for " ?p " days"))
        (recommendation "Clinical consultation is advised. The sustained binge eating pattern over ?p days suggests potential challenges with eating habits. A healthcare provider can offer tailored strategies to address this behavior effectively."))))

; Rule to detect persistent irregular pattern
(defrule detect-persistent-irregular-pattern
    (declare (salience 65))
    ?pattern <- (eating-pattern (user_id ?id)
                               (pattern-type "irregular")
                               (persistence ?p&:(>= ?p 4)))  ; 4+ days of irregular eating
    (not (eating-disorder-assessment (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "high")
        (pattern-type "irregular")
        (confidence 85)
        (evidence (str-cat "Irregular eating patterns persisting for " ?p " days"))
        (recommendation "A clinical evaluation is recommended to understand and address the persistent irregular eating patterns observed over ?p days. Early support can help restore consistency and improve overall well-being."))))

; Rule to detect moderate risk patterns
(defrule detect-moderate-eating-risk
    (declare (salience 64))
    ?pattern <- (eating-pattern (user_id ?id)
                               (pattern-type ?type&:(or (eq ?type "restrictive") 
                                                      (eq ?type "binge")
                                                      (eq ?type "irregular")))
                               (persistence ?p&:(and (>= ?p 3) (< ?p 5))))  ; 3-4 days
    (not (eating-disorder-assessment (user_id ?id)))
=>
    (assert (eating-disorder-assessment
        (user_id ?id)
        (risk-level "moderate")
        (pattern-type ?type)
        (confidence 75)
        (evidence (str-cat "Early " ?type " eating pattern detected for " ?p " days"))
        (recommendation "Monitoring your eating patterns is strongly encouraged. The early signs of ?type eating patterns over ?p days may benefit from further attention. If these patterns persist or intensify, consulting a healthcare provider is recommended."))))

; Rule to print eating disorder assessment
(defrule print-eating-disorder-assessment
    (declare (salience 63))
    ?assessment <- (eating-disorder-assessment (user_id ?id)
                                             (risk-level ?risk)
                                             (pattern-type ?type)
                                             (confidence ?conf)
                                             (evidence ?ev)
                                             (recommendation ?rec))
=>
    (printout t crlf "=== Eating Disorder Risk Assessment ===" crlf)
    (printout t "Risk Level: " ?risk crlf)
    (printout t "Pattern Type: " ?type crlf)
    (printout t "Confidence: " ?conf "%" crlf)
    (printout t "Evidence: " ?ev crlf)
    (printout t "Recommendation: " ?rec crlf)
    (printout t "=========================================" crlf))