package org.vfs.server.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vfs.server.model.Node;
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

    @AfterReturning(pointcut="@annotation(NewNodeModifier)", returning="node")
    public void newNode(Node node) {
        lockService.addNode(node);
        System.err.println("New node was captured! " + node.getName());
    }

    @After("@annotation(RemoveNodeModifier) && args(source, child)")
    public void removeNode(Node source, Node child) {
        lockService.removeLock(source, child);
        System.err.println("Remove node was captured! " + source.getName()  + " child = " + child.getName());
    }

}