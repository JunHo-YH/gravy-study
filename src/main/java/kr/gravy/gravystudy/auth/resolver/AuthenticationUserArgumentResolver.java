package kr.gravy.gravystudy.auth.resolver;

import kr.gravy.gravystudy.auth.entity.User;
import kr.gravy.gravystudy.auth.mapper.UserMapper;
import kr.gravy.gravystudy.common.exception.GravyException;
import kr.gravy.gravystudy.common.exception.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserMapper userMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 1. @AuthenticationPrincipal 어노테이션이 있는지 확인
        boolean hasAnnotation = parameter.hasParameterAnnotation(AuthenticationPrincipal.class);

        // 2. 파라미터 타입이 User인지 확인
        boolean isUserType = User.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isUserType;
    }

    @Override
    public User resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new GravyException(Status.AUTHENTICATION_FAILED);
        }

        // Principal은 UUID (JWTAuthenticationFilter에서 저장한 값)
        UUID userPublicId = (UUID) authentication.getPrincipal();

        // DB에서 User 조회
        return userMapper.getUserByPublicId(userPublicId)
                .orElseThrow(() -> new GravyException(Status.USER_NOT_FOUND));
    }
}
