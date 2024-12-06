; Templates definition
(deftemplate weather-input
    (slot condition))

(deftemplate temperature-input
    (slot value))

(deftemplate humidity-input
    (slot level))

(deftemplate weather-recommendation
    (slot user_id)
    (slot message))


; Function to get weather mood impact
(deffunction get-weather-impact (?condition)
    (if (or (eq ?condition "Clear") (eq ?condition "Sunny"))
        then 3
        else (if (eq ?condition "Rain")
                then -3
                else (if (eq ?condition "Clouds")
                        then -1
                        else (if (eq ?condition "Thunderstorm")
                                then -4
                                else (if (eq ?condition "Snow")
                                        then 1
                                        else 0))))))

; Function to get temperature impact
(deffunction get-temp-impact (?temp)
    (if (< ?temp 10)
        then -2
        else (if (< ?temp 20)
                then 2
                else (if (< ?temp 30)
                        then 1
                        else -1))))

; Function to get temperature category
(deffunction get-temp-category (?temp)
    (if (< ?temp 10)
        then "cold"
        else (if (< ?temp 20)
                then "mild"
                else (if (< ?temp 30)
                        then "warm"
                        else "hot"))))

; Function to get humidity impact
(deffunction get-humidity-impact (?humid)
    (if (< ?humid 30)
        then 1
        else (if (< ?humid 60)
                then 2
                else -1)))

; Function to get weather suggestion
(deffunction get-weather-suggestion (?wcond)
    (if (or (eq ?wcond "Clear") (eq ?wcond "Sunny"))
        then "It's a nice clear day! Perfect for outdoor activities. "
        else (if (eq ?wcond "Clouds")
                then "Cloudy conditions - good for light outdoor activities. "
                else (if (eq ?wcond "Rain")
                        then "Rainy weather - best to stay indoors and keep dry. "
                        else (if (eq ?wcond "Thunderstorm")
                                then "Stormy conditions - stay inside and be safe. "
                                else (if (eq ?wcond "Snow")
                                        then "Snowy weather - dress warmly if going outside. "
                                        else "Check local weather updates for specific conditions. "))))))


; Function to get temperature suggestion
(deffunction get-temp-suggestion (?temp-cat)
    (if (eq ?temp-cat "cold")
        then "Bundle up and stay warm. Consider hot drinks. "
        else (if (eq ?temp-cat "mild")
                then "Temperature is comfortable for most activities. "
                else (if (eq ?temp-cat "warm")
                        then "Stay hydrated and don't overexert yourself. "
                        else "Keep cool and avoid prolonged sun exposure. "))))

; Main rule to generate weather effect
(defrule generate-weather-effect
    ?w <- (weather-input (condition ?wcond))
    ?t <- (temperature-input (value ?temp))
    ?h <- (humidity-input (level ?humid))
    =>
    (bind ?w-impact (get-weather-impact ?wcond))
    (bind ?t-impact (get-temp-impact ?temp))
    (bind ?h-impact (get-humidity-impact ?humid))
    (bind ?temp-cat (get-temp-category ?temp))
    
    ; Calculate final score
    (bind ?total-impact (+ ?w-impact ?t-impact ?h-impact))
    (bind ?normalized-score (max 0 (min 100 (+ (* 5 ?total-impact) 50))))
    
    ; Generate suggestion using separate functions
    (bind ?weather-part (get-weather-suggestion ?wcond))
    (bind ?temp-part (get-temp-suggestion ?temp-cat))
    (bind ?humidity-part 
        (if (< ?humid 30)
            then "Low humidity - consider using a humidifier."
            else (if (< ?humid 60)
                    then "Humidity is at a comfortable level."
                    else "High humidity - stay hydrated and avoid strenuous activities.")))
    
    ; Create full message including score
    (bind ?full-message (str-cat "Weather Score: " ?normalized-score "/100. " 
                                ?weather-part ?temp-part ?humidity-part))
    
    ; Assert recommendation with user_id
    (assert (weather-recommendation
        (user_id ?userId)
        (message ?full-message)))
    
    ; Print results for debugging
    (printout t crlf "Weather Impact Analysis:" crlf)
    (printout t "User ID: " ?userId crlf)
    (printout t "Temperature Category: " ?temp-cat crlf)
    (printout t "Weather Score: " ?normalized-score "/100" crlf)
    (printout t "Recommendation: " ?full-message crlf))
