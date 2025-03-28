package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.GoodsEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_goods(体检套餐表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.GoodsEntity
 */
public interface GoodsMapper {
    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int insert(GoodsEntity entity);

    HashMap searchById(Map param);

    int update(GoodsEntity entity);

    GoodsEntity searchEntityById(int id);

    int updateCheckup(Map param);

    int updateStatus(Map param);

    List<String> searchImageByIds(Integer[] ids);

    int deleteByIds(Integer[] ids);

    List<HashMap> searchByPartIdLimit4(Integer id);

    List<HashMap> searchListByPage(Map param);

    long searchListCount(Map param);

    HashMap searchSnapshotNeededById(int id);

    int updateSalesVolume(int id);

}




