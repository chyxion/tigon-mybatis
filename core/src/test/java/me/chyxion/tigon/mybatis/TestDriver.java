package me.chyxion.tigon.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Donghuang
 * @date May 13, 2016 10:45:07 AM
 */
@SpringBootApplication
@ImportResource("classpath*:spring/spring-*.xml")
@MapperScan(basePackages = "me.chyxion.tigon.mybatis.mapper", annotationClass = Mapper.class)
public class TestDriver {

    public static void main(String[] args) {
        SpringApplication.run(TestDriver.class);
    }
}
