package cn.iocoder.yudao.module.family.dal.mysql.family;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 家族 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FamilyMapper extends BaseMapperX<FamilyDO> {

    default FamilyDO selectByName(String name) {
        return selectOne(FamilyDO::getName, name);
    }

}

