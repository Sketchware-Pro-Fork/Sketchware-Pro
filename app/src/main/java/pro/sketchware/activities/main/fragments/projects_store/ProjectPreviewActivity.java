package pro.sketchware.activities.main.fragments.projects_store;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.downloader.OnDownloadListener;
import com.downloader.Error;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.gson.Gson;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import pro.sketchware.BuildConfig;
import pro.sketchware.activities.main.fragments.projects_store.adapters.ProjectScreenshotsAdapter;
import pro.sketchware.activities.main.fragments.projects_store.api.ProjectModel;
import pro.sketchware.activities.main.fragments.projects_store.api.SketchHubAPI;
import pro.sketchware.databinding.FragmentStoreProjectPreviewBinding;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.UI;

public class ProjectPreviewActivity extends AppCompatActivity {
    private static final String PROJECT_DOWNLOAD_DIR = "Download/Sketchware/";
    private FragmentStoreProjectPreviewBinding binding;
    private ProjectModel.Project project;
    private SketchHubAPI sketchHubAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentStoreProjectPreviewBinding.inflate(getLayoutInflater());
        sketchHubAPI = new SketchHubAPI(BuildConfig.SKETCHUB_API_KEY);
        
        EdgeToEdge.enable(this);

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(binding.getRoot());

        binding.getRoot().setTransitionName("project_preview");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementEnterTransition(new MaterialContainerTransform().addTarget(binding.getRoot()).setDuration(400L));
        getWindow().setSharedElementReturnTransition(new MaterialContainerTransform().addTarget(binding.getRoot()).setDuration(400L));

        loadProjectData(getIntent().getExtras());

        initializeLogic();
    }

    private void initializeLogic() {
        binding.projectTitle.setText(project.getTitle());
        binding.projectAuthor.setText(project.getUserName());
        binding.projectDownloads.setText(project.getDownloads());
        binding.projectLikes.setText(project.getLikes());
        binding.projectComments.setText(project.getComments());
        binding.projectSize.setText(project.getProjectSize());
        binding.projectCategory.setText(project.getCategory());
        binding.projectDescription.setText(project.getDescription());
        binding.projectDescription.setMaxLines(4);
        binding.seeMore.setOnClickListener(v -> {
            if (binding.projectDescription.getMaxLines() == 4) {
                ChangeBounds changeBounds = new ChangeBounds();

                changeBounds.addTarget(binding.projectDescription);
                changeBounds.setDuration(300);
                TransitionManager.beginDelayedTransition(binding.getRoot(), changeBounds);

                binding.projectDescription.setMaxLines(Integer.MAX_VALUE);
                binding.seeMore.setText("See less");
            } else {
                ChangeBounds changeBounds = new ChangeBounds();

                changeBounds.addTarget(binding.projectDescription);
                changeBounds.setDuration(300);
                TransitionManager.beginDelayedTransition(binding.getRoot(), changeBounds);

                binding.projectDescription.setMaxLines(4);
                binding.seeMore.setText("See more");
            }
        });
        UI.loadImageFromUrl(binding.projectImage, project.getIcon());
        UI.loadImageFromUrl(binding.screenshot1, project.getScreenshot1());
        UI.loadImageFromUrl(binding.screenshot2, project.getScreenshot2());
        UI.loadImageFromUrl(binding.screenshot3, project.getScreenshot3());

        binding.collapsingToolbar.setTitle(project.getTitle());
        binding.collapsingToolbar.setExpandedTitleTextColor(ContextCompat.getColorStateList(this, android.R.color.transparent));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        ArrayList<String> screenshots = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            String screenshot = getScreenshot(i);
            if (screenshot != null && !screenshot.isEmpty()) {
                screenshots.add(screenshot);
            }
        }

        binding.projectScreenshots.setAdapter(new ProjectScreenshotsAdapter(screenshots));
        
        binding.btnDownloadProject.setOnClickListener(v -> {
            downloadProject(new DownloadListener() {
                @Override
                public void onComplete() {
                    SketchwareUtil.toast("downloaded successfully");
                }
                @Override
                public void onError(final Object error) {
                    if (error instanceof Exception e) {
                        SketchwareUtil.toastError(e.toString());
                    }
                }
                @Override
                public void onProgress(final int progress) {
                    SketchwareUtil.toast(String.valueOf(progress));
                }
            });
        });
    }

    private String getScreenshot(int index) {
        return switch (index) {
            case 0 -> project.getScreenshot1();
            case 1 -> project.getScreenshot2();
            case 2 -> project.getScreenshot3();
            case 3 -> project.getScreenshot4();
            case 4 -> project.getScreenshot5();
            default -> null;
        };
    }

    private void loadProjectData(Bundle bundle) {
        if (bundle != null) {
            String json = bundle.getString("project_json");
            project = new Gson().fromJson(json, ProjectModel.Project.class);
        }
    }

    private void downloadProject(DownloadListener downloadListener) {
        sketchHubAPI.getProjectDownloadUrl(project.getId(), projectDownloadUrlModel -> {
            if (projectDownloadUrlModel != null) {
                var downloadId = -1;
                var downloadPath = new File(getFilesDir(), PROJECT_DOWNLOAD_DIR);
                var config = PRDownloaderConfig.newBuilder()
                    .setDatabaseEnabled(true)
                    .build();
                PRDownloader.initialize(getWindow().getDecorView().getContext(), config); 
                if (Status.RUNNING == PRDownloader.getStatus(downloadId) ||  Status.PAUSED == PRDownloader.getStatus(downloadId)) return;
                
                try {
                    downloadId = PRDownloader.download(
                        projectDownloadUrlModel.getDownloadUrl(),
                        downloadPath.getAbsolutePath(),
                        project.getTitle())
                    .build()
                    .setOnProgressListener(progress -> {
                        var currentBytes = progress.currentBytes;
                        var totalBytes = progress.totalBytes;
                        var progressPercent = currentBytes * 100 / totalBytes;
                        downloadListener.onProgress((int) progressPercent);
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            downloadListener.onComplete();
                        }
                        @Override
                        public void onError(Error error) {
                            downloadListener.onError(error);
                        }
                    });
                } catch (Exception | OutOfMemoryError e) {
                    downloadListener.onError(e);
                }
            }
            SketchwareUtil.toastError("Something wrong with download url");
        });
    }
    
    private interface DownloadListener {
        void onComplete();
        void onError(final Object error);
        void onProgress(final int progress);
    }
}
