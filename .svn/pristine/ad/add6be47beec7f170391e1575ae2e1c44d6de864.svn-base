package com.controller.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.dao.MCDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.UploadImageUtil;

/**
 * @Desc 服务管理
 * @author lxx
 * @date 2016/8/12
 * */
@Before(ManageInterceptor.class)
public class ManageServiceCtrl extends Controller {
	/*********************常见问题*********************/
	// 列表
	public void question(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();		
		Page<Record> page = MCDao.getQuestion(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("questionlist", page.getList());
		render("question.html");
	}
	// 保存数据
	public void saveQuestion(){
		boolean result = false;
		Record record = new Record();
		record.set("title", getPara("title"));
		record.set("info", getPara("info"));
		Integer id = getParaToInt("id");
		if(id == null){
			result = Db.save("f_question", record);
		}else{
			record.set("id", id);
			result = Db.update("f_question", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getQuestion(){
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("question", Db.findById("f_question", id));
		}
		render("question_detail.html");
	}
	// 删除数据
	public void delQuestion(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_question", id));
	}
	
	/*********************养花须知*********************/
	// 列表
	public void knowledge() throws UnsupportedEncodingException{
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Integer type = getParaToInt(1)==null?0:getParaToInt(1);
		String tcode = getPara(2);
		if(tcode!=null){
			tcode = URLDecoder.decode(tcode,"utf-8");
		}
		Page<Record> page = MCDao.getKnowledge(pageno, 16, type, tcode);
		
		setAttr("type", type);
		setAttr("titlecode", tcode);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("knowledgelist", page.getList());
		render("knowledge.html");
	}
	// 保存数据
	public void saveKnowledge() throws IOException{
		boolean result = false;
		Record record = new Record();
		record.set("title", getPara("title"));
		String basestr = getPara("basestr");
		if(basestr != null && !"".equals(basestr)){
			String imgurl = UploadImageUtil.upLoadBase(basestr);
			record.set("imgurl", imgurl);
		}
		record.set("summary", getPara("summary"));
		record.set("info", getPara("info"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("type", getParaToInt("type"));
			result = Db.save("f_knowledge", record);
		}else{
			record.set("id", id);
			result = Db.update("f_knowledge", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getKnowledge(){
		Integer id = getParaToInt();
		if(id != null){
			setAttr("knowledge", Db.findById("f_knowledge", id));
		}
		render("knowledge_detail.html");
	}
	// 添加数据
	public void toAddKnowledge(){
		int type = getParaToInt();
		setAttr("type", type);
		render("knowledge_detail.html");
	}
	// 删除数据
	public void delKnowledge(){
		int id = getParaToInt();
		renderJson(Db.deleteById("f_knowledge", id));
	}
	// 置顶操作
	public void istopKnowledge(){
		int id = getParaToInt(0);
		int top = getParaToInt(1);
		top = top==1?0:1;
		Record record = new Record().set("id", id).set("istop", top);
		boolean result = Db.update("f_knowledge", record);
		renderJson(result);
	}
}
