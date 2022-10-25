package com.vendingMachine.controller;

import com.vendingMachine.model.User;
import com.vendingMachine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    /* POST method to endpoint /user/new allows user creation. */
    @Transactional
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user){
        user.setSellerValue();
        return userRepository.save(user);
    }

    /* GET method to endpoint /user/info allows a user to view its user information.
     * User properties like role, username, deposit and role can be consulted through this endpoint.
     * It will only present data about authenticated user */
    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@AuthenticationPrincipal UserDetails userDetails){
        return  userRepository.findByUsername(userDetails.getUsername());
    }

    /*DELETE method to endpoint /user/delete. It will delete the user signed in. */
    @Transactional
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser( @AuthenticationPrincipal UserDetails userDetails)throws Exception{

        User userToDelete = userRepository.findByUsername(userDetails.getUsername());
        userRepository.delete(userToDelete);
    }

    /* PUT method to endpoint /user/edit allows a user to alter its password.
     * User properties like role and username  cannot be edited.
     * A user's deposit can be altered through /buyer/deposit endpoint. */
    @Transactional
    @PutMapping("/changePassword")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser( @RequestBody User user,@AuthenticationPrincipal UserDetails userDetails){
        User updatedUser = userRepository.findByUsername(userDetails.getUsername());
        updatedUser.setPassword(user.getPassword());
        userRepository.saveAndFlush(updatedUser);
    }
}
