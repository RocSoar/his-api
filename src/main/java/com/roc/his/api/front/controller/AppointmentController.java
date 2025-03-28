package com.roc.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.config.sa_token.StpCustomerUtil;
import com.roc.his.api.db.pojo.AppointmentEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.front.dto.InsertAppointmentDTO;
import com.roc.his.api.front.dto.SearchAppointmentByPageDTO;
import com.roc.his.api.front.service.AppointmentService;
import com.roc.his.api.front.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController("FrontAppointmentController")
@RequestMapping("/front/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final OrderService orderService;
    private final AppointmentService appointmentService;

    @PostMapping("/insert")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R insert(@RequestBody @Valid InsertAppointmentDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();

        Map<String, Integer> param = Map.of("customerId", customerId, "id", dto.getOrderId());

        boolean bool = orderService.hasOwnOrder(param);
        if (!bool) {
            throw new HisException("预约失败，该订单与您无关");
        }

        String pid = dto.getPid();
        //验证身份证是否有效
        if (!IdcardUtil.isValidCard18(pid)) {
            throw new HisException("身份证号码无效");
        }
//        从身份证中解析出生日和性别
        String birthday = IdcardUtil.getBirthDate(pid).toDateStr();
        String sex = IdcardUtil.getGenderByIdCard(pid) == 1 ? "男" : "女";

        //验证日期是否为未来60天以内
        DateTime date = DateUtil.parse(dto.getDate());
        DateTime tomorrow = DateUtil.tomorrow(); //当前时刻的24小时之后
        DateTime startDate = DateUtil.parse(tomorrow.toDateStr()); // 转为明天凌晨零点
        DateTime endDate = tomorrow.offset(DateField.DAY_OF_MONTH, 60); //向后偏移60天
        boolean temp = date.isIn(startDate, endDate);
        if (!temp) {
            throw new HisException("预约日期错误");
        }

        AppointmentEntity entity = BeanUtil.toBean(dto, AppointmentEntity.class);
        entity.setUuid(IdUtil.simpleUUID().toUpperCase());
        entity.setBirthday(birthday);
        entity.setSex(sex);

        String result = appointmentService.insert(entity);
        return R.ok().put("result", result);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchByPage(@RequestBody @Valid SearchAppointmentByPageDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        int page = dto.getPage();
        int length = dto.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        param.put("customerId", customerId);
        PageUtils pageUtils = appointmentService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }
}

