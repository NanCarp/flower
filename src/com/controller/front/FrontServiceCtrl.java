package com.controller.front;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.dao.FCDao;
import com.interceptor.FrontInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.DeliveryDateUtil;
import com.util.Sign;

/**
 * @Desc 业务相关
 * @author lxx
 * @date 2016/8/30
 * */
@Before(FrontInterceptor.class)
public class FrontServiceCtrl extends Controller {
	// 购买
	public void buy(){
		int type = getParaToInt("type");		// 商品类型(1订阅,2送花,3周边,4兑换)
		if(type == 2){
			int szdx = getParaToInt("szdx");	// 适赠对象ID
			setAttr("szdx", szdx);
		}
		int pid = getParaToInt("pid");			// 产品ID
		int cycle = getParaToInt("cycle");		// 订阅次数
		Integer vase = getParaToInt("vase");	// 购买的花瓶ID
		Record product = Db.findById("f_flower_pro", pid);
		setAttr("product", product);	// 主商品
		setAttr("type", type);			// 商品类型(1订阅,2送花,3周边,4兑换)
		setAttr("cycle", cycle);		// 周期
		double yh = 0;
		if(type == 1){	// 匹配多买立减
			if(pid == 1){
				String[] dmlj = FCDao.getDmlj().split("_");
				int[] numArr = {1,4,12};
				for(int i=0;i<numArr.length;i++){
					if(numArr[i] == cycle){
						yh = Double.valueOf(dmlj[i]);
					}
				}
			}else{
				String[] dmlj2 = FCDao.getDmlj2().split("_");
				int[] numArr = {1,4,12};
				for(int i=0;i<numArr.length;i++){
					if(numArr[i] == cycle){
						yh = Double.valueOf(dmlj2[i]);
					}
				}
			}
		}
		setAttr("vase", vase);							// 花瓶ID
		Record account = getSessionAttr("account");		// 用户信息
		int isbuy = account.getInt("isbuy");			// 是否第一次购买
		setAttr("isbuy", isbuy);
		
		double price = FCDao.countPrice(product.getDouble("price"), cycle, vase);	// 商品总价值
		double totalprice = price - yh;		// 实际付出金额
		Record activity = FCDao.getActivity(totalprice);	// 活动
		if(activity != null){
			setAttr("activity", activity);
			double benefit = activity.getDouble("benefit");
			totalprice -= benefit;
			yh += benefit;
		}
		setAttr("yh", yh);		// 优惠总额
		setAttr("cashlist", FCDao.getCashAble(account.getInt("id"), totalprice));	// 花票
		setAttr("maxcash",FCDao.getMaxCash(account.getInt("id"), totalprice));  // 优惠最多的花票
		setAttr("price", price);	// 商品总价值
		setAttr("totalprice", totalprice);	// 实际付出金额
		setAttr("address", FCDao.getDefaultAddress(account.getInt("id"), 1, getParaToInt("addr")));	// 收货地址
		setAttr("jhclos",FCDao.getjihuicolor());
		setAttr("jihuis",FCDao.getjihuiType());
		if(type==1 || type==2){	// 订阅与送花
			render("buy.html");
		}
		if(type == 3){	// 周边
			render("buy_around.html");
		}
	}
	// 兑换花束
	public void exchangeflower(){
		int pid = getParaToInt("pid");		// 产品ID
		Record product = Db.findById("f_flower_pro", pid);
		setAttr("product", product);	// 主商品
		setAttr("pid", pid);
		Record account = getSessionAttr("account");		// 用户信息
		double price = FCDao.countPrice(product.getDouble("price"), 1, null);	// 商品总额
		setAttr("price", price);
		setAttr("address", FCDao.getDefaultAddress(account.getInt("id"), 1, getParaToInt("addr")));	// 收货地址
		setAttr("jihuis", FCDao.getjihuiType());
		setAttr("jhclos",FCDao.getjihuicolor());
		render("exchange.html");
	}
	// 选择收货地址
	public void chooseaddress(){
		setAttr("addr", getParaToInt("addr"));
		int type = getParaToInt("type");	// 商品类型(1订阅,2送花,3周边,4兑换)
		int give = 1;
		Record account = getSessionAttr("account");
		setAttr("addresslist", FCDao.getAddressForType(account.getInt("id"), give));
		setAttr("type", type);
		render("address_choose.html");
	}
	// 更换收货地址
	public void changeaddress(){
		setAttr("addr", getParaToInt("addr"));
		int type = getParaToInt("type");	// 商品类型(1订阅,2送花,3周边,4兑换)
		int give = 1;
		Record account = getSessionAttr("account");
		setAttr("addresslist", FCDao.getAddressForType(account.getInt("id"), give));
		
		setAttr("type", type);
		setAttr("ordercode", getPara("ordercode"));
		render("address_change.html");
	}
	// 下单
	public void createorder() throws ParserConfigurationException, IOException, SAXException{
		int type = getParaToInt("type");			// 商品类型(1订阅,2送花,3周边,4兑换)
		Integer szdx = getParaToInt("szdx");		// 适赠对象ID
		int pid = getParaToInt("pid");				// 商品ID
		Integer vase = getParaToInt("vase");		// 花瓶ID
		int addressid = getParaToInt("address");	// 收货地址ID
		int reach = getParaToInt("reach");			// 送达日期
		int cycle = getParaToInt("cycle");			// 周期
		String zhufu = getPara("zhufu");			// 祝福卡
		String songhua = getPara("songhua");		// 送花人
		String jh_list = getPara("jh_list");		// 忌讳的花
		String jhcolor_list = getPara("jhcolor_list");		// 忌讳色系
		String recommend = getPara("recommend");	// 邀请人手机号
		Integer cash = getParaToInt("cash");		// 花票
		Integer activity = getParaToInt("activity");// 活动
		Double yh = Double.parseDouble(getPara("yh"));	// 优惠总额
		// 创建订单
		Map<String, Object> xmlMap = FCDao.createOrder(getRequest(), getSession(), pid, vase, addressid, reach, cycle, zhufu, songhua, jh_list, jhcolor_list, type, 
				recommend, szdx, cash, activity, yh);
		if((boolean) xmlMap.get("result")){
			setAttr("ordercode", xmlMap.get("ordercode"));
			setAttr("payMap", Sign.sign2(xmlMap.get("prepay_id").toString()));
			setAttr("detaillist", xmlMap.get("detaillist"));
			render("topay.html");
		}else{
			render("error.html");
		}
	}
	// 兑换验证
	public void exchangevalid(){
		boolean result = false;
		int pid = getParaToInt();
		Record account = getSessionAttr("account");
		int count = (int) FCDao.getFlowerSeed(account.getInt("id")).get("count");
		if(count >= 39){
			if(pid == 1){
				result = true;
			}else if(count >= 59 && pid == 2){
				result = true;
			}else{
				result = false;
			}
		}else{
			result = false;
		}
		renderJson(result);
	}
	// 兑换花束下单
	public void createorderforexchange()  throws ParserConfigurationException, IOException, SAXException{
		int type = getParaToInt("type");			// 商品类型(1订阅,2送花,3周边,4兑换)
		int pid = getParaToInt("pid");				// 商品ID
		int addressid = getParaToInt("address");	// 收货地址ID
		int reach = getParaToInt("reach");			// 送达日期
		String jh_list = getPara("jh_list");		// 忌讳的花
		String jhcolor_list = getPara("jhcolor_list");		// 忌讳色系
		
		FCDao.exccreateOrder(getSession(), pid, addressid, reach, jh_list, jhcolor_list, 1, type);
		Record account = getSessionAttr("account");
		FCDao.chanseedstate(account.getInt("id"),pid);
		myorder();
	}
	// 我的订单
	public void myorder(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		// 订单状态:9全部[0未付款，1服务中，2待评价，3已完成，4退款，5交易取消]
		Integer state = getParaToInt(1)==null?9:getParaToInt(1);
		Record account = getSessionAttr("account");
		Page<Record> page = FCDao.getMyOrder(pageno, 16, account.getInt("id"), state);
		setAttr("seedcount", FCDao.getFlowerSeed(account.getInt("id")));
		if(account.getInt("isbuy")==0){
			for(Record r : page.getList()){
				if(r.getInt("state")==1 || r.getInt("state")==2 || r.getInt("state")==3 || r.getInt("state")==4){
					account.set("isbuy", 1);
					break;
				}
			}
		}else if(account.getInt("isbuy")==1){
			int count = 0;
			for(Record r : page.getList()){
				if(r.getInt("state")==1 || r.getInt("state")==2 || r.getInt("state")==3 || r.getInt("state")==4){
					count++;
				}
			}
			if(count>=2){
				account.set("isbuy", 2);
			}
		}
		if(pageno==1){
			setAttr("state", state==null?9:state);
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("orderlist", page.getList());
			render("order_my.html");
		}else{
			renderJson(page.getList());
		}
	}
	// 订单详情
	public void orderinfo() throws ParseException{
		String ordercode = getPara();
		Record account = getSessionAttr("account");
		Map<String, Object> map = FCDao.getOrderInfo(account.getInt("id"), ordercode);
		int cycle = (int) map.get("cycle");
		double yhje = (double) map.get("yhje");
		if((int) map.get("type") == 1){	// 匹配多买立减
			String[] dmlj = FCDao.getDmlj().split("_");
			int[] numArr = {1,4,12};
			for(int i=0;i<numArr.length;i++){
				if(numArr[i] == cycle){
					yhje += Double.valueOf(dmlj[i]);
				}
			}
		}
		setAttr("yh", yhje);	// 优惠总额
		setAttr("allprice", FCDao.getTotalprice(ordercode,cycle));
		
		Record order = (Record) map.get("order");
		String addrselect = order.get("addr");
		if(addrselect!=null){
			int addrid;
			List<Record> addresses = FCDao.getAddresses(account.getInt("id"));
			for (Record record : addresses) {
				if(record.get("addr").equals(addrselect)){
					addrid = record.get("id");
					setAttr("addrid", addrid);	// 收货地址
				}
			}
		}
		Map<String, Object> pdmap = DeliveryDateUtil.chooseDate();
		if(pdmap.get("reach") == order.get("reach")){
			if((boolean) pdmap.get("result")){
				setAttr("result", 0);
			}else{
				setAttr("result", 1);
			}
		}else{
			setAttr("result", 1);
		}
		setAttr("seedcount", FCDao.getFlowerSeed(account.getInt("id")));
		setAttr("order", map.get("order"));
		setAttr("picilist", map.get("picilist"));
		render("order_info.html");
	}
	
	// 更改订单地址
	public void changeorderaddr() throws ParseException{
		String ordercode = getPara("ordercode");
		int addrid = getParaToInt("id");
		boolean result = FCDao.changeorderaddr(ordercode, addrid);
		if(result){
			Record account = getSessionAttr("account");
			Map<String, Object> map = FCDao.getOrderInfo(account.getInt("id"), ordercode);
			int cycle = (int) map.get("cycle");
			double yhje = (double) map.get("yhje");
			if((int) map.get("type") == 1){	// 匹配多买立减
				String[] dmlj = FCDao.getDmlj().split("_");
				int[] numArr = {1,4,12};
				for(int i=0;i<numArr.length;i++){
					if(numArr[i] == cycle){
						yhje += Double.valueOf(dmlj[i]);
					}
				}
			}
			setAttr("yh", yhje);	// 优惠总额
			setAttr("allprice", FCDao.getTotalprice(ordercode,cycle));
			
			Record order = (Record) map.get("order");
			String addrselect = order.get("addr");
			int addrid1;
			if(addrselect!=null){
				List<Record> addresses = FCDao.getAddresses(account.getInt("id"));
				for (Record record : addresses) {
					if(record.get("addr").equals(addrselect)){
						addrid1 = record.get("id");
						setAttr("addrid", addrid1);	// 收货地址
					}
				}
			}
			Map<String, Object> pdmap = DeliveryDateUtil.chooseDate();
			if(pdmap.get("reach") == order.get("reach")){
				if((boolean) pdmap.get("result")){
					setAttr("result", 0);
				}else{
					setAttr("result", 1);
				}
			}else{
				setAttr("result", 1);
			}
			setAttr("seedcount", FCDao.getFlowerSeed(account.getInt("id")));
			setAttr("order", map.get("order"));
			setAttr("picilist", map.get("picilist"));
			setAttr("addrid", addrid);
			setAttr("ordercode", ordercode);
//			render("order_info.html");
			render("postpone.html");
		}
	}
	
	// 立即支付
	public void payfororder() throws ParserConfigurationException, IOException, SAXException{
		String ordercode = getPara("ordercode");
		Record account = getSessionAttr("account");
		Map<String, Object> xmlMap = FCDao.payForOrder(getRequest(), account.getStr("openid"), account.getInt("id"), ordercode);
		setAttr("ordercode", ordercode);
		setAttr("payMap", Sign.sign2(xmlMap.get("prepay_id").toString()));
		setAttr("detaillist", xmlMap.get("detaillist"));
		render("topay.html");
	}
	// 退款申请
	public void refund(){
		String ordercode = getPara();
		Record account = getSessionAttr("account");
		setAttr("order", FCDao.refund(account.getInt("id"), ordercode));
		render("refund.html");
	}
	// 赠送好友-获取信息
	public void orderreceive(){
		String ordercode = getPara();
		Record account = getSessionAttr("account");
		setAttr("order", FCDao.getOrderToReceive(account.getInt("id"), ordercode));
		render("order_receive.html");
	}
	// 赠送好友-验证与提交
	public void orderreveivevalidandsave(){
		String ordercode = getPara(0);
		int cycle = getParaToInt(1);
		Record account = getSessionAttr("account");
		renderJson(FCDao.orderReveiveValidAndSave(account.getInt("id"), ordercode, cycle));
	}
	// 提交退款申请
	public void saverefund(){
		String ordercode = getPara("ordercode");
		String remark = getPara("remark");
		Record account = getSessionAttr("account");
		renderJson(FCDao.saveRefund(account.getInt("id"), ordercode, remark));
	}
	// 取消订单
	public void cancelorder(){
		String ordercode = getPara();
		Record account = getSessionAttr("account");
		renderJson(FCDao.cancelOrder(account.getInt("id"), ordercode));
	}
	// 我的物流
	public void mylogistics(){
 		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		// 订单状态:9全部[0未付款，1服务中，2待评价，3已完成，4退款，5交易取消]
		Record account = getSessionAttr("account");
		Page<Record> page = FCDao.getMylogistics(pageno, 16, account.getInt("id"));
		if(pageno==1){
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("orderlist", page.getList());
			render("logistics_my.html");
		}else{
			renderJson(page.getList());
		}
	}
	//确认收货
	public void shipconfirm(){
		String workcode = getPara();
        Record account = getSessionAttr("account");
		renderJson(FCDao.shipconfirm(account.getInt("id"),workcode)); 
	}
	// 物流详情
	public void logisticsinfo(){
		render("logistics_info.html");
	}
	//去评价
	public void evaluate() throws ParseException{
		String ordercode = getPara();
		Record account = getSessionAttr("account");
		Map<String, Object> map = FCDao.getOrderInfo(account.getInt("id"), ordercode);
		setAttr("order", map.get("order"));
		render("evaluate.html");
	}
	//保存评价
	public void saveEvaluate(){
		String ordercode = getPara("ordercode"); 
		Integer star = getParaToInt("star");
		String eva = getPara("eva");
		renderJson(FCDao.saveEvaluate(ordercode, star, eva));
	}
	//物流编号查询
	public void getlogistics(){
		String workcode = getPara("workcode");
		List<Record> logistics = FCDao.getlogisticsInfo(workcode);
		renderJson(logistics);
	}
	//订单顺延
	public void chOrderreach(){
		String ordercode = getPara("ordercode");
		int addrid = getParaToInt("id");
		Record order = Db.findFirst("select reach,addr,name as receiver,tel,type from f_order where ordercode=?", ordercode);
		setAttr("addrid", addrid);
		setAttr("order", order);
		setAttr("ordercode", ordercode);
		render("postpone.html");
	}
	//配送日期顺延
	public void deliverdatepost() throws ParseException{
		String ordercode = getPara(0);
		int addrid = getParaToInt(1);
		String mon = DeliveryDateUtil.compare_Date(1);
		String sat = DeliveryDateUtil.compare_Date(2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date monday = sdf.parse(mon);
		Date saturday = sdf.parse(sat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(monday);
		cal.add(Calendar.DAY_OF_MONTH, 7);
		String mon_7 = sdf.format(cal.getTime());
		cal.setTime(saturday);
		cal.add(Calendar.DAY_OF_MONTH, 7);
		String sat_7 = sdf.format(cal.getTime());
		Record order = Db.findFirst("select reach,ocount from f_order where ordercode=?", ordercode);
		setAttr("ordercode", ordercode);
		setAttr("addrid", addrid);
		setAttr("mon", mon);
		setAttr("sat", sat);
		setAttr("mon_7", mon_7);
		setAttr("sat_7", sat_7);
		setAttr("reach", order.getInt("reach"));
		setAttr("pnum", order.getInt("ocount")+1);
		render("datepost.html");
	}
	//到达时间修改
	public void reachpost() throws ParseException{
		String ordercode = getPara(0);
		int addrid = getParaToInt(1);
		String mon = DeliveryDateUtil.compare_Date(1);
		String sat = DeliveryDateUtil.compare_Date(2);
		Record rec = Db.findFirst("select reach,ocount from f_order where ordercode=?", ordercode);
		setAttr("ordercode", ordercode);
		setAttr("addrid", addrid);
		setAttr("reach", rec.getInt("reach"));
		setAttr("pnum", rec.getInt("ocount")+1);
		setAttr("mon", mon);
		setAttr("sat", sat);
		render("reachpost.html");
	}
	//保存送达时间
	public void saveReachChange(){
		int reach = getParaToInt("reach");
		String ordercode = getPara("ordercode");
		String sy_code = getPara("sy_code").replace("-", "");
		int Rnum = Db.update("update f_order set reach=?,is_sy=1,sy_code=? where ordercode=?", reach, sy_code, ordercode);
		renderJson(Rnum==0?false:true);
	}
	//日期顺延
	public void saveponedate(){
		Map<String, Object> map = new HashMap<>();
		boolean result = false;
		String msg = new String();
		String ordercode = getPara("ordercode");
		String sycode = getPara("sydate").replace("-", "");
		Number cnum = Db.queryNumber("select count(1) from f_order where ordercode=? and is_sy=1 and sy_code=?", ordercode, sycode);
		if(cnum.intValue()>0){
			msg = "不可重复操作";
		}else{
			int Unum = Db.update("update f_order set is_sy=1, sy_code=? where ordercode=?", sycode, ordercode);
			if(Unum>0){
				result = true;
				msg = "顺延成功";
			}else{
				msg = "顺延失败";
			}
		}
		map.put("result", result);
		map.put("msg", msg);
		renderJson(map);
	}
}
