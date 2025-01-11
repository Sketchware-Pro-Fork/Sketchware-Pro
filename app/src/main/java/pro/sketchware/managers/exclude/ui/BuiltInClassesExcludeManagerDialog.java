package pro.sketchware.managers.exclude.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.databinding.DialogBuiltinClassesExcludeManagerBinding;
import pro.sketchware.managers.exclude.BuiltInClassesExcludeManager;

public class BuiltInClassesExcludeManagerDialog extends BottomSheetDialogFragment {

    public static final List<String> classes = List.of(
        BuiltInClassesExcludeManager.SKETCH_APPLICATION,
        BuiltInClassesExcludeManager.SKETCH_LOGGER,
        BuiltInClassesExcludeManager.SKETCHWARE_UTIL,
        BuiltInClassesExcludeManager.FILE_UTIL,
        BuiltInClassesExcludeManager.DEBUG_ACTIVITY,
        BuiltInClassesExcludeManager.REQUEST_NETWORK,
        BuiltInClassesExcludeManager.REQUEST_NETWORK_CONTROLLER,
        BuiltInClassesExcludeManager.BLUETOOTH_CONNECT,
        BuiltInClassesExcludeManager.BLUETOOTH_CONTROLLER,
        BuiltInClassesExcludeManager.GOOGLE_MAP_CONTROLLER
    );

    private final BuiltInClassesExcludeManager builtInClassExcludeManager;
    private DialogBuiltinClassesExcludeManagerBinding binding;

    public BuiltInClassesExcludeManagerDialog(final String scId) {
        builtInClassExcludeManager = new BuiltInClassesExcludeManager(scId);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        binding = DialogBuiltinClassesExcludeManagerBinding.inflate(inflater, container, false);
        initializeLogic();
        return binding.getRoot();
    }

    private void initializeLogic() {
        var adapter = new BuiltInClassesExcludeManagerDialogAdapter(classes);
        adapter.setBuiltInClassesExcludeManager(builtInClassExcludeManager);
        adapter.setItemCheckedChange((isChecked, className) -> {
            if (isChecked) {
                builtInClassExcludeManager.add(className);
            } else {
                builtInClassExcludeManager.remove(className);
            }
        });
        var dividerItemDecoration = new DividerItemDecoration(binding.list.getContext(), LinearLayoutManager.VERTICAL);
        binding.list.addItemDecoration(dividerItemDecoration);
        binding.list.setAdapter(adapter);
    }
}