// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.vast.utils.Preconditions;
import com.afanty.vast.utils.XmlUtils;

import org.w3c.dom.Node;

/**
 * This XML manager handles Wrapper nodes. Wrappers redirect to other VAST documents (which may
 * in turn redirect to more wrappers). Wrappers can also contain impression trackers,
 * trackers for a video ad, and companion ads.
 */
class VastWrapperXmlManager extends VastBaseInLineWrapperXmlManager {

    // Element names
    private static final String VAST_AD_TAG = "VASTAdTagURI";

    VastWrapperXmlManager(@NonNull final Node wrapperNode) {
        super(wrapperNode);
        Preconditions.checkNotNull(wrapperNode);
    }

    /**
     * Gets the redirect URI to the next VAST xml document. If no redirect URL, return null.
     *
     * @return The redirect URI or {@code null} if there isn't one.
     */
    @Nullable
    String getVastAdTagURI() {
        Node vastAdTagURINode = XmlUtils.getFirstMatchingChildNode(mNode, VAST_AD_TAG);
        return XmlUtils.getNodeValue(vastAdTagURINode);
    }
}
