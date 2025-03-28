package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.mis.dto.CheckPaymentResultDTO;
import com.roc.his.api.mis.dto.DeleteOrderByIdDTO;
import com.roc.his.api.mis.dto.UpdateRefundStatusByIdDTO;
import com.roc.his.api.mis.service.OrderService;
import com.roc.his.api.mis.dto.SearchOrderByPageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController("MisOrderController")
@RequestMapping("/mis/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "ORDER:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchOrderByPageDTO dto) {
        if ((dto.getStartDate() != null && dto.getEndDate() == null) || (dto.getStartDate() == null && dto.getEndDate() != null)) {
            throw new HisException("startDate和endDate不允许一个为空，另一个不为空");
        }
        //验证日期先后逻辑
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            DateTime startDate = DateUtil.parse(dto.getStartDate());
            DateTime endDate = DateUtil.parse(dto.getEndDate());
            if (endDate.isBefore(startDate)) {
                throw new HisException("endDate不能早于startDate");
            }
        }
        int currentPage = dto.getPage();
        int pageSize = dto.getLength();
        int start = (currentPage - 1) * pageSize;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        PageUtils pageUtils = orderService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/checkPaymentResult")
    @SaCheckPermission(value = {"ROOT", "ORDER:UPDATE"}, mode = SaMode.OR)
    public R checkPaymentResult(@RequestBody @Valid CheckPaymentResultDTO dto) {
        int rows = orderService.checkPaymentResult(dto.getOutTradeNoArray());
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteById")
    @SaCheckPermission(value = {"ROOT", "ORDER:DELETE"}, mode = SaMode.OR)
    public R deleteById(@RequestBody @Valid DeleteOrderByIdDTO dto) {
        int rows = orderService.deleteById(dto.getId());
        return R.ok().put("rows", rows);
    }

    @PostMapping("/updateRefundStatusById")
    @SaCheckPermission(value = {"ROOT", "ORDER:UPDATE"}, mode = SaMode.OR)
    public R updateRefundStatusById(@RequestBody @Valid UpdateRefundStatusByIdDTO dto) {
        int rows = orderService.updateRefundStatusById(dto.getId());
        return R.ok().put("rows", rows);
    }
}

