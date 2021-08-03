gamedo.core结合gamedo.persistence使用示例（由于这两个项目都属于开发初级阶段，因此这个demo更是初级版本）



## 运行环境

* mongoDb 3.6
* JDK 11

## Web API

* http://localhost:8080/logout/init 初始化数据库，向mongodDB的test库中插入10000个EntityDbPlayer数据
* http://localhost:8080/logout/login 模拟分批登录，每隔100毫秒登录1次，每次最多登录100个玩家，直到登录完10000个角色为止
* http://localhost:8080/logout/switch 切换登录开关
* http://localhost:8080/logout/logout 模拟玩家下线

