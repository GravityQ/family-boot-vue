package cn.iocoder.yudao.module.family.controller.app.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 成员树谱 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "成员树谱 Response VO")
@Data
public class MemberTreeRespVO {

    @Schema(description = "根节点ID", example = "1")
    private Long rootId;

    @Schema(description = "成员节点列表（扁平结构）")
    private List<MemberNodeVO> nodes;

    @Schema(description = "成员节点")
    @Data
    public static class MemberNodeVO {
        @Schema(description = "成员ID", example = "1")
        private Long id;
        @Schema(description = "姓名", example = "张三")
        private String name;
        @Schema(description = "性别", example = "1")
        private Integer gender;
        @Schema(description = "父亲ID", example = "2")
        private Long fatherId;
        @Schema(description = "母亲ID", example = "3")
        private Long motherId;
        @Schema(description = "配偶ID列表")
        private List<Long> spouseIds;
        @Schema(description = "子女ID列表")
        private List<Long> childrenIds;
    }

}

