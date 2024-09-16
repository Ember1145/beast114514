package com.lts.intercepters;

import com.lts.Properties.JwtProperties;
import com.lts.constant.JwtClaimsConstant;
import com.lts.context.userContext;
import com.lts.utils.JwtSecretKeyUtil;
import com.lts.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        //1、从请求头中获取令牌
            // 现在您可以使用token进行验证
        //2、校验令牌
        try {
            String authHeader = request.getHeader("Authorization");
            log.info(authHeader);
            String token = authHeader.substring(7); // 去掉前面的"Bearer "
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(JwtSecretKeyUtil.generateSecretKey(jwtProperties.getAdminSecretKey(), "salt".getBytes(), 65536, 256), token);
            Long Id = Long.valueOf(claims.get(JwtClaimsConstant.USERID).toString());
            log.info("user：{}", Id);
            userContext.setUserId(Id);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
