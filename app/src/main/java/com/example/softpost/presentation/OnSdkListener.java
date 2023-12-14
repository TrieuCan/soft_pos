package com.example.softpost.presentation;

import com.example.softpost.dto.TransactionResultDTO;

public interface OnSdkListener {
    void onRegisterSuccess();

    void onLog(String msg);

    void paymentResult(TransactionResultDTO success);

}
