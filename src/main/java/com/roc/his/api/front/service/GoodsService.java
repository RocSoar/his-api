package com.roc.his.api.front.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.GoodsSnapshotEntity;

import java.util.HashMap;
import java.util.Map;

public interface GoodsService {
    HashMap searchById(int id);

    HashMap searchIndexGoodsByPart(Integer[] partIds);

    PageUtils searchListByPage(Map param);

    GoodsSnapshotEntity searchSnapshotById(String snapshotId, Integer customerId);

}
