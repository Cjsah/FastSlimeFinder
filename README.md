# FastSlimeFinder

> [!NOTE]\
> 思路来源于[https://github.com/Nukelawe/slimefinder](https://github.com/Nukelawe/slimefinder) 且已停更
>
> 但是它算的实在是太慢了, 所以我重写了一个

- 半径1024区块搜索只需1.145s, 加上绘制20张图片的时间, 整体耗时1.338s (i7-11700F)

## 配置文件

- `center`: 中心区块坐标
  - 是区块坐标而不是方块坐标
- `offset`: 方块偏移
  - 玩家挂机点在当前区块的第几格
- `radius`: 搜索半径
  - 单位是区块
- `record`: 记录前几个最多的区块

