package com.uniprogy.outquiz.activities.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.WebViewActivity;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.activities.signin.PhoneCodeActivity;
import com.uniprogy.outquiz.activities.signin.ProfileActivity;
import com.uniprogy.outquiz.helpers.API;
import com.uniprogy.outquiz.helpers.APIListener;
import com.uniprogy.outquiz.helpers.LbAdapter;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.models.Player;
import com.uniprogy.outquiz.models.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Typeface.BOLD;

public class HomeActivity extends BaseActivity {

    private static final int RESULT_CAMERA = 1;
    private static final int RESULT_GALLERY = 2;
    private static final int PERMISSION_CAMERA = 1;
    private static final int PERMISSION_GALLERY = 2;

    // header
    ImageView faqImageView;

    // game info
    Button watchButton;
    TextView nextGameTextView;

    // profile
    ImageView menuImageView;
    ImageView avatarImageView;
    TextView usernameTextView;
    TextView rankTexView;
    TextView balanceValue;
    TextView livesValue;
    Button livesButton;

    // leaderboard
    Button lbWeekButton;
    Button lbAllButton;
    RecyclerView lbList;

    //region Properties

    Map<String,Integer> playerStats;
    Show show;
    Handler handler = new Handler();
    int lbSelected = 0;
    JSONArray lbWeekly;
    JSONArray lbTotal;
    LbAdapter lbAdapter;
    Boolean autoLaunchShow = true;


    //endregion

    //region View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
         configureView();
    }



        private void configureView()
    {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        // initialize all views
        faqImageView = findViewById(R.id.faqImageView);
        watchButton = findViewById(R.id.watchButton);
        nextGameTextView = findViewById(R.id.nextGameTextView);
        menuImageView = findViewById(R.id.menuImageView);
        avatarImageView = findViewById(R.id.avatarImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        rankTexView = findViewById(R.id.rankTexView);
        balanceValue = findViewById(R.id.balanceValue);
        livesValue = findViewById(R.id.livesValue);
        livesButton = findViewById(R.id.livesButton);
        lbWeekButton = findViewById(R.id.lbWeekButton);
        lbAllButton = findViewById(R.id.lbAllButton);
        lbList = findViewById(R.id.lbList);

        // design
        livesButton.addOnLayoutChangeListener(new RoundedCornersListener());
        watchButton.addOnLayoutChangeListener(new RoundedCornersListener());

        // initialize actions
        initLb();
        menuTap();
//        avatarTap();
        faqTap();
        balanceTap();
        livesButtonTap();
        watchButtonTap();

        // fill in data
        nextGameTextView.setText("");
        Misc.getCurrentPlayer().setAvatar(avatarImageView);
        usernameTextView.setText(Misc.getCurrentPlayer().username);
        rankTexView.setText("");
        balanceValue.setText("");
        livesValue.setText("0");
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        startShowTimer();
        loadPlayer();
        loadLb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopShowTimer();
    }

    //endregion

    //region Menu

    private void menuTap()
    {
        final PopupMenu popup = new PopupMenu(this, menuImageView);
        popup.getMenu().add(0,1,1,R.string.tr_invite);
        popup.getMenu().add(0,2,2,R.string.tr_apply_referral_code);
        //popup.getMenu().add(0,3,3,R.string.tr_logout);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId())
                {
                    // invite
                    case 1:
                        showMenuShare();
                        return true;
                    // apply referral code
                    case 2:
                        showMenuApplyReferralCode();
                        return true;
                    // logout
                    case 3:
                        Misc.logout();
                        return true;
                    default:
                        return false;
                }
            }
        });

        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
    }

    private void showMenuShare()
    {

        String shareText = getString(R.string.tr_share_text,
                getString(R.string.app_name),
                Misc.getCurrentPlayer().referral,
                "https://neighboursapp.ng/siteapi/index/app-page");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showMenuApplyReferralCode()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tr_apply_referral_code);

        final EditText input = new EditText(this);
        input.setHint(R.string.tr_referral_code);
        input.setTextColor(getResources().getColor(android.R.color.primary_text_light));
        builder.setView(input);

        builder.setPositiveButton(R.string.tr_apply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyReferralCode(input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.tr_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void applyReferralCode(String code)
    {
        if(!TextUtils.isEmpty(code)) {
            API.referral(code, referralListener);
        }
    }

    private APIListener referralListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle(getString(R.string.tr_referral_code));
            alertDialog.setMessage(getString(R.string.tr_referral_code_success));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.tr_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };

    //endregion

    //region Avatar

    private void avatarTap()
    {

        final PopupMenu popup = new PopupMenu(this, avatarImageView);
        popup.getMenu().add(0,1,1,R.string.tr_new_photo);
        popup.getMenu().add(0,2,2,R.string.tr_camera_roll);
        popup.getMenu().add(0,3,3,R.string.tr_delete_avatar);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId())
                {
                    // new photo
                    case 1:
                        imageFromCamera();
                        return true;
                    // photo from gallery
                    case 2:
                        imageFromGallery();
                        return true;
                    // remove avatar
                    case 3:
                        removeAvatar();
                        return true;
                    default:
                        return false;
                }
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
    }

    private void removeAvatar()
    {
        API.avatar(null, avatarListener);
    }

    private void imageFromCamera()
    {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA);
        }
        else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, RESULT_CAMERA);
            }

        }
    }

    private void imageFromGallery()
    {
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_GALLERY);
        }
        else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, RESULT_GALLERY);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromCamera();
                }
                return;
            case PERMISSION_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageFromGallery();
                }
                return;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;

        if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // get image bitmap
            imageBitmap = (Bitmap) extras.get("data");
        }
        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                // get image bitmap
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(imageBitmap != null)
        {
            // show spinner
            findViewById(R.id.avatarProgressBar).setVisibility(View.VISIBLE);
            // generate file and upload
            File imgFile = getImage(imageBitmap);
            if(imgFile != null) {
                API.avatar(imgFile, avatarListener);
            }
        }
    }

    private File getImage(Bitmap image) {
        try {
            File imgFile = new File(getCacheDir(), "image_");
            imgFile.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(imgFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return imgFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private APIListener avatarListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            // hide spinner
            findViewById(R.id.avatarProgressBar).setVisibility(View.GONE);
            // update player info
            Player p = Misc.getCurrentPlayer();
            p.token = Misc.jsonNull(response, "token");
            p.avatar = Misc.jsonNull(response, "avatar");
            Misc.setCurrentPlayer(p);
            // update image view
            p.setAvatar(avatarImageView, true);
        }

        @Override
        public void failure(int code, String[] errors) {
            // hide spinner
            findViewById(R.id.avatarProgressBar).setVisibility(View.GONE);
            // show errors
            showErrors(errors);
        }
    };

    //endregion

    //region FAQ

    private void faqTap()
    {
        final PopupMenu popup = new PopupMenu(this, faqImageView);
        popup.getMenu().add(0,1,1,R.string.tr_rules);
        popup.getMenu().add(0,2,2,R.string.tr_faq);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId())
                {
                    // new photo
                    case 1:
                        openLegal("rules");
                        return true;
                    // photo from gallery
                    case 2:
                        openLegal("faq");
                        return true;
                    default:
                        return false;
                }
            }
        });

        faqImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
    }

    private void openLegal(String type)
    {
        String docTitle = "";
        switch(type)
        {
            case "faq":
                docTitle = getString(R.string.tr_faq);
                break;
            case "rules":
                docTitle = getString(R.string.tr_rules);
                break;
        }

        Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("docTitle", docTitle);
        i.putExtra("docUrl", Misc.docUrl(type));
        startActivity(i);
    }

    //endregion

    //region Cashout

    private void balanceTap()
    {
        balanceValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, CashoutActivity.class);
                i.putExtra("balance", playerStats.get("balance"));
                startActivity(i);
            }
        });
    }

    //endregion

    //region Player

    private void loadPlayer()
    {
        if (Misc.getCurrentPlayer() != null) {
            API.player(playerListener);
        }
    }

    private APIListener playerListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            playerStats = new HashMap<String,Integer>();
            playerStats.put("rank_total", response.optInt("rank_total", -1));
            playerStats.put("rank_weekly", response.optInt("rank_weekly", -1));
            playerStats.put("balance", response.optInt("balance", 0));
            playerStats.put("lives", response.optInt("lives", 0));

            String rank_weekly_str = playerStats.get("rank_weekly") >= 0 ? String.format("%d", playerStats.get("rank_weekly")) : "-";
            String rank_total_str  = playerStats.get("rank_total") >= 0 ? String.format("%d", playerStats.get("rank_total")) : "-";

            rankTexView.setText(getString(R.string.tr_rank_this_week, rank_weekly_str));
            balanceValue.setText(Misc.moneyFormat(playerStats.get("balance")));
            livesValue.setText(String.format("%d", playerStats.get("lives")));

            loadShow();
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };

    private void livesButtonTap()
    {
        livesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, LivesActivity.class);
                startActivity(i);
            }
        });
    }

    //endregion

    //region Leaderboard

    private void initLb()
    {
        lbWeekButton.setSelected(true);

        lbAdapter = new LbAdapter(new JSONArray());
        lbList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lbList.setItemAnimator(new DefaultItemAnimator());
        lbList.setAdapter(lbAdapter);

        lbWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lbWeekButton.setSelected(true);
                lbAllButton.setSelected(false);
                lbSelected = 0;
                lbChanged();
            }
        });

        lbAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lbWeekButton.setSelected(false);
                lbAllButton.setSelected(true);
                lbSelected = 1;
                lbChanged();
            }
        });
    }

    private void loadLb()
    {
        API.leaderboard(lbListener);
    }

    private APIListener lbListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            try {
                lbWeekly = response.getJSONArray("weekly");
                lbTotal = response.getJSONArray("total");
                lbChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };

    private void lbChanged()
    {
        JSONArray data = lbSelected == 0 ? lbWeekly : lbTotal;
        lbAdapter.update(data);
    }

    //endregion

    //region Show

    private void watchButtonTap()
    {
        watchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchShow();
            }
        });
    }

    private void startShowTimer()
    {
        handler.postDelayed(showRunnable, 10000);
    }

    private void stopShowTimer()
    {
        handler.removeCallbacks(showRunnable);
    }

    private Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            loadShow();
            handler.postDelayed(showRunnable, 10000);
        }
    };

    private void loadShow()
    {
        API.home(showListener);
    }

    private APIListener showListener = new APIListener() {
        @Override
        public void success(JSONObject response) {
            JSONObject obj = response.optJSONObject("show");
            if(obj != null)
            {
                try {
                    int id = obj.getInt("id");
                    String schedule = obj.getString("schedule");
                    int amount = obj.getInt("amount");
                    int live = obj.getInt("live");

                    show = new Show(id, schedule, amount, live);

                } catch(Exception e) {
                    show = null;
                    e.printStackTrace();
                }
            }
            else {
                show = null;
            }
            updateShow();
        }

        @Override
        public void failure(int code, String[] errors) {
            showErrors(errors);
        }
    };

    private void updateShow()
    {
        SpannableString msg = new SpannableString("");

        if(show == null)
        {
            msg = new SpannableString(getString(R.string.tr_no_upcoming_show).toUpperCase());
            nextGameTextView.setVisibility(View.VISIBLE);
            watchButton.setVisibility(View.INVISIBLE);
        }
        else if(show.live == 0)
        {
            SpannableString strNextGameTitle = new SpannableString(getString(R.string.tr_next_game).toUpperCase());

            SpannableString strSchedule = new SpannableString(show.scheduleFormatted());
            strSchedule.setSpan(
                    new RelativeSizeSpan(strSchedule.length() < 10 ? 1.7f : 1.5f),
                    0,
                    strSchedule.length(),
                    0);
            strSchedule.setSpan(new StyleSpan(BOLD), 0, strSchedule.length(), 0);

            SpannableString strPrizeTitle = new SpannableString(getString(R.string.tr_prize).toUpperCase());

            SpannableString strPrize = new SpannableString(show.amountFormatted());
            strPrize.setSpan(new RelativeSizeSpan(1.7f), 0, strPrize.length(), 0);
            strPrize.setSpan(new StyleSpan(BOLD), 0, strPrize.length(), 0);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(strNextGameTitle).append("\n").append(strSchedule).append("\n").append(strPrizeTitle).append("\n").append(strPrize);
            msg = SpannableString.valueOf(builder);

            nextGameTextView.setVisibility(View.VISIBLE);
            watchButton.setVisibility(View.INVISIBLE);
        }
        else if(show.live == 1)
        {
            nextGameTextView.setVisibility(View.INVISIBLE);
            watchButton.setVisibility(View.VISIBLE);
            if(autoLaunchShow)
            {
                launchShow();
            }
        }

        nextGameTextView.setText(msg);
    }

    private void launchShow()
    {
        Intent i = new Intent(HomeActivity.this, ShowActivity.class);
        i.putExtra("lives", playerStats.get("lives"));
        startActivity(i);
        autoLaunchShow = false;
    }

    //endregion

}
