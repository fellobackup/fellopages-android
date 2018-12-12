package ru.dimorinny.showcasecard.position;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.widget.ScrollView;

import ru.dimorinny.showcasecard.util.ActivityUtils;
import ru.dimorinny.showcasecard.util.NavigationBarUtils;

public class BottomRightCustom implements ShowCasePosition {

    int dimen;
    public BottomRightCustom(int dimen) {
        this.dimen = dimen;
    }

    @Override
    public PointF getPosition(Activity activity) {
        float width = (float) activity.getWindow().getDecorView().getWidth();
        float height = (float) activity.getWindow().getDecorView().getHeight();

        switch (ActivityUtils.getOrientation(activity)) {
            case Configuration.ORIENTATION_LANDSCAPE:
                return new PointF(
                        width - NavigationBarUtils.navigationBarMarginForRightOrientation(activity),
                        height
                );
            default:
                return new PointF(
                        width - dimen/2,
                        height - dimen
                );
        }
    }

    @Nullable
    @Override
    public Point getScrollPosition(@Nullable ScrollView scrollView) {
        return null;
    }
}
