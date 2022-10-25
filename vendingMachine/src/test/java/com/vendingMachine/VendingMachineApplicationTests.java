package com.vendingMachine;

import com.vendingMachine.model.User;
import com.vendingMachine.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VendingMachineApplicationTests {

	@Autowired
	UserRepository userRepo;
	@Test
	void contextLoads() {
	}

	//CRUD USER TESTS

	@Test
	@Order(1)
	public void testCreateUser () {
		User peter = new User();
		peter.setUsername("peter");
		peter.setPassword("peter");
		peter.setDeposit(200);
		peter.setRole("buyer");
		userRepo.save(peter);
		Long peterId = userRepo.findByUsername("peter").getId();
		assertThat(userRepo.findById(peterId).isPresent());
	}
	@Test
	@Order(2)
	public void readAllUsers(){
		List list = userRepo.findAll();
		assertThat(list).size().isGreaterThan(0);
	}

	@Test
	@Order(3)
	public void testUpdateUser() {
		User peter = userRepo.findByUsername("peter");
		peter.setDeposit(20);
		userRepo.saveAndFlush(peter);
		assertEquals(20,userRepo.findByUsername("peter").getDeposit());
	}
	@Test
	@Order(5)
	public void testDeleteUser(){
		Long peterId = userRepo.findByUsername("peter").getId();
		userRepo.deleteById(peterId);
		assertThat(userRepo.existsById(peterId)).isFalse();
	}

 //------------------------- Test /deposit
	@Test
	@Order(4)
	public void testDepositOfCoin(){
		User peter = userRepo.findByUsername("peter");
		peter.setDeposit(5);
		peter.updateDeposit(5);
		userRepo.saveAndFlush(peter);
		assertEquals(10,userRepo.findByUsername("peter").getDeposit());
	}


}
