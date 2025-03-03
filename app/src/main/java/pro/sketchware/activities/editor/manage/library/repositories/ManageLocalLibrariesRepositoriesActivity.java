package pro.sketchware.activities.editor.manage.library.repositories;

import static pro.sketchware.utility.GsonUtils.getGson;

import android.os.Bundle;

import androidx.annotation.Nullable;

import a.a.a.wq;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.google.gson.reflect.TypeToken;

import pro.sketchware.R;
import pro.sketchware.databinding.ActivityManageLocalLibrariesRepositoriesBinding;
import pro.sketchware.utility.FileUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageLocalLibrariesRepositoriesActivity extends BaseAppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ActivityManageLocalLibrariesRepositoriesBinding binding;
    private List<Repository> repositories = new ArrayList<>();
    private RepositoriesAdapter repositoriesAdapter;

    public static final class Constants {
        public static final File REPOSITORIES_FILE = new File(wq.r(), "repositories.json");
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageLocalLibrariesRepositoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureTexts();
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        repositoriesAdapter =
                new RepositoriesAdapter(
                        index -> {
                            // todo edit repo
                        });
        executorService.execute(
                () -> {
                    repositories =
                            parseJson(FileUtil.readFile(Constants.REPOSITORIES_FILE.getAbsolutePath()));
                    runOnUiThread(() -> {
                        binding.list.setAdapter(repositoriesAdapter);
                        repositoriesAdapter.submitList(repositories);
                    });
                });
    }

    private List<Repository> parseJson(final String json) {
        final Type type = new TypeToken<List<Repository>>() {}.getType();
        return getGson().fromJson(json, type);
    }

    /** Define texts of views with translated strings from sketchware/localization/strings.xml */
    private void configureTexts() {
        binding.toolbar.setTitle(getTranslatedString(R.string.repositories));
        binding.createNew.setText(getTranslatedString(R.string.common_word_new));
    }
}
