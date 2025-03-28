package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.OrderEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_order(订单表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.OrderEntity
 */
public interface OrderMapper {

    HashMap searchFrontStatistic(int customerId);

    boolean searchIllegalCountInDay(int customerId);

    int closeOrder();

    int insert(OrderEntity entity);

    int updatePayment(Map param);

    Integer searchCustomerId(String outTradeNo);

    List<HashMap> searchFrontOrderByPage(Map param);

    long searchFrontOrderCount(Map param);

    String searchAlreadyRefund(int id);

    HashMap searchRefundNeeded(Map param);

    int updateOutRefundNo(Map param);

    int updateRefundStatusByOutRefundNo(String outRefundNo);

    List<HashMap> searchTimeoutRefund();

    int updateRefundStatusById(int id);

    int closeOrderById(Map param);

    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int deleteById(int id);

    Integer hasOwnSnapshot(Map param);

    int updateStatus(Map param);

    Integer hasOwnOrder(Map param);

}




