# NDNBlockchain

本项目是搭建在命名数据网络（NDN）上的区块链系统。

1. 项目配置

   需要配置：

   - NFD：NDN守护进程，作为服务器使用
   - maven：本项目为maven项目，使用前需要配置相应pox.xml文件
        - libs/jndn-0.20.jar：NDN的Java包，用于实现NDN网络通信部分代码
        - libs/org.json.jar：封装好的Json包，本项目用于数据库数据的序列化（区块与Json转化）
        - libs/bcprov-ext-jdk15on-161.jar：用于椭圆曲线算法ECC
        - libs/jpbc-api.jar，libs/jpbc-plaf.jar：实现双线性映射所需的库
        - libs/stdlib.jar：IO库，其中的Out类可用于将双线性对存储到a.properties中

   运行环境：Ubuntu16.04（由于NFD只支持Ubuntu16.04或macOS，因此项目不支持Windows）

2. 本项目实现了：

   - 密码学算法（椭圆曲线Secp256k1算法，哈希算法SHA256/base58/base64，双线性映射）；
   - 生成钱包地址；
   - Merkle树计算；
   - POW共识算法挖矿及校验；
   - LevelDB对区块链持久化；
   - UTXO交易池及校验；
   - 基于NDN的P2P网络通信（参考了[Blockndn](https://github.com/LebronJames0423/JinTong)项目）；
   - 序列化Block时试验了JsonObject与字节序两种方式；
   - 基于双线性映射的VRF与多重代理门限签名算法Mptlbp的挖矿及校验

3. 运行：

   （1）参数设置：在 `sys/src/main/java/sys/Configure.java`中,

   - `Consensus`：设置共识算法
   - `TARGET_BITS`：设置难度值
   - `DELEGATES`：设置代理节点门限值

   （2）通过`nfd-start`打开NFD后，运行 `sys/src/main/java/sys/Main.java` 中的main函数。

   - 系统初始化时会将数据库(`sys/db/Blockchain`)中的区块加入当前链，每生成一个区块校验合法后，即存入数据库；
   - 在网络传输中未对交易Transaction进行序列化传输，区块的传输和保存都只用Merkle根来代表；
   - 交易还未开放接口，需要自行设置（后续会填坑），目前代码(`sys/src/main/java/utxo/Transaction.java`)中有两笔交易生成示例(`genesisTransaction`和`generateTransaction`)，可参考。
   - 每个主要函数（Block，BlockChain，Transaction，UTXO等）都有test，供自行测试。

4. 参考

   [1].[Blockndn: A bitcoin blockchain decentralized system over named data networking](https://github.com/LebronJames0423/JinTong)

   [2].[NDN官网](https://named-data.net/)

   [3]《区块链技术指南》邹均著

   [4]《信息中心网络与命名数据网络》雷凯著

   [5].[NFD](http://named-data.net/doc/NFD/current/)
