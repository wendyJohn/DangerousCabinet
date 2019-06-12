package com.sanleng.dangerouscabinet.face.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.aip.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.R;

import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE;
import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE_CLOSE;
import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE_OPEN;

public class FacePreviewImageSetActivity extends Activity implements View.OnClickListener {


    private RadioButton openButton;
    private RadioButton closeButton;
    private RadioGroup radioButton;
    private Button confirmbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_image);

        openButton = findViewById(R.id.open_preview);
        closeButton = findViewById(R.id.close_preview);
        radioButton = findViewById(R.id.button_group);
        confirmbtn = findViewById(R.id.confirm_btn);
        confirmbtn.setOnClickListener(this);

        radioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                switch (checkedId) {
                    case R.id.open_preview:
                        PreferencesUtil.putInt(TYPE_PREVIEWIMAGE, TYPE_PREVIEWIMAGE_OPEN);
                        break;
                    case R.id.close_preview:
                        PreferencesUtil.putInt(TYPE_PREVIEWIMAGE, TYPE_PREVIEWIMAGE_CLOSE);
                        break;
                    default:
                        break;
                }
            }
        });


        int selectType = PreferencesUtil.getInt(TYPE_PREVIEWIMAGE, TYPE_PREVIEWIMAGE_CLOSE);
        defaultLiveness(selectType);

    }


    private void defaultLiveness(int selectType) {
        if (selectType == TYPE_PREVIEWIMAGE_OPEN) {
            openButton.setChecked(true);
        } else if (selectType == TYPE_PREVIEWIMAGE_CLOSE) {
            closeButton.setChecked(true);
        }
    }


    @Override
    public void onClick(View v) {
        finish();
    }
}
