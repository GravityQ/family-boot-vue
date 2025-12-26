package cn.iocoder.yudao.module.family.service.member;

import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberDetailRespVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberTreeRespVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.member.vo.MemberApplyUpdateReqVO;
import cn.iocoder.yudao.module.family.controller.admin.member.vo.MemberCreateReqVO;

import java.util.List;

/**
 * 成员 Service 接口
 *
 * @author 芋道源码
 */
public interface MemberService {

    /**
     * 获取家族树谱（扁平结构）
     *
     * @param familyId 家族编号
     * @return 树谱数据
     */
    MemberTreeRespVO getMemberTree(Long familyId);

    /**
     * 获取成员详情
     *
     * @param id 成员编号
     * @return 成员详情
     */
    MemberDetailRespVO getMember(Long id);

    /**
     * 申请新增成员
     *
     * @param createReqVO 创建信息
     * @return 审核记录编号
     */
    Long applyCreateMember(MemberApplyCreateReqVO createReqVO);

    /**
     * 申请修改成员
     *
     * @param updateReqVO 修改信息
     * @return 审核记录编号
     */
    Long applyUpdateMember(MemberApplyUpdateReqVO updateReqVO);

    /**
     * 管理员免审新增成员
     *
     * @param createReqVO 创建信息
     * @return 成员编号
     */
    Long createMember(MemberCreateReqVO createReqVO);

    /**
     * 删除成员
     *
     * @param id 成员编号
     */
    void deleteMember(Long id);

}

