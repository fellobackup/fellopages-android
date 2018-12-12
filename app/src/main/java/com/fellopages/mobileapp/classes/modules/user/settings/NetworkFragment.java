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
 *
 */

package com.fellopages.mobileapp.classes.modules.user.settings;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.NetworkAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseUpdateListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnSuccessListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.NetworkList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends Fragment implements OnSuccessListener {

    private View mRootView;
    private String mNetworkUrl;
    private AppConstant mAppConst;
    private Context mContext;
    private ListView mNetworkListView;
    private SelectableTextView mNoAvailableMessage, mCountNetworks;
    private JSONArray mNetworkResponse;
    private List<NetworkList> mNetworkList;
    private NetworkAdapter mNetworkAdapter;
    private String mNetworkName;
    private boolean isJoined;
    private OnResponseUpdateListener mOnResponseUpdateListener;

    public NetworkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnResponseUpdateListener = (OnResponseUpdateListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_network, container, false);
        mNetworkList = new ArrayList<>();
        mContext = getActivity();
        mAppConst = new AppConstant(mContext);

        if (getArguments() != null) {
            mNetworkName = getArguments().getString(ConstantVariables.NETWORK_NAME);
            mNetworkUrl = getArguments().getString(ConstantVariables.URL_STRING);
            try {
                mNetworkResponse = new JSONArray(getArguments().getString(ConstantVariables.NETWORK_RESPONSE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mNetworkListView = (ListView) mRootView.findViewById(R.id.network_list);
        if (mNetworkName.equals("joined")) {
            isJoined = true;
        } else {
            isJoined = false;
        }
        mNetworkAdapter = new NetworkAdapter(mContext, R.layout.list_networks, mNetworkList, isJoined, this);
        mNetworkListView.setAdapter(mNetworkAdapter);
        mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

        addDataToList();

        return mRootView;
    }

    private void addDataToList() {
        if(mNetworkResponse != null && mNetworkResponse.length() > 0){
            for(int i = 0; i < mNetworkResponse.length(); i++){
                JSONObject networkInfo = mNetworkResponse.optJSONObject(i);
                int networkId = networkInfo.optInt("network_id");
                String networkTitle = networkInfo.optString("title");
                int memberCount = networkInfo.optInt("member_count");
                mNetworkList.add(new NetworkList(networkId, memberCount, networkTitle));
            }
            mNetworkAdapter.notifyDataSetChanged();
        } else {
            mNetworkListView.setVisibility(View.GONE);
            mNoAvailableMessage = (SelectableTextView) mRootView.findViewById(R.id.noAvailableMessage);
            mNoAvailableMessage.setVisibility(View.VISIBLE);

            String message;
            if (mNetworkName.equals("joined")) {
                message = mContext.getResources().getString(R.string.no_network_joined_yet);
            } else {
                message = mContext.getResources().getString(R.string.no_network_available_message);
            }

            mNoAvailableMessage.setText(message);
        }
    }

    /**
     * Method to make server call to get the user network response.
     */
    private void makeRequest() {
        if(mNetworkUrl != null){
            mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            mAppConst.getJsonResponseFromUrl(mNetworkUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if(jsonObject != null){
                        if (mOnResponseUpdateListener != null)
                            mOnResponseUpdateListener.onResponseUpdate(jsonObject);

                        if (mNetworkName.equals("joined")) {
                            mNetworkResponse = jsonObject.optJSONArray("joinedNetworks");
                        } else {
                            mNetworkResponse = jsonObject.optJSONArray("availableNetworks");
                        }
                        addDataToList();
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbarLongWithListener(mRootView, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    getActivity().finish();
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onSuccess() {
        // Making server call on successful operation only if the fragment is currently added to the activity.
        if (isAdded()) {
            mNetworkList.clear();
            mNetworkAdapter.notifyDataSetChanged();
            makeRequest();
        }
    }
}
