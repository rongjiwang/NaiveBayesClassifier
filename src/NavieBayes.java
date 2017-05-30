import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavieBayes {

	public static void main(String[] args) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(args[0]));

		// zero means not-there, one means there
		// 0 stands for nonspam, 1 stands for spam
		int[] zero_nonspam = new int[12]; // 0--0
		int[] zero_spam = new int[12]; // 0--1
		int[] one_nonspam = new int[12]; // 1--0
		int[] one_spam = new int[12]; // 1--1

		// classifier count
		int instanceTotal = 0;
		int spamTotal = 0;
		int nonspamTotal = 0;
		// -----------------Training file parser--------------------
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] strArray = line.trim().split("\\s+");
				boolean is_spam = (strArray[strArray.length - 1].equals("1")) ? true : false;
				int x = (is_spam) ? spamTotal++ : nonspamTotal++;
				// System.out.println(is_spam);
				for (int i = 0; i < strArray.length - 1; i++) {
					// split feature 0 and 1 when it's spam
					if (is_spam) {
						if (strArray[i].equals("0")) {
							zero_spam[i] = zero_spam[i] + 1;
						} else if (strArray[i].equals("1")) {
							one_spam[i] = one_spam[i] + 1;
						} else {
							System.err.println("[Error] at spliting spam features.");
						}
					}
					// split feature 0 and 1 when it's non-spam
					else if (!is_spam) {
						if (strArray[i].equals("0")) {
							zero_nonspam[i] = zero_nonspam[i] + 1;
						} else if (strArray[i].equals("1")) {
							one_nonspam[i] = one_nonspam[i] + 1;
						} else {
							System.err.println("[Error] at spliting non-spam features.");
						}
					} else {
						System.err.println("[Error] at reading training classifier");
					}
				}
				instanceTotal++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ------------Display Data collection---------------
		System.out.println(
				"spamTotal: " + spamTotal + ", nonspamTotal: " + nonspamTotal + ", instanceTotal: " + instanceTotal);
		System.out.println("[F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12]");
		System.out.println(Arrays.toString(zero_nonspam) + ">>Feature 0 counting within non-spam.");
		System.out.println(Arrays.toString(one_nonspam) + ">>Feature 1 counting within non-spam.");
		System.out.println(Arrays.toString(zero_spam) + ">>Feature 0 counting within spam.");
		System.out.println(Arrays.toString(one_spam) + ">>Feature 1 counting within spam.");

		// ------------Dealing with zero counts---------------
		int zero_count = 0;
		// search any feature value is 0
		int y = (Arrays.stream(zero_nonspam).anyMatch(s -> s == 0)) ? zero_count++ : 0;
		y = (Arrays.stream(one_nonspam).anyMatch(s -> s == 0)) ? zero_count++ : 0;
		y = (Arrays.stream(zero_spam).anyMatch(s -> s == 0)) ? zero_count++ : 0;
		y = (Arrays.stream(one_spam).anyMatch(s -> s == 0)) ? zero_count++ : 0;
		// if found any feature value is 0
		if (zero_count > 0) {
			instanceTotal += 2;
			// increment all value by 1
			spamTotal++;
			nonspamTotal++;
			for (int i = 0; i < zero_nonspam.length; i++) {
				zero_nonspam[i] = ++zero_nonspam[i];
				one_nonspam[i] = ++one_nonspam[i];
				zero_spam[i] = ++zero_spam[i];
				one_spam[i] = ++one_spam[i];

			}
			System.out.println("--------------------After Zero counts happen---------------------------");
			System.out.println("spamTotal: " + spamTotal + ", nonspamTotal: " + nonspamTotal + ", instanceTotal: "
					+ instanceTotal);
			System.out.println("[F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12]");
			System.out.println(Arrays.toString(zero_nonspam) + ">>Feature 0 counting within non-spam.");
			System.out.println(Arrays.toString(one_nonspam) + ">>Feature 1 counting within non-spam.");
			System.out.println(Arrays.toString(zero_spam) + ">>Feature 0 counting within spam.");
			System.out.println(Arrays.toString(one_spam) + ">>Feature 1 counting within spam.");
		}

		// --------------Read Test instances file-----------------------------------
		br = new BufferedReader(new FileReader(args[1]));
		line = null;
		ArrayList<Double> spam_score = new ArrayList<>();
		ArrayList<Double> nonspam_score = new ArrayList<>();
		try {
			while ((line = br.readLine()) != null) {
				
				String[] strArray = line.trim().split("\\s+");
				//boolean is_spam = (strArray[strArray.length - 1].equals("1")) ? true : false;
				//System.out.println(Arrays.toString(strArray));
				
				// denominator of the equation
				double denominator = 1;
				// numerator of the equation
				double numerator_spam = ((double)spamTotal/(double)instanceTotal);
				double numerator_nonspam = ((double)nonspamTotal/(double)instanceTotal);
				//features calculation
				for (int i = 0; i < strArray.length; i++) {
						//spam score
						if (strArray[i].equals("0")) {
							numerator_spam*= ((double)zero_spam[i]/(double)spamTotal);
							denominator*= ((double)(zero_spam[i]+zero_nonspam[i])/(double)instanceTotal);
							//System.out.println(numerator+" "+denominator);
						} else if (strArray[i].equals("1")) {
							numerator_spam*= ((double)one_spam[i]/(double)spamTotal);
							denominator*= ((double)(one_spam[i]+one_nonspam[i])/(double)instanceTotal);
						} else {
							System.err.println("[Error] at spliting testing spam features.");
						}
						//nonspam score
						if (strArray[i].equals("0")) {
							numerator_nonspam*= ((double)zero_nonspam[i]/(double)nonspamTotal);
							//System.out.println( ((double)zero_nonspam[i]/(double)nonspamTotal)+"++");
							denominator*= ((double)(zero_nonspam[i]+zero_spam[i])/(double)instanceTotal);
							//System.out.println( ((double)(zero_nonspam[i]+zero_spam[i])/(double)instanceTotal)+"++");
						} else if (strArray[i].equals("1")) {
							numerator_nonspam*= ((double)one_nonspam[i]/(double)nonspamTotal);
							//System.out.println( (((double)one_nonspam[i]/(double)nonspamTotal)+"--"));
							denominator*= ((double)(one_nonspam[i]+one_spam[i])/(double)instanceTotal);
							//System.out.println( ((double)(one_nonspam[i]+one_spam[i])/(double)instanceTotal)+"--");
						} else {
							System.err.println("[Error] at spliting testing non-spam features.");
						}
									
				}
				//classifier output-(Note. this is the proper solution for Naive Bayes)
				//since we don't use it in our assignment(*Ignore*)
				double ans_spam = numerator_spam/denominator;
				double ans_nonspam = numerator_nonspam/denominator;	
				//store calculated scores
				spam_score.add(numerator_spam);
				nonspam_score.add(numerator_nonspam);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// --------------Display results----------------------------
		String prob_0_spam="",prob_1_spam="",prob_0_nonspam="",prob_1_nonspam="";
		for(int i=0; i<zero_nonspam.length; i++){
			prob_0_spam+="F#"+(i+1)+":["+((double)zero_spam[i]/(double)spamTotal)+"] ";
			prob_1_spam+="F#"+(i+1)+":["+((double)one_spam[i]/(double)spamTotal)+"] ";
			prob_0_nonspam+="F#"+(i+1)+":["+((double)zero_nonspam[i]/(double)nonspamTotal)+"] ";
			prob_1_nonspam+="F#"+(i+1)+":["+((double)one_nonspam[i]/(double)nonspamTotal)+"] ";
			if(i==5){
				prob_0_spam+="\n";
				prob_1_spam+="\n";
				prob_0_nonspam+="\n";
				prob_1_nonspam+="\n";
			}
		}
		System.out.println("\n----------Probabilities of features(0-non_spam)------------\n"+prob_0_nonspam+"\n");
		System.out.println("----------Probabilities of features(1-non_spam)------------\n"+prob_1_nonspam+"\n");
		System.out.println("----------Probabilities of features(0-spam)------------\n"+prob_0_spam+"\n");
		System.out.println("----------Probabilities of features(1-spam)------------\n"+prob_1_spam);

		System.out.println("\n--------------Testing cases starts---------------");
		System.out.println(">>>>>>>>>>>>>>>>>>>>Spam Prediction<<<<<<<<<<<<<<<<<<<");
		for(int i=0; i<spam_score.size();i++){
			System.out.print("["+spam_score.get(i)+"] ");
			if(i==4)System.out.println();
		}
		System.out.println();
		System.out.println(">>>>>>>>>>>>>>>>>>>>Non-Spam Prediction<<<<<<<<<<<<<<<");
		for(int i=0; i<nonspam_score.size();i++){
			System.out.print("["+nonspam_score.get(i)+"] ");
			if(i==4)System.out.println();
		}
		//Final classifiers
		String output = "\n\n>>>>>>>>>Final Classifier Prediction<<<<<<<<\n";
		for(int i=0; i<spam_score.size();i++){
			if(spam_score.get(i)>nonspam_score.get(i)){
				output+="1 ";
			}
			else{
				output+="0 ";
			}
		}
		System.out.println(output);

	}

}
