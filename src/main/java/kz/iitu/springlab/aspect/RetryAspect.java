package kz.iitu.springlab.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryAspect {
    private static final Logger log = LoggerFactory.getLogger(RetryAspect.class);

    @Around("@annotation(retryOnFailure)")
    public Object retry(ProceedingJoinPoint pjp, RetryOnFailure retryOnFailure) throws Throwable {
        int maxAttempts = retryOnFailure.attempts();
        Throwable lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (attempt > 1) {
                    log.warn("[RETRY] try {} out of {} for method {}",
                            attempt, maxAttempts, pjp.getSignature().toShortString());
                }
                return pjp.proceed();
            } catch (Throwable ex) {
                lastException = ex;
                log.error("[RETRY] error while try {}: {}", attempt, ex.getMessage());
            }
        }
        throw lastException;
    }
}