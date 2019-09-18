## RocketMQ Proxy 
#### Http Interface docs
##### send single message
> ##### Header

| 字段名| 值 | 必填 | 描述 |
| --- | --- | --- | --- |
| Code | 101 | 是 | 单发消息指令
| Language | string | JAVA/C/CPP/PYTHON | 默认JAVA |
| Version | string | 默认 1.0 | 不填则客户端默认1.0 |
| Env | string | 否 | 环境编号 |
| Region | string| 否 | 域 |
| Idc | string | 是	| |
| Dcn | string | 是 ||
| Sys | string | 是 ||
| Pid | string | 是	||
| Username | string | 否	|未来鉴权 备用 |
| Passwd | string | 否 | 未来鉴权 备用|

>##### Body

| 字段名| 值 | 必填 | 描述 |
| --- | --- | --- | --- |
| topic | string | 是 | 目标Topic
| messageType | int | 1，2，3 | 发送形式 |
| bizSeqNo | sting | 是 | 业务流水号 |
| extFields | | 否 | 拓展字段 |
| Content | | 是 | 消息体内容 |
| Ttl |  | 否| 消息时效 |
| Dcn | string | 是 ||

##### send batch message 
> ##### Header

