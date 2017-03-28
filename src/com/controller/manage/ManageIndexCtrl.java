package com.controller.manage;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.dao.MCDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.MD5Util;

public class ManageIndexCtrl extends Controller {
	// 首页
	@Before(ManageInterceptor.class)
	public void index(){
		Record admin = getSessionAttr("admin");
		setAttr("username", admin.getStr("username"));
		setAttr("name", admin.getStr("name"));
		
		List<Record> ml = new ArrayList<>();
		String mid = admin.getStr("mid");
		if(mid!=null && mid.length()>0){
			String[] midarr = mid.split(",");
			ml = MCDao.getMenu();
			for(Iterator<Record> it=ml.iterator();it.hasNext();){
				Record menu = it.next(); 
				boolean has = false;
				for(String m : midarr){
					if(menu.getInt("id") == Integer.parseInt(m)){
						has = true;
						break;
					}
				}
				if(!has){
					it.remove();
				}
			}
		}
		setAttr("menulist", ml);
		render("index.html");
	}
	// 登录页面
	public void login(){
		render("login.html");
	}
	// 登录操作
	public void adminLogin() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String username = getPara("username");
		String password = getPara("password");
		
		boolean result = false;
		String message = new String();
		
		Record admin = Db.findFirst("SELECT a.id,a.username,a.password,a.rid,b.name,c.mid FROM f_admin a LEFT JOIN f_role b ON a.rid=b.id LEFT JOIN f_authority c ON b.id=c.rid where a.username=?", username);
		if(admin == null){
			message = "用户名或密码错误";
		}else{
			boolean v = MD5Util.validPassword(password, admin.getStr("password"));
			if(v){
				result = true;
				message = "登录成功";
				getSession().setAttribute("admin", admin);
				
				Cookie cookie = new Cookie("flower", ""+admin.getInt("id"));
				cookie.setMaxAge(60*60*24*7);
				cookie.setPath("/manage/");
				getResponse().addCookie(cookie);
			}else{
				message = "用户名或密码错误";
			}
		}
		
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("result", result);
		responseMap.put("message", message);
		renderJson(responseMap);
	}
	// 退出操作
	public void adminExit(){
		getSession().removeAttribute("admin");
		redirect("/manage/login");
	}
	
	// 桌面
	public void desktop(){
		Page<Record> page = MCDao.getNotice(1, 16, null);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("noticelist", page.getList());
		render("/manage/iframe/desktop.html");
	}
}
