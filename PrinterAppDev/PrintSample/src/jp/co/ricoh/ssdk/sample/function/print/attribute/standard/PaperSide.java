package jp.co.ricoh.ssdk.sample.function.print.attribute.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;

public enum PaperSide implements PrintRequestAttribute {

    ONE_SIDED("one_sided"),
    TOP_TO_TOP("top_to_top"),
    TOP_TO_BOTTOM("top_to_bottom"),
    MAGAZINE_LEFT("magazine_left"),
    MAGAZINE_RIGHT("magazine_right"),
    BOOKLET_LEFT("booklet_left"),
    BOOKLET_RIGHT("booklet_right");
    
    private final String PAPER_SIDE = "printSide";
    private String mPaperSide;
    private PaperSide(String val) {
        this.mPaperSide = val;
    }
    @Override
    public Class<?> getCategory() {
        // TODO Auto-generated method stub
        return getClass();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return PAPER_SIDE;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return mPaperSide;
    }

    @Override
    public String toString() {
        return this.mPaperSide;
    }
    
    private static volatile Map<String, PaperSide> directory = null;

    private static Map<String, PaperSide> getDirectory() {
        if(directory == null) {
            Map<String, PaperSide> d = new HashMap<String, PaperSide>();
            for(PaperSide paperSize : values()) {
                d.put(paperSize.getValue().toString(), paperSize);
            }
            directory = d;
        }
        return directory;
    }

    public static PaperSide fromString(String value) {
        return getDirectory().get(value);
    }

    public static List<PaperSide> getSupportedValue(List<String> values) {
        if( values == null ) {
            return Collections.emptyList();
        }

        List<PaperSide> list = new ArrayList<PaperSide>();
        for(String value : values) {
            PaperSide paperSize = fromString(value);
            if( paperSize != null ) {
                list.add(paperSize);
            }
        }

        return list;
    }
}
