package com.est.jdbc.connection;

import com.est.jdbc.util.ConnectionUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.est.jdbc.util.ConnectionUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ConnectionTests {

    @Test
    @DisplayName("Database Connection 테스트")
    void connection_test() throws Exception {

        Connection connection = getConnection();

        log.info("connection = {}", connection);
        log.info("connection.getClass() = {}", connection.getClass());

    }

    @Test
    @DisplayName("DriverManager 연결 객체 테스트")
    void driver_manger_connection_test() throws Exception {

        Connection connection1 = DriverManager.getConnection(
                MariaDbConnectionConstant.URL,
                MariaDbConnectionConstant.USER,
                MariaDbConnectionConstant.PASSWORD
        );

        Connection connection2 = DriverManager.getConnection(
                H2ConnectionConstant.URL,
                H2ConnectionConstant.USER,
                H2ConnectionConstant.PASSWORD
        );

        log.info("connection1 = {}, class = {}", connection1, connection1.getClass());
        log.info("connection2 = {}, class = {}", connection2, connection2.getClass());

        assertThat(connection1).isNotNull();
        assertThat(connection2).isNotNull();

        assertThat(connection1).isNotEqualTo(connection2);

    }

    @Test
    @DisplayName("DriverManagerDataSource, Connection 획득 방법 추상화 하였음")
    void datasource_test_1() throws Exception {

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                MariaDbConnectionConstant.URL,
                MariaDbConnectionConstant.USER,
                MariaDbConnectionConstant.PASSWORD
        );

        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        log.info("conn1 = {}", conn1);
        log.info("conn2 = {}", conn2);

    }

    @Test
    @DisplayName("HikariDataSource 커넥션 풀링 테스트")
    void hikari_connection_pooling_test() throws Exception {

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setJdbcUrl(MariaDbConnectionConstant.URL);
        hikariDataSource.setUsername(MariaDbConnectionConstant.USER);
        hikariDataSource.setPassword(MariaDbConnectionConstant.PASSWORD);

        hikariDataSource.setMaximumPoolSize(5);

    }

}
