package cn.iocoder.yudao.module.family.controller.app.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 成员详情 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "成员详情 Response VO")
@Data
public class MemberDetailRespVO {

    @Schema(description = "成员ID", example = "1")
    private Long id;
    @Schema(description = "姓名", example = "张三")
    private String name;
    @Schema(description = "性别", example = "1")
    private Integer gender;
    @Schema(description = "出生日期")
    private LocalDate birthDate;
    @Schema(description = "去世日期")
    private LocalDate deathDate;
    @Schema(description = "是否健在", example = "true")
    private Boolean isAlive;
    @Schema(description = "头像URL")
    private String avatarUrl;
    @Schema(description = "人物行传/生平")
    private String biography;
    @Schema(description = "父亲ID", example = "2")
    private Long fatherId;
    @Schema(description = "母亲ID", example = "3")
    private Long motherId;
    @Schema(description = "配偶ID列表")
    private List<Long> spouseIds;
    @Schema(description = "子女ID列表")
    private List<Long> childrenIds;

}

