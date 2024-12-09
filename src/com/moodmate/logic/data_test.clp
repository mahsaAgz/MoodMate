(assert (user-input (username "john") (password "pass123")))
(assert (profile-input (user_id 1) (name "Alice") (gender 1)
                       (age 25) (mbti "unknown") (hobbies "art relax social collection")
                       (notification-frequency 2)))


(assert (trigger-status (user_id 1) (has-trigger true)))
(assert (rses-level (user_id 1) (level "low")))
