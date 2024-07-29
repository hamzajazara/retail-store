package com.retailstore.service;

import com.retailstore.constant.ItemType;
import com.retailstore.constant.UserType;
import com.retailstore.entity.Bill;
import com.retailstore.entity.Item;
import com.retailstore.entity.User;
import com.retailstore.repository.BillRepository;
import com.retailstore.repository.UserRepository;
import com.retailstore.service.impl.BillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
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

    @Mock
    private UserService userService;
    
    private List<Item> items;

    @BeforeEach
    public void setup() {
        items = Arrays.asList(
                new Item(1, 200.0, ItemType.GROCERY),
                new Item(2, 100.0, ItemType.GROCERY),
                new Item(3, 150.0, ItemType.OTHER),
                new Item(4, 50.0, ItemType.OTHER),
                new Item(5, 300.0, ItemType.OTHER)
        );
    }

    @Test
    public void testCalculateNetPayableForEmployee() {
        User employeeUser = new User();
        employeeUser.setId(1);
        employeeUser.setType(UserType.EMPLOYEE);
        employeeUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill employeeUserBill = new Bill(1, 1, items);

        when(userService.get(1)).thenReturn(employeeUser);
        when(billRepository.findByIdAndUserId(1, 1))
                .thenReturn(Optional.of(employeeUserBill));

        double result = billService.calculateNetPayableAmount(1, 1);

        assertEquals(610, result);
    }

    @Test
    public void testCalculateNetPayableForAffiliate() {
        User affiliateUser = new User();
        affiliateUser.setId(2);
        affiliateUser.setType(UserType.AFFILIATE);
        affiliateUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill affiliateUserBill = new Bill(2, 2, items);

        when(userService.get(2)).thenReturn(affiliateUser);
        when(billRepository.findByIdAndUserId(2, 2))
                .thenReturn(Optional.of(affiliateUserBill));

        double result = billService.calculateNetPayableAmount(2, 2);

        assertEquals(710.0, result);
    }

    @Test
    public void testCalculateNetPayableForNewCustomer() {

        User customerUser = new User();
        customerUser.setId(3);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now());

        Bill customerUserBill = new Bill(3, 3, items);

        when(userService.get(3)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(3, 3))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(3, 3);

        assertEquals(760.0, result);
    }

    @Test
    public void testNetPayableAmountForCustomerMoreThanTwoYears() {

        User customerUser = new User();
        customerUser.setId(4);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill customerUserBill = new Bill(4, 4, items);

        when(userService.get(4)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(4, 4))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(4, 4);

        assertEquals(735.0, result);
    }

    @Test
    public void testNetPayableAmountForCustomerWithoutAnyItems() {

        User customerUser = new User();
        customerUser.setId(5);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill customerUserBill = new Bill(5, 5, new ArrayList<>());

        when(userService.get(5)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(5, 5))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(5, 5);

        assertEquals(0.0, result);
    }

    @Test
    public void testNetPayableAmountForCustomerWithOneItem() {

        User customerUser = new User();
        customerUser.setId(6);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now());

        Bill customerUserBill = new Bill(6, 6, List.of(new Item(1, 200.0, ItemType.GROCERY)));

        when(userService.get(6)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(6, 6))
                .thenReturn(Optional.of(customerUserBill));

        double result = billService.calculateNetPayableAmount(6, 6);

        assertEquals(190.0, result);
    }
}
