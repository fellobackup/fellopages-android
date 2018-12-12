package com.bigsteptech.realtimechat.interfaces;


import com.bigsteptech.realtimechat.conversation.data_model.Message;

public interface OnRetryClicked {

    public void onRetryViewClicked(Message message);
}
