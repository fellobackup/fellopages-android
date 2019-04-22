package me.nereo.multi_image_selector;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

public class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback{

    /** The maximum picture selection times , int type, default 9*/
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** Picture Select mode, the default multiple choice */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";

    public static final String EXTRA_PAGE = "selected page";

    public static final String EXTRA_URL = "redirect url";
    /** Whether to display the camera , the default display */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** Choose a result, the return for the ArrayList <String> </> image path set  */
    public static final String EXTRA_RESULT = "select_result";
    /** The default selection set */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** Radio*/
    public static final int MODE_SINGLE = 0;
    /** Multiple choice */
    public static final int MODE_MULTI = 1;

    public static final int IS_CREATE_PAGE = 0;

    public static final int IS_VIEW_PAGE = 1;

    private static final int REQUEST_IMAGE = 300;


    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;
    private String mRedirectUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 20);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);

        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        if(intent.hasExtra(EXTRA_URL)){
            mRedirectUrl = intent.getStringExtra(EXTRA_URL);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Finish button
        mSubmitButton = (Button) findViewById(R.id.commit);
        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title));
            mSubmitButton.setEnabled(false);
        }else{
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultList != null && resultList.size() >0){

                    // Returns the selected image data
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    if(mRedirectUrl!=null) {
                        data.putExtra(EXTRA_URL, mRedirectUrl);
                        setResult(REQUEST_IMAGE, data);
                    }else {
                        setResult(RESULT_OK, data);
                    }
                    finish();
                }
            }
        });
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        if(mRedirectUrl!=null) {
            data.putExtra(EXTRA_URL, mRedirectUrl);
            setResult(REQUEST_IMAGE, data);
        }else {
            setResult(RESULT_OK, data);
        }
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // After a picture , change button states
        if(resultList.size() > 0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
            if(!mSubmitButton.isEnabled()){
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
        }else{
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title)
                    +"("+resultList.size()+"/"+mDefaultCount+")");
        }
        // When the state is to select the picture when
        if(resultList.size() == 0){
            mSubmitButton.setText(getResources().getString(R.string.action_bar_button_title));
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {

        if(imageFile != null) {

            // Send Broadcast intent to show image
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            } else {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://"
                                    + Environment.getExternalStorageDirectory())));
            }

            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            if(mRedirectUrl!=null) {
                Bundle bundle = new Bundle();
                data.putExtra(EXTRA_URL, mRedirectUrl);
                data.putExtras(bundle);
                setResult(REQUEST_IMAGE, data);
            }else {
                setResult(RESULT_OK, data);
            }
            finish();
        }
    }
}
