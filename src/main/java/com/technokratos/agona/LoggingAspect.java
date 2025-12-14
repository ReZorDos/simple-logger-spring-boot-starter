package com.technokratos.agona;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(value = "custom.logging.enabled", havingValue = "true")
@RequiredArgsConstructor
public class LoggingAspect {

    private final LoggingProperties properties;
    private final Logger myLogger;

    @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public void allControllersPointcut() {}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void servicePointcut() {}

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryPointcut() {}

    @Around("allControllersPointcut()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }
        Class<?> currentClass = joinPoint.getTarget().getClass();
        String className = currentClass.getName();
        Object[] args = joinPoint.getArgs();
        String argsString = safeFormatArgs(args);

        myLogger.info("CONTROLLER [{}] : REQUEST ARGS: [{}]", className, argsString);
        try {
            Object result = joinPoint.proceed();
            String responseResult = safeFormatResult(result);
            myLogger.info("CONTROLLER [{}] : RESPONSE ARGS: [{}]", className, responseResult);
            return result;
        } catch (Exception e) {
            myLogger.error("CONTROLLER [{}] : EXCEPTION: [{}]", className, e.getMessage());
            throw e;
        }
    }

    @Around("servicePointcut()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }
        Class<?> currentClass = joinPoint.getTarget().getClass();
        String className = currentClass.getName();
        Object[] args = joinPoint.getArgs();
        String argsString = safeFormatArgs(args);

        myLogger.debug("SERVICE [{}] : REQUEST ARGS: [{}]", className, argsString);
        try {
            Object result = joinPoint.proceed();
            String responseResult = safeFormatResult(result);
            myLogger.debug("SERVICE [{}] : RESPONSE ARGS: [{}]", className, responseResult);
            return result;
        } catch (Exception e) {
            myLogger.error("SERVICE [{}] : EXCEPTION: [{}]", className, e.getMessage());
            throw e;
        }
    }

    @Around("repositoryPointcut()")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }
        Class<?> currentClass = joinPoint.getTarget().getClass();
        String className = currentClass.getName();
        Object[] args = joinPoint.getArgs();
        String argsString = safeFormatArgs(args);

        myLogger.debug("REPOSITORY [{}] : REQUEST ARGS: [{}]", className, argsString);
        try {
            Object result = joinPoint.proceed();
            String responseResult = safeFormatResult(result);
            myLogger.debug("REPOSITORY [{}] : RESPONSE ARGS: [{}]", className, responseResult);
            return result;
        } catch (Exception e) {
            myLogger.error("REPOSITORY [{}] : EXCEPTION: [{}]", className, e.getMessage());
            throw e;
        }
    }

    private String safeFormatArgs(Object[] args) {
        if (args == null) {
            return "null";
        }
        try {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                Object arg = args[i];
                if (arg == null) {
                    sb.append("null");
                } else if (arg instanceof String str) {
                    if (str.length() > 100) {
                        sb.append(str, 0, 100).append("...");
                    } else {
                        sb.append("\"").append(str).append("\"");
                    }
                } else {
                    String str = arg.toString();
                    if (str.length() > 100) {
                        sb.append(str, 0, 100).append("...");
                    } else {
                        sb.append(str);
                    }
                }
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            myLogger.error("EXCEPTION: [{}]", e.getMessage());
            throw e;
        }
    }

    private String safeFormatResult(Object result) {
        if (result == null) {
            return "null";
        }
        try {
            String resultStr = result.toString();
            if (resultStr.length() <= 300) {
                return resultStr;
            }
            int safeLength = Math.min(resultStr.length(), 300);
            return resultStr.substring(0, safeLength);
        } catch (Exception e) {
            myLogger.error("EXCEPTION: [{}]", e.getMessage());
            throw e;
        }
    }

}