package ru.enjy.fit_balance;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@SpringBootApplication
//@EnableJpaRepositories(entityManagerFactoryRef = "dataSourceEntityManagerFactory", transactionManagerRef = "dataSourceTransactionManager")
public class FitBalanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitBalanceApplication.class, args);
	}

	/*@Bean
	public LocalContainerEntityManagerFactoryBean dataSourceEntityManagerFactory(
			@Qualifier("dataSource") DataSource dataSource,
			EntityManagerFactoryBuilder builder) {
		return builder
				.dataSource(dataSource)
				.packages("ru.enjy.fit_balance")
				.persistenceUnit("Default")
				.build();
	}

	@Bean
	public PlatformTransactionManager dataSourceTransactionManager(
			@Qualifier("dataSourceEntityManagerFactory") LocalContainerEntityManagerFactoryBean dataSourceEntityManagerFactory) {
		return new JpaTransactionManager(Objects.requireNonNull(dataSourceEntityManagerFactory.getObject()));
	}*/
}
