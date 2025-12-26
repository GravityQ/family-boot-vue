package cn.iocoder.yudao.module.family.controller.app.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 申请新增成员 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "申请新增成员 Request VO")
@Data
public class MemberApplyCreateReqVO {

    @Schema(description = "家族编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "家族编号不能为空")
    private Long familyId;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotNull(message = "姓名不能为空")
    private String name;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "性别不能为空")
    private Integer gender;

    @Schema(description = "父亲ID", example = "2")
    private Long fatherId;

    @Schema(description = "母亲ID", example = "3")
    private Long motherId;

}

