gamedo.core结合gamedo.persistence使用示例（由于这两个项目都属于开发初级阶段，因此这个demo更是初级版本）

## 运行环境

* mongoDb 3.6+
* JDK 11

## Web API

* http://localhost:7273/logout/init 初始化数据库，向mongoDB的test库中插入2000个EntityDbPlayer数据
* http://localhost:7273/logout/login 模拟分批登录，每隔100毫秒登录1次，每次最多登录100个玩家，直到登录完2000个角色为止
* http://localhost:7273/logout/switch 切换登录开关（返回值代表当前是否开启中）
* http://localhost:7273/logout/logout 模拟玩家下线

## 指标监控

* 可以通过：http://localhost:7273/actuator/prometheus 查看所有运行时指标，包括IGameLoop线程池、mongodDB连接池、mongoDB命令执行、在线IEntity数量统计。
* 也可以添加Grafana [dashboard模板（14865）](https://grafana.com/grafana/dashboards/14865)，直接查看可视化的指标监控，例如：
![dashboard](https://raw.githubusercontent.com/pcloves/gamedo.core/master/images/dashboard1.png)