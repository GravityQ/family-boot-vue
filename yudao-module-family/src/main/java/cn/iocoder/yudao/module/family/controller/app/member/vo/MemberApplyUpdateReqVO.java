package cn.iocoder.yudao.module.family.controller.app.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 申请修改成员 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "申请修改成员 Request VO")
@Data
public class MemberApplyUpdateReqVO {

    @Schema(description = "成员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "成员编号不能为空")
    private Long id;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "性别", example = "1")
    private Integer gender;

    // 其他可修改字段...

}

