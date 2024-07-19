package com.cym.behavior.interceptor;

import com.cym.model.user.pojos.ApUser;
import com.cym.utils.thread.ApUserThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppUserTokenInterceptor implements HandlerInterceptor {

    /**
     * 将userId写入线程中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        System.err.println(userId);
        if (userId !=null){
            // 存入当前线程中，在请求处理之前进行调用（Controller方法调用之前）
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            ApUserThreadLocalUtil.setUser(apUser);
        }
        return true;
    }


    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 清理线程中的userId，在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ApUserThreadLocalUtil.clear();
    }
}
