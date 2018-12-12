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

package com.fellopages.mobileapp.classes.common.interfaces;

import com.fellopages.mobileapp.classes.modules.likeNComment.CommentList;

import org.json.JSONObject;

// Interface to be implemented by calling activity
    public interface OnCommentPostListener {
        /**
         * Method is called after getting response in onPostExecute() method.
         * @param response JsonObject of Server response.
         * @param isRequestSuccessful true if request is successfully completed.
         * @param commentList comment to be posted.
         */
        void onCommentPost(JSONObject response, boolean isRequestSuccessful, CommentList commentList);
    }