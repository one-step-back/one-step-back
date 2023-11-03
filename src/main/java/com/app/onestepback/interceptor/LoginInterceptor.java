package com.app.onestepback.interceptor;

import com.app.onestepback.domain.MemberVO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberVO member = (MemberVO) request.getSession().getAttribute("member");

        if (member == null){
            response.sendRedirect("/member/login");
            return false;
        }

        return true;
    }
}
