package cn.iocoder.yudao.module.family.service.audit;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityUtils;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditApproveReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditRejectReqVO;
import cn.iocoder.yudao.module.family.controller.admin.audit.vo.AuditRespVO;
import cn.iocoder.yudao.module.family.dal.dataobject.audit.AuditLogDO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.family.dal.mysql.audit.AuditLogMapper;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.dal.mysql.member.MemberMapper;
import cn.iocoder.yudao.module.family.enums.AuditEntityTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditOperationTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

/**
 * 审核 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AuditServiceImpl implements AuditService {

    @Resource
    private AuditLogMapper auditLogMapper;
    @Resource
    private FamilyMapper familyMapper;
    @Resource
    private MemberMapper memberMapper;

    @Override
    public PageResult<AuditRespVO> getAuditPage(AuditPageReqVO pageReqVO) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(pageReqVO.getFamilyId());
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 校验当前用户是否是家族管理员
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (!family.getCreatorId().equals(currentUserId)) {
            throw exception(FAMILY_NOT_ADMIN);
        }

        // 分页查询审核记录
        PageResult<AuditLogDO> pageResult = auditLogMapper.selectPageByFamilyIdAndStatus(
                pageReqVO.getFamilyId(),
                pageReqVO.getAuditStatus(),
                pageReqVO.getPageNo(),
                pageReqVO.getPageSize()
        );

        // 转换为VO
        return new PageResult<>(
                BeanUtils.toBean(pageResult.getList(), AuditRespVO.class),
                pageResult.getTotal()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveAudit(AuditApproveReqVO approveReqVO) {
        // 校验审核记录存在
        AuditLogDO auditLog = auditLogMapper.selectById(approveReqVO.getId());
        if (auditLog == null) {
            throw exception(AUDIT_LOG_NOT_EXISTS);
        }

        // 校验审核记录状态是待审核
        if (!AuditStatusEnum.PENDING.getStatus().equals(auditLog.getAuditStatus())) {
            throw exception(AUDIT_LOG_STATUS_NOT_PENDING);
        }

        // 校验当前用户是否是家族管理员
        FamilyDO family = familyMapper.selectById(auditLog.getFamilyId());
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (!family.getCreatorId().equals(currentUserId)) {
            throw exception(AUDIT_LOG_NOT_FAMILY_ADMIN);
        }

        // 根据操作类型执行相应操作
        if (AuditEntityTypeEnum.MEMBER.getType().equals(auditLog.getEntityType())) {
            if (AuditOperationTypeEnum.CREATE.getType().equals(auditLog.getOperationType())) {
                // 新增成员：从newJson解析并插入
                Map<String, Object> newValues = JSONUtil.toBean(auditLog.getNewJson(), Map.class);
                MemberDO member = new MemberDO();
                member.setFamilyId(auditLog.getFamilyId());
                if (newValues.containsKey("name")) {
                    member.setName((String) newValues.get("name"));
                }
                if (newValues.containsKey("gender")) {
                    member.setGender((Integer) newValues.get("gender"));
                }
                if (newValues.containsKey("fatherId")) {
                    member.setFatherId(((Number) newValues.get("fatherId")).longValue());
                }
                if (newValues.containsKey("motherId")) {
                    member.setMotherId(((Number) newValues.get("motherId")).longValue());
                }
                memberMapper.insert(member);
            } else if (AuditOperationTypeEnum.UPDATE.getType().equals(auditLog.getOperationType())) {
                // 修改成员：从newJson解析并更新
                Map<String, Object> newValues = JSONUtil.toBean(auditLog.getNewJson(), Map.class);
                MemberDO updateObj = new MemberDO();
                updateObj.setId(auditLog.getEntityId());
                if (newValues.containsKey("name")) {
                    updateObj.setName((String) newValues.get("name"));
                }
                if (newValues.containsKey("gender")) {
                    updateObj.setGender((Integer) newValues.get("gender"));
                }
                memberMapper.updateById(updateObj);
            }
        }

        // 更新审核记录状态
        AuditLogDO updateObj = new AuditLogDO();
        updateObj.setId(auditLog.getId());
        updateObj.setAuditStatus(AuditStatusEnum.APPROVED.getStatus());
        updateObj.setAuditorId(currentUserId);
        updateObj.setAuditTime(LocalDateTime.now());
        auditLogMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectAudit(AuditRejectReqVO rejectReqVO) {
        // 校验审核记录存在
        AuditLogDO auditLog = auditLogMapper.selectById(rejectReqVO.getId());
        if (auditLog == null) {
            throw exception(AUDIT_LOG_NOT_EXISTS);
        }

        // 校验审核记录状态是待审核
        if (!AuditStatusEnum.PENDING.getStatus().equals(auditLog.getAuditStatus())) {
            throw exception(AUDIT_LOG_STATUS_NOT_PENDING);
        }

        // 校验当前用户是否是家族管理员
        FamilyDO family = familyMapper.selectById(auditLog.getFamilyId());
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (!family.getCreatorId().equals(currentUserId)) {
            throw exception(AUDIT_LOG_NOT_FAMILY_ADMIN);
        }

        // 更新审核记录状态为驳回
        AuditLogDO updateObj = new AuditLogDO();
        updateObj.setId(auditLog.getId());
        updateObj.setAuditStatus(AuditStatusEnum.REJECTED.getStatus());
        updateObj.setAuditComment(rejectReqVO.getRejectReason());
        updateObj.setAuditorId(currentUserId);
        updateObj.setAuditTime(LocalDateTime.now());
        auditLogMapper.updateById(updateObj);
    }

}

