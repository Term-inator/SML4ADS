# 场景解析算法

## 这是啥？

一个场景解析算法，将场景描述语言（JSON格式）文件和OpenDRIVE地图文件（xodr格式）

## 这有啥？

### 代码文件 src/main/java

- json: ADSML文本的解析
    - tree：行为树对应的Java类
    - importer：JSON文本读取、解析并转化为Java对象
    - exporter：解析后数据写入
    
- xodr：OpenDRIVE地图的解析
    - map：路网结构对应的Java类
    - importer：地图读取、解析并转化为Java对象
    - exporter：解析后数据写入
- Convert：主类，项目启动口

### 资源文件 src/main/resources

- examples：ADSML案例（JSON格式）
- maps：地图案例（xodr格式）
- models：生成的Uppaal SMC模型（XML格式）
- uppaal：Uppaal中已经定义好的数据结构、函数和变量等

## 这咋干？

### 模型转换算法

#### 思想

JSON --> Java Object--> XML

#### 关键

待补充

### 地图解析算法

#### 思想

JSON --> Java Object--> XML

#### 关键

待补充

