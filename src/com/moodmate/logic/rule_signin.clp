(defrule check-valid-signin
    (sign-in-input (username ?u) (password ?p))
    (not (user-record (username ?u) (password ?p)))  ;; Ensure no matching user-record is found
=>
    (assert (sign-in-result (valid FALSE) (message "Invalid credentials!")))
)

(defrule valid-signin
    (sign-in-input (username ?u) (password ?p))
    (user-record (username ?u) (password ?p))  ;; Match the user-record for valid sign-in
=>
    (assert (sign-in-result (valid TRUE) (message "Sign-in successful!")))
)
(watch rules)
(watch facts)