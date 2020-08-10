package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import com.changgou.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: UserFeign
 * 作者: crh
 * 日期: 2020/6/13 0013下午 5:05
 */
@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {
    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping("/load/{id}")
    Result<User> findById(@PathVariable("id") String id);

    /**
     * 增加用户积分
     * @param points
     * @return
     */
    @GetMapping(value = "/points/add")
    Result addPoints(@RequestParam(value = "points")Integer points);
}
