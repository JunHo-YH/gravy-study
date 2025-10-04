package kr.gravy.gravystudy.infrastructure.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@MapperScan(basePackages = "kr.gravy.gravystudy.**.mapper")
public class MyBatisConfiguration {

}
