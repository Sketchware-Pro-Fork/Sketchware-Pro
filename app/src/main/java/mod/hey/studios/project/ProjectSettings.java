package mod.hey.studios.project;

import android.os.Environment;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashMap;

import mod.hey.studios.util.Helper;
import mod.jbk.util.LogUtil;

import pro.sketchware.utility.FileUtil;

public class ProjectSettings {

    private static final String TAG = "ProjectSettings";
    
    public static final String SETTING_GENERIC_VALUE_TRUE = "true";
    public static final String SETTING_GENERIC_VALUE_FALSE = "false";

    /** Setting for the final app's {@code minSdkVersion} */
    public static final String SETTING_MINIMUM_SDK_VERSION = "min_sdk";
    
    /** Setting for the final app's {@code targetSdkVersion} */
    public static final String SETTING_TARGET_SDK_VERSION = "target_sdk";
    
    /** Setting for the final app's {@link Application} class */
    public static final String SETTING_APPLICATION_CLASS = "app_class";
    
    /** Setting to enable view binding in the project */
    public static final String SETTING_VIEWBINDING = "enable_viewbinding";
    
    /** Setting to disable showing deprecated methods included in every generated class, e.g. showMessage(String) */
    public static final String SETTING_OLD_METHODS = "disable_old_methods";
    
    /** Setting to make the app's main theme inherit from fully material-styled themes, and not *.Bridge ones */
    public static final String SETTING_BRIDGELESS_THEMES = "enable_bridgeless_themes";
    
    /** Setting to make the app's main theme material design 3. */
    public static final String SETTING_MATERIAL3_THEME = "material3_theme";
    
    /** Setting to use new xml command */
    public static final String SETTING_NEW_XML_COMMAND = "xml_command";
    
    protected final String scId;
    
    private final String settingsPath;
    private HashMap<String, String> settingsMap;

    public ProjectSettings(final String scId) {
        this.scId = scId;
        settingsPath = new File(Environment.getExternalStorageDirectory(), ".sketchware/data/" + scId + "/project_config").getAbsolutePath();

        if (FileUtil.isExistFile(settingsPath)) {
            try {
                settingsMap = new Gson().fromJson(FileUtil.readFile(settingsPath).trim(), Helper.TYPE_STRING_MAP);
            } catch (Exception e) {
                LogUtil.e("ProjectSettings", "Failed to read project settings for project " + scId + "!", e);
                settingsMap = new HashMap<>();
                save();
            }
        } else {
            settingsMap = new HashMap<>();
        }
    }

    /**
     * @return The configured minimum SDK version. Returns 21 if none or an invalid value was set.
     *
     * @see #SETTING_MINIMUM_SDK_VERSION
     */
    public int getMinSdkVersion() {
        return Integer.parseInt(getValue(SETTING_MINIMUM_SDK_VERSION, "21"));
    }
    
    /**
     * @return Return true if Material Components Bridgeless is enable. false if no.
     *
     * @see #SETTING_BRIDGELESS_THEMES
     */
    public boolean isBridgelessThemeEnable() {
        return Boolean.parseBoolean(getValue(SETTING_BRIDGELESS_THEMES, "false"));
    }
    
    /**
     * @return Return true if Material Design 3 is enable. false if no.
     *
     * @see #SETTING_MATERIAL3_THEME
     */
    public boolean isMaterial3ThemeEnable() {
        return Boolean.parseBoolean(getValue(SETTING_MATERIAL3_THEME, "false"));
    }
    
    public String getValue(String key, String defaultValue) {
        if (settingsMap.containsKey(key)) {
            if (!settingsMap.get(key).isEmpty()) {
                return settingsMap.get(key);
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public void setValues(View... views) {
        for (View view : views) {
            if (view.getTag() != null) {
                String key = (String) view.getTag();
                String value;

                if (view instanceof EditText editText) {
                    value = editText.getText().toString();
                } else if (view instanceof Checkable checkable) {
                    value = checkable.isChecked() ? "true" : "false";
                } else if (view instanceof RadioGroup radioGroup) {
                    value = getCheckedRbValue(radioGroup);
                } else {
                    continue;
                }
                settingsMap.put(key, value);
            }
        }
        save();
    }

    public void setValue(String key, String value) {
        settingsMap.put(key, value);
        save();
    }

    private String getCheckedRbValue(RadioGroup rg) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rg.getChildAt(i);

            if (rb.isChecked()) {
                return rb.getText().toString();
            }
        }

        return "";
    }
    
    public String getPath() {
        return settingsPath;
    }

    private void save() {
        var gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        FileUtil.writeFile(settingsPath, gson.toJson(settingsMap));
    }
}
