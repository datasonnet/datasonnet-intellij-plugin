package io.portx.datasonnet.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSonnetProjectSettings implements Serializable {

    // TODO Make auto-sync configurable.
    // TODO Add default template for new file with custom content.
    private List<String> dataSonnetLibraryPaths;

    public DataSonnetProjectSettings() {
        dataSonnetLibraryPaths = new ArrayList<String>();
    }

    public List<String> getDataSonnetLibraryPaths() {
        return dataSonnetLibraryPaths;
    }

    public void setDataSonnetLibraryPaths(List<String> dataSonnetLibraryPaths) {
        this.dataSonnetLibraryPaths = dataSonnetLibraryPaths;
    }


}
