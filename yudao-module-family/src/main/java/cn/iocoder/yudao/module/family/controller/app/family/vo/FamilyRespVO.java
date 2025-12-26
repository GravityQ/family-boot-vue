package cn.iocoder.yudao.module.family.controller.app.family.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 家族详情 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "家族详情 Response VO")
@Data
public class FamilyRespVO {

    @Schema(description = "家族编号", example = "1")
    private Long id;

    @Schema(description = "家族名称", example = "张氏家族")
    private String name;

    @Schema(description = "主要姓氏", example = "张")
    private String mainSurname;

    @Schema(description = "祖籍位置", example = "河南省郑州市")
    private String location;

    @Schema(description = "家族简介/家训", example = "传承家族文化")
    private String desc;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "隐私等级", example = "0")
    private Integer privacyLevel;

    @Schema(description = "是否家族成员", example = "true")
    private Boolean isMember;

}

