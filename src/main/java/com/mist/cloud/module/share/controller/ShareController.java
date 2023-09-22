package com.mist.cloud.module.share.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mist.cloud.core.constant.ResponseCode;
import com.mist.cloud.core.exception.ShareException;
import com.mist.cloud.core.exception.ShareInvalidException;
import com.mist.cloud.core.result.R;
import com.mist.cloud.module.file.context.FileServiceContext;
import com.mist.cloud.module.file.model.pojo.FolderDetail;
import com.mist.cloud.module.share.model.ShareFileInfo;
import com.mist.cloud.module.share.model.ShareItem;
import com.mist.cloud.module.share.model.req.CreateShareRequest;
import com.mist.cloud.module.share.model.req.ResaveFileRequest;
import com.mist.cloud.module.share.model.resp.ShareLinkResponse;
import com.mist.cloud.module.share.service.IShareContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: securemist
 * @Datetime: 2023/9/18 08:02
 * @Description:
 */
@RestController
@RequestMapping("/share")
public class ShareController {
    @Resource
    private IShareContext shareContext;

    @PostMapping("/create")
    public R createShare(@RequestBody CreateShareRequest createShareRequest) {
        ShareLinkResponse share = shareContext.createShare(createShareRequest);
        return R.success(share);
    }

    @GetMapping("/extract")
    public R listFile(@RequestParam String uniqueKey, @RequestParam String code) {
        try {
            ShareFileInfo shareInfo = shareContext.extractFile(code, uniqueKey);
            return R.success(shareInfo);
        } catch (ShareInvalidException e) {
            return R.error(ResponseCode.SHARE_FAILED);
        }
    }

    @GetMapping("/list")
    public R listShare() {
        List<ShareItem> list = shareContext.listShares();
        return R.success(list);
    }

    @GetMapping("/delete")
    public R deleteShare(@RequestParam String uniqueKey) {
        shareContext.deleteShare(uniqueKey);
        return R.success();
    }

    @PostMapping("/resave")
    public R resave(@RequestBody ResaveFileRequest resaveFileRequest) {
        if (resaveFileRequest.getIdList().size() == 0) {
            return R.error();
        }

        shareContext.resave(resaveFileRequest.getIdList(), resaveFileRequest.getTargetFolderId());
        return R.success();
    }

}
