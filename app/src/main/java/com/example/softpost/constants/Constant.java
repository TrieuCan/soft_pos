package com.example.softpost.constants;

public class Constant {
    public static final String TRANSACTION_SUCCESS = "00";
    public static final String CARD_ISSUER = "01";
    public static final String CARD_ISSUER_SPECIAL_CONDITION = "02";
    public static final String INVALID_MERCHANT = "03";

    public static final String INVALID_TRANSACTION = "12";
    public static final String INVALID_AMOUNT = "13";
    public static final String INVALID_CARD_NUMBER = "14";

    public static final String INVALID_RESPONSE = "20";

    public static final String EXPIRED_CARD = "33";
    public static final String SUSPECTED_FRAUD = "54";
    public static final String WRONG_PIN = "55";
    public static final String SYSTEM_MALFUNCTION = "96";
    public static final String EXCEED_PIN = "75";

    //fail case
    public static final int INTERNAL_ERROR = 100;
    public static final int HAVE_NOT_NFC = 110;
    public static final int SAFETY_NET_API_ERROR = 103;
    public static final int SDK_SOFT_POS_ERROR = 108;
    public static final int ANOTHER_TRANSACTION = 109;
    public static final int NFC_NOT_ACTIVE = 127;
    public static final int DATA_NOT_CORRECT = 131;
    public static final int TRANSACTION_ERROR = 132;
    public static final String EXCEED_WITHDRAW = "65";
    public static final String CER_STREAM = "CER_STREAM";
    public static final String URL = "URL";
    public static final String API_KEY = "API_KEY";
    public static final String HOST_NAME = "HOST_NAME";
    public static final String PIN_PACKAGE_NAME = "PIN_PACKAGE_NAME";

    public static final String FILE_LOGGING = "FILE_LOGGING";

    public static final String ACQUIRER_ID = "ACQUIRER_ID";
    public static final String CONNECTION_TIME_OUT = "CONNECTION_TIME_OUT";
    public static final String HOST_RESPONSE_TIME_OUT = "HOST_RESPONSE_TIME_OUT";
    public static final String ISO_DEEP_TIME_OUT = "ISO_DEEP_TIME_OUT";
    public static final String AMOUNT_PAYMENT = "AMOUNT_PAYMENT";
}