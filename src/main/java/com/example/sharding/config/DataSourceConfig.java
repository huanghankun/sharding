package com.example.sharding.config;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 数据源及分表配置
 * Created by Kane on 2018/1/17.
 */
@Configuration
@MapperScan(basePackages = "com.example.sharding.mapper", sqlSessionTemplateRef = "test1SqlSessionTemplate")
public class DataSourceConfig {


    /**
     * 自定义异步线程池
     * @return
     */
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("Anno-Executor");
        executor.setMaxPoolSize(10);

        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                // .....
            }
        });
        // 使用预定义的异常处理类
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }


    /**
     * 配置数据源0，数据源的名称最好要有一定的规则，方便配置分库的计算规则
     *
     * @return
     */
    @Bean(name = "dataSource_guangdong")
    @ConfigurationProperties(prefix = "spring.datasource.guangdong")
    public DataSource dataSource0() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置数据源1，数据源的名称最好要有一定的规则，方便配置分库的计算规则
     *
     * @return
     */
    @Bean(name = "dataSource_beijing")
    @ConfigurationProperties(prefix = "spring.datasource.beijing")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 配置数据源规则，即将多个数据源交给sharding-jdbc管理，并且可以设置默认的数据源，
     * 当表没有配置分库规则时会使用默认的数据源
     *
     * @param dataSource0
     * @param dataSource1
     * @return
     */
    @Bean
    public DataSourceRule dataSourceRule(@Qualifier("dataSource_guangdong") DataSource dataSource0,
                                         @Qualifier("dataSource_beijing") DataSource dataSource1) {
        /**
         *  设置分库映射
         */
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("dataSource_guangdong", dataSource0);
        dataSourceMap.put("dataSource_beijing", dataSource1);
        /*
            设置默认库，两个库以上时必须设置默认库。默认库的数据源名称必须是dataSourceMap的key之一
         */
        DataSourceRule dsr = new DataSourceRule(dataSourceMap, "dataSource_guangdong");
        return dsr;
    }

    /**
     * 配置数据源策略和表策略，具体策略需要自己实现
     *
     * @param dataSourceRule
     * @return
     */
    @Bean
    public ShardingRule shardingRule(DataSourceRule dataSourceRule) {
        //具体分库分表策略
        TableRule guangdongTableRule = TableRule.builder("t_order_guangdong")
                .actualTables(Arrays.asList("t_order_guangdong_guangzhou", "t_order_guangdong_zhaoqing", "t_order_guangdong_shenzhen"))
                .tableShardingStrategy(new TableShardingStrategy("city", new ModuloTableShardingAlgorithm()))
                .dataSourceRule(dataSourceRule)
                .build();
        TableRule beijingTableRule = TableRule.builder("t_order_beijing")
                .actualTables(Arrays.asList("t_order_beijing"))
                .tableShardingStrategy(new TableShardingStrategy("city", new ModuloTableShardingAlgorithm()))
                .dataSourceRule(dataSourceRule)
                .build();

        //绑定表策略，在查询时会使用主表策略计算路由的数据源，因此需要约定绑定表策略的表的规则需要一致，可以一定程度提高效率
        List<BindingTableRule> bindingTableRules = new ArrayList<BindingTableRule>();
        bindingTableRules.add(new BindingTableRule(Arrays.asList(guangdongTableRule,beijingTableRule)));

        ShardingRule bulid = ShardingRule.builder()
                .dataSourceRule(dataSourceRule)
                .tableRules(Arrays.asList(guangdongTableRule,beijingTableRule))
                .bindingTableRules(bindingTableRules)
                .databaseShardingStrategy(new DatabaseShardingStrategy("province", new ModuloDatabaseShardingAlgorithm()))
                .tableShardingStrategy(new TableShardingStrategy("city", new ModuloTableShardingAlgorithm()))
                .build();
        return bulid;
    }

    /**
     * 创建sharding-jdbc的数据源DataSource，MybatisAutoConfiguration会使用此数据源
     *
     * @param shardingRule
     * @return
     * @throws SQLException
     */
    @Bean(name = "dataSource")
    public DataSource shardingDataSource(ShardingRule shardingRule) throws SQLException {
        return ShardingDataSourceFactory.createDataSource(shardingRule);
    }

    /**
     * 需要手动配置事务管理器
     *
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactitonManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "test1SqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));


        return bean.getObject();
    }

    @Bean(name = "test1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("test1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}