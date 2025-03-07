package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.db.pojo.UserEntity;
import com.roc.his.api.mis.dto.*;
import com.roc.his.api.mis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public R login(@RequestBody @Valid LoginDTO loginDTO) {
        //把Form对象转换成Map对象。因为Form对象中含有后端验证表达式，该对象仅用于Web层，不适合传给业务层
        Map param = BeanUtil.beanToMap(loginDTO);

        //获取登陆用户的主键值
        Integer userId = userService.login(param);

        if (userId == null) return R.ok().put("result", false);

        /*
         * 实现同端互斥效果，把之前登录的Web端的令牌销毁。
         * 在其他浏览器上已经登陆的该账户，令牌就失效了，实现同端互斥。
         */
        StpUtil.logout(userId, "Web");
        //通过会话对象，向SaToken传递userId
        StpUtil.login(userId, "Web");
        //生成新的令牌字符串，标记该令牌是给Web端用户使用的
        String token = StpUtil.getTokenValueByLoginId(userId, "Web");
        //获取用户的权限列表
        List<String> permissions = StpUtil.getPermissionList();
        //向前端返回数据
        return R.ok().put("result", true).put("token", token).put("permissions", permissions);
    }

    @GetMapping("/logout")
    @SaCheckLogin  //必须在登录状态下才可退出登录
    public R logout() {

//        从token中decode出userId
        int userId = StpUtil.getLoginIdAsInt();
//        销毁Web端的令牌, 其他端不受影响
        StpUtil.logout(userId, "Web");
        return R.ok();
    }

    @PostMapping("/updatePassword")
    @SaCheckLogin
    public R updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        int userId = StpUtil.getLoginIdAsInt();
        Map map = new HashMap<>();
        map.put("userId", userId);
        map.put("password", dto.getPassword());
        map.put("newPassword", dto.getNewPassword());

        int rows = userService.updatePassword(map);

        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchUserDTO dto) {
        int page = dto.getPage();
        int pageSize = dto.getLength();
        int start = (page - 1) * pageSize;

        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);

        PageUtils pageUtils = userService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "USER:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertUserDTO dto) {
        UserEntity user = BeanUtil.toBean(dto, UserEntity.class);
        user.setStatus(1);
        user.setRole(JSONUtil.parseArray(dto.getRole()).toString());
        int rows = userService.insert(user);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchUserByIdDTO dto) {
        HashMap map = userService.searchById(dto.getUserId());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateUserDTO dto) {
        Map param = BeanUtil.beanToMap(dto);
        param.replace("role", JSONUtil.parseArray(dto.getRole()).toString());
        int rows = userService.update(param);
        if (rows == 1) {
            //该用户的Web端、APP端、小程序端等，全部退出登陆
            StpUtil.logout(dto.getUserId());
        }
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "USER:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@Valid @RequestBody DeleteUserByIdsDTO dto) {
        Integer userId = StpUtil.getLoginIdAsInt();
        if (ArrayUtil.contains(dto.getIds(), userId)) {
            return R.error("您不能删除自己的帐户");
        }
        int rows = userService.deleteByIds(dto.getIds());
        if (rows > 0) {
            for (Integer id : dto.getIds()) {
                StpUtil.logout(id);
            }
        }
        return R.ok().put("rows", rows);
    }

    @PostMapping("/dismiss")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    public R dismiss(@RequestBody @Valid DismissDTO dto) {
        int rows = userService.dismiss(dto.getUserId());
        if (rows > 0) {
            StpUtil.logout(dto.getUserId());
        }
        return R.ok().put("rows", rows);
    }
}
