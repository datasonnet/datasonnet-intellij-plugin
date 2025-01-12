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
    private boolean autoRefresh = true; // Default to true for backwards compatibility.

    public DataSonnetProjectSettings() {
        defaultTemplate = DEFAULT_TEMPLATE;
        dataSonnetLibraryPaths = new ArrayList<>();
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

    /**
     * Gets whether to automatically sync the DataSonnet mappings. The default is {@code false}.
     *
     * @return Whether to automatically sync the DataSonnet mappings.
     */
    public boolean getAutoRefresh() {
        return autoRefresh;
    }

    /**
     * Sets whether to automatically sync the DataSonnet mappings.
     *
     * @param autoRefresh Whether to automatically sync the DataSonnet mappings.
     */
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

}
