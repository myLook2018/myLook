package com.mylook.mylook.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.mylook.mylook.R;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.profile.NewPasswordActivity;

public class DialogManager {

    public DialogManager() {

    }

    /**
     * @param context        Instance of an Activity
     * @param title          Title shown in the dialog
     * @param message        Message shown in the dialog
     * @param positiveButton Text in the positive button
     * @param negativeButton Text shown in the negative button
     * @return AlertDialog dialog
     */
    public static AlertDialog createLogoutDialog(final Context context, String title, String message, String positiveButton, String negativeButton) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        final AlertDialog alert = dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (paramDialogInterface, paramInt) -> {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Toast.makeText(context, "Cerraste sesión :(", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                })
                .setNegativeButton(negativeButton, (paramDialogInterface, paramInt) -> {

                }).create();
        alert.setOnShowListener(dialog1 -> {
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.grey));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.purple));
        });
        return alert;
    }

    /**
     * @param context        Instance of an Activity
     * @param title          Title shown in the dialog
     * @param message        Message shown in the dialog
     * @param positiveButton Text in the positive button
     * @param negativeButton Text shown in the negative button
     * @return AlertDialog dialog
     */
    public android.app.AlertDialog createChangePasswordDialog(
            final Context context,
            String title,
            String message,
            String positiveButton,
            String negativeButton) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context, R.style.AlertDialogTheme);

        final android.app.AlertDialog alert = dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (paramDialogInterface, paramInt) -> {
                    Intent intent = new Intent(context, NewPasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                })
                .setNegativeButton(negativeButton, (paramDialogInterface, paramInt) -> {

                }).create();
        alert.setOnShowListener(dialog1 -> {
            alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.purple));
            alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple));
        });
        return alert;
    }

    public android.app.AlertDialog succesfulChangedPassword(
            final Context context,
            String title,
            String message,
            String positiveButton) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context, R.style.AlertDialogTheme);

        final android.app.AlertDialog alert = dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (paramDialogInterface, paramInt) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }).create();
        alert.setOnShowListener(dialog1 -> {
            alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.purple));
            alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.purple));
        });
        return alert;
    }
}