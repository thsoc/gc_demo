
```bash
top
# 1. 找到吃 CPU 的线程 ID（十进制）
top -Hp <pid>          # 按 P 排序，记下最靠前的几个 TID
# 备选：ps -T -p <pid> -o spid,pcpu,comm | sort -k2 -nr | head

# 2. 转成 16 进制（jstack 里的 nid 是 16 进制）
printf "%x\n" <tid>    # 例如 12345 -> 3039

# 3. 抓线程栈，搜这个 nid
jstack <pid> > /tmp/dump.txt
grep -A 30 "nid=0x3039" /tmp/dump.txt
# JDK 11+ 也可用：jcmd <pid> Thread.print

```
