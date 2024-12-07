package com.moodmate.logic;

import jess.JessException;
import jess.Rete;
import jess.*;

public class Main {
	public static void main(String[] args) {
		String file1 = "src/com/moodmate/logic/templates.clp";
		String file2 = "src/com/moodmate/logic/data.clp";
		String file3 ="src/com/moodmate/logic/sleep_disorders_rules.clp";
//		String file4 ="src/com/moodmate/logic/EFT_daily_rules.clp";
//		String file4 ="src/com/moodmate/logic/pattern_recognize_rules.clp";
//		String file5 ="src/com/moodmate/logic/eating_disorder_rules.clp";


		
		try {
			Rete r = new Rete();
			
			r.eval("(watch facts)");
//			r.eval("(watch rules)");
			r.eval("(reset)");
			
			r.eval("(batch " + file1 + ")");
			r.eval("(batch " + file2 + ")");
			r.eval("(batch " + file3 + ")");
//			r.eval("(batch " + file4 + ")");
//			r.eval("(batch " + file5 + ")");
			
//			r.eval("(facts)");
			r.eval("( run)");
//			r.eval("(facts)");
		
			
			
			
		}
		catch (JessException j) {
			j.printStackTrace();
		}
	}


}

// for sleep time conversion
//class TimeHelper {
//    public static double getDecimalHours(String time) {
//        String[] parts = time.split(":");
//        int hours = Integer.parseInt(parts[0]);
//        int minutes = Integer.parseInt(parts[1]);
//        return hours + (minutes / 60.0);  // returns like 23.5 for 23:30
//    }
//}