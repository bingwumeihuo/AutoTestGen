package com.autotestgen.sample;

public class OrderService {

    private final InventoryService inventoryService;

    public OrderService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public OrderResult createOrder(User user, Item item) {
        if (user.getBalance() < item.getPrice()) {
            return OrderResult.fail("余额不足");
        }
        if (!inventoryService.hasStock(item.getId())) {
            return OrderResult.fail("无货");
        }

        // 扣减余额
        user.setBalance(user.getBalance() - item.getPrice());

        return OrderResult.success();
    }
}
