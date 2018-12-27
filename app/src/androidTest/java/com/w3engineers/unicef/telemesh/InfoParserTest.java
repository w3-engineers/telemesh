package com.w3engineers.unicef.telemesh;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [27-Dec-2018 at 11:20 AM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [27-Dec-2018 at 11:20 AM].
 * * --> <Second Editor> on [27-Dec-2018 at 11:20 AM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [27-Dec-2018 at 11:20 AM].
 * * --> <Second Reviewer> on [27-Dec-2018 at 11:20 AM].
 * * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class InfoParserTest {

    //region constants

    //endregion constants

    //region helper fields

    //endregion helper fields

    InfoParser SUT;

    @Before
    public void setup() throws Exception {
        SUT = new InfoParser();
    }

    @Test
    public void testHelloText() {
        String testString = "Hello TeleMesh";
        assertEquals(testString, SUT.getHelloText());
    }

    @After
    public void tearDown() throws Exception {
    }

    //region helper methods

    //endregion helper methods

    //region helper classes

    //endregion helper classes

}