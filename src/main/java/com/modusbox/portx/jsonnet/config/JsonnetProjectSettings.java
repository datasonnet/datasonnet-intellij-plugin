package com.modusbox.portx.jsonnet.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonnetProjectSettings implements Serializable {

    private List<String> jsonnetLibraryPaths;

    public JsonnetProjectSettings() {
        jsonnetLibraryPaths = new ArrayList<String>();
    }

    public List<String> getJsonnetLibraryPaths() {
        return jsonnetLibraryPaths;
    }

    public void setJsonnetLibraryPaths(List<String> jsonnetLibraryPaths) {
        this.jsonnetLibraryPaths = jsonnetLibraryPaths;
    }


}
