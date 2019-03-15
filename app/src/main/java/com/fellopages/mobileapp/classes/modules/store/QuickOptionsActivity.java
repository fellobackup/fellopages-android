package com.fellopages.mobileapp.classes.modules.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;

import org.json.JSONObject;

public class QuickOptionsActivity extends AppCompatActivity {
    CardView mShippingMethod, mPaymentMethod, mAddProduct;
    TextView mStoreName, mSkip;
    private JSONObject mBody;
    private Context mContext;
    private String mRedirectUrl;
    private ImageView shippingMethodDone, paymentMethodDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_options);
        mShippingMethod = findViewById(R.id.store_quick_option_shipping);
        mPaymentMethod = findViewById(R.id.store_quick_option_payment);
        mAddProduct = findViewById(R.id.store_quick_option_product);
        mStoreName = findViewById(R.id.store_quick_name);
        mSkip = findViewById(R.id.store_quick_skip);
        shippingMethodDone = findViewById(R.id.shipping_method_check);
        paymentMethodDone = findViewById(R.id.payment_method_check);
        mContext = QuickOptionsActivity.this;
        mBody = GlobalFunctions.getCreateResponse(getIntent().getStringExtra(ConstantVariables.EXTRA_CREATE_RESPONSE));
        mStoreName.setText(mBody.optString("title"));
        mRedirectUrl = AppConstant.DEFAULT_URL + "sitestore/payment-info/" + mBody.optString("store_id");
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = StoreUtil.getStoreViewPageIntent(mContext, mBody.optString("store_id"));
                viewIntent.putExtra("isStoreCreate", true);
                startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        mShippingMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addShippingURL = AppConstant.DEFAULT_URL + "sitestore/add-shipping-method/" + mBody.optString("store_id");
                Intent intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, addShippingURL);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.SHIPPING_METHOD);
                startActivityForResult(intent, ConstantVariables.VIEW_PAGE_EDIT_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editUrl = AppConstant.DEFAULT_URL + "sitestore/set-store-gateway-info/" + mBody.optString("store_id");
                mRedirectUrl = AppConstant.DEFAULT_URL + "sitestore/payment-info/" + mBody.optString("store_id");
                showValidationPopup(mContext, mRedirectUrl, editUrl);

            }
        });

        mAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRedirectUrl = AppConstant.DEFAULT_URL + "sitestore/product/create?store_id=" + mBody.optString("store_id");
                Intent intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                intent.putExtra(ConstantVariables.CREATE_URL, mRedirectUrl);
                intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.ADD_PRODUCT);
                startActivityForResult(intent, ConstantVariables.CREATE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        ImageView addProdcutIcon = findViewById(R.id.add_product_icon);
        addProdcutIcon.setColorFilter(getResources().getColor(R.color.colorPrimary));

        ImageView shippingMethodIcon = findViewById(R.id.shipping_method_icon);
        shippingMethodIcon.setColorFilter(getResources().getColor(R.color.colorPrimary));

        ImageView paymentMethodIcon = findViewById(R.id.payment_method_icon);
        paymentMethodIcon.setColorFilter(getResources().getColor(R.color.colorPrimary));


    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void showValidationPopup(final Context mContext, final String redirectUrl, final String editUrl) {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        final LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextInputLayout passwordWrapper = new TextInputLayout(mContext);
        passwordWrapper.setHint(mContext.getResources().getString(R.string.lbl_enter_password));
        final AppCompatEditText password = new AppCompatEditText(mContext);
        password.setGravity(Gravity.START | Gravity.TOP);
        password.setFocusableInTouchMode(true);
        password.setSingleLine(true);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint(mContext.getResources().getString(R.string.enter_password));
        passwordWrapper.addView(password);
        linearLayout.addView(passwordWrapper);
        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_15dp);
        linearLayout.setPadding(padding, padding, padding, 0);
        alertBuilder.setView(linearLayout);
        alertBuilder.setTitle(mContext.getResources().getString(R.string.validate_before_configuration));
        alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.continue_string), null);
        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);
        final AlertDialog mAlertDialog = alertBuilder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button continueButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!password.getText().toString().equals("")) {

                            Intent intent = new Intent(mContext, EditEntry.class);
                            intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PAYMENT_METHOD_CONFIG);
                            intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.PAYMENT_METHOD_CONFIG);
                            intent.putExtra(ConstantVariables.URL_STRING, redirectUrl + "?password=" + password.getText().toString());
                            intent.putExtra(ConstantVariables.EDIT_URL_STRING, editUrl + "?password=" + password.getText().toString());
                            startActivityForResult(intent, ConstantVariables.EDIT_ENTRY_RETURN_CODE);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            mAlertDialog.cancel();
                        }
                    }
                });
            }
        });
        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mAlertDialog.cancel();
            }
        });
        mAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantVariables.REQUEST_CANCLED) {
            return;
        }
        switch (requestCode) {
            case ConstantVariables.EDIT_ENTRY_RETURN_CODE:
                paymentMethodDone.setVisibility(View.VISIBLE);
                break;
            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                shippingMethodDone.setVisibility(View.VISIBLE);
                break;
        }
    }
}
