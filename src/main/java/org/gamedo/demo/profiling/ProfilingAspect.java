package org.gamedo.demo.profiling;

import org.gamedo.demo.logging.MyMarkers;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Log4j2
@Aspect
public class ProfilingAspect {

    @Around("methodsToBeProfiled()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start(pjp.getSignature().getName());
            return pjp.proceed();
        } finally {
            sw.stop();
            log.info(MyMarkers.Profiling, "{}", sw.prettyPrint());
        }
    }

    @Pointcut("execution(* org.gamedo..*.*(..))")
    public void methodsToBeProfiled(){}
}
