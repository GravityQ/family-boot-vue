package cn.iocoder.yudao.module.family.service.member;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberTreeRespVO;
import cn.iocoder.yudao.module.family.controller.admin.member.vo.MemberCreateReqVO;
import cn.iocoder.yudao.module.family.convert.member.MemberConvert;
import cn.iocoder.yudao.module.family.dal.dataobject.audit.AuditLogDO;
import cn.iocoder.yudao.module.family.dal.dataobject.family.FamilyDO;
import cn.iocoder.yudao.module.family.dal.dataobject.familyunit.FamilyUnitDO;
import cn.iocoder.yudao.module.family.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.family.dal.mysql.audit.AuditLogMapper;
import cn.iocoder.yudao.module.family.dal.mysql.family.FamilyMapper;
import cn.iocoder.yudao.module.family.dal.mysql.familymember.FamilyMemberMapper;
import cn.iocoder.yudao.module.family.dal.mysql.familyunit.FamilyUnitMapper;
import cn.iocoder.yudao.module.family.dal.mysql.member.MemberMapper;
import cn.iocoder.yudao.module.family.enums.AuditEntityTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditOperationTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditStatusEnum;
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
@Service("familyMemberServiceImpl")
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
    @Resource
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public MemberTreeRespVO getMemberTree(Long familyId) {
        // 校验家族存在
        FamilyDO family = familyMapper.selectById(familyId);
        if (family == null) {
            throw exception(FAMILY_NOT_EXISTS);
        }

        // 校验当前用户是否是家族成员
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
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

        // 查询所有家庭单元，用于构建配偶和子女关系
        List<FamilyUnitDO> familyUnits = familyUnitMapper.selectList("family_id", familyId);
        
        // 构建成员ID到配偶ID列表的映射
        Map<Long, List<Long>> spouseMap = new HashMap<>();
        for (FamilyUnitDO unit : familyUnits) {
            if (unit.getHusbandId() != null && unit.getWifeId() != null) {
                spouseMap.computeIfAbsent(unit.getHusbandId(), k -> new ArrayList<>()).add(unit.getWifeId());
                spouseMap.computeIfAbsent(unit.getWifeId(), k -> new ArrayList<>()).add(unit.getHusbandId());
            }
        }
        
        // 构建成员ID到子女ID列表的映射
        Map<Long, List<Long>> childrenMap = new HashMap<>();
        for (MemberDO member : members) {
            if (member.getParentFamilyUnitId() != null) {
                // 找到该成员所属的家庭单元
                FamilyUnitDO unit = familyUnits.stream()
                        .filter(u -> u.getId().equals(member.getParentFamilyUnitId()))
                        .findFirst()
                        .orElse(null);
                if (unit != null) {
                    if (unit.getHusbandId() != null) {
                        childrenMap.computeIfAbsent(unit.getHusbandId(), k -> new ArrayList<>()).add(member.getId());
                    }
                    if (unit.getWifeId() != null) {
                        childrenMap.computeIfAbsent(unit.getWifeId(), k -> new ArrayList<>()).add(member.getId());
                    }
                }
            }
        }

        // 构建扁平节点列表
        List<MemberTreeRespVO.MemberNodeVO> nodes = members.stream()
                .map(member -> {
                    MemberTreeRespVO.MemberNodeVO node = new MemberTreeRespVO.MemberNodeVO();
                    node.setId(member.getId());
                    node.setName(member.getName());
                    node.setGender(member.getGender());
                    node.setFatherId(member.getFatherId());
                    node.setMotherId(member.getMotherId());
                    // 填充配偶ID列表
                    node.setSpouseIds(spouseMap.getOrDefault(member.getId(), Collections.emptyList()));
                    // 填充子女ID列表
                    node.setChildrenIds(childrenMap.getOrDefault(member.getId(), Collections.emptyList()));
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
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        Boolean isMember = isFamilyMember(member.getFamilyId(), currentUserId);
        if (family.getPrivacyLevel() == 1 && !isMember) {
            throw exception(FAMILY_NOT_MEMBER);
        }

        // 使用 Convert 进行转换和脱敏
        MemberDetailRespVO respVO = MemberConvert.INSTANCE.convert(member, isMember);
        
        // 查询配偶关系
        List<Long> spouseIds = new ArrayList<>();
        // 查询作为丈夫的家庭单元
        List<FamilyUnitDO> unitsAsHusband = familyUnitMapper.selectListByHusbandId(id);
        for (FamilyUnitDO unit : unitsAsHusband) {
            if (unit.getWifeId() != null) {
                spouseIds.add(unit.getWifeId());
            }
        }
        // 查询作为妻子的家庭单元
        List<FamilyUnitDO> unitsAsWife = familyUnitMapper.selectListByWifeId(id);
        for (FamilyUnitDO unit : unitsAsWife) {
            if (unit.getHusbandId() != null) {
                spouseIds.add(unit.getHusbandId());
            }
        }
        respVO.setSpouseIds(spouseIds);
        
        // 查询子女关系：找到所有以该成员为父母之一的家庭单元
        List<FamilyUnitDO> allUnits = familyUnitMapper.selectList("family_id", member.getFamilyId());
        List<Long> childrenIds = new ArrayList<>();
        for (FamilyUnitDO unit : allUnits) {
            if ((unit.getHusbandId() != null && unit.getHusbandId().equals(id)) ||
                (unit.getWifeId() != null && unit.getWifeId().equals(id))) {
                // 找到该家庭单元的所有子女
                List<MemberDO> unitChildren = memberMapper.selectListByFamilyUnitId(unit.getId());
                for (MemberDO child : unitChildren) {
                    childrenIds.add(child.getId());
                }
            }
        }
        respVO.setChildrenIds(childrenIds);
        
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
        auditLog.setApplicantId(SecurityFrameworkUtils.getLoginUserId());
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
        auditLog.setApplicantId(SecurityFrameworkUtils.getLoginUserId());
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
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
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

