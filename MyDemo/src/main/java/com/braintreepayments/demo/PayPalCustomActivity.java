package com.braintreepayments.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.braintreepayments.api.Braintree;
import com.braintreepayments.api.Braintree.PaymentMethodNonceListener;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.view.PaymentButton;
import com.braintreepayments.api.dropin.view.SecureLoadingProgressBar;
import com.braintreepayments.api.models.CardBuilder;

public class PayPalCustomActivity extends Activity implements PaymentMethodNonceListener {

    private Braintree mBraintree;
    private PaymentButton mPaymentButton;
    //private EditText mCardNumber;
    //private EditText mExpirationDate;
    private SecureLoadingProgressBar mLoadingSpinner;

    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.paypalcustom);

        mPaymentButton = (PaymentButton) findViewById(R.id.payment_button);
        //mCardNumber = (EditText) findViewById(R.id.card_number);
        //mExpirationDate = (EditText) findViewById(R.id.card_expiration_date);

        mBraintree = Braintree.getInstance(this,
                getIntent().getStringExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN));
        mBraintree.addListener(this);
        //mPaymentButton.initialize(this, mBraintree);

        mLoadingSpinner = (SecureLoadingProgressBar) findViewById(R.id.loading_spinner);
    }

    public void onPurchase(View v) {

        mBraintree.startPayWithPayPal(this, mPaymentButton.REQUEST_CODE);

    }

    @Override
    public void onPaymentMethodNonce(String paymentMethodNonce) {
        setResult(RESULT_OK, new Intent()
                .putExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE, paymentMethodNonce));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == PaymentButton.REQUEST_CODE) {
            mBraintree.finishPayWithPayPal(this, responseCode, data);
        }
    }

}
