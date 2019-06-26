package com.sanleng.dangerouscabinet.ui.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;
import com.sanleng.dangerouscabinet.R;

/**
 * 描述: 验证码输入框
 */

public class CodeEditView extends LinearLayout implements TextWatcher, View.OnClickListener, View.OnLongClickListener {
    private int editViewNum = 6; //默认输入框数量
    private ArrayList<TextView> mTextViewsList = new ArrayList<>(); //存储EditText对象
    private Context mContext;
    private EditText mEditText;
    private int borderSize = 35; //方格边框大小
    private int borderMargin = 10; //方格间距
    private int textSize = 8; //字体大小
    private int textColor = 0xff; //字体颜色
    private int inputType = InputType.TYPE_CLASS_NUMBER;
    private inputEndListener callBack;

    public CodeEditView(Context context) {
        super(context);
        init(context);
    }

    public CodeEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
        init(context);
    }

    public CodeEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs);
        init(context);
    }

    private void initData(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CodeEditView);
        borderSize = array.getInteger(R.styleable.CodeEditView_borderSize, 35);
        borderMargin = array.getInteger(R.styleable.CodeEditView_borderMargin, 10);
        textSize = array.getInteger(R.styleable.CodeEditView_textSize, 8);
        textColor = array.getColor(R.styleable.CodeEditView_textColor, Color.BLACK);
        editViewNum = array.getInteger(R.styleable.CodeEditView_borderNum, 6);
    }

    /**
     * 获取输入框内容
     */
    public String getText() {
        return mEditText.getText().toString();
    }

    public void setOnInputEndCallBack(inputEndListener onInputEndCallBack) {
        callBack = onInputEndCallBack;
    }

    /**
     * 长按弹出PopupWindow,用来粘贴文本
     */
    @Override
    public boolean onLongClick(View v) {
//        showPopupWindow();
        return true;
    }

//    private void showPopupWindow() {
//        View popupwindow = LayoutInflater.from(mContext).inflate(com.lkx.library.R.layout.popupwindow_view, null);
//        final PopupWindow popupWindow = new PopupWindow(popupwindow, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
//        popupWindow.showAsDropDown(this, getWidth() / 2, DensityUtil.dip2px(mContext, 5));
//        popupwindow.findViewById(com.lkx.library.R.id.paste).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                pasteTextToView();
////                Toast.makeText(mContext, getPasetText(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /**
     * 粘贴内容到文本框
     */
    private void pasteTextToView() {
        if (getPasetText() != null && !TextUtils.isEmpty(getPasetText()) && isInteger(getPasetText())) {
            //纯数字
            char[] chars = getPasetText().substring(0, editViewNum).toCharArray();
            mEditText.setText(getPasetText().substring(0, editViewNum));
            for (int i = 0; i < chars.length; i++) {
                mTextViewsList.get(i).setText(String.valueOf(chars[i]));
            }
        } else {
            Toast.makeText(mContext, "粘贴的文本必须为纯数字", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public String getPasetText() {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            CharSequence text = clipData.getItemAt(0).getText();
            return text.toString().trim();
        }
        return "";
    }

    public interface inputEndListener {
        void input(String text);

        void afterTextChanged(String text);
    }

    private void init(final Context context) {
        mContext = context;
        initEditText(context);
        //设置方格间距
        LayoutParams params = new LayoutParams(
                DensityUtil.dip2px(mContext, borderSize), DensityUtil.dip2px(mContext, borderSize));
        params.setMargins(DensityUtil.dip2px(mContext, borderMargin), 0, 0, 0);
        //设置方格文字
        for (int i = 0; i < editViewNum; i++) {
            TextView textView = new TextView(mContext);
            textView.setBackgroundResource(R.drawable.shape_border_normal);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(DensityUtil.sp2px(mContext, textSize));
            textView.getPaint().setFakeBoldText(true);
            textView.setLayoutParams(params);
            textView.setInputType(inputType);
            textView.setTextColor(textColor);
            textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);
            textView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mTextViewsList.add(textView);
            addView(textView);
        }

        //显示隐藏软键盘
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                mEditText.setHintTextColor(Color.parseColor("#ff0000"));
            }
        }, 500);

        //监听删除键
        mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (mEditText.getText().length() >= mTextViewsList.size()) return false;
                    mTextViewsList.get(mEditText.getText().length()).setText("");
                }
                return false;
            }
        });

        this.setOnLongClickListener(this);
    }

    private void initEditText(Context context) {
        mEditText = new EditText(context);
        mEditText.setBackgroundColor(Color.parseColor("#00000000"));
        mEditText.setMaxLines(1);
        mEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(editViewNum)});
        mEditText.addTextChangedListener(this);
        mEditText.setTextSize(0);
        addView(mEditText);
    }

    //清空文字
    public void clearText() {
        mEditText.setText("");
        for (TextView textView : mTextViewsList) {
            textView.setText("");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (callBack != null) {
            callBack.afterTextChanged(s.toString());
        }
        if (s.length() <= 1) {
            mTextViewsList.get(0).setText(s);
        } else {
            mTextViewsList.get(mEditText.getText().length() - 1).setText(s.subSequence(s.length() - 1, s.length()));
        }
        if (s.length() == editViewNum) {
            if (callBack != null) {
                callBack.input(mEditText.getText().toString());
            }
        }
    }

    @Override
    public void onClick(View v) { //TextView点击时获取焦点弹出输入法
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
