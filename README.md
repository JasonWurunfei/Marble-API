# Marble

> Impression over Translation

Version 0.1 

## 介绍

Marble 是一款专注于帮助主动的语言学习者以一点一滴的日常积累实现双语甚至多语能力的软件产品。Marble 1.0 能够让语言学习者方便的将身边的事物、人的感情、生活琐事快速地与其想学习的语言联系到一起。Marble 的哲学是 “Impression over Translation”,“印象胜过翻译“。 Marble 的单词本是多媒体的。Marble 的用户可以将亲身经历的、亲眼看到的事物通过录像、拍照等方式记录下来。将这些记录与Marble用户想学的语言的单词或句子联系到一起从而让学习者让这些单词或句子与其经历产生紧密的联系从而实现高效的学习的目的。



Marble 名字的灵感来自于：

- Marble 形似Markable 学习者周围的一切都是可记录的、可学习的

- 每一次积累都像是一颗弹珠收入囊中



## API 介绍

### 用户

1. 获取用户数据 GET api/user/{user_id}
2. 用户登录 GET api/user/login?username={username}

### Marble

1. 获取特定Marble的数据 GET api/marble/{marble_id}
2. 获取特定用户的所有Marble GET api/marble/user/{user_id}
3. 创建新的Marble POST api/marble
4. 修改编辑特定Marble的数据 PUT api/marble/{marble_id}
5. 删除Marble的数据 DELETE api/marble/{marble_id}
6. 批量删除Marble DELETE api/marble/batchremove/

### Bag

1. 获取特定用户的所有Bag的数据 GET api/bag/user/{user_id} 
2. 获取特定Bag的所有Marble api/bag/marble/{bag_id}
3. 创建新的Bag POST api/bag
4. 修改编辑特定Bag的数据 PUT api/bag/{bag_id}
5. 删除Bag的数据 DELETE api/bag/{bag_id}
6. 批量删除Bag DELETE api/bag/batchremove/

### Impression

> type:
>
> 1 视频
>
> 2 图片
>
> 3 音频

1. 获取特定Marble的所有Impression GET api/impression/marble/{marble_id}
2. 创建新的Impression POST api/impression
3. 删除Impression 的数据 DELETE api/impression/{impression_id}
4. 批量删除Impression DELETE api/impression/batchremove/