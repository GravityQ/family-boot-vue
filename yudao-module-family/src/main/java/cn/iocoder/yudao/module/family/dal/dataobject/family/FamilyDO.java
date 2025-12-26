package cn.iocoder.yudao.module.family.dal.dataobject.family;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.family.enums.FamilyPrivacyLevelEnum;
import cn.iocoder.yudao.module.family.enums.FamilyStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 家族 DO
 *
 * @author 芋道源码
 */
@TableName("family")
@KeySequence("family_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 家族名称
     */
    private String name;
    /**
     * 主要姓氏
     */
    private String mainSurname;
    /**
     * 祖籍位置
     */
    private String location;
    /**
     * 家族简介/家训
     */
    private String desc;
    /**
     * 封面图URL
     */
    private String coverUrl;
    /**
     * 创建者UserID
     */
    private Long creatorId;
    /**
     * 状态
     *
     * 枚举 {@link FamilyStatusEnum}
     */
    private Integer status;
    /**
     * 隐私等级
     *
     * 枚举 {@link FamilyPrivacyLevelEnum}
     */
    private Integer privacyLevel;
    /**
     * 租户ID(保留字段)
     */
    private Long tenantId;

}

