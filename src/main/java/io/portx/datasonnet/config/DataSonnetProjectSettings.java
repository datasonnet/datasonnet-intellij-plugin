package io.portx.datasonnet.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSonnetProjectSettings implements Serializable {

    /**
     * The default template for a new DataSonnet file.
     */
    private static final String DEFAULT_TEMPLATE = """
            /** DataSonnet
            version=2.5
            default null
            */
            
            {}
            """;

    private String defaultTemplate;
    private List<String> dataSonnetLibraryPaths;

    public DataSonnetProjectSettings() {
        defaultTemplate = DEFAULT_TEMPLATE;
        dataSonnetLibraryPaths = new ArrayList<String>();
    }

    /**
     * Get the default template for a new DataSonnet file.
     *
     * @return the default template
     */
    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    /**
     * Set the default template for a new DataSonnet file.
     *
     * @param defaultTemplate the new default template
     */
    public void setDefaultTemplate(final String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public List<String> getDataSonnetLibraryPaths() {
        return dataSonnetLibraryPaths;
    }

    public void setDataSonnetLibraryPaths(List<String> dataSonnetLibraryPaths) {
        this.dataSonnetLibraryPaths = dataSonnetLibraryPaths;
    }


}
