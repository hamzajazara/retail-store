package com.retailstore.controller;

import com.retailstore.constant.ItemType;
import com.retailstore.constant.UserType;
import com.retailstore.entity.Bill;
import com.retailstore.entity.Item;
import com.retailstore.entity.User;
import com.retailstore.repository.BillRepository;
import com.retailstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillRepository billRepository;

    @MockBean
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForEmployee() throws Exception {

        User employeeUser = new User();
        employeeUser.setId(1);
        employeeUser.setType(UserType.EMPLOYEE);
        employeeUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill employeeUserBill = new Bill(1, 1, items);

        when(userService.get(1)).thenReturn(employeeUser);
        when(billRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(employeeUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "1")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("netPayableAmount", is(610.0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForAffiliate() throws Exception {

        User affiliateUser = new User();
        affiliateUser.setId(2);
        affiliateUser.setType(UserType.AFFILIATE);
        affiliateUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill affiliateUserBill = new Bill(2, 2, items);

        when(userService.get(2)).thenReturn(affiliateUser);
        when(billRepository.findByIdAndUserId(2, 2)).thenReturn(Optional.of(affiliateUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "2")
                        .param("userId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPayableAmount").value(710.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForNewCustomer() throws Exception {

        User customerUser = new User();
        customerUser.setId(3);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now());

        Bill customerUserBill = new Bill(3, 3, items);

        when(userService.get(3)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(3, 3)).thenReturn(Optional.of(customerUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "3")
                        .param("userId", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPayableAmount").value(760.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForCustomerMoreThanTwoYears() throws Exception {

        User customerUser = new User();
        customerUser.setId(4);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill customerUserBill = new Bill(4, 4, items);

        when(userService.get(4)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(4, 4)).thenReturn(Optional.of(customerUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "4")
                        .param("userId", "4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPayableAmount").value(735.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForCustomerWithoutAnyItems() throws Exception {

        User customerUser = new User();
        customerUser.setId(5);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now().minusYears(3));

        Bill customerUserBill = new Bill(5, 5, new ArrayList<>());

        when(userService.get(5)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(5, 5)).thenReturn(Optional.of(customerUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "5")
                        .param("userId", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPayableAmount").value(0.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForCustomerWithOneItem() throws Exception {

        User customerUser = new User();
        customerUser.setId(6);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDate.now());

        Bill customerUserBill = new Bill(6, 6, List.of(new Item(1, 200.0, ItemType.GROCERY)));

        when(userService.get(6)).thenReturn(customerUser);
        when(billRepository.findByIdAndUserId(6, 6)).thenReturn(Optional.of(customerUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "6")
                        .param("userId", "6"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPayableAmount").value(190.0));
    }
}
