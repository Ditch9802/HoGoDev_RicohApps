package jp.co.ricoh.ssdk.sample.function.print.attribute.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;

public enum PageSource implements PrintRequestAttribute{
    

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
    
    private final String PAGE_SOURCE = "pageSource";
    private String mPageSource;
    
    private PageSource(String val){
        this.mPageSource = val;
    }
    @Override
    public Class<?> getCategory() {
        // TODO Auto-generated method stub
        return getClass();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return PAGE_SOURCE;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return mPageSource;
    }
    
    @Override
    public String toString() {
        return this.mPageSource;
    }

    
    private static volatile Map<String, PageSource> directory = null;
    private static Map<String, PageSource> getDirectory() {
        if(directory == null) {
            Map<String, PageSource> d = new HashMap<String, PageSource>();
            for(PageSource pageSource : values()) {
                d.put(pageSource.getValue().toString(), pageSource);
            }
            directory = d;
        }
        return directory;
    }

    public static PageSource fromString(String value) {
        return getDirectory().get(value);
    }

    public static List<PageSource> getSupportedValue(List<String> values) {
        if( values == null ) {
            return Collections.emptyList();
        }

        List<PageSource> list = new ArrayList<PageSource>();
        for(String value : values) {
            PageSource pageSource = fromString(value);
            if( pageSource != null ) {
                list.add(pageSource);
            }
        }

        return list;
    }
}
