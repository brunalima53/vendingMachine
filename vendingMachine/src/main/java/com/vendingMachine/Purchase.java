package com.vendingMachine;

import com.vendingMachine.model.Product;
import com.vendingMachine.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class Purchase {
    Long productId;
    int amount;
    @Autowired
    private ProductRepository productRepository;
    public Purchase(Long productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }



    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
