package com.justbeatit.smartguide.text;

/**
 * Created by Dominik.Czerwinski on 2017-06-09.
 */

public interface Messenger {

    void sendMessage(String message);
    void setMode(MessengerImpl.DisablitityType mode);

}
