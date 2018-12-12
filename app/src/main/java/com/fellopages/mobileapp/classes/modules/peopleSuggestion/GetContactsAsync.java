/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.modules.peopleSuggestion;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GetContactsAsync extends AsyncTask<Void, String, JSONObject> {

    //Member variables
    private Context mContext;
    private AppConstant mAppConst;
    private int mPageNumber = 1;
    private String mMemberShipRequestType;
    private OnContactLoadResponseListener mOnContactLoadResponseListener;

    /**
     * Public constructor to initialize member variables.
     *
     * @param context        Context of calling class.
     * @param membershipType Membership type for which member is to be loaded.
     * @param pageNumber     Page number for next page response.
     */
    public GetContactsAsync(Context context, String membershipType, int pageNumber) {
        this.mContext = context;
        this.mMemberShipRequestType = membershipType;
        mPageNumber = pageNumber;
        mAppConst = new AppConstant(mContext);

    }

    /**
     * Method to set ResponseListener.
     *
     * @param onContactLoadResponseListener Instance of OnContactLoadResponseListener.
     */
    public void setOnResponseListener(OnContactLoadResponseListener onContactLoadResponseListener) {
        this.mOnContactLoadResponseListener = onContactLoadResponseListener;
    }

    public interface OnContactLoadResponseListener {
        void onContactLoaded(JSONObject jsonObject, boolean isRequestSuccessful);
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject contactObject = new JSONObject();
        try {

            // When the request is for load more members then gettign the contacts from preferences.
            // Otherwise getting from  the contact manager.
            if (mPageNumber == 1 || (PreferencesUtils.getContactList(mContext).isEmpty() && mPageNumber > 1)) {

                // Creating Content resolver and contact Cursor.
                ContentResolver contResolver = mContext.getContentResolver();
                Cursor cur = contResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                // Null Check for the contact cursor.
                if (cur != null && cur.getCount() > 0) {
                    while (cur.moveToNext()) {

                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        int hasPhone = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        // get the user's email address.
                        String email = null;
                        Cursor emailCursor = contResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (emailCursor != null && emailCursor.moveToFirst()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            emailCursor.close();
                        }

                        // get the user's phoneNumber.
                        String phoneNumber = null;
                        if (hasPhone > 0) {
                            Cursor phoneCursor = contResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneCursor.close();
                            }
                        }

                        // Putting all the contacts in JSON which has the email id or mobile numbers.
                        try {
                            if (email != null && !email.isEmpty()) {
                                contactObject.put(email, name);
                            } else if (phoneNumber != null && !phoneNumber.isEmpty()) {
                                contactObject.put(phoneNumber, name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    cur.close();
                }
            } else if (!PreferencesUtils.getContactList(mContext).isEmpty()) {
                contactObject = new JSONObject(PreferencesUtils.getContactList(mContext));
            }

            LogUtils.LOGD(GetContactsAsync.class.getSimpleName(), "contactObject: " + contactObject);
            if (contactObject.length() > 0) {
                PreferencesUtils.setContactList(mContext, contactObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {

        try {
            if (result != null && result.length() > 0) {

                String postUrl = UrlUtil.MEMBERSHIP_DEFAULT_URL + "?page=" + mPageNumber + "&limit=" + AppConstant.LIMIT;
                Map<String, String> postParams = new HashMap<>();
                if (mMemberShipRequestType != null && mMemberShipRequestType.equals("contact")) {
                    postParams.put("membershipType", "add_friend");
                }
                postParams.put("emails", result.toString());

                mAppConst.postJsonResponseForUrl(postUrl, postParams, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        PreferencesUtils.setContactSyncedInfo(mContext, true);
                        mOnContactLoadResponseListener.onContactLoaded(jsonObject, true);
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        mOnContactLoadResponseListener.onContactLoaded(GlobalFunctions.getErrorJsonString(message), false);
                    }
                });

            } else {
                mOnContactLoadResponseListener.onContactLoaded(null, false);
            }
        } catch (Exception e) {
            mOnContactLoadResponseListener.onContactLoaded(null, false);
            e.printStackTrace();
        }
    }

}
