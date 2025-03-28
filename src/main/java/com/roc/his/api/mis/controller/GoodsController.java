package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.common.MinioUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.db.pojo.GoodsEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.mis.dto.*;
import com.roc.his.api.mis.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController("MisGoodsController")
@RequestMapping("/mis/goods")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsService goodsService;
    private final MinioUtil minioUtil;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchGoodsByPageDTO dto) {
        int page = dto.getPage();
        int length = dto.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        PageUtils pageUtils = goodsService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/uploadImage")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R uploadImage(@Param("file") MultipartFile file) {
        String path = goodsService.uploadImage(file);
        return R.ok().put("result", path);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertGoodsDTO dto) {
        System.out.println(dto);
        /* 因为POJO对象中的check_1、check_2、check_3和check_4是String类型，
         * 而FORM类中的这些东西是ArrayList类型的，为了避免FORM对象转换成POJO对象
         * 出现异常，所以要排除这四个变量。
         */
        GoodsEntity entity = BeanUtil.toBean(dto, GoodsEntity.class, CopyOptions.create().setIgnoreProperties("checkup_1", "checkup_2", "checkup_3", "checkup_4", "tag"));
        String temp = null;

        if (dto.getCheckup_1() != null) {
            //手动将ArrayList转换成JSON数组字符串，给POJO对象的checkup_1变量赋值
            temp = JSONUtil.parseArray(dto.getCheckup_1()).toString();
            entity.setCheckup_1(temp);
        }

        if (dto.getCheckup_2() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_2()).toString();
            entity.setCheckup_2(temp);
        }

        if (dto.getCheckup_3() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_3()).toString();
            entity.setCheckup_3(temp);
        }
        if (dto.getCheckup_4() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_4()).toString();
            entity.setCheckup_4(temp);
        }
        if (dto.getTag() != null) {
            temp = JSONUtil.parseArray(dto.getTag()).toString();
            entity.setTag(temp);
        }

        int rows = goodsService.insert(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchGoodsByIdDTO dto) {
        HashMap map = goodsService.searchById(dto.id());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateGoodsDTO dto) {
        GoodsEntity entity = BeanUtil.toBean(dto, GoodsEntity.class, CopyOptions.create().setIgnoreProperties("checkup_1", "checkup_2", "checkup_3", "checkup_4", "tag"));
        String temp = null;
        if (dto.getCheckup_1() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_1()).toString();
            entity.setCheckup_1(temp);
        }

        if (dto.getCheckup_2() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_2()).toString();
            entity.setCheckup_2(temp);
        }

        if (dto.getCheckup_3() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_3()).toString();
            entity.setCheckup_3(temp);
        }
        if (dto.getCheckup_4() != null) {
            temp = JSONUtil.parseArray(dto.getCheckup_4()).toString();
            entity.setCheckup_4(temp);
        }
        if (dto.getTag() != null) {
            temp = JSONUtil.parseArray(dto.getTag()).toString();
            entity.setTag(temp);
        }
        int rows = goodsService.update(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/uploadCheckupExcel")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R uploadCheckupExcel(@Valid @Param("id") UploadCheckupExcelDTO dto, @Param("file") MultipartFile file) {
        goodsService.updateCheckup(dto.getId(), file);
        return R.ok();
    }

    @GetMapping("/downloadCheckupExcel")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public void downloadCheckupExcel(@Valid DownloadCheckupExcelDTO dto, HttpServletResponse response) {
        //设置下载文件的名称
        response.setHeader("Content-Disposition", "attachment;filename=" + dto.getId() + ".xlsx");
        //该MIME类型会让浏览器弹出下载对话框
        response.setContentType("application/x-download");
        response.setCharacterEncoding("UTF-8");
        String path = "/mis/goods/checkup/" + dto.getId() + ".xlsx";
        try (
                //读取Minio存储文件的输入流
                InputStream in = minioUtil.downloadFile(path);
                BufferedInputStream bin = new BufferedInputStream(in);
                //向响应输出数据的输出流
                ServletOutputStream out = response.getOutputStream();
                BufferedOutputStream bout = new BufferedOutputStream(out);) {
            //把输入流中的数据拷贝到输出流
            IoUtil.copy(bin, bout);
        } catch (Exception e) {
            throw new HisException("文档下载失败");
        }
    }

    @PostMapping("/updateStatus")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R updateStatus(@RequestBody @Valid UpdateGoodsStatusDTO dto) {
        Map param = BeanUtil.beanToMap(dto);
        boolean bool = goodsService.updateStatus(param);
        return R.ok().put("result", bool);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "GOODS:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteGoodsByIdsDTO dto) {
        int rows = goodsService.deleteByIds(dto.getIds());
        return R.ok().put("rows", rows);
    }
}
