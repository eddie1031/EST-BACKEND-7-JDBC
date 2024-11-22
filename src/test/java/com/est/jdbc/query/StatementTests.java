package com.est.jdbc.query;

import com.est.jdbc.domain.Member;
import com.est.jdbc.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class StatementTests {

    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;

    ResultSet rs;

    @BeforeEach
    void init() {
        conn = ConnectionUtil.getConnection();
    }

    @AfterEach
    void closeConnection() {

        if ( rs != null ){
            try {
                rs.close();
            } catch ( SQLException e ) {
                log.error("ResultSet close Error");
            }
        }

        if ( stmt != null ) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("Statement close Error");
            }
        }

        if ( pstmt != null ) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                log.error("Prepared statement close Error");
            }
        }

        if ( conn != null ) {
            try {
                conn.close();
            } catch ( SQLException e ) {
                log.error("Connection close Error");
            }
        }

    }

    @Test
    @DisplayName("Jdbc Member INSERT")
    void jdbc_insert_test() throws Exception {

        Member admin = genMember("admin", "admin");
        Member user = genMember("user", "user");

        String sql1 = genInsertQuery(admin);
        String sql2 = genInsertQuery(user);

        stmt = conn.createStatement();

        int result1 = stmt.executeUpdate(sql1);
        log.info("result1 = {}", result1);
        int result2 = stmt.executeUpdate(sql2);
        log.info("result2 = {}", result2);

    }

    private String genInsertQuery(Member member) {
        return "INSERT INTO member (username, password) VALUES ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    private Member genMember(String username, String password) {
        return new Member(0, username, password);
    }

    @Test
    @DisplayName("JDBC SELECT 테스트(로그인)")
    void jdbc_select_test() throws Exception {

        // 사용자로부터 값을 수취한 상황을 가정
        Member user = genMember("user", "user");

        // 비정상적인 user 로그인
        Member user_ng = genMember("user", "1234");

        // username password가 데이터베이스에 있는 값과 입력 받은 값이 같은지 같지 않은지
        String sql1 = genSelectQuery(user);
        String sql2 = genSelectQuery(user_ng);

        stmt = conn.createStatement();

        rs = stmt.executeQuery(sql1);// 조회

        Member findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isNotBlank();
        assertThat(findMember.getPassword()).isNotBlank();

        assertThat(findMember.getUsername()).isEqualTo("user");
        assertThat(findMember.getPassword()).isEqualTo("user");

        rs.close();

        rs = stmt.executeQuery(sql2);

        Member findMember2 = new Member();

        if ( rs.next() ) {
            findMember2.setMemberId(rs.getInt("member_id"));
            findMember2.setUsername(rs.getString("username"));
            findMember2.setPassword(rs.getString("password"));
        }

        assertThat(findMember2.getUsername()).isNull();
        assertThat(findMember2.getPassword()).isNull();


    }

    private static String genSelectQuery(Member member) {
        return "SELECT m.member_id, m.username, m.password  FROM member as m WHERE m.username = '%s' AND m.password = '%s'".formatted(member.getUsername(), member.getPassword());
    }

    @Test
    @DisplayName("JDBC Statement 테스트")
    void jdbc_statement_test() throws Exception {

        // 로그인
        // user -> admin
        Member dangerAttempt = genMember("admin", "' or '' = '");

        /*
        SQL Injection

        SELECT m.member_id, m.username, m.password
        FROM member as m
        WHERE
        m.username = 'admin'
        AND
        m.password = ''
        or '' = ''
         */

        String sql = genSelectQuery(dangerAttempt);

        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        Member findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        log.info("findMember.getUsername() = {}", findMember.getUsername());

    }

    @Test
    @DisplayName("JDBC Prepared Statement")
    void prepared_statement_test() throws Exception {

        Member dangerAttempt = genMember("admin", "' or '' = '");

        /*
        "SELECT m.member_id, m.username, m.password
        FROM member as m
        WHERE m.username = '%s'
        AND m.password = '%s'"
         */

        String sql = "SELECT m.username, m.password FROM member as m WHERE m.username = ? AND m.password = ?";

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, dangerAttempt.getUsername());
        pstmt.setString(2, dangerAttempt.getPassword());

        rs = pstmt.executeQuery();

        Member findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        log.info("findMember.getUsername() = {}", findMember.getUsername());

    }



}
