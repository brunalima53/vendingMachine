package com.vendingMachine.controller;

import com.vendingMachine.Coin;
import com.vendingMachine.PurchaseResult;
import com.vendingMachine.Result;
import com.vendingMachine.exceptions.NotAValidCoinException;
import com.vendingMachine.exceptions.NotEnoughStockException;
import com.vendingMachine.exceptions.NotExistentProductException;
import com.vendingMachine.model.Product;
import com.vendingMachine.Purchase;
import com.vendingMachine.model.User;
import com.vendingMachine.repository.ProductRepository;
import com.vendingMachine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(path= "/", produces="application/json")
public class BuyerController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    /* PUT method to endpoint /deposit/{coin} allows to add coins to a buyer's deposit.
     Operation available only to users of role "buyer".
     Values of coins allowed: 5,10,20,50 and 100 cents.
     Ex: http://localhost:8080/deposit/100*/
    @Transactional
    @PostMapping(path = "deposit/{coin}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result> depositCoin(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer coin) throws Exception {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername());
            validateCoin(coin);
            user.updateDeposit(coin);
            userRepository.flush();
            Result result = new Result("Deposit successfully processed");
            return new ResponseEntity(result,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity(new Result(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    /* PUT method to endpoint /reset/ allows to reset a buyer's deposit. Operation available only to users of role "buyer". */
    @Transactional
    @PutMapping(path = "reset/" )
    public ResponseEntity<Result> resetBuyerDeposit(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername());
            user.resetDeposit();
            userRepository.saveAndFlush(user);
            return new ResponseEntity<>( new Result("User deposit reset successfully!"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new Result(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    /* POST method to endpoint /buy/ allows product's purchase with "buyer" user.
     * It must be provided a json indicating the productId to be bought and amount in the request body, ex: {\"productId\":2,\"amount\":2}"*/
    @Transactional
    @PostMapping(path = "buy/")
    public ResponseEntity<PurchaseResult> buyProduct(@RequestBody Purchase purchase, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            //check if there is enough stock available
            checkProductAvailability(purchase);
            //calculate purchase total cost
            int totalCost = calculatePurchaseTotal(purchase);
            //find out user info through username used to log
            User user = userRepository.findByUsername(userDetails.getUsername());
            //check if there is enough money available
            if (!(user.getDeposit()> totalCost)){
                throw new Exception("Not enough coins deposited, please insert more coins in order to complete your request.");
            }
            //calculate the change value
            int remainingDeposit = user.getDeposit() - totalCost;
            //calculate the coins retrieved by the buyer
            int[] change = new int[5];
            //check if there is change
            if(remainingDeposit!=0){
                change= calculateChange(remainingDeposit);
            }
            //construct result with change if there is any
            PurchaseResult result = (remainingDeposit==0 ?  new PurchaseResult("Purchase done successfuly!",totalCost,purchase.getProductId()) : new PurchaseResult("Purchase done successfuly!",totalCost,purchase.getProductId(),change));
            //Updates product's stock
            updateProductStock(purchase);
            // reset buyer deposit to 0
            user.resetDeposit();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (NotExistentProductException e){
            //("No product with given id found.");
            return new ResponseEntity<PurchaseResult>(new PurchaseResult("No product with given id found."),HttpStatus.BAD_REQUEST);
        }
        catch (NotEnoughStockException e){
            return new ResponseEntity<PurchaseResult>(new PurchaseResult(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
        catch(Exception e){
            return new ResponseEntity<PurchaseResult>(new PurchaseResult(e.getMessage()),HttpStatus.NOT_ACCEPTABLE);
        }
    }
    /*Method validateCoin validates coin inserted is allowed*/
    private void validateCoin(int insertedCoin) throws NotAValidCoinException{
       boolean validCoin = false;
        for (Coin coin : Coin.values()) {
            if (coin.getValue()==insertedCoin) {
                validCoin = true;
                break;
            }
        }
        if (!validCoin){
            throw new NotAValidCoinException("Not a valid coin. Please insert coins of 5,10,20,50 or 100 cents, one at a time, please.");
        }
    }

    /*Method checkProductAvailability evaluates if the product mentioned in Purchase object exists
    and, if it does, checks if there is enough stock of that product to complete the purchase request*/
    private void checkProductAvailability(Purchase purchase) throws Exception {
        //check if product exists
        Product chosenProduct = checkProductExists(purchase);
        if(chosenProduct.getAmountAvailable()<purchase.getAmount()){
            throw new NotEnoughStockException("There is no stock available to complete your request.");
        }
    }

    /*Method checkProductExists checks whether product exists in vending machine through productId given in purchase
     request. If it doesn't exist it will throw a NotExistentProductException. */
        private Product checkProductExists(Purchase purchase) throws NotExistentProductException{


        try {// check if product exists in vending machine
            Product chosenProduct = productRepository.findById(purchase.getProductId()).orElseThrow();
            return chosenProduct;
        }catch (Exception e){
            throw new NotExistentProductException("Error: Not a valid productId.");
        }
    }

    /*Method calculateChange calculates number of each 5,10,20, 50 and 100 cents coin to be returned in change. */
    public int[] calculateChange(int amountMoneyToReturn) {
        int[] change = new int[5];
        int remainingAmount = amountMoneyToReturn;
        change[4] = remainingAmount / Coin.HUNDRED_CENTS.getValue();
        remainingAmount = remainingAmount % Coin.HUNDRED_CENTS.getValue();

        change[3] = remainingAmount / Coin.FIFTY_CENTS.getValue();
        remainingAmount = remainingAmount % Coin.FIFTY_CENTS.getValue();

        change[2] = remainingAmount / Coin.TWENTY_CENTS.getValue();
        remainingAmount = remainingAmount % Coin.TWENTY_CENTS.getValue();

        change[1] = remainingAmount / Coin.TEN_CENTS.getValue();
        remainingAmount = remainingAmount % Coin.TEN_CENTS.getValue();

        change[0] = remainingAmount / Coin.FIVE_CENTS.getValue();

        return change;
    }

    /*Method calculatePurchaseTotal calculates total price of requested purchase. */
    public int calculatePurchaseTotal(Purchase purchase) throws NotExistentProductException{
        Product purchasedProduct = productRepository.findById(purchase.getProductId()).orElseThrow();
        int productCost =purchasedProduct.getCost();
        return productCost * purchase.getAmount();
    }

    /*Method updateProductStock updates product stock, removing the bought units from stock after successful purchase occurs. */
    public void updateProductStock(Purchase purchase){
        Product chosenProduct = productRepository.findById(purchase.getProductId()).orElseThrow();
        chosenProduct.setAmountAvailable(chosenProduct.getAmountAvailable() - purchase.getAmount());
        productRepository.saveAndFlush(chosenProduct);
    }

}
