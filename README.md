*******************************************
# Project : Virtual File Server
*******************************************
VFS is a shared emulator for a file system.

Technology stack: Java17, Spring framework, Spring Boot, protobuf, netty

VFS-client allows you to connect to the server.

Demonstrates how to do implement a project without using Spring Framework.

*******************************************
## Find and terminate running process on Mac
*******************************************
1. Find the process based on the port
```bash
lsof -i tcp:4499
```
The above command should return something like that:
```bash
COMMAND   PID    USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
java    58944 islonik  184u  IPv6 0x4b73f6a4635a157f      0t0  TCP localhost:4499 (LISTEN)
```

2. Kill the process
```bash
kill -9 PID
```

*******************************************
## Release & How to release
*******************************************

Full version : http://2mohitarora.blogspot.ru/2014/02/maven-release-plugin-and-gitflow.html

Short version:
1) 	Create a release branch from develop
execute: git checkout -b release/1.0 develop
2) 	Do maven release
execute: mvn release:prepare 
3) 	Merge Release branch in Develop branch
execute: git checkout develop             
		 git merge --no-ff release/1.0 
4) 	Merge Released code in master (in other word commit 1 of step 2)
execute: git checkout master               
		 git merge --no-ff release/1.0~1 
5) 	Delete local release branch
execute: git branch -D release/1.0
6) 	Push everything to remote (Tag/Master and Develop)
execute: git push --all && git push --tags

*******************************************
## Release & How to make tag
*******************************************
1) git tag -a 'name-version' -m 'message/description'
2) git push origin 'name-version'

*******************************************
## VFS tags evolution / description  
*******************************************
1) Tag: <b>vfs-plain-v1.0</b><br/>
Description: VFS server/client based on java/multi-thread model
2) Tag: <b>vfs-spring-v1.0</b><br/>
Description: VFS server/client uses Spring framework
3) Tag: <b>vfs-nio-v1.0</b><br/>
Description: VFS server/client uses single-thread model
4) Tag: <b>vfs-protobuf-v1.0</b><br/>
Description: VFS server/client sends and receives messages using protobuf framework
5) Tag: <b>vfs-netty-v1.0</b><br/>
Description: VFS server/client opens and closes connections using netty framework
6) Tag: <b>vfs-boot-v1.0 (planning)</b><br/>
Description: VFS server uses Spring Boot

*******************************************
## Junit5 test & maven
*******************************************
To execute successfully junit5 tests you should use at least maven version - 3.9.5 