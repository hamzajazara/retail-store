package com.retailstore.controller;

import com.retailstore.model.response.PayableResponse;
import com.retailstore.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping("/amount")
    public ResponseEntity<PayableResponse> netPayableAmount(@RequestParam int billId, @RequestParam int userId) {
        return ResponseEntity.ok(new PayableResponse(billService.calculateNetPayableAmount(billId, userId)));
    }
}
