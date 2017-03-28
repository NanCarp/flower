package com.controller.manage;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import com.dao.MCDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.MD5Util;

/**
 * @Desc 系统设置
 * @author lxx
 * @date 2016/8/11
 * */
@Before(ManageInterceptor.class)
public class ManageSystomCtrl extends Controller {
	/*********************数据字典*********************/
	// 数据字典
	public void dictionary(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getDictionary(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("dictionarylist", page.getList());
		render("dictionary.html");
	}
	// 保存数据
	public void saveDictionary(){
		boolean result = false;
		Record record = new Record();
		String code_name = getPara("code_name");
		String code_key = getPara("code_key");
		String code_value = getPara("code_value");
		if(code_key.equals("cash")){
			Number numN = Db.queryNumber("select count(1) from f_cashtheme where name=?", code_value);
			if(numN.intValue()>0){
				record.set("code_name", code_name);
				record.set("code_key", code_key);
				record.set("code_value", code_value);
				record.set("note", getPara("note"));
				Integer id = getParaToInt("id");
				if(id == null){
					result = Db.save("f_dictionary", record);
				}else{
					record.set("id", id);
					result = Db.update("f_dictionary", record);
				}
			}
		}else{
			record.set("code_name", code_name);
			record.set("code_key", code_key);
			record.set("code_value", code_value);
			record.set("note", getPara("note"));
			Integer id = getParaToInt("id");
			if(id == null){
				result = Db.save("f_dictionary", record);
			}else{
				record.set("id", id);
				result = Db.update("f_dictionary", record);
			}
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getDictionary(){
		Integer id = getParaToInt();
		if(id != null){
			setAttr("dictionary", Db.findById("f_dictionary", id));
		}
		render("dictionary_detail.html");
	}
	// 重复验证
	public void validDictionary(){
		Integer id = getParaToInt("id");
		String key = getPara("key");
		String value = getPara("value");
		Number num;
		if(id == null){
			num = Db.queryNumber("select count(1) from f_dictionary where code_key=? and code_value=?", key, value);
		}else{
			num = Db.queryNumber("select count(1) from f_dictionary where code_key=? and code_value=? and id!=?", key, value, id);
		}
		renderJson("num", num.intValue());
	}
	// 删除数据
	public void delDictionary(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_dictionary", id));
	}
	
	/*********************地区管理*********************/
	// 获取地区
	public void area(){
		setAttr("arealist", MCDao.getArea());
		render("area.html");
	}
	// 获取子级地区
	public void pointArea(){
		int pid = getParaToInt();
		renderJson(MCDao.getArea(pid));
	}
	// 保存数据
	public void saveArea(){
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		int ca = getParaToInt("ca");
		int op = getParaToInt("op");
		if(op == 0){
			record.set("pid", ca);
		}else{
			record.set("pid", op);
		}
		Integer id = getParaToInt("id");
		if(id == null){
			result = Db.save("f_area", record);
		}else{
			record.set("id", id);
			result = Db.update("f_area", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getArea(){
		Integer id = getParaToInt();
		setAttr("areaA", MCDao.getArea(0));
		int ca = 0;
		int op = 0;
		if(id != null){
			Record area = Db.findById("f_area", id);
			setAttr("area", area);
			// 是否为一级地区
			if(area.getInt("pid") > 0){
				// 获得父级地区(一级或二级)
				Record r = Db.findById("f_area", area.getInt("pid"));
				if(r.getInt("pid") != 0){
					ca = r.getInt("pid");
					op = r.getInt("id");
					// 二级地区
					setAttr("areaB", MCDao.getArea(r.getInt("pid")));
				}else{
					// 一级地区
					ca = r.getInt("id");
					// 获取对应二级子地区
					setAttr("areaB", MCDao.getArea(r.getInt("id")));
				}
			}
		}
		setAttr("ca", ca);
		setAttr("op", op);
		render("area_detail.html");
	}
	// 删除数据
	public void delArea(){
		int id = getParaToInt();
		renderJson(MCDao.delArea(id));
	}
	
	/*********************用户管理*********************/
	// 获取管理员
	public void admin(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getAdmin(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("adminlist", page.getList());
		render("admin.html");
	}
	// 保存数据
	public void saveAdmin() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		boolean result = false;
		Record record = new Record();
		String username = getPara("username");
		String rid = getPara("rid");
		record.set("username", username);
		record.set("rid", rid);
		Integer id = getParaToInt("id");
		Record user = Db.findFirst("select * from f_admin where rid=? and username=?",rid,username);
		if(user==null){
			if(id == null){
				record.set("password", MD5Util.getEncryptedPwd("111111"));
				record.set("ctime", new Date());
				result = Db.save("f_admin", record);
			}else{
				record.set("id", id);
				result = Db.update("f_admin", record);
			}
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getAdmin(){
		Integer id = getParaToInt();
		List<Record> rolelist = Db.find("select id,name from f_role where id != 1");
		if(id != null){
			Record admin = Db.findById("f_admin", id);
			setAttr("admin", admin);
			if(admin.getInt("rid") == 1){
				rolelist = Db.find("select id,name from f_role where id = 1");
			}
		}
		setAttr("rolelist", rolelist);
		render("admin_detail.html");
	}
	// 删除数据
	public void delAdmin(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_admin", id));
	}
	
	/*********************角色管理*********************/
	//修改密码
	public void savepsw() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		boolean result = false;
		String oldpsw = getPara(0);
		String newpsw = MD5Util.getEncryptedPwd(getPara(1));
		Record record = getSessionAttr("admin");
		Integer id = record.getInt("id");
		Record psw = Db.findFirst("select password from f_admin where id =?",id);
		boolean v = MD5Util.validPassword(oldpsw, psw.getStr("password"));
		if(v){
			result = true; 
			Db.update("update f_admin set password=? where id =?",newpsw,id);
		}
		renderJson("result",result);
	}
	
	/*********************角色管理*********************/
	// 获取角色
	public void role(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getRole(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("rolelist", page.getList());
		render("role.html");
	}
	// 保存数据
	public void saveRole(){
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		Integer id = getParaToInt("id");
		if(id == null){
			result = Db.save("f_role", record);
		}else{
			record.set("id", id);
			result = Db.update("f_role", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getRole(){
		Integer id = getParaToInt();
		if(id != null){
			setAttr("role", Db.findById("f_role", id));
		}
		render("role_detail.html");
	}
	// 获取权限
	public void getAuthority(){
		int rid = getParaToInt();
		Record record = Db.findFirst("select id,mid from f_authority where rid=?", rid);
		if(record != null){
			setAttr("id", record.getInt("id"));
			setAttr("mid", record.getStr("mid"));
		}
		setAttr("rid", rid);
		render("role_authority.html");
	}
	// 保存权限
	public void saveAuthority(){
		Integer id = getParaToInt("id");
		String mid = getPara("mid");
		int rid = getParaToInt("rid");
		renderJson(MCDao.saveAuthority(id, mid, rid));
	}
	// 删除数据
	public void delRole(){
		int id = getParaToInt();
		boolean result = false;
		if(Db.deleteById("f_role", id)){
			int n = Db.update("delete from f_admin where rid = ?", id);
			result = n==0?false:true;
		}
		renderJson(result);
	}
	
	/*********************菜单管理*********************/
	// 列表
	public void menu(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getMenuPage(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("menulist", page.getList());
		render("menu.html");
	}
	// 所有
	public void allMenu(){
		renderJson(MCDao.getMenu());
	}
	// 获取单条数据
	public void getMenu(){
		Integer id = getParaToInt();
		setAttr("menulist", MCDao.getMenu());
		if(id != null){
			setAttr("menu", Db.findById("f_menu", id));
		}
		render("menu_detail.html");
	}
	// 保存数据
	public void saveMenu(){
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		record.set("url", getPara("url"));
		record.set("pid", getPara("pid"));
		Integer id = getParaToInt("id");
		if(id == null){
			result = Db.save("f_menu", record);
		}else{
			record.set("id", id);
			result = Db.update("f_menu", record);
		}
		renderJson("result", result);
	}
	// 删除数据
	public void delMenu(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_menu", id));
	}
}
