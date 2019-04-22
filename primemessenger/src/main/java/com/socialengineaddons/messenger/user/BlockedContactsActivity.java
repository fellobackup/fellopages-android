package com.socialengineaddons.messenger.user;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.contacts.ContactsListFragment;
import com.socialengineaddons.messenger.contacts.data_model.ContactsList;
import com.socialengineaddons.messenger.user.interfaces.OnContactSelectListener;

public class BlockedContactsActivity extends AppCompatActivity implements OnContactSelectListener{

    private Fragment fragment;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        mContext =  this;
        initViews();
    }

    private void initViews() {

        findViewById(R.id.imageTitleView).setVisibility(View.GONE);
        findViewById(R.id.addMembersView).setVisibility(View.GONE);
        findViewById(R.id.addMembers).setVisibility(View.GONE);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean("blockedContacts", true);
        fragment = ContactsListFragment.newInstance(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        ((ContactsListFragment)fragment).setmContactSelectedListener(this);

    }

    @Override
    public void onContactSelected(final ContactsList user) {

        // Show AlertDialogue for Unblock Option and Unblock User on Clicking on Unblock

        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().
                getDimension(R.dimen.body_medium_font_size));
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        textView.setText(String.format(mContext.getResources().
                        getString(R.string.unblock_user),
                mContext.getResources().getString(R.string.unblock), user.getmUserTitle()
        ));
        int margin_30dp = (int) mContext.getResources().getDimension(R.dimen.margin_25dp);
        textView.setPadding(margin_30dp, margin_30dp, margin_30dp, margin_30dp);
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((ContactsListFragment)fragment).unBlockSelectedMember(user.getmUserId());
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
