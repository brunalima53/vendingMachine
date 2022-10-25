package com.vendingMachine;

public class PurchaseResult extends Result {
    int totalSpent;
    Long productId;
    int[] change;
    public PurchaseResult(String resultMessage){
        super(resultMessage);
    }
    public PurchaseResult(String resultMessage,int totalSpent, Long productId) {
        super(resultMessage);
        this.totalSpent = totalSpent;
        this.productId = productId;
    }

    public PurchaseResult(String resultMessage,int totalSpent, Long productId, int[] change) {
        super(resultMessage);
        this.totalSpent = totalSpent;
        this.productId = productId;
        this.change = change;
    }

    public int getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(int totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int[] getChange() {
        return change;
    }

    public void setChange(int[] change) {
        this.change = change;
    }
}
