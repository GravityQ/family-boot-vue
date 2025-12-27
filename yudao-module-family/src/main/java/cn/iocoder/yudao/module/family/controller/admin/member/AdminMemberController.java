package cn.iocoder.yudao.module.family.controller.admin.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.family.controller.admin.member.vo.MemberCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 成员 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 成员")
@RestController
@RequestMapping("/family/member")
@Validated
public class AdminMemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/create")
    @Operation(summary = "管理员免审新增成员")
    @PreAuthorize("@ss.hasPermission('family:member:create')")
    public CommonResult<Long> createMember(@Valid @RequestBody MemberCreateReqVO createReqVO) {
        return success(memberService.createMember(createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得成员")
    @Parameter(name = "id", description = "成员编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('family:member:query')")
    public CommonResult<MemberDetailRespVO> getMember(@RequestParam("id") Long id) {
        return success(memberService.getMember(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除成员")
    @PreAuthorize("@ss.hasPermission('family:member:delete')")
    @Parameter(name = "id", description = "成员编号", required = true, example = "1")
    public CommonResult<Boolean> deleteMember(@RequestParam("id") Long id) {
        memberService.deleteMember(id);
        return success(true);
    }

}

