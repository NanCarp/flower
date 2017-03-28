package com.controller.manage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dao.MCDao;
import com.dao.OrderDao;
import com.google.gson.Gson;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.util.Constant;

/**
 * @Desc 会员管理
 * @author lxx
 * @date 2016/8/16
 * */
@Before(ManageInterceptor.class)
public class ManageMemberCtrl extends Controller {
	/*********************会员管理*********************/
	// 列表
	public void account(){
		Integer pageno = getParaToInt("pageno")==null?1:getParaToInt("pageno");
		Integer state = getParaToInt("state")==null?9:getParaToInt("state");
		Integer isbuy = getParaToInt("isbuy")==null?9:getParaToInt("isbuy");
		Integer isgive = getParaToInt("isgive")==null?9:getParaToInt("isgive");
		String code = getPara("code");
		String aid = getPara("aid");
		Integer gid = getParaToInt("groupid")==null?9999:getParaToInt("groupid");
		String time_a = getPara("time_a");
		String time_b = getPara("time_b");
		Page<Record> page = MCDao.getAccount(pageno, 16, state, isbuy, isgive, code, gid, time_a, time_b, aid);
		String groups = HttpUtils.get(Constant.getHost+"/weixin/getGroups");
		Gson gson = new Gson();
		List<Map<String, Object>> glist = gson.fromJson(groups, ArrayList.class);
		Integer cashid = Db.queryInt("select id from f_cashtheme where tpid = 4");
		List<Record> cashlist = Db.find("SELECT * FROM f_cashtheme WHERE id > 3 AND ltime>0");
		setAttr("cashid", cashid);
		setAttr("cashlist", cashlist);
		setAttr("glist", glist);
		setAttr("state", state);
		setAttr("isbuy", isbuy);
		setAttr("isgive", isgive);
		setAttr("code", code);
		setAttr("gid", gid);
		setAttr("time_a", time_a);
		setAttr("time_b", time_b);
		setAttr("aid", aid);
		
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("accountlist", page.getList());
		render("account.html");
	}
	// 冻结&启用
	public void freezeAccount(){
		int id = getParaToInt(0);
		int state = getParaToInt(1);
		renderJson(MCDao.freezeAccount(id, state));
	}
	// 设为已赠送
	public void setGive(){
		int id = getParaToInt();
		Record account = Db.findById("f_account", id);
		account.set("isgive", 1);
		renderJson(Db.update("f_account", account));
	}
	// 导出报表
	public void exportinfo(){
		Date time_a = getParaToDate("time_a");
		Date time_b = getParaToDate("time_b");
		try {
			OrderDao.getinfoForExcel(getResponse(), time_a, time_b);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderNull();
	}
	//设置分组
	public void setgroup(){
		int id = getParaToInt(0);
		int gid = getParaToInt(1);
		String groups = HttpUtils.get(Constant.getHost + "/weixin/getGroups");
		Gson gson = new Gson();
		List<Map<String, Object>> glist = gson.fromJson(groups, ArrayList.class);
		setAttr("glist", glist);
		setAttr("id", id);
		setAttr("gid", gid);
		render("group_set.html");
	}
	//保存用户信息
	public void saveGroupInfo(){
		int id = getParaToInt("id");
		int gid = getParaToInt("gid");
		boolean result = MCDao.updateMemGroup(id, gid);
		renderJson(result);
	}
	//推送花票
	public void pushcash(){
		Map<String, Object> map = new HashMap<>();
		int cashid = getParaToInt("cashid");
		Db.update("UPDATE f_cashtheme SET tpid = 0 WHERE id > 3 AND ltime > 0");
		Db.update("UPDATE f_cashtheme SET tpid = 4 WHERE id > 3 AND ltime > 0 AND id = ?", cashid);
		String theme = Db.queryStr("select name from f_cashtheme where id=?", cashid);
		map.put("result", true);
		map.put("theme", theme);
		renderJson(map);
	}
	/*********************意见反馈*********************/
	// 列表
	public void feedback(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getFeedback(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("feedbacklist", page.getList());
		render("feedback.html");
	}
	// 意见反馈详情
	public void advice(){
		Integer id = getParaToInt()==null?9999:getParaToInt();
		Record advice = MCDao.getAdviceinfo(id);
		setAttr("advice", advice);
		render("advice.html");
	}
}