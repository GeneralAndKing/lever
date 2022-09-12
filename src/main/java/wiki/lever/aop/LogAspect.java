package wiki.lever.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import wiki.lever.entity.SysLog;
import wiki.lever.modal.ErrorResponse;
import wiki.lever.modal.annotation.Log;
import wiki.lever.repository.SysLogRepository;
import wiki.lever.util.JacksonUtil;
import wiki.lever.util.RequestUtil;

import java.util.HashMap;
import java.util.Objects;

/**
 * 2022/09/12 11:38:24
 *
 * @author xy
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final SysLogRepository sysLogRepository;

    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, Log operateLog) throws Throwable {
        RequestAttributes requestAttributes = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = Objects.requireNonNull((HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST));
        SysLog sysLog = buildLog(joinPoint, operateLog, request);
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            sysLog.setResult(JacksonUtil.toJson(new ErrorResponse(e)));
        }
        sysLogRepository.save(sysLog);
        return result;
    }

    /**
     * Build log entity.
     *
     * @param joinPoint  method join point
     * @param operateLog operate log
     * @param request    http request
     * @return log
     */
    private SysLog buildLog(ProceedingJoinPoint joinPoint, Log operateLog, HttpServletRequest request) {
        return new SysLog()
                .setParamContent(getParameter(request, joinPoint))
                .setClassName(joinPoint.getTarget().getClass().getName())
                .setMethodName(joinPoint.getSignature().getName())
                .setHttpMethod(HttpMethod.valueOf(request.getMethod()))
                .setOperateModule(operateLog.operateModule())
                .setOperateType(operateLog.operateType())
                .setOperateName(operateLog.operateName())
                .setOperateIp(RequestUtil.getIpAddr(request));
    }

    /**
     * Get parameter json string form {@link HttpServletRequest} and {@link ProceedingJoinPoint}.
     *
     * @param request   http request
     * @param joinPoint join point
     * @return parameter string
     */
    private String getParameter(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        Object[] requestParams = joinPoint.getArgs();
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] sigParamNames = methodSignature.getParameterNames();
        if (HttpMethod.POST.matches(request.getMethod())) {
            return JacksonUtil.toJson(requestParams[0]);
        }
        HashMap<String, Object> parameters = new HashMap<>(sigParamNames.length);
        for (int i = 0; i < sigParamNames.length; i++) {
            parameters.put(sigParamNames[i], requestParams[i]);
        }
        return JacksonUtil.toJson(parameters);
    }


}

