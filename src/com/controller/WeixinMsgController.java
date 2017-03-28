package com.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.dao.FCDao;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.CustomServiceApi.Articles;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.event.EventInMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.util.Constant;

/**
 * @Desc 关注消息
 * @author yeQing
 */
public class WeixinMsgController extends MsgControllerAdapter {

	static Log logger = Log.getLog(WeixinMsgController.class);
	
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
	
	/**
	 * 实现父类抽方法，处理关注/取消关注消息
	 */
	protected void processInFollowEvent(InFollowEvent inFollowEvent){
		if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())){
			logger.debug("关注：" + inFollowEvent.getFromUserName());
			sendInfo2User(inFollowEvent, null, null);
		}
		// 如果为取消关注事件，将无法接收到传回的信息
		else if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent())){
			logger.debug("取消关注：" + inFollowEvent.getFromUserName());
			Db.update("update f_account set isfans = 1 where openid = ?", inFollowEvent.getFromUserName());
		}
	}

	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent){
		if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())){
			logger.debug("扫码未关注：" + inQrCodeEvent.getFromUserName());
			//切割字符串
			String[] eventArray = inQrCodeEvent.getEventKey().split("_");
			//获取二维码参数
			String typeId = eventArray[1];          //推荐类型
			String eventUserId = eventArray[2];     //推荐用户ID
			System.out.println(typeId);
			System.out.println(eventUserId);
			
			//添加用户数据进入推广action
			//添加成功完成关注公众号操作
			//调用函数处理，最好添加事务处理机制
			
			//发送消息给关注用户
			sendInfo2User(inQrCodeEvent, typeId, eventUserId);
		}
		if (InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent())){
			logger.debug("扫码已关注：" + inQrCodeEvent.getFromUserName());
		}
	}

	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		String key = inMenuEvent.getEventKey();
		if("32".equalsIgnoreCase(key)){
			CustomServiceApi.sendText(inMenuEvent.getFromUserName(), "请输入“转接客服”");
		}
	}
	
	@Override
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		 String fromUserName = inTemplateMsgEvent.getFromUserName();
		 String msgId = inTemplateMsgEvent.getMsgId();
		 String status = inTemplateMsgEvent.getStatus();
		 System.out.println("购买成功通知：fromUserName: "+fromUserName+"msgId: "+msgId+"status: "+status);
		 renderNull();
	}

	/**
	 * @Desc 回复消息送花票
	 */
	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		String textMsg = inTextMsg.getContent().trim();
		String openid = inTextMsg.getFromUserName();
		int aid = Db.queryInt("select id from f_account where openid =?", openid);
		Record msgR = Db.findFirst("SELECT code_value,note FROM f_dictionary WHERE code_key = 'cash'");
		//根据用户发送的消息自动回复
		OutTextMsg outTextMsg = new OutTextMsg(inTextMsg);
		if(msgR.getStr("note").equalsIgnoreCase(textMsg)){
			int cashid = Db.queryInt("select id from f_cashtheme where name = ?", msgR.getStr("code_value"));
			Articles article = new Articles();
	 		article.setTitle("回复送花票");
	 		article.setDescription("遇见你，生活美美！");
	 		article.setUrl(Constant.getHost + "/account/cashexact?type=2");
	 		article.setPicurl("http://www.flowermm.net/image/1473730470055.png");
	 		List<Articles> list = new ArrayList<>();
	 		list.add(article);
	 		ApiResult ar = CustomServiceApi.sendNews(openid, list);
	 		if(ar.getInt("errcode") == 0){
				List<Record> cashlist = Db.find("SELECT a.id,a.ltime,b.id FROM f_cashtheme a LEFT JOIN f_cashclassify b ON a.id=b.tid WHERE a.ltime>0 and b.state=0 and a.id=?", cashid);
			    int numN = Db.queryNumber("SELECT count(1) FROM f_cash a LEFT JOIN f_cashclassify b ON a.cid = b.id WHERE a.origin = 6 AND a.aid =? and b.tid=?", aid, cashid).intValue();
			    if(numN==0){
			    	for(Record cash : cashlist){
			    		Record c = new Record();
			    		c.set("aid", aid);
			    		c.set("cid", cash.getInt("id"));
			    		c.set("code", "5555");
			    		Calendar now = Calendar.getInstance();
			    		c.set("time_a", now.getTime());
			    		now.add(Calendar.DAY_OF_MONTH, cash.getInt("ltime"));	
			    		c.set("time_b", now.getTime());
			    		c.set("state", 0);
			    		c.set("pushid", 1);
			    		c.set("origin", 6);
			    		Db.save("f_cash", c);
			    	}
			    	renderJson(ar.getJson());
			    	outTextMsg.setContent("领取你的花票吧！");
			    }else{
			    	outTextMsg.setContent("此花票你已经领过哦");
			    }
			}
	 		render(outTextMsg);
		}else if("转接客服".equalsIgnoreCase(textMsg)){
			CustomServiceApi.sendText(openid, "正在连接客服，请等待...");
			render(new OutCustomMsg(inTextMsg));
		}else{
			outTextMsg.setContent("遇见你，生活美美~");
			render(outTextMsg);
		}
	}
	
	// 接收图片消息事件
	@Override
	protected void processInImageMsg(InImageMsg inImageMsg) {
		render(new OutCustomMsg(inImageMsg));
	}
	
	// 接收语音消息事件
	@Override
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		render(new OutCustomMsg(inVoiceMsg));
	}
	
	// 接收视频消息事件
	@Override
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		render(new OutCustomMsg(inVideoMsg));
	}
	
	// 接收地理位置消息事件
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		render(new OutCustomMsg(inLocationMsg));
	}
	
	// 接收链接消息事件
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		render(new OutCustomMsg(inLinkMsg));
	}
	
	// 接收小视频消息
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		render(new OutCustomMsg(inShortVideoMsg));
	}
	
	/**
	 * @Desc 关注事件的消息反馈
	 * @author yetangtang
	 * @date 2016/11/09
	 * @param Event
	 */
	private void sendInfo2User(EventInMsg Event, String typeId, String eventUserId) {
		String openId = Event.getFromUserName();
		Number num = Db.queryNumber("select count(1) from f_account where openid = ?", openId);
		if(num.intValue() == 0){
			CustomServiceApi.sendText(openId, "欢迎首次关注花美美！");
			FCDao.setAccount(null, openId, typeId, eventUserId);
			int ltime = Db.queryInt("SELECT ltime FROM f_cashtheme WHERE id = 2");
			if(ltime > 0){
				CustomServiceApi.sendText(openId, "首次关注花美美，获赠花票\n<a href=\"" + Constant.getHost + "/account/mycash\">立即查看>></a>");
			}
		}else{
			Db.update("update f_account set isfans = 0,state = 0 where openid = ?", openId);
		}
		
		OutTextMsg outMsg = new OutTextMsg(Event);
		//outMsg.setContent("感谢您的关注，二维码内容：" + inQrCodeEvent.getEventKey());
		String message = "遇见你，生活美美！\n"+
				 "花美美，每周一束，品种随机\n"+
				 "订阅<a href='" + Constant.getHost + "/product/1'>【双品花束】</a>，简约大方\n"+
				 "订阅<a href='" + Constant.getHost + "/product/2'>【多品花束】</a>，丰富多彩\n"+
				 "点击<a href='" + Constant.getHost + "/product/3'>【我要送花】</a>，传递情谊\n"+
				 "花田直采，花量大、花新鲜、花期长，比比就知道！";
		/*String message = "花美美，专做鲜花订阅服务的移动互联网平台。\n"+
				"花美美倡导“遇见你，生活美美”的生活理念，为用户提供双品花和多品花订阅。\n"+
				"每周一或周六将鲜花给您新鲜送达！给工作注入能量，给家庭增添温馨！";*/
		outMsg.setContent(message);
		render(outMsg);
	}
}