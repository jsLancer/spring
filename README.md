
## Spring + MVC + JdbcTemplate

> 模仿 Spring 实现的一个小框架，麻雀虽小，五脏俱全，还存在很多问题

### 实现功能

1. Spring IOC：支持 xml 配置和注解两种方式，注解优先原则；
2. @Transactional 简单事务代理
3. MVC 数据动态绑定，
4. JdbcTemplate 对 jdbc 的简单封装


### 使用

applicationContext.xml 配置
```
<?xml version="1.0" encoding="utf-8" ?>
<beans>

    <!--<bean id="jdbcTemplate" class="com.spring.jdbc.JdbcTemplate" scope="singleton">-->
    <!--</bean>-->

    <component-scan base-package="cn.test"/>

    <bean id="handlerView" class="com.spring.mvc.handler.HandlerView">
        <!--视图名称前缀-->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!--视图名称后缀-->
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```


#### web.xml 

本来想着向 SpringMVC 一样过滤 / 请求，但遇到各种问题，所以退而求其次：*.action
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">


    <servlet>
        <servlet-name>DispatcherServlet</servlet-name>
        <servlet-class>com.spring.mvc.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>applicationContext.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>DispatcherServlet</servlet-name>
        <url-pattern>*.action</url-pattern>
    </servlet-mapping>

</web-app>
```

#### db.properties
```
jdbc.url=jdbc:mysql://localhost/exam?characterEncoding=utf-8
jdbc.user=root
jdbc.password=123
jdbc.driver=com.mysql.jdbc.Driver
```

#### controller
```
@Controller
@RequestMapping
public class IndexController {

    @Autowired
    private IndexService indexService;
    
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("/index")
    public String index(@RequestParam(value="id", defaultValue="1") Integer id
    					HttpServletRequest request,  
            			HttpSession session,
            			Model) {
        return "index";
    }
}
```

#### service

```

@Service("indexService")
public class IndexServiceImpl implements IndexService {

    @Autowired
    private IndexDao indexDao;
    
    public void setIndexDao(IndexDao indexDao) {
        this.indexDao = indexDao;
    }
    
    @Transactional
    public void test() {
        //.....
    }
}    
```

#### dao
```
@Repository
public class IndexDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
```

因为平时用 Spring + SpringMVC 比较多，用的时候也经常在想框架背后的实现原理，所以花了一点时间尝试自己实现了一下，遇到很多问题，目前也存在各种 Bug，扩展性也不好，很多地方也没有深入Spring源码，而是根据自己的理解加以实现。嗯，就这样，后期如果有时间会继续更新。。