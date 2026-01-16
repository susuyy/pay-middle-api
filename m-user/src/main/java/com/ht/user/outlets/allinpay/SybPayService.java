package com.ht.user.outlets.allinpay;

import com.ht.user.outlets.controller.OutletsOrdersController;
import com.ht.user.outlets.exception.CheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class SybPayService {
	private static Logger logger = LoggerFactory.getLogger(SybPayService.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String,String> handleResult(String reqsn,String result) throws Exception{
		logger.info(reqsn+"-ret:"+result);
		Map map = SybUtil.json2Obj(result, Map.class);
		if(map == null){
			logger.info(reqsn+"通联异常,返回数据错误,数据为空");
			throw new CheckException(1500,"返回数据错误");
		}
		if("SUCCESS".equals(map.get("retcode"))){
			TreeMap tmap = new TreeMap();
			tmap.putAll(map);
			String appkey = SybConstants.SYB_MD5_APPKEY;
			if(SybUtil.validSign(tmap, appkey, SybConstants.SIGN_TYPE)){
				return map;
			}else{
				logger.info(reqsn+"通联异常,验证签名失败");
				throw new CheckException(1500,"验证签名失败");
			}
		}else{
			logger.info(reqsn+"通联异常,支付失败-"+map.get("retmsg").toString());
			throw new CheckException(1500,map.get("retmsg").toString());
		}
	}

	/**
	 * 扫码支付
	 * @param trxamt
	 * @param reqsn
	 * @param body
	 * @param remark
	 * @param authcode
	 * @param limitPay
	 * @param idno
	 * @param truename
	 * @param asinfo
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> scanPay(long trxamt,String reqsn,String body,String remark,String authcode,String limitPay,String idno,String truename,String asinfo) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/scanqrpay");
		http.init();
		TreeMap<String,String> params = new TreeMap<>();
		if(!SybUtil.isEmpty(SybConstants.SYB_ORGID)) {
			params.put("orgid", SybConstants.SYB_ORGID);
		}
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", reqsn);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("body", body);
		params.put("remark", remark);
		params.put("authcode", authcode);
		params.put("limit_pay", limitPay);
		params.put("asinfo", asinfo);
		params.put("signtype", SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;;

		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	/**
	 * 撤销
	 * @param trxamt
	 * @param reqsn
	 * @param oldtrxid
	 * @param oldreqsn
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> cancel(long trxamt,String reqsn,String oldtrxid,String oldreqsn) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/cancel");
		http.init();
		TreeMap<String,String> params = new TreeMap<String,String>();
		if(!SybUtil.isEmpty(SybConstants.SYB_ORGID)) {
			params.put("orgid", SybConstants.SYB_ORGID);
		}
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", reqsn);
		params.put("oldtrxid", oldtrxid);
		params.put("oldreqsn", oldreqsn);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("signtype", SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;

		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	/**
	 * 退款
	 * @param trxamt
	 * @param reqsn
	 * @param oldtrxid
	 * @param oldreqsn
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> refund(long trxamt,String reqsn,String oldtrxid,String oldreqsn) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/refund");
		http.init();
		TreeMap<String,String> params = new TreeMap<String,String>();
		if(!SybUtil.isEmpty(SybConstants.SYB_ORGID)) {
			params.put("orgid", SybConstants.SYB_ORGID);
		}
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", reqsn);
		params.put("oldreqsn", oldreqsn);
		params.put("oldtrxid", oldtrxid);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("signtype", SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;

		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	public Map<String,String> query(String reqsn,String trxid) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/query");
		http.init();
		TreeMap<String,String> params = new TreeMap<String,String>();
		if(!SybUtil.isEmpty(SybConstants.SYB_ORGID)) {
			params.put("orgid", SybConstants.SYB_ORGID);
		}
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("reqsn", reqsn);
		params.put("trxid", trxid);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("signtype", SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	public Map<String, String> posRefund(String trxamt, String trxid,String reqsn,
									  String remark,String oldtrxid) throws Exception {
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_API_POS_URL+"/refund");
		http.init();
		TreeMap<String,String> params = posBuildMap();
		params.put("trxamt", trxamt);//完成金额，以分为单位
		params.put("reqsn", reqsn);
		params.put("oldtrxid", oldtrxid);
		params.put("trxid", trxid);//填入预授权POS交易的通联交易ID-》trxid
		params.put("randomstr", System.currentTimeMillis()+"");
		params.put("remark", remark);
		params.put("signtype",SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}


	public Map<String, String> posRefund(String trxamt, String trxid,String reqsn,
										 String remark) throws Exception {
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_API_POS_URL+"/refund");
		http.init();
		TreeMap<String,String> params = posBuildMap();
		params.put("trxamt", trxamt);//完成金额，以分为单位
		params.put("reqsn", reqsn);
		params.put("trxid", trxid);//填入预授权POS交易的通联交易ID-》trxid
		params.put("randomstr", System.currentTimeMillis()+"");
		params.put("remark", remark);
		params.put("signtype",SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	private TreeMap<String, String> posBuildMap() {
		TreeMap<String,String> params = new TreeMap<String,String>();
		if(!SybUtil.isEmpty(SybConstants.SYB_ORGID)) {
			params.put("orgid", SybConstants.SYB_ORGID);
		}
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		return params;
	}

	public Map<String, String> posCancel(String trxamt, String trxid ,String reqsn) throws Exception {
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_API_POS_URL+"/cancel");
		http.init();
		TreeMap<String,String> params = posBuildMap();
		params.put("trxamt", trxamt);//完成金额，以分为单位
		params.put("reqsn", reqsn);
		params.put("trxid", trxid);//填入预授权POS交易的通联交易ID-》trxid
		params.put("randomstr", System.currentTimeMillis()+"");
		params.put("signtype",SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	public Map<String, String> posOlQuery(String reqsn, String trxid) throws Exception {
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_API_POS_URL+"/query");
		http.init();
		TreeMap<String,String> params = posBuildMap();
		params.put("trxid", trxid);//填入预授权POS交易的通联交易ID-》trxid
		params.put("reqsn", reqsn);//填入预授权POS交易的通联交易ID-》trxid
		params.put("randomstr", System.currentTimeMillis()+"");
		params.put("signtype",SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(reqsn,result);
		return map;
	}

	//https://vsp.allinpay.com/apiweb/tranx/queryorder
	public Map<String, String> posOrderPayQuery(String orderid, String trxid, String trxdate) throws Exception {
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_API_POS_TRANX_URL+"/queryorder");
		http.init();
		TreeMap<String,String> params = posBuildMap();
		params.put("trxdate", trxdate);//时间 mmdd
		params.put("trxid", trxid);//通联交易ID-》trxid
		params.put("orderid", orderid);//商户订单号
		params.put("resendnotify", "0");//通知重发
		params.put("randomstr", System.currentTimeMillis()+"");
		params.put("signtype",SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(orderid,result);
		return map;
	}


	public Map mallUnionOrderBuyApiWeb(Integer trxamt, String orderCode, String body, String returl, String notifyUrl,String payType) throws Exception {
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/pay");
		http.init();
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", orderCode);
		params.put("paytype", payType);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("body", body);
		params.put("notify_url", notifyUrl);
		params.put("signtype",SybConstants.SIGN_TYPE);
		String appkey = SybConstants.SYB_MD5_APPKEY;
		params.put("sign", SybUtil.unionSign(params,appkey,SybConstants.SIGN_TYPE));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(orderCode,result);
		logger.info("通联线上统一支付返回数据:"+map);
		return map;
	}

}
