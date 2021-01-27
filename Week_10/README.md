学习笔记
day1
# 1.rpcfx1.1: 给自定义RPC实现简单的分组(group)和版本(version)。
## 1.curator使用
### 简介
Curator是Netflix公司开源的一套Zookeeper客户端框架。现在已经是apache基金会的产品。了解过Zookeeper原生API都会清楚其复杂度。<br>
Curator帮助我们在其基础上进行封装、实现一些开发细节，包括接连重连、反复注册Watcher和NodeExistsException等。<br>
目前已经作为Apache的顶级项目出现，是最流行的Zookeeper客户端之一。<br>
### 版本信息
目前Curator有2.x.x和3.x.x两个系列的版本，支持不同版本的Zookeeper。<br>
其中Curator 2.x.x兼容Zookeeper的3.4.x和3.5.x。(本项目使用版本，zookeeper version=3.6)而Curator 3.x.x只兼容Zookeeper 3.5.x，
