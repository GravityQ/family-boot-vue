package cn.iocoder.yudao.module.family.controller.admin.audit;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditApproveReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditRejectReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditRespVO;
import cn.iocoder.yudao.module.family.service.audit.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 审核 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 审核")
@RestController
@RequestMapping("/admin-api/family/audit")
@Validated
public class AdminAuditController {

    @Resource
    private AuditService auditService;

    @GetMapping("/page")
    @Operation(summary = "分页查询审核记录")
    @PreAuthorize("@ss.hasPermission('family:audit:query')")
    public CommonResult<PageResult<AuditRespVO>> getAuditPage(@Valid AuditPageReqVO pageReqVO) {
        return success(auditService.getAuditPage(pageReqVO));
    }

    @PutMapping("/approve")
    @Operation(summary = "审核通过")
    @PreAuthorize("@ss.hasPermission('family:audit:approve')")
    public CommonResult<Boolean> approveAudit(@Valid @RequestBody AuditApproveReqVO approveReqVO) {
        auditService.approveAudit(approveReqVO);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "审核驳回")
    @PreAuthorize("@ss.hasPermission('family:audit:reject')")
    public CommonResult<Boolean> rejectAudit(@Valid @RequestBody AuditRejectReqVO rejectReqVO) {
        auditService.rejectAudit(rejectReqVO);
        return success(true);
    }

}

