package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.common.MinioUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.GoodsMapper;
import com.roc.his.api.db.pojo.GoodsEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.mis.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;

@Service("MisGoodsService")
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {
    private final GoodsMapper goodsMapper;
    private final MinioUtil minioUtil;

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long count = goodsMapper.searchCount(param);
        if (count > 0) {
            list = goodsMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String filename = IdUtil.simpleUUID() + ".jpg";
        String path = "front/goods/" + filename;

        return minioUtil.uploadImage(path, file);
    }

    @Override
    @Transactional
    public int insert(GoodsEntity entity) {
        String md5 = genEntityMd5(entity);
        entity.setMd5(md5);
        return goodsMapper.insert(entity);
    }

    @Override
    public HashMap searchById(int id) {
        Map param = new HashMap() {{
            put("id", id);
        }};
        HashMap map = goodsMapper.searchById(param);

        String[] column = {"tag", "checkup_1", "checkup_2", "checkup_3", "checkup_4"};
        for (String one : column) {
            String temp = MapUtil.getStr(map, one);
            if (temp != null) {
                JSONArray array = JSONUtil.parseArray(temp);
                map.replace(one, array);
            }
        }
        return map;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#entity.id")
    public int update(GoodsEntity entity) {
        //修改商品信息需要重新计算MD5值
        String md5 = this.genEntityMd5(entity);
        entity.setMd5(md5);
        int rows = goodsMapper.update(entity);
        return rows;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#id")
    public void updateCheckup(int id, MultipartFile file) {
        //创建空的ArrayList用于保存从Excel文档中解析出来的数据
        List<Map> list = new ArrayList<>();

        //读取文件内容
        try (InputStream in = file.getInputStream();
             BufferedInputStream bin = new BufferedInputStream(in);
        ) {
            XSSFWorkbook workbook = new XSSFWorkbook(bin);
            //获取Excel文档中第一个Sheet页
            XSSFSheet sheet = workbook.getSheetAt(0);
            //从第二行（第一行是表头）循环到最后一行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);

                //获取检查地点
                XSSFCell cell_1 = row.getCell(0);
                String value_1 = cell_1.getStringCellValue();

                //获取导诊名称
                XSSFCell cell_2 = row.getCell(1);
                String value_2 = cell_2.getStringCellValue();

                //获取检查项
                XSSFCell cell_3 = row.getCell(2);
                String value_3 = cell_3.getStringCellValue();

                //获取采集方式
                XSSFCell cell_4 = row.getCell(3);
                String value_4 = cell_4.getStringCellValue();

                //获取模板编码
                XSSFCell cell_5 = row.getCell(4);
                String value_5 = cell_5.getStringCellValue();

                //获取性别要求
                XSSFCell cell_6 = row.getCell(5);
                String value_6 = cell_6.getStringCellValue();

                //获取模板值
                XSSFCell cell_7 = row.getCell(6);
                String value_7 = cell_7.getStringCellValue();

                //获取输出模板
                XSSFCell cell_8 = row.getCell(7);
                String value_8 = cell_8.getStringCellValue();

                //希望Map对象按照添加顺序保存键值对
                LinkedHashMap map = new LinkedHashMap<>() {{
                    put("place", value_1);
                    put("name", value_2);
                    put("item", value_3);
                    put("type", value_4);
                    put("code", value_5);
                    put("sex", value_6);
                    put("value", value_7);
                    put("template", value_8);
                }};

                //把当前行解析出来的数据保存到ArrayList对象中
                list.add(map);
            }
            //打印解析的结果，测试完即可注释掉这句话
//            System.out.println("id: " + id);
//            System.out.println(list);
        } catch (Exception e) {
            throw new HisException("处理Excel文件失败", e);
        }

        if (list.size() == 0) {
            throw new HisException("文档内容无效");
        }

        //把文件存储到Minio服务器
        String path = "/mis/goods/checkup/" + id + ".xlsx";
        minioUtil.uploadExcel(path, file);

        // 根据商品ID查询商品记录，然后重新计算MD5值，更新商品记录的checkup和md5字段
        GoodsEntity entity = goodsMapper.searchEntityById(id);
        String temp = JSONUtil.parseArray(list).toString();
        entity.setCheckup(temp);
        String md5 = genEntityMd5(entity);
        Map map = Map.of("id", id, "checkup", temp, "md5", md5);
        int rows = goodsMapper.updateCheckup(map);
        if (rows != 1) {
            throw new HisException("更新体检内容失败");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#param.get('id')", condition = "#param.get('status')==false")
    public boolean updateStatus(Map param) {
        int rows = goodsMapper.updateStatus(param);
        return rows == 1;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goods", key = "#ids")
    public int deleteByIds(Integer[] ids) {
        //删除记录之前先查询商品的封面
        List<String> list = goodsMapper.searchImageByIds(ids);
        //删除记录
        int rows = goodsMapper.deleteByIds(ids);
        if (rows > 0) {
            //删除商品对应的封面图片
            list.forEach(minioUtil::deleteFile);
        }
        return rows;
    }

    private String genEntityMd5(GoodsEntity entity) {
        JSONObject json = JSONUtil.parseObj(entity);
        //以下内容不属于计算商品信息MD5值范畴之内
        json.remove("id");
        json.remove("partId");
        json.remove("salesVolume");
        json.remove("status");
        json.remove("md5");
        json.remove("updateTime");
        json.remove("createTime");
        String md5 = MD5.create().digestHex(json.toString()).toUpperCase();
        return md5;
    }
}
