; Function to map raw weather condition to category
(deffunction map-weather-condition (?raw-condition)
    (if (or (eq ?raw-condition "Clear") (eq ?raw-condition "Sunny"))
        then "sunny"
        else (if (or (eq ?raw-condition "Clouds") (eq ?raw-condition "Mist") 
                     (eq ?raw-condition "Haze") (eq ?raw-condition "Fog"))
                then "cloudy"
                else (if (or (eq ?raw-condition "Rain") (eq ?raw-condition "Drizzle"))
                        then "rainy"
                        else (if (or (eq ?raw-condition "Storm") (eq ?raw-condition "Thunderstorm") 
                                   (eq ?raw-condition "Squall") (eq ?raw-condition "Tornado"))
                                then "stormy"
                                else (if (eq ?raw-condition "Snow")
                                        then "snowy"
                                        else (if (or (eq ?raw-condition "Smoke") (eq ?raw-condition "Dust")
                                                   (eq ?raw-condition "Sand") (eq ?raw-condition "Ash"))
                                                then "hazardous"
                                                else "unknown")))))))
; Function to get weather mood impact based on category
(deffunction get-weather-impact (?condition)
    (bind ?category (map-weather-condition ?condition))
    (if (eq ?category "sunny")
        then 3
        else (if (eq ?category "rainy")
                then -3
                else (if (eq ?category "cloudy")
                        then -1
                        else (if (eq ?category "stormy")
                                then -4
                                else (if (eq ?category "snowy")
                                        then 1
                                        else (if (eq ?category "hazardous")
                                                then -5
                                                else 0)))))))

; Function to get weather suggestion based on category
(deffunction get-weather-suggestion (?wcond)
    (bind ?category (map-weather-condition ?wcond))
    (if (eq ?category "sunny")
        then "It's a beautiful sunny day! Perfect for outdoor activities. "
        else (if (eq ?category "cloudy")
                then "Cloudy conditions - good for light outdoor activities. Consider bringing a light jacket. "
                else (if (eq ?category "rainy")
                        then "Rainy weather - best to stay indoors or bring an umbrella if going out. "
                        else (if (eq ?category "stormy")
                                then "Severe weather conditions - stay indoors and follow local safety guidelines. "
                                else (if (eq ?category "snowy")
                                        then "Snowy weather - dress warmly and be cautious of icy conditions. "
                                        else (if (eq ?category "hazardous")
                                                then "Hazardous conditions - stay indoors and follow local health advisories. "
                                                else "Check local weather updates for specific conditions. ")))))))

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

(deffunction get-temp-impact (?temp)
    (if (< ?temp 10)
        then -2
        else (if (< ?temp 20)
                then 2
                else (if (< ?temp 30)
                        then 1
                        else -1))))

(deffunction get-temp-category (?temp)
    (if (< ?temp 10)
        then "cold"
        else (if (< ?temp 20)
                then "mild"
                else (if (< ?temp 30)
                        then "warm"
                        else "hot"))))

(deffunction get-humidity-impact (?humid)
    (if (< ?humid 30)
        then 1
        else (if (< ?humid 60)
                then 2
                else -1)))


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
    ?user <- (user-id (userId ?userId))
    ?w <- (weather-input (condition ?wcond))
    ?t <- (temperature-input (value ?temp))
    ?h <- (humidity-input (level ?humid))
    =>
    ; Map the weather condition to category first
    (bind ?weather-category (map-weather-condition ?wcond))
    
    ; Calculate impacts
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
            then "Low humidity - consider using a humidifier. "
            else (if (< ?humid 60)
                    then "Humidity is at a comfortable level. "
                    else "High humidity - stay hydrated and avoid strenuous activities. ")))
    
    ; Create full message including score and category
    (bind ?full-message (str-cat "Weather Score: " ?normalized-score "/100. "
                                "Weather Category: " ?weather-category ". "
                                ?weather-part ?temp-part ?humidity-part))
    
    ; Assert recommendation
    (assert (weather-recommendation
        (user_id ?userId)
        (message ?full-message)))
    
    ; Print results for debugging
    (printout t crlf "Weather Impact Analysis:" crlf)
    (printout t "User ID: " ?userId crlf)
    (printout t "Weather Category: " ?weather-category crlf)
    (printout t "Temperature Category: " ?temp-cat crlf)
    (printout t "Weather Score: " ?normalized-score "/100" crlf)
    (printout t "Recommendation: " ?full-message crlf))