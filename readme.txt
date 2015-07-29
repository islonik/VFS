*******************************************
*** Project : Virtual File Server
*******************************************


*******************************************
*** Release & How to release
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
*** Release & How to make tag
*******************************************
1) git tag -a 'name-version' -m 'message/description'
2) git push origin 'name-version'

*******************************************
*** VFS tags evolution / description  
*******************************************
1) vfs-plain-v1.0
VFS server/client based on java/multi-thread model
2) vfs-spring-v1.0
VFS server/client uses Spring framework
3) vfs-nio-v1.0
VFS server/client uses single-thread model
4) vfs-protobuf-v1.0
VFS server/client sends and receives messages using protobuf framework
5) vfs-netty-v1.0
VFS server/client opens and closes connections using netty framework
