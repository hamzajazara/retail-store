package com.retailstore.service;

public interface BillService {

    double calculateNetPayableAmount(long billId, long userId);
}
