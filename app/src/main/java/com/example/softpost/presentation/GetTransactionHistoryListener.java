package com.example.softpost.presentation;

public interface GetTransactionHistoryListener {
    void onGetTransactionHistoryResponse(Object transactions);

    void onTransactionHistoryError(String errorMessage);
}
