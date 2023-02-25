package io.upi.payment.listener;


import io.upi.payment.entity.TransactionResponse;

public interface PaymentStatusListener {
    void onTransactionCompleted(TransactionResponse transactionDetails);
    void onTransactionSuccess(TransactionResponse transactionDetails);
    void onTransactionSubmitted();
    void onTransactionFailed();
    void onTransactionCancelled();
}
