package com.project.restaurantOrderingManagement.waiter;
import com.project.restaurantOrderingManagement.models.Food;
import com.project.restaurantOrderingManagement.models.Log;
import com.project.restaurantOrderingManagement.repositories.foodRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.project.restaurantOrderingManagement.repositories.logRepo;
import java.util.Date;
import java.util.List;
@Service
public class billService {
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private final foodRepo foodRepo;

    public billService(com.project.restaurantOrderingManagement.repositories.logRepo logRepo, OrderService orderService, RedisTemplate<String, Object> redisTemplate, foodRepo foodRepo) {
        this.logRepo = logRepo;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
        this.foodRepo = foodRepo;
    }

    @Autowired
    private logRepo logRepo;

    public double calculateAmount(List<Order> orders) {
        double amount = 0;
        for (Order order : orders) {
            Food food =  (Food) foodRepo.findById(order.getFoodCode()).get();
            amount += food.getPrice() * order.getQuantity();
        }
        return amount;
    }
    public void createBill(String waiterCode, int tableNo, long billNo) {
        redisTemplate.opsForList().rightPush("bill:" + billNo, new billDTO(billNo, waiterCode, tableNo));
    }
    public void closeBill(long billNo, billDTO billDTO) {
        List<Order> orders = orderService.closeOrder(billNo);
        double amt = calculateAmount(orders);
        Date date = new Date();
        Log log = new Log();
        log.setAmount(amt);
        log.setFoodItems(orders);
        log.setBillNo(billNo);
        log.setWaiterCode(billDTO.getWaitercode());
        log.setDate(date);
        logRepo.save(log);
        redisTemplate.opsForList().remove("bill:" + billNo,1,billDTO);

    }

}
