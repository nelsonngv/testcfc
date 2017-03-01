package com.pbasolutions.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.adapter.MenuDrawerRVA;
import com.pbasolutions.android.model.NavDrawerItem;
import com.pbasolutions.android.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentDrawer extends Fragment {
    /**
     *
     */
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private MenuDrawerRVA adapter;
    private View containerView;
    public static String[] titles = null;
    private TextView usernameTextView;
    private FragmentDrawerListener drawerListener;
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     *
     */
    public FragmentDrawer() {

    }

    /**
     *
     * @param listener
     */
    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    /**
     *
     * @return
     */
    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PandoraMain.instance != null) {
            if (PandoraMain.instance.menuList == null)
                titles = EMPTY_ARRAY;
            else titles = PandoraMain.instance.menuList;
        }
        else titles = EMPTY_ARRAY;
        // drawer labels
//        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        usernameTextView = (TextView) layout.findViewById(R.id.username_text);
        adapter = new MenuDrawerRVA(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }

    /**
     *
     * @param fragmentId
     * @param drawerLayout
     * @param toolbar
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    /**
     *
     */
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    /**
     *
     */
    static class RecyclerTouchListener implements FastScrollRecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /**
     *
     */
    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    /**
     *
     * @param username
     */
    public void resetUsername(String username){
        usernameTextView.setText(username);
    }

    /**
     *
     */
    public void invalidateView() {
        getView().post(new Runnable() {
            @Override
            public void run() {
                getView().invalidate();
            }
        });
    }

    public void updateDrawer() {
        if (PandoraMain.instance != null) {
//            if (PandoraMain.instance == null || PandoraMain.instance.menuList == null || PandoraMain.instance.menuList.length == 0)
//                titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
//            else titles = PandoraMain.instance.menuList;
            if (PandoraMain.instance.menuList == null)
                titles = EMPTY_ARRAY;
            else titles = PandoraMain.instance.menuList;
        }
        else titles = EMPTY_ARRAY;
        adapter = new MenuDrawerRVA(getActivity(), getData());
        recyclerView.setAdapter(adapter);
    }
}
