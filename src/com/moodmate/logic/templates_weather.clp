(deftemplate Weather
   (slot condition) ;; sunny, rainy, cloudy, stormy, etc.
   (slot mood-impact)) ;; impact on mood (e.g., -3 to 3)

(deftemplate Temperature
   (slot value) ;; e.g., mild, hot, cold
   (slot mood-impact)) ;; impact score

(deftemplate Humidity
   (slot level) ;; e.g., low, moderate, high
   (slot mood-impact)) ;; impact score

(deffacts Weather-Facts
   (Weather (condition sunny) (mood-impact 3))
   (Weather (condition rainy) (mood-impact -3))
   (Weather (condition cloudy) (mood-impact -1))
   (Weather (condition stormy) (mood-impact -4))
   (Weather (condition snowy) (mood-impact 1))))





(defglobal ?*mood-score* = 0)
;;;;;;;;;check the condition 
(defrule score-weather
   ?w <- (Weather (condition ?condition) (mood-impact ?impact))
   =>
   (bind ?*mood-score* (+ ?*mood-score* ?impact))
   (printout t "Weather condition: " ?condition " impacts mood by " ?impact "." crlf))
;;;;;;;;Check the temperature score
(defrule score-temperature
   ?t <- (Temperature (value ?temp) (mood-impact ?impact))
   =>
   (bind ?*mood-score* (+ ?*mood-score* ?impact))
   (printout t "Temperature: " ?temp " impacts mood by " ?impact "." crlf))

;;;;;;;check the humidity
(defrule score-humidity
   ?h <- (Humidity (level ?level) (mood-impact ?impact))
   =>
   (bind ?*mood-score* (+ ?*mood-score* ?impact))
   (printout t "Humidity level: " ?level " impacts mood by " ?impact "." crlf))

;;;;;calculate the total score
(defrule normalize-mood-score
   (declare (salience -10)) ;; Ensure this runs last
   =>
   ;; Normalize the score from -10 to 10 to a 0–100 scale
   (bind ?normalized-score (max 0 (min 100 (+ (* 5 ?*mood-score*) 50))))
   (printout t "Final normalized mood score: " ?normalized-score crlf)
   (if (>= ?normalized-score 70)
      then (printout t "Overall, the weather has a POSITIVE impact on mood." crlf)
      else
      (if (<= ?normalized-score 30)
         then (printout t "Overall, the weather has a NEGATIVE impact on mood." crlf)
         else (printout t "Overall, the weather has a NEUTRAL impact on mood." crlf))))

(watch facts)
(watch rules)
(run)


