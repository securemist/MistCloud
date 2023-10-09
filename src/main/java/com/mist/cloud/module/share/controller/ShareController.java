package com.mist.cloud.module.share.controller;

import com.mist.cloud.core.constant.ResponseCode;
import com.mist.cloud.core.exception.ShareInvalidException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.share.model.ShareFileInfo;
import com.mist.cloud.module.share.model.ShareItem;
import com.mist.cloud.module.share.model.req.CreateShareRequest;
import com.mist.cloud.module.share.model.req.ResaveFileRequest;
import com.mist.cloud.module.share.model.resp.ShareLinkResponse;
import com.mist.cloud.module.share.service.IShareContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 08:02
 * @Description:
 */
@RestController
@RequestMapping("/share")
@Tag(name = "文件分享")
public class ShareController {
    @Resource
    private IShareContext shareContext;

    @PostMapping("/create")
    @Operation(summary = "创建分享")
    @Parameter(name = "createShareRequest", description = "创建分享请求")
    public R createShare(@RequestBody CreateShareRequest createShareRequest) {
        ShareLinkResponse share = shareContext.createShare(createShareRequest);
        return R.success(share);
    }

    @GetMapping("/extract")
    @Operation(summary = "提取文件")
    @Parameters({
            @Parameter(name = "uniqueKey", description = "链接标识"),
            @Parameter(name = "code", description = "提取码")})
    public R listFile(@RequestParam String uniqueKey, @RequestParam String code) {
        try {
            ShareFileInfo shareInfo = shareContext.extractFile(code, uniqueKey);
            return R.success(shareInfo);
        } catch (ShareInvalidException e) {
            return R.error(ResponseCode.SHARE_FAILED);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "列出用户所有的分享")
    public R listShare() {
        List<ShareItem> list = shareContext.listShares();
        return R.success(list);
    }

    @GetMapping("/delete")
    @Operation(summary = "删除分享")
    @Parameter(name = "uniqueKey", description = "链接唯一标识")
    public R deleteShare(@RequestParam String uniqueKey) {
        shareContext.deleteShare(uniqueKey);
        return R.success();
    }

    @PostMapping("/resave")
    @Operation(summary = "转存文件")
    @Parameter(name = "resaveFileRequest")
    public R resave(@RequestBody ResaveFileRequest resaveFileRequest) {
        if (resaveFileRequest.getIdList().size() == 0) {
            return R.error();
        }

        shareContext.resave(resaveFileRequest.getIdList(), resaveFileRequest.getTargetFolderId());
        return R.success();
    }

}
