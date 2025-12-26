package cn.iocoder.yudao.module.family.service.audit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditApproveReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditRejectReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditRespVO;

/**
 * 审核 Service 接口
 *
 * @author 芋道源码
 */
public interface AuditService {

    /**
     * 分页查询审核记录
     *
     * @param pageReqVO 分页查询
     * @return 审核记录分页
     */
    PageResult<AuditRespVO> getAuditPage(AuditPageReqVO pageReqVO);

    /**
     * 审核通过
     *
     * @param approveReqVO 审核信息
     */
    void approveAudit(AuditApproveReqVO approveReqVO);

    /**
     * 审核驳回
     *
     * @param rejectReqVO 审核信息
     */
    void rejectAudit(AuditRejectReqVO rejectReqVO);

}

