package com.autotestgen.sample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testCreateOrder_BalanceInsufficient() {
        System.out.println("开始测试：余额不足场景");
        User user = new User();
        user.setBalance(50.0);
        Item item = new Item();
        item.setPrice(100.0);

        OrderResult result = orderService.createOrder(user, item);

        System.out.println("预期结果：OrderResult.fail(\"余额不足\")");
        System.out.println("实际结果：" + result);
        assertEquals("余额不足", result.getMessage());
        System.out.println("余额不足场景测试通过\n");
    }

    @Test
    public void testCreateOrder_OutOfStock() {
        System.out.println("开始测试：无货场景");
        User user = new User();
        user.setBalance(200.0);
        Item item = new Item();
        item.setId(123L);
        item.setPrice(100.0);

        when(inventoryService.hasStock(anyLong())).thenReturn(false);

        OrderResult result = orderService.createOrder(user, item);

        System.out.println("预期结果：OrderResult.fail(\"无货\")");
        System.out.println("实际结果：" + result);
        assertEquals("无货", result.getMessage());
        verify(inventoryService).hasStock(anyLong());
        System.out.println("无货场景测试通过\n");
    }

    @Test
    public void testCreateOrder_Success() {
        System.out.println("开始测试：下单成功场景");
        User user = new User();
        user.setBalance(200.0);
        Item item = new Item();
        item.setId(456L);
        item.setPrice(100.0);

        when(inventoryService.hasStock(anyLong())).thenReturn(true);

        OrderResult result = orderService.createOrder(user, item);

        System.out.println("预期结果：OrderResult.success()");
        System.out.println("实际结果：" + result);
        assertEquals("success", result.getStatus());

        verify(inventoryService).hasStock(anyLong());
        System.out.println("用户余额扣减后：" + user.getBalance());
        System.out.println("下单成功场景测试通过\n");
    }
}
