(deftemplate Recommendation
   (slot message)
)
(deftemplate Weather
   (slot condition) ;; sunny, rainy, cloudy, stormy, etc.
   (slot mood-impact
	(type NUMBER))) ;; impact on mood (e.g., -3 to 3)

(deftemplate Temperature
   (slot value) ;; e.g., mild, hot, cold
   (slot mood-impact
	(type NUMBER))) ;; impact score

(deftemplate Humidity
   (slot level) ;; e.g., low, moderate, high
   (slot mood-impact
	(type NUMBER))) ;; impact score

(deftemplate humidity-input
   (slot level
    (type NUMBER)))

(deftemplate temperature-input
   (slot value
    (type NUMBER)))
(deftemplate weather-input
   (slot condition))

(deftemplate FinalEffect
   (slot temperature)
   (slot final-mood-score (default 0)))

(defglobal ?*mood-score* = 0) ;; Initialize the mood score

;;;;;;;;;;; Rules to Map Specific Keywords to General Conditions
(defrule calculate-cloudy-condition
   (declare (salience 10))
   ?input <- (weather-input (condition ?raw-condition))
   (or (test (eq ?raw-condition "Clouds"))
	(test (eq ?raw-condition "Mist"))
	(test (eq ?raw-condition "Haze"))
	(test (eq ?raw-condition "Fog")));; Match specific input
   =>
   (assert (Weather (condition cloudy) (mood-impact -1))) ;; Assert "cloudy" with its impact
   (printout t "Mapped Weather Condition: cloudy, Impact: -1" crlf))

(defrule calculate-sunny-weather
   (declare (salience 10))
   ?input <- (weather-input (condition ?raw-condition))
   (or (test (eq ?raw-condition "Clear"))
       (test (eq ?raw-condition "Sunny"))) ;; Match specific input
   =>
   (assert (Weather (condition sunny) (mood-impact 3))) ;; Assert "sunny" with its impact
   (printout t "Mapped Weather Condition: sunny, Impact: 3" crlf))

(defrule calculate-rainy-weather
   (declare (salience 10))
   ?input <- (weather-input (condition ?raw-condition))
   (or (test (eq ?raw-condition "Rain"))
       (test (eq ?raw-condition "Drizzle"))) ;; Match specific input
   =>
   (assert (Weather (condition rainy) (mood-impact -3))) ;; Assert "rainy" with its impact
   (printout t "Mapped Weather Condition: rainy, Impact: -3" crlf))

(defrule calculate-stormy-weather
   (declare (salience 10))
   ?input <- (weather-input (condition ?raw-condition))
   (or (test (eq ?raw-condition "Storm"))
	(test (eq ?raw-condition "Thunderstorm"))
	(test (eq ?raw-condition "Squall"))
	(test (eq ?raw-condition "Tornado"))) ;; Match specific input
   =>
   (assert (Weather (condition stormy) (mood-impact -5))) ;; Assert "stormy" with its impact
   (printout t "Mapped Weather Condition: stormy, Impact: -5" crlf))

(defrule calculate-snowy-weather
   (declare (salience 10))
   ?input <- (weather-input (condition ?raw-condition))
   (test (eq ?raw-condition "Snow"))   
=>
   (assert (Weather (condition snowy) (mood-impact 1))) ;; Assert "snowy" with its impact
   (printout t "Mapped Weather Condition: snowy, Impact: 1" crlf))

(defrule calculate-hazardous-weather
   (declare (salience 10))
   ?input <- (weather-input (condition ?raw-condition))
   (or (test (eq ?raw-condition "Smoke"))
	(test (eq ?raw-condition "Dust"))
	(test (eq ?raw-condition "Sand"))
	(test (eq ?raw-condition "Ash"))) ;; Match specific input
   =>
   (assert (Weather (condition hazardous) (mood-impact -4))) ;; Assert "stormy" with its impact
   (printout t "Mapped Weather Condition: stormy, Impact: -4" crlf))


;;;;;;;;; Rules to Calculate Temperature Level and Impact

(defrule calculate-mild-temperature
   (declare (salience 10))
   ?t <- (temperature-input (value ?temp))
   (test (and (>= ?temp 10) (<= ?temp 21))) ;; if temp is between 10°C and 21°C
   =>
   (assert (Temperature (value "mild") (mood-impact 3)))  ;; assert "mild"
   (assert (FinalEffect (temperature "mild"))))

(defrule calculate-cold-temperature
   (declare (salience 10))
   ?t <- (temperature-input (value ?temp))
   (test (< ?temp 10))  ;; if temp is below 10°C
   =>
   (assert (Temperature (value "cold") (mood-impact -2)))  ;; assert "cold"
   (assert (FinalEffect (temperature "cold"))))

(defrule calculate-hot-temperature
   (declare (salience 10))
   ?t <- (temperature-input (value ?temp))
   (test (> ?temp 24))  ;; if temp is above 24°C
   =>
   (assert (Temperature (value "hot") (mood-impact -3)))  ;; assert "hot"
   (assert (FinalEffect (temperature "hot"))))

(defrule calculate-optimal-temperature
   (declare (salience 10))
   ?t <- (temperature-input (value ?temp))
   (test (and (>= ?temp 21) (<= ?temp 22)))  ;; if temp is between 21°C-22°C
   =>
   (assert (Temperature (value "optimal") (mood-impact 4)))  ;; assert "optimal"
   (assert (FinalEffect (temperature "optimal"))))




;;;;;;;;;;;;; Rule to Calculate Humidity Level and Impact

(defrule calculate-humidity
   (declare (salience 10))
   ?h <- (humidity-input (level ?level))
   ;(test (and (>= ?level 40) (<= ?level 70))) ;; moderate humidity range
   =>
   (if (and (>= ?level 40)(<= ?level 70))
      then (assert (Humidity (level "moderate") (mood-impact 1)))
      else
      (if (< ?level 40)
         then (assert (Humidity (level "low") (mood-impact 2)))
         else (assert (Humidity (level "high") (mood-impact -3)))))

   )  ;; assert moderate humidity

;(defrule calculate-high-humidity
 ;  ?h <- (humidity-input (level ?level))
  ; (test (> ?level 70)) ;; high humidity (>70%)
   ;=>
   ;(assert (Humidity (level "high") (mood-impact -3))))  ;; assert high humidity

;(defrule calculate-low-humidity
 ;  ?h <- (humidity-input (level ?level))
  ; (test (< ?level 40)) ;; low humidity (<40%)
   ;=>
   ;(assert (Humidity (level "low") (mood-impact 2))))  ;; assert low humidity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;;;;;;;;;;;;;;;;;;;;; Rule to Calculate Mood from Weather
(defrule score-weather
   (declare (salience 10))
   ?w <- (Weather (condition ?condition) (mood-impact ?impact))
   =>
   (bind ?*mood-score* (+ ?*mood-score* ?impact))
   (printout t "Weather condition: " ?condition " impacts mood by " ?impact ",, Total Mood Score: " ?*mood-score* crlf))


;;;;;;;;Check the temperature score
(defrule score-temperature
   (declare (salience 10))
   ?t <- (Temperature (value ?temp) (mood-impact ?impact))
   =>
   (bind ?*mood-score* (+ ?*mood-score* ?impact))
   (printout t "Temperature: " ?temp " impacts mood by " ?impact ", Total Mood Score: " ?*mood-score* crlf))

;;;;;;;check the humidity
(defrule score-humidity
   (declare (salience 10))
   ?h <- (Humidity (level ?level) (mood-impact ?impact))
   =>
   (bind ?*mood-score* (+ ?*mood-score* ?impact))
   (printout t "Humidity level: " ?level " impacts mood by " ?impact ", Total Mood Score: " ?*mood-score* crlf))

;;;;;;;;;;; Normalize Mood Score
(defrule normalize-mood-score
   (declare (salience 1)) ;; Ensure this runs last
   ?fsc <- (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (finalize-score) ;; Trigger fact
   =>
   (printout t "Current mood score before normalization: " ?*mood-score* crlf)
   (bind ?normalized-score (max 0 (min 100 (+ (* 5 ?*mood-score*) 50)))) ;; Normalize score
   (printout t "Final normalized mood score: " ?normalized-score crlf)
   (modify ?fsc (final-mood-score ?normalized-score))
   (printout t "temp and score: " ?temp ?score crlf)
   (if (>= ?normalized-score 70)
      then (printout t "Overall, the weather has a POSITIVE impact on mood." crlf)
      else
      (if (<= ?normalized-score 30)
         then (printout t "Overall, the weather has a NEGATIVE impact on mood." crlf)
         else (printout t "Overall, the weather has a NEUTRAL impact on mood." crlf))))


;;;;;;;;;;;;;; Rules for Weather-Dependent Recommendations

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Positive Mood and Sunny Weather
(defrule positive-mood-sunny-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition sunny) (mood-impact ?impact))
   (test (>= ?score 70))  ;; Positive Mood
   =>
   (assert (Recommendation (message "Your mood is great! Enjoy the uplifting energy by engaging in activities that you love, like exercising, creating art, or meeting friends.")))
   (assert (Recommendation (message "Take advantage of the sunny weather to step outside and soak up some Vitamin D. Try a bike ride or go for a picnic.")))
   (assert (Recommendation (message "Boost your mood with a gratitude journal or plan a fun event for the future.")))
   (printout t "Positive mood and sunny weather: Suggestions for outdoor activities and creative pursuits!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Positive Mood and Cloudy Weather
(defrule positive-mood-cloudy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition cloudy) (mood-impact ?impact))
   (test (>= ?score 70))  ;; Positive Mood
   =>
   (assert (Recommendation (message "Your mood is positive! Even with cloudy weather, you can enjoy indoor activities like painting or baking.")))
   (assert (Recommendation (message "Stay active by taking a brisk walk or stretching indoors.")))
   (printout t "Positive mood and cloudy weather: Indoor activities and light exercise suggestions!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Positive Mood and Snowy Weather
(defrule positive-mood-snowy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition snowy) (mood-impact ?impact))
   (test (>= ?score 70))  ;; Positive Mood
   =>
   (assert (Recommendation (message "Your positive mood is enhanced by the snowy weather! Try skiing, snowball fights, or simply enjoy the beautiful view.")))
   (assert (Recommendation (message "If indoors, enjoy the cozy atmosphere with a warm drink and good company.")))
   (printout t "Positive mood and snowy weather: Suggestions for outdoor activities and cozy indoor time!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Neutral Mood and Sunny Weather
(defrule neutral-mood-sunny-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition sunny) (mood-impact ?impact))
   (test (and (< ?score 70) (> ?score 30)))  ;; Neutral Mood
   =>
   (assert (Recommendation (message "The sunny weather might nudge your mood up. Take a short walk outside and enjoy the sunshine.")))
   (assert (Recommendation (message "Consider light activities like yoga in the park or a stroll through nature.")))
   (printout t "Neutral mood and sunny weather: Suggestions for light physical activities." crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Neutral Mood and Cloudy Weather
(defrule neutral-mood-cloudy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition cloudy) (mood-impact ?impact))
   (test (and (< ?score 70) (> ?score 30)))  ;; Neutral Mood
   =>
   (assert (Recommendation (message "The cloudy weather may feel dull. Try indoor activities like reading or meditating.")))
   (assert (Recommendation (message "If feeling uninspired, listen to music or call a friend.")))
   (printout t "Neutral mood and cloudy weather: Suggestions for indoor activities and light interactions!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Neutral Mood and Snowy Weather
(defrule neutral-mood-snowy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition snowy) (mood-impact ?impact))
   (test (and (< ?score 70) (> ?score 30)))  ;; Neutral Mood
   =>
   (assert (Recommendation (message "Snowy weather offers a peaceful atmosphere. Try building a snowman or taking a photo of the snow.")))
   (assert (Recommendation (message "If staying indoors, enjoy the view with a hot drink or step outside for a short walk.")))
   (printout t "Neutral mood and snowy weather: Suggestions for both outdoor and indoor activities!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Negative Mood and Sunny Weather
(defrule negative-mood-sunny-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition sunny) (mood-impact ?impact))
   (test (<= ?score 30))  ;; Negative Mood
   =>
   (assert (Recommendation (message "Even with sunny weather, your mood is low. Step outside for a brief walk and try to soak in the sunshine.")))
   (assert (Recommendation (message "Use the sunlight to your advantage: open windows, stretch, and breathe in fresh air.")))
   (printout t "Negative mood and sunny weather: Suggestions for small outdoor steps to improve mood!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Negative Mood and Cloudy Weather
(defrule negative-mood-cloudy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition cloudy) (mood-impact ?impact))
   (test (<= ?score 30))  ;; Negative Mood
   =>
   (assert (Recommendation (message "Cloudy weather can add to your low mood. Try listening to upbeat music or practicing mindfulness exercises.")))
   (assert (Recommendation (message "Surround yourself with comforting items like a favorite blanket or a warm drink.")))
   (printout t "Negative mood and cloudy weather: Suggestions for indoor comforts and mood boosters!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Negative Mood and Snowy Weather
(defrule negative-mood-snowy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition snowy) (mood-impact ?impact))
   (test (<= ?score 30))  ;; Negative Mood
   =>
   (assert (Recommendation (message "Snowy weather might feel isolating. Bring warmth into your day with a favorite movie or a hot drink.")))
   (assert (Recommendation (message "If you prefer, step outside for a short walk to feel the fresh air and calm atmosphere.")))
   (printout t "Negative mood and snowy weather: Suggestions for comfort and renewal in the snow!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Negative Mood and Rainy Weather
(defrule negative-mood-rainy-weather
   (declare (salience 0))
   (FinalEffect (final-mood-score ?score) (temperature ?temp))
   (Weather (condition rainy) (mood-impact ?impact))
   (test (<= ?score 30))  ;; Negative Mood
   =>
   (assert (Recommendation (message "Rainy weather may add to your low mood. Stay indoors and focus on relaxing activities like watching a movie or journaling.")))
   (assert (Recommendation (message "Brighten up your environment with warm lighting, a hot drink, and uplifting music.")))
   (printout t "Negative mood and rainy weather: Suggestions for indoor relaxation and mood improvement!" crlf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Rule for Hazardous Weather (for all moods)
(defrule hazardous-weather
   (declare (salience 0))
   (Weather (condition ?condition) (mood-impact ?impact))
   (test (eq ?condition "hazardous"))
   =>
   (assert (Recommendation (message "Hazardous weather requires staying indoors. Use an air purifier if available, and avoid outdoor exposure.")))
   (assert (Recommendation (message "Stay hydrated, eat light meals, and rest. If you need to go outside, wear a mask.")))
   (printout t "Hazardous weather: Recommendations for staying safe and comfortable indoors." crlf))
   


(watch rules)
(watch facts)
(run)
