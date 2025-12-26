package cn.iocoder.yudao.module.family.controller.admin.family;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.family.controller.admin.family.vo.FamilyAuditReqVO;
import cn.iocoder.yudao.module.family.service.family.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 家族 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 家族")
@RestController
@RequestMapping("/admin-api/family")
@Validated
public class AdminFamilyController {

    @Resource
    private FamilyService familyService;

    @PutMapping("/audit/approve")
    @Operation(summary = "审核通过家族创建申请")
    @PreAuthorize("@ss.hasPermission('family:audit:approve')")
    public CommonResult<Boolean> approveFamilyCreate(@Valid @RequestBody FamilyAuditReqVO auditReqVO) {
        familyService.approveFamilyCreate(auditReqVO);
        return success(true);
    }

    @PutMapping("/audit/reject")
    @Operation(summary = "审核驳回家族创建申请")
    @PreAuthorize("@ss.hasPermission('family:audit:reject')")
    public CommonResult<Boolean> rejectFamilyCreate(@Valid @RequestBody FamilyAuditReqVO auditReqVO) {
        familyService.rejectFamilyCreate(auditReqVO);
        return success(true);
    }

}

