package com.braintreepayments.demo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class OptionsActivity extends Activity implements OnCheckedChangeListener {

    public static final int CUSTOM = 0;
    public static final int DROP_IN = 1;
    public static final int CUSTOM_ONLY = 2;
    public static final int PAYPAL_CUSTOM = 3;


    private static final String ENVIRONMENT = "environment";
    private static final String FORM_TYPE = "form_type";
    private static final String CUSTOMER = "customer";
    private static final String SANDBOX_BASE_SERVER_URL = "https://safe-badlands-8879.herokuapp.com";
    private static final String PRODUCTION_BASE_SERVER_URL = "https://safe-badlands-8879.herokuapp.com";

    private EditText mCustomerId;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        mCustomerId = (EditText) findViewById(R.id.customerId);
        RadioGroup environment = (RadioGroup) findViewById(R.id.environment);
        RadioGroup form = (RadioGroup) findViewById(R.id.form);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCustomerId.setText(mPrefs.getString(CUSTOMER, ""));
        environment.check(getEnvironmentId());
        environment.setOnCheckedChangeListener(this);
        form.check(getFormId());
        form.setOnCheckedChangeListener(this);
    }

    protected void onDestroy() {
        mPrefs.edit().putString(CUSTOMER, mCustomerId.getText().toString()).apply();
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.environment) {
            if (checkedId == R.id.production) {
                mPrefs.edit().putString(ENVIRONMENT, PRODUCTION_BASE_SERVER_URL).apply();
            } else if (checkedId == R.id.sandbox) {
                mPrefs.edit().putString(ENVIRONMENT, SANDBOX_BASE_SERVER_URL).apply();
            }
        } else if (group.getId() == R.id.form) {
            if (checkedId == R.id.custom) {
                mPrefs.edit().putInt(FORM_TYPE, CUSTOM).apply();
            } else if (checkedId == R.id.dropin) {
                mPrefs.edit().putInt(FORM_TYPE, DROP_IN).apply();
            }
            else if (checkedId == R.id.customonly) {
                mPrefs.edit().putInt(FORM_TYPE, CUSTOM_ONLY).apply();
            }
            else if (checkedId == R.id.paypalcustom) {
                mPrefs.edit().putInt(FORM_TYPE, PAYPAL_CUSTOM).apply();
            }
        }
    }

    private int getEnvironmentId() {
        if (getEnvironmentUrl(this).equals(PRODUCTION_BASE_SERVER_URL)) {
            return R.id.production;
        } else {
            return R.id.sandbox;
        }
    }

    public static String getEnvironmentUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(ENVIRONMENT, SANDBOX_BASE_SERVER_URL);
    }

    public static String getClientTokenUrl(Context context) {
        String path = "/api/payments/braintree/token";
        String customer = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(CUSTOMER, "");
        if (!TextUtils.isEmpty(customer)) {
            path += "?customer_id=" + customer;
        }

        return getEnvironmentUrl(context) + path;
    }

    private int getFormId() {
        if (getFormType(this) == CUSTOM) {
            return R.id.custom;
        } else if (getFormType(this) == DROP_IN) {
            return R.id.dropin;
        }
        else if (getFormType(this) == PAYPAL_CUSTOM) {
            return R.id.paypalcustom;
        }
        else {
            return R.id.customonly;
        }
    }

    public static int getFormType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(FORM_TYPE, DROP_IN);
    }

}
