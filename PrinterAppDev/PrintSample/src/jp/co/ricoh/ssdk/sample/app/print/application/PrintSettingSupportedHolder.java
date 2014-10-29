/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.app.print.application;

import jp.co.ricoh.ssdk.sample.function.print.PrintFile;
import jp.co.ricoh.ssdk.sample.function.print.PrintService;
import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Copies;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PageTray;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PaperSide;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintColor;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.PrintResolution;
import jp.co.ricoh.ssdk.sample.function.print.attribute.standard.Staple;
import jp.co.ricoh.ssdk.sample.function.print.supported.MaxMinSupported;

import java.util.List;
import java.util.Set;

/**
 * 印刷にあたり、指定可能な設定値の一覧を保持するクラスです。
 * The class saves the list of supported print setting values.
 */
public class PrintSettingSupportedHolder {

    private Set<Class<? extends PrintRequestAttribute>> mSelectableCategories;

    private List<Staple> mSelectableStapleLabelList = null;
    private MaxMinSupported mSelectableCopiesRange = null;
    private List<PrintColor> mSelectablePrintColorList = null;
    private List<PageTray> mSelectablePageTrayList = null;
    private List<PaperSide> mSelectablePaperSideList = null;
    private List<PrintResolution> mSelectablePrintResolutionList = null;
    /**
     * コンストラクタ
     * [処理内容]
     *  機器から指定可能な属性一覧を取得し、保持します。
     *
     *  Constructor.
     *  [Processes]
     *    Obtains the list of supported attributes and saves them.
     *
     * @param service プリントサービスへの参照
     *                Reference to the print service.
     * @param pdl PDL
     */
    public PrintSettingSupportedHolder(PrintService service, PrintFile.PDL pdl) {
        mSelectableCategories = getSelectableCategory(service, pdl);
        if (mSelectableCategories != null) {
            if(mSelectableCategories.contains(Staple.class)) {
                mSelectableStapleLabelList = getStapleListFromSupportedValue(service, pdl);
            }

            if(mSelectableCategories.contains(Copies.class)) {
                mSelectableCopiesRange = getCopiesRangeFromSupportedValue(service, pdl);
            }

            if(mSelectableCategories.contains(PrintColor.class)) {
                mSelectablePrintColorList = getPrintColorListFromSupportedValue(service, pdl);
            }
            
            if(mSelectableCategories.contains(PageTray.class)) {
                mSelectablePageTrayList = getPageTrayListFromSupportedValue(service, pdl);
            }
            if(mSelectableCategories.contains(PaperSide.class)) {
                mSelectablePaperSideList = getPaperSideListFromSupportedValue(service, pdl);
            }
            if(mSelectableCategories.contains(PrintResolution.class)) {
                mSelectablePrintResolutionList = getPrintResolutionListFromSupportedValue(service, pdl);
            }
        }
    }

    /***********************************************************
     * 公開メソッド
     * Public methods
     ***********************************************************/

    /**
     * 指定可能なステープル設定値のリストを取得します。
     * Get the list of supported staple setting values.
     * @return
     */
    public List<Staple> getSelectableStapleList() {
        return mSelectableStapleLabelList;
    }

    /**
     * 指定可能な部数設定値の範囲を取得します。
     * Get the range of the number of copies.
     * @return
     */
    public MaxMinSupported getSelectableCopiesRange() {
        return mSelectableCopiesRange;
    }

    /**
     * 指定可能な属性カテゴリのセットを取得します。
     * Get the set of the supported print attribute setting categories.
     * @return
     */
    public Set<Class<? extends PrintRequestAttribute>> getSelectableCategories() {
        return this.mSelectableCategories;
    }

    /**
     * 指定可能な印刷カラー設定値のリストを取得します。
     * Get the list of supported print color setting values.
     * @return
     */
    public List<PrintColor> getSelectablePrintColorList() {
        return mSelectablePrintColorList;
    }
    public List<PageTray> getSelectablePageTrayList() {
        return mSelectablePageTrayList;
    }
    
    public List<PaperSide> getSelectablePaperSideList() {
        return mSelectablePaperSideList;
    }
    public List<PrintResolution> getSelectablePrintResolutionList() {
        return mSelectablePrintResolutionList;
    }

    /***********************************************************
     * 内部メソッド
     ***********************************************************/

    /**
     * サービスから指定可能なステープル設定値のリストを取得します。
     * Obtains the list of the supported staple setting from the service.
     * @param service
     * @param pdl
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Staple> getStapleListFromSupportedValue(PrintService service, PrintFile.PDL pdl) {
        return (List<Staple>)service.getSupportedAttributeValues(pdl, Staple.class);
    }

    /**
     * サービスから指定可能な部数設定値の範囲を取得します。
     * Obtains the range of the number of copies from the service.
     * @param service
     * @param pdl
     * @return
     */
    private MaxMinSupported getCopiesRangeFromSupportedValue(PrintService service, PrintFile.PDL pdl) {
        return (MaxMinSupported)service.getSupportedAttributeValues(pdl, Copies.class);
    }

    /**
     * サービスから指定可能な属性カテゴリのリストを取得します。
     * Obtains the list of the supported attribute categories from the service.
     * @param service
     * @param pdl
     * @return
     */
    private Set<Class<? extends PrintRequestAttribute>> getSelectableCategory(PrintService service, PrintFile.PDL pdl){
        return service.getSupportedAttributeCategories(pdl);
    }

    /**
     * サービスから指定可能な印刷カラー設定値のリストを取得します。
     * Obtains the list of the supported print color setting from the service.
     * @param service
     * @param pdl
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<PrintColor> getPrintColorListFromSupportedValue(PrintService service, PrintFile.PDL pdl) {
        return (List<PrintColor>)service.getSupportedAttributeValues(pdl, PrintColor.class);
    }
    
    @SuppressWarnings("unchecked")
    private List<PageTray> getPageTrayListFromSupportedValue(PrintService service, PrintFile.PDL pdl) {
        return (List<PageTray>)service.getSupportedAttributeValues(pdl, PageTray.class);
    }
    
    @SuppressWarnings("unchecked")
    private List<PaperSide> getPaperSideListFromSupportedValue(PrintService service, PrintFile.PDL pdl) {
        return (List<PaperSide>)service.getSupportedAttributeValues(pdl, PaperSide.class);
    }
    @SuppressWarnings("unchecked")
    private List<PrintResolution> getPrintResolutionListFromSupportedValue(PrintService service, PrintFile.PDL pdl) {
        return (List<PrintResolution>)service.getSupportedAttributeValues(pdl, PrintResolution.class);
    }

}
