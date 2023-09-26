package io.portx.datasonnet.config;

import java.io.Serializable;

public class DataSonnetSettings implements Serializable {

    private String dataSonnetExecPath;
    private boolean isBuiltInParser = true;
    private boolean isExtVars = true;

    public String getDataSonnetExecPath() {
        return dataSonnetExecPath;
    }

    public void setDataSonnetExecPath(String dataSonnetExecPath) {
        this.dataSonnetExecPath = dataSonnetExecPath;
    }

    public boolean isBuiltInParser() {
        return isBuiltInParser;
    }

    public void setBuiltInParser(boolean builtInParser) {
        isBuiltInParser = builtInParser;
    }

    public boolean isExtVars() {
        return isExtVars;
    }

    public void setExtVars(boolean extVars) {
        isExtVars = extVars;
    }
}
