package com.uniprogy.outquiz.activities.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.activities.base.BaseActivity;
import com.uniprogy.outquiz.helpers.CacheMedia;
import com.uniprogy.outquiz.helpers.ChatAdapter;
import com.uniprogy.outquiz.helpers.Misc;
import com.uniprogy.outquiz.helpers.RoundedCornersListener;
import com.uniprogy.outquiz.models.ChatMessage;
import com.uniprogy.outquiz.models.Player;
import com.uniprogy.outquiz.views.AnswerOption;
import com.uniprogy.outquiz.views.AnswerOptionListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ShowActivity extends BaseActivity {

    // video
    ProgressBar progressBar;
    SurfaceView playerSurfaceView;
    LibVLC mLibVLC;
    MediaPlayer vlcPlayer;

    // overlay
    View overlay;

    // header
    TextView viewersCountTextView;
    TextView warningTextView;
    ImageView appIcon;

    // chat
    RecyclerView chatList;
    ImageView openButton;
    ImageView sendButton;
    EditText chatEditText;
    TextView swipeInfoTextView;
    ConstraintLayout chat;
    View swipeArea;

    // question
    ConstraintLayout questionCardView;
    TextView questionTextView;
    ConstraintLayout questionOptionsView;
    DonutProgress questionCountdownTimer;
    ImageView questionStatusImageView;

    // winners
    ConstraintLayout winnersView;
    ConstraintLayout winnersUserView;
    TextView winnersTitleTextView;
    ImageView avatarImageView;
    TextView winnerUsername;
    TextView winnerPrize;
    Button winnersShareButton;

    //region Properties

    ChatAdapter chatAdapter;
    GestureDetector gestureDetector;
    Socket socket;
    Boolean inTheGame = true;
    int lives = 0;
    Boolean isLastQuestion = false;
    int selectedOption = -1;
    Handler handler = new Handler();
    Runnable questionRunnable;
    ArrayList<String> cachedImages = new ArrayList<>();
    int networkCache = 250;

    //endregion

    //region View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent i = getIntent();
        lives = i.getIntExtra("lives", 0);

        configureView();
    }

    private void configureView()
    {
        // init video player
        playerSurfaceView = findViewById(R.id.playerSurfaceView);
        videoInit();

        // init all views
        progressBar = findViewById(R.id.progressBar);

        overlay = findViewById(R.id.overlay);
        viewersCountTextView = findViewById(R.id.viewersCountTextView);
        warningTextView = findViewById(R.id.warningTextView);
        appIcon = findViewById(R.id.appIcon);
        chatList = findViewById(R.id.chatList);
        openButton = findViewById(R.id.openButton);
        sendButton = findViewById(R.id.sendButton);
        chatEditText = findViewById(R.id.chatEditText);
        swipeInfoTextView = findViewById(R.id.swipeInfoTextView);
        chat = findViewById(R.id.chat);
        swipeArea = findViewById(R.id.swipeArea);
        questionCardView = findViewById(R.id.questionCardView);
        questionTextView = findViewById(R.id.questionTextView);
        questionOptionsView = findViewById(R.id.questionOptionsView);
        questionCountdownTimer = findViewById(R.id.questionCountdownTimer);
        questionStatusImageView = findViewById(R.id.questionStatusImageView);
        winnersTitleTextView = findViewById(R.id.winnersTitleTextView);
        winnersView = findViewById(R.id.winnersView);
        winnersUserView = findViewById(R.id.winnersUserView);
        avatarImageView = findViewById(R.id.avatarImageView);
        winnerUsername = findViewById(R.id.winnerUsername);
        winnerPrize = findViewById(R.id.winnerPrize);
        winnersShareButton = findViewById(R.id.winnersShareButton);

        // hide necessary items
        progressBar.setVisibility(View.GONE);
        viewersCountTextView.setText("");
        warningTextView.setText("");
        warningTextView.setAlpha(0f);
        sendButton.setVisibility(View.INVISIBLE);
        chatEditText.setVisibility(View.INVISIBLE);
        swipeInfoTextView.setVisibility(View.INVISIBLE);
        questionCardView.setVisibility(View.INVISIBLE);
        winnersView.setVisibility(View.INVISIBLE);
        overlay.setVisibility(View.INVISIBLE);

        // design
        chatEditText.addOnLayoutChangeListener(new RoundedCornersListener());
        warningTextView.addOnLayoutChangeListener(new RoundedCornersListener());
        winnersShareButton.addOnLayoutChangeListener(new RoundedCornersListener());

        // keyboard handling
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // initialize actions
        appIconTap();
        chatInit();
        winnersShareButtonTap();

        // check timer
        questionRunnable = new Runnable() {
            @Override
            public void run() {
                tooLate();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // connect to socket
        socketConnect();
        // start video
        videoStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disconnect from socket
        socketDisconnect();
        //stop video
        videoStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(vlcPlayer != null) {
            vlcPlayer.release();
        }
        if(mLibVLC != null) {
            mLibVLC.release();
        }
    }


    //endregion

    //region Video

    private void videoInit()
    {
        mLibVLC = new LibVLC(this);
        vlcPlayer = new MediaPlayer(mLibVLC);
        vlcPlayer.setScale(videoScale());
        vlcPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch(event.type) {
                    case MediaPlayer.Event.Buffering:
                        if(event.getBuffering() < 100f) {
                            videoBuffering();
                        } else {
                            videoBufferingEnded();
                        }
                        break;
                }
            }
        });
    }

    private void videoStart()
    {
        Point size = videoSize();

        final IVLCVout vlcVout = vlcPlayer.getVLCVout();
        vlcVout.setVideoView(playerSurfaceView);
        vlcVout.setWindowSize(size.x, size.y);
        vlcVout.attachViews();

        Media media = new CacheMedia(mLibVLC, Uri.parse(Misc.streamingUrl()));
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=" + String.valueOf(networkCache));
        media.addOption(":clock-jitter=0");
        media.addOption(":clock-synchro=0");

        vlcPlayer.setMedia(media);
        media.release();
        vlcPlayer.play();
    }

    private void videoStop()
    {
        if(vlcPlayer != null)
        {
            vlcPlayer.stop();
            vlcPlayer.getVLCVout().detachViews();
        }
    }

    private Point videoSize()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    private float videoScale()
    {
        Point size = videoSize();

        int w = Integer.valueOf(getString(R.string.video_width));
        int h = Integer.valueOf(getString(R.string.video_height));

        float widthScale = size.x / (w*1f);
        float heightScale = size.y / (h*1f);

        return Math.max(widthScale, heightScale);
    }

    private void videoBuffering()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void videoBufferingEnded()
    {
        progressBar.setVisibility(View.GONE);
    }


    //endregion

    //region Show

    private void appIconTap()
    {
        appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(ShowActivity.this).create();
                alertDialog.setTitle(getString(R.string.tr_close));
                alertDialog.setMessage(getString(R.string.tr_leave_game));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.tr_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.tr_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showClose();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void showOverlay(long duration)
    {
        overlay.setAlpha(0f);
        overlay.setVisibility(View.VISIBLE);

        overlay.animate().alpha(1f).setDuration(duration).setListener(null);
    }

    private void hideOverlay(long duration)
    {
        overlay.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                overlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showQuestion(JSONObject data) throws Exception
    {
        // nullify current selected option
        selectedOption = -1;

        // extract data
        String question = data.getString("question");
        JSONArray answers = data.getJSONArray("answers");
        isLastQuestion = data.optBoolean("islast", false);

        // fill in question
        questionTextView.setText(question);
        // create answer options
        genAnswers(answers);

        // show card
        showQuestionCard();

        // timers
        int answer_time = Integer.valueOf(getString(R.string.settings_answer_time));
        if(inTheGame)
        {
            handler.postDelayed(questionRunnable, answer_time * 1000 + 300);
        }
        showQuestionCountdown();

        // hide after answer time passes + 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideQuestionCard();
            }
        }, (answer_time + 1) * 1000);
    }

    private void showQuestionCard()
    {
        questionCardView.setAlpha(0f);
        questionCardView.setVisibility(View.VISIBLE);

        for(int i=0;i<questionOptionsView.getChildCount();i++)
        {
            View child = questionOptionsView.getChildAt(i);
            child.setAlpha(0f);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) child.getLayoutParams();
            layoutParams.topMargin = layoutParams.topMargin + Misc.dpToPx(16);
            child.setLayoutParams(layoutParams);
        }

        showOverlay(300);
        questionCardView.animate().alpha(1f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for(int i=0;i<questionOptionsView.getChildCount();i++)
                {
                    long duration = 500;
                    long delay = i * 200;

                    View child = questionOptionsView.getChildAt(i);

                    child.animate().alpha(1f).setDuration(duration).setStartDelay(delay).setListener(null);
                    child.animate().translationYBy(-Misc.dpToPx(16)).setDuration(duration).setStartDelay(delay).setListener(null);
                }
            }
        });
    }

    private void genAnswers(JSONArray answers) throws Exception
    {
        questionOptionsView.removeAllViews();

        // working with dp, convert to px after
        int space = Math.round(48f/answers.length());
        int height = Math.round((Misc.pxToDp(questionOptionsView.getHeight()) * 1f - (answers.length() - 1) * space * 1f) / answers.length());
        if(height > 54)
        {
            height = 54;
        }

        for(int i=0;i<answers.length();i++)
        {
            JSONObject a = answers.getJSONObject(i);
            String answer = a.getString("answer");
            int answerId = a.getInt("id");

            // initiate custom view
            AnswerOption view = new AnswerOption(this);
            int viewId = View.generateViewId();
            view.setId(viewId);
            // apply basic layout params
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Misc.dpToPx(height));
            if(i > 0) {
                layoutParams.setMargins(0, Misc.dpToPx((space + height) * i), 0, 0);
            }
            view.setLayoutParams(layoutParams);
            view.reset(answer, answerId, answerOptionListener, Misc.dpToPx(height));
            // add view to answers layout
            questionOptionsView.addView(view);
            // create constraints
            ConstraintSet set = new ConstraintSet();
            set.clone(questionOptionsView);
            set.connect(viewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(viewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            // apply constraints
            set.applyTo(questionOptionsView);
        }
    }

    private void showQuestionCountdown()
    {
        appIcon.setVisibility(View.INVISIBLE);
        questionCountdownTimer.setAlpha(0f);
        questionCountdownTimer.setVisibility(View.VISIBLE);

        final int answer_time = Integer.valueOf(getString(R.string.settings_answer_time));
        new CountDownTimer(answer_time * 1000, 1) {
            public void onTick(long millisUntilFinished) {
                long passed = (answer_time * 1000) - millisUntilFinished;
                int progress = Math.round(passed / (answer_time * 1000f) * 1000);
                questionCountdownTimer.setProgress(progress);
                questionCountdownTimer.setText(String.format("%d", Math.round(Math.ceil(millisUntilFinished / 1000.0))));
            }

            public void onFinish() {
                questionCountdownTimer.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        questionCountdownTimer.setVisibility(View.INVISIBLE);
                        appIcon.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();

        questionCountdownTimer.animate().alpha(1f).setDuration(200).setListener(null);
    }

    private void hideQuestionCard()
    {
        hideOverlay(300);
        questionCardView.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                questionCardView.setVisibility(View.INVISIBLE);
                appIcon.setVisibility(View.VISIBLE);
                questionStatusImageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showCorrect(int correct, JSONObject stats) throws Exception
    {
        if(questionOptionsView.getChildCount() == 0)
        {
            return;
        }

        // calculate total
        int total = 0;
        Iterator<String> keys = stats.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            int votes = stats.getInt(key);
            total+= votes;
        }

        // update views
        for(int i=0;i<questionOptionsView.getChildCount();i++) {
            AnswerOption view = (AnswerOption) questionOptionsView.getChildAt(i);
            int id = view.getAnswerId();
            int votes = stats.getInt(String.valueOf(id));

            String type = "neutral";

            // correct answer
            if (id == correct) {
                type = "correct";
                if (id == selectedOption && inTheGame) {
                    showQuestionStatus(true);
                }
            }
            // wrong answer
            else if (id == selectedOption) {
                type = "wrong";

                if(inTheGame)
                {
                    showQuestionStatus(false);
                }

                kickOut();

                // suggest to use life if there is one and this is not a last question
                if(lives > 0 && !isLastQuestion)
                {
                    suggestLife();
                }
            }

            view.stats(votes, total, type);
        }

        // show question card
        showQuestionCard();

        // hide after 5 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideQuestionCard();
            }
        }, 5 * 1000);
    }

    private void showQuestionStatus(Boolean isCorrect)
    {
        if(isCorrect) {
            questionStatusImageView.setImageDrawable(getDrawable(R.drawable.checkmark));
        }
        else {
            questionStatusImageView.setImageDrawable(getDrawable(R.drawable.cross));
        }

        questionStatusImageView.setAlpha(0f);
        questionStatusImageView.setVisibility(View.VISIBLE);

        appIcon.setVisibility(View.INVISIBLE);

        questionStatusImageView.animate().alpha(1f).setDuration(200).setListener(null);
    }

    private void suggestLife()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(ShowActivity.this).create();
        alertDialog.setTitle(getString(R.string.tr_wrong_answer));
        alertDialog.setMessage(getString(R.string.tr_suggest_life));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.tr_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.tr_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        socketSendLife();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showWinners(int amount, JSONArray winners) throws Exception
    {
        // only show if there are winners to show
        if(winners.length() > 0)
        {
            ArrayList<JSONObject> players = new ArrayList<>();
            for(int i=0;i<winners.length();i++)
            {
                JSONObject data = winners.getJSONObject(i);
                data.put("prize", amount);

                // current player is a winner - show only them
                if(data.getInt("id") == Misc.getCurrentPlayer().id)
                {
                    ArrayList<JSONObject> theWinner = new ArrayList<>();
                    theWinner.add(data);
                    showWinnersCard(theWinner);

                    winnersShareButton.setVisibility(View.VISIBLE);
                    winnersTitleTextView.setText(R.string.tr_you_won);
                    return;
                }

                players.add(data);

                // prefetch avatars
                String avatar = data.optString("avatar", "");
                if(!TextUtils.isEmpty(avatar))
                {
                    cachedImages.add(avatar);
                    AndroidNetworking.get(avatar).build().prefetch();
                }
            }
            // show winners card
            showWinnersCard(players);
        }
    }

    private void showWinnersCard(final ArrayList<JSONObject> winners)
    {
        winnersView.setAlpha(0f);
        winnersView.setVisibility(View.VISIBLE);
        winnersUserView.setTag(0);
        winnersUserView.setAlpha(0f);
        winnersUserView.setVisibility(View.VISIBLE);

        showOverlay(300);
        winnersView.animate().alpha(1f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showWinnerInfo(winners);
            }
        });

        // hide after required amount of time - 2 seconds to show host
        int winners_time = Integer.valueOf(getString(R.string.settings_winners_time));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideWinnersCard();
            }
        }, (winners_time - 2) * 1000);
    }

    private void hideWinnersCard()
    {
        hideOverlay(300);
        winnersView.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                winnersView.setVisibility(View.INVISIBLE);
                for(int i=0;i<cachedImages.size();i++)
                {
                    AndroidNetworking.evictBitmap(cachedImages.get(i));
                }
                cachedImages.clear();
            }
        });
    }

    private void showWinnerInfo(final ArrayList<JSONObject> winners)
    {
        final int ind = (Integer) winnersUserView.getTag();
        if(ind < winners.size())
        {
            JSONObject data = winners.get(ind);
            Player player = new Player(data);

            player.setAvatar(avatarImageView);
            winnerUsername.setText(player.username);
            winnerPrize.setText(Misc.moneyFormat(data.optInt("prize", 0)));

            long showTime = Math.round((Integer.valueOf(getString(R.string.settings_winners_time)) * 1000f - 2000f) / winners.size() * 1f) - 200;
            if(showTime < 200) { showTime = 200; }
            final long theShowTime = showTime;

            winnersUserView.animate().alpha(1f).setDuration(200).setStartDelay(0).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    winnersUserView.animate().alpha(0f).setDuration(200).setStartDelay(theShowTime).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            winnersUserView.setTag(ind + 1);
                            showWinnerInfo(winners);
                        }
                    });
                }
            });
        }
    }

    private void winnersShareButtonTap()
    {
        winnersShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prize = winnerPrize.getText().toString();
                String shareText = getString(R.string.tr_share_win_text,
                        getString(R.string.app_name),
                        prize,
                        getString(R.string.app_host));

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    private AnswerOptionListener answerOptionListener = new AnswerOptionListener() {
        @Override
        public Boolean canSelectOption() {
            return selectedOption == -1 && questionCardView.getVisibility() == View.VISIBLE && questionCardView.getAlpha() == 1f;
        }

        @Override
        public Boolean optionSelected(int id) {
            if(inTheGame)
            {
                selectedOption = id;
                socketSendAnswer();
                handler.removeCallbacks(questionRunnable);
                return true;
            }
            showWarning(getString(R.string.tr_watching_only), true);
            return false;
        }
    };

    private void showWarning(String message, Boolean autoRemove)
    {
        showWarning(message, autoRemove, "warning");
    }

    private void showWarning(String message, final Boolean autoRemove, String type)
    {
        warningTextView.setText(message);
        warningTextView.setAlpha(0f);

        if(type.equals("success")) { warningTextView.setBackgroundColor(getResources().getColor(R.color.show_success)); }
        else { warningTextView.setBackgroundColor(getResources().getColor(R.color.show_warning)); }

        warningTextView.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(autoRemove) {
                    Handler handler = new Handler();
                    handler.postDelayed(hideWarningRunnable, 2000);
                }
            }
        });
    }

    private Runnable hideWarningRunnable = new Runnable() {
        @Override
        public void run() {
            hideWarning();
        }
    };

    private void hideWarning()
    {
        warningTextView.animate().alpha(0f).setListener(null);
    }

    private void tooLate()
    {
        kickOut();
        showWarning(getString(R.string.tr_too_late), true);
    }

    private void watchOnly()
    {
        kickOut();
        String[] errs = {
            getString(R.string.tr_already_started)
        };
        showErrors(errs);
    }

    private void kickOut()
    {
        inTheGame = false;
    }

    private void kickIn()
    {
        inTheGame = true;
    }

    private void showClose()
    {
        finish();
    }

    //endregion

    //region Chat

    private void chatInit()
    {
        chatAdapter = new ChatAdapter(new ArrayList<ChatMessage>());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(layoutManager);
        chatList.setItemAnimator(new DefaultItemAnimator());

        // Scroll to bottom on new messages
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                layoutManager.scrollToPosition(chatAdapter.getItemCount()-1);
            }
        });

        chatList.setAdapter(chatAdapter);

        gestureDetector = new GestureDetector(this, new ChatGestureListener());
        swipeArea.setOnTouchListener(chatTouchListener);
        chatEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(chatEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(chatEditText.getWindowToken(), 0);
                    chatHideField();
                }
            }
        });

        openButtonTap();
        sendButtonTap();
    }

    private View.OnTouchListener chatTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    };

    private void chatReceived(ChatMessage message)
    {
        chatAdapter.add(message);
    }

    private void openButtonTap()
    {
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatShowField();
            }
        });
    }

    private void sendButtonTap()
    {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatEditText.getText().toString();
                socketSendChat(message);
                chatEditText.setText("");
            }
        });
    }

    private void chatShowField()
    {
        openButton.setVisibility(View.INVISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        chatEditText.setVisibility(View.VISIBLE);
        chatEditText.requestFocus();

    }

    private void chatHideField()
    {
        openButton.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.INVISIBLE);
        chatEditText.setVisibility(View.INVISIBLE);
    }

    class ChatGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent event) {
            return chatEditText.hasFocus() ? false : true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            float deltaX = event1.getX() - event2.getX();
            // right swipe
            if(deltaX < 0) {

                ChangeBounds transition = new ChangeBounds();
                transition.setInterpolator(new AccelerateInterpolator());
                transition.setDuration(300);
                TransitionManager.beginDelayedTransition(chat, transition);

                ConstraintSet set = new ConstraintSet();
                set.clone(chat);
                set.connect(R.id.message, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
                set.connect(R.id.message, ConstraintSet.END, R.id.outsideGuideline, ConstraintSet.END);
                set.applyTo(chat);

                swipeInfoTextView.setAlpha(0f);
                swipeInfoTextView.setVisibility(View.VISIBLE);

                swipeInfoTextView.animate().alpha(0.5f).setDuration(300).setListener(null);

            }
            // left swipe
            else {

                TransitionManager.beginDelayedTransition(chat);

                ConstraintSet set = new ConstraintSet();
                set.clone(chat);
                set.connect(R.id.message, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                set.connect(R.id.message, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                set.applyTo(chat);

                swipeInfoTextView.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        swipeInfoTextView.setVisibility(View.INVISIBLE);
                    }
                });
            }

            return true;
        }
    }

    //endregion

    //region Socket

    private void socketConnect()
    {
        try {
            IO.Options opts = new IO.Options();
            opts.query = "token=" + Misc.getCurrentPlayer().token;
            opts.forceNew = true;
            opts.reconnection = true;
            opts.reconnectionAttempts = 0;
            opts.reconnectionDelay = 3000;

            socket = IO.socket(getString(R.string.socket_host)+":"+getString(R.string.socket_port), opts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(socket == null) {
            return;
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketConnected();
                    }
                });
            }
        });

        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketReconnecting();
                    }
                });
            }
        });

        socket.on("watch", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        watchOnly();
                    }
                });
            }
        });

        socket.on("chat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = args[0].toString();
                        JSONObject user = (JSONObject) args[1];
                        ChatMessage chatMessage = new ChatMessage(msg, user);
                        chatReceived(chatMessage);
                    }
                });
            }
        });

        socket.on("stats", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int count = (Integer) args[0];
                        viewersCountTextView.setText(formatCounter(count));
                    }
                });
            }
        });

        socket.on("question", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject qdata = (JSONObject) args[0];
                        try {
                            showQuestion(qdata);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.on("correct", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int correct = (Integer) args[0];
                        JSONObject stats = (JSONObject) args[1];
                        try {
                            showCorrect(correct, stats);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.on("winners", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int amount = (Integer) args[0];
                        JSONArray winners = (JSONArray) args[1];
                        try {
                            showWinners(amount, winners);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.on("end", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showClose();
                    }
                });
            }
        });

        socket.connect();
    }

    private void socketDisconnect()
    {
        if(socket != null)
        {
            socket.disconnect();
            socket = null;
        }
    }

    private void socketConnected()
    {
        if(warningTextView.getAlpha() > 0f && warningTextView.getVisibility() == View.VISIBLE)
        {
            showWarning(getString(R.string.tr_connected), true, "success");
        }
    }

    private void socketReconnecting()
    {
        if(warningTextView.getAlpha() == 0f || warningTextView.getVisibility() != View.VISIBLE)
        {
            showWarning(getString(R.string.tr_connection_lost), false);
        }
    }

    private boolean socketOK()
    {
        return socket != null && socket.connected();
    }

    private void socketSendChat(String message)
    {
        if(socketOK() && !TextUtils.isEmpty(message))
        {
            socket.emit("chat", message);
        }
    }

    private void socketSendAnswer()
    {
        if(socketOK())
        {
            socket.emit("answer", selectedOption, new Ack() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Boolean result = (Boolean) args[0];
                            // answer was not stored, only reason is out of time
                            if(!result)
                            {
                                tooLate();
                            }
                        }
                    });
                }
            });
        }
    }

    private void socketSendLife()
    {
        if(socketOK())
        {
            socket.emit("life", new Ack() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Boolean result = (Boolean) args[0];
                            // life used ok
                            if (result) {
                                kickIn();
                                lives -= 1;
                                showWarning(getString(R.string.tr_life_used_success), true, "success");
                            }
                            // life usage failed
                            else {
                                showWarning(getString(R.string.tr_life_used_failure), true);
                            }
                        }
                    });
                }
            });
        }
    }

    //endregion

    //region Helpers

    private String formatCounter(int count)
    {
        if (count < 1000) { return String.format("%d",count); }
        else if (count < 100000) { return String.format("%.1fK", count/1000f); }
        else { return String.format("%.0fK", count/1000f); }
    }

    //endregion
}
