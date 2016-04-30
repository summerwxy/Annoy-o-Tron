package fun.wxy.annoy_o_tron.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import fun.wxy.annoy_o_tron.R;

/**
 * Created by 0_o on 2016/4/30.
 */
public class ScrollingFabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int toolbarHeight;
    public ScrollingFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin - 30; // 隱藏不完全, 所以隨便加個數字
            int distanceToScroll = child.getHeight() - fabBottomMargin;
            float ratio = (float) dependency.getY() / (float) toolbarHeight;
            child.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}
