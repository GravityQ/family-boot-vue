package cn.iocoder.yudao.module.family.convert.family;

import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyRespVO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 家族 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface FamilyConvert {

    FamilyConvert INSTANCE = Mappers.getMapper(FamilyConvert.class);

    /**
     * 转换为 FamilyRespVO，根据是否成员决定是否脱敏
     *
     * @param family 家族DO
     * @param isMember 是否是家族成员
     * @return FamilyRespVO
     */
    default FamilyRespVO convert(FamilyDO family, Boolean isMember) {
        if (family == null) {
            return null;
        }
        FamilyRespVO respVO = convert(family);
        respVO.setIsMember(isMember);
        
        // 如果不是成员且隐私等级为仅族人可见，则脱敏敏感字段
        if (!isMember && family.getPrivacyLevel() != null && family.getPrivacyLevel() == 1) {
            // 脱敏：不返回家族简介、位置等敏感信息
            respVO.setDesc(null);
            respVO.setLocation(null);
        }
        
        return respVO;
    }

    /**
     * 基础转换方法
     */
    @org.mapstruct.Mapping(target = "isMember", ignore = true)
    FamilyRespVO convert(FamilyDO bean);

}
