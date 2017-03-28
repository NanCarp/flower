package com.controller.front;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dao.FCDao;
import com.interceptor.FrontInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.util.Constant;
import com.util.DesUtil;
import com.util.Constant.seedType;
import com.util.SendMsgUtil;
import sun.misc.BASE64Encoder;

/**
 * @Desc 会员相关
 * @author lxx
 * @date 2016/8/31
 * */
@Before(FrontInterceptor.class)
public class FrontAccountCtrl extends Controller {
	// 会员中心
	public void center(){
		Record account_1 = getSessionAttr("account");
		Record account = Db.findFirst("select * from f_account where id=?",account_1.getInt("id"));
		setAttr("account", account);
		// 检测今日是否签到
		Number num = Db.queryNumber("select count(1) from f_flowerseed where aid=? and type=? and ctime=curdate()", account.getInt("id"), seedType.sign.name());
		setAttr("sign", num.intValue());
		setAttr("ordercount", FCDao.orderCount(account.getInt("id")));
		setAttr("yqhy", FCDao.getInviteFriend());
		setAttr("cashcount", Db.queryLong("SELECT COUNT(id) FROM f_cash WHERE state=1 AND aid=? and CURDATE()>=time_a AND CURDATE()<=time_b", account.getInt("id")));
		render("center.html");
	}
	// 签到
	public void signin(){
		Record account = getSessionAttr("account");
		renderJson(FCDao.signin(account.getInt("id")));
	}
	// 绑定手机号
	public void binding(){
		render("binding.html");
	}
	// 获取验证码
	public void getBindingCode() throws Exception{
		String number = getPara("number");
		int result = SendMsgUtil.sendMsg(number, getSession());
		renderJson(result);
	}
	// 保存绑定号码
	public void saveBinding(){
		String number = getPara("number");
		String msgcode = getPara("msgcode");
		String bindingcode = getSessionAttr("bindingcode");
		renderJson(FCDao.saveBinding(number, msgcode, bindingcode, getSession()));
	}
	// 我的地址
	public void address(){
		Record account = getSessionAttr("account");
		setAttr("addresslist", FCDao.getAddress(account.getInt("id")));
		render("address.html");
	}
	// 新增地址
	public void addAddress(){
		// 跳转参数
		String queryString = getRequest().getQueryString();
		setAttr("queryString", queryString);
		setAttr("areajson", FCDao.getArea());
		render("address_add.html");
	}
	// 修改地址-获得详细
	public void getAddress(){
		int id = getParaToInt("id");
		// 跳转参数
		String queryString = getRequest().getQueryString();
		setAttr("queryString", queryString);
		setAttr("areajson", FCDao.getArea());
		setAttr("address", Db.findById("f_address", id));
		render("address_detail.html");
	}
	// 保存地址
	public void saveAddress(){
		Integer id = getParaToInt("id");
		Integer state = getParaToInt("state");
		String name = getPara("name");
		String tel = getPara("tel");
		String area = getPara("area");
		String addr = getPara("addr").trim().replace(" ", "");
		int give = getParaToInt("give");
		Record account = getSessionAttr("account");
		boolean result = FCDao.saveAddress(id, state, account.getInt("id"), name, tel, area, addr, give);
		renderJson(result);
	}
	// 设置默认地址
	public void setDefault(){
		int id = getParaToInt("id");
		Record account = getSessionAttr("account");
		renderJson(FCDao.setDefault(id, account.getInt("id")));
	}
	// 删除地址
	public void delAddress(){
		int id = getParaToInt("id");
		renderJson(Db.deleteById("f_address", id));
	}
	// 意见反馈
	public void feedback(){
		render("feedback.html");
	}
	// 提交反馈
	public void saveFeedback(){
		String title = getPara("title");
		String info = getPara("info");
		Record account = getSessionAttr("account");
		renderJson(FCDao.saveFeedback(title, info, account.getInt("id")));
	}
	// 我的花票
	public void mycash(){
		Record account = getSessionAttr("account");
		setAttr("mycash", FCDao.getCashList(account.getInt("id")));
		render("mycash.html");
	}
	// 分享花票页面
	public void sharecash(){
		String cl = getPara();
		List<Record> sharelist = FCDao.getFriendCash(cl);
		setAttr("account", getSessionAttr("account"));
		setAttr("sharelist", sharelist);
		setAttr("cl", cl);
		render("sharecash.html");
	}
	// 删除赠送好友的花票
	public void deletecash(){
		String cl = getPara();
		Record account = getSessionAttr("account");
		int uNum = Db.update("update f_cash set state = 0,origin = 5,aid_f = ? where id in ("+cl+") and aid =?", account.getInt("id"), account.getInt("id")); // 赠送好友的花票状态为0：未激活
		renderJson(uNum==0?false:true);
	}
	// 领花票引导页
	public void getgfcash(){
		String cl = getPara();
		List<Record> fcashlist = FCDao.getCashFriend(cl);
		Integer aid = FCDao.getcashforId(cl);
		Record account = getSessionAttr("account");
		if(account.getInt("id").equals(aid)){
			setAttr("self", false);
		}else{
			setAttr("self", true);
		}
		setAttr("cl",cl);
		setAttr("fcashlist", fcashlist);
		render("friendcash.html");
	}
	// 好友领取花票
	public void receiveFriCash(){
		String cid = getPara();
		boolean result = false;
		String msg = new String();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Record account_session = getSessionAttr("account");
		int aid_f = Db.queryInt("select distinct aid_f from f_cash where id in ("+cid+") and state = 0 and origin = 5");
		if(aid_f == account_session.getInt("id")){
			msg = "此花票已赠送";
		}else{
			Record account = Db.findById("f_account", account_session.getInt("id"));
			if(account.getInt("isfans")==0){
				int gNum = Db.update("update f_cash set aid =?,state=1 where id in ("+cid+") and state=0 and origin = 5", account.getInt("id"));
				result = gNum==0?false:true;
				if(result){
					msg = "领取成功";
				}else{
					msg = "不能重复领哦";
				}
			}else{
				msg = "先关注花美美才能领取好友花票哦！";
			}
		}
		resultMap.put("result", result);
		resultMap.put("msg", msg);
		renderJson(resultMap);
	}
	// 领取花票
	public void receiveCash(){
		Record account = getSessionAttr("account");
		setAttr("list", FCDao.receiveCash(account.getInt("id")));
		render("receivecash.html");
	}
	// 激活花票
	public void activateCash(){
		int id = getParaToInt("id");
		String code = getPara("code");
		Calendar now = Calendar.getInstance();
		// 有效天数
		int ltime = Db.queryInt("SELECT c.ltime FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid=b.id LEFT JOIN f_cashtheme c ON b.tid=c.id WHERE a.id=?", id);
		now.add(Calendar.DAY_OF_MONTH, ltime);
		int result = Db.update("update f_cash set state=1,time_a=?,time_b=? where id=? and code=? and state=0", new Date(), now.getTime(), id, code);
		renderJson(result==1?true:false);
	}
	// 领取花票页面
	public void cashexact(){
		int type = getParaToInt("type");
		setAttr("type", type);
		render("cash_exact.html");
	}
	// 激活花票
	public void getcashexact(){
		Record account = getSessionAttr("account");
		int type = getParaToInt("type");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String msg = "";
		boolean result = false;
		String ids = new String();
		if(type==1){
			ids = Db.queryStr("SELECT GROUP_CONCAT(b.id) FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id = b.tid WHERE a.tpid = 4 AND b.state = 0");
		}else{
			ids = Db.queryStr("SELECT GROUP_CONCAT(b.id) FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id = b.tid left join f_dictionary c on a.name = c.code_value WHERE c.code_key = 'cash' AND b.state = 0");
		}
		Number hpNum = Db.queryNumber("select count(1) from f_cash where state = 0 and aid =? and cid in ("+ids+")", account.getInt("id"));
		if(hpNum.intValue() > 0){
			Number gqNum = Db.queryNumber("select count(1) from f_cash where state = 0 and aid =? and cid in ("+ids+") and time_b > now()", account.getInt("id"));
			if(gqNum.intValue() > 0){
				Db.update("update f_cash set state = 1 where aid =? and cid in ("+ids+") and state = 0", account.getInt("id"));
				result = true;
				msg = "花票领取成功";
			}else{
				msg = "该花票已过期";
			}
		}else{
			msg = "你已领取了该花票";
		}
		resultMap.put("result", result);
		resultMap.put("msg", msg);
		resultMap.put("aid", account.getInt("id"));
		renderJson(resultMap);
	}
	// 我的种植花束
	public void flowerseed(){
		Record account = getSessionAttr("account");
		setAttr("account", account);
		setAttr("seedcount", FCDao.getFlowerSeed(account.getInt("id")));
		render("flowerseed.html");
	}
	// 兑换花束页面
	public void flowersubs(){
		Record account = getSessionAttr("account");
		setAttr("seedcount", FCDao.getFlowerSeed(account.getInt("id")));
		render("flowerchange.html");
	}
	
	// 邀请好友
	public void invitefri() throws Exception{
		Integer pageno = getParaToInt(0)==null?1:getParaToInt(0);
		Integer type = getParaToInt(1)==null?1:getParaToInt(1);
		setAttr("type", type);
		Record account = getSessionAttr("account");
		String idStr = new DesUtil().encrypt(account.getInt("id").toString());
		setAttr("scancode", idStr);
		setAttr("dimem", 1);  //区分 地推人员二维码 和 会员二维码
		Page<Record> page = null;
		if(type == 1){
			String url = account.getStr("qrurl");
			if(url == null){
				url = HttpKit.get(Constant.getHost + "/weixin/createQrCode/1-" + account.getInt("id"));
				account.set("qrurl", url);
				Db.update("f_account", account);
			}
			setAttr("qrurl", account.getStr("qrurl"));
			setAttr("headimg", base64Encode(account.getStr("headimg")));
		}else{
			int aid = account.getInt("id"); 
			page = FCDao.findOrderMember(pageno, 16, aid, 1);
			setAttr("pageno", page.getPageNumber());
			setAttr("totalpage", page.getTotalPage());
			setAttr("totalrow", page.getTotalRow());
			setAttr("ormems",page.getList());
		}
		setAttr("yqhy", FCDao.getInviteFriend());
		if(pageno == 1){
			render("invitefriend.html");
		}else{
			renderJson(page.getList());
		}
	}
	
	// 查看祝福卡
	public void seecardinfo(){
		int type = getParaToInt()==null?2:getParaToInt(); // 1:使用其他手机；2:扫二维码
		if(type==2){
			Record account = getSessionAttr("account");
			Record user = Db.findFirst("select tel,isfans from f_account where id=?", account.getInt("id"));
			if(user.getInt("isfans")==1){
				setAttr("msg", "请先关注花美美，在查看祝福卡");
			}
			setAttr("tel", user.getStr("tel"));
		}
		render("seecard.html");
	}
	
	// 获得祝福卡
	public void cardget(){
		String tel = getPara();
		List<Record> cards = FCDao.getCards(tel);
		setAttr("cards", cards);
		render("card_get.html");
	}
	
	// 祝福卡内容
	public void cardcontent(){
		String ordercode = getPara();
		Record zhufu = FCDao.getCardInfo(ordercode);
		setAttr("zhufu", zhufu);
		render("card_detail.html");
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
}