package pro.sketchware.managers.exclude;

import static pro.sketchware.utility.GsonUtils.getGson;

import android.os.Environment;
import com.google.gson.reflect.TypeToken;
import pro.sketchware.utility.FileUtil;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class BuiltInClassesExcludeManager {

    /** use these constants in the list to exclude */
    public static final String SKETCH_APPLICATION = "SketchApplication.java";
    public static final String SKETCH_LOGGER = "SketchLogger.java";
    public static final String SKETCHWARE_UTIL = "SketchwareUtil.java";
    public static final String FILE_UTIL = "FileUtil.java";
    public static final String DEBUG_ACTIVITY = "DebugActivity.java";
    public static final String REQUEST_NETWORK = "RequestNetwork.java";
    public static final String REQUEST_NETWORK_CONTROLLER = "RequestNetworkController.java";
    public static final String BLUETOOTH_CONNECT = "BluetoothConnect.java";
    public static final String BLUETOOTH_CONTROLLER = "BluetoothController.java";
    public static final String GOOGLE_MAP_CONTROLLER = "GoogleMapController.java";

    private final String scId;
    private List<String> excludedClasses = new ArrayList<>();

    public BuiltInClassesExcludeManager(final String scId) {
        this.scId = scId;
        if (!getFile().exists()) save(); // saves empty array json
        excludedClasses = get();
    }

    /**
     * Checks if the class exists in the excluded list.
     *
     * @param className Name of class to be checked, see consts.
     */
    public final boolean isPresent(final String className) {
        return excludedClasses.contains(className);
    }

    /** 
     * Add new excluded class in excluded list
     *
     * @param className Name of class to be added, see consts.
     */
    public final void add(final String className) {
        excludedClasses.add(className);
        save();
    }

    /** 
     * Remove class of excluded list
     *
     * @param className Name of class to be removed, see consts.
     */
    public final void remove(final String className) {
        excludedClasses.remove(className);
        save();
    }
    
    /** Read excluded classes from file */
    public final List<String> get() {
        var type = new TypeToken<List<String>>(){}.getType();
        return getGson().fromJson(FileUtil.readFile(getFile().getAbsolutePath()), type);
    }

    /** Saves excluded list in a file */
    public final void save() {
        var json = getGson().toJson(excludedClasses);
        FileUtil.writeFile(getFile().getAbsolutePath(), json);
    }

    /** The file where list are stored */
    public final File getFile() {
        return new File(Environment.getExternalStorageDirectory(), ".sketchware/" + "data/" + scId + "/excluded_classes");
    }
}
