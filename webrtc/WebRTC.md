#### webrtc module 说明
1，jniLibs中的libjingle_peerconnection_so.so是使用webrtc android源码编译好的so文件。
2，java代码来自webrtc android源码目录中的java相关源码copy出来的。

#### version 获取规则
1，进入下载的webrtc android源码目录的src路径（~/webrtc/src/）。
2，执行git命令 “git log -1 | grep 'Cr-Commit-Position: refs/heads/master@{#' | egrep -o "[0-9]+}" | tr -d '}'”，得到的即是version名称。