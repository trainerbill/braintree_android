package com.braintreepayments.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.view.SecureLoadingProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FinishedActivity extends Activity {

    private SecureLoadingProgressBar mLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finished);
        mLoadingSpinner = (SecureLoadingProgressBar) findViewById(R.id.loading_spinner);

        sendNonceToServer(
                getIntent().getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE));
    }

    private void sendNonceToServer(String nonce) {

        RequestParams params = new RequestParams();
        params.put("paymentMethodNonce", nonce);
        params.put("amount", "1");

        new AsyncHttpClient().post(OptionsActivity.getEnvironmentUrl(this) + "/api/payments/braintree/create", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            Log.i("Response:",response.toString());
                            showSuccessView(new JSONObject(response));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    private void showSuccessView(JSONObject message) {

        mLoadingSpinner.setVisibility(View.GONE);

        if (message != null) {
            try {
                if (message.getString("success") == "true") {
                    findViewById(R.id.thanks).setVisibility(View.VISIBLE);
                    TextView transactionid = (TextView) findViewById(R.id.transaction_id);
                    TextView transactionstatus = (TextView) findViewById(R.id.transaction_status);

                    JSONObject transaction = message.getJSONObject("transaction");
                    transactionid.setText("ID:" + transaction.getString("id"));
                    transactionstatus.setText("Status:" + transaction.getString("status"));
                    transactionid.setVisibility(View.VISIBLE);
                    transactionstatus.setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.error).setVisibility(View.VISIBLE);
                    TextView errormessage = (TextView) findViewById(R.id.errormessage);
                    errormessage.setText(message.getString("message"));
                    errormessage.setVisibility(View.VISIBLE);

                }
            } catch (JSONException e) {
                findViewById(R.id.error).setVisibility(View.VISIBLE);
                TextView errormessage = (TextView) findViewById(R.id.errormessage);
                errormessage.setText(e.getMessage());
                errormessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
