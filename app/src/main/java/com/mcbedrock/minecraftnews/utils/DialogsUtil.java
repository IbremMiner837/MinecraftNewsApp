package com.mcbedrock.minecraftnews.utils;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mcbedrock.minecraftnews.R;

public class DialogsUtil {

    public void showErrorDialog(Context context, String error) {
        new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.Theme_AppTheme_MaterialAlertDialog))
                .setIcon(R.drawable.ic_round_error_outline_24)
                .setTitle(R.string.error)
                .setMessage(error)
                .setNegativeButton(R.string.copy, (dialogInterface, i) -> {
                    new OtherAPI().copyTextToClipboard(context, error);
                })
                .setPositiveButton(R.string.OK, null)
                .show();
    }

    public void showLinkDialog(Context context, String url) {
        new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.Theme_AppTheme_MaterialAlertDialog))
                //.setIcon(R.drawable.ic_link_24)
                .setTitle(R.string.link)
                .setMessage(url)
                .setNegativeButton(R.string.copy_link, (dialogInterface, i) -> {
                    new OtherAPI().copyTextToClipboard(context, url);
                })
                .setPositiveButton(R.string.open_link, ((dialogInterface, i) -> {
                    new CustomTabUtil().OpenCustomTab(context, url, context.getColor(R.color.design_default_color_on_primary));
                }))
                .show();
    }

    public void deletingTranslationModelDone(Context context) {
        new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.Theme_AppTheme_MaterialAlertDialog))
                .setIcon(R.drawable.ic_round_done_24)
                .setTitle(R.string.translation_model_removed)
                .setMessage(context.getString(R.string.model) + " " + context.getString(R.string.was_deleted))
                .setPositiveButton(R.string.OK, null)
                .show();
    }

    public void deleteTranslationModel(Context context) {
        new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.Theme_AppTheme_MaterialAlertDialog))
                .setIcon(R.drawable.ic_round_delete_24)
                .setTitle(context.getString(R.string.delete_translation_model_title) + "?")
                .setMessage(R.string.delete_translation_model_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                    MLKitTranslationUtils.deleteModelTranslateRemoteModel();
                })
                .show();
    }

    public void downloadTranslateModel(Context context) {
        new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.Theme_AppTheme_MaterialAlertDialog))
                .setIcon(R.drawable.ic_round_download_24)
                .setTitle(context.getString(R.string.download_translation_model_title) + "?")
                .setMessage(R.string.download_translation_model_message)
                .setPositiveButton(R.string.OK, ((dialogInterface, i) -> {
                    MLKitTranslationUtils.downloadModel();
                }))
                .setNegativeButton("Позже", null)
                .show();
    }

    public void translateModelDownloaded(Activity context) {
        new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.Theme_AppTheme_MaterialAlertDialog))
                .setIcon(R.drawable.ic_round_download_done_24)
                .setTitle(context.getString(R.string.downloaded_translation_model_title))
                .setMessage(R.string.downloaded_translation_model_message)
                .setPositiveButton(R.string.OK, ((dialogInterface, i) -> {
                    context.recreate();
                }))
                .show();
    }
}
