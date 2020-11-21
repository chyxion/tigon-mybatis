# Tigon MyBatis

## 简介

Tigon MyBatis为Spring工程中MyBatis的Mapper提供增强，主要有以下特点

- Spring和MyBatis的前提下，仅依赖`org.slf4j:slf4j-api`
- 代码又少又壮，绝不做多余的事情
- 仅需Mapper继承接口，实现`增` `删` `改` `查`，无额外配置，爽到没女朋友
- 用完即走，毫不留恋

### 开始使用

- 引入Maven依赖

```xml
<dependency>
  <groupId>me.chyxion.tigon</groupId>
  <artifactId>tigon-mybatis</artifactId>
  <version>0.0.1</version>
</dependency>
```

- SpringBoot项目，无需其他操作
- 一般的Spring项目，手动注册Bean *me.chyxion.tigon.mybatis.TigonMyBatisConfiguration*
- 业务Mapper继承*me.chyxion.tigon.mybatis.BaseMapper*或相关衍生Mapper，*Base(Query, Insert, Update, Delete)Mapper*

### 使用示例

下面是使用示例，可以在源代码中找到更详细的单元测试。**Talk is cheep，read the fine source code.**

##### 定义Entity

```java
package me.chyxion.tigon.mybatis.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import lombok.ToString;
import java.io.Serializable;
import me.chyxion.tigon.mybatis.Table;
import me.chyxion.tigon.mybatis.NotUpdate;

@Getter
@Setter
@ToString
@Table("tb_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotUpdate
    private String account;
    private String mobile;
    private String name;
    private Gender gender;
    private String password;
    private Date birthDate;
    private String city;
    private String avatar;

    private Boolean active;
    private String remark;
    private String createdBy;
    private Date createdAt;
    private String updatedBy;
    private Date updatedAt;

    public enum Gender {
        MALE,
        FEMALE
    }
}
```

##### 定义Mapper

```java
package me.chyxion.tigon.mybatis.mapper;

import java.util.List;
import me.chyxion.tigon.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import me.chyxion.tigon.mybatis.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<Integer, User> {
}
```

##### 注入Mapper对象

```java
@Autowired
private UserMapper mapper;
```

##### I. 插入

```java
final User user = new User();

user.setName("Donghuang");
user.setAccount("donghuang");
user.setMobile("137647788xx");
user.setPassword(RandomStringUtils.randomAlphanumeric(16));
user.setGender(User.Gender.MALE);
user.setBirthDate(DateUtils.parseDate("1994-04-04"));
user.setCity("Shanghai");
user.setActive(true);
user.setRemark("Uncle Donghuang");
user.setCreatedBy("donghuang");
user.setCreatedAt(new Date());

mapper.insert(user);
```

##### II. 查询

根据`ID`查询单个对象

```java
final Integer id = 1154;
final User user = mapper.find(id);
```

根据属性查询单个对象

```java
final User user = mapper.find(
    new Search("account", "donghuang")
        .eq("mobile", "137647788xx"));
```

根据属性查询列表

```java
final List<User> users = mapper.list(new Search()
    .between("birth_date",
        DateUtils.parseDate("1982-04-04"),
        DateUtils.parseDate("1994-04-04")
    )
    .eq("gender", User.Gender.MALE)
    .asc("birth_date")
    .limit(42));
```

`Search`对象支持的`API`

- `and` And another `Search`
- `asc` Order ASC
- `between` Between two values
- `build` Build query criterion
- `contains` Value contains string
- `desc` Order DSC
- `endsWith` Value ends with string
- `eq` Eqauls
- `gt` Greater than
- `gte` Eqauls or greater than
- `in` In values
- `isNull` Value is null
- `like` Value like
- `limit` Return rows limit
- `lt` Less than
- `lte` Eqauls or less than
- `ne` Not equals
- `notIn` Not in values
- `notNull` Value is not null
- `offset` Return rows offset
- `or` Or another `Search`
- `orderBy` Order by
- `startsWith` Value starts with string

##### III. 更新

通过`Entity`根据`ID`更新

```java
final User user = mapper.find(1);

user.setName("东皇大叔");
user.setUpdatedBy("SYS");
user.setUpdatedAt(new Date());

mapper.update(user);
```

通过`Map<String, Object>`更新

```java
final Map<String, Object> update = new HashMap<>(6);
update.put("name", "东皇大叔");
update.put("updatedBy", "SYS");
update.put("updatedAt", new Date());

mapper.update(update, 1);
// OR
// mapper.update(update, new Search("id", 1));
// mapper.update(update, new Search(1));
```

更新列为`NULL`

```java
// Update remark to NULL of id 274229
mapper.setNull("remark", 274229);
// Update remark to NULL of id 1154L
mapper.setNull("remark", new Search("id", 1154));
// Update all remarks to NULL. BE CAREFUL!!!
mapper.setNull("remark", new Search());
```

##### IV. 删除

通过`ID`删除数据

```java
mapper.delete(1);
```

通过`Search`对象删除数据

```java
mapper.delete(new Search("id", 1));
```

### 原理

Tigon MyBatis并**不改变**MyBatis相关功能，所做的只是在程序**启动期间**检测业务Mapper接口，如果继承了相关`BaseMapper.java`，则注入相关方法`MappedStatement`，具体逻辑参见源码，超简单，超幼稚。

### 其他

在前面使用`Search`的例子中，我们需要一些`User`的属性常量字符串，比如

```java
final User user = mapper.find(new Search("account", "donghuang"));
```

可以将这些常量定义在`User`类中，如

```java
public static final String ACCOUNT = "account";
```

使用过程中可以使用属性常量，如

```java
final User user = mapper.find(new Search(User.ACCOUNT, "donghuang"));
```

也可以使用`Lombok`的`@FieldNameConstants`注解生成，只是这个注解还处于试验阶段，有一定不稳定风险。

### 最后

为什么要有这个项目，其实这些代码本人从2014年就陆续在写在用，在自己参与的一些项目里默默奉献。

有想过开源，奈何一直忙着修福报，此外很重要的一点是，觉得方案并不完善，还是比较长比较臭。

开源界已经有很多MyBatis相关的项目了，包括官方出品的`mybatis-dynamic-sql`，这玩意把我可恶心坏了。最近接触的项目里有在用，看着那一坨一坨的完全没动力碰的垃圾代码，全世界都在看我们的笑话，Java什么时候变成这样了，让玩PHP，玩C#，玩GO，玩Ruby的同学怎么看待我们，哎卧槽。

魔幻2020年就快结束了，熬夜把拖延了好几年的待办事项做个了结，后续如果有人气，我会考虑把生成代码的逻辑一并释放出来。
