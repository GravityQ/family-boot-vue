package cn.iocoder.yudao.module.family.controller.app.family.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 家族创建申请 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "家族创建申请 Request VO")
@Data
public class FamilyCreateReqVO {

    @Schema(description = "家族名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张氏家族")
    @NotBlank(message = "家族名称不能为空")
    private String name;

    @Schema(description = "主要姓氏", requiredMode = Schema.RequiredMode.REQUIRED, example = "张")
    @NotBlank(message = "主要姓氏不能为空")
    private String mainSurname;

    @Schema(description = "祖籍位置", example = "河南省郑州市")
    private String location;

    @Schema(description = "家族简介/家训", example = "传承家族文化")
    private String desc;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

}

