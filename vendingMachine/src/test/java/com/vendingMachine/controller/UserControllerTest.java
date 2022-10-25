package com.vendingMachine.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vendingMachine.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void addUser() throws Exception {
        this.mockMvc.perform(post("/user/new").contentType(APPLICATION_JSON_UTF8)
                        .content("{\"username\": \"buyer4\",\"password\": \"buyer4\",\"deposit\": 10,\"role\": \"buyer\"}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated());

    }

    @Test
    @Order(2)
    @WithMockUser(username="Bruna",roles={"SELLER","BUYER"})
    void sellerGetsUserInfo() throws Exception{
        this.mockMvc.perform(get("/user/info")).andExpect(status().isOk());
    }
    @Test
    @Order(3)
    @WithMockUser(username="buyer4",roles={"BUYER"})
    void buyerGetsUserInfo() throws Exception{
        this.mockMvc.perform(get("/user/info")).andExpect(status().isOk());
    }
    @Test
    @Order(4)
    void unauthGetsUserInfo() throws Exception{
        this.mockMvc.perform(get("/user/info")).andExpect(status().isUnauthorized());
    }
    @Test
    @Order(5)
    @WithMockUser(username="buyer4",roles={"BUYER"})
    void updateUser() throws Exception {
        this.mockMvc.perform(put("/user/changePassword").contentType(APPLICATION_JSON_UTF8).content("{\"password\": \"buyer4b\"}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
    }


    @Test
    @Order(6)
    @WithMockUser(username="buyer4",roles={"BUYER"})
    void deleteUser() throws Exception{
        this.mockMvc.perform(delete("/user/delete")).andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void unauthDeleteUser() throws Exception{
        this.mockMvc.perform(delete("/user/delete")).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    void unauthUpdateUser() throws Exception {
        this.mockMvc.perform(put("/user/changePassword").contentType(APPLICATION_JSON_UTF8).content("{\"password\": \"buyer4b\"}"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isUnauthorized());
    }
}