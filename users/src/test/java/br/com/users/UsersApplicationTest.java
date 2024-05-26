package br.com.users;


import br.com.users.shared.annotation.DatabaseTest;
import br.com.users.shared.annotation.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@IntegrationTest
@DatabaseTest
class UsersApplicationTest {

	private final ApplicationContext applicationContext;

	@Autowired
	UsersApplicationTest(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	void contextLoads() {
		var digitalParkingApplication = applicationContext.getBean(UsersApplication.class);
		Assertions.assertThat(digitalParkingApplication).isNotNull();
	}

}
