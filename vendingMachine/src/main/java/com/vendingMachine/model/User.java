package com.vendingMachine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "User")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private int deposit = 0;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false, name = "is_seller")
    private boolean seller;
    @JsonIgnore
    @OneToMany(mappedBy = "seller")
    private Set<Product> products = new HashSet<>();

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setSellerValue() {
        if(this.role.equalsIgnoreCase("buyer"))
            seller = false;
        else if( this.role.equalsIgnoreCase("seller")){
            seller = true;
            this.deposit=0;
        }
    }
    public void updateDeposit(int coin){
        int currentDeposit= this.getDeposit();
        int newDeposit = currentDeposit + coin;
        this.setDeposit(newDeposit);
    }
    public void resetDeposit(){
        this.setDeposit(0);
    }

}
