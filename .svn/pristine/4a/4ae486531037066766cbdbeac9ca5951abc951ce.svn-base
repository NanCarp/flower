package com.controller.manage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import com.dao.FCDao;
import com.dao.MCDao;
import com.dao.OrderDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * @Desc 订单管理
 * @author lxx
 * @date 2016/9/21
 * */
@Before(ManageInterceptor.class)
public class ManageOrderCtrl extends Controller {
	/*********************订单列表*********************/
	// 列表
	public void orderlist(){
		Integer pageno = getParaToInt("pageno")==null?1:getParaToInt("pageno");
		Integer time = getParaToInt("time")==null?0:getParaToInt("time");
		Integer type = getParaToInt("type")==null?0:getParaToInt("type");
		Integer state = getParaToInt("state")==null?9:getParaToInt("state");
		String ordercode = getPara("code");
		Integer ishas = getParaToInt("ishas")==null?2:getParaToInt("ishas");
		String receiver = getPara("receiver");
		String time_a = getPara("time_a");
		String time_b = getPara("time_b");
		String aid = getPara("aid");
		String tel = getPara("tel");
		
		if(receiver!=null){
			try {
				receiver = URLDecoder.decode(receiver,"utf-8");
				Page<Record> page = MCDao.getOrderList(pageno, 16, time, type, state, ordercode, ishas, receiver, time_a, time_b, aid, tel);
				setAttr("time", time);
				setAttr("type", type);
				setAttr("state", state);
				setAttr("ordercode", ordercode);
				setAttr("ishas", ishas);
				setAttr("receiver", receiver);
				setAttr("time_a", time_a);
				setAttr("time_b", time_b);
				setAttr("aid", aid);
				setAttr("tel", tel);
				
				setAttr("pageno", page.getPageNumber());
				setAttr("totalpage", page.getTotalPage());
				setAttr("totalrow", page.getTotalRow());
				setAttr("orderlist", page.getList());
				render("order_list.html");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Page<Record> page = MCDao.getOrderList(pageno, 16, time, type, state, ordercode, ishas, receiver, time_a, time_b, aid, tel);
			setAttr("time", time);
			setAttr("type", type);
			setAttr("state", state);
			setAttr("ordercode", ordercode);
			setAttr("ishas", ishas);
			setAttr("receiver", receiver);
			setAttr("time_a", time_a);
			setAttr("time_b", time_b);
			setAttr("aid", aid);
			setAttr("tel", tel);
			
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("orderlist", page.getList());
			render("order_list.html");
		}
		
	}
	
	/**
	 * @see 导出订单物流信息
	 * @author yetangtang add
	 * @date 2016/10/29
	 */
	public void exportOrderList(){
		
		int time = getParaToInt("time");
		int type = getParaToInt("type");
		int state = getParaToInt("state");
		String ordercode = getPara("code");
		int ishas = getParaToInt("ishas");
		String receiver = getPara("receiver");
		String time_a = getPara("time_a");
		String time_b = getPara("time_b");
		String aid = getPara("aid");
		String tel = getPara("tel");
		if(receiver!=null){
			try {
				receiver = URLDecoder.decode(receiver,"utf-8");
				OrderDao.exportOrderList(getResponse(),time,type,state,ordercode,ishas,receiver,time_a,time_b,aid,tel);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			OrderDao.exportOrderList(getResponse(),time,type,state,ordercode,ishas,receiver,time_a,time_b,aid,tel);
		}
		renderNull();
	}
	
	// 详情
	public void orderinfo(){
		String ordercode = getPara();
		Map<String, Object> map = MCDao.getorderInfo(ordercode); 
		setAttr("order", map.get("order"));
		setAttr("detaillist", map.get("detaillist"));
		setAttr("flowerSjlist", map.get("flowerSjlist"));
		setAttr("picilist", map.get("picilist"));
		setAttr("picivase", map.get("picivase"));
		render("order_detail.html");
	}
	
	// 客诉补单
	public void customorder(){
		String code = getPara();
		boolean rs = MCDao.validateorder(code);
		if(rs){
			Map<String, Object> map = MCDao.getorderInfo(code);
			Record order = (Record) map.get("order");
			setAttr("vaseid", MCDao.getVase(code)==null?0:MCDao.getVase(code));
			setAttr("vases", MCDao.getVases());
			setAttr("floid", MCDao.getFlowerPro(code)==null?0:MCDao.getFlowerPro(code));
			setAttr("flopros", MCDao.getFlowerPros());
			setAttr("order", order);
			setAttr("detaillist", map.get("detaillist"));
			setAttr("flowerSjlist", map.get("flowerSjlist"));
			setAttr("picilist", map.get("picilist"));
			setAttr("picivase", map.get("picivase"));
			setAttr("arealist", FCDao.getArea());
			
			String area = order.getStr("addr");
     		String[] areas = area.split("-");
     		Record prov = Db.findFirst("select id,name from f_area where name=?", areas[0]);
     		Record city = Db.findFirst("select id,name from f_area where name=? and pid = ?", areas[1], prov.getInt("id"));
     		Record county = Db.findFirst("select id,name from f_area where name=? and pid = ?", areas[2], city.getInt("id"));
     		
     		setAttr("prov", prov);
     		setAttr("city", city);
     		setAttr("county",county);
     		setAttr("place", areas[3]);
		}
		render("order_custom.html");
	}
	
	// 客诉下单
	public void createorderks(){
		String ordercode = getPara("ordercode");	//原订单号
		String aid = getPara("aid");	//原订单号
		Integer szdx = getParaToInt("szdx");		// 适赠对象ID
		int pid = getParaToInt("pid");				// 商品ID
		int type = Db.queryLong("SELECT CASE WHEN ptid IN (1,2) THEN 1 WHEN ptid IN (3) THEN 2 ELSE 3 END TYPE FROM f_flower_pro WHERE id =?", pid).intValue();// 商品类型(1订阅,2送花,3周边)
		Integer vase = getParaToInt("vase");		// 花瓶ID
		int reach = getParaToInt("reach");			// 送达日期
		int cycle = getParaToInt("cycle");			// 周期
		String zhufu = getPara("zhufu");			// 祝福卡
		String songhua = getPara("songhua");		// 送花人
		String jh_list = getPara("jh_list");		// 忌讳的花
		String jhcolor_list = getPara("jhcolor_list");		// 忌讳色系
		String recommend = getPara("recommend");	// 邀请人手机号
		Integer cash = getParaToInt("cash");		// 花票
		Integer activity = getParaToInt("activity");// 活动
		Integer prov = getParaToInt("prov");// 省
		Integer city = getParaToInt("city");// 区
		Integer county = getParaToInt("county");// 县
		String area = prov+","+city+","+county;
		String address = getPara("address");// 详细地址
		String name = getPara("name");// 收货人姓名
		String tel = getPara("tel");// 收货人电话
		Double yh = 0.00;	// 优惠总额
		// 创建订单
		boolean result = MCDao.createorderks(ordercode, aid, pid, vase, area, address, reach, cycle, zhufu, songhua, jh_list, jhcolor_list, type, 
				recommend, szdx, cash, activity, yh, name, tel);
		renderJson(result);
	}
	
	//取消顺延
	public void cancelsy() throws ParseException{
		Map<String, Object> map = new HashMap<>();
		boolean r = false;
		String msg = new String();
		String ordercode = getPara();
		Number Cnum = Db.queryNumber("select count(1) from f_order where is_sy=1 and ordercode=?", ordercode);
		if(Cnum.intValue()==0){
			msg = "此订单无顺延";
		}else{
			int Unum = Db.update("update f_order set is_sy=0,sy_code=? where ordercode=?", null , ordercode);
			if(Unum==0){
				msg = "取消失败";
			}else{
				r = true;
				msg = "取消成功";
			}
		}
		map.put("msg", msg);
		map.put("R", r);
		renderJson(map);
	}
	/*********************采购订单*********************/
	// 列表
	public void purchase(){
		Integer pageno = getParaToInt("pageno")==null?1:getParaToInt("pageno");
		String ordercode = getPara("ordercode");
		Page<Record> page = MCDao.getPurchaseList(pageno, 16, ordercode);
		setAttr("ordercode", ordercode);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("purchaselist", page.getList());
		render("purchase_list.html");
	}
	// 生成采购订单
	public void createpurchase(){
		renderJson(MCDao.createPurchase());
	}
	// 详情
	public void purchaseinfo(){
		String code = getPara();
		setAttr("code", code);
		setAttr("flowerMap", MCDao.getPurchaseInfo(code));
		render("purchase_detail.html");
	}
	// 修改采购数量
	public void updatepurchase(){
		String code = getPara("code");
		String numarr = getPara("numarr");
		renderJson(MCDao.updatePurchase(code, numarr));
	}
	// 导出采购单
	public void exportpurchase(){
		String code = getPara();
		//MCDao.getPurchaseInfoForExcel_a(getResponse(), code);
		MCDao.getPurchaseInfoForExcel_b(getResponse(), (Record) getSessionAttr("admin"), code);
		renderNull();
	}

	/*********************退款申请*********************/
	// 列表
	public void refundlist(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Integer state = getParaToInt(1)==null?9:getParaToInt(1);
		String ordercode = getPara(2);
		
		Page<Record> page = MCDao.getRefundList(pageno, 16, state, ordercode);
		
		setAttr("state", state);
		setAttr("ordercode", ordercode);
		
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("refundlist", page.getList());
		render("refund_list.html");
	}
	// 详情
	public void refundinfo(){
		String ordercode = getPara();
		Map<String, Object> map = MCDao.getRefundInfo(ordercode); 
		setAttr("refund", map.get("refund"));
		setAttr("order", map.get("order"));
		setAttr("detaillist", map.get("detaillist"));
		render("refund_detail.html");
	}
	// 退款处理
	public void refundaction(){
		String ordercode = getPara("ordercode");
		String money = getPara("cash");
		renderJson(MCDao.refundAction(ordercode, Double.parseDouble(money)));
	}
	
	// 订单评价
	public void comment(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Page<Record> page = MCDao.getCommentList(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("commentlist", page.getList());
		render("comment.html");
	}

	// 撤销退款
	public void restorefund(){
		String ordercode = getPara();
		boolean result = MCDao.restorefund(ordercode);
		renderJson(result);
	}
	/*********************分享订单*********************/
	// 列表
	public void sharelist(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Integer time = getParaToInt(1)==null?0:getParaToInt(1);
		String ordercode = getPara(2);
		
		Page<Record> page = MCDao.getShareList(pageno, 16, time, ordercode);
		
		setAttr("time", time);
		setAttr("ordercode", ordercode);

		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("sharelist", page.getList());
		render("share_list.html");
	}
	
	// 详情
	public void shareinfo(){
		String ordercode = getPara();
		Map<String, Object> map = MCDao.getShareInfo(ordercode); 
		setAttr("order", map.get("order"));
		setAttr("detaillist", map.get("detaillist"));
		setAttr("flowerSjlist", map.get("flowerSjlist"));
		setAttr("picilist", map.get("picilist"));
		setAttr("picivase", map.get("picivase"));
		render("share_detail.html");
	}
}
