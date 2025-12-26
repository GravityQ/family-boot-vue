package cn.iocoder.yudao.module.family.service.family;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityUtils;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyRespVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.admin.family.vo.FamilyAuditReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.enums.FamilyStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

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
        family.setCreatorId(SecurityUtils.getLoginUserId());
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

        FamilyRespVO respVO = BeanUtils.toBean(family, FamilyRespVO.class);
        // TODO: 判断当前用户是否是家族成员（后续实现）
        respVO.setIsMember(false);
        return respVO;
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
        Long currentUserId = SecurityUtils.getLoginUserId();
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

}

