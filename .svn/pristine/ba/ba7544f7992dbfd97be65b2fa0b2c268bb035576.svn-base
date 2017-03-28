package com.controller.manage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dao.MCDao;
import com.dao.WLDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * @Desc 物流管理
 * @author lxx
 * @date 2016/8/16
 * */
@Before(ManageInterceptor.class)
public class ManageLogisticsCtrl extends Controller {
	/*********************物流平台*********************/
	// 列表
	public void express(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getExpress(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("expresslist", page.getList());
		render("express.html");
	}
	// 保存数据
	public void saveExpress(){
		Map<String, Object> map = new HashMap<>();
		boolean result = false;
		String msg = new String();
		String name = getPara("name");
		String code = getPara("code");
		Number num = Db.queryNumber("select count(1) from f_express where code = ?", code);
		if(num.intValue() > 0){
			msg = "代码不能重复";
		}else{
			Record record = new Record();
			record.set("name", name);
			record.set("code", code);
			result = Db.save("f_express", record);
			if(result){
				msg = "保存成功";
			}else{
				msg = "保存失败";
			}
		}
		map.put("result", result);
		map.put("msg", msg);
		renderJson(map);
	}
	// 获取单条数据
	public void getExpress(){
		render("express_detail.html");
	}
	// 删除数据
	public void delExpress(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_express", id));
	}
	
	/*********************物流列表*********************/
	// 列表
	public void orderinfo(){
		Integer pageno = getParaToInt("pageno")==null?1:getParaToInt("pageno");
		Integer state = getParaToInt("state")==null?10:getParaToInt("state");
		Integer ishas = getParaToInt("ishas")==null?10:getParaToInt("ishas");
		
		String picicode = getPara("picicode");			// 查询批次
		String ordercode = getPara("ordercode");		// 订单编号
		String logisticcode = getPara("logisticcode");	// 物流编号
		String wuliucode = getPara("wuliucode")==null?"xzwl":getPara("wuliucode");	// 选择物流
		String receiver = getPara("receiver");	// 收货人
		if(receiver!=null){
			try {
				receiver = URLDecoder.decode(receiver,"utf-8");
				Page<Record> page = MCDao.getOrderInfo(pageno, 16, picicode, ordercode, logisticcode, state, ishas, wuliucode, receiver);
				List<Record> wuliulist = MCDao.getWuliu();
				setAttr("picicode", picicode);
				setAttr("ordercode", ordercode);
				setAttr("wuliucode", wuliucode);
				setAttr("logisticcode", logisticcode);
				setAttr("wuliulist", wuliulist);
				setAttr("state", state);
				setAttr("ishas", ishas);
				setAttr("receiver", receiver);
				
				setAttr("pageno", page.getPageNumber());
				setAttr("totalpage", page.getTotalPage());
				setAttr("totalrow", page.getTotalRow());
				setAttr("orderinfolist", page.getList());
				render("orderinfo.html");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Page<Record> page = MCDao.getOrderInfo(pageno, 16, picicode, ordercode, logisticcode, state, ishas, wuliucode, receiver);
			List<Record> wuliulist = MCDao.getWuliu();
			setAttr("picicode", picicode);
			setAttr("ordercode", ordercode);
			setAttr("wuliucode", wuliucode);
			setAttr("logisticcode", logisticcode);
			setAttr("wuliulist", wuliulist);
			setAttr("state", state);
			setAttr("ishas", ishas);
			setAttr("receiver", receiver);
			
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("orderinfolist", page.getList());
			render("orderinfo.html");
		}
	}
	// 物流详情
	public void orderpro(){
		int oid  = getParaToInt(0)==null?1:getParaToInt(0);
		String code  = getPara(1);
		Map<String, Object> map = MCDao.getOrderPro(oid,code);
		setAttr("orderinfo", map.get("orderinfo"));
		setAttr("logilist", map.get("logilist"));
		setAttr("prolist", map.get("prolist"));
		setAttr("order_jt", map.get("order_jt"));
		setAttr("picilist", map.get("picilist"));
		setAttr("jihuis", map.get("jihuis"));
		setAttr("jhcolors", map.get("jhcolors"));
		setAttr("dgsp", map.get("dgsp"));
		setAttr("cps", map.get("cps"));
		setAttr("ptid", map.get("ptid"));
		render("orderpro.html");
	}
	// 开始配单
	public void startsingle(){
		setAttr("key", MCDao.getCode());
		render("single.html");
	}
	// 发货
	public void seedgoods(){
		String wlcode = getPara(0);
		int ishas = getParaToInt(1);
		Map<String, Object> map = WLDao.seedGoods(wlcode, ishas);
		renderJson(map);
	}
	// 需要打印的物流信息
	public void printlist(){
		setAttr("printlist", WLDao.print());
		render("print.html");
	}
	// 导出物流
	public void exportwuliu(){
		String ecode = getPara(0);
		int ishas = getParaToInt(1);
		String code = getPara(2);
		boolean result = WLDao.getlogisticForExcel(getResponse(), ecode, ishas, code);
		if(result){
			renderNull();
		}else{
			renderText("存在异常订单，导出失败！");
		}
	}
	
	// 更改订单产品
	public void changepro(){
		int pid = getParaToInt("pid");
		int orderid = getParaToInt("orderid");
		renderJson("result",MCDao.changeproduct(pid,orderid));
	}
}