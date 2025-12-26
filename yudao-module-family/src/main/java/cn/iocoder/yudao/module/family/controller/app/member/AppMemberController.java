package cn.iocoder.yudao.module.family.controller.app.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberTreeRespVO;
import cn.iocoder.yudao.module.family.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 用户 App - 成员 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "用户 App - 成员")
@RestController
@RequestMapping("/app-api/family/v1/member")
@Validated
public class AppMemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/tree")
    @Operation(summary = "获取家族树谱")
    @Parameter(name = "familyId", description = "家族编号", required = true, example = "1")
    public CommonResult<MemberTreeRespVO> getMemberTree(@RequestParam("familyId") Long familyId) {
        return success(memberService.getMemberTree(familyId));
    }

    @GetMapping("/get")
    @Operation(summary = "获取成员详情")
    @Parameter(name = "id", description = "成员编号", required = true, example = "1")
    public CommonResult<MemberDetailRespVO> getMember(@RequestParam("id") Long id) {
        return success(memberService.getMember(id));
    }

    @PostMapping("/apply-create")
    @Operation(summary = "申请新增成员")
    public CommonResult<Long> applyCreateMember(@Valid @RequestBody MemberApplyCreateReqVO createReqVO) {
        return success(memberService.applyCreateMember(createReqVO));
    }

    @PostMapping("/apply-update")
    @Operation(summary = "申请修改成员")
    public CommonResult<Long> applyUpdateMember(@Valid @RequestBody MemberApplyUpdateReqVO updateReqVO) {
        return success(memberService.applyUpdateMember(updateReqVO));
    }

}

