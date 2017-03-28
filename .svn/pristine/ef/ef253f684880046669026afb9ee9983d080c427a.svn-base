package com.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeliveryDateUtil {

	/**
	 * @see 鲜花首次送达时间方法
	 * @author yeqing
	 * @date 2016/08/11
	 * @param int choose 用户选择
	 * @return String
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public static String compare_Date(int choose) throws ParseException {

		int defaultDayStep = 0; // 默认的日期步长
		int wednesdayStep = 3; // 周三
		int fridayStep = 5; // 周五
		int defaultWeekStep = 0; // 默认的星期步长
		int lastWeekStep = 0; // 默认下一次送达步长

		// 定义string类型的数组集合
		//String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

		// 获取当前系统时间
		Date _nowTime = new Date();

		// 日期格式化
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat theFirstDatedf = new SimpleDateFormat("yyyy-MM-dd");

		// 创建日历对象，并初始化
		Calendar calendar = Calendar.getInstance();

		// 获取日期为周几
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 != 0 ? calendar.get(Calendar.DAY_OF_WEEK) - 1 : 0;

		//System.out.println(weekDays[dayOfWeek]);

		// 用户选择周一送
		if (choose == Constant.MondayTime) {

			// 设置截止日期为周五
			defaultDayStep = fridayStep;
			
			// 默认步长为8
			defaultWeekStep = 8;
			
			// 下一次步长为15
			lastWeekStep = 15;

		} else {
			
			// 设置截止日期为周三
			defaultDayStep = wednesdayStep;
			
			// 默认步长为6
			defaultWeekStep = 6;
			
			// 下一次步长为13
			lastWeekStep = 13;
		}
		
		// 计算本周周三日期
		calendar.add(Calendar.DATE, -dayOfWeek + defaultDayStep);

		// 初始化周五日期的时、分、秒为 18：00：00
		calendar.set(calendar.HOUR_OF_DAY, 18);
		calendar.set(calendar.MINUTE, 0);
		calendar.set(calendar.SECOND, 0);

		//System.out.println(df.format(calendar.getTime()));

		// 转换时间格式
		Date d1 = df.parse(df.format(calendar.getTime()));

		// 判定下单时间
		boolean flag = d1.after(_nowTime);

		// 获取日期步长
		int dayStep = flag == true ? defaultWeekStep : lastWeekStep;

		// 判断送达时间
		// if(flag){

		// 计算首次送达日期，并返回
		calendar.setTime(_nowTime);
		calendar.add(calendar.DATE, -dayOfWeek + dayStep);
		
		//获取首次送达日期
		String result = theFirstDatedf.format(calendar.getTime());
		//System.out.println(theFirstDatedf.format(calendar.getTime()));

		// }//else{

		// 计算下下周一的日期
		// calendar.setTime(_nowTime);
		// calendar.add(calendar.DATE, -dayOfWeek+15);
		// System.out.println(theFirstDatedf.format(calendar.getTime()));
		// }

		//
		//System.out.println(dayOfWeek);
		//
		// System.out.println(df.format(_nowTime));

		// 返回首次送达日期
		return result;
	}
	
	/**
	 * @see 获取有效日期方法
	 * @author yeqing
	 * @date 2016/08/11
	 * @param void
	 * @return Map<String,Object> map
	 * @result	Key<effectiveDate> Value<StringDate>
	 * @result	Key<invalidDate> Value<StringDate>
	 */
	public static Map<String,Object> getEffectiveDate(){
		
		//定义返回值变量
		String effectiveDate;
		String invalidDate;
		
		//创建map容器，存储返回值
		Map<String,Object> result = new HashMap<>();
		
		//生效时间,这里为了方便取现在系统的时间
        Date ableTime = new Date();
        
        // 失效时间
        Date disableTime = null;
        
        // 用Calendar类进行日期的计算
        Calendar c = Calendar.getInstance();
        
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        	
            // 设置生效时间
            c.setTime(ableTime);
            
            // 计算失效时间，根据你的计算方法，这里假设两个月后为失效时间
            c.add(Calendar.MONTH, 2);
            
            // 赋值给失效时间
            disableTime = c.getTime();
            
            //给定义的变量赋值
            effectiveDate = sf.format(ableTime);
            invalidDate = sf.format(disableTime);
            
            //添加变量到map容器
            result.put("effectiveDate",effectiveDate);
            result.put("invalidDate",invalidDate);
            
            //输出测试结果
            System.out.println("生效时间：" + sf.format(ableTime) + ",失效时间：" + sf.format(disableTime));
        
        //返回结果集    
		return result;
	}
	
	// 获取产品批次
	public static Map<String, Object> makeCode(){
		Calendar now = Calendar.getInstance();
		//一周第一天是否为星期天
		boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
		//获取周几
		int weekDay = now.get(Calendar.DAY_OF_WEEK);
		//若一周第一天为星期天，则-1
		if(isFirstSunday){
			weekDay = weekDay - 1;
			if(weekDay == 0){
				weekDay = 7;
			}
		}
		Map<String, Object> map = new HashMap<>();
		boolean result = false;
		String datecode = new String();
		// 周一-周五(18:00)，配本周六产品
		// 周五(18:00)-周日，配下周一产品
		switch(weekDay){
			case 1:
				result = true;
				now.add(Calendar.DAY_OF_MONTH, 5);
				break;
			case 2:
				result = true;
				now.add(Calendar.DAY_OF_MONTH, 4);
				break;
			case 3:
				result = true;
				now.add(Calendar.DAY_OF_MONTH, 3);
				break;
			case 4:
				result = true;
				now.add(Calendar.DAY_OF_MONTH, 2);
				break;
			case 5:
				result = true;
				if(now.get(Calendar.HOUR_OF_DAY) < 18){
					now.add(Calendar.DAY_OF_MONTH, 1);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 3);
				}
				break;
			case 6:
				result = true;
				now.add(Calendar.DAY_OF_MONTH, 2);
				break;
			case 7:
				result = true;
				now.add(Calendar.DAY_OF_MONTH, 1);
				break;
		}
		map.put("result", result);
		if(result){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			datecode = sdf.format(now.getTime());
			map.put("datecode", datecode);
		}
		return map;
	}
	
	// 配单日期
	public static Map<String, Object> chooseDate(){
		Calendar now = Calendar.getInstance();
		//一周第一天是否为星期天
		boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
		//获取周几
		int weekDay = now.get(Calendar.DAY_OF_WEEK);
		//若一周第一天为星期天，则-1
		if(isFirstSunday){
			weekDay = weekDay - 1;
			if(weekDay == 0){
				weekDay = 7;
			}
		}
		Map<String, Object> map = new HashMap<>();
		boolean result = false;
		int reach = 1;
		String datecode = new String();
		// 周三(18:00)-周五(18:00)，配本周六
		// 周五(18:00)-周日，配下周一
		switch(weekDay){
			case 3:
				if(now.get(Calendar.HOUR_OF_DAY) >= 18){
					result = true;
					reach = 2;
					now.add(Calendar.DAY_OF_MONTH, 3);
				}
				break;
			case 4:
				result = true;
				reach = 2;
				now.add(Calendar.DAY_OF_MONTH, 2);
				break;
			case 5:
				result = true;
				if(now.get(Calendar.HOUR_OF_DAY) < 18){
					reach = 2;
					now.add(Calendar.DAY_OF_MONTH, 1);
				}else{
					reach = 1;
					now.add(Calendar.DAY_OF_MONTH, 3);
				}
				break;
			case 6:
				result = true;
				reach = 1;
				now.add(Calendar.DAY_OF_MONTH, 2);
				break;
			case 7:
				result = true;
				reach = 1;
				now.add(Calendar.DAY_OF_MONTH, 1);
				break;
			default : break;
		}
		map.put("result", result);
		if(result){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			datecode = sdf.format(now.getTime());
			map.put("reach", reach);
			map.put("datecode", datecode);
		}
		return map;
	}
	
	public static String[] getTimeByCode(String code){
		String[] strArr = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = sdf.parse(code);
			Calendar now = Calendar.getInstance();
			now.setTime(date);
			boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
			//获取周几
			int weekDay = now.get(Calendar.DAY_OF_WEEK);
			//若一周第一天为星期天，则-1
			if(isFirstSunday){
				weekDay = weekDay - 1;
				if(weekDay == 0){
					weekDay = 7;
				}
			}
			now.add(Calendar.HOUR_OF_DAY, 18);
			if(weekDay == 6){
				now.add(Calendar.DAY_OF_MONTH, -3);
			}else{
				now.add(Calendar.DAY_OF_MONTH, -3);
			}
			SimpleDateFormat sdf_x = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			strArr[1] = sdf_x.format(now.getTime());	// 截止日期
			now.add(Calendar.DAY_OF_MONTH, -7);
			strArr[0] = sdf_x.format(now.getTime());	// 开始日期
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strArr;
	}

	// 根据订单创建时间获取首次送达日期
	public static String getDateByOrder(int reach, Date ctime){
		Calendar now = Calendar.getInstance();
		now.setTime(ctime);
		boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
		//获取周几
		int weekDay = now.get(Calendar.DAY_OF_WEEK);
		//若一周第一天为星期天，则-1
		if(isFirstSunday){
			weekDay = weekDay - 1;
			if(weekDay == 0){
				weekDay = 7;
			}
		}
		switch(weekDay){
			case 1:
				if(reach==1){
					now.add(Calendar.DAY_OF_MONTH, 7);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 5);
				}
				break;
			case 2:
				if(reach==1){
					now.add(Calendar.DAY_OF_MONTH, 6);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 4);
				}
				break;
			case 3:
				if(reach==1){
					now.add(Calendar.DAY_OF_MONTH, 5);
				}else{
					if(now.get(Calendar.HOUR_OF_DAY) < 18){
						now.add(Calendar.DAY_OF_MONTH, 3);
					}else{
						now.add(Calendar.DAY_OF_MONTH, 10);
					}
				}
				break;
			case 4:
				if(reach==1){
					now.add(Calendar.DAY_OF_MONTH, 4);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 9);
				}
				break;
			case 5:
				if(reach==1){
					if(now.get(Calendar.HOUR_OF_DAY) < 18){
						now.add(Calendar.DAY_OF_MONTH, 3);
					}else{
						now.add(Calendar.DAY_OF_MONTH, 10);
					}
				}else{
					now.add(Calendar.DAY_OF_MONTH, 8);
				}
				break;
			case 6:
				if(reach==1){
					now.add(Calendar.DAY_OF_MONTH, 9);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 7);
				}
				break;
			case 7:
				if(reach==1){
					now.add(Calendar.DAY_OF_MONTH, 8);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 6);
				}
				break;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(now.getTime());
	}
	
	// 获得月份第一天的日期
	public static String getFirstDayOfMon(String time){
		int year = Integer.parseInt(time.substring(0,4));
		int month = Integer.parseInt(time.substring(5));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(cal.getTime());
	}
	
	// 获得月份最后一天的日期
	public static String getLastDayOfMon(String time){
		int year = Integer.parseInt(time.substring(0,4));
		int month = Integer.parseInt(time.substring(5));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DATE, 1);
		cal.roll(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return sdf.format(cal.getTime());
	}
}