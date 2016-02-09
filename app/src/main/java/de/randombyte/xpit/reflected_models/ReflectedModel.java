package de.randombyte.xpit.reflected_models;

import de.robv.android.xposed.XposedHelpers;

/**
 * All models that represent a class of the target app should extend this class
 */
public class ReflectedModel {

    private final Object objectOfTargetApp;

    /**
     * @param objectOfTargetApp The original object of the target app
     */
    public ReflectedModel(Object objectOfTargetApp) {
        this.objectOfTargetApp = objectOfTargetApp;
    }

    /**
     * @return Original, maybe manipulated, object
     */
    public final Object getObject() {
        return objectOfTargetApp;
    }

    //Shadows of XposedHelpers.getXXXField()
    protected Object getObjectField(String fieldName) {
        return XposedHelpers.getObjectField(objectOfTargetApp, fieldName);
    }

    protected int getIntField(String fieldName) {
        return XposedHelpers.getIntField(objectOfTargetApp, fieldName);
    }

    //Shadows of XposedHelpers.setXXXField()
    protected void setObjectField(String fieldName, Object value) {
        XposedHelpers.setObjectField(objectOfTargetApp, fieldName, value);
    }
}
