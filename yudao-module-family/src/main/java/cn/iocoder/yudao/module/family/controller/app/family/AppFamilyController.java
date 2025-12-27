package cn.iocoder.yudao.module.family.controller.app.family;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyCreateReqVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyRespVO;
import cn.iocoder.yudao.module.family.controller.app.family.vo.FamilyUpdateReqVO;
import cn.iocoder.yudao.module.family.service.family.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 用户 App - 家族 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "用户 App - 家族")
@RestController
@RequestMapping("/family/v1")
@Validated
public class AppFamilyController {

    @Resource
    private FamilyService familyService;

    @PostMapping("/create")
    @Operation(summary = "创建家族申请")
    public CommonResult<Long> createFamily(@Valid @RequestBody FamilyCreateReqVO createReqVO) {
        return success(familyService.createFamily(createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取家族详情")
    @Parameter(name = "id", description = "家族编号", required = true, example = "1")
    public CommonResult<FamilyRespVO> getFamily(@RequestParam("id") Long id) {
        return success(familyService.getFamily(id));
    }

    @PutMapping("/update")
    @Operation(summary = "更新家族设置")
    public CommonResult<Boolean> updateFamily(@Valid @RequestBody FamilyUpdateReqVO updateReqVO) {
        familyService.updateFamily(updateReqVO);
        return success(true);
    }

}

