package com.yunjae.dynamicrouter.controller.manager;

import com.yunjae.dynamicrouter.Application;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManagementController {

    @PostMapping("/restart")
    public void restart() {
        Application.restart();
    }
}
