package com.w3engineers.unicef.telemesh.data.remote.model;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [23-Oct-2018 at 6:06 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [23-Oct-2018 at 6:06 PM].
 * * --> <Second Editor> on [23-Oct-2018 at 6:06 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [23-Oct-2018 at 6:06 PM].
 * * --> <Second Reviewer> on [23-Oct-2018 at 6:06 PM].
 * * ============================================================================
 **/
public class MessageText extends BaseMessage {

    private String textMessage;

    public String getTextMessage() {
        return textMessage;
    }

    public MessageText setTextMessage(String textMessage) {
        this.textMessage = textMessage;
        return this;
    }

}
