package com.uniprogy.outquiz.views;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;

public class AnswerOption extends ConstraintLayout {

    ConstraintLayout answer;
    View scaleView;
    TextView titleTextView;
    TextView statsTextView;
    AnswerOptionListener listener;
    int answerId;
    int height;
    Context mContext;

    public AnswerOption(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public AnswerOption(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public AnswerOption(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.row_answer, this);
        answer = findViewById(R.id.answer);
        scaleView = findViewById(R.id.scaleView);
        titleTextView = findViewById(R.id.titleTextView);
        statsTextView = findViewById(R.id.statsTextView);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tapOption();
            }
        });
    }

    public int getAnswerId()
    {
        return answerId;
    }

    public void reset(String title, int answerId, AnswerOptionListener listener, int height)
    {
        // reset answer
        this.answerId = answerId;

        // reset height
        this.height = height;

        // reset listener
        this.listener = listener;

        // reset title
        titleTextView.setText(title);

        // reset scale view to invisible width
        ConstraintSet set = new ConstraintSet();
        set.clone(answer);
        set.constrainPercentWidth(R.id.scaleView, 0f);
        set.applyTo(answer);

        // reset visibility
        scaleView.setVisibility(INVISIBLE);
        statsTextView.setVisibility(INVISIBLE);
        setClipToOutline(true);

        // reset colors
        resetColors();
    }

    public void stats(int votes, int total, String type)
    {
        resetColors();
        statsTextView.setText(String.format("%d", votes));
        statsTextView.setVisibility(VISIBLE);
        scaleView.setVisibility(VISIBLE);

        if(type == "wrong" || type == "neutral" || type == "correct")
        {
            int color = mContext.getResources().getIdentifier("answer."+type, "color", mContext.getPackageName());
            scaleView.setBackgroundColor(mContext.getResources().getColor(color));
        }

        float percent = total > 0 ? votes / (total * 1f) : 0f;

        ChangeBounds transition = new ChangeBounds();
        transition.setInterpolator(new DecelerateInterpolator());
        transition.setDuration(1000);
        transition.setStartDelay(500);
        TransitionManager.beginDelayedTransition(answer, transition);

        ConstraintSet set = new ConstraintSet();
        set.clone(answer);
        set.constrainPercentWidth(R.id.scaleView, percent);
        set.applyTo(answer);
    }

    private void resetColors()
    {
        setBackground(genBackground(mContext.getResources().getColor(android.R.color.white)));
        //RoundRectShape shape = new RoundRectShape(null, new RectF(0, 0, getWidth(), getHeight()), null);
        setClipChildren(true);
        titleTextView.setTextColor(mContext.getResources().getColor(android.R.color.black));
    }

    private GradientDrawable genBackground(int color)
    {
        int radius = (int) Math.round(height / 2.0);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(color);
        return shape;
    }

    private void tapOption()
    {
        if(statsTextView.getVisibility() == INVISIBLE && listener != null && listener.canSelectOption())
        {
            if(listener.optionSelected(answerId))
            {
                GradientDrawable shape = (GradientDrawable) getBackground();

                ObjectAnimator.ofObject(shape, "color", new ArgbEvaluator(),
                        mContext.getResources().getColor(android.R.color.white),
                        mContext.getResources().getColor(R.color.answer_highlight))
                        .setDuration(200)
                        .start();

                ObjectAnimator.ofObject(titleTextView, "textColor", new ArgbEvaluator(),
                            mContext.getResources().getColor(android.R.color.black),
                            mContext.getResources().getColor(android.R.color.white))
                        .setDuration(200)
                        .start();
            }
        }
    }
}
