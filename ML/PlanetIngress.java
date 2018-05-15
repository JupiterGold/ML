import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.javacraft.astro.ChartInformation;
import com.javacraft.astro.DegTable;
import com.javacraft.astro.HousePositions;
import com.javacraft.astro.PLANETDETAILS;
import com.javacraft.astro.PlanetConstants;
import com.javacraft.astro.PlanetaryPositions;
import com.javacraft.astro.Significators;
import com.javacraft.astro.Sub2193;
import com.javacraft.core.dbutil.BaseDBManager;
import com.javacraft.utils.DateTime;
import com.javacraft.utils.TimeZoneCalendar;

import lottery.logic.HistoryFile;

public class PlanetIngress implements Serializable, PlanetConstants
{
  public void process ( TimeZoneCalendar oTZCalendar ) throws Exception
  {
    boolean bQuit = false ;
    String dbProps = "AstroPlanetDB.properties" ;
    BaseDBManager db = BaseDBManager.getInstance ( dbProps ) ;
    if ( db == null )
    {
      db = new BaseDBManager ( new DBConnector ( dbProps ) ) ;
      BaseDBManager.addDBManager ( dbProps, db ) ;
    }
    // db.updateSQL ( "DELETE FROM APP.EVENTS" ) ;

    Calendar oCalendar = Calendar.getInstance ( ) ;
    oCalendar.set ( Calendar.YEAR, 2004 ) ;
    oCalendar.set ( Calendar.HOUR_OF_DAY, 18 ) ;
    oCalendar.set ( Calendar.MINUTE, 30 ) ;
    oCalendar.set ( Calendar.SECOND, 0 ) ;
/*
    oCalendar.set ( Calendar.YEAR, 2007 ) ;
    oCalendar.set ( Calendar.MONTH, 2 ) ;
    oCalendar.set ( Calendar.DATE, 1 ) ;
    oCalendar.set ( Calendar.HOUR, 21 ) ;
    oCalendar.set ( Calendar.MINUTE, 30 ) ;
    oCalendar.set ( Calendar.SECOND, 0 ) ;
*/
    // Date oDate = TimeZoneCalendar.getCorrectCalendar (
    // oTZCalendar.getCalendar ( ) ).getTime ( ) ;
    Date oDate = oCalendar.getTime ( ) ;

    Significators oSF, oPSF = null ;

    PLANETDETAILS oOld = null ;
    Calendar oLastDate = Utility.getLastDate ( ) ;
    Connection oConnection = db.getConnection() ;
//    db.updateSQL ( "delete from Events", oConnection ) ;
    db.updateSQL ( "delete from PlanetPos", oConnection ) ;

    System.out.println ( "MP,Ms,Mt,Ls,Lt,Ss,St,N" ) ;//MP = Moon Phase
//  System.out.println ( "Date,Moon,N" ) ;
    String sPhases [ ] = { "0", "1", "2", "3", "4", "-1", "-2", "-3" } ;

    boolean bTrue = true ;
    Map oMap = HistoryFile.read ("/Users/walter/Projects/AstroPlanet/TotoHistory.txt" ) ;
    Iterator oI = oMap.keySet ( ).iterator ( ) ;
      String sDate = ( String ) oI.next ( ) ;
      oCalendar = DateTime.getCalendar(sDate) ;
      oCalendar.set ( Calendar.HOUR_OF_DAY, 18 ) ;
      oCalendar.set ( Calendar.MINUTE, 30 ) ;
      oCalendar.set ( Calendar.SECOND, 0 ) ;
      oDate = oCalendar.getTime() ;
    while ( bTrue )
    {
       if ( oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY && oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
       {
    	     if ( oI.hasNext() )
    	     {
    	    	   sDate = ( String ) oI.next() ;
//    	    	   oSTE = new StringTokenizer(sDate, ',') ;
    	    	   oCalendar = DateTime.getCalendar(sDate) ;
    	     }
    	     else
    	     {
    	    	   if ( oCalendar.after ( oLastDate ) ) break ;
    	    	   else oCalendar.add ( Calendar.DATE, 1 ) ;
    	     }
    	     oCalendar.set ( Calendar.HOUR_OF_DAY, 18 ) ;
    	     oCalendar.set ( Calendar.MINUTE, 30 ) ;
    	     oCalendar.set ( Calendar.SECOND, 0 ) ;
    	     oDate = oCalendar.getTime ( ) ;
    	     continue ;
       }
       System.out.println ( " Date: " + oDate ) ;

//      bQuit = ( oCalendar.get ( Calendar.YEAR ) == 2008 ) ;
      // System.out.println ( "Date: " + oTZCalendar ) ;
      // System.out.println ( "Calculating Planet Position using Correct Time: "
      // + TimeZoneCalendar.getCorrectCalendar ( oCalendar ).getTime ( ) ) ;
      ChartInformation oCI = new ChartInformation ( oDate, StaticInfo.getPlaceData() ) ;
      double b6 = oCI.getB6 ( ) ;
      PlanetaryPositions oPP = new PlanetaryPositions ( b6, oCI.getYY ( ), spd = new PLANETDETAILS ( ) ) ;
      oPP.adjustAyanamsa ( getAyanamsa ( ) ) ;
      spd.PlanetPositions [ 0 ] = oPP.adjustAyanamsa ( getAyanamsa ( ), oCI.lagna ( ) ) ;
      HousePositions oHP = new HousePositions ( oCI, spd, KP_CUSPS, 0 ) ;
      for ( int n = 0 ; n < MAX_CUSPS * 2 ; n++ )
        spd.CuspalPositions [ n ] = oPP.adjustAyanamsa ( StaticInfo.getAyanamsa ( ), spd.CuspalPositions [ n ] ) ;
      oSF = new Significators ( spd ) ;
      oSF.calculateSignStarSub ( ) ;
      sDate = DateTime.get ( oDate, "yyyyMMdd" ) ;
      String sSQL ;
      String sEventTime = DateTime.get ( oDate, "HHmm" ) ;
/*      
      for ( int c = 1 ;  c < 13 ; c++ )
      {
    	  if (  c != VENUS )
    		  continue ;
//    	  if ( c == RAHU || c == KETHU || c == URANUS || c == PLUTO || c == NEPTUNE || c == SATURN || c == JUPITER || c == VENUS || c == MARS  )
//    		  continue ;
          sSQL = "INSERT INTO EVENTS ( EventOn, EventTime, Event ) VALUES ( " ;
          sSQL += sDate + "," + sEventTime + ", '" + spd.PlanetSignLord [ c ] + '-' + spd.PlanetStarLord [ c ] + '-' + spd.PlanetSubLord [ c ] + "'" ;
          sSQL += ")" ;
          db.updateSQL ( sSQL, oConnection ) ;
      }
*/      
      int iMoonPhase = 0 ; //org.apache.tools.ant.util.DateUtils.getPhaseOfMoon ( oCalendar ) ;
      sSQL = "INSERT INTO PlanetPos ( EventOn, su_pos, mo_pos, lg_pos, sg_su, st_su, sb_su, ss_su, sg_mo, st_mo, sb_mo, ss_mo, sg_lg, st_lg, sb_lg, ss_lg, phase_mo ) "
      		+ "VALUES ( ";
      sSQL += sDate + "," + spd.PlanetPositions[SUN] + ","+ spd.PlanetPositions[MOON] + "," + spd.CuspalPositions[LAGNA] ;
      sSQL += "," + spd.PlanetSignLord[SUN] + "," + spd.PlanetStarLord[SUN] + "," + spd.PlanetSubLord[SUN] + "," + spd.PlanetSubSubLord[SUN] ;
      sSQL += "," + spd.PlanetSignLord[MOON] + "," + spd.PlanetStarLord[MOON] + "," + spd.PlanetSubLord[MOON] + "," + spd.PlanetSubSubLord[MOON] ;
      sSQL += "," + spd.CuspSignLord[LAGNA] + "," + spd.CuspStarLord[LAGNA] + "," + spd.CuspSubLord[LAGNA] + "," + spd.CuspSubSubLord[LAGNA] ;
      sSQL += ", " + iMoonPhase ;
      
      sSQL += ")" ;
      db.updateSQL ( sSQL, oConnection ) ;
      
/*
      int iAsp[] = { MOON, SUN, MARS, MERCURY, VENUS, JUPITER, SATURN, RAHU, KETHU } ;
//      int iAsp[] = { MOON, SUN, MARS, MERCURY, VENUS } ;
      for ( int c = 0 ; c < iAsp.length ; c++ )
        for ( int i = 1 ; i < 13 ; i++ )
        {
        	int z = 0 ;
        	for ( ; z < iAsp.length ; z++ )
        	{
        	  if ( i == iAsp [ z ] )
        		  break ;
        	}
//        	if ( z != iAsp.length )
//        		continue ;

//          if ( i == KETHU || i == RAHU )
//        	  continue ;
          if ( i == iAsp [ c ] )
            continue ;
          if ( ( i == RAHU && iAsp [ c ] == KETHU ) || ( i == KETHU && iAsp [ c ] == RAHU ) )
            continue ;
//          if ( i == URANUS || i == NEPTUNE || i == PLUTO )
//        	continue ;
          String s = FindAspect.getAspectString ( spd.PlanetPositions [ iAsp [ c ] ], spd.PlanetPositions [ i ] ) ;
          if ( s == null )
          {
        	int diff = ( int ) ( spd.PlanetPositions [ iAsp [ c ] ] - spd.PlanetPositions [ i ] ) ;
        	if ( diff < 0 )  // Duplicate => Since the positive aspect will be caught anyways
        	  continue ;
        	if ( diff % 3 == 0 )
        	  s = "" + diff ;
        	else
              continue ;
          }

    //      System.out.println ( s ) ;
          String sAspect = planetShortNames [ iAsp [ c ] ] + "-" + planetShortNames [ i ] + "-" + s ;
          sSQL = "INSERT INTO EVENTS ( EventOn, EventTime, Event ) VALUES ( " ;
          sSQL += sDate + "," + sEventTime + ", '" + sAspect + "'" ;
          sSQL += ")" ;
          db.updateSQL ( sSQL, oConnection ) ;
        }
*/      
      
/*
      for ( int i = 1 ; i < 13 ; i++ )
      {
        String s = FindAspect.getAspectString ( spd.CuspalPositions [ LAGNA ], spd.PlanetPositions [ i ] ) ;
        if ( s == null )
          continue ;
//        System.out.println ( s ) ;
        
        String sAspect = "Asc" + "-" + planetShortNames [ i ] + "-" + s ;
        sSQL = "INSERT INTO EVENTS ( EventOn, EventTime, Event ) VALUES ( " ;
        sSQL += sDate + "," + sEventTime + ", '" + sAspect + "'" ;
        sSQL += ")" ;
        db.updateSQL ( sSQL, oConnection ) ;
      }
      for ( int i = 1 ; i < 13 ; i++ )
      {
        String s = FindAspect.getAspectString ( spd.CuspalPositions [ LAGNA ], spd.PlanetPositions [ i ] ) ;
        if ( s == null )
          continue ;
//        System.out.println ( s ) ;
        
        String sAspect = "Asc-" + "-" + planetShortNames [ i ] + "-" + s + "-" + oSF.getHouseNumber ( i ) ;
        sSQL = "INSERT INTO EVENTS ( EventOn, EventTime, Event ) VALUES ( " ;
        sSQL += sDate + "," + sEventTime + ", '" + sAspect + "'" ;
        sSQL += ")" ;
        db.updateSQL ( sSQL, oConnection ) ;
      }
*/      
      oOld = spd ;

      if ( oI.hasNext() )
      {
        sDate = ( String ) oI.next ( ) ;
        oCalendar = DateTime.getCalendar(sDate) ;
      }
      else
      {
        if ( oCalendar.after ( oLastDate ) )
        	break ;
        else
          oCalendar.add ( Calendar.DATE, 1 ) ;
      }
      oCalendar.set ( Calendar.HOUR_OF_DAY, 18 ) ;
      oCalendar.set ( Calendar.MINUTE, 30 ) ;
      oCalendar.set ( Calendar.SECOND, 0 ) ;
//      if ( oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || 
//    		  oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )
//      {
//        oCalendar.set ( Calendar.HOUR_OF_DAY, 21 ) ;
//        oCalendar.set ( Calendar.MINUTE, 00 ) ;
//      }
      oDate = oCalendar.getTime ( ) ; // NY Time
      // Get the Correct Time
      // oDate = TimeZoneCalendar.getCorrectCalendar ( oTZCalendar.getCalendar (
      // ) ).getTime ( ) ;
//      break ;
      oConnection.close ( ) ;
      oConnection = db.getConnection() ;
    }
    oConnection.close ( ) ;
  }

  public static void main ( String args[] ) throws Exception
  {
    TimeZoneCalendar oTZC = new TimeZoneCalendar ( Calendar.getInstance ( ).getTime ( ), TimeZone.getTimeZone ( "America/New_York" ) ) ;
    new PlanetIngress ( ).process ( oTZC ) ;
  }

  PLANETDETAILS spd = null ;

  static Sub2193 oS = new Sub2193 ( ) ;

  public DegTable hasChanged ( double dOld, double dNew )
  {
    DegTable oDT = oS.getSub2193 ( dNew ) ;
    if ( oDT.isSame ( oS.getSub2193 ( dOld ), DegTable.SUB ) == false )
      return oDT ;
    return null ;
  }

  public int getAyanamsa ( )
  {
    return PlanetConstants.KP_AYANAMSA ;
  }

  public TimeZone getTimeZone ( )
  {
    return TimeZone.getTimeZone ( "EST" ) ;
  }

}
