package pro.sketchware.activities.editor.theme;

import static pro.sketchware.utility.SketchwareUtil.toast;
import static pro.sketchware.utility.SketchwareUtil.toastError;

import android.os.Bundle;
import android.os.Environment;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.besome.sketch.lib.base.BaseAppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mod.hey.studios.util.Helper;

import pro.sketchware.R;
import pro.sketchware.databinding.ActivityMaterialThemeEditorBinding;
import pro.sketchware.utility.FileUtil;

import a.a.a.aB;

public class MaterialThemeEditorActivity extends BaseAppCompatActivity {

    private ActivityMaterialThemeEditorBinding binding;
    private MaterialThemeEditorAdapter listAdapter;
    private List<ThemeItem> themeItems;

    @Nullable private String scId;

    private OnBackPressedCallback onBackPressedCallback =
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (isContentModified()) {
                        var dialog = new aB(MaterialThemeEditorActivity.this);
                        dialog.a(R.drawable.ic_mtrl_warning);
                        dialog.b(Helper.getResString(R.string.common_word_warning));
                        dialog.a(Helper.getResString(R.string.src_code_editor_unsaved_changes_dialog_warning_message));
                        dialog.b(Helper.getResString(R.string.common_word_exit),v -> {
                            dialog.dismiss();
                            saveAll();
                            finish();
                        });
                        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
                        dialog.show();
                    } else {
                        saveAll();
                        finish();
                    }
                }
            };

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMaterialThemeEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configureToolbar(binding.toolbar);
        loadData(savedInstanceState);
        configureList();
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void loadData(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            scId = getIntent().getStringExtra("sc_id");
        } else {
            scId = savedInstanceState.getString("sc_id");
        }
    }

    private void configureList() {
        listAdapter = new MaterialThemeEditorAdapter(
            (themeItem, index) -> onItemClick(themeItem, index),
            (themeItem, index) -> onItemLongClick(themeItem, index)
        );
        File themeFile;
        try {
            themeFile = getFile();
        } catch (IllegalArgumentException e) {
            toastError("Something wrong with project id");
            return;
        }
        if (FileUtil.isExistFile(themeFile.getAbsolutePath())) {
            themeItems = parseJson(FileUtil.readFile(themeFile.getAbsolutePath()));
        }
        binding.list.setAdapter(listAdapter);
        listAdapter.submitList(themeItems);
    }

    private List<ThemeItem> parseJson(final String json) {
        Type listType = new TypeToken<List<ThemeItem>>() {}.getType();
        return getGson().fromJson(json, listType);
    }

    private Gson getGson() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    private void onItemClick(final ThemeItem themeItem, final int index) {
        // todo
    }

    private void onItemLongClick(final ThemeItem themeItem, final int index) {
        // todo
    }

    private File getFile() {
        if (scId == null) throw new IllegalArgumentException("Project sc id cannot be null.");
        return new File(Environment.getExternalStorageDirectory(), ".sketchware/data/" + scId + "/theme/material_theme.json");
    }

    private boolean isContentModified() {
        File themeFile;
        List<ThemeItem> originalList = new ArrayList<>();
        try {
            themeFile = getFile();
        } catch (IllegalArgumentException e) {
            toastError("Something wrong with project id");
            return false;
        }
        if (FileUtil.isExistFile(themeFile.getAbsolutePath())) {
            originalList = parseJson(FileUtil.readFile(themeFile.getAbsolutePath()));
        }
        return !originalList.equals(themeItems);
    }

    private void saveAll() {
        File themeFile;
        try {
            themeFile = getFile();
        } catch (IllegalArgumentException e) {
            toastError("Something wrong with project id");
            return;
        }
        saveAll(themeFile.getAbsolutePath(), "Saved with success in" + themeFile.getAbsolutePath());
    }

    private void saveAll(final String path, final String message) {
        FileUtil.writeFile(
            path,
            getGson().toJson(themeItems)
        );
        toast(message);
    }

    @Override
    protected void configureToolbar(@NonNull final MaterialToolbar toolbar) {
        super.configureToolbar(toolbar);
        toolbar.setSubtitle(scId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}