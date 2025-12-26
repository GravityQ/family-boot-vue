package cn.iocoder.yudao.module.family.dal.mysql.familyunit;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.familyunit.FamilyUnitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 家庭单元 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FamilyUnitMapper extends BaseMapperX<FamilyUnitDO> {

    default List<FamilyUnitDO> selectListByHusbandId(Long husbandId) {
        return selectList(FamilyUnitDO::getHusbandId, husbandId);
    }

    default List<FamilyUnitDO> selectListByWifeId(Long wifeId) {
        return selectList(FamilyUnitDO::getWifeId, wifeId);
    }

}

