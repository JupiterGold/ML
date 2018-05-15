
import java.io.FileWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.javacraft.astro.PlanetConstants;
import com.javacraft.core.dbutil.BaseDBManager;
import com.javacraft.utils.GlobalUtils;

public class PrepareData implements Serializable, PlanetConstants {
	static String sPlanetCombo[][] = {// { "Su_pos", "Mo_pos" }, { "Lg_pos", "Mo_pos" }, { "Lg_pos", "Su_Pos" }, 
			{ "su_pos", "st_su" }
			,{ "mo_pos", "st_mo" }
			};
	

//	static String sPlanetCombo[][] = { { "Su_pos", "Mo_pos" }, { "Lg_pos", "Mo_pos" }, { "Lg_pos", "Su_Pos" } };
	BaseDBManager db = null;

	public PrepareData() throws Exception {
		String dbProps = "AstroPlanetDB.properties";
		db = BaseDBManager.getInstance(dbProps);
		if (db == null) {
			db = new BaseDBManager(new DBConnector(dbProps));
			BaseDBManager.addDBManager(dbProps, db);
		}
	}

	public void process() throws Exception {
		Connection oConnection = db.getConnection();
		String s = GlobalUtils.getFileContents("/Users/walter/Projects/AstroPlanet/src/walter/ml/ViewTemplate.sql");
		Map oM = new HashMap();
		for (int c = 0; c < sPlanetCombo.length; c++) {
			oM.put("P1", sPlanetCombo[c][0]);
			oM.put("P2", sPlanetCombo[c][1]);
			for (int i = 1; i <= 49; i++) {
				oM.put("AA", "" + i);
				String sSQL = GlobalUtils.processFindAndReplace(s, oM);
				System.out.println(sSQL);
				try {
					db.updateSQL(sSQL, oConnection);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		oConnection.close();
	}

	public void createARFF() throws Exception {
		Connection oConnection = db.getConnection();
		String s = "select drawon, @P1@, @P2@, N from V@P1@@P2@_@AA@";
//		String sTemplate = "@RELATION " + sPlanetCombo + "-@AA@\n";
//		sTemplate += "\n@ATTRIBUTE drawon string";
//		sTemplate += "\n@ATTRIBUTE @P1@ real";
//		sTemplate += "\n@ATTRIBUTE @P2@ real";
//		sTemplate += "\n@ATTRIBUTE class {0,@AA@}";
//		sTemplate += "\n\n@DATA\n";
		Map oM = new HashMap();
		for (int c = 0; c < sPlanetCombo.length; c++) {
			oM.put("P1", sPlanetCombo[c][0]);
			oM.put("P2", sPlanetCombo[c][1]);
			String sTemplate = "@RELATION " + sPlanetCombo[c][0] + sPlanetCombo[c][1] + "-@AA@\n";
			sTemplate += "\n@ATTRIBUTE drawon string";
			sTemplate += "\n@ATTRIBUTE @P1@ real";
			sTemplate += "\n@ATTRIBUTE @P2@ real";
			sTemplate += "\n@ATTRIBUTE class {0,@AA@}";
			sTemplate += "\n\n@DATA\n";
			for (int i = 1; i <= 49; i++) {
				oM.put("AA", "" + i);
				String sSQL = GlobalUtils.processFindAndReplace(s, oM);
				try {
					db.writeSQLResultToFile(sSQL, "/tmp/0.txt");
					FileWriter oFW = new FileWriter(
							"/tmp/" + sPlanetCombo[c][0] + sPlanetCombo[c][1] + "-" + i + ".arff");
					oFW.write(GlobalUtils.processFindAndReplace(sTemplate, oM));
					oFW.write(GlobalUtils.getFileContents("/tmp/0.txt").replaceAll("\"", ""));
					oFW.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		oConnection.close();
	}

	public void createARFF2Predict() throws Exception {
		Connection oConnection = db.getConnection();
		String sSQL = "select eventon, @P1@, @P2@, '?' from PlanetPos where eventon > (select max(drawon) from SGToto)";
		Map oM = new HashMap();
		for (int c = 0; c < sPlanetCombo.length; c++) {
			oM.put("P1", sPlanetCombo[c][0]);
			oM.put("P2", sPlanetCombo[c][1]);
			String sTemplate = "@RELATION " + sPlanetCombo[c][0] + sPlanetCombo[c][1] + "-@AA@\n";
			sTemplate += "\n@ATTRIBUTE drawon string";
			sTemplate += "\n@ATTRIBUTE @P1@ real";
			sTemplate += "\n@ATTRIBUTE @P2@ real";
			sTemplate += "\n@ATTRIBUTE class {0,@AA@}";
			sTemplate += "\n\n@DATA\n";
			for (int i = 1; i <= 49; i++) {
				oM.put("AA", "" + i);
				try {
					db.writeSQLResultToFile(GlobalUtils.processFindAndReplace(sSQL, oM), "/tmp/0.txt");
					FileWriter oFW = new FileWriter(
							"/tmp/" + sPlanetCombo[c][0] + sPlanetCombo[c][1] + "-" + i + "-p.arff");
					oFW.write(GlobalUtils.processFindAndReplace(sTemplate, oM));
					oFW.write(GlobalUtils.getFileContents("/tmp/0.txt").replaceAll("\"", ""));
					oFW.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		oConnection.close();
	}

	public static void main(String args[]) throws Exception {
		// new PrepareData ( ).process ( ) ;
		PrepareData oPD = new PrepareData();
		oPD.process();
		oPD.createARFF();
		oPD.createARFF2Predict();
	}
}
