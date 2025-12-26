package cn.iocoder.yudao.module.family.dal.mysql.member;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.member.MemberDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 成员 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberMapper extends BaseMapperX<MemberDO> {

    default List<MemberDO> selectListByFamilyId(Long familyId) {
        return selectList(MemberDO::getFamilyId, familyId);
    }

    default List<MemberDO> selectListByFatherId(Long fatherId) {
        return selectList(MemberDO::getFatherId, fatherId);
    }

    default Long selectCountByFatherId(Long fatherId) {
        return selectCount(MemberDO::getFatherId, fatherId);
    }

    default List<MemberDO> selectListByFamilyUnitId(Long familyUnitId) {
        return selectList(MemberDO::getParentFamilyUnitId, familyUnitId);
    }

}

