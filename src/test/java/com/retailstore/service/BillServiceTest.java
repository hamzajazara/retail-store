package com.retailstore.service;

import com.retailstore.constant.ItemType;
import com.retailstore.constant.UserType;
import com.retailstore.entity.Bill;
import com.retailstore.entity.Item;
import com.retailstore.entity.User;
import com.retailstore.repository.BillRepository;
import com.retailstore.service.impl.BillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillServiceImpl billService;

    private List<Item> items;

    @BeforeEach
    public void setup() {
        items = Arrays.asList(
                new Item(1L, 200.0, ItemType.GROCERY),
                new Item(2L, 100.0, ItemType.GROCERY),
                new Item(3L, 150.0, ItemType.OTHER),
                new Item(4L, 50.0, ItemType.OTHER),
                new Item(5L, 300.0, ItemType.OTHER)
        );
    }

    @Test
    public void testCalculateNetPayableForEmployee() {
        User employeeUser = new User();
        employeeUser.setId(1L);
        employeeUser.setType(UserType.EMPLOYEE);
        employeeUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill employeeUserBill = new Bill(1L, employeeUser, items);

        when(billRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(employeeUserBill));

        double result = billService.calculateNetPayableAmount(1L, 1L);

        assertEquals(610, result);
    }

    @Test
    public void testCalculateNetPayableForAffiliate() {
        User affiliateUser = new User();
        affiliateUser.setId(2L);
        affiliateUser.setType(UserType.AFFILIATE);
        affiliateUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill affiliateUserBill = new Bill(2L, affiliateUser, items);

        when(billRepository.findByIdAndUserId(2L, 2L))
                .thenReturn(Optional.of(affiliateUserBill));

        double result = billService.calculateNetPayableAmount(2L, 2L);

        assertEquals(710.0, result);
    }

    @Test
    public void testCalculateNetPayableForNewCustomer() {

        User customerUser = new User();
        customerUser.setId(3L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now());

        Bill customerUserBill = new Bill(3L, customerUser, items);

        when(billRepository.findByIdAndUserId(3L, 3L))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(3L, 3L);

        assertEquals(760.0, result);
    }

    @Test
    public void testNetPayableAmountForCustomerMoreThanTwoYears() {

        User customerUser = new User();
        customerUser.setId(4L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill customerUserBill = new Bill(4L, customerUser, items);

        when(billRepository.findByIdAndUserId(4L, 4L))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(4L, 4L);

        assertEquals(735.0, result);
    }

    @Test
    public void testNetPayableAmountForCustomerWithoutAnyItems() {

        User customerUser = new User();
        customerUser.setId(5L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill customerUserBill = new Bill(5L, customerUser, new ArrayList<>());

        when(billRepository.findByIdAndUserId(5L, 5L))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(5L, 5L);

        assertEquals(0.0, result);
    }

    @Test
    public void testNetPayableAmountForCustomerWithOneItem() {

        User customerUser = new User();
        customerUser.setId(6L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now());

        Bill customerUserBill = new Bill(6L, customerUser, List.of(new Item(1L, 200.0, ItemType.GROCERY)));

        when(billRepository.findByIdAndUserId(6L, 6L))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(6L, 6L);

        assertEquals(190.0, result);
    }
}
