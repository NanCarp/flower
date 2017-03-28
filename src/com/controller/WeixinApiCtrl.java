package com.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.dao.MCDao;
import com.google.gson.Gson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.CustomServiceApi.Articles;
import com.jfinal.weixin.sdk.api.GroupsApi;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.MediaApi;
import com.jfinal.weixin.sdk.api.MediaApi.MediaType;
import com.jfinal.weixin.sdk.api.JsTicketApi.JsApiType;
import com.jfinal.weixin.sdk.api.MenuApi;
import com.jfinal.weixin.sdk.api.MessageApi;
import com.jfinal.weixin.sdk.api.QrcodeApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.util.Constant;
import com.util.Sign;
import com.util.Signature;
import com.util.XMLParser;

/**
 * @Desc 微信设置
 * @author lxx
 * @date 2016/8/23
 * */
public class WeixinApiCtrl extends ApiController {
	@Override
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量
        ac.setToken(PropKit.get("token"));
        ac.setAppId(PropKit.get("appId"));
        ac.setAppSecret(PropKit.get("appSecret"));
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
        return ac;
	}
    
    // 创建自定义菜单
    public void createmenu() {
    	String path = Constant.getHost;
    	String jsonstr = "{" +
				"    \"button\": [" +
				"        {" +
				"            \"name\": \"鲜花订阅\"," +
				"            \"sub_button\": [" +
				"			 	{\"name\": \"多品鲜花订阅 | 59.99元\",\"type\": \"view\",\"url\": \"" + path + "/product/2\"}," +
				"			 	{\"name\": \"双品鲜花订阅 | 39.99元\",\"type\": \"view\",\"url\": \"" + path + "/product/1\"}," +
				"				{\"name\": \"我要送花| 69.99元\",\"type\": \"view\",\"url\": \"" + path + "/product/3\"}," +
				"				{\"name\": \"花边好物 | 花瓶 花剪\",\"type\": \"view\",\"url\": \"" + path + "/around\"}," +
				"				{\"name\": \"轻松赚花票\",\"type\": \"view\",\"url\": \"" + path + "/account/invitefri\"}]" +
				"        }," +
				"        {" +
                "            \"name\": \"小美秘密\"," +
				"            \"sub_button\": [" +
                "            	{\"name\": \"生活美学\",\"type\": \"view\",\"url\": \"" + path + "/esthetics\"}," +
                "				{\"name\": \"养护 | 搭配\",\"type\": \"view\",\"url\": \"" + path + "/knowledge\"}," +
                "				{\"name\": \"晒 晒 晒\",\"type\": \"view\",\"url\": \"http://www.webei.cn/1b434a4e7d\"}," +
				"           	{\"name\": \"我要带颜\",\"type\": \"view\",\"url\": \""+ path + "/account/invitefri\"}]" +
				"        }," +
				"        {" +
				"    		 \"name\": \"为您服务\"," +
				"    		 \"sub_button\": ["+
				"			 	{\"name\": \"会员中心\",\"type\": \"view\",\"url\": \"" + path + "/account/center\"}," +
				"			 	{\"name\": \"在线客服\",\"type\": \"click\",\"key\": \"32\"}," +
				"           	{\"name\": \"联系我们\",\"type\": \"view\",\"url\": \""+ path + "/contactus\"},"+
				"			 	{\"name\": \"物流查询\",\"type\": \"view\",\"url\": \"" + path + "/logistics_query\"}," +
				"			 	{\"name\": \"常见问题\",\"type\": \"view\",\"url\": \"" + path + "/question\"}]" +
				"        }" +
				"    ]" +
				"}";
    	ApiResult apiResult = MenuApi.createMenu(jsonstr);
    	renderText(apiResult.getJson());
    }
    
    // config接口注入权限验证配置
    public void configvalid(){
    	JsTicket jt = JsTicketApi.getTicket(JsApiType.jsapi);
    	Map<String, String> map = Sign.sign1(jt.getTicket(), getPara("url"));
    	renderJson(map);
    }
    
    // 微信支付异步通知回调地址
 	public void wxpayback() throws IOException, ParserConfigurationException, SAXException{
 		ServletInputStream in = getRequest().getInputStream();
		int size = getRequest().getContentLength();
		if(size>0){
			byte[] bdata = new byte[size];
			in.read(bdata);
			String xmlStr = new String(bdata, XMLParser.getCharacterEncoding(getRequest(), getResponse()));
			if(Signature.SignValidIsFromWeXin(xmlStr)){
				Map<String,Object> paramsMap = XMLParser.getMapFromXML(xmlStr);
				String return_code = paramsMap.get("return_code").toString();
				String out_trade_no = paramsMap.get("out_trade_no").toString();
				if ("SUCCESS".equals(return_code)) {
					if(MCDao.paySuccess(out_trade_no)){
						renderJson("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
					}else{
						renderJson("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERR]]></return_msg></xml>");
					}
				}else{
					renderJson("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERR]]></return_msg></xml>");
				}
			}
		}
		// 不是来自微信的回调通知
		renderJson("bitch");
 	}
 	// 花票推送页面
 	public void toPushCash(){
 		int id = getParaToInt();
 		ApiResult ar = GroupsApi.get();
 		setAttr("id", id);
 		setAttr("groupList", ar.getList("groups"));
 		render("/manage/iframe/benefit/cash_push.html");
 	}
 	// 花票推送
 	public void pushcash(){
 		int id = getParaToInt("id");
 		Integer gid = getParaToInt("gid");
 		Map<String, Object> jsonMap = new HashMap<>();
 		boolean result = false;
 		String msg = new String();
 		// 获取要推送的花票主题
 		Record cashtheme = Db.findFirst("select id,state from f_cashtheme where id=? and state=0", id);
 		if(cashtheme != null){
 			// 获取花票主题下的花票类型
 			List<Record> list = Db.find("select id from f_cashclassify where tid = ? and state=0", id);
 			if(list.size() > 0){
 				if(MCDao.pushvalid()){
	 				String media_id = "5OFYNFbIr2ffmnyyysS5XMRNEiq8ZbYUdd-KWqNydc0";	// 素材ID
	 				String jsonStr = new String();
	 				if(gid == null){
		 				jsonStr = "{" +
		 		 				"	\"filter\":{\"is_to_all\":true},"+
		 		 				"	\"mpnews\":{\"media_id\":\"" + media_id + "\"},"+
		 		 				"	\"msgtype\":\"mpnews\"}";
	 				}else{
	 					jsonStr = "{" +
		 		 				"	\"filter\":{\"is_to_all\":false,\"tag_id\":\"" + gid + "\"},"+
		 		 				"	\"mpnews\":{\"media_id\":\"" + media_id + "\"},"+
		 		 				"	\"msgtype\":\"mpnews\"}";
	 				}
	 		 		ApiResult apiResult = MessageApi.sendAll(jsonStr);
	 		 		if(apiResult.getInt("errcode")==0){
	 		 			cashtheme.set("state", 1);
	 		 			cashtheme.set("ptime", new Date());
	 		 			Db.update("f_cashtheme", cashtheme);
	 		 			result = true;
	 		 			msg = "推送提交成功";
	 		 		}else{
 		 				msg = "推送提交失败";
 		 			}
 				}else{
 					msg = "每月最多推送4次";
 				}
 			}else{
 				msg = "请录入花票类型";
 			}
 		}else{
 			msg = "花票主题不存在";
 		}
 		jsonMap.put("result", result);
 		jsonMap.put("msg", msg);
 		renderJson(jsonMap);
 	}
 	// 花票精确投送
 	public void pushcashexact(){
 		int id = getParaToInt();
 		Record admin = getSessionAttr("admin");
 		String openid = Db.queryStr("select openid from f_account where id = ?", id);
 		Articles article = new Articles();
 		article.setTitle("您有新的花票需要领取");
 		article.setDescription("遇见你，生活美美！");
 		article.setUrl(Constant.getHost + "/account/cashexact?type=1");
 		article.setPicurl("http://www.flowermm.net/image/1478487469270.jpeg");
 		List<Articles> list = new ArrayList<>();
 		list.add(article);
 		ApiResult ar = CustomServiceApi.sendNews(openid, list);
 		if(ar.getInt("errcode") == 0){
 		// 精确投放花票
			List<Record> cashlist = Db.find("SELECT a.ltime,b.id FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id=b.tid WHERE a.ltime>0 and b.state=0 and a.tpid=4");
			for(Record cash : cashlist){
				Record c = new Record();
				c.set("aid", id);
				c.set("cid", cash.getInt("id"));
				c.set("code", "4444");
				Calendar now = Calendar.getInstance();
				c.set("time_a", now.getTime());
				now.add(Calendar.DAY_OF_MONTH, cash.getInt("ltime"));
				c.set("time_b", now.getTime());
				c.set("state", 0);
				c.set("pushid", admin.getInt("id"));
				c.set("origin", 4);
				Db.save("f_cash", c);
			}
		}
 		renderJson(ar.getJson());
 	}
 	
 	// 领取花票推送链接
 	public void sendinfocash(){
 		int aid = getParaToInt();
 		String openid = Db.queryStr("select openid from f_account where id = ?", aid);
 		String message = "花票领取成功，可在会员中心查看。立即去使用:<a href='" + Constant.getHost + "/product/1'>【订阅双品花束】</a>、<a href='" + Constant.getHost + "/product/2'>【订阅多品花束】</a>、<a href='" + Constant.getHost + "/product/3'>【我要送花】</a>";
 		ApiResult ar = CustomServiceApi.sendText(openid, message);
 		renderJson(ar.getJson());
 	}
 	
 	// 获取素材列表
 	public void getmedia(){
 		ApiResult apiResult = MediaApi.batchGetMaterial(MediaType.NEWS, 0, 10);
 		renderJson(apiResult.getJson());
 	}
 	
 	// 获取模板ID
 	public static String gettemplateId(){
 		String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token="+AccessTokenApi.getAccessTokenStr();
 		String json = HttpUtils.get(url);
 		String temp = json.substring(json.indexOf("["), json.indexOf("]")+1);
 		Gson gson = new Gson();
 		String tid = new String();
		List<Map<String, Object>> tlist = gson.fromJson(temp, ArrayList.class);
		for (Map<String, Object> map : tlist) {
			if("购买成功通知".equalsIgnoreCase((String) map.get("title"))){
				tid = (String)map.get("template_id");
			}
		}
		return tid;
 	}
 	
 	// 获得模版消息
 	public void gettemplate(){
 		String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token="+AccessTokenApi.getAccessTokenStr();
 		String json = HttpUtils.get(url);
 		renderJson(json);
 	}
 	
 	//发送模板消息
    public static ApiResult sendTemplateMsg(String jsonStr) {
        String jsonResult = HttpKit.post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + AccessTokenApi.getAccessToken(), jsonStr);
        return new ApiResult(jsonResult);
    }
 	
 	/**
 	 * @Desc 查询用户分组列表
 	 * @author yetangtang
 	 * @dete 2016/11/10
 	 * @param null
 	 * @return null
 	 */
 	public void getGroups(){
 		ApiResult ar = GroupsApi.get();
 		renderJson(ar.getList("groups"));
 	}
 	public void getUserGroupList(){
 		ApiResult ar = GroupsApi.get();
 		setAttr("groupList", ar.getList("groups"));
 		render("member/group_list.html");
 	}
 	// 获取分组详情
 	public void getGroup() throws UnsupportedEncodingException{
 		Integer id = getParaToInt(0);
 		String name = getPara(1);
 		if(name != null){
 			name = URLDecoder.decode(name, "utf-8");
 		}
 		setAttr("id", id);
 		setAttr("name", name);
 		render("member/group_detail.html");
 	}
 	// 保存分组信息
 	public void saveUserGroup(){
 		Integer id = getParaToInt("id");
 		String name = getPara("name");
 		if(id == null){
 			GroupsApi.create(name);
 		}else{
 			GroupsApi.update(id, name);
 		}
 		renderNull();
 	}
 	// 删除分组
 	public void delUserGroup(){
 		Integer id = getParaToInt();
 		GroupsApi.delete(id);
 		Db.update("update f_account set gid= 0 where gid= ?",id);
 		renderNull();
 	}
 	// 移动用户分组
 	public void editUserGroup(){
 		Integer id = getParaToInt(0);
 		Integer rid = getParaToInt(1);
 		String openid = Db.queryStr("select openid from f_account where id=?", id);
 		GroupsApi.membersUpdate(openid, rid);
 		renderNull();
 	}
 	// 创建二维码
 	public void createQrCode(){
 		int type= getParaToInt(0);	// 推荐人类型(1微信用户/2地推人员)
 		int id = getParaToInt(1);	// 用户ID
 		ApiResult ar = QrcodeApi.createPermanent(type + "_" + id);
 		Gson gson = new  Gson();
 		Map<?,?> map = gson.fromJson(ar.getJson(), HashMap.class);
 		String url = (String) map.get("url");
 		renderText(url);
 	}

}