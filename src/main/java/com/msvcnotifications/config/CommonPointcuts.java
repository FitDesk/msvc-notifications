package com.msvcnotifications.config;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommonPointcuts {
    @Pointcut("execution(* com.msvcnotifications.services.*.*(..))")
    public void greetingLoggerServices(){};

    @Pointcut("execution(* com.msvcnotifications.controllers.*.*(..))")
    public void greetingLoggerControllers(){};
}
