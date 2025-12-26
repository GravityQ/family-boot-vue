package cn.iocoder.yudao.module.family.dal.mysql.audit;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.audit.AuditLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 审核记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AuditLogMapper extends BaseMapperX<AuditLogDO> {

    default PageResult<AuditLogDO> selectPageByFamilyIdAndStatus(Long familyId, Integer auditStatus, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AuditLogDO>()
                .eq(AuditLogDO::getFamilyId, familyId)
                .eqIfPresent(AuditLogDO::getAuditStatus, auditStatus)
                .orderByDesc(AuditLogDO::getCreateTime));
    }

    default List<AuditLogDO> selectListByFamilyIdAndStatus(Long familyId, Integer auditStatus) {
        return selectList(new LambdaQueryWrapperX<AuditLogDO>()
                .eq(AuditLogDO::getFamilyId, familyId)
                .eqIfPresent(AuditLogDO::getAuditStatus, auditStatus)
                .orderByDesc(AuditLogDO::getCreateTime));
    }

}

