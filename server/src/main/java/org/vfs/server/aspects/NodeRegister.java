package org.vfs.server.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.stereotype.Component;
import org.vfs.server.model.NodeTypes;
import java.lang.reflect.Method;

/**
 * @author Lipatov Nikita
 */
@Component
public class NodeRegister implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        System.err.println("We are here! " + method.getName() + "  " + args[0] + "  " + args[1]);
    }

}

