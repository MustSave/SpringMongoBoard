package spring.mongo.board.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class QueryAspect {
    @Around("execution(* spring.mongo.board.repository.*.*(..))")
    public Object measureQueryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        String methodName = joinPoint.getSignature().getName();
        log.info("Query execution time of {}: {}ms", methodName, executionTime);

        return result;
    }
}
