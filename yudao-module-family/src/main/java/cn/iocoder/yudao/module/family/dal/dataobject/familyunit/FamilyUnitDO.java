package cn.iocoder.yudao.module.family.dal.dataobject.familyunit;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.family.enums.FamilyUnitStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 家庭单元 DO
 *
 * @author 芋道源码
 */
@TableName("family_unit")
@KeySequence("family_unit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyUnitDO extends BaseDO {

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
     * 丈夫ID
     */
    private Long husbandId;
    /**
     * 妻子ID (可为空,如未婚生子)
     */
    private Long wifeId;
    /**
     * 妻子姓名(若妻子未录入系统)
     */
    private String wifeName;
    /**
     * 结婚日期
     */
    private LocalDate marriageDate;
    /**
     * 状态
     *
     * 枚举 {@link FamilyUnitStatusEnum}
     */
    private Integer status;
    /**
     * 配偶排序(原配/续弦)
     */
    private Integer sort;

}

