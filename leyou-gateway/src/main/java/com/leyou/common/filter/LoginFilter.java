package com.leyou.common.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginFilter extends ZuulFilter {

    @Resource()
    private JwtProperties jwtProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        //获取当前请求uri
        String uri = context.getRequest().getRequestURI();
        for (String s : this.jwtProperties.getPath()) {
            //如果uri路径包含白名单路径，就返回false，代表不执行run方法
            boolean bool = StringUtils.contains(uri, s);
            if (bool) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        //获取请求cookie中的token
        String token = CookieUtils.getCookieValue(context.getRequest(), this.jwtProperties.getCookieName());
        try {
            //解析token
            JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //解析不通过就不转发，并且设置响应状态码为403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
