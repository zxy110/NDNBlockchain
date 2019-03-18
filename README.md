# NDNBlockchain

本项目是搭建在命名数据网络（NDN）上的区块链系统。

1. 项目配置

   需要配置：NFD，jndn-0.20.jar，org.json.jar，bcprov-ext-jdk15on-161.jar, maven

   运行环境：Ubuntu16.04（由于NFD只支持Ubuntu16.04或macOS，因此项目不支持Windows）

2. 本项目实现了：

   - 密码学算法（椭圆曲线Secp256k1算法，哈希算法SHA256/base58/base64）；

   - 生成钱包地址；
   - Merkle树计算；
   - POW共识算法挖矿及校验；
   - LevelDB对区块链持久化；
   - UTXO交易池及校验；
   - 基于NDN的P2P网络通信（参考了Blockndn[https://github.com/LebronJames0423/JinTong]项目）
   - 序列化Block时试验了JsonObject与字节序两种方式

3. 运行：

   通过`nfd-start`打开NFD后，运行 `sys/src/main/java/src/Main.java` 中的main函数。

   - 系统初始化时会将数据库(`sys/db/Blockchain`)中的区块加入当前链，每生成一个区块校验合法后，即存入数据库；
   - 在网络传输中未对交易Transaction进行序列化传输，区块的传输和保存都只用Merkle根来代表；
   - UTXO并未序列化到数据库，后续可能会填坑；
   - 交易还未开放接口，需要自行设置（后续会填坑），目前代码(`sys/src/main/java/UTXO/Transaction.java`)中有两笔交易生成示例(`genesisTransaction`和`generateTransaction`)，可参考。
   - 每个主要函数（Block，BlockChain，Transaction，UTXO等）都有test，供自行测试。

4. 参考

   [1]Blockndn: A bitcoin blockchain decentralized system over named data networking[https://github.com/LebronJames0423/JinTong]

   [2]基于Java语言构建区块链[https://segmentfault.com/a/1190000013923201]

   [3]NDN官网[https://named-data.net/]

   [4]《区块链技术指南》邹均著

   [5]《信息中心网络与命名数据网络》雷凯著

   [6]NFD[http://named-data.net/doc/NFD/current/]
