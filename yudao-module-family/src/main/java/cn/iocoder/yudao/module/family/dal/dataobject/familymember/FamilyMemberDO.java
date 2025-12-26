package cn.iocoder.yudao.module.family.dal.dataobject.familymember;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 家族成员关系 DO
 *
 * @author 芋道源码
 */
@TableName("family_member")
@KeySequence("family_member_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 家族ID
     */
    private Long familyId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色：0-普通成员，1-家族管理员
     */
    private Integer role;

}
