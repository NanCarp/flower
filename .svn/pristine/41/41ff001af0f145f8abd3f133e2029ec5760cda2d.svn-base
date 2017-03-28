package com.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.api.PaymentApi.TradeType;
import com.util.Constant;
import com.util.Constant.orderState;
import com.util.Constant.seedType;
import com.util.DeliveryDateUtil;
import com.util.Signature;
import com.util.XMLParser;

/**
 * @Desc 后台公共数据接口
 * */
public class FCDao {
	static Map<String , Object> resultMap;	//创建订单结果
	static int Pid;
	static Integer Vase;
	static int addressId;
	static int Reach;
	static int Cycle;
	static String Zhufu;
	static String Songhua;
	static String jh_List;
	static String jhColor_List;
	static int Type;
	static String Recommend;
	static Integer Szdx;
	static Integer Cash;
	static Integer Activity;
	static Double Yh;
	static Record Account;
	static Record pro_Flower;
	static int isBuy;
	static Record Order;
	
	// 花边好物列表
	public static List<Record> getAround(int isbuy){
		String typeStr = "";
		if(isbuy == 0){
			typeStr = " and b.type in (0,1)";
		}else{
			typeStr = " and b.type in (0,2)";
		}
		return Db.find("SELECT b.id,b.name,b.imgurl,b.describe,b.price FROM f_dictionary a LEFT JOIN f_flower_pro b ON a.code_value=b.ptid "
				+ "where b.state=0 AND a.code_value>=4" + typeStr);
	}
	// 花边好物详情
	public static Record getAroundInfo(int pid){
		return Db.findFirst("SELECT a.id,a.name,a.imgurl,a.itinfo1,a.itinfo2,a.itinfo3,a.itinfo4,a.itinfo5,"
				+ "a.describe,a.price FROM f_flower_pro a WHERE ptid>=4 AND id=?", pid);
	}
	// 获取商品详情
	public static Record getProductInfo(int ptid){
		Record pro = Db.findFirst("SELECT b.id,b.name,b.imgurl,b.itinfo1,b.itinfo2,b.itinfo3,b.itinfo4,b.itinfo5,"
				+ "b.describe,b.price FROM f_dictionary a LEFT JOIN f_flower_pro b ON a.code_value=b.ptid where a.code_value=?", ptid);
		return pro;
	}
	// 获取商品列表
	public static List<Record> getProducts(int ptid,int isbuy){
		String typeStr = "";
		if(ptid==4){
			if(isbuy == 0){
				typeStr = " and b.type in (0,1)";
			}else{
				typeStr = " and b.type in (0,2)";
			}
		}
		return Db.find("SELECT b.id,b.state,b.name,b.imgurl,b.itinfo1,b.itinfo2,b.itinfo3,b.itinfo4,b.itinfo5"
				+ ",b.describe,b.price FROM f_dictionary a LEFT JOIN f_flower_pro b ON a.code_value=b.ptid where b.state !=1 AND a.code_value=?" + typeStr, ptid);
	}
	// 获得地区数据
	public static String getArea(){
		List<Record> list = Db.find("SELECT a.id,a.name,a.pid,b.pid AS ppid FROM f_area a LEFT JOIN f_area b ON a.pid=b.id");
		return JsonKit.toJson(list);
	}
	// 获取我的地址
	public static List<Record> getAddress(int aid){
		List<Record> list = Db.find("select id,name,tel,area,addr,state from f_address where aid = ?", aid);
		for(Record address : list){
			String addr = Db.queryStr("SELECT GROUP_CONCAT(NAME ORDER BY pid ASC SEPARATOR '-') as addr FROM f_area WHERE id IN ("+ address.getStr("area") +")");
			address.set("addr", addr+"-"+address.getStr("addr"));
		}
		return list;
	}
	// 保存地址
	public static boolean saveAddress(Integer id, Integer state, int aid, String name, String tel, String area, String addr, int give){
		if(id == null){
			Number count = Db.queryNumber("SELECT COUNT(1) FROM f_address WHERE aid = ?", aid);
			if(count.intValue() >= 10){
				return false;
			}
		}
		Record address = new Record();
		if(state == 1){
			address.set("state", state);
			Db.update("update f_address set state=0 where aid=?", aid);
		}
		address.set("aid", aid);
		address.set("name", name);
		address.set("tel", tel);
		address.set("area", area);
		address.set("addr", addr);
		address.set("give", give);
		if(give == 2){
			address.set("state", 0);
		}
		if(id == null){
			return Db.save("f_address", address);
		}else{
			address.set("id", id);
			return Db.update("f_address", address);
		}
	}
	// 设置默认地址
	public static boolean setDefault(int id, int aid){
		Db.update("update f_address set state=0 where aid=?", aid);
		int count = Db.update("update f_address set state=1 where id=?", id);
		return count==1?true:false;
	}
	// 购买-获取默认地址
	public static Record getDefaultAddress(int aid, int give, Integer addrid){
		Record address = null;
		String addr = new String();
		if(addrid == null){
			List<Record> list = Db.find("select id,name,tel,area,addr,state from f_address where aid = ? and give = ?", aid, give);
			if(list.size() > 0){
				for(Record r : list){
					if(r.getInt("state") == 1){
						// 如果有默认地址则取默认地址
						address = r;
						addr = Db.queryStr("SELECT GROUP_CONCAT(NAME ORDER BY pid ASC SEPARATOR '-') as addr FROM f_area WHERE id IN ("+ address.getStr("area") +")");
						address.set("addr", addr+"-"+address.getStr("addr"));
					}
				}
				if(address == null){
					address = list.get(0);	// 默认取第一条
					addr = Db.queryStr("SELECT GROUP_CONCAT(NAME ORDER BY pid ASC SEPARATOR '-') as addr FROM f_area WHERE id IN ("+ list.get(0).getStr("area") +")");
					address.set("addr", addr+"-"+address.getStr("addr"));
				}
			}
		}else{
			address = Db.findFirst("select id,name,tel,area,addr,state from f_address where id = ? and aid = ? and give = ?", addrid, aid, give);
			addr = Db.queryStr("SELECT GROUP_CONCAT(NAME ORDER BY pid ASC SEPARATOR '-') as addr FROM f_area WHERE id IN ("+ address.getStr("area") +")");
			address.set("addr", addr+"-"+address.getStr("addr"));
		}
		return address;
	}
	// 购买-选择收货地址
	public static List<Record> getAddressForType(int aid, int give){
		List<Record> list = Db.find("select id,name,tel,area,addr,state from f_address where aid = ? and give = ?", aid, give);
		for(Record address : list){
			String addr = Db.queryStr("SELECT GROUP_CONCAT(NAME ORDER BY pid ASC SEPARATOR '-') as addr FROM f_area WHERE id IN ("+ address.getStr("area") +")");
			address.set("addr", addr+"-"+address.getStr("addr"));
		}
		return list;
	}
	// 获取所有收货地址
	public static List<Record> getAddresses(int aid){
		List<Record> list = Db.find("SELECT id,area,addr FROM f_address where aid =?", aid);
		for(Record address : list){
			String addr = Db.queryStr("SELECT GROUP_CONCAT(NAME ORDER BY pid ASC SEPARATOR '-') as addr FROM f_area WHERE id IN ("+ address.getStr("area") +")");
			address.set("addr", addr+"-"+address.getStr("addr"));
		}
		return list;
	}
	// 签到
	public static boolean signin(int aid){
		boolean result = false;
		Number num = Db.queryNumber("select count(1) from f_flowerseed where aid=? and type=? and ctime=curdate()", aid, seedType.sign.name());
		if(num.intValue() == 0){
			for(int i = 0; i<seedType.sign.point; i++){
				Record seed = new  Record();
				seed.set("aid", aid);
				seed.set("send", 1);
				seed.set("type", seedType.sign.name());
				seed.set("remarks", seedType.sign.name);
				seed.set("ctime", new Date());
				result = Db.save("f_flowerseed", seed);
			}
		}
		return result;
	}
	// 订单数量统计
	public static int[] orderCount(int aid){
		List<Record> list = Db.find("SELECT state FROM f_order WHERE aid = ?", aid);
		int a = 0;
		int b = 0;
		int c = 0;
		for(Record order : list){
			if(order.getInt("state")==orderState.STATE0.state){
				a++;
			}else if(order.getInt("state")==orderState.STATE1.state){
				b++;
			}else if(order.getInt("state")==orderState.STATE3.state){
				c++;
			}
		}
		int[] count = {a,b,c};
		return count;
	}
	// 花票列表
	public static Map<String, Object> getCashList(int aid){
		//state:0未领取/1未使用/2已使用
		Map<String, Object> map = new HashMap<>();
		List<Record> list1 = Db.find("SELECT a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id where a.aid=? and a.state=1 and CURDATE()>=a.time_a AND CURDATE()<=a.time_b", aid);
		List<Record> list2 = Db.find("SELECT a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id where a.aid=? and a.state=2", aid);
		List<Record> list3 = Db.find("SELECT a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id where a.aid=? and a.state=1 and CURDATE()>a.time_b", aid);
		List<Record> list4 = Db.find("SELECT a.id,a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id where a.aid=? and a.state=1 and CURDATE()>=a.time_a AND CURDATE()<=a.time_b", aid);
		map.put("list1", list1);	// 1未使用
		map.put("list2", list2);	// 2已使用
		map.put("list3", list3);	// 3已过期
		map.put("list4", list4);	// 4花票分享
		return map;
	}
	// 获得未使用的花票
	public static List<Record> getCashAble(int aid, double totalprice){
		return Db.find("SELECT a.id,a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id "
				+ "where a.aid=? and b.benefit<=? and a.state=1 and CURDATE()>=a.time_a AND CURDATE()<=a.time_b", aid, totalprice);
	}
	// 获得未使用的优惠最大的花票
	public static Record getMaxCash(int aid, double totalprice){
		return Db.findFirst("SELECT a.id,a.time_a,a.time_b,MAX(b.money) money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id "
				+ "where a.aid=? and b.benefit<=? and a.state=1 and CURDATE()>=a.time_a AND CURDATE()<=a.time_b", aid, totalprice);
	}
	// 领取花票
	public static List<Record> receiveCash(int aid){
		List<Record> cashlist = Db.find("SELECT b.id as cid,b.money,b.benefit FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id=b.tid WHERE a.id=(SELECT MAX(id) FROM f_cashtheme WHERE state=1) and b.state=0");
		for(Record cash : cashlist){
			Record mycash = Db.findFirst("select id,code,state from f_cash where aid=? and cid=?", aid, cash.getInt("cid"));
			if(mycash == null){
				mycash = new Record();
				mycash.set("aid", aid);
				mycash.set("cid", cash.getInt("cid"));
				mycash.set("code", getCode());
				Db.save("f_cash", mycash);
				cash.set("state", 0);
				cash.set("id", mycash.getLong("id"));
			}else{
				cash.set("state", mycash.getInt("state"));
				cash.set("id", mycash.getInt("id"));
			}
			cash.set("code", mycash.getStr("code"));
		}
		return cashlist;
	}
	// 我的种植花束
	public static Map<String, Object> getFlowerSeed(int aid){
		Map<String, Object> map = new HashMap<>();
		Number number = Db.queryNumber("SELECT count(1) FROM f_flowerseed WHERE aid=? AND DATE_ADD(ctime, INTERVAL 2 MONTH) >= CURDATE() and state = 0", aid);
		int count = number.intValue();	// 花籽总数
		int a = 0;	// 花籽
		int b = 0;	// 花苗	10个花籽
		int c = 0;	// 花束	39个花籽
		if(count > 0){
			c = count/39;
			b = (count%39)/10;
			a = count - c*39 - b*10;
		}
		map.put("a", a);
		map.put("b", b);
		map.put("c", c);
		map.put("count", count);
		return map;
	}
	// 保存绑定号码
	public static Map<String, Object> saveBinding(String number, String msgcode, String bindingcode, HttpSession session){
		Map<String, Object> responseMap = new HashMap<>();
		Record account = (Record) session.getAttribute("account");
		boolean result = false;
		String msg = new String();
		if(bindingcode == null){
			msg = "验证码错误";
		}else{
			if(bindingcode.equals(msgcode)){
				if(account.getStr("tel") == null){
					for(int i = 0; i<seedType.binding.point; i++){
						Record seed = new  Record();
						seed.set("aid", account.get("id"));
						seed.set("send", 1);
						seed.set("type", seedType.binding.name());
						seed.set("remarks", seedType.binding.name);
						seed.set("ctime", new Date());
						Db.save("f_flowerseed", seed);
					}
				}
				account.set("tel", number);
				account.set("isfans", 0);
				result = Db.update("f_account", account);
				msg = "保存成功";
			}else{
				msg = "验证码错误";
			}
		}
		responseMap.put("result", result);
		responseMap.put("msg", msg);
		return responseMap;
	}
	// 提交反馈
	public static boolean saveFeedback(String title, String info, int id){
		Record record = new Record();
		record.set("aid", id);
		record.set("title", title);
		record.set("info", info);
		record.set("ctime", new Date());
		return Db.save("f_feedback", record);
	}
	//提交评价
	public static boolean saveEvaluate(String id,int point,String descripte){
		Record record = new Record();
		record.set("ordercode", id);
		record.set("point", point);
		record.set("descripte", descripte);
		record.set("ctime", new Date());
		Db.update("update f_order set state = 3 where ordercode = ?", id);
		return Db.save("f_comment", record);
	}
	// 生活美学
	public static Page<Record> getEstheticsPage(int pageno, int pagesize){
		return Db.paginate(pageno, pagesize, "select id,title,imgurl,DATE_FORMAT(ctime,'%Y-%m-%d') as ctime", "FROM f_esthetics order by istop desc,id desc");
	}
	// 获取活动信息
	public static Record getActivity(Double price){
		return Db.findFirst("SELECT id,name,money,benefit FROM f_activity WHERE time_a < NOW() AND time_b > NOW() AND state=0 AND money <= ? ORDER BY money desc", price);
	}
	// 获取活动信息集合
	public static List<Record> getActivityList(){
		return Db.find("SELECT id,name,money,benefit FROM f_activity WHERE time_a < NOW() AND time_b > NOW() AND state=0 ORDER BY money desc");
	}
	// 计算订单总额
	public static double countPrice(double price, int cycle, Integer vaseid){
		double price1 = price * cycle;
		if(vaseid != null){
			price1 += Db.queryDouble("select price from f_flower_pro where id = ?", vaseid);
		}
		return price1;
	}
	// 创建订单
	public static Map<String, Object> createOrder(HttpServletRequest request, HttpSession session, int pid, Integer vase, int addressid, int reach, 
			  int cycle, String zhufu, String songhua, String jh_list, String jhcolor_list, int type, String recommend, Integer szdx, Integer cash, Integer activity, Double yh) 
			throws ParserConfigurationException, IOException, SAXException{
		Pid = pid;
		Vase = vase;
		addressId = addressid;
		Reach = reach;
		Cycle = cycle;
		Zhufu = zhufu;
		Songhua = songhua;
		jh_List = jh_list;
		jhColor_List = jhcolor_list;
		Type = type;
		Recommend = recommend;
		Szdx = szdx;
		Cash = cash;
		Activity = activity;
		Yh = yh;
		
		resultMap = new HashMap<>();
		Account = (Record) session.getAttribute("account");
		pro_Flower = Db.findById("f_flower_pro", pid);
		isBuy = Account.getInt("isbuy");
		Order = new Record();	//订单对象
		boolean R = false;
		R = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean result = false;
				
				Map<String, Object> priceMap = countPriceForOrder(Account.getInt("id"), Pid, Cycle, Vase, Cash, Activity, Yh);
				Order.set("ordercode", getOrderCode());
				Order.set("aid", Account.getInt("id"));
				Record address = Db.findById("f_address", addressId);
				Order.set("name", address.getStr("name"));	// 收货人姓名
				Order.set("tel", address.getStr("tel"));	// 收货人电话
				Order.set("addr", getAddressArea(address.getStr("area")) + address.getStr("addr"));	// 收货人地址
				Order.set("reach", Reach);		// 送达时间(1周一/2周六)
				Order.set("price", (double)priceMap.get("price"));		// 总价
				if((double)priceMap.get("price") <= 0){
					return result;
				}

				Order.set("cycle", Cycle);		// 周期
				if(Vase != null){
					Order.set("isvase", 1);		// 购买花瓶
				}
				Order.set("zhufu", "".equals(Zhufu)?null:Zhufu);		// 祝福卡
				Order.set("songhua", "".equals(Songhua)?null:Songhua);	// 赠花人
				Order.set("jh_list", "".equals(jh_List)?null:jh_List);	// 忌讳的花
				Order.set("jh_color", "".equals(jhColor_List)?null:jhColor_List);	// 忌讳的花
				if(!"".equals(jh_List)){
//					Order.set("jihui", Db.queryStr("select group_concat(NAME) from f_flower where id in ("+jh_List+")"));// 忌讳的花
					Order.set("jihui", Db.queryStr("select group_concat(NAME) from f_flower_type where id in ("+jh_List+")"));// 忌讳的花材分类
				}
				if(!"".equals(jhColor_List)){
					Order.set("color", Db.queryStr("select group_concat(NAME) from f_color where id in ("+jhColor_List+")"));// 忌讳的色系
				}
				Order.set("yhje", priceMap.get("benefit"));		// 优惠金额
				Order.set("yhfs", priceMap.get("yhfs"));		// 优惠方式
				Order.set("ctime", new Date());	// 下单日期
				Order.set("type", Type);		// 商品类型(订阅/赠送/周边/兑换)
				if(Type == 4){
					Order.set("state", 1);	// 兑换的订单直接为服务中
				}
				Order.set("szdx", Szdx);		// 适赠对象ID
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
		if(R){
			List<Record> detaillist = Db.find("SELECT a.cycle,a.price AS totalprice,c.imgurl,c.name,b.price,b.type FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id WHERE a.ordercode=?", Order.getStr("ordercode"));
			// 微信统一下单
			String xml = FCDao.wxPushOrder(pro_Flower.getStr("name"), Order.getStr("ordercode"), Order.getDouble("price"), getRemortIP(request), Account.getStr("openid"));
			resultMap = XMLParser.getMapFromXML(xml);
			resultMap.put("detaillist", detaillist);
			resultMap.put("ordercode", Order.getStr("ordercode"));
		}else{
			Account.set("isbuy", isBuy);
		}
		resultMap.put("result", R);
		return resultMap;
	}
	
	// 创建兑换订单
	public static void exccreateOrder(HttpSession session, int pid, int addressid, int reach, String jh_list, String jhcolor_list, int cycle,int type){
		Record account = (Record) session.getAttribute("account");
		Record pro_flower = Db.findById("f_flower_pro", pid);
		Record order = new Record();
		order.set("ordercode", getOrderCode());
		order.set("aid", account.get("id"));
		order.set("price", pro_flower.get("price"));
		Record address = Db.findById("f_address", addressid);
		order.set("name", address.getStr("name"));	// 收货人姓名
		order.set("tel", address.getStr("tel"));	// 收货人电话
		order.set("addr", getAddressArea(address.getStr("area")) + address.getStr("addr"));	// 收货人地址
		order.set("reach", reach);		// 送达时间(1周一/2周六)
		order.set("jh_list", "".equals(jh_list)?null:jh_list);	// 忌讳的花
		order.set("jh_color", "".equals(jhcolor_list)?null:jhcolor_list);	// 忌讳的色系
		if(!"".equals(jh_list)){
			order.set("jihui", Db.queryStr("select group_concat(NAME) from f_flower_type where id in ("+jh_list+")"));// 忌讳的花材分类
		}
		if(!"".equals(jhcolor_list)){
			order.set("color", Db.queryStr("select group_concat(NAME) from f_color where id in ("+jhcolor_list+")"));// 忌讳的色系
		}
		order.set("cycle", cycle);		// 周期
		order.set("ctime", new Date());	// 下单日期
		order.set("type", 4);		// 商品类型(兑换)
		order.set("state", 1);		// 兑换订单成功直接去评价该订单 
		int[] yhfs = {0,0};
		order.set("yhfs",  yhfs[0]+","+yhfs[1]);   //兑换订单无优惠方式
		// 保存订单
		if(Db.save("f_order", order)){
			Record order_detail_flower = new Record();
			order_detail_flower.set("ordercode", order.getStr("ordercode"));
			order_detail_flower.set("fpid", pid);
			order_detail_flower.set("price", pro_flower.getDouble("price"));
			order_detail_flower.set("type", 1);
			Db.save("f_order_detail", order_detail_flower);
		}
	}
	
	// 下单应付总额
	public static Map<String, Object> countPriceForOrder(int aid, int pid, int cycle, Integer vaseid, Integer cash, Integer activity, Double yh){
		Map<String, Object> map = new HashMap<>();
		BigDecimal price = new BigDecimal(Db.queryDouble("select price * ? from f_flower_pro where id = ?", cycle, pid));//鲜花总价
		BigDecimal benefit = new BigDecimal(0.00);//优惠价格
		int[] yhfs = {0,0};
		if(vaseid != null){
			BigDecimal vaseprice = new BigDecimal(Db.queryDouble("select price from f_flower_pro where id = ?", vaseid));
			price = price.add(vaseprice);
		}
		yh = yh==null?0.00:yh;
		price = price.subtract(new BigDecimal(yh));//优惠总额，包括活动中的优惠
		if(activity != null){
			BigDecimal activity_money = new BigDecimal(Db.queryDouble("SELECT benefit FROM f_activity WHERE id=?", activity));
			benefit = benefit.add(activity_money);
			yhfs[0] = activity;
		}
		if(cash != null){
			Record mycash = Db.findFirst("SELECT money FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id WHERE a.aid=? AND a.id=? AND a.state=1", aid, cash);
			if(mycash != null){
				Db.update("update f_cash set state = 2 where id = ?", cash);
				price = price.subtract(new BigDecimal(mycash.getDouble("money")));
				benefit = benefit.add(new BigDecimal(mycash.getDouble("money")));
				yhfs[1] = cash;
			}
		}
		Double price_d = price.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		Double benefit_d = benefit.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		map.put("price", price_d<=0?0.01:price_d);				// 总额
		map.put("benefit", benefit_d);			// 优惠金额
		map.put("yhfs", yhfs[0]+","+yhfs[1]);	// 优惠方式
		return map;
	}
	
	// 生成订单编号
	public static String getOrderCode(){
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
	
	// 我的订单
	public static Page<Record> getMyOrder(int pageno, int pagesize, int aid, Integer state){
		String select = "SELECT a.ordercode,b.fpid,c.ptid,c.name,c.imgurl,b.price,a.cycle,a.reach,a.isvase,a.price AS totalprice,a.state,a.ctime,d.money,d.state as rstate,a.type,a.ocount ";
		String sqlExceptSelect = "FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id left join f_refund d on a.ordercode=d.ordercode WHERE a.aid=? AND b.type=1";
		Page<Record> page;
		if(state == 9){
			sqlExceptSelect += " order by a.id desc";
			page = Db.paginate(pageno, pagesize, select, sqlExceptSelect, aid);
		}else{
			sqlExceptSelect += " AND a.state=? order by a.id desc";
			page = Db.paginate(pageno, pagesize, select, sqlExceptSelect, aid, state);
		}
		for(Record order : page.getList()){
			int reach = order.getInt("reach");
			Date ctime = order.getDate("ctime");
			String firstDate = DeliveryDateUtil.getDateByOrder(reach, ctime);
			order.set("firstDate", firstDate);
		}
		return page;
	}
	
	// 订单详情
	public static Map<String, Object> getOrderInfo(int aid, String ordercode) throws ParseException{
		Map<String, Object> map = new HashMap<>();
		Record order = Db.findFirst("SELECT a.ocount,a.is_sy,a.sy_code,a.yhje,a.ordercode,b.fpid,c.name,a.name as receiver,a.tel,a.addr,a.cycle,a.reach,a.price AS totalprice,c.describe,b.price,a.isvase,a.jihui,a.color,a.songhua,a.zhufu,a.type,a.ctime,a.state,c.imgurl "+
				"FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id "+
				"where a.aid=? and a.ordercode=? and b.type=1",
				aid, ordercode);
		String FirstDate = DeliveryDateUtil.getDateByOrder(order.getInt("reach"), order.getDate("ctime"));
		order.set("firstDate", FirstDate);
		
		//该订单历史批次信息
		List<Record> picilist = Db.find("SELECT a.code,a.state,a.number,c.fname FROM f_order_info a LEFT JOIN f_order_pro b ON a.id = b.oid "
				+ "LEFT JOIN f_product c ON b.pid = c.id WHERE a.ordercode = ? AND b.type=0 AND a.state>=2 ORDER BY a.code", ordercode);
		Record picivase = Db.findFirst("SELECT a.code,a.number,c.name from f_order_info a LEFT JOIN f_order_pro b on a.id = b.oid "
					+ "LEFT JOIN f_flower_pro c on b.pid = c.id where a.state>=2 and b.type=1 and a.ordercode = ?", ordercode);
		if(picivase != null){
			for(Record pici : picilist){
				if(pici.getStr("number").equals(picivase.getStr("number"))){
					pici.set("fname", pici.getStr("fname")+","+picivase.getStr("name"));
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar now = Calendar.getInstance();
		Date firstDate = new Date();
		String sy_code = new String();
		Date sy_date = new Date();
		if(order.getStr("sy_code")!=null){
			sy_code = order.getStr("sy_code");// 顺延时间
			sy_date = sdf.parse(sy_code);
		}
		String fcode_1 = FirstDate.replace("-", "");// 根据下单时间  首次送达时间
		Date fdate_1 = sdf.parse(fcode_1);
		
		Date fdate_2 = new Date();
		if(order.getInt("ocount")!=0){
			now.setTime(fdate_1);
			now.add(Calendar.DAY_OF_MONTH, 7 * order.getInt("ocount"));
			fdate_2  = now.getTime(); // 根据首次送达时间算出 最近送达时间
		}
		
		Date fdate_3 = sdf.parse(DeliveryDateUtil.compare_Date(order.getInt("reach")).replace("-", "")); // 根据送达时间算出周几日期
		//首次送达时间
		if(picilist.size()>0){
			String firstCode = picilist.get(picilist.size()-1).getStr("code");
			firstDate = sdf.parse(firstCode);
			now.setTime(firstDate);
		}else{
			if(order.getInt("is_sy")==1 && (sy_date.getTime() - fdate_1.getTime())/(24*60*60*1000) < 14){
				now.setTime(sy_date);
			}else{
				now.setTime(fdate_1);
			}
		}
		//每个批次送达时间
		if(picilist.size()>0){
			int size = picilist.size()-1;
			int ocount = order.getInt("ocount");
			
			for (int i=size;i<order.getInt("cycle");i++) {
				if(i >= size+1){
					Record future = new Record();
					future.set("code", sdf.format(now.getTime()));
					picilist.add(future);
				}
				int n_reach = now.get(Calendar.DAY_OF_WEEK) - 1==1?1:2;
				if(order.getInt("is_sy")==1 && i+1==ocount && (sy_date.getTime() - fdate_2.getTime())/(24*60*60*1000) == 7){
					now.setTime(sy_date);
				}else if(order.getInt("is_sy")==1 && i==1 && (sy_date.getTime() - fdate_2.getTime())/(24*60*60*1000) == 14){
					now.setTime(sy_date);
				}else if((order.getInt("reach") != n_reach)){
					now.setTime(fdate_3);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 7);
				}
			}
		}else{
			for (int i=0 ;i<order.getInt("cycle");i++) {
				if(i >= 0){
					Record future = new Record();
					future.set("code", sdf.format(now.getTime()));
					picilist.add(future);
				}
				int n_reach = now.get(Calendar.DAY_OF_WEEK) - 1==1?1:2;
				if(order.getInt("is_sy")==1 && i==0 && (sy_date.getTime() - fdate_1.getTime())/(24*60*60*1000) == 14){
					now.setTime(fdate_1);
					now.add(Calendar.DAY_OF_MONTH, 14);
				}else if((order.getInt("reach") != n_reach)){
					now.setTime(fdate_3);
				}else{
					now.add(Calendar.DAY_OF_MONTH, 7);
				}
			}
		}
		if(order != null){
			map.put("order", order);
			map.put("cycle", order.get("cycle"));
			map.put("yhje", order.get("yhje"));
			map.put("type", order.get("type"));
			map.put("totalprice", order.get("totalprice"));
			map.put("picilist", picilist);
		}
		return map;
	}
	
	// 立即支付
	public static Map<String, Object> payForOrder(HttpServletRequest request, String openid, int aid, String ordercode) throws ParserConfigurationException, IOException, SAXException{
		Map<String, Object> map = new HashMap<>();
		Record order = Db.findFirst("select id,price from f_order where aid=? and ordercode=? and state=0", aid, ordercode);
		if(order != null){
			List<Record> detaillist = Db.find("SELECT a.cycle,a.price AS totalprice,c.imgurl,c.name,b.price,b.type FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id WHERE a.ordercode=?", ordercode);
			// 微信统一下单
			for(Record detail:detaillist){
				if(detail.getInt("type") == 1){
					String xml = FCDao.wxPushOrder(detail.getStr("name"), ordercode, order.getDouble("price"), getRemortIP(request), openid);
					map = XMLParser.getMapFromXML(xml);
				}
			}
			map.put("detaillist", detaillist);
		}
		return map;
	}
	
	// 取消订单
	public static boolean cancelOrder(int aid, String ordercode){
		boolean result = false;
		Record order = Db.findFirst("select id,yhfs from f_order where ordercode=? and aid=?", ordercode, aid);
		if(order != null){
			order.set("state", 5);
			result = Db.update("f_order", order);
			if(order.get("yhfs") != null){
				int index = order.getStr("yhfs").indexOf(",");
				int yid = Integer.parseInt(order.getStr("yhfs").substring(index + 1));
				Db.update("update f_cash set state = 1 where id =?", yid);
			}
		}
		return result;
	}
	
	// 退款申请
	public static Record refund(int aid, String ordercode){
		return Db.findFirst("SELECT a.ordercode,c.name,c.imgurl,b.price,a.cycle,a.reach,a.isvase,a.price AS totalprice FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id WHERE a.aid=? AND a.ordercode=? AND b.type=1", aid, ordercode);
	}
	
	// 赠送好友-获取信息
	public static Record getOrderToReceive(int aid, String ordercode){
		Record order = Db.findFirst("SELECT a.ordercode,c.name,c.imgurl "+
				"FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id "+
				"where a.aid=? and a.ordercode=? and b.type=1", aid, ordercode);
		return order;
	}
	
	// 赠送好友-验证与提交
	public static Map<String, Object> orderReveiveValidAndSave(int aid, String ordercode, int cycle){
		Map<String, Object> map = new HashMap<>();
		boolean result = false;
		String msg = new String();
		Record order = Db.findFirst("select id from f_order where ordercode=? and aid=?", ordercode, aid);
		if(order != null){
			Record share = Db.findFirst("select aid,code from f_share where ordercode = ?", ordercode);
			if(share != null){
				if(share.getInt("aid") == null){
					msg = "分享记录已存在，尚未领取，提取码:"+share.getStr("code");
				}else{
					msg = "分享记录已存在，并已成功领取";
				}
			}else{
				share = new Record();
				share.set("ordercode", ordercode);
				share.set("cycle", cycle);
				share.set("code", getCode());
				share.set("ctime", new Date());
				if(Db.save("f_share", share)){
					msg = "在右上角选择&lceil;发送给朋友&rfloor;进行分享，提取码:"+share.getStr("code");
					result = true;
				}else{
					msg = "操作失败";
				}
			}
		}else{
			msg = "订单不存在";
		}
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	// 领花-提取码验证
	public static boolean validCode(String ordercode, String code){
		Record share = Db.findFirst("select id from f_share where ordercode=? and code=?", ordercode, code);
		return share==null?false:true;
	}
	
	// 领花
	public static Map<String, Object> getOrderReceive(int aid, String ordercode, String code,String name, String tel, String area,String addr){
		Map<String, Object> map = new HashMap<>();
		boolean result = false;
		String msg = new String();
		Record share = Db.findFirst("select id,aid from f_share where ordercode=? and code=?", ordercode, code);
		if(share == null){
			msg = "提取码错误";
		}else{
			if(share.getInt("aid") == null){
				share.set("aid", aid);
				share.set("name", name);
				share.set("tel", tel);
				String address = FCDao.getAddressArea(area);
				share.set("addr", address+addr);
				share.set("gtime", new Date());
				if(Db.update("f_share", share)){
					result = true;
					msg = "领取成功";
				}else{
					msg = "领取失败";
				}
			}else{
				msg = "领取失败，此赠送已被领取";
			}
		}
		map.put("result", result);
		map.put("msg", msg);
		return map;
	}
	
	// 保存退款申请
	public static boolean saveRefund(int aid, String ordercode, String remark){
		boolean result = false;
		// 服务中的订单可退款
		Record order = Db.findFirst("select id from f_order where aid=? and ordercode=? and state=1", aid, ordercode);
		if(order != null){
			order.set("state", Constant.orderState.STATE4.state);
			if(Db.update("f_order", order)){
				Record refund = new Record();
				refund.set("ordercode", ordercode);
				refund.set("remark_a", remark);
				refund.set("time_a", new Date());
				Record fo = Db.findFirst("select id from f_refund where ordercode=?", ordercode);
				if(fo != null){
					refund.set("id", fo.get("id"));
					refund.set("state", 0);
					result = Db.update("f_refund", refund);
				}else{
					result = Db.save("f_refund", refund);
				}
			}
		}
		return result;
	}
	
	// 微信统一下单
	public static String wxPushOrder(String body, String ordercode, double price, String ip, String openid){
		Map<String, String> params = new HashMap<>();
    	params.put("appid", PropKit.get("appId"));
    	params.put("mch_id", PropKit.get("mchId"));
    	params.put("nonce_str", System.currentTimeMillis() + "");
    	params.put("body", "FlowerMM");
    	params.put("out_trade_no", ordercode);
    	params.put("total_fee", (int)(price*100) + "");
    	params.put("spbill_create_ip", ip);
    	params.put("notify_url", Constant.getHost + "/weixin/wxpayback");
    	params.put("trade_type", TradeType.JSAPI.name());
    	params.put("openid", openid);
    	params.put("sign", Signature.getSign(params));
    	String xml = PaymentApi.pushOrder(params);
    	return xml;
	}
	
	// 获取openId
	public static Map<String, Object> getAccessToken(String code) {
		String rUrl = Constant.getAccess_Token
				.replace("APPID", PropKit.get("appId"))
				.replace("SECRET", PropKit.get("appSecret"))
				.replace("CODE", code);
		Map<String, Object> map = new HashMap<>();
		try {
			URL url = new URL(rUrl);
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}

			br.close();
			isr.close();
			is.close();
			
			ApiResult ar = new ApiResult(builder.toString());
			map.put("access_token", ar.get("access_token"));
			map.put("openid", ar.get("openid"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	// 根据openId注册新用户
	public static Record setAccount(String access_token, String openId, String typeId, String eventUserId){
		Record account = Db.findFirst("select id,openid,nick,headimg,tel,recommend,isbuy,qrurl,state from f_account where openid = ?", openId);
		if(account == null){
			ApiResult ar;
			account = new Record();
			account.set("openid", openId);
			if(access_token == null){
				ar = UserApi.getUserInfo(openId);
			}else{
				String userinfo = HttpKit.get(Constant.getSnsapi_userinfo
												.replace("ACCESS_TOKEN", access_token)
												.replace("OPENID", openId));
				ar = new ApiResult(userinfo);
				account.set("isfans", 1);
			}
			try {
				account.set("nick", filterOffUtf8Mb4(ar.get("nickname").toString()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			account.set("headimg", ar.get("headimgurl"));
			account.set("isbuy", 0);
			account.set("ctime", new Date());
			account.set("state", 0);
			if(typeId!=null && eventUserId!=null){
				account.set("tjid", typeId+"_"+eventUserId);
			}
			boolean result = Db.save("f_account", account);
			if(result){
				account.set("id", account.getLong("id").intValue());
				// 新注册获得花籽
				for(int i=0;i<seedType.register.point;i++){
					Record seed = new Record();
					seed.set("aid", account.getInt("id"));
					seed.set("send", 1);
					seed.set("type", seedType.register.name());
					seed.set("remarks", seedType.register.name);
					seed.set("ctime", new Date());
					Db.save("f_flowerseed", seed);
				}
				// 新注册获取花票
				List<Record> cashlist = Db.find("SELECT a.ltime,b.id FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id=b.tid WHERE a.id=1 and a.ltime>0 and b.state=0");
				for(Record cash : cashlist){
					Record c = new Record();
					c.set("aid", account.getInt("id"));
					c.set("cid", cash.getInt("id"));
					c.set("code", "1111");
					Calendar now = Calendar.getInstance();
					c.set("time_a", now.getTime());
					now.add(Calendar.DAY_OF_MONTH, cash.getInt("ltime"));
					c.set("time_b", now.getTime());
					c.set("state", 1);
					c.set("origin", 1);
					Db.save("f_cash", c);
				}
			}
		}
		return account;
	}
	
	public static String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            }
            else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            }
            else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        return new String(buffer.array(), "utf-8");
    }
	
	// 获取用户IP
	public static String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr(); 
		}
		String ipStr = request.getHeader("x-forwarded-for");
		String[] ipArr = ipStr.split(",");
		String ip = new String();
		for(String i : ipArr){
			if(!"unknown".equals(ip)){
				ip = i;
				break;
			}
		}
		return ip;
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
	
	//更改花籽状态
	public static void chanseedstate(int id, int pid){
		int count = pid==1?39:59;
		Db.update("update f_flowerseed set state=1 where aid=? and DATE_ADD(ctime, INTERVAL 2 MONTH)>=CURDATE() and state=0 ORDER BY ctime LIMIT ?", id, count);
	}
	
	//获得物流信息
	public static List<Record> getlogisticsInfo(String code){
		return Db.find("SELECT WorkCode,OpDateTime,OpDescription from f_logistics where WorkCode =? order by ctime desc",code);
	}
	
	//我的物流信息
	public static Page<Record> getMylogistics(int pageno, int pagesize, int id){
		String select = "SELECT e.state,a.ordercode,d.workCode,max(d.orderstatuscode) as orderstatuscode,b.fpid,c.name,a.cycle,a.reach,a.price AS totalprice,b.price,a.isvase,a.type,a.ctime,a.state,c.imgurl";
		String sqlExceptSelect = "FROM f_order a LEFT JOIN f_order_detail b ON a.ordercode=b.ordercode LEFT JOIN f_flower_pro c ON b.fpid=c.id"+
								 " LEFT JOIN f_logistics d on a.ordercode = d.ClientCode"+
								 " left JOIN f_order_info e on d.WorkCode = e.number"+
								 " WHERE a.aid =? AND d.workCode is not null and e.state = 2"+
								 " group by d.workCode order by a.id";
		return Db.paginate(pageno, pagesize,select,sqlExceptSelect,id);
		
	}
	//确认收货
	public static boolean shipconfirm(int id,String workcode){
		boolean result = false;
		Record order_info = Db.findFirst("select id from f_order_info where number=? and aid=?",workcode,id);
		if(order_info != null){
			order_info.set("state", 9);
			if(Db.update("f_order_info", order_info)){
				result = true;
			}
		}
		return result;
	}
	
	//单品多买立减
	public static String getDmlj(){
		Record code_value = Db.findFirst("select code_value from f_dictionary where code_key = 'dmlj'");
		return code_value.get("code_value");
	}
	//多品多买立减
	public static String getDmlj2(){
		Record code_value = Db.findFirst("select code_value from f_dictionary where code_key = 'dmlj2'");
		return code_value.get("code_value");
	}
	
	//获得商品总额
	public static Double getTotalprice(String ordercode,int cycle){
		Double totalprice = 0.00;
		List<Record> prices = Db.find("SELECT type,price from f_order_detail  where ordercode = ? order by type",ordercode);
		for (Record record : prices) {
			if(record.getInt("type")==1){
				totalprice += record.getDouble("price")*cycle;
			}else{
				totalprice += record.getDouble("price");
			}
		} 
		return totalprice;
	}
	
	//获得忌讳花材
	public static List<Record> getjihuiflo(){
		return Db.find("SELECT id,name FROM f_flower WHERE jh = 1 AND state = 1");
	}
	
	//获得忌讳色系
	public static List<Record> getjihuicolor(){
		return Db.find("SELECT id,name FROM f_color WHERE jh = 1");
	}
	
	//获得忌讳花材分类
	public static List<Record> getjihuiType(){
		return Db.find("SELECT id,name FROM f_flower_type WHERE jh = 1");
	}
	
	//更改订单地址
	public static boolean changeorderaddr(String ordercode,int addrid){
		Record address = Db.findById("f_address", addrid);
		String newaddr = getAddressArea(address.getStr("area")) + address.getStr("addr");
		int result = Db.update("update f_order set addr = '"+newaddr+"',name = '"+address.getStr("name")+"',tel = '"+address.getStr("tel")+"' where ordercode = "+ordercode);
		return result==1?true:false;
	}
	
	//获得邀请好友内容
	public static String getInviteFriend(){
		String yqhy = Db.queryStr("SELECT code_value FROM f_dictionary WHERE code_key = 'yqhy'");
		return yqhy;
	}
	
	//获取推广的好友信息
	public static Page<Record> findOrderMember(int pageno, int pagesize, int id, int type){  //type: 1 会员：2 递推人员
		Page<Record> list = Db.paginate(pageno, pagesize,"SELECT id,headimg, nick "," FROM f_account WHERE SUBSTRING(tjid,1,1) = "+type+" AND SUBSTRING(tjid,3) = "+id+" and isbuy > 0");
		List<Record> li = list.getList();
		for(Iterator<Record> it=li.iterator();it.hasNext();){
			Record ditui = it.next(); 
			long num = Db.queryLong("SELECT COUNT(1) FROM f_order WHERE state IN (1,2,3) and aid = ?",ditui.getInt("id"));
			ditui.set("num", num);
			if(num == 0){
				it.remove();
			}
		}
		return list ;
	}
	
	//赠送好友的花票信息
	public static List<Record> getFriendCash(String cl){
		return Db.find("SELECT a.id,a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id where a.id in ("+cl+") and a.state=1 and CURDATE()>=a.time_a AND CURDATE()<=a.time_b");
	}
	//获得好友赠送的花票信息
	public static List<Record> getCashFriend(String cl){
		return Db.find("SELECT a.id,a.time_a,a.time_b,b.money,b.benefit FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id where a.id in ("+cl+") and a.state=0 and CURDATE()>=a.time_a AND CURDATE()<=a.time_b");
	}
	//获得花票的赠送人ID
	public static Integer getcashforId(String cl){
		return Db.queryInt("select distinct aid_f from f_cash where id in ("+cl+")");
	}
	
	//号码对应的服务中的订单
	public static List<Record> getCards(String tel){
		List<Record> cards = Db.find("SELECT ordercode,ctime FROM f_order WHERE tel = ? AND state = 1 and zhufu is not null ORDER BY ctime DESC", tel);
		for (Record card : cards) {
			Date cdate = card.getDate("ctime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			String ctime = sdf.format(cdate);
			card.set("ctime", ctime);
		}
		return cards;
	}
	//获得祝福语
	public static Record getCardInfo(String ordercode){
		Record zhufu = Db.findFirst("SELECT zhufu,songhua,name,ordercode FROM f_order WHERE ordercode = ?", ordercode);
		return zhufu;
	}
}