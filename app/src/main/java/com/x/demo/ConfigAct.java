package com.x.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigAct extends Activity {

    private EditText test_pid;
    private LinearLayout config_name;
    private TextView name1;
    private TextView name2;
    private TextView name3;

    private LinearLayout configValue;
    private EditText cloud_value;
    private Button ok;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_act);
        test_pid = findViewById(R.id.test_pid);
        config_name = findViewById(R.id.config_name);
        name1 = findViewById(R.id.name1);
        name2 = findViewById(R.id.name2);
        name3 = findViewById(R.id.name3);
        configValue = findViewById(R.id.layout);
        cloud_value = findViewById(R.id.cloud_value);
        ok = findViewById(R.id.ok);
        name1.setOnClickListener(getListener());
        name2.setOnClickListener(getListener());
        name3.setOnClickListener(getListener());
        test_pid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                String[] arr = getThree(s);
                if (arr.length>0){
                    config_name.setVisibility(View.VISIBLE);
                    name1.setText(arr[0]);
                    name2.setText(arr[1]);
                    name3.setText(arr[2]);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingConfig.setValue(test_pid.getText().toString(),cloud_value.getText().toString());
                Toast.makeText(ConfigAct.this,"success",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View.OnClickListener getListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t = (TextView) view;
                String ts = t.getText().toString();
                test_pid.setText(ts);
                config_name.setVisibility(View.GONE);
                String st = SettingConfig.getValue(ts);
                configValue.setVisibility(View.VISIBLE);
                cloud_value.setText(st);
            }
        };
    }

    private String[] getThree(String s){
        String[] s3 = {"","",""};
        int count = 0;
        for (String k:SettingConfig.getKeys()){
            if (count ==3)
                return s3;
            if (k.contains(s)){
                s3[count] = k;
                count++;
            }
        }
        return s3;
    }

    public static void start(Activity act){
        act.startActivity(new Intent(act,ConfigAct.class));
    }
}
