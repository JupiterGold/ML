package walter.ml;

import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.meta.RandomizableFilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class LPredict {

	static boolean bDebugO = false;
	static String sPlanetCombo[][] = {// { "Su_pos", "Mo_pos" }, { "Lg_pos", "Mo_pos" }, { "Lg_pos", "Su_Pos" }, 
			{ "su_pos", "st_su" }
			,{ "mo_pos", "st_mo" }
			};
	static String sLogic = "SuMo-";
	static String sTrainBaseFile = "/tmp/" + sLogic;
	static String sPredictData = "/tmp/" + sLogic;

	// static String[] options = new String[2];
	//
	// static {
	// options[0] = "-R"; // "range"
	// options[1] = "1"; // first attribute
	// }

	public static Instances oOriginal = null;

	public static Instances getTrainData(int i) throws Exception {

		System.out.println("Training Data: " + sTrainBaseFile + i + ".arff");
		Instances oI = DataSource.read(sTrainBaseFile + i + ".arff");

		Remove removeFilter = new Remove();
		removeFilter.setAttributeIndices("1");
		removeFilter.setInputFormat(oI);
		Instances oINoDate = Filter.useFilter(oI, removeFilter);
		oINoDate.setClassIndex(oINoDate.numAttributes() - 1);

		if (bDebugO)
			System.out.println(oI.instance(0));
		return oINoDate;
	}

	public static Instances getPredictData(int i) throws Exception {
		// prepareData(i);
		Instances oI = DataSource.read(sPredictData + i + "-p.arff");

		Remove removeFilter = new Remove();
		removeFilter.setAttributeIndices("1");
		removeFilter.setInputFormat(oI);

		Instances oINoDate = Filter.useFilter(oI, removeFilter);
		oINoDate.setClassIndex(oINoDate.numAttributes() - 1);

		if (bDebugO)
			System.out.println(oI.instance(0));

		oOriginal = oI;
		return oINoDate;
	}

	public static void printPrediction(Classifier oC, Instances oI, int N, String sPredictor) throws Exception {
		for (int c = 0; c < oI.size(); c++) {
			double p = oC.classifyInstance(oI.instance(c));
			oI.instance(c).setClassValue(p);
			if ((int) p != 0) {
				System.out.println(N + " => " + oOriginal.instance(c) + " Predicted:" + N);
				oFW.write(sPredictor + "," + N + "," + oOriginal.instance(c).toString());
				oFW.write("\n");
			}
		}
	}

	static FileWriter oFW = null;

	public static void main(String[] args) throws Exception {
//		String sPlanetCombo = PrepareData.sPlanetCombo ;
		for (int c = 0; c < sPlanetCombo.length; c++) {
			sLogic = sPlanetCombo[c][0] + sPlanetCombo[c][1] + "-";
			sTrainBaseFile = "/tmp/" + sLogic;
			sPredictData = "/tmp/" + sLogic;

			oFW = new FileWriter("/tmp/Predicted-" + sLogic + ".csv");
			oFW.write(",,,\n") ;
			for (int i = 1; i <= 49; i++) {
				System.out.println("Analyzing N: " + i);
				// model
				IBk oIBk = new IBk();
				oIBk.buildClassifier(getTrainData(i));
				String[] s = oIBk.getOptions();
				if (bDebugO) {
					System.out.print("IBk ");
					for (int d = 0; d < s.length; d++)
						System.out.print(s[d] + ' ');
				}
				printPrediction(oIBk, getPredictData(i), i, "IBk");

				RandomCommittee oRC = new RandomCommittee();
				oRC.buildClassifier(getTrainData(i));
				s = oRC.getOptions();
				if (bDebugO) {
					System.out.print("RC ");
					for (int d = 0; d < s.length; d++)
						System.out.print(s[d] + ' ');
				}
				printPrediction(oRC, getPredictData(i), i, "RC");

				RandomizableFilteredClassifier oRFC = new RandomizableFilteredClassifier();
				oRFC.buildClassifier(getTrainData(i));
				s = oRFC.getOptions();
				if (bDebugO) {
					System.out.print("RFC ");
					for (int d = 0; d < s.length; d++)
						System.out.print(s[d] + ' ');
				}
				printPrediction(oRFC, getPredictData(i), i, "RFC");

				RandomForest oRF = new RandomForest();
				oRFC.buildClassifier(getTrainData(i));
				s = oRFC.getOptions();
				if (bDebugO) {
					System.out.print("RF ");
					for (int d = 0; d < s.length; d++)
						System.out.print(s[d] + ' ');
				}
				printPrediction(oRFC, getPredictData(i), i, "RF");

				RandomTree oRT = new RandomTree();
				oRFC.buildClassifier(getTrainData(i));
				s = oRFC.getOptions();
				if (bDebugO) {
					System.out.print("RT ");
					for (int d = 0; d < s.length; d++)
						System.out.print(s[d] + ' ');
				}
				printPrediction(oRFC, getPredictData(i), i, "RT");

			}
			oFW.close();
		}
	}
}
