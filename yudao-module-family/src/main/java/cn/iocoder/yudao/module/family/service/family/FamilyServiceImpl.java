package cn.iocoder.yudao.module.family.service.family;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyRespVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.admin.family.vo.FamilyAuditReqVO;
import cn.iocoder.yudao.module.family.convert.family.FamilyConvert;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.dal.mysql.familymember.FamilyMemberMapper;
import cn.iocoder.yudao.module.family.enums.FamilyStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

/**
 * 家族 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class FamilyServiceImpl implements FamilyService {

    @Resource
    private FamilyMapper familyMapper;
    @Resource
    private FamilyMemberMapper familyMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFamily(FamilyCreateReqVO createReqVO) {
        // 校验家族名称唯一性
        FamilyDO existingFamily = familyMapper.selectByName(createReqVO.getName());
        if (existingFamily != null) {
            throw exception(FAMILY_NAME_DUPLICATE);
        }

        // 插入家族记录
        FamilyDO family = BeanUtils.toBean(createReqVO, FamilyDO.class);
        family.setCreatorId(SecurityFrameworkUtils.getLoginUserId());
        family.setStatus(FamilyStatusEnum.PENDING.getStatus());
        family.setPrivacyLevel(0); // 默认公开
        familyMapper.insert(family);
        return family.getId();
    }

    @Override
    public FamilyRespVO getFamily(Long id) {
        FamilyDO family = familyMapper.selectById(id);
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 判断当前用户是否是家族成员
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Boolean isMember = isFamilyMember(id, currentUserId);
        // 使用 Convert 进行转换和脱敏
        return FamilyConvert.INSTANCE.convert(family, isMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFamily(FamilyUpdateReqVO updateReqVO) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(updateReqVO.getId());
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 校验当前用户是否是家族管理员
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!family.getCreatorId().equals(currentUserId)) {
            throw exception(FAMILY_NOT_ADMIN);
        }

        // 更新家族信息
        FamilyDO updateObj = BeanUtils.toBean(updateReqVO, FamilyDO.class);
        familyMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveFamilyCreate(FamilyAuditReqVO auditReqVO) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(auditReqVO.getId());
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 校验家族状态是待审核
        if (!FamilyStatusEnum.PENDING.getStatus().equals(family.getStatus())) {
            throw exception(FAMILY_STATUS_NOT_PENDING);
        }

        // 更新家族状态为已生效
        FamilyDO updateObj = new FamilyDO();
        updateObj.setId(auditReqVO.getId());
        updateObj.setStatus(FamilyStatusEnum.APPROVED.getStatus());
        familyMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectFamilyCreate(FamilyAuditReqVO auditReqVO) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(auditReqVO.getId());
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 校验家族状态是待审核
        if (!FamilyStatusEnum.PENDING.getStatus().equals(family.getStatus())) {
            throw exception(FAMILY_STATUS_NOT_PENDING);
        }

        // 更新家族状态为已拒绝
        FamilyDO updateObj = new FamilyDO();
        updateObj.setId(auditReqVO.getId());
        updateObj.setStatus(FamilyStatusEnum.REJECTED.getStatus());
        familyMapper.updateById(updateObj);
    }

    /**
     * 判断用户是否是家族成员
     */
    private boolean isFamilyMember(Long familyId, Long userId) {
        if (familyId == null || userId == null) {
            return false;
        }
        // 先查询家族成员关系表
        if (familyMemberMapper.existsByFamilyIdAndUserId(familyId, userId)) {
            return true;
        }
        // 如果是家族创建者，也认为是成员
        FamilyDO family = familyMapper.selectById(familyId);
        return family != null && family.getCreatorId().equals(userId);
    }

}

