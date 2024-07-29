package com.retailstore.service.impl;

import com.retailstore.entity.Bill;
import com.retailstore.entity.Item;
import com.retailstore.entity.User;
import com.retailstore.repository.BillRepository;
import com.retailstore.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static com.retailstore.constant.ItemType.GROCERY;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    public Bill get(long id, long userId) {
        return billRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    @Override
    public double calculateNetPayableAmount(long billId, long userId) {
        Bill bill = get(billId, userId);
        List<Item> items = bill.getItems();

        double totalAmount = calculateTotalAmount(items);
        double groceryAmount = calculateGroceryAmount(items);

        // The percentage based discounts do not apply on groceries.
        double nonGroceryAmount = totalAmount - groceryAmount;
        double percentageDiscount = calculatePercentageDiscount(bill.getUser(), nonGroceryAmount);

        //  Every $100 on the bill, there would be a $ 5 discount.
        double additionalDiscount = calculateAdditionalDiscount(totalAmount);

        return totalAmount - percentageDiscount - additionalDiscount;
    }

    private double calculateTotalAmount(List<Item> items) {
        return items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }

    private double calculateGroceryAmount(List<Item> items) {
        return items.stream()
                .filter(item -> GROCERY.equals(item.getType()))
                .mapToDouble(Item::getPrice)
                .sum();
    }

    private double calculatePercentageDiscount(User user, double nonGroceryAmount) {
        return switch (user.getType()) {
            case EMPLOYEE -> 0.30 * nonGroceryAmount;
            case AFFILIATE -> 0.10 * nonGroceryAmount;
            case CUSTOMER -> {
                if (user.getCreatedAt().isBefore(LocalDateTime.now().minusYears(2))) {
                    yield 0.05 * nonGroceryAmount;
                }
                yield 0;
            }
        };
    }

    private double calculateAdditionalDiscount(double totalAmount) {
        return BigDecimal.valueOf(totalAmount)
                .divide(BigDecimal.valueOf(100), RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(5))
                .doubleValue();
    }
}

