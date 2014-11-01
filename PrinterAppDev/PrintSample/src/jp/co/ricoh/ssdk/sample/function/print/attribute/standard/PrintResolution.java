package jp.co.ricoh.ssdk.sample.function.print.attribute.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.ricoh.ssdk.sample.function.print.attribute.PrintRequestAttribute;

public enum PrintResolution implements PrintRequestAttribute {

    DPI_1200_1200_1("1200_1200_1"),
    DPI_1200_1200_2("1200_1200_2"),
    DPI_1200_600_1("1200_600_1"),
    DPI_200_200_1("200_200_1"),
    DPI_300_300_1("300_300_1"),
    DPI_400_400_1("400_400_1"),
    DPI_600_1200_1("600_1200_1"),
    DPI_600_600_1("600_600_1"),
    DPI_600_600_2("600_600_2"),
    DPI_600_600_4("600_600_4"),
    DPI_600_600_8("600_600_8");
    
    private final String PRINT_RESOLUTION = "printResolution";
    private String mPrintResolution;
    private PrintResolution(String val) {
        this.mPrintResolution = val;
    }

    @Override
    public Object getValue() {
        return mPrintResolution;
    }

    @Override
    public Class<?> getCategory() {
        return getClass();
    }

    @Override
    public String getName() {
        return PRINT_RESOLUTION;
    }

    @Override
    public String toString() {
        return this.mPrintResolution;
    }


    private static volatile Map<String, PrintResolution> directory = null;

    private static Map<String, PrintResolution> getDirectory() {
        if(directory == null) {
            Map<String, PrintResolution> d = new HashMap<String, PrintResolution>();
            for(PrintResolution printResolution : values()) {
                d.put(printResolution.getValue().toString(), printResolution);
            }
            directory = d;
        }
        return directory;
    }

    public static PrintResolution fromString(String value) {
        return getDirectory().get(value);
    }

    public static List<PrintResolution> getSupportedValue(List<String> values) {
        if( values == null ) {
            return Collections.emptyList();
        }

        List<PrintResolution> list = new ArrayList<PrintResolution>();
        for(String value : values) {
            PrintResolution printResolution = fromString(value);
            if( printResolution != null ) {
                list.add(printResolution);
            }
        }

        return list;
    }

}
