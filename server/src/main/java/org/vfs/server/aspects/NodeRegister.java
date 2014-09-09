package org.vfs.server.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.vfs.server.model.NodeTypes;

/**
 * @author Lipatov Nikita
 */
@Aspect
public class NodeRegister {

    @After("execution(* org.vfs.server.services.NodeService.newNode(..))")
    public void advice1(JoinPoint joinPoint) {
        System.err.println("We are here!" + joinPoint.getKind() + "  " + joinPoint.getArgs()[0] + "  " + joinPoint.getArgs()[1]);
    }

}

