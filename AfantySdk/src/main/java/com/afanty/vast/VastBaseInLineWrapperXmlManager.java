// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.vast.utils.Preconditions;
import com.afanty.vast.utils.XmlUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

abstract class VastBaseInLineWrapperXmlManager {

    // Element Names
    private static final String IMPRESSION_TRACKER = "Impression";
    private static final String COMPANION = "Companion";
    private static final String LINEAR = "Linear";
    private static final String CREATIVES = "Creatives";
    private static final String CREATIVE = "Creative";
    private static final String COMPANION_ADS = "CompanionAds";
    private static final String ERROR = "Error";
    private static final String EXTENSIONS = "Extensions";
    private static final String ADTITLE = "AdTitle";

    @NonNull
    protected final Node mNode;

    VastBaseInLineWrapperXmlManager(@NonNull Node node) {
        Preconditions.checkNotNull(node);
        mNode = node;
    }

    /**
     * Gets a list of impression trackers for this InLine node. If there are no trackers, return
     * an empty list.
     *
     * @return List of URLs of impression trackers
     */
    @NonNull
    List<VastTracker> getImpressionTrackers() {
        final List<Node> impressionNodes = XmlUtils.getMatchingChildNodes(mNode, IMPRESSION_TRACKER);

        List<VastTracker> impressionTrackers = new ArrayList<VastTracker>();
        for (Node impressionNode : impressionNodes) {
            String uri = XmlUtils.getNodeValue(impressionNode);
            if (!TextUtils.isEmpty(uri)) {
                impressionTrackers.add(new VastTracker(uri, ""));
            }
        }

        return impressionTrackers;
    }

    /**
     * Gets the error tracker associated with this node.
     *
     * @return The URL of the error tracker.
     */
    @NonNull
    List<VastTracker> getErrorTrackers() {
        final List<VastTracker> errorTrackers = new ArrayList<VastTracker>();
        final List<Node> errorNodes = XmlUtils.getMatchingChildNodes(mNode, ERROR);
        if (errorNodes == null) {
            return errorTrackers;
        }

        for (Node error : errorNodes) {
            final String tracker = XmlUtils.getNodeValue(error);
            if (!TextUtils.isEmpty(tracker)) {
                errorTrackers.add(new VastTracker(tracker, true, ""));
            }
        }
        return errorTrackers;
    }

    /**
     * If there is a Linear section with at least one linear creative, return its XML manager.
     *
     * @return The {@link VastLinearXmlManager}s or an empty list if there is no Linear child node.
     */
    @NonNull
    List<VastLinearXmlManager> getLinearXmlManagers() {
        final List<VastLinearXmlManager> linearXmlManagers = new ArrayList<VastLinearXmlManager>();
        final Node creativesNode = XmlUtils.getFirstMatchingChildNode(mNode, CREATIVES);
        if (creativesNode == null) {
            return linearXmlManagers;
        }

        // NOTE: there can only be one <Linear>, <CompanionAds>, OR <NonLinearAds> element
        // per creative node

        final List<Node> creativeNodes = XmlUtils.getMatchingChildNodes(creativesNode, CREATIVE);
        if (creativeNodes == null) {
            return linearXmlManagers;
        }

        for (Node creativeNode : creativeNodes) {
            Node linearNode = XmlUtils.getFirstMatchingChildNode(creativeNode, LINEAR);
            if (linearNode != null) {
                linearXmlManagers.add(new VastLinearXmlManager(linearNode));
            }
        }
        return linearXmlManagers;
    }

    /**
     * If there is a CompanionAds section with at least one CompanionAd, return its XML manager.
     *
     * @return The {@link VastCompanionAdXmlManager}s or an empty list if there are no
     * CompanionAds or Companion child node.
     */
    @NonNull
    List<VastCompanionAdXmlManager> getCompanionAdXmlManagers() {
        final List<VastCompanionAdXmlManager> companionAdXmlManagers = new
                ArrayList<VastCompanionAdXmlManager>();
        final Node creativesNode = XmlUtils.getFirstMatchingChildNode(mNode, CREATIVES);
        if (creativesNode == null) {
            return companionAdXmlManagers;
        }

        final List<Node> creativeNodes = XmlUtils.getMatchingChildNodes(creativesNode, CREATIVE);
        if (creativeNodes == null) {
            return companionAdXmlManagers;
        }

        for (Node creativeNode : creativeNodes) {
            final Node companionAds = XmlUtils.getFirstMatchingChildNode(creativeNode, COMPANION_ADS);
            if (companionAds == null) {
                continue;
            }

            List<Node> companionAdsNodes = XmlUtils.getMatchingChildNodes(companionAds, COMPANION);
            if (companionAdsNodes == null) {
                continue;
            }

            for (Node companionNode : companionAdsNodes) {
                companionAdXmlManagers.add(new VastCompanionAdXmlManager(companionNode));
            }
        }

        return companionAdXmlManagers;
    }

    /**
     * If there is an Extensions section with at least one Extension, return its XML manager.
     *
     * @return The {@link VastExtensionParentXmlManager} or null if there are no Extensions or
     * Extension child nodes.
     */
    @Nullable
    VastExtensionParentXmlManager getVastExtensionParentXmlManager() {
        Node vastExtensionsNode = XmlUtils.getFirstMatchingChildNode(mNode, EXTENSIONS);
        if (vastExtensionsNode == null) {
            return null;
        }

        return new VastExtensionParentXmlManager(vastExtensionsNode);
    }

    @NonNull
    String getTitle() {
        final Node titleNode = XmlUtils.getFirstMatchingChildNode(mNode, ADTITLE);
        final String title = XmlUtils.getNodeValue(titleNode);
        if (title == null) {
            return null;
        }

        if (title.trim().isEmpty()) {
            return null;
        }

        return title.trim();
    }
}
