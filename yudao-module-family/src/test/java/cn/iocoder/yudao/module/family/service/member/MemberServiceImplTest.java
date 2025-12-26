package cn.iocoder.yudao.module.family.service.member;

import cn.iocoder.yudao.framework.security.core.util.SecurityUtils;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberTreeRespVO;
import cn.iocoder.yudao.module.family.controller.admin.member.vo.MemberCreateReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.family.dal.dataobject.familyunit.FamilyUnitDO;
import cn.iocoder.yudao.module.family.dal.dataobject.audit.AuditLogDO;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.dal.mysql.member.MemberMapper;
import cn.iocoder.yudao.module.family.dal.mysql.familyunit.FamilyUnitMapper;
import cn.iocoder.yudao.module.family.dal.mysql.audit.AuditLogMapper;
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
 * {@link MemberServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MemberServiceImpl.class)
public class MemberServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MemberServiceImpl memberService;

    @Resource
    private MemberMapper memberMapper;
    @Resource
    private FamilyMapper familyMapper;
    @Resource
    private FamilyUnitMapper familyUnitMapper;
    @Resource
    private AuditLogMapper auditLogMapper;

    @Test
    public void testGetMemberTree_success() {
        // 准备参数
        Long familyId = 1L;
        Long userId = 1L;

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：成员
        MemberDO rootMember = randomPojo(MemberDO.class, o -> {
            o.setFamilyId(familyId);
            o.setName("祖先");
            o.setFatherId(null); // root节点
        });
        memberMapper.insert(rootMember);

        MemberDO childMember = randomPojo(MemberDO.class, o -> {
            o.setFamilyId(familyId);
            o.setName("儿子");
            o.setFatherId(rootMember.getId());
        });
        memberMapper.insert(childMember);

        // mock 当前登录用户（已加入家族）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            MemberTreeRespVO tree = memberService.getMemberTree(familyId);

            // 断言
            assertNotNull(tree);
            assertNotNull(tree.getRootId());
            assertNotNull(tree.getNodes());
            assertEquals(2, tree.getNodes().size());
        }
    }

    @Test
    public void testGetMemberTree_notMember() {
        // 准备参数
        Long familyId = 1L;
        Long userId = 1L;

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
            o.setPrivacyLevel(1); // 仅族人可见
        });
        familyMapper.insert(family);

        // mock 当前登录用户（未加入家族）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用，并断言异常
            assertServiceException(() -> memberService.getMemberTree(familyId), FAMILY_NOT_MEMBER);
        }
    }

    @Test
    public void testGetMember_success() {
        // 准备参数
        Long memberId = 1L;
        Long familyId = 1L;
        Long userId = 1L;

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：成员
        MemberDO member = randomPojo(MemberDO.class, o -> {
            o.setId(memberId);
            o.setFamilyId(familyId);
            o.setName("张三");
            o.setGender(1);
        });
        memberMapper.insert(member);

        // mock 当前登录用户（已加入家族）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            MemberDetailRespVO detail = memberService.getMember(memberId);

            // 断言
            assertNotNull(detail);
            assertEquals("张三", detail.getName());
            assertEquals(1, detail.getGender());
        }
    }

    @Test
    public void testApplyCreateMember_success() {
        // 准备参数
        Long familyId = 1L;
        Long userId = 1L;
        MemberApplyCreateReqVO createReqVO = randomPojo(MemberApplyCreateReqVO.class, o -> {
            o.setFamilyId(familyId);
            o.setName("新成员");
            o.setGender(1);
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 当前登录用户（已加入家族）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            Long auditLogId = memberService.applyCreateMember(createReqVO);

            // 断言
            assertNotNull(auditLogId);
            AuditLogDO auditLog = auditLogMapper.selectById(auditLogId);
            assertNotNull(auditLog);
            assertEquals(familyId, auditLog.getFamilyId());
            assertEquals(userId, auditLog.getApplicantId());
            assertEquals(AuditEntityTypeEnum.MEMBER.getType(), auditLog.getEntityType());
            assertEquals(AuditOperationTypeEnum.CREATE.getType(), auditLog.getOperationType());
            assertEquals(AuditStatusEnum.PENDING.getStatus(), auditLog.getAuditStatus());
            // 断言正式表中没有新增成员
            assertEquals(0, memberMapper.selectCount());
        }
    }

    @Test
    public void testApplyUpdateMember_success() {
        // 准备参数
        Long memberId = 1L;
        Long familyId = 1L;
        Long userId = 1L;
        MemberApplyUpdateReqVO updateReqVO = randomPojo(MemberApplyUpdateReqVO.class, o -> {
            o.setId(memberId);
            o.setName("更新后的姓名");
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：成员
        MemberDO member = randomPojo(MemberDO.class, o -> {
            o.setId(memberId);
            o.setFamilyId(familyId);
            o.setName("原姓名");
        });
        memberMapper.insert(member);

        // mock 当前登录用户（已加入家族）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            Long auditLogId = memberService.applyUpdateMember(updateReqVO);

            // 断言
            assertNotNull(auditLogId);
            AuditLogDO auditLog = auditLogMapper.selectById(auditLogId);
            assertNotNull(auditLog);
            assertEquals(memberId, auditLog.getEntityId());
            assertEquals(AuditOperationTypeEnum.UPDATE.getType(), auditLog.getOperationType());
            // 断言正式表中的成员数据未改变
            MemberDO unchangedMember = memberMapper.selectById(memberId);
            assertEquals("原姓名", unchangedMember.getName());
        }
    }

    @Test
    public void testCreateMember_success() {
        // 准备参数
        Long familyId = 1L;
        Long userId = 1L;
        MemberCreateReqVO createReqVO = randomPojo(MemberCreateReqVO.class, o -> {
            o.setFamilyId(familyId);
            o.setName("新成员");
            o.setGender(1);
        });

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId); // 家族管理员
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 当前登录用户（家族管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            Long memberId = memberService.createMember(createReqVO);

            // 断言
            assertNotNull(memberId);
            MemberDO member = memberMapper.selectById(memberId);
            assertNotNull(member);
            assertEquals("新成员", member.getName());
            assertEquals(1, member.getGender());
        }
    }

    @Test
    public void testDeleteMember_hasChildren() {
        // 准备参数
        Long memberId = 1L;
        Long familyId = 1L;
        Long userId = 1L;

        // mock 数据：已生效的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId);
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 数据：成员及其子女
        MemberDO member = randomPojo(MemberDO.class, o -> {
            o.setId(memberId);
            o.setFamilyId(familyId);
        });
        memberMapper.insert(member);

        MemberDO child = randomPojo(MemberDO.class, o -> {
            o.setFamilyId(familyId);
            o.setFatherId(memberId); // 有子女
        });
        memberMapper.insert(child);

        // mock 当前登录用户（家族管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用，并断言异常
            assertServiceException(() -> memberService.deleteMember(memberId), MEMBER_HAS_CHILDREN);
        }
    }

}

