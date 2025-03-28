package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.GoodsEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

public interface GoodsService {

    PageUtils searchByPage(Map param);

    String uploadImage(MultipartFile file);

    int insert(GoodsEntity entity);

    HashMap searchById(int id);

    int update(GoodsEntity entity);

    void updateCheckup(int id, MultipartFile file);

    boolean updateStatus(Map param);

    int deleteByIds(Integer[] ids);
}
