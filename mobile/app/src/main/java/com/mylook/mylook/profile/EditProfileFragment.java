package com.mylook.myapp.Window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.Toast;

import com.mylook.myapp.R;

import java.util.regex.Pattern;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private TextInputLayout tilName;
    private TextInputLayout tilPhone;
    private TextInputLayout tilMail;
    private TextInputLayout tilLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_edit_profile,container,false);


        tilName = (TextInputLayout) view.findViewById(R.id.til_nombre);
        tilPhone = (TextInputLayout) view.findViewById(R.id.til_phone);
        tilMail = (TextInputLayout) view.findViewById(R.id.til_mail);
        tilLocation = (TextInputLayout) view.findViewById(R.id.til_location);


        //setup backarrow for navigating to AccountSettings
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'account settings'");
                getActivity().finish(); //toma la actividad accountsetting y la termina, por eso vuleve a la window activity

            }
        });

        ImageView accentMark = (ImageView) view.findViewById(R.id.check);
        accentMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataValidation()){
                    Log.d(TAG, "onClick: Se puede modicar el perfil.");
                }else {

                    Log.d(TAG, "onClick: No se puede modificar el perfil.");
                }
                
            }
        });

        return view;
    }

    private boolean nameValidation(String name){
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(name).matches() || name.length() > 30) {
            tilName.setError("Nombre inválido");
            return false;
        } else {
            tilName.setError(null);
        }

        return true;
    }

    private boolean phoneValidation(CharSequence phone){
        if (!Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.setError("Teléfono inválido");
            return false;
        } else {
            tilPhone.setError(null);
        }

        return true;
    }

    private boolean mailValidation(String mail){
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            tilMail.setError("Correo electrónico inválido");
            return false;
        } else {
            tilMail.setError(null);
        }

        return true;
    }

    private boolean dataValidation() {
        String nombre = tilName.getEditText().getText().toString();
        String telefono = tilPhone.getEditText().getText().toString();
        String correo = tilMail.getEditText().getText().toString();

        boolean a = nameValidation(nombre);
        boolean b = phoneValidation(telefono);
        boolean c = mailValidation(correo);

        if (a && b && c) {
            // OK, se pasa a la siguiente acción
            Toast.makeText(getActivity(), "Se guardaron los cambios", Toast.LENGTH_LONG).show();
            return true;
        }else {
            return false;
        }
    }
}
