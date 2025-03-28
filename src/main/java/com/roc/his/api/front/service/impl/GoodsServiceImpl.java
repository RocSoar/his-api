package com.roc.his.api.front.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.GoodsMapper;
import com.roc.his.api.db.dao.GoodsSnapshotDao;
import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.db.pojo.GoodsSnapshotEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.front.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("FrontGoodsService")
@Slf4j
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {
    private final GoodsMapper goodsMapper;
    private final GoodsSnapshotDao goodsSnapshotDao;
    private final OrderMapper orderMapper;

    @Override
    @Cacheable(cacheNames = "goods", key = "#id")
    public HashMap searchById(int id) {
        Map param = Map.of("id", id, "status", true);

        HashMap map = goodsMapper.searchById(param);

        if (map == null) return null;

        for (String key : List.of("tag", "checkup_1", "checkup_2", "checkup_3", "checkup_4")) {
            String temp = MapUtil.getStr(map, key);
            JSONArray array = JSONUtil.parseArray(temp);
            map.replace(key, array);
        }
        return map;
    }

    @Override
    public HashMap searchIndexGoodsByPart(Integer[] partIds) {
        HashMap map = new HashMap<>();

        for (Integer partId : partIds) {
            List<HashMap> list = goodsMapper.searchByPartIdLimit4(partId);
            map.put(partId, list);
        }

        return map;
    }

    @Override
    public PageUtils searchListByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long totalCount = goodsMapper.searchListCount(param);
        if (totalCount > 0) {
            list = goodsMapper.searchListByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, totalCount, page, length);
        return pageUtils;
    }

    @Override
    public GoodsSnapshotEntity searchSnapshotById(String snapshotId, Integer customerId) {
        // 如果customerId不为空，检查该客户是否拥有该订单快照
        if (customerId != null) {
            //判断用户是否购买过该商品
            Map param = Map.of("customerId", customerId, "snapshotId", snapshotId);
            if (orderMapper.hasOwnSnapshot(param) == null) {
                throw new HisException("您没有购买过该商品");
            }
        }

        GoodsSnapshotEntity entity = goodsSnapshotDao.searchById(snapshotId);
        return entity;

    }
}

