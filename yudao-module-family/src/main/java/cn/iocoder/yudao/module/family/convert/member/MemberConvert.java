package cn.iocoder.yudao.module.family.convert.member;

import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.dal.dataobject.member.MemberDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 成员 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberConvert {

    MemberConvert INSTANCE = Mappers.getMapper(MemberConvert.class);

    /**
     * 转换为 MemberDetailRespVO，根据是否成员决定是否脱敏
     *
     * @param member 成员DO
     * @param isMember 是否是家族成员
     * @return MemberDetailRespVO
     */
    default MemberDetailRespVO convert(MemberDO member, Boolean isMember) {
        if (member == null) {
            return null;
        }
        MemberDetailRespVO respVO = convert(member);
        
        // 如果不是成员，则脱敏敏感字段
        if (!isMember) {
            // 脱敏：不返回出生日期、去世日期、头像、行传等敏感信息
            respVO.setBirthDate(null);
            respVO.setDeathDate(null);
            respVO.setAvatarUrl(null);
            respVO.setBiography(null);
        }
        
        return respVO;
    }

    /**
     * 基础转换方法
     */
    @org.mapstruct.Mapping(target = "spouseIds", ignore = true)
    @org.mapstruct.Mapping(target = "childrenIds", ignore = true)
    MemberDetailRespVO convert(MemberDO bean);

}
