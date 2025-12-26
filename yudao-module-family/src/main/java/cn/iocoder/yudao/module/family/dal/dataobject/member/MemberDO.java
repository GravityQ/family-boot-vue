package cn.iocoder.yudao.module.family.dal.dataobject.member;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 成员 DO
 *
 * @author 芋道源码
 */
@TableName("member")
@KeySequence("member_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 所属家族ID
     */
    private Long familyId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别: 1男, 0女
     */
    private Integer gender;
    /**
     * 字辈/第几世
     */
    private Integer generationLevel;
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    /**
     * 去世日期
     */
    private LocalDate deathDate;
    /**
     * 是否健在
     */
    private Boolean isAlive;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 人物行传/生平
     */
    private String biography;
    /**
     * 父亲ID (冗余,便于快速构建树)
     */
    private Long fatherId;
    /**
     * 母亲ID (冗余)
     */
    private Long motherId;
    /**
     * 所属的原生家庭单元ID
     */
    private Long parentFamilyUnitId;
    /**
     * 排序(长幼顺序)
     */
    private Integer sort;

}

