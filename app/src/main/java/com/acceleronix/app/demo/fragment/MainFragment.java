package com.acceleronix.app.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.acceleronix.app.demo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

public class MainFragment extends SupportFragment {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    private SupportFragment[] mFragments = new SupportFragment[3];

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.iv_tab1)
    ImageView iv_tab1;
    @BindView(R.id.iv_tab2)
    ImageView iv_tab2;
    @BindView(R.id.iv_tab3)
    ImageView iv_tab3;

    @BindView(R.id.tv_tab1)
    TextView tv_tab1;
    @BindView(R.id.tv_tab2)
    TextView tv_tab2;
    @BindView(R.id.tv_tab3)
    TextView tv_tab3;

    private Unbinder unbinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(DeviceFragment.class);
        if (firstFragment == null) {
            System.out.println("firstFragment == null");
            selectTabOne();

            mFragments[FIRST] = DeviceFragment.newInstance();
            mFragments[SECOND] = DeviceGroupFragment.newInstance();
            mFragments[THIRD] = MineFragment.newInstance();


            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]
            );
        } else {
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(DeviceGroupFragment.class);
            mFragments[THIRD] = findChildFragment(MineFragment.class);
            int  prePosition =  savedInstanceState.getInt("prePosition",0);
          //  System.out.println("savedInstanceState prePosition--:"+prePosition);
            resetTab(prePosition);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

   int  prePosition = 0;

    @OnClick({R.id.rl_tab1, R.id.rl_tab2, R.id.rl_tab3})
    public void buttonClick(View view) {

        switch (view.getId()) {
            case R.id.rl_tab1:

                selectTabOne();

                if(prePosition!=0)
                {
                    showHideFragment(mFragments[0], mFragments[prePosition]);
                }

                prePosition=0;
                break;

            case R.id.rl_tab2:

                selectTabTwo();

                showHideFragment(mFragments[1], mFragments[prePosition]);

                prePosition=1;
                break;


            case R.id.rl_tab3:
                selectTabThree();
                showHideFragment(mFragments[2], mFragments[prePosition]);
                prePosition=2;
                break;
        }

    }

    public void selectTwoProject(){
        selectTabTwo();
        showHideFragment(mFragments[1], mFragments[prePosition]);
        prePosition=1;
      //  EventBus.getDefault().post(new RefreshMainGongDan());
    }

    public void  resetTab(int type)
    {
        prePosition=type;
        switch (type)
        {
            case 0 :
                selectTabOne();
                break;

            case 1 :
                selectTabTwo();
                break;
            case 2 :
                selectTabThree();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object object) {

//        if(object instanceof GoToTuiJian)
//        {
//            selectTabFour();
//            showHideFragment(mFragments[3], mFragments[prePosition]);
//            prePosition=3;
//        }
//        else if(object instanceof GoToBuyHouse)
//        {
//            selectTabTwo();
//            showHideFragment(mFragments[1], mFragments[prePosition]);
//            prePosition=1;
//        }
//        else if(object instanceof PushOneEvent)
//        {
//            selectTabOne();
//            if(prePosition!=0)
//            {
//                showHideFragment(mFragments[0], mFragments[prePosition]);
//            }
//            prePosition=0;
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void selectTabOne() {
        iv_tab1.setBackgroundResource(R.mipmap.tab_home_1);
        tv_tab1.setTextColor(getResources().getColor(R.color.main2));
        iv_tab2.setBackgroundResource(R.mipmap.tab_msg_0);
        tv_tab2.setTextColor(getResources().getColor(R.color.main2));
        iv_tab3.setBackgroundResource(R.mipmap.tab_my_0);
        tv_tab3.setTextColor(getResources().getColor(R.color.main2));

    }

    private void selectTabTwo() {
        iv_tab1.setBackgroundResource(R.mipmap.tab_home_0);
        tv_tab1.setTextColor(getResources().getColor(R.color.main2));
        iv_tab2.setBackgroundResource(R.mipmap.tab_msg_1);
        tv_tab2.setTextColor(getResources().getColor(R.color.main2));
        iv_tab3.setBackgroundResource(R.mipmap.tab_my_0);
        tv_tab3.setTextColor(getResources().getColor(R.color.main2));


    }

    private void selectTabThree() {
        iv_tab1.setBackgroundResource(R.mipmap.tab_home_0);
        tv_tab1.setTextColor(getResources().getColor(R.color.main2));
        iv_tab2.setBackgroundResource(R.mipmap.tab_msg_0);
        tv_tab2.setTextColor(getResources().getColor(R.color.main2));
        iv_tab3.setBackgroundResource(R.mipmap.tab_my_1);
        tv_tab3.setTextColor(getResources().getColor(R.color.main2));

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("prePosition",prePosition);
        super.onSaveInstanceState(outState);
    }
}
