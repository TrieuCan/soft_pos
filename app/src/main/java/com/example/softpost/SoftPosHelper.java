package com.example.softpost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cardtek.softpos.SoftPosService;
import com.cardtek.softpos.constants.CurrencyCode;
import com.cardtek.softpos.constants.TransactionType;
import com.cardtek.softpos.interfaces.CheckPOSServiceListener;
import com.cardtek.softpos.interfaces.CustomRequestListener;
import com.cardtek.softpos.interfaces.InitializeListener;
import com.cardtek.softpos.interfaces.PinAppListener;
import com.cardtek.softpos.interfaces.RegisterListener;
import com.cardtek.softpos.interfaces.TransactionListener;
import com.cardtek.softpos.interfaces.UnregisterListener;
import com.cardtek.softpos.model.entity.CardAppInfo;
import com.cardtek.softpos.results.SoftPosError;
import com.cardtek.softpos.utils.SoftPosInfo;
import com.example.softpost.constants.Constant;
import com.example.softpost.dto.TransactionHistoryRequest;
import com.example.softpost.dto.TransactionListResponse;
import com.example.softpost.presentation.GetTransactionHistoryListener;
import com.example.softpost.presentation.OnSdkListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SoftPosHelper {
    private SoftPosService softPosService;
    private OnSdkListener onSdkListener;
    private int amount = 0;
    private static SoftPosHelper instance;

    public static SoftPosHelper getInstance() {
        if (instance == null) {
            synchronized (SoftPosHelper.class) {
                instance = new SoftPosHelper();
            }
        }
        return instance;
    }

    public void startSdk(Context context) {
        getCurrencyCode();
        Log.e("startSdk", "startSdk");
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.AMOUNT_PAYMENT, this.amount);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public SoftPosHelper() {
    }

    public SoftPosHelper(Context context, OnSdkListener onSdkListener) {
        this.onSdkListener = onSdkListener;
        this.onSdkListener.onLog("start init");
        doInitSoftPosService(context);
    }

    public void doInitSoftPosService(Context context) {
        Log.e("SoftPosService", "init soft pos service");
        softPosService = new SoftPosService(context.getApplicationContext());
        softPosService.initialize(new InitializeListener() {
            @Override
            public void onPOSReady() {
                Log.e("onPOSReady", "onPOSReady");
                getCurrencyCode();
                onSdkListener.onRegisterSuccess();
                onSdkListener.onLog("onPOSReady");
            }

            @Override
            public void onRegisterNeed() {
                onSdkListener.onLog("onRegisterNeed");
                onRegister(context);
            }

            @Override
            public void onPermissionNeed(ArrayList<String> arrayList) {
                onSdkListener.onLog("onPermissionNeed: " + String.join(",", arrayList));
            }


            @Override
            public void onInitializeError(SoftPosError softPosError) {
                onSdkListener.onLog("onInitializeError:" + new Gson().toJson(softPosError).toString());
            }
        });
    }

    public void initSDK(Context context, Map<String, Object> config, int amount, OnSdkListener onSdkListener) throws URISyntaxException, IOException {
        this.amount = amount;
        instance.onSdkListener = onSdkListener;
        InputStream cert = (InputStream) config.get(Constant.CER_STREAM);
        String url = (String) config.get(Constant.URL);
        String apiKey = (String) config.get(Constant.API_KEY);
        String hostName = (String) config.get(Constant.HOST_NAME);
        String fileLogging = (String) config.get(Constant.FILE_LOGGING);
        String pinPackageName = (String) config.get(Constant.PIN_PACKAGE_NAME);
        Long acquirerId = (Long) config.get(Constant.ACQUIRER_ID);
        Integer connectionTimeOut = (Integer) config.get(Constant.CONNECTION_TIME_OUT);
        Integer hostResponseTimeOut = (Integer) config.get(Constant.HOST_RESPONSE_TIME_OUT);
        Integer isoDeepTimeOut = (Integer) config.get(Constant.ISO_DEEP_TIME_OUT);


        SoftPosInfo.setUrl(url);
        SoftPosInfo.setSafetyNetApiKey(apiKey);
        SoftPosInfo.setAcquirerId(acquirerId);
        SoftPosInfo.setConnectionTimeoutSec(connectionTimeOut);
        SoftPosInfo.setHostCertificate(cert);
        SoftPosInfo.setHostName(hostName);
        SoftPosInfo.enableForegroundDispatch();
        SoftPosInfo.setL2HostResponseTimeoutMs(hostResponseTimeOut); // 3 min is the max value. I'll
        SoftPosInfo.setIsoDepTimeoutMs(isoDeepTimeOut);
        SoftPosInfo.enableFileLogging(); // use default log path
        SoftPosInfo.enableFileLogging(fileLogging); // set custom log path
        SoftPosInfo.enableGeoCoordinates();

//        SoftPosInfo.setPinAppPackageName(pinPackageName);
//        SoftPosInfo.setPinAppVersion(com.softpos.pin.BuildConfig.VERSION_NAME);
//        SoftPosInfo.setPinAppLanguage("vi");
//        SoftPosInfo.setPinAppTimeoutMs(60000);
//        SoftPosInfo.disablePinAppAutoKill();
//        SoftPosInfo.enablePinAppAutoKill();

        SoftPosInfo.setDetectionListener(() -> new Handler(Looper.getMainLooper()).post(() -> {
            onSdkListener.onLog("onRiskDetected");
        }));

        doInitSoftPosService(context);
    }

    public void onRegister(Context context) {
        Log.e("onRegister", "onRegister");
        softPosService.register("001-C-294858", "91510003", "123456", new RegisterListener() {
            @Override
            public void onRegisterSuccess() {
                Log.e("onRegisterSuccess", "onRegisterSuccess");
                getCurrencyCode();
                onSdkListener.onRegisterSuccess();
            }

            @Override
            public void onRegisterError(SoftPosError softPosError) {
                Log.e("onRegisterError", "onRegisterError" + softPosError.getErrorCode() + "-" + softPosError.getErrorMessage());
                onSdkListener.onLog("onRegister error: " +
                        softPosError.getErrorCode() + "-" + softPosError.getErrorMessage());
            }
        });
    }

    public CurrencyCode getCurrencyCode() {
        return softPosService.getCurrency();
    }

    public void onCheckPosService(CheckPOSServiceListener checkPOSServiceListener) {
        new Thread(() -> {
            softPosService.checkPOSService(checkPOSServiceListener);
        }).start();
    }

    public void onStartTransaction(Activity activity, TransactionListener transactionListener) {
        new Thread(() -> {
            Log.e("onStartTransaction", "onStartTransaction");

//            Map<String, String>
//                    customMessage = new HashMap<>();
//            customMessage.put("transactionId",
//                    "17823678");
//
//            CardAppInfo cardAppInfo = new CardAppInfo();
//            cardAppInfo.setCardType("C");
//            cardAppInfo.setAID("A0000000041010");
//            cardAppInfo.setTrnxType("adfasdf");
//            cardAppInfo.setKernelId(111);

            if (getCardAppInfo().size() > 0) {

            } else {
                softPosService.startTransaction(this.amount, TransactionType.SALE, 20000, "0100", activity, transactionListener);
            }
        }).start();

    }


    private List<CardAppInfo> getCardAppInfo() {
        //Prepares cardAppInfo list according to the provided  transaction type.
        List<CardAppInfo> cardAppInfoList = softPosService.prepareAppList(TransactionType.SALE);
        return cardAppInfoList;

//        CardAppInfo cardAppInfo = new CardAppInfo();
//        cardAppInfo.setCardType("C");
//        cardAppInfo.setAID("A0000000041010");
//        cardAppInfo.setTrnxType("adfasdf");
//        cardAppInfo.setKernelId(111);
//        return cardAppInfo;
    }

    public void alertSDK() {
//        softPosService.alertAppTamper();
    }

    public void enableForegroundDispatch(Activity context) {
        onSdkListener.onLog("enableForegroundDispatch");
        softPosService.enableForegroundDispatch(context);
    }

    public void disableForegroundDispatch(Activity context) {
        onSdkListener.onLog("disableForegroundDispatch");
        softPosService.disableForegroundDispatch(context);
    }

    public void unregister() {
        Log.e("unregister", "unregister");
        softPosService.unregister(new UnregisterListener() {
            @Override
            public void onUnregisterSuccess() {
                Log.e("onUnregisterSuccess", "onUnregisterSuccess");
                onSdkListener.onLog("onUnregisterSuccess");
            }

            @Override
            public void onUnregisterError(SoftPosError softPosError) {
                onSdkListener.onLog("onRegister error: " +
                        softPosError.getErrorCode() + "-" + softPosError.getErrorMessage());
            }
        });
    }

    public String loadGifNFC() {
        return softPosService.getNFCLocationGifUrl();
    }

    public OnSdkListener getSdkListener() {
        return onSdkListener;
    }

    public void setPinAppListener(PinAppListener pinAppListener) {
        softPosService.setPinAppListener(pinAppListener);
    }

    public void getTransactionHistory(TransactionHistoryRequest transactionHistoryRequest,
                                      GetTransactionHistoryListener getTransactionHistoryListener) {
        new Thread(() -> {
            softPosService.sendCustomRequest("getTransactionsV2",
                    transactionHistoryRequest, TransactionListResponse.class, new
                            CustomRequestListener<TransactionListResponse>() {
                                @Override
                                public void onGetCustomResponse(TransactionListResponse
                                                                        transactionLists) {
                                    getTransactionHistoryListener.onGetTransactionHistoryResponse(transactionLists.getTransactions());
                                }

                                @Override
                                public void onCustomRequestError(final SoftPosError error) {
                                    getTransactionHistoryListener.onTransactionHistoryError(error.getErrorMessage());
                                }
                            });
        }).start();
    }

    public boolean cancelTransaction() {
        return softPosService.cancelTransaction();
    }

}
