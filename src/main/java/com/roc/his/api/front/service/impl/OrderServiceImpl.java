package com.roc.his.api.front.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.GoodsMapper;
import com.roc.his.api.db.dao.GoodsSnapshotDao;
import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.db.pojo.GoodsSnapshotEntity;
import com.roc.his.api.db.pojo.OrderEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.front.service.OrderService;
import com.roc.his.api.front.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("FrontOrderService")
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final GoodsMapper goodsMapper;
    private final PaymentService paymentService;
    private final RedisTemplate redisTemplate;
    private final GoodsSnapshotDao goodsSnapshotDao;

    @Value("${wechat-payment-notifyUrl}")
    private String paymentNotifyUrl;

    @Value("${wechat-refund-notifyUrl}")
    private String refundNotifyUrl;

    @Override
    @Transactional
    public HashMap createPayment(Map param) {
        int goodsId = MapUtil.getInt(param, "goodsId");
        Integer number = MapUtil.getInt(param, "number");
        int customerId = MapUtil.getInt(param, "customerId");

        //如果当天该客户有10个以上未付款订单或者5个以上退款订单，当天就无法下单
        boolean illegal = orderMapper.searchIllegalCountInDay(customerId);
        if (illegal) {
            return null;
        }

        // 查找商品详情信息
        HashMap map = goodsMapper.searchSnapshotNeededById(goodsId);
        String goodsCode = MapUtil.getStr(map, "code");
        String goodsTitle = MapUtil.getStr(map, "title");
        String goodsDescription = MapUtil.getStr(map, "description");
        String goodsImage = MapUtil.getStr(map, "image");
        BigDecimal goodsInitialPrice = new BigDecimal(MapUtil.getStr(map, "initialPrice"));
        BigDecimal goodsCurrentPrice = new BigDecimal(MapUtil.getStr(map, "currentPrice"));
        String goodsRuleName = MapUtil.getStr(map, "ruleName");
        String goodsRule = MapUtil.getStr(map, "rule");
        String goodsType = MapUtil.getStr(map, "type");
        String goodsMd5 = MapUtil.getStr(map, "md5");

        String temp = MapUtil.getStr(map, "checkup_1");
        List<Map> goodsCheckup_1 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_2");
        List<Map> goodsCheckup_2 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_3");
        List<Map> goodsCheckup_3 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_4");
        List<Map> goodsCheckup_4 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup");
        List<Map> goodsCheckup = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "tag");
        List<String> goodsTag = temp != null ? JSONUtil.parseArray(temp).toList(String.class) : null;

        // 计算订单金额
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("number", number.intValue());
        context.put("price", goodsCurrentPrice.toString());

        String amount = null;
        if (goodsRule != null) {
            try {
                //执行规则引擎计算支付结果
                amount = runner.execute(goodsRule, context, null, true, false).toString();
            } catch (Exception e) {
                throw new HisException("规则引擎计算价格失败", e);
            }
        } else {
            amount = goodsCurrentPrice.multiply(new BigDecimal(number)).toString();
        }

        // 创建微信支付单
        //把付款金额从元转换成分
        int total = NumberUtil.mul(amount, "100").intValue();
        //生成商品订单流水号
        String outTradeNo = IdUtil.simpleUUID().toUpperCase();

        //付款过期时间为20分钟
        DateTime dateTime = new DateTime();
        dateTime.offset(DateField.MINUTE, 20); //把当前时间向后偏移20分钟即为过期时间
        String timeExpire = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        //创建支付单
        ObjectNode objectNode = paymentService.unifiedOrder(outTradeNo, total, "购买体检套餐", paymentNotifyUrl, timeExpire);

        String codeUrl = objectNode.get("code_url").textValue();

        // 创建支付单缓存，设置缓存过期时间
        /*
         * 把支付单的codeUrl缓存到Redis中，用于将来检测未付款订单是否可以付款
         * 比如客户进入订单列表页面，想要对未付款的订单付款，我们可以根据是否存
         * 在缓存，判定用户能否付款。这样可以省去调用微信支付接口查询付款单状态。
         */
        String key = "codeUrl_" + customerId + "_" + outTradeNo;
        redisTemplate.opsForValue().set(key, codeUrl);
        redisTemplate.expireAt(key, dateTime); //设置缓存过期时间


        //根据商品md5查询是否存在快照
        String _id = goodsSnapshotDao.hasGoodsSnapshot(goodsMd5);
        //如果不存在商品快照就创建快照
        if (_id == null) {
            GoodsSnapshotEntity entity = new GoodsSnapshotEntity();
            entity.setId(goodsId);
            entity.setCode(goodsCode);
            entity.setTitle(goodsTitle);
            entity.setDescription(goodsDescription);
            entity.setCheckup_1(goodsCheckup_1);
            entity.setCheckup_2(goodsCheckup_2);
            entity.setCheckup_3(goodsCheckup_3);
            entity.setCheckup_4(goodsCheckup_4);
            entity.setCheckup(goodsCheckup);
            entity.setImage(goodsImage);
            entity.setInitialPrice(goodsInitialPrice);
            entity.setCurrentPrice(goodsCurrentPrice);
            entity.setType(goodsType);
            entity.setTag(goodsTag);
            entity.setRuleName(goodsRuleName);
            entity.setRule(goodsRule);
            entity.setMd5(goodsMd5);

            //保存商品快照到mongodb中，拿到快照的主键值
            _id = goodsSnapshotDao.insert(entity);
        }

        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(customerId);
        entity.setGoodsId(goodsId);
        entity.setSnapshotId(_id); //关联商品快照

        entity.setGoodsTitle(goodsTitle);
        entity.setGoodsPrice(goodsCurrentPrice);
        entity.setNumber(number);
        entity.setAmount(new BigDecimal(amount));
        entity.setGoodsImage(goodsImage);
        entity.setGoodsDescription(goodsDescription);
        entity.setOutTradeNo(outTradeNo);
        //保存订单记录
        orderMapper.insert(entity);

        // 付款二维码图片转换成base64字符串返回给前端
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(230);
        qrConfig.setHeight(230);
        qrConfig.setMargin(2);
        String qrCodeBase64 = QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");

        //更新商品销量
        int rows = goodsMapper.updateSalesVolume(goodsId);
        if (rows != 1) {
            throw new HisException("更新商品销量失败");
        }

        return new HashMap() {{
            put("qrCodeBase64", qrCodeBase64);
            put("outTradeNo", outTradeNo);
        }};
    }

    @Override
    @Transactional
    public boolean updatePayment(Map param) {
        int rows = orderMapper.updatePayment(param);
        return rows == 1;
    }

    @Override
    public Integer searchCustomerId(String outTradeNo) {
        Integer customerId = orderMapper.searchCustomerId(outTradeNo);
        return customerId;
    }

    @Override
    @Transactional
    public boolean searchPaymentResult(String outTradeNo) {
        String transactionId = paymentService.searchPaymentResult(outTradeNo);
        if (transactionId == null) return false;

        this.updatePayment(Map.of("outTradeNo", outTradeNo, "transactionId", transactionId));
        return true;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long totalCount = orderMapper.searchFrontOrderCount(param);
        if (totalCount > 0) {
            list = orderMapper.searchFrontOrderByPage(param);
        }
        int currentPage = (Integer) param.get("page");
        int pageSize = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, totalCount, currentPage, pageSize);
        return pageUtils;
    }

    @Override
    @Transactional
    public boolean refund(Map param) {
        //先查询订单是否存在退款流水号，避免用户重复申请退款
        int id = MapUtil.getInt(param, "id");
        String outRefundNo = orderMapper.searchAlreadyRefund(id);
        //判断该订单是否申请退款了
        if (outRefundNo != null) {
            return false;
        }

        HashMap map = orderMapper.searchRefundNeeded(param);
        String transactionId = MapUtil.getStr(map, "transactionId");
        String amount = MapUtil.getStr(map, "amount");

        if (transactionId == null) {
            log.error("transactionId不能为空");
            return false;
        }

        //int total = NumberUtil.mul(amount, "100").intValue();  //订单总金额(分)
        //int refund = total;   //退款金额
        int total = 1; //总金额为1分钱(测试用)
        int refund = 1; //退款1分钱(测试用)

        //执行退款
        outRefundNo = paymentService.refund(transactionId, refund, total, refundNotifyUrl);

        if (outRefundNo == null) return false;

        param.put("outRefundNo", outRefundNo);
        //更新订单的退款流水号和退款日期时间
        int rows = orderMapper.updateOutRefundNo(param);
        return rows == 1;
    }

    @Override
    @Transactional
    public boolean updateRefundStatus(String outRefundNo) {
        int rows = orderMapper.updateRefundStatusByOutRefundNo(outRefundNo);
        return rows == 1;
    }

    @Override
    public String payOrder(int customerId, String outTradeNo) {
        String key = "codeUrl_" + customerId + "_" + outTradeNo;
        if (redisTemplate.hasKey(key)) {
            //从Redis中取出缓存的付款URL
            String codeUrl = redisTemplate.opsForValue().get(key).toString();
            QrConfig qrConfig = new QrConfig();
            qrConfig.setWidth(230);
            qrConfig.setHeight(230);
            qrConfig.setMargin(2);
            String qrCodeBase64 = QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");
            return qrCodeBase64;
        }
        return null;
    }

    @Override
    public boolean closeOrderById(Map param) {
        int rows = orderMapper.closeOrderById(param);
        return rows == 1;
    }

    @Override
    public boolean hasOwnOrder(Map param) {
        Integer id = orderMapper.hasOwnOrder(param);
        return id != null;
    }
}

