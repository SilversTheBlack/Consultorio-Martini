package com.michele.martins;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DataConfiguration {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/martins"); 
        dataSource.setUsername("root");
        dataSource.setPassword("damnedkkk1");
     
        try (Connection connection = dataSource.getConnection()) {
        	System.out.println("------------------------------------------------------------------------");
            System.out.println("sucesso ao se conectar com o banco de dados!");
            System.out.println("------------------------------------------------------------------------");
        } catch (SQLException e) {
        	System.out.println("------------------------------------------------------------------------");
            System.err.println("Falha na conexão com o banco de dados: " + e.getMessage());
            System.out.println("------------------------------------------------------------------------");
        }

        return dataSource;
    }   

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        adapter.setPrepareConnection(true);
        return adapter;
    }
}

