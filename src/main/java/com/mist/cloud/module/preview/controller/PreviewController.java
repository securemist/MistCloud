package com.mist.cloud.module.preview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: securemist
 * @Datetime: 2023/9/17 20:49
 * @Description:
 */
@Controller
public class PreviewController {
    @GetMapping("/preview")
    public void preview(@RequestParam Long id) {

    }
}
