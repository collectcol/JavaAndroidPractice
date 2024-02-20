package com.example.actionbar_tabs_ex;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 설정
        actionBar = getSupportActionBar();
        if (actionBar == null) {
            actionBar = getSupportActionBar();
        }

        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            // 탭 추가
            ActionBar.Tab tabSong = actionBar.newTab().setText("음악별").setTabListener(this);
            ActionBar.Tab tabArtist = actionBar.newTab().setText("가수별").setTabListener(this);
            ActionBar.Tab tabAlbum = actionBar.newTab().setText("앨범별").setTabListener(this);

            actionBar.addTab(tabSong);
            actionBar.addTab(tabArtist);
            actionBar.addTab(tabAlbum);
        }
    }

    // 탭 선택 시 호출되는 메서드
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // 프래그먼트 동적으로 추가
        MyTabFragment myTabFrag = new MyTabFragment();
        Bundle data = new Bundle();
        data.putString("tabName", tab.getText().toString());
        myTabFrag.setArguments(data);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, myTabFrag).commit();
    }

    // 다른 탭 선택 시 호출되는 메서드
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    // 탭이 재선택될 때 호출되는 메서드
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    // 프래그먼트 클래스
    public static class MyTabFragment extends Fragment {
        String tabName;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle data = getArguments();
            tabName = data.getString("tabName");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout baseLayout = new LinearLayout(getActivity());
            baseLayout.setOrientation(LinearLayout.VERTICAL);
            baseLayout.setLayoutParams(params);

            // 탭 이름에 따라 배경색 설정
            if (tabName.equals("음악별"))
                baseLayout.setBackgroundColor(Color.RED);
            else if (tabName.equals("가수별"))
                baseLayout.setBackgroundColor(Color.GREEN);
            else if (tabName.equals("앨범별"))
                baseLayout.setBackgroundColor(Color.BLUE);

            return baseLayout;
        }
    }
}
