package com.dao;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import com.controller.WeixinApiCtrl;
import com.google.gson.Gson;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.util.Constant;
import com.util.DeliveryDateUtil;
import com.util.Constant.seedType;

/**
 * @Desc 后台公共数据接口
 * */
public class MCDao{
	//日期编码
	static String dateCode;
	//返回信息
	static Map<String, Object> responseMap;
	//反馈信息
	static String Msg;
	
	static int Id;
	
	static int typeId;
	
	static String fList;
	
	static String fName;
	
	static String sName;
	
	static Integer iId;
	
	static Integer fPid;
	
	static Map<String, Object> map;
	
	static String Copycode;
	
	static int Type;
	
	static String OrderCode;
	static String Aid;
	static int Pid;
	static Integer Vase;
	static String Area;
	static String Address;
	static int Reach;
	static int Cycle;
	static String Zhufu;
	static String Songhua;
	static String jh_List;
	static String jhColor_List;
	static int TType;
	static String Recommend;
	static Integer Szdx;
	static Integer Cash;
	static Integer Activity;
	static Double Yh;
	static Record Account;
	static Record pro_Flower;
	static int isBuy;
	static Record Order;
	static String Name;
	static String Tel;

	// 获取导航目录
	public static List<Record> getMenu(){
		return Db.find("select id,name,url,pid from f_menu");
	}
	
	// 获取导航目录
	public static Page<Record> getMenuPage(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select a.id,a.name,a.url,a.pid,b.name as pname", "FROM f_menu a LEFT JOIN f_menu b ON a.pid=b.id");
	}
	
	// 获取数据字典
	public static Page<Record> getDictionary(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,code_name,code_key,code_value,note", "from f_dictionary");
	}
	
	// 获取常见问题
	public static Page<Record> getQuestion(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,title,info", "from f_question order by id desc");
	}
	
	//意见反馈详情
	public static Record getAdviceinfo(int id){
		return Db.findFirst("select title,info from f_feedback where id=?", id);
	}
	
	// 获取养花须知
	public static Page<Record> getKnowledge(int pageno, int pagesize, int type, String tcode){
		String sqlExceptSelect = "FROM f_knowledge where 1=1";
		if(type !=0 )
			sqlExceptSelect += " and type = "+type;
		if(tcode != null)
			sqlExceptSelect += " and title like '%" + tcode + "%'";
		sqlExceptSelect += " order by istop desc,id desc";
		return Db.paginate(pageno, pagesize, "select id,type,title,imgurl,summary,istop", sqlExceptSelect);
	}
	
	// 获取地区
	public static List<Record> getArea(){
		return Db.find("select id, name, pid from f_area");
	}
	
	// 获取子级地区
	public static List<Record> getArea(int pid){
		return Db.find("select id, name, pid from f_area where pid = ?", pid);
	}
	
	// 获取周边产品
	public static List<Record> getAroundPros(){
		return Db.find("SELECT id, name FROM f_flower_pro WHERE ptid > 3 and state = 0");
	}
	
	// 删除地区
	public static boolean delArea(int id){
		Record area = Db.findById("f_area", id);
		if(area.getInt("pid")==0){
			// 一级地区
			Db.deleteById("f_area", id);
			List<Record> list = getArea(id);
			for(Record r : list){
				if(Db.delete("f_area", r)){
					Db.batch("delete from f_area where pid=?", new Object[][]{{r.getInt("id")}}, 50);
				}
			}
		}else{
			// 二级地区或多级地区
			if(Db.deleteById("f_area", id)){
				Db.batch("delete from f_area where pid=?", new Object[][]{{id}}, 50);
			}
		}
		return true;
	}
	
	// 获取系统公告
	public static Page<Record> getNotice(int pageno, int pagesize,String tcode){
		String sqlExceptSelect = "FROM f_notice a";
		if(tcode != null)
			sqlExceptSelect += " where a.title like '%" + tcode + "%'";
		sqlExceptSelect += " order by a.id desc";
		return Db.paginate(pageno, pagesize, "select id,title,ctime,info,a.describe ", sqlExceptSelect);
	}
	
	// 获取生活美学
	public static Page<Record> getEsthetics(int pageno, int pagesize, String code){
		String sqlExceptSelect = "FROM f_esthetics";
		if(code != null)
			sqlExceptSelect += " where title like '%" + code + "%'";
		sqlExceptSelect += " order by istop desc,id desc";
		return Db.paginate(pageno, pagesize, "select id,title,imgurl,ctime,istop", sqlExceptSelect);
	}
	
	// 获取物流平台
	public static Page<Record> getExpress(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,name,code", "from f_express");
	}
	
	// 获取物流列表
	public static Page<Record> getOrderInfo(int pageno, int pagesize, String picicode, String ordercode, String logisticcode, int state, int ishas, String wuliucode, String receiver){
		String sqlExceptionSelect = " FROM f_order_info a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode "+
						"LEFT JOIN f_flower_pro c ON b.fpid=c.id where 1=1 and b.type <> 3";
		if(picicode != null){
			sqlExceptionSelect += " and a.code like '"+picicode+"%'";
		}
		if(ordercode !=null){
			sqlExceptionSelect += " and a.ordercode like '%"+ordercode+"%'";
		}
		if(logisticcode !=null){
			sqlExceptionSelect += " and a.number like '%"+logisticcode+"%'";
		}
		if(state !=10){
			sqlExceptionSelect += " and a.state =" + state;
		}
		if(ishas !=10){
			sqlExceptionSelect += " and a.ishas =" + ishas;
		}
		if(!"xzwl".equals(wuliucode)){
			sqlExceptionSelect += " and a.ecode ='" + wuliucode+"'";
		}
		if(receiver != null)
			sqlExceptionSelect += " and a.name like '%" + receiver + "%'";
		sqlExceptionSelect += " GROUP BY a.id,a.code,a.ordercode,a.number,a.NAME,a.tel,a.ename,a.address,a.state order by a.id desc";
		Page<Record> page = Db.paginate(pageno, pagesize, "SELECT a.id,a.code,a.ordercode,a.number,a.NAME as sname,a.tel,a.ename,a.address,a.ishas,a.ofcycle,a.state,a.ctime,GROUP_CONCAT(c.NAME) dgsp ", sqlExceptionSelect);
		for(Record oi : page.getList()){
			List<Record> oplist = Db.find("select pid,type from f_order_pro where oid=?", oi.getInt("id"));	// 物流详细集合
			String name = new String();	// 物流产品名称
			Double cost = 0.00;			// 成本
			for(int i=0;i<oplist.size();i++){
				Record op = oplist.get(i);
				if(op.getInt("type") == 0){
					Record n = Db.findFirst("select fname,price,type,code from f_product where id=?", op.getInt("pid"));
					Double cost_1 = 0.00;
					if(n.getInt("type") == 1||n.getInt("type") == 2){
						 cost_1 += Db.queryDouble("SELECT sum(p.num*q.price1) as cost FROM f_product o,f_product_info p,f_flower_price q"
								+ "	WHERE o.id = p.pid AND p.fid = q.fid AND o.code = q.code and o.id=? and q.type=0", op.getInt("pid"));
					}else{
						 cost_1 += Db.queryDouble("SELECT sum(p.num*q.price1) as cost FROM f_product o,f_product_info p,f_flower_price q"
								+ "	WHERE o.id = p.pid AND p.fid = q.fid AND o.code = q.code and o.id=? and q.type=1", op.getInt("pid"));
					}
					cost += cost_1; 
					if(i==0){
						name = n.getStr("fname");
					}else{
						name += "," + n.getStr("fname");
					}
				}else if(op.getInt("type") == 1){
					Record n = Db.findFirst("select name,price from f_flower_pro where id=?", op.getInt("pid"));	
					if(i==0){
						name = n.getStr("name");
					}else{
						name += "," + n.getStr("name");
					}
					cost += n.getDouble("price");
				}else{
					Record n = Db.findFirst("select name,price from f_flower_pro where id=?", op.getInt("pid"));
					if(i==0){
						name = "（首单赠送）"+n.getStr("name");
					}else{
						name += "," + n.getStr("name");
					}
				}
			}
			oi.set("wlname", name);
			oi.set("cost", cost);
			String OrderStatusCode = Db.queryStr("select OrderStatusCode from f_logistics where WorkCode =? and OrderStatusCode = 46", oi.getStr("number"));
			oi.set("OrderStatusCode", OrderStatusCode);
		}
		return page;
	}
	
	// 获取物流详情
	public static Map<String, Object> getOrderPro(int oid, String code){
		Map<String, Object> map = new HashMap<>();
		Record orderinfo = Db.findById("f_order_info", oid);	// 物流详情
		Record order_jt = Db.findFirst("select jh_list,type,jh_color from f_order where ordercode = ?", orderinfo.get("ordercode"));		// 忌讳
		// 购买商品类型
		Integer ptid = Db.queryInt("SELECT b.ptid FROM f_order_detail a LEFT JOIN f_flower_pro b ON a.fpid=b.id WHERE a.ordercode=? AND a.type = 1", orderinfo.get("ordercode"));
		
		List<Record> cps = new ArrayList<>();//可选产品
		if(ptid == 1){	// 双品订阅
			String sql = "SELECT a.id,a.fname FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid left join f_flower c on b.fid = c.id WHERE a.type=1 AND a.code=?";
			if(order_jt.getStr("jh_list") != null){
				//忌讳的花类对应的产品
				String cp_list = Db.queryStr(" SELECT GROUP_CONCAT(DISTINCT a.id)  FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid LEFT JOIN f_flower c ON b.fid = c.id WHERE c.tid IN (" + order_jt.getStr("jh_list") + ")");
				if(cp_list != null){
					sql += " AND a.id NOT IN (" + cp_list + ")";
				}
			}
			if(order_jt.getStr("jh_color") != null){
				//忌讳的颜色
				String co_list = Db.queryStr("SELECT GROUP_CONCAT(DISTINCT a.id)  FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid LEFT JOIN f_flower c ON b.fid = c.id WHERE c.cid IN (" + order_jt.getStr("jh_color") + ")");
				if(co_list != null){
					sql += " AND a.id NOT IN (" + co_list + ")";
				}
				
			}
			cps = Db.find(sql, orderinfo.get("code"));	// 获取满足条件的本批次产品
//			String hasgetsql = "SELECT c.fid FROM f_order_info a LEFT JOIN f_order_pro b ON a.id=b.oid LEFT JOIN f_product_info c ON b.pid=c.pid WHERE b.type=0 AND a.ordercode=?";
//			List<Record> hasgetlist = Db.find(hasgetsql, orderinfo.getStr("ordercode"));
//			// 匹配送过的花并排除
//			for(Iterator<Record> it = cps.iterator();it.hasNext();){
//				Record pro = it.next();
//				for(Record hasget : hasgetlist){
//					if(pro.getInt("fid").equals(hasget.getInt("cid"))){
//						it.remove();
//						break;
//					}
//				}
//			}
			cps = getHasGet(cps, orderinfo.getStr("ordercode"));
		}else if(ptid == 2){
			cps = Db.find("select id,fname from f_product where type=2 and code=?", orderinfo.get("code"));	// 获取本批次产品
//			if(order_jt.getStr("jh_list") != null){	// 订单存在忌讳的花
//				String sql = "SELECT a.id FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid WHERE a.type=2 AND a.code=? AND b.fid IN (" + order_jt.getStr("jh_list") + ") GROUP BY b.pid";
//				// 查找不满足条件的花束
//				List<Record> jihuiList = Db.find(sql, orderinfo.getStr("ordercode"));
//				if(jihuiList.size() > 0){
//					for(Iterator<Record> it = cps.iterator();it.hasNext();){	// 匹配忌讳的花并排除
//						Record pro = it.next();
//						for(Record jh : jihuiList){
//							if(pro.getInt("id") == jh.getInt("id")){
//								it.remove();
//								break;
//							}
//						}
//					}
//				}
//			}
			// 获取已送过的花并排除
			cps = getHasGet(cps, orderinfo.getStr("ordercode"));
		}else if(ptid == 3){
			Integer szdx = Db.queryInt("select szdx from f_order where ordercode=?", orderinfo.getStr("ordercode"));
			cps = Db.find("SELECT b.id,b.fname FROM f_order_detail a LEFT JOIN f_product b ON a.fpid=b.fpid WHERE a.ordercode=? AND a.type=1 AND b.code=? AND b.type=3 AND b.iid=?", orderinfo.getStr("ordercode"), orderinfo.get("code"), szdx);	// 获取本批次产品
//			if(order_jt.getStr("jh_list") != null){	// 订单存在忌讳的花
//				String sql = "SELECT a.id FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid WHERE a.type=3 AND a.code=? AND b.fid IN (" + order_jt.getStr("jh_list") + ") GROUP BY b.pid";
//				// 查找不满足条件的花束
//				List<Record> jihuiList = Db.find(sql, orderinfo.get("code"));
//				if(jihuiList.size() > 0){
//					for(Iterator<Record> it = cps.iterator();it.hasNext();){	// 匹配忌讳的花并排除
//						Record pro = it.next();
//						for(Record jh : jihuiList){
//							if(pro.getInt("id") == jh.getInt("id")){
//								it.remove();
//								break;
//							}
//						}
//					}
//				}
//			}
		}
		map.put("cps", cps);
		
		if(order_jt.get("jh_list")!=null && !"".equals(order_jt.get("jh_list"))){
			String jihuis = Db.queryStr("select GROUP_CONCAT(name) as names from f_flower_type where id in ("+order_jt.get("jh_list")+")");
			map.put("jihuis", jihuis);	// 忌讳的花
		}

		if(order_jt.get("jh_color")!=null && !"".equals(order_jt.get("jh_color"))){
			String jhcolors = Db.queryStr("select GROUP_CONCAT(name) as names from f_color where id in ("+order_jt.get("jh_color")+")");
			map.put("jhcolors", jhcolors);	// 忌讳的花
		}
		
		List<Record> oprolist = Db.find("select id,pid,type from f_order_pro where oid = ?", oid);	// 派单产品集合
		List<Record> prolist = new ArrayList<>();
		for(Record opro : oprolist){
			Record pro;
			if(opro.getInt("type") == 0){	// 花束
				pro = Db.findFirst("SELECT a.type,b.type as ptype,c.name,b.fname FROM f_order_pro a LEFT JOIN f_product b ON a.pid=b.id left join f_flower_pro c on b.fpid=c.id where a.id = ?", opro.getInt("id"));
			}else{	// 周边
				pro = Db.findFirst("SELECT a.type,b.name,b.describe FROM f_order_pro a LEFT JOIN f_flower_pro b ON a.pid=b.id where a.id = ?", opro.getInt("id"));
			}
			prolist.add(pro);
		}
		List<Record> logilist = Db.find("select WorkCode,OpDateTime,OpDescription,OrderStatusCode from f_logistics where WorkCode =? order by ctime desc", code);
		
		String dgsp = Db.queryStr("select GROUP_CONCAT(b.name) from f_order_detail a LEFT JOIN f_flower_pro b on a.fpid = b.id where a.ordercode = ? and a.type <> 3", orderinfo.get("ordercode"));
		List<Record> picilist = Db.find("SELECT a.code,a.number,c.fname FROM f_order_info a LEFT JOIN f_order_pro b ON a.id = b.oid "
					+ "LEFT JOIN f_product c ON b.pid = c.id WHERE a.ordercode = ? AND b.type=0 AND a.state>=2 ORDER BY a.code", orderinfo.get("ordercode"));
		Record picivase = Db.findFirst("SELECT a.code,a.number,c.name from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
					+ "LEFT JOIN f_flower_pro c on b.pid = c.id where a.state>=2 and b.type=1 and a.ordercode = ?", orderinfo.get("ordercode"));
		String vasegive = "";
		if(picivase != null){
			for(Record pici : picilist){
				if(pici.getStr("number").equals(picivase.getStr("number"))){
					vasegive = picivase.getStr("name");
					pici.set("fname", pici.getStr("fname")+","+vasegive);
				}
			}
		}
		//赠品
		Record givepro = Db.findFirst("SELECT a.code,a.number,c.name from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
				+ "LEFT JOIN f_flower_pro c on b.pid = c.id where a.state>=2 and b.type=2 and a.ordercode = ?", orderinfo.get("ordercode"));
		if(givepro != null){
			for(Record pici : picilist){
				if(pici.getStr("number").equals(givepro.getStr("number"))){
					vasegive += ("（首单赠品）"+givepro.getStr("name"));
					pici.set("fname", pici.getStr("fname")+","+vasegive);
				}
			}
		}
		
		map.put("ptid", ptid);
		map.put("picilist", picilist);	// 派单历史
		map.put("logilist", logilist);	// 物流轨迹
		map.put("orderinfo", orderinfo);
		map.put("order_jt", order_jt);
		map.put("prolist", prolist);	// 派单产品集合
		map.put("dgsp", dgsp);			// 订购商品
		return map;
	}
	
	// 获取已送过的花并排除
	public static List<Record> getHasGet(List<Record> proList, String ordercode){
		if(proList.size() > 0){
			// 获取已送出的花束(花材id集合)
			List<Record> list = Db.find("SELECT GROUP_CONCAT(c.fid ORDER BY c.fid ASC) as gfid FROM f_order_info a LEFT JOIN f_order_pro b ON a.id=b.oid LEFT JOIN f_product_info c ON b.pid=c.pid WHERE b.type=0 AND a.ordercode=? GROUP BY a.number", ordercode);
			String idStr = new String();
			for(int i=0;i<proList.size();i++){
				if(i==0){
					idStr += proList.get(i).getInt("id");
				}else{
					idStr += "," + proList.get(i).getInt("id");
				}
			}
			proList = Db.find("SELECT pid as id,GROUP_CONCAT(fid ORDER BY fid ASC) AS gfid FROM f_product_info WHERE pid IN (" + idStr + ") GROUP BY pid");
			for(Iterator<Record> it = proList.iterator();it.hasNext();){	// 匹配已送出的花束并排除
				Record pro = it.next();
				for(Record hg : list){
					if(pro.getStr("gfid").equals(hg.getStr("gfid"))){
						it.remove();
						break;
					}
				}
			}
			
		}
		for(Record r : proList){
			String name = Db.findById("f_product", r.getInt("id")).getStr("fname");
			r.set("fname", name);
		}
		return proList;
	}
		
	// 获取会员
	public static Page<Record> getAccount(int pageno, int pagesize, int state, int isbuy, int isgive, String code, int gid, String time_a, String time_b, String aid){
		String sqlExceptSelect = " from f_account where 1=1";
		if(state != 9){
			sqlExceptSelect += " and state =" + state;
		}
		if(isbuy != 9){
			sqlExceptSelect += " and isbuy =" + isbuy;
		}
		if(isgive != 9){
			sqlExceptSelect += " and isgive =" + isgive;
		}
		if(code != null && code != ""){
			sqlExceptSelect += " and tel like '%"+code+"%'";
		}
		if(gid != 9999){
			sqlExceptSelect += " and gid = " + gid;
		}
		if(aid != null && aid != ""){
			sqlExceptSelect += " and id = " + aid;
		}
		if(time_a == null && time_b == null){
			sqlExceptSelect += "";
		}else{
			if(time_a != "" && "".equals(time_b)){
				sqlExceptSelect += " and ctime >='"+time_a+"'";
			}else if("".equals(time_a) && time_b != ""){
				sqlExceptSelect += " and ctime <='"+time_b+" 23:59:59'";
			}else if(time_a != "" &&time_b != ""){
				sqlExceptSelect += " and ctime between '"+time_a+"' and '"+time_b+" 23:59:59'";
			}
		}
		sqlExceptSelect += " order by ctime desc";
		Page<Record> page = Db.paginate(pageno, pagesize, "select id,gid,nick,isfans,headimg,tel,ctime,isbuy,isgive,state", sqlExceptSelect);
		String groups = HttpUtils.get(Constant.getHost + "/weixin/getGroups");
		Gson gson = new Gson();
		List<Map<String, Object>> gl = gson.fromJson(groups, ArrayList.class);
		for(Record account : page.getList()){
			for(Map<String, Object> g : gl){
				if(account.getInt("gid").equals(((Double) g.get("id")).intValue())){
					account.set("groupname", g.get("name"));
				}
			}
		}
		return page;
	}
	
	// 获取意见反馈
	public static Page<Record> getFeedback(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "SELECT a.id,b.nick,a.title,a.info,a.ctime", "FROM f_feedback a LEFT JOIN f_account b ON a.aid=b.id order by a.id desc");
	}
	
	// 获取会员电话
	public static Page<Record> getnumber (int pageno, int pagesize, String code){
		String sqlExceptSelect = "FROM f_account where 1=1 ";
		if(code != null){
			sqlExceptSelect += " and tel like '%" + code + "%'";
		}
		return Db.paginate(pageno, pagesize, "SELECT id,nick,headimg,tel,ctime,state ", sqlExceptSelect);
	}
	
	// 冻结用户
	public static boolean freezeAccount(int id, int state){
		Record record = new Record();
		record.set("state", state);
		record.set("id", id);
		return Db.update("f_account", record);
	}
	
	// 获取管理员
	public static Page<Record> getAdmin(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select a.id,a.username,a.rid,a.ctime,b.name", "from f_admin a left join f_role b on a.rid=b.id");
	}
		
	// 获取角色
	public static Page<Record> getRole(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,name", "from f_role");
	}
	
	// 保存权限
	public static boolean saveAuthority(Integer id, String mid, int rid){
		Record record = new Record();
		record.set("rid", rid);
		if(rid == 1){
			mid = Db.queryStr("SELECT GROUP_CONCAT(id) FROM f_menu");
		}
		record.set("mid", mid);
		if(id == null){
			return Db.save("f_authority", record);
		}else{
			record.set("id", id);
			return Db.update("f_authority", record);
		}
	}
	
	// 获取花材分类
	public static Page<Record> getFlowerType(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,name,ctime,jh", " from f_flower_type_view");
	}
	
	// 获取花材分类
	public static Page<Record> getFlower(int pageno, int pagesize, String ftype, String fname){
		String sqlExceptSelect = " from f_flower_view a left join f_flower_type b on a.tid=b.id where 1=1";
		if(ftype != ""){
			sqlExceptSelect += " and b.name like '%"+ ftype +"%'";
		}
		if(fname != ""){
			sqlExceptSelect += " and a.name like '%"+ fname +"%'";
		}
		return Db.paginate(pageno, pagesize, "select a.id,b.name as tname,a.name,a.imgurl,a.cid,a.ctime,a.state", sqlExceptSelect);
	}
	
	// 获取花材分类(仅类别查找)
	public static Page<Record> getFlowerftype(int pageno, int pagesize, String ftype){
		String sqlExceptSelect = " from  f_flower_type where 1=1";
		if(ftype != ""){
			sqlExceptSelect += " and name like '%"+ ftype +"%'";
		}
		return Db.paginate(pageno, pagesize, "select id,name,ctime,jh", sqlExceptSelect);
	}
	
	// 花材上架与下架
	public static boolean upordownFlower(int id, int state){
		Record record = new Record();
		record.set("state", state);	// 【1上架，0下架】
		record.set("id", id);
		return Db.update("f_flower", record);
	}

	public static void setJhFlower(int id, int jh){
		Db.update("update f_flower set jh=? where id=?", jh, id);
	}
	
	// 获取商品分类
	public static Page<Record> getFlowerPro(int pageno, int pagesize){
		Page<Record> flopros = Db.paginate(pageno, pagesize, "SELECT a.code_name,b.ptid,b.id,b.name,b.imgurl,b.describe,b.price,b.ctime,b.state,b.type ", "FROM f_dictionary a LEFT JOIN f_flower_pro b ON a.code_value=b.ptid WHERE a.code_key='ptid' AND b.id IS NOT NULL AND b.state != 1");
		List<Record> flos = flopros.getList();
		for (Record flo : flos) {
			if(flo.getInt("ptid")==1||flo.getInt("ptid")==2){
				String imgs = flo.getStr("imgurl");
				if(imgs.indexOf("-")!= -1){
					String[] img = imgs.split("-");
					flo.set("imgurl", img[0]);
				}else{
					flo.set("imgurl", imgs);
				}
			}
		}
		return flopros;
	}
	
	// 获取花票主题
	public static Page<Record> getCash(int pageno, int pagesize){
		Page<Record> page = Db.paginate(pageno, pagesize, "select id,name,ltime,ctime,state,type", "from f_cashtheme where type = 0 order by id desc");
		List<Record> themes = Db.find("select id,name,ltime,ctime,state,type from f_cashtheme where type = 1");
		for(int i=0;i<themes.size();i++){
			page.getList().add(i, themes.get(i));	
		}
		return page;
	}
		
	// 获取花票统计
	public static Page<Record> getCashsatistic(int pageno, int pagesize, int themeid, String pushid, String aid){
		String sqlExceptSelect = "FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid = b.id LEFT JOIN f_cashtheme c ON b.tid = c.id where 1=1 ";
		if(themeid != 9999){
			sqlExceptSelect += " and c.id = "+themeid;
		}
		if(pushid != null && pushid != ""){
			sqlExceptSelect += " and a.pushid = "+pushid;
		}
		if(aid != null && aid != ""){
			sqlExceptSelect += " and a.aid = "+aid;
		}
		Page<Record> page = Db.paginate(pageno, pagesize, "SELECT c.name,a.time_a,a.aid,a.pushid,a.aid,a.state", sqlExceptSelect);
		for(Record statis: page.getList()){
			String aname = Db.queryStr("select nick from f_account where id=?", statis.getInt("aid"));
			String tname = Db.queryStr("select username from f_admin where id=?", statis.getInt("pushid"));
			statis.set("aname", aname);
			statis.set("tname", tname);
		}
		return page;
	}
	
	// 获取花票统计
	public static List<Record> getCashthemes(){
		List<Record> themes = Db.find("select id,name from f_cashtheme");
		return themes;
	}
	
	// 获取活动列表
	public static Page<Record> getActivity(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,name,money,benefit,time_a,time_b,ctime,state", "from f_activity where state = 0 order by id desc");
	}
	
	// 保存商品信息
	public static Map<String, Object> saveProduct(int typeid, String flist, String fname, String sname, Integer iid, Integer fpid){
		responseMap = new HashMap<>();
		boolean R = false;
		Msg = "操作失败";
		Map<String, Object> map = DeliveryDateUtil.makeCode();
		boolean result = (boolean) map.get("result");
		dateCode = (String) map.get("datecode");
		typeId = typeid;
		fList = flist;
		fName = fname;
		sName = sname;
		iId = iid;
		fPid = fpid;
		
		// 判断是否在可操作时间范围内
		if(result){
			R = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					boolean save_result = false;
					Number yfh = Db.queryNumber("select count(1) from f_order_info where code=? and state=2", dateCode);	// 已发货
					if(yfh.intValue() == 0){
						String[] list = fList.split(",");
						Record product = new Record();
						product.set("code", dateCode);
						product.set("type", typeId);
						if(Db.queryNumber("select count(1) from f_product where fname = ?", fName).intValue() > 0){
							Msg = "产品名称重复";
							return save_result;
						}
						if(Db.queryNumber("select count(1) from f_product where sname = ?", sName).intValue() > 0){
							Msg = "产品简称重复";
							return save_result;
						}
						product.set("fname", fName);
						product.set("sname", sName);
						Db.update("delete from f_order_info where code=?", dateCode);	// 删除已生成的物流配单
						// 商品分类
						List<Record> fpl = Db.find("SELECT id,price FROM f_flower_pro where state = 0 AND ptid = ?", typeId);
						if(typeId == 3){
							if(!validSongHua(null, iId, fPid, dateCode)){
								Msg = "适赠类型已存在";
								return save_result;
							}else{
								for(Record fp : fpl){
									// 添加分类ID与价格
									if(fp.getInt("id") == fPid){
										product.set("fpid", fp.getInt("id"));
										product.set("price", fp.getDouble("price"));
										break;
									}
								}
							}
						}else{
							product.set("fpid", fpl.get(0).getInt("id"));
							product.set("price", fpl.get(0).getDouble("price"));
						}
						product.set("iid", iId);
						// 添加产品成功
						if(Db.save("f_product", product)){
							Long pid = product.getLong("id");
							for(String flower : list){
								int fid = Integer.parseInt(flower.substring(0, flower.indexOf("-")));
								int num = Integer.parseInt(flower.substring(flower.indexOf("-")+1));
								Record proinfo = new Record();
								proinfo.set("pid", pid);
								proinfo.set("fid", fid);
								proinfo.set("num", num);
								// 添加商品详情
								Db.save("f_product_info", proinfo);
							}
							Msg = "操作成功";
							save_result = true;
						}
					}else{
						Msg = "该批次已发货";
					}
					return save_result;
				}
			});
		}else{
			Msg = "不在可操作时间内";
		}
		responseMap.put("R", R);
		responseMap.put("msg", Msg);
		return responseMap;
	}
	
	// 送花系列添加商品验证
	public static boolean validSongHua(Integer id, int iid, int fpid, String datecode){
		String sql = "SELECT COUNT(1) FROM f_product WHERE TYPE=3 AND fpid=? AND iid=? AND CODE=?";
		if(id != null){
			sql += " AND id!=?";
			Number number = Db.queryNumber(sql, fpid, iid, datecode, id);
			return number.intValue()>0?false:true;
		}else{
			Number number = Db.queryNumber(sql, fpid, iid, datecode);
			return number.intValue()>0?false:true;
		}
	}
	
	// 修改商品信息
	public static Map<String, Object> editProduct(int id, int typeid, String flist, String fname, String sname, Integer iid, Integer fpid){
		responseMap = new HashMap<>();
		boolean R = false;
		Msg = "操作失败";
		Map<String, Object> map = DeliveryDateUtil.makeCode();
		boolean result = (boolean) map.get("result");
		dateCode = (String) map.get("datecode");
		Id = id;
		typeId = typeid;
		fList = flist;
		fName = fname;
		sName = sname;
		iId = iid;
		fPid = fpid;
		
		// 判断是否在可操作时间范围内
		if(result){
			R = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					boolean ch_result = false;
					Record pi = Db.findById("f_product", Id);
					// 判断是否是本批次产品
					if(dateCode.equals(pi.getStr("code"))){
						Number yfh = Db.queryNumber("select count(1) from f_order_info where code=? and state=2", dateCode);	// 已发货
						if(yfh.intValue() == 0){
							String[] list = fList.split(",");
							Record product = new Record();
							product.set("id", Id);
							product.set("type", typeId);
							if(Db.queryNumber("select count(1) from f_product where id != ? and fname = ?", Id, fName).intValue() > 0){
								Msg = "产品名称重复";
								return ch_result;
							}
							if(Db.queryNumber("select count(1) from f_product where id != ? and sname = ?", Id, sName).intValue() > 0){
								Msg = "产品简称重复";
								return ch_result;
							}
							product.set("fname", fName);
							product.set("sname", sName);
							Db.batch("delete from f_product_info where pid=?", new Object[][]{{Id}}, 10);	// 删除旧商品详情数据
							Db.update("delete from f_order_info where code=?", dateCode);	// 删除已生成的物流配单
							// 商品
							List<Record> fpiList = Db.find("SELECT id,price FROM f_flower_pro where state = 0 AND ptid = ?", typeId);
							if(typeId == 3){
								if(!validSongHua(Id, iId, fPid, dateCode)){
									Msg = "适赠类型已存在";
									return ch_result;
								}else{
									for(Record fp : fpiList){
										// 添加分类ID与价格
										if(fp.getInt("id").equals(fPid)){
											product.set("fpid", fp.getInt("id"));
											product.set("price", fp.getDouble("price"));
											break;
										}
									}
								}
							}else{
								product.set("fpid", fpiList.get(0).getInt("id"));
								product.set("price", fpiList.get(0).getDouble("price"));
							}
							product.set("iid", iId);
							// 修改产品成功
							if(Db.update("f_product", product)){
								for(String flower : list){
									int fid = Integer.parseInt(flower.substring(0, flower.indexOf("-")));
									int num = Integer.parseInt(flower.substring(flower.indexOf("-")+1));
									Record proinfo = new Record();
									proinfo.set("pid", Id);
									proinfo.set("fid", fid);
									proinfo.set("num", num);
									// 添加商品详情
									Db.save("f_product_info", proinfo);
								}
								ch_result = true;
								Msg = "操作成功";
							}
						}
					}else{
						Msg = "无法修改其他批次产品";
					}
					return ch_result;
				}
			});
		}else{
			Msg = "不在可操作时间内";
		}
		responseMap.put("R", R);
		responseMap.put("msg", Msg);
		return responseMap;
	}
	
	// 删除产品
	public static boolean delProduct(int id){
		boolean R = false;
		Map<String, Object> map = DeliveryDateUtil.makeCode();
		boolean result = (boolean) map.get("result");
		String datecode = (String) map.get("datecode");
		if(result){
			Record pi = Db.findById("f_product", id);
			// 判断是否是本批次产品
			if(datecode.equals(pi.getStr("code"))){
				Number yfh = Db.queryNumber("select count(1) from f_order_info where code=? and state=2", datecode);	// 已发货
				if(yfh.intValue() == 0){
					if(Db.deleteById("f_product", id)){
						Db.batch("delete from f_product_info where pid = ?", new Object[][]{{id}}, 10);	// 删除旧商品详情数据
						Db.update("delete from f_order_info where code=?", datecode);	// 删除已生成的物流配单
						R = true;
					}
				}
			}
		}
		return R;
	}
	
	// 获取产品分页
	public static Page<Record> getProduct(int type, int pageno, int pagesize,String code,String minprice,String maxprice){
		if(type == 3){
			String sqlExceptSelect = " FROM f_product a LEFT JOIN f_flower_pro b ON a.fpid=b.id LEFT JOIN f_idoneity c ON a.iid=c.id  LEFT JOIN ("+
					" SELECT o.code, p.pid,SUM(p.num*q.price1) cost FROM f_product o,f_product_info p,f_flower_price q WHERE o.id = p.pid AND p.fid = q.fid AND o.code = q.code"+
					" AND q.type = 1 AND o.type = 3  GROUP BY o.code,p.pid ) d ON a.code = d.code AND a.id = d.pid  WHERE a.type= 3";
			if(code!=null && !"".equals(code) ){
				sqlExceptSelect += " and a.code like '" + code + "%'";
			}
			if(minprice != null&&!"".equals(minprice)&&maxprice != null&&!"".equals(maxprice)){
				sqlExceptSelect += " and cost between " + minprice + " and "+maxprice;
			}
			sqlExceptSelect += " order by a.code desc,d.cost desc";
			return Db.paginate(pageno, pagesize, "select a.id,a.code,a.type,b.name,a.fname,a.sname,a.price,c.title,d.cost", sqlExceptSelect);
		}else{
			String sqlExceptSelect = " FROM f_product a left join f_flower_pro b on a.fpid=b.id LEFT JOIN ";
			if(type == 1){
				sqlExceptSelect += "(select o.code,p.pid,sum(p.num*q.price1) as cost from f_product o left join f_product_info p on o.id=p.pid left join f_flower_price q on p.fid=q.fid where o.code=q.code and q.type=0 and o.type=1 group by o.code,p.pid)"
					+ " c ON a.code=c.code AND a.id=c.pid WHERE a.type=1";
			}else{
				sqlExceptSelect += "(select o.code,p.pid,sum(p.num*q.price1) as cost from f_product o left join f_product_info p on o.id=p.pid left join f_flower_price q on p.fid=q.fid where o.code=q.code and q.type=0 and o.type=2 group by o.code,p.pid)"
					+ " c ON a.code=c.code AND a.id=c.pid WHERE a.type=2";
			}
			if(code!=null && !"".equals(code)){
				sqlExceptSelect += " and a.code like '" + code + "%'";
			}
			if(minprice!=null && !"".equals(minprice) && maxprice!=null && !"".equals(maxprice)){
				sqlExceptSelect += " and cost between " + minprice + " and "+maxprice;
			}
			sqlExceptSelect += " order by a.code desc,c.cost desc";
			return Db.paginate(pageno, pagesize, "select a.id,a.code,a.type,b.name,a.fname,a.sname,a.price,c.cost", sqlExceptSelect);
		}
	}
	
	// 获取花材批次
	public static Page<Record> getPriceForBatch(int pageno, int pagesize, String batchcode){
		String sqlExceptSelect = "FROM f_flower_price where 1=1";
		if(batchcode != null){
			sqlExceptSelect += " and code like '" + batchcode + "%'";
		}
		sqlExceptSelect += " group by code order by code desc";
		return Db.paginate(pageno, pagesize, "SELECT code ", sqlExceptSelect);
	}
	
	// 获取批次花材
	public static Map<String, Object> getFlowerForCode(int type){
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> map = DeliveryDateUtil.makeCode();
		boolean result = (boolean) map.get("result");
		String datecode = (String) map.get("datecode");
		List<Record> flowerlist = new ArrayList<>();
		
		if(result){
			if(type == 0){
				flowerlist = Db.find("SELECT a.type,b.fid,c.name FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid LEFT JOIN f_flower c ON b.fid=c.id WHERE a.code=? and (a.type=1 or a.type=2) GROUP BY b.fid", datecode);
			}else{
				flowerlist = Db.find("SELECT a.type,b.fid,c.name FROM f_product a LEFT JOIN f_product_info b ON a.id=b.pid LEFT JOIN f_flower c ON b.fid=c.id WHERE a.code=? and a.type=3 GROUP BY b.fid", datecode);
			}
			List<Record> pricelist = Db.find("select fid,price1,price2 from f_flower_price where code = ? and type = ?", datecode, type);
			for(Iterator<Record> it = flowerlist.iterator();it.hasNext();){
				Record t = it.next();
				if(type == 0){
					// (订阅)普通花材
					if(t.getInt("type")==1 || t.getInt("type")==2){
						for(Record price : pricelist){
							Integer fid1 = t.getInt("fid");
							Integer fid2 = price.getInt("fid");
							if(fid1.equals(fid2)){
								t.set("price1", price.getDouble("price1"));
								t.set("price2", price.getDouble("price2"));
								break;
							}
						}
					}else{
						it.remove();
					}
				}else{
					// (送花)上品花材
					if(t.getInt("type")==3){
						for(Record price : pricelist){
							if(t.getInt("fid").equals(price.getInt("fid"))){
								t.set("price1", price.getDouble("price1"));
								t.set("price2", price.getDouble("price2"));
								break;
							}
						}
					}else{
						it.remove();
					}
				}
			}
		}
		responseMap.put("flowerlist", flowerlist);
		responseMap.put("datecode", datecode);
		return responseMap;
	}
	
	// 保存花价
	public static boolean savePrice(int type, String flist){
		Map<String, Object> map = DeliveryDateUtil.makeCode();
		boolean result = (boolean) map.get("result");
		String datecode = (String) map.get("datecode");
		if(result){
			List<Record> pricelist = new ArrayList<>();
			String[] list = flist.split(",");
			// 删除旧数据
			Db.batch("delete from f_flower_price where code = ? and type = ?", new Object[][]{{datecode, type}}, 100);
			for(String p : list){
				String[] param = p.split("-");
				Record price = new Record();
				price.set("code", datecode);
				price.set("type", type);
				price.set("fid", Integer.parseInt(param[0]));
				price.set("price1", Double.parseDouble(param[1]));
				if(param.length==3){
					price.set("price2", Double.parseDouble(param[2]));
				}
				pricelist.add(price);
			}
			// 保存数据
			Db.batchSave("f_flower_price", pricelist, 100);
		}
		return result;
	}
	
	// 保存花价查看中的实际花价
	public static boolean savePriceSee(String flist){
		String[] list = flist.split(",");
		int num = 0;
		for(String p : list){
			String[] param = p.split("-");
			String code = param[0];
			int type = Integer.parseInt(param[1]);
			int fid = Integer.parseInt(param[2]);
			Double price2 = Double.parseDouble(param[3]);
			int f = Db.update("update f_flower_price set price2 = ? where code =? and type=? and fid =?", price2, code, type, fid);
			num += f;
		}
		if(num > 0){
			return true;
		}else{
			return false;
		}
	}
	
	// 查看花价
	public static List<Record> seePrice(String code){
		List<Record> list = Db.find("SELECT a.fid,a.code,a.type,b.name,b.imgurl,a.price1,a.price2 FROM f_flower_price a LEFT JOIN f_flower_view b ON a.fid=b.id where a.code=?", code);
		return list;
	}
	
	// 订单列表
	public static Page<Record> getOrderList(int pageno, int pagesize, int time,	int type, int state, String ordercode, int ishas, String receiver, String time_a, String time_b, String aid, String tel){
		String sqlExceptSelect = "FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id where b.type=1";
		if(time != 0)
			sqlExceptSelect += " and a.reach=" + time;
		if(type != 0)
			sqlExceptSelect += " and a.type=" + type;
		if(state != 9)
			sqlExceptSelect += " and a.state=" + state;
		if(ordercode != null)
			sqlExceptSelect += " and a.ordercode like '%" + ordercode + "%'";
		if(ishas != 2)
			sqlExceptSelect += " and a.ishas =" + ishas;
		if(receiver != null)
			sqlExceptSelect += " and a.name like '%" + receiver + "%'";
		if(aid != null && aid != "")
			sqlExceptSelect += " and a.aid = " + aid;
		if(tel != null && tel != "")
			sqlExceptSelect += " and a.tel = " + tel;
		
		if(time_a == null && time_b == null){
			sqlExceptSelect += "";
		}else{
			if(time_a != "" && "".equals(time_b)){
				sqlExceptSelect += " and a.ctime >='"+time_a+"'";
			}else if("".equals(time_a) && time_b != ""){
				sqlExceptSelect += " and a.ctime <='"+time_b+"'";
			}else if(time_a != "" &&time_b != ""){
				sqlExceptSelect += " and a.ctime between '"+time_a+"' and '"+time_b+"'";
			}
		}
		sqlExceptSelect += " order by a.id desc";
		return Db.paginate(pageno, pagesize, "SELECT a.ordercode,a.aid,c.name,a.name as receiver,a.tel,a.jihui,a.color,a.cycle,a.reach,a.price AS totalprice,a.type,a.ctime,a.state,a.ishas,a.is_sy ", sqlExceptSelect);
	}
	
	// 订单评分列表
	public static Page<Record> getCommentList(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select ordercode,point,descripte,ctime", "from f_comment");
	}
	
	// 采购订单
	public static Page<Record> getPurchaseList(int pageno, int pagesize, String ordercode){
		String sqlExceptSelect = " from f_purchase where 1=1 ";
		if(ordercode != null){
			sqlExceptSelect += " and code like '%"+ordercode+"%'";
		}
		sqlExceptSelect += " group by code,ctime order by code desc";
		return Db.paginate(pageno, pagesize, "SELECT code,SUM(num_a) AS numa,SUM(num_b) AS numb,SUM(price*num_b) AS totalprice,ctime ", sqlExceptSelect);
	}
	
	// 生成采购订单
	public static Map<String, Object> createPurchase(){
		Map<String, Object> resultMap = new HashMap<>();
		boolean R = false;
		String M = new String();
		Map<String, Object> dateMap = DeliveryDateUtil.chooseDate();
		if((boolean) dateMap.get("result")){
			String datecode = (String) dateMap.get("datecode");
			Number fyc = Db.queryNumber("select count(1) from f_order_info where state>=1 and code=?", datecode);	// 非异常物流
			if(fyc.intValue() > 0){	// 必须发货之后才能生成采购单
				Db.update("delete from f_purchase where code=?", datecode);
				Number errorNum = Db.queryNumber("SELECT COUNT(1) FROM f_order_info a LEFT JOIN f_order_pro b ON a.id=b.oid "
						+ "LEFT JOIN f_product_info c ON b.pid=c.pid LEFT JOIN f_flower_price d ON c.fid=d.fid"
						+ " WHERE a.code=? AND b.type=0 AND d.price1 IS null", datecode);
				if(errorNum.intValue() == 0){
					Number dozens = Db.queryNumber("SELECT COUNT(1) FROM f_flower a LEFT JOIN f_flower_price b ON a.id = b.fid WHERE CODE = ? AND a.dozen = 0", datecode);
					if(dozens.intValue() == 0){
						List<Record> flist = Db.find("SELECT c.fid,ROUND((SUM(c.num)*f.loss+SUM(c.num))/f.dozen) AS num,d.price1,d.price2,d.type FROM f_order_info a " +
								"LEFT JOIN f_order_pro b ON a.id=b.oid " +
								"LEFT JOIN f_product_info c ON b.pid=c.pid " +
								"LEFT JOIN f_flower_price d ON c.fid=d.fid " +
								"LEFT JOIN f_flower f ON c.fid=f.id " +
								"LEFT JOIN (SELECT id,CASE WHEN TYPE<=2 THEN 0 ELSE 1 END AS TYPE FROM f_product) e ON b.pid=e.id " +
								"WHERE a.code=? AND d.code=? AND b.type=0 AND d.type=e.type GROUP BY c.fid,d.type" , datecode, datecode);
						Date ctime = new Date();
						for(Record f : flist){
							Record purchase = new Record();
							purchase.set("code", datecode);
							purchase.set("type", f.getInt("type")==0?1:2);
							purchase.set("fid", f.getInt("fid"));
							purchase.set("price", f.getDouble("price1"));
							purchase.set("num_a", f.get("num"));
							purchase.set("num_b", f.get("num"));
							purchase.set("ctime", ctime);
							Db.save("f_purchase", purchase);
						}
						R = true;
						M = "批次" + datecode + "采购订单生成完成";
					}else{
						M = "花材成打量未完善";
					}
				}else{
					M = "批次" + datecode + "花价未完善";
				}
			}else{
				M = "批次" + datecode + "暂无法生成采购订单";
			}
		}else{
			M = "新批次订单尚未生成";
		}
		resultMap.put("R", R);
		resultMap.put("M", M);
		return resultMap;
	}
	
	// 订单详情
	public static Map<String, Object> getPurchaseInfo(String code){
		Map<String, Object> flowerMap = new HashMap<>();
		String sql = "SELECT a.fid,b.imgurl,c.name as tname,b.name,a.price,a.num_a,a.num_b FROM f_purchase a LEFT JOIN f_flower b ON a.fid=b.id LEFT JOIN f_flower_type c ON b.tid=c.id WHERE a.code=? AND a.type=?";
		List<Record> listA = Db.find(sql, code, 1);
		List<Record> listB = Db.find(sql, code, 2);
		List<Record> listC = Db.find("SELECT ax.st,COUNT(ax.st) AS stnum "
				+ "FROM (SELECT CASE WHEN b.fpid < 3 THEN b.fpid ELSE 3 END AS st "
				+ "FROM f_order_info a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode WHERE a.code=? AND b.type=1) ax GROUP BY ax.st", code);
		flowerMap.put("listA", listA);
		flowerMap.put("listB", listB);
		int[] stnum = {0,0,0};
		for(Record r : listC){
			stnum[r.getLong("st").intValue()-1] = r.getLong("stnum").intValue();
		}
		flowerMap.put("stnum", stnum);
		return flowerMap;
	}
	
	// 修改采购数量
	public static boolean updatePurchase(String code, String numarr){
		String[] arr = numarr.split(",");
		for(String num : arr){
			String[] p = num.split("-");
			Db.update("update f_purchase set num_b=? where code=? and type=? and fid=?", p[2], code, p[0], p[1]);
		}
		return true;
	}
	
	// 导出采购单-one
	public static void getPurchaseInfoForExcel_a(HttpServletResponse response, String code){
		String sql = "SELECT b.name,a.price,a.num_b FROM f_purchase a LEFT JOIN f_flower b ON a.fid=b.id WHERE a.code=? and a.type=?";
		List<Record> listA = Db.find(sql, code, 1);	// 订阅级
		List<Record> listB = Db.find(sql, code, 2);	// 送花级
		HSSFWorkbook wbook = new HSSFWorkbook();
		HSSFSheet sheet1 = wbook.createSheet();
		wbook.setSheetName(0, "订阅级", (short)1);
		HSSFSheet sheet2 = wbook.createSheet();
		wbook.setSheetName(1, "送花级", (short)1);
		HSSFRow row;
		for(int i=0;i<listA.size();i++){
			if(i==0){
				row = sheet1.createRow(i);
				HSSFCell cell0 = row.createCell((short) 0);
				cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell0.setCellValue("名称");
				HSSFCell cell1 = row.createCell((short) 1);
				cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell1.setCellValue("单价");
				HSSFCell cell2 = row.createCell((short) 2);
				cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell2.setCellValue("采购量");
			}
			row = sheet1.createRow(i+1);
			Record r = listA.get(i);
			HSSFCell cell0 = row.createCell((short) 0);
			cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell0.setCellValue(r.getStr("name"));
			HSSFCell cell1 = row.createCell((short) 1);
			cell1.setCellValue(r.getDouble("price"));
			HSSFCell cell2 = row.createCell((short) 2);
			cell2.setCellValue(r.getInt("num_b"));
		}
		for(int i=0;i<listB.size();i++){
			if(i==0){
				row = sheet2.createRow(i);
				HSSFCell cell0 = row.createCell((short) 0);
				cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell0.setCellValue("名称");
				HSSFCell cell1 = row.createCell((short) 1);
				cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell1.setCellValue("单价");
				HSSFCell cell2 = row.createCell((short) 2);
				cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell2.setCellValue("采购量");
			}
			row = sheet2.createRow(i+1);
			Record r = listB.get(i);
			HSSFCell cell0 = row.createCell((short) 0);
			cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell0.setCellValue(r.getStr("name"));
			HSSFCell cell1 = row.createCell((short) 1);
			cell1.setCellValue(r.getDouble("price"));
			HSSFCell cell2 = row.createCell((short) 2);
			cell2.setCellValue(r.getInt("num_b"));
		}
		response.addHeader("Content-Disposition", "attachment;filename=" + code + ".xls");
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		try {
			ServletOutputStream out = response.getOutputStream();
			wbook.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 导出采购单-two
	public static void getPurchaseInfoForExcel_b(HttpServletResponse response, Record account, String code){
		String[] strArr = DeliveryDateUtil.getTimeByCode(code);
		List<Record> listA = Db.find("SELECT ax.st,COUNT(ax.st) AS stnum "
				+ "FROM (SELECT CASE WHEN b.fpid < 3 THEN b.fpid ELSE 3 END AS st "
				+ "FROM f_order_info a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode WHERE a.code=? AND b.type=1) ax GROUP BY ax.st", code);
		int[] stnum = {0,0,0};
		for(Record r : listA){
			stnum[r.getLong("st").intValue()-1] = r.getLong("stnum").intValue();
		}
		// 使用到的花材统计
		List<Record> listB = Db.find("SELECT a.fid,c.name AS tname,b.name,SUM(a.num_a) as num FROM f_purchase a "
				+ "LEFT JOIN f_flower b ON a.fid=b.id LEFT JOIN f_flower_type c ON b.tid=c.id WHERE a.code=? GROUP BY a.fid", code);
		for(Record r : listB){
			int fid = r.getInt("fid");
			Number num_a = Db.queryNumber("SELECT COUNT(1) FROM f_order_info a LEFT JOIN f_order_pro b ON a.id=b.oid LEFT JOIN f_product_info c ON b.pid=c.pid LEFT JOIN f_product d ON c.pid=d.id WHERE a.code=? AND b.type=0 AND c.fid=? AND d.fpid=1", code, fid);
			Number num_b = Db.queryNumber("SELECT COUNT(1) FROM f_order_info a LEFT JOIN f_order_pro b ON a.id=b.oid LEFT JOIN f_product_info c ON b.pid=c.pid LEFT JOIN f_product d ON c.pid=d.id WHERE a.code=? AND b.type=0 AND c.fid=? AND d.fpid=2", code, fid);
			Number num_c = Db.queryNumber("SELECT COUNT(1) FROM f_order_info a LEFT JOIN f_order_pro b ON a.id=b.oid LEFT JOIN f_product_info c ON b.pid=c.pid LEFT JOIN f_product d ON c.pid=d.id WHERE a.code=? AND b.type=0 AND c.fid=? AND d.fpid>2", code, fid);
			r.set("numa", num_a.intValue());
			r.set("numb", num_b.intValue());
			r.set("numc", num_c.intValue());
		}
		HSSFWorkbook wbook = new HSSFWorkbook();
		HSSFSheet sheet = wbook.createSheet();
		
		sheet.setColumnWidth((short) 0, (short) 4500);
		sheet.setColumnWidth((short) 1, (short) 4500);
		sheet.setColumnWidth((short) 2, (short) 9000);
		sheet.setColumnWidth((short) 3, (short) 9000);
		sheet.setColumnWidth((short) 4, (short) 9000);
		sheet.setColumnWidth((short) 5, (short) 9000);
		sheet.setColumnWidth((short) 6, (short) 9000);
		HSSFRow row;
		HSSFCell cell;
		// 样式一
		HSSFCellStyle cellStyle1 = wbook.createCellStyle();
		HSSFFont font1 = wbook.createFont();
		font1.setFontHeightInPoints((short) 11);
		font1.setFontName("微软雅黑");
		cellStyle1.setFont(font1);
		cellStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);	//垂直居中
		cellStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);			//水平居中
		// 样式二
		HSSFCellStyle cellStyle2 = wbook.createCellStyle();
		HSSFFont font2 = wbook.createFont();
		font2.setFontHeightInPoints((short) 12);
		font2.setFontName("微软雅黑");
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);					//粗体显示
		cellStyle2.setFont(font2);
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);	//垂直居中
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);			//水平居中
		cellStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		// 样式三
		HSSFCellStyle cellStyle3 = wbook.createCellStyle();
		HSSFFont font3 = wbook.createFont();
		font3.setFontHeightInPoints((short) 12);
		font3.setFontName("微软雅黑");
		cellStyle3.setFont(font3);
		cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);	//垂直居中
		cellStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);			//水平居中
		cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		for(int i=0;i<listB.size();i++){
			if(i==0){
				row = sheet.createRow(0);
				cell = row.createCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				cell.setCellValue("计算人：" + account.getStr("username"));
				cell = row.createCell((short) 1);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				Calendar now = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				cell.setCellValue("计算时间：" + sdf.format(now.getTime()));
				cell = row.createCell((short) 2);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				cell.setCellValue("订单起始时间：" + strArr[0]);
				cell = row.createCell((short) 3);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				cell.setCellValue("订单截止时间：" + strArr[1]);
				cell = row.createCell((short) 4);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				cell.setCellValue("包含双品：" + stnum[0] + "束");
				cell = row.createCell((short) 5);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				cell.setCellValue("包含多品：" + stnum[1] + "束");
				cell = row.createCell((short) 6);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle1);
				cell.setCellValue("包含送花：" + stnum[2] + "束");
				
				sheet.addMergedRegion(new Region(1, (short) 0, 1, (short) 1));
				row = sheet.createRow(1);
				cell = row.createCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle2);
				cell.setCellValue("种类");
				cell = row.createCell((short) 1);
				cell.setCellStyle(cellStyle2);
				cell = row.createCell((short) 2);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle2);
				cell.setCellValue("花材名称");
				cell = row.createCell((short) 3);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle2);
				cell.setCellValue("双品单量");
				cell = row.createCell((short) 4);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle2);
				cell.setCellValue("多品单量");
				cell = row.createCell((short) 5);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle2);
				cell.setCellValue("送花");
				cell = row.createCell((short) 6);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(cellStyle2);
				cell.setCellValue("需采购的花材打量");
			}
			
			sheet.addMergedRegion(new Region((i*2+2), (short) 0, (i*2+3), (short) 1));
			sheet.addMergedRegion(new Region((i*2+2), (short) 2, (i*2+3), (short) 2));
			sheet.addMergedRegion(new Region((i*2+2), (short) 3, (i*2+3), (short) 3));
			sheet.addMergedRegion(new Region((i*2+2), (short) 4, (i*2+3), (short) 4));
			sheet.addMergedRegion(new Region((i*2+2), (short) 5, (i*2+3), (short) 5));
			sheet.addMergedRegion(new Region((i*2+2), (short) 6, (i*2+3), (short) 6));
			
			row = sheet.createRow(i*2+2);
			Record exa = listB.get(i);
			
			cell = row.createCell((short) 0);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellStyle(cellStyle3);
			cell.setCellValue(exa.getStr("tname"));
			
			cell = row.createCell((short) 2);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellStyle(cellStyle3);
			cell.setCellValue(exa.getStr("name"));
			
			cell = row.createCell((short) 3);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellStyle(cellStyle3);
			cell.setCellValue(exa.getInt("numa"));
			
			cell = row.createCell((short) 4);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellStyle(cellStyle3);
			cell.setCellValue(exa.getInt("numb"));
			
			cell = row.createCell((short) 5);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellStyle(cellStyle3);
			cell.setCellValue(exa.getInt("numc"));
			
			cell = row.createCell((short) 6);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellStyle(cellStyle3);
			cell.setCellValue(exa.getBigDecimal("num").intValue());
			
			row = sheet.createRow(i*2+3);
			cell = row.createCell((short) 0);
			cell.setCellStyle(cellStyle3);
			cell = row.createCell((short) 1);
			cell.setCellStyle(cellStyle3);
			cell = row.createCell((short) 2);
			cell.setCellStyle(cellStyle3);
			cell = row.createCell((short) 3);
			cell.setCellStyle(cellStyle3);
			cell = row.createCell((short) 4);
			cell.setCellStyle(cellStyle3);
			cell = row.createCell((short) 5);
			cell.setCellStyle(cellStyle3);
			cell = row.createCell((short) 6);
			cell.setCellStyle(cellStyle3);
		}
		response.addHeader("Content-Disposition", "attachment;filename=" + code + ".xls");
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		try {
			ServletOutputStream out = response.getOutputStream();
			wbook.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 订单详情
	public static Map<String, Object> getorderInfo(String ordercode){
		Map<String, Object> map = new HashMap<>();
		Record order = Db.findFirst("select a.aid,a.ordercode,a.name,a.tel,a.addr,a.reach,a.price,a.cycle,a.zhufu,a.songhua,a.jihui,a.color,a.yhje,a.yhfs,a.ctime,a.type,a.state,a.jh_list,a.jh_color,b.title,a.szdx,a.remark "
				+ "from f_order a left join f_idoneity b on a.szdx = b.id where a.ordercode=?", ordercode);
		List<Record> detaillist = Db.find("SELECT b.name,a.price,a.type,a.fpid FROM f_order_detail a LEFT JOIN f_flower_pro b ON a.fpid=b.id where a.ordercode=?", ordercode);
		List<Record> flowerSjlist = Db.find("select id,name from f_flower where state = 1");
		// 优惠方式
		String yhfs = order.getStr("yhfs");
		String[] ids = yhfs.split(",");
		yhfs = null;
		if(Integer.parseInt(ids[0]) != 0){
			Record activity = Db.findById("f_activity", ids[0]);
			yhfs = activity.getStr("name");
		}
		if(Integer.parseInt(ids[1]) != 0){
			Record cash = Db.findFirst("SELECT b.money,b.benefit,c.name FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id LEFT JOIN f_cashtheme c ON b.tid=c.id where a.id=?", ids[1]);
			if(yhfs == null){
				yhfs = cash.getStr("name") + ",满" + cash.getDouble("benefit") + "减" + cash.getDouble("money");
			}else{
				yhfs += "、" + cash.getStr("name") + ",满" + cash.getDouble("benefit") + "减" + cash.getDouble("money");
			}
		}
		List<Record> picilist = Db.find("select a.code,a.number,c.fname from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
				                 + "LEFT JOIN f_product c on b.pid = c.id where b.type=0 and a.state>=2 and ordercode =? order by a.code desc",ordercode);
		Record picivase = Db.findFirst("select a.code,a.number,c.name from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
								  + "LEFT JOIN f_flower_pro c on b.pid = c.id  where a.state>=2 and b.type=1 and a.ordercode = ?",ordercode);
		map.put("picilist", picilist);
		map.put("picivase", picivase);
		order.set("yhfs", yhfs);
		map.put("order", order);
		map.put("detaillist", detaillist);
		map.put("flowerSjlist", flowerSjlist);
		return map;
	}
	
	// 退款申请
	public static Page<Record> getRefundList(int pageno, int pagesize, int state, String ordercode){
		String sqlExceptSelect = "FROM f_refund a LEFT JOIN f_order b ON a.ordercode=b.ordercode where 1=1";
		if(state != 9)
			sqlExceptSelect += " and a.state=" + state;
		if(ordercode != null)
			sqlExceptSelect += " and a.ordercode like '%" + ordercode + "%'";
		return Db.paginate(pageno, pagesize, "SELECT a.id,a.ordercode,a.money,a.remark_a,a.time_a,a.state,b.price", sqlExceptSelect);
	}
	
	// 退款详情
	public static Map<String, Object> getRefundInfo(String ordercode){
		Map<String, Object> map = new HashMap<>();
		Record refund = Db.findFirst("select money,remark_a,time_a,remark_b,time_b,state from f_refund where ordercode=?", ordercode);
		Record order = Db.findFirst("select ordercode,name,tel,addr,reach,price,cycle,zhufu,songhua,jihui,yhje,yhfs,ctime,type,state,jh_list from f_order where ordercode=?", ordercode);
		List<Record> detaillist = Db.find("SELECT b.name,a.price,a.type FROM f_order_detail a LEFT JOIN f_flower_pro b ON a.fpid=b.id where a.ordercode=?", ordercode);
		// 优惠方式
		String yhfs = order.getStr("yhfs");
		String[] ids = yhfs.split(",");
		yhfs = null;
		if(Integer.parseInt(ids[0]) != 0){
			Record activity = Db.findById("f_activity", ids[0]);
			yhfs = activity.getStr("name");
		}
		if(Integer.parseInt(ids[1]) != 0){
			Record cash = Db.findFirst("SELECT b.money,b.benefit,c.name FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id LEFT JOIN f_cashtheme c ON b.tid=c.id where a.id=?", ids[1]);
			if(yhfs == null){
				yhfs = cash.getStr("name") + ",满" + cash.getDouble("benefit") + "减" + cash.getDouble("money");
			}else{
				yhfs += "、" + cash.getStr("name") + ",满" + cash.getDouble("benefit") + "减" + cash.getDouble("money");
			}
		}
		order.set("yhfs", yhfs);
		map.put("refund", refund);
		map.put("order", order);
		map.put("detaillist", detaillist);
		return map;
	}
	
	// 退款处理
	public static boolean refundAction(String ordercode, double money){
		boolean result = false;
		result = Db.update("update f_refund set money=?,time_b=?,state=1 where ordercode=?", money, new Date(),ordercode)==1?true:false;
		if(result){
			Record record = Db.findFirst("SELECT a.id,a.isbuy FROM f_account a LEFT JOIN f_order b ON a.id = b.aid WHERE b.ordercode =?", ordercode);
			if(record.getInt("isbuy")==1){
				result = Db.update("update f_account set isbuy=0 where id=?", record.getInt("id"))==1?true:false;
			}
		}
		return result;
	}
	
	// 支付成功-修改订单状态
	public static boolean paySuccess(String ordercode){
		boolean result = false;
		Record order = Db.findFirst("select id,aid,type from f_order where state=0 and ordercode=?", ordercode);
		if(order != null){
			order.set("state", 1);
			if(Db.update("f_order", order)){
				result = true;
				int aid = order.getInt("aid");
				for(int i = 0; i<seedType.buy.point; i++){	// 付款成功送花籽
					Record seed = new Record();
					seed.set("aid", aid);
					seed.set("send", 1);
					seed.set("type", seedType.buy.name());
					seed.set("remarks", seedType.buy.name);
					seed.set("ctime", new Date());
					Db.save("f_flowerseed", seed);
				}
				seed(aid, ordercode);
				
				//修改首单用户状态 和 首单状态
				Record member = Db.findById("f_account", aid);
				if(member.getInt("isbuy") == 0){
					member.set("isbuy", 1);
					order.set("ishas", 0);
					List<Record> cashlist = Db.find("SELECT a.ltime,b.id FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id=b.tid WHERE a.id=2 and a.ltime>0 and b.state=0");
					for(Record cashsd : cashlist){
						Record c = new Record();
						c.set("aid", aid);
						c.set("cid", cashsd.getInt("id"));
						c.set("code", "2222");
						Calendar now = Calendar.getInstance();
						c.set("time_a", now.getTime());
						now.add(Calendar.DAY_OF_MONTH, cashsd.getInt("ltime"));
						c.set("time_b", now.getTime());
						c.set("state", 1);
						c.set("origin", 2);
						Db.save("f_cash", c);
					}
					// 邀请好友送花票，好友首单购买成功，则给邀请人送花票
					if(member.get("tjid")!= null){
						String tjid = member.get("tjid");
						String typeid = tjid.substring(0,1);
						if(Integer.parseInt(typeid) == 1){
							int tjaid = Integer.parseInt(tjid.substring(2));
							List<Record> invcashlist = Db.find("SELECT a.ltime,b.id FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id=b.tid WHERE a.id=3 and a.ltime>0 and b.state=0");
							for(Record cashsd : invcashlist){
								Record d = new Record();
								d.set("aid", tjaid);
								d.set("cid", cashsd.getInt("id"));
								d.set("code", "3333");
								Calendar now = Calendar.getInstance();
								d.set("time_a", now.getTime());
								now.add(Calendar.DAY_OF_MONTH, cashsd.getInt("ltime"));
								d.set("time_b", now.getTime());
								d.set("state", 1);
								d.set("origin", 3);
								Db.save("f_cash", d);
							}
						}
					}
					// 订阅-首单送花瓶
					if(order.getInt("type") == 1){
						Integer fpid = Db.queryInt("SELECT gid FROM f_activity WHERE id = 1");
						if(fpid != null && fpid != 0){
							Record fpro = new Record();
							fpro.set("ordercode", ordercode);
							fpro.set("fpid", fpid);
							fpro.set("price", 0);
							fpro.set("type", 3);
							Db.save("f_order_detail", fpro);
						}
					}
				}else if(member.getInt("isbuy") == 1){
					member.set("isbuy", 2);	
				}
				Db.update("f_account", member);
				Db.update("f_order", order);
			}
		}
		return result;
	}
	
	public static void seed(int aid, String ordercode){
 		Record account = Db.findById("f_account", aid);
 		List<Record> list = Db.find("SELECT a.type,c.name FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id where a.ordercode=? and a.aid=? ORDER BY b.type ASC", ordercode, aid);
 		Record o = Db.findFirst("select reach,isvase,cycle,price,ctime from f_order where ordercode=?", ordercode);
 		String product = new String();//商品信息
 		String price = String.valueOf(o.getDouble("price"));//商品价格
 		Date cd = o.getDate("ctime");
 		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
 		String ctime = sdf.format(cd);//购买时间
 		String cycle = String.valueOf(o.getInt("cycle"));//配送次数
 		String firstDate = DeliveryDateUtil.getDateByOrder(o.getInt("reach"), o.getDate("ctime"));//首次送达时间
 		String vase_str = o.getInt("isvase")==0?"无花瓶":"有花瓶";//有无花瓶
 		if(account!=null && list.size()>0){
 			for(int i=0;i<list.size();i++){
 				if(i==0){
 					product = list.get(i).getStr("name");
 				}else{
 					product += "," + list.get(i).getStr("name");
 				}
 			}
			ApiResult result = TemplateMsgApi.send(TemplateData.New()
		    // 消息接收者
		    .setTouser(account.getStr("openid"))
		    // 模板id
		    .setTemplate_id(WeixinApiCtrl.gettemplateId())
		    .setTopcolor("#eb414a")
		    .setUrl(Constant.getHost + "/account/center")
		    .add("name", ""+product+"\n商品价格："+price+"元\n购买时间："+ctime+"", "#333")
		    .add("remark", "\n小主，我们已收到了您的订单哦，总共配送"+cycle+"次，鲜花首次送达日期"+firstDate+"，"+vase_str+"。改地址，改时间，查看订单，物流信息，花票等请点击菜单：为您服务》会员中心", "#888")
		    .build());
			 WeixinApiCtrl.sendTemplateMsg(result.getJson());
 		}
 	}
	
	// 判断花票是否适合推送
	public static boolean pushvalid(){
		boolean result = false;
		// 本月推送数量
		Number count = Db.queryNumber("SELECT COUNT(1) FROM f_cashtheme WHERE DATE_FORMAT(ptime,'%Y-%m') = DATE_FORMAT(CURDATE(),'%Y-%m')");
		if(count.intValue() < 4){
			result = true;
		}
		return result;
	}
	
	//生成4位随机数
	public static String getCode(){
		Random r = new Random();
		StringBuffer vc = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			String ch = String.valueOf(r.nextInt(10));
			vc.append(ch);
		}
		return vc.toString();
	}
	
	//获得物流公司
	public static List<Record> getWuliu(){
		 return Db.find("select name,code from f_express group by name,code");
	}
	
	//更换订单产品
	public static boolean changeproduct(Integer pid,Integer orderid){
		boolean result = false;
		if(!"".equals(pid) && pid!=null && !"".equals(orderid) && orderid!=null){
			int us = Db.update("update f_order_pro set pid =? where oid=? and type = 0", pid,orderid);
			return us==1?true:false;
		}else{
			return result;
		}
	}
	
	//获得分享订单列表
	public static Page<Record> getShareList(int pageno, int pagesize, int time,	String ordercode){
		String sqlExceptSelect = "FROM f_share a LEFT JOIN f_order b ON a.ordercode = b.ordercode LEFT JOIN f_order_detail c ON a.ordercode = c.ordercode LEFT JOIN f_flower_pro d ON c.fpid = d.id WHERE c.type=1";
		if(time != 0)
			sqlExceptSelect += " and b.reach=" + time;
		if(ordercode != null)
			sqlExceptSelect += " and a.ordercode like '%" + ordercode + "%'";
		sqlExceptSelect += " order by a.id desc";
		return Db.paginate(pageno, pagesize, "SELECT a.ordercode,d.name,a.name AS receiver,b.jihui,b.color,a.cycle,a.ocount,d.ctime,b.type,b.reach,a.ocount ", sqlExceptSelect);
	}
	
	// 分享订单详情
	public static Map<String, Object> getShareInfo(String ordercode){
		Map<String, Object> map = new HashMap<>();
		Record order = Db.findFirst("select a.ordercode,c.name,c.tel,c.addr,a.reach,a.price,a.cycle,a.zhufu,a.songhua,a.jihui,a.yhje,a.yhfs,a.ctime,a.type,a.state,a.jh_list,a.jh_color,b.title,c.ocount,c.ctime,c.gtime "
				+ "from f_order a left join f_idoneity b on a.szdx = b.id left join f_share c on a.ordercode = c.ordercode where a.ordercode=?", ordercode);
		List<Record> detaillist = Db.find("SELECT b.name,a.price,a.type,a.fpid FROM f_order_detail a LEFT JOIN f_flower_pro b ON a.fpid=b.id where a.ordercode=?", ordercode);
		List<Record> flowerSjlist = Db.find("select id,name from f_flower where state = 1");
		// 优惠方式
		String yhfs = order.getStr("yhfs");
		String[] ids = yhfs.split(",");
		yhfs = null;
		if(Integer.parseInt(ids[0]) != 0){
			Record activity = Db.findById("f_activity", ids[0]);
			yhfs = activity.getStr("name");
		}
		if(Integer.parseInt(ids[1]) != 0){
			Record cash = Db.findFirst("SELECT b.money,b.benefit,c.name FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id LEFT JOIN f_cashtheme c ON b.tid=c.id where a.id=?", ids[1]);
			if(yhfs == null){
				yhfs = cash.getStr("name") + ",满" + cash.getDouble("benefit") + "减" + cash.getDouble("money");
			}else{
				yhfs += "、" + cash.getStr("name") + ",满" + cash.getDouble("benefit") + "减" + cash.getDouble("money");
			}
		}
		String jh_list = order.getStr("jh_list");
		String jihuis = Db.queryStr("select group_concat(NAME) from f_flower_type where id in ("+jh_list+")");
		order.set("jihui", jihuis);
		String jh_color = order.getStr("jh_color");
		String colors = Db.queryStr("select group_concat(NAME) from f_color where id in ("+jh_color+")");
		order.set("colors", colors);
		List<Record> picilist = Db.find(" SELECT a.code,a.number,c.fname from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
				                 + "LEFT JOIN f_product c on b.pid = c.id where fname is not null and a.state>=2 and ordercode =? order by code",ordercode);
		Record picivase = Db.findFirst("select a.code,a.number,c.name from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
								  + "LEFT JOIN f_flower_pro c on b.pid = c.id  where a.state>=2 and b.type=1 and a.ordercode = ?",ordercode);
		map.put("picilist", picilist);
		map.put("picivase", picivase);
		order.set("yhfs", yhfs);
		map.put("order", order);
		map.put("detaillist", detaillist);
		map.put("flowerSjlist", flowerSjlist);
		return map;
	}
	
	// 获得递推人员
	public static Page<Record> getSpreadList(int pageno, int pagesize, int state, String number, String name, String tel, String time_a, String time_b){
		String sqlExceptSelect = " FROM  f_spread where 1=1";
		if(state != 9){
			sqlExceptSelect += " and state ="+state;
		}
		if(number!= null){
			sqlExceptSelect += " and number like '%"+number+"%'";
		}
		if(name!= null){
			sqlExceptSelect += " and name like '%"+name+"%'";
		}
		if(tel!= null){
			sqlExceptSelect += " and tel like '%"+tel+"%'";
		}
		Page<Record> page = Db.paginate(pageno, pagesize, "SELECT id,number,name,sex,tel,address,area,state", sqlExceptSelect);
		for (Record sp : page.getList()) {
			SimpleDateFormat sdft = new SimpleDateFormat("yyyy年MM月");
			List<Record> speadlist = null;
			String timearea = "";
			if(time_a == null && time_b == null){
				speadlist = Db.find("SELECT SUBSTRING(tjid, 3) AS sid,COUNT(a.ordercode) as num FROM f_order a LEFT JOIN f_account b ON a.aid = b.id "
						+ " WHERE a.state IN (1,2,3) AND SUBSTRING(b.tjid,1,1) = 2 GROUP BY SUBSTRING(tjid, 3)");
			}else{
				if(time_a != "" && "".equals(time_b)){
					timearea += " and a.ctime >='"+time_a+"'";
				}else if("".equals(time_a) && time_b != ""){
					timearea += " and a.ctime <='"+time_b+" 23:59:59'";
				}else if(time_a != "" &&time_b != ""){
					timearea += " and a.ctime between '"+time_a+"' and '"+time_b+" 23:59:59'";
				}
				speadlist = Db.find("SELECT SUBSTRING(tjid, 3) AS sid,COUNT(a.ordercode) as num FROM f_order a LEFT JOIN f_account b ON a.aid = b.id "
						+ " WHERE a.state IN (1,2,3) AND SUBSTRING(b.tjid,1,1) = 2 "+timearea+" GROUP BY SUBSTRING(tjid, 3)");
			}
			for (Record spread : speadlist) {
				if(Integer.parseInt(spread.getStr("sid")) == sp.getInt("id")){
					sp.set("tgnum", spread.get("num"));
				}
			}
			List<Record> memlist = Db.find("SELECT SUBSTRING(tjid, 3) AS sid,COUNT(id) as tgmem FROM f_account a WHERE SUBSTRING(tjid,1,1) = 2 "+timearea+" GROUP BY SUBSTRING(tjid, 3)");
			for (Record mem : memlist) {
				if(Integer.parseInt(mem.getStr("sid")) == sp.getInt("id")){
					sp.set("tgmem", mem.get("tgmem"));
				}
			}
		}
		return page;
	}
	
	// 冻结人员
	public static boolean freezeSpread(int id, int state){
		Record record = new Record();
		record.set("state", state);
		record.set("id", id);
		return Db.update("f_spread", record);
	}
	
	// 设置分组
	public static boolean updateMemGroup(int id, int gid){
		HttpKit.get(Constant.getHost + "/weixin/editUserGroup/"+id+"-"+gid);
		Record record = new Record();
		record.set("gid", gid);
		record.set("id", id);
		return Db.update("f_account", record);
	}
	
	//获得色系列表
	public static Page<Record> getColor(int pageno, int pagesize, String fcolor){
		String sqlExceptSelect = " from f_color a where 1=1";
		if(fcolor != ""){
			sqlExceptSelect += " and a.name like '%"+ fcolor +"%'";
		}
		return Db.paginate(pageno, pagesize, "select id,name,jh", sqlExceptSelect);
	}
	
//	public static Page<Record> getColor(int pageno, int pagesize){
//		return Db.paginate(pageno, pagesize, "select id,name,jh", " from f_color where 1=1");
//	}
	
	public static Page<Record> getColor(int pageno, int pagesize){
		return getColor(pageno, pagesize, "");
	}
	
	
	// 复制粘贴批次产品
	public static Map<String, Object> copypasteCode(String copycode, int type){
		responseMap = new HashMap<>();
		boolean R = false;
		map = DeliveryDateUtil.makeCode();
		Msg = "复制失败";
		Copycode = copycode;
		Type = type;
		if((boolean)map.get("result")){
			R = Db.tx(new IAtom() {
					boolean result = false;
					@Override
					public boolean run() throws SQLException {
						Number num = Db.queryNumber("SELECT COUNT(1) FROM f_product WHERE CODE = ? AND TYPE = ?", Copycode, Type);
						if(num.intValue() > 0){
							if(Copycode.equals(map.get("datecode"))){
								Msg = "不能复制当前批次";
								return result;
							}else{
								// 更新产品表f_product
								int num_1 = Db.update("INSERT INTO f_product(CODE,TYPE,fname,sname,price,fpid,iid) "
										+ "SELECT ?, TYPE, fname, sname, price, fpid,iid FROM f_product WHERE CODE = ? AND TYPE = ?", map.get("datecode"), Copycode, Type);
								if(num_1 > 0){
									// 更新产品明细表 f_product_info
									int num_2 = Db.update("INSERT INTO f_product_info(pid, fid, num) "
											+ "SELECT c.id,a.fid,a.num FROM f_product_info a LEFT JOIN  f_product b ON a.pid = b.id LEFT JOIN f_product c ON b.fname = c.fname AND b.sname = c.sname "
											+ "WHERE b.CODE = ? AND c.code = ? AND b.TYPE = ?", Copycode, map.get("datecode"), Type);
									if(num_2 > 0){
										Db.update("UPDATE f_product SET fname = REPLACE(fname,'（"+map.get("datecode")+"）',''), sname = REPLACE(sname,'（"+map.get("datecode")+"）','')  WHERE CODE = ? and type = ?", map.get("datecode"), Type);
										Db.update("UPDATE f_product SET fname = CONCAT(fname, '（"+map.get("datecode")+"）'), sname = CONCAT(sname, '（"+map.get("datecode")+"）')  WHERE CODE = ? and type = ?", map.get("datecode"), Type);
										Msg = "复制成功";
										result = true;
										return result;
									}else{
										Msg = "复制的批次中的产品没有花材";
										return result;
									}
								}else{
									Msg = "复制的批次中没有产品";
									return result;
								}
							}
						}else{
							Msg = "复制批次不存在";
							return result;
						}
					}
				});
		}else{
			Msg = "不在操作时间内";
		}
		responseMap.put("Msg", Msg);
		responseMap.put("R", R);
		return responseMap;
	}
	
	// 客诉补单 验证订单是否存在
	public static boolean validateorder(String code){
		Number rNum = Db.queryNumber("select count(1) from f_order where ordercode =?", code);
		return rNum.intValue()==0?false:true;
	}
	
	// 订单花瓶
	public static Integer getVase(String code){
		return Db.queryInt("SELECT fpid FROM f_order_detail where type = 2 and ordercode =?", code);
	}
	
	// 花瓶
	public static List<Record> getVases(){
		return Db.find("SELECT id,name FROM f_flower_pro WHERE ptid = 4 AND state = 0");
	}
	
	// 订单商品
	public static Integer getFlowerPro(String code){
		return Db.queryInt("SELECT fpid FROM f_order_detail where type = 1 and ordercode =?", code);
	}
	
	// 商品
	public static List<Record> getFlowerPros(){
		return Db.find("SELECT id,name FROM f_flower_pro");
	}
	
	// 创建客诉补单
	public static boolean createorderks(String ordercode, String aid, int pid, Integer vase,String area,String address, int reach, 
			  int cycle, String zhufu, String songhua, String jh_list, String jhcolor_list, int type, String recommend, Integer szdx, Integer cash, Integer activity, Double yh, String name, String tel){
		OrderCode = ordercode;
		Aid = aid;
		Pid = pid;
		Vase = vase;
		Area = area;
		Address = address;
		Reach = reach;
		Cycle = cycle;
		Zhufu = zhufu;
		Songhua = songhua;
		jh_List = jh_list;
		jhColor_List = jhcolor_list;
		TType = type;
		Recommend = recommend;
		Szdx = szdx;
		Cash = cash;
		Activity = activity;
		Yh = yh;
		Name = name;
		Tel = tel;
		
		pro_Flower = Db.findById("f_flower_pro", Pid);
		Order = new Record();	//订单对象
		boolean R = false;
		R = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean result = false;
				Order.set("ordercode", getorderCode());
				Order.set("aid", Aid);
				Order.set("addr", getAddressArea(Area) + Address);	// 收货人地址
				Order.set("name", Name);	// 收货人姓名
				Order.set("tel", Tel);	// 收货人电话
				Order.set("reach", Reach);		// 送达时间(1周一/2周六)
				Order.set("price", 0.00);		// 总价
			
				Order.set("cycle", Cycle);		// 周期
				if(Vase != null){
					Order.set("isvase", 1);		// 购买花瓶
				}
				Order.set("zhufu", "".equals(Zhufu)?null:Zhufu);		// 祝福卡
				Order.set("songhua", "".equals(Songhua)?null:Songhua);	// 赠花人
				Order.set("jh_list", "".equals(jh_List)?null:jh_List);	// 忌讳的花
				Order.set("jh_color", "".equals(jhColor_List)?null:jhColor_List);	// 忌讳的花
				if(!"".equals(jh_List)){
					Order.set("jihui", Db.queryStr("select group_concat(NAME) from f_flower_type where id in ("+jh_List+")"));// 忌讳的花材分类
				}
				if(!"".equals(jhColor_List)){
					Order.set("color", Db.queryStr("select group_concat(NAME) from f_color where id in ("+jhColor_List+")"));// 忌讳的色系
				}
				Order.set("yhje", 0.00);		// 优惠金额
				Order.set("yhfs", "0,0");		// 优惠方式
				Order.set("ctime", new Date());	// 下单日期
				Order.set("type", TType);		// 商品类型(订阅/赠送/周边/兑换)
				Order.set("state", 1);	// 客诉补单直接为服务中
				Order.set("szdx", Szdx);		// 适赠对象ID
				Order.set("isks", 1);		// 对应原订单
				Order.set("remark", OrderCode);		// 对应原订单
				// 保存订单
				result = Db.save("f_order", Order);
				if(result){
					// 订单订购明细-主件(花束or花边好物)
					Record order_detail_flower = new Record();
					order_detail_flower.set("ordercode", Order.getStr("ordercode"));
					order_detail_flower.set("fpid", Pid);
					order_detail_flower.set("price", pro_Flower.getDouble("price"));
					order_detail_flower.set("type", 1);
					result = Db.save("f_order_detail", order_detail_flower);
					
					if(Vase != null){
						Record pro_vase = Db.findById("f_flower_pro", Vase);
						// 订单订购明细-附件(花瓶)
						Record order_detail_vase = new Record();
						order_detail_vase.set("ordercode", Order.getStr("ordercode"));
						order_detail_vase.set("fpid", Vase);
						order_detail_vase.set("price", pro_vase.getDouble("price"));
						order_detail_vase.set("type", 2);
						result = Db.save("f_order_detail", order_detail_vase);
					}
				}
				return result;
			}
		});
		return R;
	}
	// 生成订单编号
	public static String getorderCode(){
		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(now.getTime());
	}
	// 获取收货地址区域
	public static String getAddressArea(String area){
		List<Record> arealist = Db.find("select name from f_area where id in (" + area + ") ORDER BY FIELD (id, " + area + ")");
		String areaname = new String();
		for(Record name:arealist){
			areaname += name.getStr("name")+"-";
		}
		return areaname;
	}
	
	//撤销退款
	public static boolean restorefund(String ordercode){
		boolean result = false;
		int state = Db.queryInt("SELECT state FROM f_refund WHERE ordercode =?", ordercode);
		if(state!=2){
			int num = Db.update("UPDATE f_refund SET state = 2 WHERE ordercode =?", ordercode);
			result = num==1?true:false;
			if(result){
				Db.update("update f_order set state = 1 where ordercode=?", ordercode);
				Record record = Db.findFirst("SELECT a.id,a.isbuy FROM f_account a LEFT JOIN f_order b ON a.id = b.aid WHERE b.ordercode =?", ordercode);
				if(record.getInt("isbuy")==0){
					Db.update("update f_account set isbuy=1 where id=?", record.getInt("id"));
				}
			}
		}
		return result;
	}
}