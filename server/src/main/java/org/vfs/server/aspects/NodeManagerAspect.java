package org.vfs.server.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.server.services.LockService;

/**
 * @author Lipatov Nikita
 */
@Aspect
@Component
public class NodeManagerAspect {

    private final LockService lockService;

    @Autowired
    public NodeManagerAspect(LockService lockService) {
        this.lockService = lockService;
    }

//    @AfterReturning(pointcut="@annotation(org.vfs.server.aspects.CreateNode)", returning="node")
//    public void newNode(Node node) {
//        lockService.addNode(node);
//    }
//
//    @After("@annotation(org.vfs.server.aspects.RemoveNode)")
//    public void removeNode(Node source, Node child) {
//        lockService.removeNode(child);
//    }

}