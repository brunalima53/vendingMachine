package com.vendingMachine.controller;

import com.vendingMachine.Purchase;
import com.vendingMachine.exceptions.NotExistentProductException;
import com.vendingMachine.model.User;
import com.vendingMachine.repository.ProductRepository;
import com.vendingMachine.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class BuyerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ProductRepository prodRepo;

    @Autowired
    BuyerController buyerController;

    // Test Cenario: /deposit/{coin} endpoint with 5 cents coin with buyer user
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void deposit5CentsCoin() throws Exception {
        int expectedDeposit = userRepo.findByUsername("buyer2").getDeposit() + 5;
        this.mockMvc.perform(post("/deposit/5")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        assertEquals(expectedDeposit,userRepo.findByUsername("buyer2").getDeposit());
    }

    // Test Cenario: /deposit/{coin} endpoint with 10 cents coin with buyer user
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void deposit10CentsCoin() throws Exception {
        int expectedDeposit = userRepo.findByUsername("buyer2").getDeposit() + 10;
        this.mockMvc.perform(post("/deposit/10")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        assertEquals(expectedDeposit,userRepo.findByUsername("buyer2").getDeposit());
    }

    // Test Cenario: /deposit/{coin} endpoint with 20 cents coin with buyer user
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void deposit20CentsCoin() throws Exception {
        int expectedDeposit = userRepo.findByUsername("buyer2").getDeposit() + 20;
        this.mockMvc.perform(post("/deposit/20")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        assertEquals(expectedDeposit,userRepo.findByUsername("buyer2").getDeposit());
    }
    @Test
    // Test Cenario: /deposit/{coin} endpoint with 50 cents coin with buyer user
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void deposit50CentsCoin() throws Exception {
        int expectedDeposit = userRepo.findByUsername("buyer2").getDeposit() + 50;
        this.mockMvc.perform(post("/deposit/50")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        assertEquals(expectedDeposit,userRepo.findByUsername("buyer2").getDeposit());
    }

    @Test
    // Test Cenario: /deposit/{coin} endpoint with 100 cents coin with buyer user
    // Expected Outcome: HTTP STATUS Ok - 100 cents should be added to buyer deposit
    // Expected Outcome: HTTP STATUS FORBIDDEN - seller should not be allowed to deposit coins
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void deposit100CentsCoin() throws Exception {
        int expectedDeposit = userRepo.findByUsername("buyer2").getDeposit() + 100;
        this.mockMvc.perform(post("/deposit/100")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        assertEquals(expectedDeposit,userRepo.findByUsername("buyer2").getDeposit());
    }

    // Test Cenario: /deposit/{coin} endpoint with 100 cents coin with seller user
    // Expected Outcome: HTTP STATUS FORBIDDEN - seller should not be allowed to deposit coins
    @Test
    @Order(1)
    @WithMockUser(username="Bruna",roles={"SELLER"})
    void sellerDeposit() throws Exception {
        this.mockMvc.perform(post("/deposit/100")).andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden());
    }


    // Test Cenario: /deposit/{coin} endpoint with invalid coin with buyer user
    // Expected Outcome: HTTP STATUS BAD REQUEST - Only 5,10,20,50 and 100 cents coins should  be allowed
    @Test
    @Order(2)
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void buyerDepositInvalidCoin() throws Exception {
        int expectedDeposit = userRepo.findByUsername("Bruna").getDeposit() ;
        this.mockMvc.perform(post("/deposit/200")).andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());
        assertEquals(expectedDeposit,userRepo.findByUsername("Bruna").getDeposit());
    }


    // Test Cenario: /deposit/{coin} endpoint without authentication
    // Expected Outcome: HTTP STATUS UNAUTHORIZED -  Only authenticated buyers should be allowed to deposit coins.
    @Test
    @Order(3)
    void noAuthDepositCoin() throws Exception {
        this.mockMvc.perform(post("/deposit/100")).andDo(MockMvcResultHandlers.print()).andExpect(status().isUnauthorized());
    }

    // Test Cenario: /reset/ endpoint with buyer user authenticated
    // Expected Outcome: HTTP STATUS OK -  Buyers' deposit should be set to 0.
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void resetBuyerDeposit() throws Exception {
        int expectedDeposit = 0;
        this.mockMvc.perform(put("/reset/")).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        assertEquals(expectedDeposit,userRepo.findByUsername("buyer2").getDeposit());
    }

    // Test Cenario: /reset/ endpoint with seller user authenticated
    // Expected Outcome: HTTP STATUS FORBIDDEN -  Sellers users aren't allowed to deposit coins.
    @Test
    @WithMockUser(username="Bruna",roles={"SELLER"})
    void resetSellerDeposit() throws Exception {
        int expectedDeposit = 0;
        this.mockMvc.perform(put("/reset/")).andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden());
    }


    // Test Cenario: /buy/ endpoint with buyer user authenticated who tries to buy a not existent product
    // Expected Outcome: HTTP STATUS BAD REQUEST -  It should not be possible to buy a not existent product.
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void buyNotExistentProduct() throws Exception {
        this.mockMvc.perform(post("/buy/").contentType(APPLICATION_JSON_UTF8).content("{\"productId\":23,\"amount\":2}"))
 .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());
    }


    // Test Cenario: /buy/ endpoint with buyer user authenticated who tries to buy an unavailable amount of product
    // Expected Outcome: HTTP STATUS BAD REQUEST -  It should not be possible to buy an unavailable amount of product.
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void buyNotEnoughProduct() throws Exception {
        this.mockMvc.perform(post("/buy/").contentType(APPLICATION_JSON_UTF8).content("{\"productId\":2,\"amount\":24}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());

    }

    // Test Cenario: /buy/ endpoint with buyer user authenticated who tries to buy an amount of product that can't afford.
    // Expected Outcome: HTTP STATUS BAD REQUEST -  It should not be possible to buy an amount of product when the deposit does not cover purchase costs.
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void buyNotEnoughDeposit() throws Exception {
        this.mockMvc.perform(post("/buy/").contentType(APPLICATION_JSON_UTF8).content("{\"productId\":2,\"amount\":20}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());

    }


    // Test Cenario: /buy/ endpoint with seller user authenticated.
    // Expected Outcome: HTTP STATUS FORBIDDEN - It should not be possible that a seller user buys a product.
    @Test
    @WithMockUser(username="Bruna",roles={"SELLER"})
    void resetBuy() throws Exception {
        this.mockMvc.perform(post("/buy/").contentType(APPLICATION_JSON_UTF8).content("{\"productId\":2,\"amount\":2}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden());
    }

    // Test Cenario: /buy/ endpoint with seller user authenticated.
    // Expected Outcome: HTTP STATUS OK - It should be possible that a buyer user buys a product.
    @Test
    @WithMockUser(username="buyer2",roles={"BUYER"})
    void buyProduct() throws Exception {
        int amountAvailable = prodRepo.findById(2L).get().getAmountAvailable();
        this.mockMvc.perform(post("/buy/").contentType(APPLICATION_JSON_UTF8).content("{\"productId\":2,\"amount\":2}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        User buyer2 = userRepo.findByUsername("buyer2");
        int actualAmount = prodRepo.findById(2L).get().getAmountAvailable();
        int expectedAmount= amountAvailable -2 ;
        assertEquals(0,buyer2.getDeposit());
        assertEquals(expectedAmount, actualAmount);

    }

    // Test Cenario: calculate distribution of coins of change of 185 cents
    // Expected Outcome: It should return a coin of each amount.
    @Test
    void calculateChange() {
        int[] changeExpected = new int[5];
        changeExpected[0]=1;
        changeExpected[1]=1;
        changeExpected[2]=1;
        changeExpected[3]=1;
        changeExpected[4]=1;

        int[] change = buyerController.calculateChange(185);
        assertEquals(changeExpected[0],change[0]);
        assertEquals(changeExpected[1],change[1]);
        assertEquals(changeExpected[2],change[2]);
        assertEquals(changeExpected[3],change[3]);
        assertEquals(changeExpected[4],change[4]);
    }

    // Test Cenario: calculate total cost of purchase request.
    // Expected Outcome: The cost of 2 products which cost 25 cents each should be 50cents.
    @Test
    void calculatePurchaseTotal() {
        try {
            Purchase purchase =  new Purchase(2L,2);
            int result = buyerController.calculatePurchaseTotal(purchase);
            assertEquals(50,result);
        } catch (NotExistentProductException e) {
            throw new RuntimeException(e);
        }
    }


    // Test Cenario: update amount of available products after request purchase of 2 equal products.
    // Expected Outcome: The amount of products available should be reduced by 2 units.
    @Test
    void updateProductStock(){
        Purchase purchase = new Purchase(2L,2);
        int amount = prodRepo.findById(purchase.getProductId()).get().getAmountAvailable();
        buyerController.updateProductStock(purchase);
        int expectedAmount = amount -2;
        int actualAmount = prodRepo.findById(purchase.getProductId()).get().getAmountAvailable();
        assertEquals(expectedAmount,actualAmount);
    }
}