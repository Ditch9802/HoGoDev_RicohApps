package jp.co.ricoh.ssdk.sample.function.print.attribute.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;

public enum PageTray implements PrintRequestAttribute{
    

    AUTO("auto"),
    LARGE_CAPACITY("large_capacity"),
    MANUAL("manual"),
    TRAY1("tray1"),
    TRAY2("tray2"),
    TRAY3("tray3"),
    TRAY4("tray4"),
    TRAY5("tray5"),
    TRAY6("tray6"),
    TRAY7("tray7"),
    TRAY8("tray8"),
    TRAY9("tray9");
    
    private final String PAGE_TRAY = "paperTray";
    private String mPageTray;
    
    private PageTray(String val){
        this.mPageTray = val;
    }
    @Override
    public Class<?> getCategory() {
        // TODO Auto-generated method stub
        return getClass();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return PAGE_TRAY;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return mPageTray;
    }
    
    @Override
    public String toString() {
        return this.mPageTray;
    }

    
    private static volatile Map<String, PageTray> directory = null;
    private static Map<String, PageTray> getDirectory() {
        if(directory == null) {
            Map<String, PageTray> d = new HashMap<String, PageTray>();
            for(PageTray pageTray : values()) {
                d.put(pageTray.getValue().toString(), pageTray);
            }
            directory = d;
        }
        return directory;
    }

    public static PageTray fromString(String value) {
        return getDirectory().get(value);
    }

    public static List<PageTray> getSupportedValue(List<String> values) {
        if( values == null ) {
            return Collections.emptyList();
        }

        List<PageTray> list = new ArrayList<PageTray>();
        for(String value : values) {
            PageTray pageTray = fromString(value);
            if( pageTray != null ) {
                list.add(pageTray);
            }
        }

        return list;
    }
}
