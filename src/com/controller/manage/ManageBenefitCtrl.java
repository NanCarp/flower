package com.controller.manage;

import java.util.Date;
import java.util.List;

import com.dao.MCDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * @Desc 优惠管理
 * @author lxx
 * @date 2016/8/24
 * */
@Before(ManageInterceptor.class)
public class ManageBenefitCtrl extends Controller {
	/*********************花票设置*********************/
	// 列表
	public void cash(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getCash(pageno, 12);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("cashlist", page.getList());
		render("cash.html");
	}
	// 保存数据
	public void saveCash(){
		boolean result = false;
		Record record = new Record();
		String name = getPara("name");
		int numN = Db.queryNumber("select count(1) from f_cashtheme where name=?", name).intValue();
		if(numN==0){
			record.set("name", name);
			record.set("ltime", getPara("ltime"));
			Integer id = getParaToInt("id");
			if(id == null){
				record.set("ctime", new Date());
				result = Db.save("f_cashtheme", record);
			}else{
				record.set("id", id);
				result = Db.update("f_cashtheme", record);
			}
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getCash(){
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("cashtheme", Db.findById("f_cashtheme", id));
		}
		render("cash_detail.html");
	}
	// 获取主题分类
	public void classify(){
		int id = getParaToInt();
		setAttr("state", Db.findById("f_cashtheme", id).getInt("state"));
		setAttr("tid", id);
		setAttr("classifylist", Db.find("select id,money,benefit from f_cashclassify where tid = ? and state = 0", id));
		render("classify.html");
	}
	// 获取主题分类详情
	public void getClassify(){
		Integer id = getParaToInt(1);
		if(id != null){
			setAttr("classify", Db.findById("f_cashclassify", id));
		}else{
			setAttr("tid", getParaToInt(0));
		}
		render("classify_detail.html");
	}
	// 保存主题分类详情
	public void saveClassify(){
		boolean result = false;
		Record record = new Record();
		record.set("money", getPara("money"));
		record.set("benefit", getPara("benefit"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("tid", getParaToInt("tid"));
			result = Db.save("f_cashclassify", record);
		}else{
			record.set("id", id);
			result = Db.update("f_cashclassify", record);
		}
		renderJson("result", result);
	}
	// 删除主题分类
	public void delClassify(){
		int id = getParaToInt();
		boolean result = false;
		Record record = Db.findFirst("SELECT b.id FROM f_cashclassify a LEFT JOIN f_cashtheme b ON a.tid=b.id WHERE a.id=? AND b.state=0", id);
		if(record != null){
			Db.update("update f_cashclassify set state = 1 where id=?",id);
			result = true;
		}
		renderJson(result);
	}
	
	/*********************花票统计*********************/
	// 列表
	public void cashsatistic(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Integer themeid = getParaToInt(1)==null?9999:getParaToInt(1);
		String pushid = getPara(2);
		String aid = getPara(3);
		Page<Record> page = MCDao.getCashsatistic(pageno, 12, themeid, pushid, aid);
		List<Record> cashthemelist = MCDao.getCashthemes();
		setAttr("themeid", themeid);
		setAttr("pushid", pushid);
		setAttr("aid", aid);
		setAttr("cashthemelist", cashthemelist);
		
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("cashsatlist", page.getList());
		render("cash_statistic.html");
	}
	
	/*********************活动设置*********************/
	// 列表
	public void activity(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getActivity(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("activitylist", page.getList());
		render("activity.html");
	}
	// 保存数据
	public void saveActivity(){
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		record.set("money", getPara("money"));
		record.set("benefit", getPara("benefit"));
		record.set("time_a", getParaToDate("time_a"));
		record.set("time_b", getParaToDate("time_b"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_activity", record);
		}else{
			record.set("id", id);
			if(id == 1){
				record.set("gid", getParaToInt("gid"));
			}
			result = Db.update("f_activity", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getActivity(){
		Integer id = getParaToInt(0);
		List<Record> ars = MCDao.getAroundPros();
		setAttr("ars", ars);
		if(id != null){
			setAttr("activity", Db.findById("f_activity", id));
		}
		render("activity_detail.html");
	}
	// 删除数据
	public void delActivity(){
		int id = getParaToInt();
		Record record = new Record();
		record.set("id", id);
		record.set("state", 1);
		renderJson(Db.update("f_activity", record));
	}
}
