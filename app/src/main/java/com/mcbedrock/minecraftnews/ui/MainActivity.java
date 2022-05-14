package com.mcbedrock.minecraftnews.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.mcbedrock.minecraftnews.R;
import com.mcbedrock.minecraftnews.adapter.ChangelogsAdapter;
import com.mcbedrock.minecraftnews.adapter.MinecraftNewsAdapter;
import com.mcbedrock.minecraftnews.api.CustomDialogAPI;
import com.mcbedrock.minecraftnews.databinding.ActivityMainBinding;
import com.mcbedrock.minecraftnews.model.BaseModel;
import com.mcbedrock.minecraftnews.model.NewsModel;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final int RC_APP_UPDATE = 100;
    private AppUpdateManager mAppUpdateManager;
    private List<BaseModel> base_models;
    private List<NewsModel> news_models;
    private ChangelogsAdapter adapter;
    private MinecraftNewsAdapter minecraftNewsAdapter;
    private static final String NEWS_JSON = "https://www.minecraft.net/content/minecraft-net/_jcr_content.articles.grid";
    private static final String BEDROCK_JSON = "https://raw.githubusercontent.com/JVMFrog/Minecraft-Changeloges/master/Russian/bedrock-releases.json";
    private static final String BETA_JSON = "https://raw.githubusercontent.com/JVMFrog/Minecraft-Changeloges/master/Russian/bedrock-beta-and-preview.json";
    private static final String JAVA_JSON = "https://raw.githubusercontent.com/JVMFrog/Minecraft-Changeloges/master/Russian/java-realeses.json";
    private static final String SNAPSHOT_JSON = "https://raw.githubusercontent.com/JVMFrog/Minecraft-Changeloges/master/Russian/java-snapshots.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        DynamicColors.applyToActivitiesIfAvailable(getApplication());

        BottomAppBar();
        base_models = new ArrayList<>();
        news_models = new ArrayList<>();
        ParseNews(NEWS_JSON);
        binding.toolbar.setSubtitle("");

        //Play Core Update
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //mAppUpdateManager.registerListener(installStateUpdatedListener);
    }

    private void ParseChangelogs(String jsonURL) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonURL, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);

                    BaseModel model = new BaseModel();
                    model.setTitle(jsonObject.getString("title"));
                    model.setVersion(jsonObject.getString("version"));
                    model.setUrl_text(jsonObject.getString("changelogURL"));
                    model.setImage_url(jsonObject.getString("imageURL"));

                    base_models.add(model);
                }
                binding.recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new ChangelogsAdapter(getApplicationContext(), base_models);
                binding.recview.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
                new CustomDialogAPI()
                        .showErrorDialog(this, e.toString());
            }
        }, error -> {
            Log.d("tag", "OnErrorResponse" + error.getMessage());
            new CustomDialogAPI()
                    .showErrorDialog(this, error.toString());
        });

        queue.add(jsonArrayRequest);
    }

    private void ParseNews(String newsURL) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, newsURL, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("article_grid");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    NewsModel model = new NewsModel();
                    model.setTitle(jsonObject1.getJSONObject("default_tile").getString("title"));
                    model.setSub_header(jsonObject1.getJSONObject("default_tile").getString("sub_header"));
                    model.setImage_url(jsonObject1.getJSONObject("default_tile").getJSONObject("image").getString("imageURL"));
                    model.setArticle_url(jsonObject1.getString("article_url"));

                    news_models.add(model);
                }
                binding.recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                minecraftNewsAdapter = new MinecraftNewsAdapter(this, news_models);
                binding.recview.setAdapter(minecraftNewsAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                new CustomDialogAPI()
                        .showErrorDialog(this, e.toString());
            }
        }, error -> {
            Log.d("tag", "OnErrorResponse" + error.getMessage());
            new CustomDialogAPI()
                    .showErrorDialog(this, error.toString());
        });
        queue.add(jsonObjectRequest);
    }

    public void BottomAppBar() {

        MaterialButton btn_news, btn_bedrock, btn_beta_and_preview, btn_java, btn_snapshot;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_menu);

        binding.BottomAppBar.setNavigationOnClickListener(view1 -> bottomSheetDialog.show());

        binding.BottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.news:
                    base_models.clear();
                    news_models.clear();
                    binding.toolbar.setSubtitle("");
                    ParseNews(NEWS_JSON);
                    bottomSheetDialog.dismiss();
                    break;

                case R.id.about:
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    break;
            }
            return false;
        });

        btn_news = bottomSheetDialog.findViewById(R.id.BS_MinecraftNewsBtn);
        btn_bedrock = bottomSheetDialog.findViewById(R.id.BS_MinecraftBedrockBtn);
        btn_beta_and_preview = bottomSheetDialog.findViewById(R.id.BS_MinecraftBetaBtn);
        btn_java = bottomSheetDialog.findViewById(R.id.BS_MinecraftJavaBtn);
        btn_snapshot = bottomSheetDialog.findViewById(R.id.BS_MinecraftSnapshotBtn);

        btn_news.setOnClickListener(view -> {
            base_models.clear();
            news_models.clear();
            binding.toolbar.setSubtitle("");
            ParseNews(NEWS_JSON);
            bottomSheetDialog.dismiss();
        });

        btn_bedrock.setOnClickListener(view -> {
            base_models.clear();
            news_models.clear();
            binding.toolbar.setSubtitle(R.string.minecraft_bedrock_releases);
            ParseChangelogs(BEDROCK_JSON);
            bottomSheetDialog.dismiss();
        });

        btn_beta_and_preview.setOnClickListener(view -> {
            base_models.clear();
            news_models.clear();
            binding.toolbar.setSubtitle(R.string.minecraft_betas_and_previews);
            ParseChangelogs(BETA_JSON);
            bottomSheetDialog.dismiss();
        });

        btn_java.setOnClickListener(view -> {
            base_models.clear();
            news_models.clear();
            binding.toolbar.setSubtitle(R.string.minecraft_java_edition_releases);
            ParseChangelogs(JAVA_JSON);
            bottomSheetDialog.dismiss();
        });

        btn_snapshot.setOnClickListener(view -> {
            base_models.clear();
            news_models.clear();
            binding.toolbar.setSubtitle(R.string.minecraft_java_edition_snapshots);
            ParseChangelogs(SNAPSHOT_JSON);
            bottomSheetDialog.dismiss();
        });
    }

    //Play Core Update
    private final InstallStateUpdatedListener installStateUpdatedListener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showCompletedUpdate();
        }
    };

    //Play Core Update
    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.new_update_availability,
                Snackbar.LENGTH_SHORT);
        snackbar.setAction(R.string.update, v -> mAppUpdateManager.completeUpdate());
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Play Core Update
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}