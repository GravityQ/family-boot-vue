package cn.iocoder.yudao.module.family.dal.mysql.familymember;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.familymember.FamilyMemberDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 家族成员关系 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FamilyMemberMapper extends BaseMapperX<FamilyMemberDO> {

    default FamilyMemberDO selectByFamilyIdAndUserId(Long familyId, Long userId) {
        return selectOne("family_id", familyId, "user_id", userId);
    }

    default boolean existsByFamilyIdAndUserId(Long familyId, Long userId) {
        return selectByFamilyIdAndUserId(familyId, userId) != null;
    }

}
