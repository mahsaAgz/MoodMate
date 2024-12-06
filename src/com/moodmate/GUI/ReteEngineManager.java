package com.moodmate.GUI;
import jess.*;


public class ReteEngineManager {

	private static Rete rete;

    private ReteEngineManager() {}

    public static Rete getInstance() {
        if (rete == null) {
            rete = new Rete();
            try {
                // Load your Jess files only once
                rete.batch("src/com/moodmate/logic/templates.clp");
                rete.batch("src/com/moodmate/logic/EFT_rules.clp");
                rete.batch("src/com/moodmate/logic/EFT_daily_rules.clp");
                rete.batch("src/com/moodmate/logic/primary_reason_rules.clp");
                rete.batch("src/com/moodmate/logic/sleep_rules.clp");
                rete.batch("src/com/moodmate/logic/physical_activity_rules.clp");
                rete.batch("src/com/moodmate/logic/food_rules.clp");
                rete.batch("src/com/moodmate/logic/pattern_recognize_rules.clp");
                rete.batch("src/com/moodmate/logic/anxiety_rules.clp");
                rete.batch("src/com/moodmate/logic/bipolar_rules.clp");
                rete.batch("src/com/moodmate/logic/depression_rules.clp");
                rete.batch("src/com/moodmate/logic/eating_disorder_rules.clp");
                rete.batch("src/com/moodmate/logic/suggestion_rules.clp");
                rete.batch("src/com/moodmate/logic/rules_weather.clp");

            } catch (JessException e) {
                e.printStackTrace();
            }
        }
        return rete;
    }

}
