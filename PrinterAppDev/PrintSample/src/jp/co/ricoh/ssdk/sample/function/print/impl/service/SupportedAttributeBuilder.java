/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.print.impl.service;

import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Copies;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PageTray;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PaperSide;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintColor;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintResolution;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Staple;
import jp.co.ricoh.ssdk.sample.function.print.supported.MaxMinSupported;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.printer.Capability;

import java.util.HashMap;
import java.util.Map;

/**
 * 指定したCapabilityオブジェクトから、SupportedAttributeを生成するためのクラスです。
 * The class to create supportedAttribute from specified capability object.
 */
public class SupportedAttributeBuilder {
    private SupportedAttributeBuilder() {
    }

    public static Map<Class<? extends PrintRequestAttribute>, Object> getSupportedAttribute(Capability cap) {
        if( cap == null ) return null;

        Map<Class<? extends PrintRequestAttribute>, Object> retList = new HashMap<Class<? extends PrintRequestAttribute>, Object>();

        if(cap.getCopiesRange() != null ) {
            retList.put(Copies.class, MaxMinSupported.getMaxMinSupported(cap.getCopiesRange()));
        }

        if(cap.getStapleList() != null) {
            retList.put(Staple.class, Staple.getSupportedValue(cap.getStapleList()));
        }

        if(cap.getPrintColorList() != null) {
            retList.put(PrintColor.class, PrintColor.getSupportedValue(cap.getPrintColorList()));
        }

        if(cap.getPaperTrayList() != null) {
            retList.put(PageTray.class, PageTray.getSupportedValue(cap.getPaperTrayList()));
        }
        
        if(cap.getPrintSideList() != null) {
            retList.put(PaperSide.class, PaperSide.getSupportedValue(cap.getPrintSideList()));
        }
        if(cap.getPrintResolutionList() != null) {
            retList.put(PrintResolution.class, PrintResolution.getSupportedValue(cap.getPrintResolutionList()));
        }

        return retList;
    }
}
