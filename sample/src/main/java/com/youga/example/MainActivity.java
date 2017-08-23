package com.youga.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.youga.imageselector.ImageActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 222;
    @Bind(R.id.showCamera)
    CheckBox showCamera;
    @Bind(R.id.number)
    EditText number;
    @Bind(R.id.crop)
    CheckBox crop;
    @Bind(R.id.submit)
    Button submit;
    @Bind(R.id.frame)
    LinearLayout frame;
    @Bind(R.id.group)
    RadioGroup group;
    @Bind(R.id.imageView)
    ImageView imageView;
    ArrayList<String> resultList = new ArrayList<>();
    @Bind(R.id.result)
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        frame.setVisibility(View.GONE);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.image == checkedId) {
                    frame.setVisibility(View.VISIBLE);
                } else {
                    frame.setVisibility(View.GONE);
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = number.getText().toString();
                if (m.isEmpty()) m = "1";
                Intent intent;
                if (frame.isShown()) {
                    intent = ImageActivity.choiceImage(MainActivity.this, crop.isChecked(), Integer.valueOf(m), showCamera.isChecked(), resultList);
                } else {
                    intent = ImageActivity.openCamera(MainActivity.this, crop.isChecked());
                }
                startActivityForResult(intent, REQUEST_IMAGE);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            resultList = data.getStringArrayListExtra(ImageActivity.EXTRA_RESULT);
            result.setText("");
            for (String path : resultList) {
                Log.i("IMAGE", "path:" + path);
                result.append(resultList.indexOf(path) + "-->" + path + "\n");
            }

            File file = new File(resultList.get(0));
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
    }
}
