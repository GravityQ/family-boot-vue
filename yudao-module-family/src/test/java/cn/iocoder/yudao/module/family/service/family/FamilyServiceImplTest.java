package cn.iocoder.yudao.module.family.service.family;

import cn.iocoder.yudao.framework.security.core.util.SecurityUtils;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyRespVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.admin.family.vo.FamilyAuditReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.enums.FamilyPrivacyLevelEnum;
import cn.iocoder.yudao.module.family.enums.FamilyStatusEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link FamilyServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(FamilyServiceImpl.class)
public class FamilyServiceImplTest extends BaseDbUnitTest {

    @Resource
    private FamilyServiceImpl familyService;

    @Resource
    private FamilyMapper familyMapper;

    @MockitoBean
    private SecurityUtils securityUtils;

    @Test
    public void testCreateFamily_success() {
        // 准备参数
        Long userId = 1L;
        FamilyCreateReqVO createReqVO = randomPojo(FamilyCreateReqVO.class, o -> {
            o.setName("测试家族");
            o.setMainSurname("张");
        });

        // mock 当前登录用户
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            Long familyId = familyService.createFamily(createReqVO);

            // 断言
            assertNotNull(familyId);
            FamilyDO family = familyMapper.selectById(familyId);
            assertNotNull(family);
            assertEquals("测试家族", family.getName());
            assertEquals("张", family.getMainSurname());
            assertEquals(userId, family.getCreatorId());
            assertEquals(FamilyStatusEnum.PENDING.getStatus(), family.getStatus());
        }
    }

    @Test
    public void testCreateFamily_duplicateName() {
        // 准备参数
        Long userId = 1L;
        FamilyCreateReqVO createReqVO = randomPojo(FamilyCreateReqVO.class, o -> {
            o.setName("测试家族");
        });

        // mock 数据：已存在同名家族
        FamilyDO existingFamily = randomPojo(FamilyDO.class, o -> {
            o.setName("测试家族");
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(existingFamily);

        // mock 当前登录用户
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用，并断言异常
            assertServiceException(() -> familyService.createFamily(createReqVO), FAMILY_NAME_DUPLICATE);
        }
    }

    @Test
    public void testApproveFamilyCreate_success() {
        // 准备参数
        Long userId = 1L;
        Long familyId = 1L;
        FamilyAuditReqVO auditReqVO = randomPojo(FamilyAuditReqVO.class, o -> {
            o.setId(familyId);
        });

        // mock 数据：待审核的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId);
            o.setStatus(FamilyStatusEnum.PENDING.getStatus());
        });
        familyMapper.insert(family);

        // mock 当前登录用户（系统管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(999L); // 系统管理员ID

            // 调用
            familyService.approveFamilyCreate(auditReqVO);

            // 断言
            FamilyDO updatedFamily = familyMapper.selectById(familyId);
            assertEquals(FamilyStatusEnum.APPROVED.getStatus(), updatedFamily.getStatus());
        }
    }

    @Test
    public void testRejectFamilyCreate_success() {
        // 准备参数
        Long userId = 1L;
        Long familyId = 1L;
        String rejectReason = "家族名称不符合规范";
        FamilyAuditReqVO auditReqVO = randomPojo(FamilyAuditReqVO.class, o -> {
            o.setId(familyId);
            o.setRejectReason(rejectReason);
        });

        // mock 数据：待审核的家族
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId);
            o.setStatus(FamilyStatusEnum.PENDING.getStatus());
        });
        familyMapper.insert(family);

        // mock 当前登录用户（系统管理员）
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(999L); // 系统管理员ID

            // 调用
            familyService.rejectFamilyCreate(auditReqVO);

            // 断言
            FamilyDO updatedFamily = familyMapper.selectById(familyId);
            assertEquals(FamilyStatusEnum.REJECTED.getStatus(), updatedFamily.getStatus());
        }
    }

    @Test
    public void testUpdateFamily_success() {
        // 准备参数
        Long userId = 1L;
        Long familyId = 1L;
        FamilyUpdateReqVO updateReqVO = randomPojo(FamilyUpdateReqVO.class, o -> {
            o.setId(familyId);
            o.setCoverUrl("https://example.com/cover.jpg");
            o.setDesc("更新后的简介");
            o.setPrivacyLevel(FamilyPrivacyLevelEnum.MEMBERS_ONLY.getLevel());
        });

        // mock 数据：已生效的家族，且当前用户是家族管理员
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(userId); // 创建者即为家族管理员
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 当前登录用户
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用
            familyService.updateFamily(updateReqVO);

            // 断言
            FamilyDO updatedFamily = familyMapper.selectById(familyId);
            assertEquals("https://example.com/cover.jpg", updatedFamily.getCoverUrl());
            assertEquals("更新后的简介", updatedFamily.getDesc());
            assertEquals(FamilyPrivacyLevelEnum.MEMBERS_ONLY.getLevel(), updatedFamily.getPrivacyLevel());
        }
    }

    @Test
    public void testUpdateFamily_notAdmin() {
        // 准备参数
        Long userId = 1L;
        Long otherUserId = 2L;
        Long familyId = 1L;
        FamilyUpdateReqVO updateReqVO = randomPojo(FamilyUpdateReqVO.class, o -> {
            o.setId(familyId);
        });

        // mock 数据：已生效的家族，但当前用户不是家族管理员
        FamilyDO family = randomPojo(FamilyDO.class, o -> {
            o.setId(familyId);
            o.setCreatorId(otherUserId); // 其他用户是创建者
            o.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        });
        familyMapper.insert(family);

        // mock 当前登录用户
        try (var mockedStatic = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getLoginUserId).thenReturn(userId);

            // 调用，并断言异常
            assertServiceException(() -> familyService.updateFamily(updateReqVO), FAMILY_NOT_ADMIN);
        }
    }

}

