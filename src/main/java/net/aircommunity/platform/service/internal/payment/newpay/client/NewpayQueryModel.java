package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.service.internal.payment.newpay.client.NewpayMessage.TimeAdapter;

/**
 * Newpay query model for payment and refund
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayQueryModel implements Serializable {
	private static final long serialVersionUID = 1L;

	// 1:单笔 2:批量
	@XmlElement(name = "mode", required = true)
	private NewpayQueryMode mode = NewpayQueryMode.SINGLE;

	// 1:支付订单 2:退款订单
	@XmlElement(name = "type", required = true)
	private NewpayQueryType type;

	// 商户订单号 单笔查询时,需查询的支付订单的商户订单号(当 查询退款订单时,需传入原始支付订单的商户订 单号)
	@XmlElement(name = "orderID", required = false)
	private String orderNo = "";

	// 查询开始时间 String(14), yyyyMMddHHmmss
	@XmlElement(name = "beginTime", required = false)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date beginTime;

	// 查询结束时间 String(14), yyyyMMddHHmmss
	@XmlElement(name = "endTime", required = false)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date endTime;

	// 扩展字段 String(256) 填写英文或中文字符串,照原样返回给商户
	@XmlElement(name = "remark", required = true)
	private String remark = "query";

	public NewpayQueryMode getMode() {
		return mode;
	}

	public void setMode(NewpayQueryMode mode) {
		this.mode = mode;
	}

	public NewpayQueryType getType() {
		return type;
	}

	public void setType(NewpayQueryType type) {
		this.type = type;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayQueryModel [mode=").append(mode).append(", type=").append(type).append(", orderNo=")
				.append(orderNo).append(", beginTime=").append(beginTime).append(", endTime=").append(endTime)
				.append(", remark=").append(remark).append("]");
		return builder.toString();
	}
}
