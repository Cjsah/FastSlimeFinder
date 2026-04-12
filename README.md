# FastSlimeFinder

> [!NOTE]\
> 思路来源于[https://github.com/Nukelawe/slimefinder](https://github.com/Nukelawe/slimefinder) 且已停更
>
> 但是它算的实在是太慢了, 所以我重写了一个

- 半径512格搜索只需0.257s, 加上绘制20张图片的时间, 整体耗时0.445s (i7-11700F)
  - 丸辣, 计算方块数好慢....
  - 让我试试用rust重写一个

## 配置文件

- `mode`: 模式
  - `normal`: 尽可能覆盖多的史莱姆区块, 即使是不完整覆盖
  - `cover`: 只计算完整覆盖的史莱姆区块
- `center`: 中心区块坐标
  - 是区块坐标而不是方块坐标
- `offset`: 方块偏移
  - 玩家挂机点在当前区块的第几格
- `radius`: 搜索半径
  - 单位是区块
- `record`: 记录前几个最多的区块

