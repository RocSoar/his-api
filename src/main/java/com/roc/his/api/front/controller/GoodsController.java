package com.roc.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.config.sa_token.StpCustomerUtil;
import com.roc.his.api.db.pojo.GoodsSnapshotEntity;
import com.roc.his.api.front.dto.SearchGoodsByIdDTO;
import com.roc.his.api.front.dto.SearchGoodsListByPageDTO;
import com.roc.his.api.front.dto.SearchGoodsSnapshotByIdDTO;
import com.roc.his.api.front.dto.SearchIndexGoodsByPartDTO;
import com.roc.his.api.front.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController("FrontGoodsController")
@RequestMapping("/front/goods")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsService goodsService;

    @PostMapping("/searchById")
    public R searchById(@RequestBody @Valid SearchGoodsByIdDTO dto) {
        HashMap map = goodsService.searchById(dto.id());
        return R.ok().put("result", map);
    }

    @PostMapping("/searchIndexGoodsByPart")
    public R searchIndexGoodsByPart(@RequestBody @Valid SearchIndexGoodsByPartDTO dto) {
        HashMap map = goodsService.searchIndexGoodsByPart(dto.getPartIds());
        return R.ok().put("result", map);
    }

    @PostMapping("/searchListByPage")
    public R searchListByPage(@RequestBody @Valid SearchGoodsListByPageDTO dto) {
        int page = dto.getPage();
        int length = dto.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        PageUtils pageUtils = goodsService.searchListByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/searchSnapshotForMis")
    @SaCheckLogin
    public R searchSnapshotForMis(@RequestBody @Valid SearchGoodsSnapshotByIdDTO dto) {
        GoodsSnapshotEntity entity = goodsService.searchSnapshotById(dto.getSnapshotId(), null);
        return R.ok().put("result", entity);
    }

    @PostMapping("/searchSnapshotForFront")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchSnapshotForFront(@RequestBody @Valid SearchGoodsSnapshotByIdDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        GoodsSnapshotEntity entity = goodsService.searchSnapshotById(dto.getSnapshotId(), customerId);
        return R.ok().put("result", entity);
    }
}

