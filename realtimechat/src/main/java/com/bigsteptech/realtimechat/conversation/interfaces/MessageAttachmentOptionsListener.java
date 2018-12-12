package com.bigsteptech.realtimechat.conversation.interfaces;


public interface MessageAttachmentOptionsListener {

    public void onLocationPressed();
    public void onTakePhotoPressed();
    public void onPickImagePressed();

    /** Invoked when the option button pressed, If returned true the system wont show the option popup.*/
    public boolean onOptionButtonPressed();
}
