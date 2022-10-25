package com.vendingMachine.controller;

import com.vendingMachine.Result;
import com.vendingMachine.exceptions.NotAValidProductCostException;
import com.vendingMachine.exceptions.OperationForbiddenException;
import com.vendingMachine.model.Product;
import com.vendingMachine.model.User;
import com.vendingMachine.repository.ProductRepository;
import com.vendingMachine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Product> listProducts(){
        return productRepository.findAll();
    }


    /* POST method to endpoint /product/admin allows product's creation with "seller" user.
     * Product cost should be multiple of 5 */
    @Transactional
    @PostMapping(path = "/admin", produces="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Result> addProduct(@RequestBody Product product, @AuthenticationPrincipal UserDetails userDetails) throws Exception{
        try{
            User seller = userRepository.findByUsername(userDetails.getUsername());
            checksProductCost(product.getCost());
            product.setSeller(seller);
            productRepository.save(product);
            return new ResponseEntity<>(new Result("Product added successfuly to vending machine"), HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(new Result(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    /* PUT method to endpoint /product/admin allows product's update with "seller" user.
     * Product cost should be multiple of 5. Only product's cost and amount available can be updated. */
    @Transactional
    @PutMapping(path = "/admin")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Result> updateProduct(@RequestBody Product product, @AuthenticationPrincipal UserDetails userDetails) throws Exception{
        try{
            checkProductSeller(product,userDetails);
            Product productToUpdate =  productRepository.findByProductName(product.getProductName());
            checksProductCost(product.getCost());
            productToUpdate.setCost(product.getCost());
            productToUpdate.setAmountAvailable(product.getAmountAvailable());
            productRepository.flush();
            return new ResponseEntity<>(new Result("Product updated successfuly!"),HttpStatus.ACCEPTED);
        }catch (Exception e){
            return new ResponseEntity<>(new Result(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    /* DELETE method to endpoint /product/ allows to delete product with "seller" user.
     * Product can only be deleted by the user who sells it. */
    @Transactional
    @DeleteMapping(path = "admin/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result> deleteProduct(@RequestBody Product product, @AuthenticationPrincipal UserDetails userDetails) throws Exception{
        try{
            checkProductSeller(product,userDetails);
            Product productToDelete =  productRepository.findByProductName(product.getProductName());
            productRepository.delete(productToDelete);
            return new ResponseEntity<>(new Result("Product deleted successfuly!"),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new Result(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    /*Method checkProductSeller validates whether the authenticated user is the seller of the mentioned product.
    * If the seller does not sell this product this method throws an OperationForbiddenException */
    public void checkProductSeller(Product product,UserDetails userDetails) throws OperationForbiddenException {
      Product savedProduct = productRepository.findByProductName(product.getProductName());
      if(!userDetails.getUsername().equals(savedProduct.getSeller().getUsername())){
          throw new OperationForbiddenException("Operation forbidden - Product not sold by this seller. Update not successful.");
      }
    }

    /*Method checksProductCost validates whether the product's cost (product to be created or updated)
     * is multiple of 5. If it's not, throws a NotAValidProductCostException */
    public void checksProductCost(int productCost) throws NotAValidProductCostException {
        if(productCost%5!=0){
                throw new NotAValidProductCostException("Product cost should be divisible by 5.");
        }

    }

}
