// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;

import com.afanty.vast.utils.Preconditions;
import com.afanty.vast.utils.XmlUtils;

import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Object for AVID's VAST Custom Extension.
 */
class AvidBuyerTagXmlManager {
    private static final String AD_VERIFICATIONS = "AdVerifications";
    private static final String VERIFICATION = "Verification";
    private static final String JAVA_SCRIPT_RESOURCE = "JavaScriptResource";

    private final Node mAvidNode;

    AvidBuyerTagXmlManager(@NonNull final Node avidNode) {
        Preconditions.checkNotNull(avidNode);

        mAvidNode = avidNode;
    }

    /**
     * Return the corresponding javascript resources for the AVID-related Extension node.
     * <p>
     * Expected Extension node:
     * <Extension>
     * <AVID>
     * <AdVerifications>
     * <Verification>
     * <JavaScriptResource>
     * <![CDATA[
     * https://temp.avid.com/pixel.gif?avidExtension
     * ]]>
     * </JavaScriptResource>
     * </Verification>
     * </AdVerifications>
     * </AVID>
     * </Extension>
     *
     * @return Collection of JavaScriptResources in string form, i.e.
     * [https://temp.avid.com/pixel.gif?avidExtension]
     */
    @NonNull
    Set<String> getJavaScriptResources() {
        final Set<String> avidJavaScriptResources = new HashSet<String>();

        final Node adVerification = XmlUtils.getFirstMatchingChildNode(mAvidNode, AD_VERIFICATIONS);
        if (adVerification == null) {
            return avidJavaScriptResources;
        }

        final List<Node> verifications = XmlUtils.getMatchingChildNodes(adVerification, VERIFICATION);
        if (verifications == null) {
            return avidJavaScriptResources;
        }

        for (final Node verification : verifications) {
            final Node javaScriptResource = XmlUtils.getFirstMatchingChildNode(verification,
                    JAVA_SCRIPT_RESOURCE);
            if (javaScriptResource != null) {
                avidJavaScriptResources.add(XmlUtils.getNodeValue(javaScriptResource));
            }
        }

        return avidJavaScriptResources;
    }
}
