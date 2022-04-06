package com.hanghae99.boilerplate.chat.aop;


import com.hanghae99.boilerplate.chat.trace.TraceStatus;
import com.hanghae99.boilerplate.chat.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TraceAspect {

    private final LogTrace trace;

    @Around("@annotation(com.hanghae99.boilerplate.chat.annotation.TimeTrace)")
    public Object trace1(ProceedingJoinPoint joinPoint) throws Throwable{
        TraceStatus status = null;
        try {
            status = trace.begin(joinPoint.toShortString());

            // 로직 호출
            Object result = joinPoint.proceed();
            trace.end(status);
            return result;

        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }


}
