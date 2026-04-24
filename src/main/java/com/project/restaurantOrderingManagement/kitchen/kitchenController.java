package com.project.restaurantOrderingManagement.kitchen;

import com.project.restaurantOrderingManagement.exceptions.BadRequestException;
import com.project.restaurantOrderingManagement.manager.ChefDisplayDTO;
import com.project.restaurantOrderingManagement.manager.KitchenOrderDTO;
import com.project.restaurantOrderingManagement.models.Chef;
import com.project.restaurantOrderingManagement.models.Employee;
import com.project.restaurantOrderingManagement.repositories.empRepo;
import com.project.restaurantOrderingManagement.service.ChefAttendanceService;
import com.project.restaurantOrderingManagement.service.ChefOrderService;
import com.project.restaurantOrderingManagement.waiter.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chef")
public class kitchenController {

    private final kitchenService kitchenService;
    private final ChefOrderService chefOrderService;
    private final ChefAttendanceService chefAttendanceService;
    private final empRepo employeeRepository;

    public kitchenController(kitchenService kitchenService,
                             ChefOrderService chefOrderService,
                             ChefAttendanceService chefAttendanceService,
                             empRepo employeeRepository) {
        this.kitchenService = kitchenService;
        this.chefOrderService = chefOrderService;
        this.chefAttendanceService = chefAttendanceService;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/{empCode}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable String empCode,
                                                    @RequestBody OrderRequest request) {
        if (request.getBillno() == null || request.getBillno().isBlank()) {
            throw new BadRequestException("billNo is required");
        }
        if (request.getFoodCode() == null || request.getFoodCode().isBlank()) {
            throw new BadRequestException("foodCode is required");
        }

        kitchenService.updateOrderStatus(request.getFoodCode(), request.getBillno());
        return ResponseEntity.ok("Order preparation started by chef " + empCode);
    }

    @GetMapping("/{empCode}")
    public ResponseEntity<List<Order>> getChefOrders(@PathVariable String empCode) {
        return ResponseEntity.ok(chefOrderService.getOrdersForChef(empCode));
    }

    @GetMapping("/active-display")
    public ResponseEntity<List<ChefDisplayDTO>> getActiveChefDisplay() {
        List<String> activeChefCodes = chefAttendanceService.getActiveChefCodes();
        List<ChefDisplayDTO> display = new ArrayList<>();

        for (String code : activeChefCodes) {
            Optional<Employee> employee = employeeRepository.findById(code);
            if (employee.isEmpty() || !(employee.get() instanceof Chef chef)) {
                continue;
            }

            List<Order> orders = chefOrderService.getOrdersForChef(code);
            List<KitchenOrderDTO> orderDTOs = new ArrayList<>();
            for (Order order : orders) {
                KitchenOrderDTO dto = new KitchenOrderDTO();
                dto.setBillNo(order.getBillNo());
                dto.setFoodCode(order.getFoodCode());
                dto.setQuantity(order.getQuantity());
                dto.setStatus(order.getStatus());
                orderDTOs.add(dto);
            }

            ChefDisplayDTO chefDTO = new ChefDisplayDTO();
            chefDTO.setEmpCode(code);
            chefDTO.setEmpName(chef.getEmpName());
            chefDTO.setSpec(chef.getSpec());
            chefDTO.setLoggedIn(true);
            chefDTO.setOrders(orderDTOs);
            display.add(chefDTO);
        }

        return ResponseEntity.ok(display);
    }
}
