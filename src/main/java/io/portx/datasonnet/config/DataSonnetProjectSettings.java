package io.portx.datasonnet.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSonnetProjectSettings implements Serializable {

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
