package cn.iocoder.yudao.module.family.service.family;

import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyRespVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.admin.family.vo.FamilyAuditReqVO;

/**
 * 家族 Service 接口
 *
 * @author 芋道源码
 */
public interface FamilyService {

    /**
     * 创建家族申请
     *
     * @param createReqVO 创建信息
     * @return 家族编号
     */
    Long createFamily(FamilyCreateReqVO createReqVO);

    /**
     * 获取家族详情
     *
     * @param id 家族编号
     * @return 家族详情
     */
    FamilyRespVO getFamily(Long id);

    /**
     * 更新家族设置（仅家族管理员）
     *
     * @param updateReqVO 更新信息
     */
    void updateFamily(FamilyUpdateReqVO updateReqVO);

    /**
     * 系统管理员审核家族创建申请 - 通过
     *
     * @param auditReqVO 审核信息
     */
    void approveFamilyCreate(FamilyAuditReqVO auditReqVO);

    /**
     * 系统管理员审核家族创建申请 - 驳回
     *
     * @param auditReqVO 审核信息
     */
    void rejectFamilyCreate(FamilyAuditReqVO auditReqVO);

}

