package org.vfs.server.aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.stereotype.Component;
import org.vfs.server.model.NodeTypes;
import java.lang.reflect.Method;

/**
 * @author Lipatov Nikita
 */
@Aspect
@Component
public class NodeRegisterAspect {
    @After("@annotation(NodeModifier)")
    public void afterReturning(JoinPoint joinPoint) {
        System.err.println("We are here! " + joinPoint.getSignature().getName() + "  " + joinPoint.getArgs()[0] + "  " + joinPoint.getArgs()[1]);
    }
}