package com.catl.line.entity;

import java.util.Comparator;

import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;

public class LayoutComparator implements Comparator<LayoutDefinitionReadView> {

        @Override
        public int compare(LayoutDefinitionReadView layout1,
                LayoutDefinitionReadView layout2) {
            try {

                // Fetch the display name
                Long id1 = layout1.getReadViewIdentifier()
                        .getContextIdentifier().getId();
                TypeDefinitionReadView readView1 = TypeDefinitionServiceHelper.service
                        .getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id1);
                String displayName1 = PropertyHolderHelper.getDisplayName(
                        readView1, SessionHelper.getLocale());

                Long id2 = layout2.getReadViewIdentifier()
                        .getContextIdentifier().getId();
                TypeDefinitionReadView readView2 = TypeDefinitionServiceHelper.service
                        .getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id2);
                String displayName2 = PropertyHolderHelper.getDisplayName(
                        readView2, SessionHelper.getLocale());

                if (displayName1 != null && displayName2 != null) {
                    return displayName1.toLowerCase().compareTo(
                            displayName2.toLowerCase());
                }

            } catch (WTException e) {
            }

            return 0;
        }

    }