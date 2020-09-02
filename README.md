暂时不做复杂

无注册中心，producer和consumer直连broker进行通信

无复杂操作，只有sendMessage和pullMessage

broker端不做持久化，使用LinkedBlockQueue缓存

不提供消息Id，避免复杂操作

---

协议部分仿照rocket

- 消息总长度（使用netty时会用到，验证消息接收的正确性，防止部分接收等问题）
- 序列化类型&消息长度（序列化方式固定为json，类型表示为0）
- 消息头数据（消息头部分暂不做复杂的区分，自定义部分暂时固定为topic和queueId，扩展部分暂时不用。）
- 消息体数据（二进制字节数组）



