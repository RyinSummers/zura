package com.homepetzura.ljy.zura;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*
        开关服务
         */
        final Intent serviceintent=new Intent(MainActivity.this,ZuraService.class);
        ToggleButton toggle_service=(ToggleButton)findViewById(R.id.togglebutton_openstopservice);
        toggle_service.setChecked(ZuraService.isServiceOpen);
        toggle_service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(serviceintent);
                } else {
                    stopService(serviceintent);
                }
            }
        });
        /*
        按钮，开启记事本
        blabla
         */

        ToggleButton toggle_note=(ToggleButton)findViewById(R.id.togglebutton_notepad);
        SharedPreferences pref=getSharedPreferences("notefile",MODE_PRIVATE);
        toggle_note.setChecked(pref.getBoolean("ison",false));
        final EditText editText_note=(EditText)findViewById(R.id.edittext_note);
        if(pref.getBoolean("ison",false))
            editText_note.setVisibility(View.VISIBLE);
        else
            editText_note.setVisibility(View.GONE);
        editText_note.setText(pref.getString("note",""));
        toggle_note.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=getSharedPreferences("notefile",MODE_PRIVATE).edit();
                if (isChecked) {
                    editText_note.setVisibility(View.VISIBLE);
                    editor.putBoolean("ison",true);
                    editor.commit();
                } else {
                    editText_note.setVisibility(View.GONE);
                    editor.putBoolean("ison", false);
                    editor.commit();
                }
            }
        });
        editText_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor=getSharedPreferences("notefile",MODE_PRIVATE).edit();
                editor.putString("note",editText_note.getText().toString());
                editor.commit();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
