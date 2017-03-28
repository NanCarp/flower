package com.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.dao.WLDao;
import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.util.DeliveryDateUtil;

/**
 * @Desc 物流测试
 * @author lxx
 * @date 2016/10/08
 * */
public class WuLiuCtrl extends Controller {
	public static String testurl = "http://query.szdod.com/D2DReceiveOrder.aspx";
	public static String Key = "870110";
	public static String custCode = "C108";
	
	public static String Rtn_Code = "2";
	public static String Rtn_Msg = "失败";
	public static Map<?, ?> jsonPara;
	
	// 挑单-将物流信息同步至门对门
	public void synchro_toD2D(){
		Map<String, Object> chooseMap = DeliveryDateUtil.chooseDate();
		String msg = new String();
		if((boolean) chooseMap.get("result")){
			String dateCode = (String) chooseMap.get("datecode");
			// 物流状态(0异常,1正常,2发货,9确认收货)
			// 本批次的物流数量
			Number wlNum = Db.queryNumber("select count(1) from f_order_info where code=?", dateCode);
			// 异常物流数量
			Number ycNum = Db.queryNumber("select count(1) from f_order_info where code=? and state=0", dateCode);
			// 已发货的物流数量
			Number yfNum = Db.queryNumber("select count(1) from f_order_info where code=? and state=2", dateCode);
			// 挑单记录
			Number tdNum = Db.queryNumber("select count(1) from f_tiaodan where code=?", dateCode);
			if(wlNum.intValue() > 0){
				if(ycNum.intValue() == 0){
					if(yfNum.intValue() == 0){
						if(tdNum.intValue() == 0){
							if(WLDao.synchro_toD2D(dateCode)){
								msg = "挑单成功";
							}else{
								msg = "挑单同步过程出错,请重试";
							}
						}else{
							msg = "批次"+dateCode+"挑单已完成,无法重复挑单";
						}
					}else{
						msg = "批次"+dateCode+"已经发货,无法重复挑单";
					}
				}else{
					msg = "批次"+dateCode+"存在异常物流信息,挑单失败";
				}
			}else{
				msg = "批次"+dateCode+"尚未配单,无法挑单";
			}
		}else{
			msg = "无单可挑！";
		}
		renderJson("{\"msg\":\""+msg+"\"}");
	}
	
	// 接收门对门物流信息完善回调
	public void perfect_ajax(){
		String jsonStr = HttpKit.readData(getRequest());	
		Gson gson = new Gson();
		Map<?, ?> jsonPara = gson.fromJson(jsonStr, HashMap.class);
		Map<String, Object> jsonMap = new HashMap<>();
		String Rtn_Code = "2";
		String Rtn_Msg = "失败";
		try{
			String WorkCode = (String) jsonPara.get("WorkCode");		// 条码号
			String eCode = (String) jsonPara.get("eCode");				// 物流公司编码
			String WorkArea = (String) jsonPara.get("WorkArea");		// 仓储区域
			String WorkStr = (String) jsonPara.get("WorkNumber");		// 仓储编码
			Integer WorkNumber = null;
			try{
				WorkNumber = Integer.parseInt(WorkStr);	// 仓储编码
			}catch(NumberFormatException e){
				
			}
			Record orderinfo = Db.findFirst("select id from f_order_info where number = ?", WorkCode);
			if(orderinfo != null){
				Record express = Db.findFirst("select name from f_express where code=?", eCode);	// 获取物流平台名称
				if(express != null){
					orderinfo.set("ename", express.getStr("name"));
					orderinfo.set("ecode", eCode);
					orderinfo.set("workarea", WorkArea);		// 仓储区域
					if(WorkNumber != null){
						orderinfo.set("worknumber", WorkNumber);	// 仓储编码
					}
					if(Db.update("f_order_info", orderinfo)){
						Rtn_Code = "1";
						Rtn_Msg = "成功";
					}
				}
			}
		}catch(NullPointerException e){}
		jsonMap.put("Rtn_Code", Rtn_Code);
		jsonMap.put("Rtn_Msg", Rtn_Msg);
		renderJson("[" + JsonKit.toJson(jsonMap) + "]");
	}
	
	// 接收门对门推送的订单轨迹接口
	public void notify_ajax() {
		String jsonStr = HttpKit.readData(getRequest());	
		Gson gson = new Gson();
		jsonPara = gson.fromJson(jsonStr, HashMap.class);
		Map<String, Object> jsonMap = new HashMap<>();
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean R = false;
				try{
					String ClientCode = (String) jsonPara.get("ClientCode");	// 订单号
					String WorkCode = (String) jsonPara.get("WorkCode");		// 条码号
					String OpDateTime = (String) jsonPara.get("OpDateTime");	// 操作时间
					String OrderStatusCode = (String) jsonPara.get("OrderStatusCode");	// 订单轨迹状态代码
					String OpDescription = (String) jsonPara.get("OpDescription");	// 轨迹的具体描述
					Record logistics = new Record();
					logistics.set("ClientCode", ClientCode);
					logistics.set("WorkCode", WorkCode);
					logistics.set("OpDateTime", OpDateTime);
					logistics.set("OrderStatusCode", OrderStatusCode);
					logistics.set("OpDescription", OpDescription);
					logistics.set("ctime", new Date());
					R = Db.save("f_logistics", logistics);
				}catch(NullPointerException e){}
				if(R){
					Rtn_Code = "1";
					Rtn_Msg = "成功";
				}
				return R;
			}
		});
		jsonMap.put("Rtn_Code", Rtn_Code);
		jsonMap.put("Rtn_Msg", Rtn_Msg);
		renderJson("[" + JsonKit.toJson(jsonMap) + "]");
	}
}
