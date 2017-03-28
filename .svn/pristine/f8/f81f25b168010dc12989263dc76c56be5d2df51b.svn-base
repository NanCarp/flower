package com.controller.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dao.MCDao;
import com.interceptor.ManageInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.UploadImageUtil;

/**
 * @Desc 商品管理
 * @author lxx
 * @date 2016/8/18
 * */
@Before(ManageInterceptor.class)
public class ManageProductCtrl extends Controller {
	/*********************花材分类*********************/
	// 列表
	public void flower_type(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getFlowerType(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("typelist", page.getList());
		render("flower_type.html");
	}
	//花材分类查询(仅仅分类查询)
	public void flower_searchType(){
		try {
			String ftype = getPara(0);
			ftype = URLDecoder.decode(ftype==null?"":ftype, "utf-8");
			Page<Record> page = MCDao.getFlowerftype(1, 16, ftype);
			List<Record> fls = page.getList();
			setAttr("ftype", ftype);
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("typelist", fls);			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render("flower_type.html");
	}
	
	// 保存数据
	public void saveFlower_type(){
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_flower_type", record);
		}else{
			record.set("id", id);
			result = Db.update("f_flower_type", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getFlower_type(){
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("type", Db.findById("f_flower_type", id));
		}
		render("flower_type_detail.html");
	}
	// 设置忌讳的花材分类
	public void set_type_jh(){
		Integer id = getParaToInt("id");
		Integer jh = getParaToInt("jh");
		Db.update("update f_flower_type set jh = ? where id =?", jh, id);
	}
	
	/*********************花材管理*********************/
	// 列表
	public void flower(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		String ftype = getPara(1);
		String fname = getPara(2);
		try {
			ftype = URLDecoder.decode(ftype==null?"":ftype, "utf-8");
			fname = URLDecoder.decode(fname==null?"":fname, "utf-8");
			Page<Record> page = MCDao.getFlower(pageno, 16, ftype, fname);
			List<Record> fls = page.getList();
			for (Record fl : fls) {
				fl.set("cname", Db.queryStr("select name from f_color where id ="+fl.getInt("cid")));
			}
			setAttr("ftype", ftype);
			setAttr("fname", fname);
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("flowerlist", fls);
			render("flower.html");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 保存数据
	public void saveFlower() throws IOException{
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		record.set("tid", getParaToInt("tid"));
		record.set("cid", getParaToInt("cid"));
		record.set("loss", Double.parseDouble(getPara("loss")));
		record.set("dozen", getParaToInt("dozen"));
		String basestr = getPara("basestr");
		if(basestr != null && !"".equals(basestr)){
			String imgurl = UploadImageUtil.upLoadBase(basestr);
			record.set("imgurl", imgurl);
		}
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_flower", record);
		}else{
			record.set("id", id);
			result = Db.update("f_flower", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getFlower(){
		setAttr("typelist", Db.find("select id,name from f_flower_type"));
		setAttr("colors", Db.find("select id,name from f_color"));
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("flower", Db.findById("f_flower", id));
		}
		render("flower_detail.html");
	}
	// 上架与下架
	public void upordownFlower(){
		int id = getParaToInt(0);
		int state = getParaToInt(1);
		renderJson(MCDao.upordownFlower(id, state));
	}
	// 忌讳的花设置
	public void set_jh_flower(){
		int id = getParaToInt("id");
		int jh = getParaToInt("jh");
		MCDao.setJhFlower(id, jh);
		renderJson();
	}
	
	/*********************商品管理*********************/
	// 列表
	public void flowerpro(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = MCDao.getFlowerPro(pageno, 16);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("flowerprolist", page.getList());
		render("flower_pro.html");
	}
	// 保存数据
	public void saveFlower_pro() throws IOException{
		boolean result = false;
		Record record = new Record();
		Integer ptid = getParaToInt("ptid");
		record.set("ptid", ptid);
		if(ptid.intValue() > 3){
			record.set("type", getParaToInt("protype"));
		}else{
			record.set("type", 0);
		}
		record.set("name", getPara("name"));
		String basestr1 = getPara("basestr1");
		String basestr1_1 = getPara("basestr1_1");
		String basestr1_2 = getPara("basestr1_2");
		String basestr1_3 = getPara("basestr1_3");
		
		if(ptid > 2){
			if(basestr1 != null && !"".equals(basestr1)){
				String imgurl = UploadImageUtil.upLoadBase(basestr1);
				record.set("imgurl", imgurl);
			}
		}else{
			Integer id = getParaToInt("id");
			if(id != null){
				String img = Db.queryStr("SELECT imgurl FROM f_flower_pro WHERE id =?", id);
				if(img.indexOf("-")!=-1){
					String[] im = img.split("-");
					String img_1 = im[0];
					String img_2 = im[1];
					String img_3 = im[2];
					
					if(basestr1_1 != null && !"".equals(basestr1_1)){
						String imgurl_1 = UploadImageUtil.upLoadBase(basestr1_1);
						img_1 = imgurl_1;
					}
					if(basestr1_2 != null && !"".equals(basestr1_2)){
						String imgurl_2 = UploadImageUtil.upLoadBase(basestr1_2);
						img_2 = imgurl_2;
					}
					if(basestr1_3 != null && !"".equals(basestr1_3)){
						String imgurl_3 = UploadImageUtil.upLoadBase(basestr1_3);
						img_3 = imgurl_3;
					}
					record.set("imgurl", img_1 +"-"+ img_2 +"-"+ img_3);
				}else{
					String img_2 = "";
					String img_3 = "";
					if(basestr1_2 != null && !"".equals(basestr1_2)){
						String imgurl_2 = UploadImageUtil.upLoadBase(basestr1_2);
						img_2 = imgurl_2;
					}else{
						img_2 = img;
					}
					if(basestr1_3 != null && !"".equals(basestr1_3)){
						String imgurl_3 = UploadImageUtil.upLoadBase(basestr1_3);
						img_3 = imgurl_3;
					}else{
						img_3 = img;
					}
					record.set("imgurl", img +"-"+ img_2 +"-"+ img_3);
				}
			}else{
				String im = "";
				if(basestr1_1 != null && !"".equals(basestr1_1)){
					String imgurl_1 = UploadImageUtil.upLoadBase(basestr1_1);
					im = imgurl_1;
				}
				if(basestr1_2 != null && !"".equals(basestr1_2)){
					String imgurl_2 = UploadImageUtil.upLoadBase(basestr1_2);
					im += ("-"+imgurl_2);
				}
				if(basestr1_3 != null && !"".equals(basestr1_3)){
					String imgurl_3 = UploadImageUtil.upLoadBase(basestr1_3);
					im += ("-"+imgurl_3);
				}
				record.set("imgurl", im);
			}
		}
		
//		String basestr2 = getPara("basestr2");	// 产品描述
//		if(basestr2 != null && !"".equals(basestr2)){
//			String infoimg = UploadImageUtil.upLoadBase(basestr2);
//			record.set("infoimg", infoimg);
//		}
		
		String basestr2_1 = getPara("basestr2_1");	// 产品描述
		if(basestr2_1 != null && !"".equals(basestr2_1)){
			String itinfo = UploadImageUtil.upLoadBase(basestr2_1);
			record.set("itinfo1", itinfo);
		}
		String basestr2_2 = getPara("basestr2_2");	// 发货详情
		if(basestr2_2 != null && !"".equals(basestr2_2)){
			String itinfo = UploadImageUtil.upLoadBase(basestr2_2);
			record.set("itinfo2", itinfo);
		}
		String basestr2_3 = getPara("basestr2_3");	// 购花须知
		if(basestr2_3 != null && !"".equals(basestr2_3)){
			String itinfo = UploadImageUtil.upLoadBase(basestr2_3);
			record.set("itinfo3", itinfo);
		}
		String basestr2_4 = getPara("basestr2_4");	// 品质保障
		if(basestr2_4 != null && !"".equals(basestr2_4)){
			String itinfo = UploadImageUtil.upLoadBase(basestr2_4);
			record.set("itinfo4", itinfo);
		}
		String basestr2_5 = getPara("basestr2_5");	// 养护建议
		if(basestr2_5 != null && !"".equals(basestr2_5)){
			String itinfo = UploadImageUtil.upLoadBase(basestr2_5);
			record.set("itinfo5", itinfo);
		}
		
		record.set("price", getPara("price"));
		record.set("describe", getPara("describe"));
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_flower_pro", record);
		}else{
			record.set("id", id);
			result = Db.update("f_flower_pro", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getFlower_pro(){
		setAttr("typelist", Db.find("select code_name,code_value from f_dictionary where code_key=?", "ptid"));
		Integer id = getParaToInt();
		if(id != null){
			Record flopro = Db.findById("f_flower_pro", id);
			String imgurl = flopro.getStr("imgurl");
			if(imgurl.indexOf("-")!=-1){
				String[] img = imgurl.split("-");
				flopro.set("imgurl1", img[0]);
				flopro.set("imgurl2", img[1]);
				flopro.set("imgurl3", img[2]);
			}else{
				flopro.set("imgurl1", imgurl);
			}
			setAttr("flowerpro", flopro);
		}
		render("flower_pro_detail.html");
	}
	// 删除
	public void delFlower_pro(){
		int id = getParaToInt();
		Record record = new Record();
		record.set("id", id);
		record.set("state", 1);
		renderJson(Db.update("f_flower_pro", record));
	}
	
	/*********************产品设置*********************/
	// 产品列表
	public void product(){
		int type = getParaToInt("type");
		Integer pageno = getParaToInt("pageno")==null?1:getParaToInt("pageno");
		String code = getPara("code");
 		String minprice = getPara("minprice");
 		String maxprice = getPara("maxprice");
		Page<Record> page = MCDao.getProduct(type, pageno, 16, code, minprice, maxprice);
		List<Record> pros = page.getList();
		if(type != 3){
			for (Record pro : pros) {
				String cname = Db.queryStr("select name from f_color where id =?", pro.getInt("cid"));
				pro.set("cname", cname==null?"无":cname);
			}
		}
		setAttr("batchcode", code);
		setAttr("minprice", minprice);
		setAttr("maxprice", maxprice);
		setAttr("type", type);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("productlist", pros);
		render("product.html");
	}
	// 产品详情
	public void getProduct() throws UnsupportedEncodingException{
		int typeid = getParaToInt(0);
		String flotype = getPara(2);
		if(flotype != null){
			flotype = URLDecoder.decode(flotype, "utf-8");
		}
		List<Record> proinfolist = new ArrayList<>();
		// typeid为0修改数据，否则添加数据
		if(typeid == 0){
			int id = getParaToInt(1);
			// 需要修改的商品属性
			Record product = Db.findById("f_product", id);
			setAttr("typeid", product.getInt("type"));
			typeid = product.getInt("type");
			setAttr("id", id);
			setAttr("typeid", product.getInt("type"));
			setAttr("fname", product.getStr("fname"));
			setAttr("sname", product.getStr("sname"));
			setAttr("cid", product.getInt("cid"));
			setAttr("fpid", product.getInt("fpid"));
			setAttr("iid", product.getInt("iid"));
			// 商品用的花材
			proinfolist = Db.find("select fid,num from f_product_info where pid=?", id);
		}else{
			setAttr("typeid", typeid);
		}
		if(typeid == 3){
			// 送花系列商品类型
			setAttr("ptlist", Db.find("select id,name from f_flower_pro where ptid = 3"));
			// 适赠对象
			setAttr("idoneitylist", Db.find("select id,title from f_idoneity where state = 0"));
		}
		
		// 花材
		String sql = "select id,name,imgurl from f_flower_view where state=?";
		if(flotype!=null && !"".equals(flotype)){
			sql += " and name like '%"+flotype+"%'";
		}
		List<Record> flowerlist = Db.find(sql, 1);
		for(Record fl : flowerlist){
			for(Record pi : proinfolist){
				if(fl.getInt("id").equals(pi.getInt("fid"))){
					fl.set("num", pi.getInt("num"));
					break;
				}
			}
		}
		
		// 全量花材
		String all_sql = "select id,name,imgurl from f_flower_view where state=?";
		List<Record> floalllist = Db.find(all_sql, 1);
		for(Record fl : floalllist){
			for(Record pi : proinfolist){
				if(fl.getInt("id").equals(pi.getInt("fid"))){
					fl.set("num", pi.getInt("num"));
					break;
				}
			}
		}
		setAttr("flowerlist", flowerlist);
		setAttr("floalllist", floalllist);
		setAttr("flotype", flotype);
		List<Record> clist = Db.find("select id,name,jh from f_color");
		setAttr("clist", clist);
		render("product_detail.html");
	}
	
	// 保存商品信息
	public void saveProduct(){
		Integer id = getParaToInt("id");		// 产品ID
		int typeid = getParaToInt("typeid");	// 商品类型ID
		String flist = getPara("flist");		// 花材列表
		String fname = getPara("fname");		// 商品名称
		String sname = getPara("sname");		// 商品简称
		Integer iid = getParaToInt("iid");		// 适赠对象ID
		Integer fpid = getParaToInt("fpid");	// 产品分类ID
		if(id == null){
			// 添加产品
			renderJson(MCDao.saveProduct(typeid, flist, fname, sname, iid, fpid));
		}else{
			// 修改产品
			renderJson(MCDao.editProduct(id, typeid, flist, fname, sname, iid, fpid));
		}
	}
	// 删除商品
	public void delProduct(){
		int id = getParaToInt();
		renderJson("result", MCDao.delProduct(id));
	}
	// 复制粘贴产品
	public void copypaste(){
		String copycode = getPara("copycode");
		Integer type = getParaToInt("type");
		Map<String, Object> map = MCDao.copypasteCode(copycode, type);
		renderJson(map);
	}
	/*********************花价管理*********************/
	// 获取花材批次列表
	public void price(){
		Integer pageno = getParaToInt("pageno")==null?1:getParaToInt("pageno");
		String code = getPara("code");
		Page<Record> page = MCDao.getPriceForBatch(pageno, 16, code);
		setAttr("batchcode", code);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("pricebatchlist", page.getList());
		render("price.html");
	}
	// 获取花价详情
	public void priceDetail(){
		int type = getParaToInt();
		if(type==1 || type==2){	// 订阅级
			setAttr("type", 0);
			setAttr("flowerMap", MCDao.getFlowerForCode(0));
		}else{
			setAttr("type", 1);	// 送花级
			setAttr("flowerMap", MCDao.getFlowerForCode(1));
		}
		render("price_detail.html");
	}
	// 保存花价
	public void savePrice(){
		int type = getParaToInt("type");
		String flist = getPara("flist");
		renderJson(MCDao.savePrice(type, flist));
	}
	// 花材价格查看
	public void seePrice(){
		String code = getPara();
		setAttr("pricelist", MCDao.seePrice(code));
		render("price_see.html");
	}
	// 花材价格查看中的实际花价
	public void savePriceSee(){
		String flist = getPara("flist");
		renderJson(MCDao.savePriceSee(flist));
	}
	
	/*********************适赠对象*********************/
	// 适赠对象列表
	public void idoneity(){
		setAttr("idoneitylist", Db.find("select id,title,imgurl,ctime from f_idoneity where state = ?", 0));
		render("idoneity.html");
	}
	// 保存数据
	public void saveIdoneity() throws IOException{
		boolean result = false;
		Record record = new Record();
		record.set("title", getPara("title"));
		String basestr = getPara("basestr");
		if(basestr != null && !"".equals(basestr)){
			String imgurl = UploadImageUtil.upLoadBase(basestr);
			record.set("imgurl", imgurl);
		}
		Integer id = getParaToInt("id");
		if(id == null){
			record.set("ctime", new Date());
			result = Db.save("f_idoneity", record);
		}else{
			record.set("id", id);
			result = Db.update("f_idoneity", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getIdoneity(){
		Integer id = getParaToInt();
		if(id != null){
			setAttr("idoneity", Db.findById("f_idoneity", id));
		}
		render("idoneity_detail.html");
	}
	// 删除
	public void delIdoneity(){
		int id = getParaToInt();
		Record record = new Record();
		record.set("id", id);
		record.set("state", 1);
		renderJson(Db.update("f_idoneity", record));
	}
	
	/*********************色系管理*********************/
	//管理色系
	public void color(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		String fcolor = getPara(1);
		try {
			fcolor = URLDecoder.decode(fcolor==null?"":fcolor, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Page<Record> page = MCDao.getColor(pageno, 16, fcolor);
		setAttr("fcolor", fcolor);
		setAttr("pageno", page.getPageNumber());
		setAttr("totalpage", page.getTotalPage());
		setAttr("totalrow", page.getTotalRow());
		setAttr("colorlist", page.getList());
		render("color.html");
	
	}
//	
//	public void color(){
//	Integer pageno = getParaToInt()==null?1:getParaToInt();
//	Page<Record> page = MCDao.getColor(pageno, 16);
//	setAttr("pageno", page.getPageNumber());
//	setAttr("totalpage", page.getTotalPage());
//	setAttr("totalrow", page.getTotalRow());
//	setAttr("colorlist", page.getList());
//	render("color.html");
//	}
//	
	
	
	
	// 保存数据
	public void saveProductColor(){
		boolean result = false;
		Record record = new Record();
		record.set("name", getPara("name"));
		Integer id = getParaToInt("id");
		if(id == null){
			result = Db.save("f_color", record);
		}else{
			record.set("id", id);
			result = Db.update("f_color", record);
		}
		renderJson("result", result);
	}
	// 获取单条数据
	public void getProductColor(){
		Integer id = getParaToInt(0);
		if(id != null){
			setAttr("color", Db.findById("f_color", id));
		}
		render("color_detail.html");
	}
	// 设置忌讳的花材分类
	public void set_color_jh(){
		Integer id = getParaToInt("id");
		Integer jh = getParaToInt("jh");
		Db.update("update f_color set jh = ? where id =?", jh, id);
	}
	
	public void updateVaseState(){
		boolean result = false;
		int id = getParaToInt(0);
		int state = getParaToInt(1);
		Record record = new Record();
		record.set("id", id);
		record.set("state", state);
		result = Db.update("f_flower_pro", record);
		renderJson("result", result);
	}
}
