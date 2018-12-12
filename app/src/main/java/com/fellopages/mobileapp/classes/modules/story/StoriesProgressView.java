/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.modules.story;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fellopages.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class StoriesProgressView extends LinearLayout {

    private static final int PROGRESS_MAX = 100;

    private final LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
    private final LayoutParams SPACE_LAYOUT_PARAM = new LayoutParams(5, LayoutParams.WRAP_CONTENT);

    private final List<ProgressBar> progressBars = new ArrayList<>();
    private final List<ObjectAnimator> animators = new ArrayList<>();

    private int storiesCount = -1;
    /**
     * pointer of running animation
     */
    private int current = 0;
    private StoriesListener storiesListener;
    boolean isReverse;
    boolean isComplete;

    public interface StoriesListener {
        public void onNext();

        public void onNextStarted();

        public void onPrev();

        public void onComplete();
    }

    public StoriesProgressView(Context context) {
        this(context, null);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StoriesProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        storiesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        removeAllViews();

        for (int i = 0; i < storiesCount; i++) {
            final ProgressBar p = createProgressBar();
            p.setMax(PROGRESS_MAX);
            progressBars.add(p);
            addView(p);
            if ((i + 1) < storiesCount) {
                addView(createSpace());
            }
        }
    }

    private ProgressBar createProgressBar() {
        ProgressBar p = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        p.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress));
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    public void setStoriesListener(StoriesListener storiesListener) {
        this.storiesListener = storiesListener;
    }

    /**
     * Skip current story
     */
    public void skip() {
        if (isComplete) return;
        ProgressBar p = progressBars.get(current);
        p.setProgress(p.getMax());
        animators.get(current).cancel();
    }

    /**
     * Reverse current story
     */
    public void reverse() {
        if (isComplete) return;
        ProgressBar p = progressBars.get(current);
        p.setProgress(0);
        isReverse = true;
        animators.get(current).cancel();
        if (0 <= (current - 1)) {
            p = progressBars.get(current - 1);
            p.setProgress(0);
            animators.get(--current).start();
        } else {
            animators.get(current).start();
        }
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    public void setStoryDuration(long duration) {
        animators.clear();
        for (int i = 0; i < progressBars.size(); i++) {
            animators.add(createAnimator(i, duration));
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    public void setStoriesCountWithDurations(@NonNull long[] durations) {
        storiesCount = durations.length;
        bindViews();
        animators.clear();
        for (int i = 0; i < progressBars.size(); i++) {
            animators.add(createAnimator(i, durations[i]));
        }
    }

    /**
     * Start progress animation
     */
    public void startStories() {
        animators.get(0).start();
    }

    /**
     * Start progress animation
     */
    public void startStoriesWithPause(int position) {
        animators.get(position).start();
        stopAnimation(position);
    }

    /**
     * Returns animator size.
     */
    public int getAnimatorSize() {
        return animators.size();
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        for (ObjectAnimator a : animators) {
            a.removeAllListeners();
            a.cancel();
        }
    }

    private long mAnimationTime;

    public void stopAnimation(int position) {
        ObjectAnimator animation = animators.get(position);
        if(animation != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animation.pause();
            } else {
                mAnimationTime = animation.getCurrentPlayTime();
                animation.cancel();
            }
        }
    }

    public void playAnimation(int position) {
        ObjectAnimator animation = animators.get(position);
        if (animation != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animation.resume();
            } else {
                animation.start();
                animation.setCurrentPlayTime(mAnimationTime);
            }
        }
    }

    private ObjectAnimator createAnimator(final int index, long duration) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBars.get(index), "progress", PROGRESS_MAX);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(duration);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                current = index;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isReverse) {
                    isReverse = false;
                    if (storiesListener != null) storiesListener.onPrev();
                    return;
                }
                int next = current + 1;
                if (next <= (animators.size() - 1)) {
                    if (storiesListener != null) storiesListener.onNext();
                    animators.get(next).start();
                    if (storiesListener != null) storiesListener.onNextStarted();
                } else {
                    isComplete = true;
                    if (storiesListener != null) storiesListener.onComplete();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animation;
    }
}
