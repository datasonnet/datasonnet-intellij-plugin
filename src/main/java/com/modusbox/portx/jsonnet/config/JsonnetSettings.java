package com.modusbox.portx.jsonnet.config;

import java.io.Serializable;

public class JsonnetSettings implements Serializable {

    private String jsonnetExecPath;
    private boolean isBuiltInParser = true;
    private boolean isExtVars = true;

    public String getJsonnetExecPath() {
        return jsonnetExecPath;
    }

    public void setJsonnetExecPath(String jsonnetExecPath) {
        this.jsonnetExecPath = jsonnetExecPath;
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
