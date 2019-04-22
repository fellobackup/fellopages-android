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

package com.bigsteptech.realtimechat.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.bigsteptech.realtimechat.R;


public class AlertDialogWithAction {

    private Context mContext;
    private AlertDialog.Builder mDialogBuilder;

    public AlertDialogWithAction(Context context) {
        this.mContext = context;
        mDialogBuilder = new AlertDialog.Builder(mContext);
    }


    /**
     * Method to show alert dialog on respective permission result.
     * @param permissionType Permission type for which manifest permission will be requested.
     * @param requestCode Request code for respective permission.
     */
    public void showDialogForAccessPermission(final String permissionType,
                                                final int requestCode) {

        String message;
        switch (permissionType) {

            case Manifest.permission.READ_EXTERNAL_STORAGE:
                message = mContext.getResources().getString(R.string.allow_read_external_storage);
            break;

            case Manifest.permission.CAMERA:
                message = mContext.getResources().getString(R.string.allow_camera_permission);
            break;

            default:
                message = mContext.getResources().getString(R.string.allow_write_external_storage);
            break;
        }

        mDialogBuilder.setMessage(message);

        mDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionsUtils.requestForManifestPermission(mContext, permissionType, requestCode);

                    }
                });
        mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);

        mDialogBuilder.create().show();
    }

}
