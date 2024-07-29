package com.retailstore.controller;

import com.retailstore.constant.ItemType;
import com.retailstore.constant.UserType;
import com.retailstore.entity.Bill;
import com.retailstore.entity.Item;
import com.retailstore.entity.User;
import com.retailstore.repository.BillRepository;
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

import java.time.LocalDateTime;
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testNetPayableAmountForEmployee() throws Exception {

        User employeeUser = new User();
        employeeUser.setId(1L);
        employeeUser.setType(UserType.EMPLOYEE);
        employeeUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill employeeUserBill = new Bill(1L, employeeUser, items);

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
        affiliateUser.setId(2L);
        affiliateUser.setType(UserType.AFFILIATE);
        affiliateUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill affiliateUserBill = new Bill(2L, affiliateUser, items);

        when(billRepository.findByIdAndUserId(2, 2))
                .thenReturn(Optional.of(affiliateUserBill));

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
        customerUser.setId(3L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now());

        Bill customerUserBill = new Bill(3L, customerUser, items);

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
        customerUser.setId(4L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill customerUserBill = new Bill(4L, customerUser, items);

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
        customerUser.setId(5L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now().minusYears(3));

        Bill customerUserBill = new Bill(5L, customerUser, new ArrayList<>());

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
        customerUser.setId(6L);
        customerUser.setType(UserType.CUSTOMER);
        customerUser.setCreatedAt(LocalDateTime.now());

        Bill customerUserBill = new Bill(6L, customerUser, List.of(new Item(1L, 200.0, ItemType.GROCERY)));

        when(billRepository.findByIdAndUserId(6, 6)).thenReturn(Optional.of(customerUserBill));

        mockMvc.perform(get("/api/bill/amount")
                        .param("billId", "6")
                        .param("userId", "6"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netPayableAmount").value(190.0));
    }
}
