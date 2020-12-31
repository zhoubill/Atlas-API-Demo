package com.cmcc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    	Calendar cal = Calendar.getInstance();
    	//取得系统当前时间的前一个月
//    	cal.set(Calendar.MONTH, -1);
    	//输出上月最后一天日期
//    	System.out.println(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    	
    	cal.add(Calendar.DATE, -2);  
    	System.out.println(sdf.format(cal.getTime()));
    	
    	String ss = "1233";
    	System.out.println(isNum(ss));
    }
    
    public static boolean isNum(String str){
        Pattern pattern = Pattern.compile("^-?[0-9]+");
        if(pattern.matcher(str).matches()){
            //数字
            return true;
         } else {
            //非数字
            return false;
         }
     }
}
