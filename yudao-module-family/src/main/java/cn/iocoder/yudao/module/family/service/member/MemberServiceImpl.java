package cn.iocoder.yudao.module.family.service.member;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityUtils;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberTreeRespVO;
import cn.iocoder.yudao.module.family.controller.admin.member.vo.MemberCreateReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.audit.AuditLogDO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.dataobject.familyunit.FamilyUnitDO;
import cn.iocoder.yudao.module.family.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.family.dal.mysql.audit.AuditLogMapper;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.dal.mysql.familyunit.FamilyUnitMapper;
import cn.iocoder.yudao.module.family.dal.mysql.member.MemberMapper;
import cn.iocoder.yudao.module.family.enums.AuditEntityTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditOperationTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditStatusEnum;
import cn.iocoder.yudao.module.family.enums.FamilyStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

/**
 * 成员 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class MemberServiceImpl implements MemberService {

    @Resource
    private MemberMapper memberMapper;
    @Resource
    private FamilyMapper familyMapper;
    @Resource
    private FamilyUnitMapper familyUnitMapper;
    @Resource
    private AuditLogMapper auditLogMapper;

    @Override
    public MemberTreeRespVO getMemberTree(Long familyId) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(familyId);
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // TODO: 校验当前用户是否是家族成员（后续实现）
        // 暂时先检查隐私等级
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (family.getPrivacyLevel() == 1 && !isFamilyMember(familyId, currentUserId)) {
            throw exception(FAMILY_NOT_MEMBER);
        }

        // 查询所有成员
        List<MemberDO> members = memberMapper.selectListByFamilyId(familyId);
        if (members.isEmpty()) {
            MemberTreeRespVO respVO = new MemberTreeRespVO();
            respVO.setNodes(Collections.emptyList());
            return respVO;
        }

        // 查找根节点（fatherId为null的节点）
        MemberDO root = members.stream()
                .filter(m -> m.getFatherId() == null)
                .findFirst()
                .orElse(members.get(0)); // 如果没有根节点，使用第一个成员

        // 构建扁平节点列表
        List<MemberTreeRespVO.MemberNodeVO> nodes = members.stream()
                .map(member -> {
                    MemberTreeRespVO.MemberNodeVO node = new MemberTreeRespVO.MemberNodeVO();
                    node.setId(member.getId());
                    node.setName(member.getName());
                    node.setGender(member.getGender());
                    node.setFatherId(member.getFatherId());
                    node.setMotherId(member.getMotherId());
                    // TODO: 填充spouseIds和childrenIds（后续实现）
                    node.setSpouseIds(Collections.emptyList());
                    node.setChildrenIds(Collections.emptyList());
                    return node;
                })
                .collect(Collectors.toList());

        MemberTreeRespVO respVO = new MemberTreeRespVO();
        respVO.setRootId(root.getId());
        respVO.setNodes(nodes);
        return respVO;
    }

    @Override
    public MemberDetailRespVO getMember(Long id) {
        // 校验成员存在
        MemberDO member = memberMapper.selectById(id);
        if (member == null) {
            throw exception(MEMBER_NOT_EXISTS);
        }

        // TODO: 校验当前用户是否是家族成员（后续实现）
        FamilyDO family = familyMapper.selectById(member.getFamilyId());
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (family.getPrivacyLevel() == 1 && !isFamilyMember(member.getFamilyId(), currentUserId)) {
            throw exception(FAMILY_NOT_MEMBER);
        }

        MemberDetailRespVO respVO = BeanUtils.toBean(member, MemberDetailRespVO.class);
        // TODO: 填充spouseIds和childrenIds（后续实现）
        respVO.setSpouseIds(Collections.emptyList());
        respVO.setChildrenIds(Collections.emptyList());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyCreateMember(MemberApplyCreateReqVO createReqVO) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(createReqVO.getFamilyId());
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // TODO: 校验当前用户是否是家族成员（后续实现）

        // 创建审核记录
        AuditLogDO auditLog = new AuditLogDO();
        auditLog.setFamilyId(createReqVO.getFamilyId());
        auditLog.setApplicantId(SecurityUtils.getLoginUserId());
        auditLog.setEntityType(AuditEntityTypeEnum.MEMBER.getType());
        auditLog.setEntityId(null); // 新增时为空
        auditLog.setOperationType(AuditOperationTypeEnum.CREATE.getType());
        auditLog.setOldJson(null); // 新增时没有旧值
        auditLog.setNewJson(JSONUtil.toJsonStr(createReqVO)); // 存储新值
        auditLog.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
        auditLogMapper.insert(auditLog);
        return auditLog.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyUpdateMember(MemberApplyUpdateReqVO updateReqVO) {
        // 校验成员存在
        MemberDO member = memberMapper.selectById(updateReqVO.getId());
        if (member == null) {
            throw exception(MEMBER_NOT_EXISTS);
        }

        // TODO: 校验当前用户是否是家族成员（后续实现）

        // 构建变更字段的JSON（仅包含变更字段）
        Map<String, Object> newValues = new HashMap<>();
        if (StrUtil.isNotBlank(updateReqVO.getName())) {
            newValues.put("name", updateReqVO.getName());
        }
        if (updateReqVO.getGender() != null) {
            newValues.put("gender", updateReqVO.getGender());
        }
        // TODO: 添加其他可修改字段

        // 创建审核记录
        AuditLogDO auditLog = new AuditLogDO();
        auditLog.setFamilyId(member.getFamilyId());
        auditLog.setApplicantId(SecurityUtils.getLoginUserId());
        auditLog.setEntityType(AuditEntityTypeEnum.MEMBER.getType());
        auditLog.setEntityId(updateReqVO.getId());
        auditLog.setOperationType(AuditOperationTypeEnum.UPDATE.getType());
        auditLog.setOldJson(JSONUtil.toJsonStr(member)); // 存储旧值
        auditLog.setNewJson(JSONUtil.toJsonStr(newValues)); // 仅存储变更字段
        auditLog.setAuditStatus(AuditStatusEnum.PENDING.getStatus());
        auditLogMapper.insert(auditLog);
        return auditLog.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMember(MemberCreateReqVO createReqVO) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(createReqVO.getFamilyId());
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 校验当前用户是否是家族管理员
        Long currentUserId = SecurityUtils.getLoginUserId();
        if (!family.getCreatorId().equals(currentUserId)) {
            throw exception(FAMILY_NOT_ADMIN);
        }

        // 直接插入成员
        MemberDO member = BeanUtils.toBean(createReqVO, MemberDO.class);
        memberMapper.insert(member);
        return member.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMember(Long id) {
        // 校验成员存在
        MemberDO member = memberMapper.selectById(id);
        if (member == null) {
            throw exception(MEMBER_NOT_EXISTS);
        }

        // 校验是否有子女
        Long childrenCount = memberMapper.selectCountByFatherId(id);
        if (childrenCount > 0) {
            throw exception(MEMBER_HAS_CHILDREN);
        }

        // 删除成员
        memberMapper.deleteById(id);
    }

    /**
     * 判断用户是否是家族成员
     * TODO: 后续实现家族成员关系表后，完善此方法
     */
    private boolean isFamilyMember(Long familyId, Long userId) {
        // 暂时简单判断：如果是家族创建者，则认为是成员
        FamilyDO family = familyMapper.selectById(familyId);
        return family != null && family.getCreatorId().equals(userId);
    }

}

