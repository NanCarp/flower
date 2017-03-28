package com.controller.front;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.dao.FCDao;
import com.dao.MCDao;
import com.interceptor.FrontInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.DesUtil;
import com.util.SignUtil;

import sun.misc.BASE64Encoder;

@Before(FrontInterceptor.class)
public class FrontIndexCtrl extends Controller {
	// 首页
	public void index(){
		setAttr("flower1", FCDao.getProductInfo(1));	// 双品
		setAttr("flower2", FCDao.getProductInfo(2));	// 多品
		Record account = getSessionAttr("account");
		int isbuy = account.getInt("isbuy");
		setAttr("aroundlist", FCDao.getAround(isbuy));	// 周边
		render("index.html");
	}
	// 花边好物列表
	public void around(){
		Record account = getSessionAttr("account");
		int isbuy = account.getInt("isbuy");
		setAttr("aroundlist", FCDao.getAround(isbuy));
		render("around.html");
	}
	// 花边好物详情
	public void aroundinfo(){
		int pid = getParaToInt();
		setAttr("flower", FCDao.getAroundInfo(pid));
		render("product_around.html");
	}
	// 商品详情
	public void product(){
		int pid = getParaToInt(0);
		int ptid = getParaToInt(1)==null?pid:getParaToInt(1);
		Record account = getSessionAttr("account");
		int isbuy = account.getInt("isbuy");
		setAttr("isbuy", isbuy);	// 判断是否第一次购物
		setAttr("activitylist", JsonKit.toJson(FCDao.getActivityList()));	//有效时间内的活动
		if(ptid==1 || ptid==2){
			Record pro = FCDao.getProductInfo(pid);
			String imgs = pro.getStr("imgurl");
			if(imgs.indexOf("-")!= -1){
				String[] ims = imgs.split("-");
				pro.set("imgurl1", ims[0]);
				pro.set("imgurl2", ims[1]);
				pro.set("imgurl3", ims[2]);
			}else{
				pro.set("imgurl1", imgs);
				pro.set("imgurl2", imgs);
				pro.set("imgurl3", imgs);
			}
			setAttr("flower", pro);	// 双品与多品
			
			setAttr("vaselist", FCDao.getProducts(4, isbuy));		// 花瓶
			setAttr("dmlj", FCDao.getDmlj());				// 多加一次，价格立减(双品)
			setAttr("dmlj2", FCDao.getDmlj2());				// 多加一次，价格立减(多品)
			render("product.html");
		}else if(ptid == 3){
			setAttr("givelist", FCDao.getProducts(3, isbuy));		// 送花系列
			setAttr("vaselist", FCDao.getProducts(4, isbuy));		// 花瓶
			setAttr("idoneitylist", Db.find("select id,title,imgurl from f_idoneity where state = 0"));	// 适赠对象
			setAttr("givetitle", Db.queryStr("SELECT code_value FROM f_dictionary WHERE code_key = 'give'"));
			render("product_give.html");
		}else{
			setAttr("flower", FCDao.getAroundInfo(pid));
			render("product_around.html");
		}
	}
	// 生活美学
	public void esthetics(){
		Integer pageno = getParaToInt()==null?1:getParaToInt();
		Page<Record> page = FCDao.getEstheticsPage(pageno, 16);
		if(pageno == 1){
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("estheticslist", page.getList());
			render("esthetics.html");
		}else{
			renderJson(page.getList());
		}
	}
	// 生活美学详情
	public void esthetics_detail(){
		int id = getParaToInt();
		setAttr("esthetics", Db.findById("f_esthetics", id));
		render("esthetics_detail.html");
	}
	// 投稿须知
	public void esthetics_notice(){
		render("esthetics_notice.html");
	}
	// 常见问题
	public void question(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Page<Record> page = MCDao.getQuestion(pageno, 16);
		if(pageno == 1){
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("questionlist", page.getList());
			render("question.html");
		}else{
			renderJson(page.getList());
		}
	}
	// 养花须知
	public void knowledge(){
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Integer type = getParaToInt(1)==null?1:getParaToInt(1);
		setAttr("type", type);
		Page<Record> page = null;
		if(type == 1){
			page = MCDao.getKnowledge(pageno, 28, type, null);
		}else{
			page = MCDao.getKnowledge(pageno, 16, type, null);
		}
		if(pageno == 1){
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("viewlist",page.getList());
			render("knowledge.html");
		}else{
			renderJson(page.getList());
		}
	}
	// 养花须知详情
	public void knowledge_detail(){
		int id = getParaToInt();
		setAttr("knowledge", Db.findById("f_knowledge", id));
		render("knowledge_detail.html");
	}
	// 物流查询
	public void logistics_query(){
		render("logistics_query.html");
	}
	// 领花引导页
	public void receive_order(){
		setAttr("ordercode", getPara());
		setAttr("account", getSessionAttr("account"));
		setAttr("areajson", FCDao.getArea());
		render("receive.html");
	}
	// 领花提取码验证
	public void receive_code_valid(){
		String ordercode = getPara("ordercode");
		String code = getPara("code");
		renderJson(FCDao.validCode(ordercode, code));
	}
	// 领花
	public void getorderreceive(){
		String ordercode = getPara("ordercode");
		String code = getPara("code");
		String name = getPara("name");
		String tel = getPara("tel");
		String area = getPara("area");
		String addr = getPara("addr");
		Record account = getSessionAttr("account");
		renderJson(FCDao.getOrderReceive(account.getInt("id"), ordercode, code, name, tel, area, addr));
	}
	// 地推人员推广页面
	@Clear
	public void spread() throws Exception{
		String idStr = getPara(0);
		Integer pageno = getParaToInt(1)==null?1:getParaToInt(1);
		Integer type = getParaToInt(2)==null?1:getParaToInt(2);
		Page<Record> page = null;
		setAttr("type", type);
		setAttr("scancode", idStr);
		setAttr("dimem", 2);   //区分 地推人员二维码 和 会员二维码
		Record spread = Db.findById("f_spread", Integer.parseInt(new DesUtil().decrypt(idStr)));
		if(type == 1){
			setAttr("qrurl", spread.getStr("qrurl"));
		}else{
			int id = spread.getInt("id"); 
			page = FCDao.findOrderMember(pageno, 16, id, 2);
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("ormems",page.getList());
		}
		setAttr("yqhy", FCDao.getInviteFriend());
		if(pageno == 1){
			render("account/invitefriend.html");
		}else{
			renderJson(page.getList());
		}
	}
	
	// 分享二维码
	@Clear
	public void shareqcode() throws NumberFormatException, Exception{
		int dimem = getParaToInt(0);
		String scancode = getPara(1);
		int id = Integer.parseInt(new DesUtil().decrypt(scancode));
		String qrurl = new String();
		if(dimem == 1){
			Record record = Db.findFirst("select headimg,qrurl from f_account where id = ?", id);
			qrurl = record.getStr("qrurl");
			setAttr("headimg", base64Encode(record.getStr("headimg")));
		}else{
			qrurl = Db.queryStr("select qrurl from f_spread where id = ?", id);
		}
		setAttr("yqhy", FCDao.getInviteFriend());
		setAttr("qrurl", qrurl);
		render("shareqcode.html");
	}
	
	public static String base64Encode(String imgurl) throws IOException {
		URL url = new URL(imgurl);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5 * 1000);
		InputStream is = con.getInputStream();
        
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = is.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] data = swapStream.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
	}
	
	// 禁用账号进入页面
	@Clear
	public void freeze(){
		render("freeze.html");
	}
	
	// 微信握手验证
	@Clear
	public void CoreServlet(){
		// 微信加密签名
		String signature = getPara("signature");
		// 时间戳
		String timestamp = getPara("timestamp");
		// 随机数
		String nonce = getPara("nonce");
		// 随机字符串
		String echostr = getPara("echostr");

		// 通过检验signature 对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			renderText(echostr);
		}else{
			renderNull();
		}
	}
	
	// js域名验证
	@Clear
	public void mp_verify(){
		renderText("TfvqjMOIvPJVUR0D");
	}
	
	@Clear
	public void dbtest(){
		List<Record> list = Db.find("select id,jihui,jh_list from f_order where jh_list is not null");
		for(Record record : list){
			Record jh = Db.findFirst("SELECT GROUP_CONCAT(DISTINCT a.tid) AS id,GROUP_CONCAT(DISTINCT b.name) AS name FROM f_flower a LEFT JOIN f_flower_type b ON a.tid=b.id WHERE a.id IN ("+record.getStr("jh_list")+")");
			record.set("jihui", jh.getStr("name"));
			record.set("jh_list", jh.getStr("id"));
			//Db.update("f_order", record);
		}
		renderJson(list);
	}
	
	@Clear
	public void canvas(){
		render("canvas.html");
	}
	
	//联系我们
	public void contactus(){
		render("contactus.html");
	}
}