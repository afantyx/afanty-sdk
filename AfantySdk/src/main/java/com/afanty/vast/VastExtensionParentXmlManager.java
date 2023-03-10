// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;

import com.afanty.vast.utils.Preconditions;
import com.afanty.vast.utils.XmlUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This XML manager handles Extensions nodes, which may in turn contain Extension nodes.
 */
public class VastExtensionParentXmlManager {

    private static final String EXTENSION = "Extension";
    @NonNull
    private final Node mVastExtensionParentNode;

    VastExtensionParentXmlManager(@NonNull Node vastExtensionParentNode) {
        Preconditions.checkNotNull(vastExtensionParentNode);

        mVastExtensionParentNode = vastExtensionParentNode;
    }

    /**
     * If there are Extension sections, return their XML managers.
     *
     * @return The {@link VastExtensionXmlManager}s or an empty list if there are no Extension
     * nodes.
     */
    @NonNull
    List<VastExtensionXmlManager> getVastExtensionXmlManagers() {
        final List<VastExtensionXmlManager> vastExtensionXmlManagers = new
                ArrayList<VastExtensionXmlManager>();

        final List<Node> vastExtensionNodes =
                XmlUtils.getMatchingChildNodes(mVastExtensionParentNode, EXTENSION);
        if (vastExtensionNodes == null) {
            return vastExtensionXmlManagers;
        }

        for (Node vastExtensionNode : vastExtensionNodes) {
            vastExtensionXmlManagers.add(new VastExtensionXmlManager(vastExtensionNode));
        }

        return vastExtensionXmlManagers;
    }

}
