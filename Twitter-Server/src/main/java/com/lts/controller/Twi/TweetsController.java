package com.lts.controller.Twi;


import com.lts.domain.dto.twiDTO;
import com.lts.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author author
 * @since 2024-04-07
 */
@RestController
@RequestMapping("/tweets")
public class TweetsController {
    @PostMapping("/submit")
    public Result submitForm(@RequestBody twiDTO twiDTO) {

    }

}
