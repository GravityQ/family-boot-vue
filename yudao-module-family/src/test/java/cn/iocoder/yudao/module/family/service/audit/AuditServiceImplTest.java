package cn.iocoder.yudao.module.family.service.audit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityUtils;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
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
import cn.iocoder.yudao.module.family.enums.FamilyStatusEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AuditServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(AuditServiceImpl.class)
public class AuditServiceImplTest extends BaseDbUnitTest {

    @Resource
    private AuditServiceImpl auditService;

    @Resource
    private AuditLogMapper auditLogMapper;
    @Resource
    private FamilyMapper familyMapper;
    @Resource
    private MemberMapper memberMapper;

    @Test
    public void testGetAuditPage_success() {
        // 准备参数
        Long familyId = 1L;
        Long userId = 1L;
        AuditPageReqVO pageReqVO = randomPojo(AuditPageReqVO.class, o -> {
            o.setFamilyId(familyId);
            o.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
            o.setPageNo(1);
            o.setPageSize(10);
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId); // 家族管理员
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：待审核记录
        AuditLogDO auditLog = randomPojo(AuditLogDO.class, o -> {
            o.setFamilyId(familyId);
            o.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
        });
        auditLogMapper.insert(auditLog);

        // mock 当前登录用户（家族管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            PageResult<AuditRespVO> pageResult = auditService.getAuditPage(pageReqVO);

            // 断言
            assertNotNull(pageResult);
            assertTrue(pageResult.getTotal() > 0);
        }
    }

    @Test
    public void testApproveAudit_success() {
        // 准备参数
        Long auditLogId = 1L;
        Long familyId = 1L;
        Long userId = 1L;
        Long memberId = 1L;
        AuditApproveReqVO approveReqVO = randomPojo(AuditApproveReqVO.class, o -> {
            o.setId(auditLogId);
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId); // 家族管理员
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：待审核的新增成员记录
        AuditLogDO auditLog = randomPojo(AuditLogDO.class, o -> {
            o.setId(auditLogId);
            o.setFamilyId(familyId);
            o.setEntityType(AuditEntityTypeEnum.MEMBER.getType());
            o.setOperationType(AuditOperationTypeEnum.CREATE.getType());
            o.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
            o.setNewJson("{\"name\":\"新成员\",\"gender\":1}");
        });
        auditLogMapper.insert(auditLog);

        // mock 当前登录用户（家族管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            auditService.approveAudit(approveReqVO);

            // 断言：审核状态已更新
            AuditLogDO updatedAuditLog = auditLogMapper.selectById(auditLogId);
            assertEquals(AuditStatusEnum.APPROVED.getStatus(), updatedAuditLog.getAuditStatus());
            assertEquals(userId, updatedAuditLog.getAuditorId());
            assertNotNull(updatedAuditLog.getAuditTime());
            // 断言：正式表中已新增成员
            assertEquals(1, memberMapper.selectCount());
        }
    }

    @Test
    public void testRejectAudit_success() {
        // 准备参数
        Long auditLogId = 1L;
        Long familyId = 1L;
        Long userId = 1L;
        String rejectReason = "信息不完整";
        AuditRejectReqVO rejectReqVO = randomPojo(AuditRejectReqVO.class, o -> {
            o.setId(auditLogId);
            o.setRejectReason(rejectReason);
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId); // 家族管理员
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：待审核记录
        AuditLogDO auditLog = randomPojo(AuditLogDO.class, o -> {
            o.setId(auditLogId);
            o.setFamilyId(familyId);
            o.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
        });
        auditLogMapper.insert(auditLog);

        // mock 当前登录用户（家族管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            auditService.rejectAudit(rejectReqVO);

            // 断言：审核状态已更新
            AuditLogDO updatedAuditLog = auditLogMapper.selectById(auditLogId);
            assertEquals(AuditStatusEnum.REJECTED.getStatus(), updatedAuditLog.getAuditStatus());
            assertEquals(rejectReason, updatedAuditLog.getAuditComment());
            assertEquals(userId, updatedAuditLog.getAuditorId());
            assertNotNull(updatedAuditLog.getAuditTime());
            // 断言：正式表中没有新增数据
            assertEquals(0, memberMapper.selectCount());
        }
    }

    @Test
    public void testApproveAudit_notFamilyAdmin() {
        // 准备参数
        Long auditLogId = 1L;
        Long familyId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;
        AuditApproveReqVO approveReqVO = randomPojo(AuditApproveReqVO.class, o -> {
            o.setId(auditLogId);
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(otherUserId); // 其他用户是家族管理员
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：待审核记录
        AuditLogDO auditLog = randomPojo(AuditLogDO.class, o -> {
            o.setId(auditLogId);
            o.setFamilyId(familyId);
            o.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
        });
        auditLogMapper.insert(auditLog);

        // mock 当前登录用户（不是家族管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用，并断言异常
            assertServiceException(() -> auditService.approveAudit(approveReqVO), AUDIT_LOG_NOT_FAMILY_ADMIN);
        }
    }

}

