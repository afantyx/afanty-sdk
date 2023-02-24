package com.afanty.request;

import com.afanty.internal.internal.AdRequestListener;

public abstract class BaseRTBRequest {

    protected String mTagId;

    public BaseRTBRequest(){

    }

    public BaseRTBRequest(String tagId){
        mTagId = tagId;
    }

    public void startLoad(AdRequestListener adRequestListener){
    }
}
