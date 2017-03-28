package com.config;

import java.util.Timer;
import com.controller.UploadCtrl;
import com.controller.WeixinApiCtrl;
import com.controller.WeixinMsgController;
import com.controller.WuLiuCtrl;
import com.controller.front.FrontAccountCtrl;
import com.controller.front.FrontIndexCtrl;
import com.controller.front.FrontOnlineCtrl;
import com.controller.front.FrontServiceCtrl;
import com.controller.manage.ManageArticleCtrl;
import com.controller.manage.ManageBenefitCtrl;
import com.controller.manage.ManageIndexCtrl;
import com.controller.manage.ManageLogisticsCtrl;
import com.controller.manage.ManageMemberCtrl;
import com.controller.manage.ManageOrderCtrl;
import com.controller.manage.ManageProductCtrl;
import com.controller.manage.ManageServiceCtrl;
import com.controller.manage.ManageSpreadCtrl;
import com.controller.manage.ManageSystomCtrl;
import com.handler.WebSocketHandler;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.util.TaskService;

public class WeixinConfig extends JFinalConfig {
	
	private Timer timer = new Timer();
		
	@Override
	public void configConstant(Constants me) {
		PropKit.use("config.txt");
		me.setDevMode(true);
		ApiConfigKit.setDevMode(me.getDevMode());
		me.setError404View("/error/404.html");
		me.setError500View("/error/500.html");
	}

	@Override
	public void configRoute(Routes me) {
		/*************** 前台 **************/
		// 首页
		me.add("/", FrontIndexCtrl.class, "/front");
		// 会员相关
		me.add("/account", FrontAccountCtrl.class, "/front/account");
		// 业务相关
		me.add("/service", FrontServiceCtrl.class, "/front/service");
		// 在线客服
		me.add("/online", FrontOnlineCtrl.class, "/front/online");
		
		/*************** 后台 **************/
		me.add("/manage", ManageIndexCtrl.class);
		// 商品管理
		me.add("/manage/iframe/product", ManageProductCtrl.class);
		// 订单管理
		me.add("/manage/iframe/order", ManageOrderCtrl.class);
		// 系统管理
		me.add("/manage/iframe/systom", ManageSystomCtrl.class);
		// 服务管理
		me.add("/manage/iframe/service", ManageServiceCtrl.class);
		// 文章管理
		me.add("/manage/iframe/article", ManageArticleCtrl.class);
		// 物流管理
		me.add("/manage/iframe/logistics", ManageLogisticsCtrl.class);
		// 会员管理
		me.add("/manage/iframe/member", ManageMemberCtrl.class);
		// 优惠管理
		me.add("/manage/iframe/benefit", ManageBenefitCtrl.class);
		// 推广管理
		me.add("/manage/iframe/spread", ManageSpreadCtrl.class);
		
		/*************** 微信 **************/
		me.add("/weixin", WeixinApiCtrl.class, "/manage/iframe");
		
		/*************** 文件上传 **************/
		me.add("/upload", UploadCtrl.class);
		
		/*************** 物流 **************/
		me.add("/wuliu", WuLiuCtrl.class, "/");
		
		/****************消息处理**************/
		me.add("/msg",WeixinMsgController.class);
		
	}

	@Override
	public void configPlugin(Plugins me) {
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		c3p0Plugin.setMinPoolSize(50);		// 连接池中保留的最小连接数
		c3p0Plugin.setMaxPoolSize(200);		// 连接池中保留的最大连接数
		c3p0Plugin.setInitialPoolSize(50);	// 初始链接数
		c3p0Plugin.setMaxIdleTime(60);		// 每60秒检查所有连接池中的空闲连接。Default: 0
		c3p0Plugin.setAcquireIncrement(10);	// 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
	}

	@Override
	public void configInterceptor(Interceptors me) {}

	@Override
	public void configHandler(Handlers me) {
		me.add(new WebSocketHandler());
	}

	@Override
	public void afterJFinalStart() {
		// TODO Auto-generated method stub
		super.afterJFinalStart();
		timer.schedule(new TaskService(), 1000*60*60*6, 1000*60*60*24*4);  
	}

	@Override
	public void beforeJFinalStop() {
		// TODO Auto-generated method stub
		super.beforeJFinalStop();
		timer.cancel();  
	}
}