package com.controller.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import com.dao.MCDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.UploadImageUtil;

/**
 * @Desc 文章管理
 * @author lxx
 * @date 2016/8/12
 * */
@Before(ManageInterceptor.class)
public class ManageArticleCtrl extends Controller {
	/*********************系统公告*********************/
	// 列表
	public void notice(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);	 
		String tcode = getPara(1);
		if(tcode!=null){
			try {
				tcode = URLDecoder.decode(tcode, "utf-8");
				Page<Record> page = MCDao.getNotice(pageno, 16, tcode);
				setAttr("titlecode", tcode);
				setAttr("pageno", page.getPageNumber());
				setAttr("totalpage", page.getTotalPage());
				setAttr("totalrow", page.getTotalRow());
				setAttr("noticelist", page.getList());
				render("notice.html");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			Page<Record> page = MCDao.getNotice(pageno, 16, tcode);
			setAttr("titlecode", tcode);
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("noticelist", page.getList());
			render("notice.html");
		}
	}
	// 保存数据
	public void saveNotice(){
		boolean result = false;
		Record record = new Record();
		record.set("title", getPara("title"));
		record.set("describe", getPara("describe"));
		record.set("info", getPara("info"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_notice", record);
		}else{
			record.set("id", id);
			result = Db.update("f_notice", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getNotice(){
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("notice", Db.findById("f_notice", id));
		}
		render("notice_detail.html");
	}
	// 获取单条数据
	public void getNoticeInfo(){
		int id = getParaToInt(0);
		setAttr("notice", Db.findById("f_notice", id));
		render("notice_detail_check.html");
	}
	// 删除数据
	public void delNotice(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_notice", id));
	}
	
	/*********************生活美学*********************/
	// 列表
	public void esthetics() throws UnsupportedEncodingException {
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		String tcode = getPara(1);
		if(tcode != null){
			tcode = URLDecoder.decode(tcode, "utf-8");
		}
		Page<Record> page = MCDao.getEsthetics(pageno, 16, tcode);
		
		setAttr("titlecode", tcode);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("estheticslist", page.getList());
		render("esthetics.html");
	}
	// 保存数据
	public void saveEsthetics() throws IOException{
		boolean result = false;
		Record record = new Record();
		record.set("title", getPara("title"));
		String basestr = getPara("basestr");
		if(basestr != null && !"".equals(basestr)){
			String imgurl = UploadImageUtil.upLoadBase(basestr);
			record.set("imgurl", imgurl);
		}
		record.set("info", getPara("info"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_esthetics", record);
		}else{
			record.set("id", id);
			result = Db.update("f_esthetics", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getEsthetics(){
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("esthetics", Db.findById("f_esthetics", id));
		}
		render("esthetics_detail.html");
	}
	// 删除数据
	public void delEsthetics(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_esthetics", id));
	}
	// 置顶操作
	public void istopEsthetics(){
		int id = getParaToInt(0);
		int top = getParaToInt(1);
		top = top==1?0:1;
		Record record = new Record().set("id", id).set("istop", top);
		boolean result = Db.update("f_esthetics", record);
		renderJson(result);
	}
}
