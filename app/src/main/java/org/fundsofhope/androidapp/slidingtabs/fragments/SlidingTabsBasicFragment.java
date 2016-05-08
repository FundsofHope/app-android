/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fundsofhope.androidapp.slidingtabs.fragments;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import org.fundsofhope.androidapp.R;
import org.fundsofhope.androidapp.activities.CircleTransform;
import org.fundsofhope.androidapp.slidingtabs.views.SlidingTabLayout;


public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBaseFragment";

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    class SamplePagerAdapter extends PagerAdapter {
        final String [] TITLES = {"Home", "Registered NGO's", "Profile"};

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if(position==0){
                view = getActivity().getLayoutInflater().inflate(R.layout.home_tab, container, false);
            }
            else if(position==2){
                view = getActivity().getLayoutInflater().inflate(R.layout.profile, container, false);
                SharedPreferences prefs;
                ImageView pic= (ImageView) view.findViewById(R.id.image);
                prefs = getActivity().getSharedPreferences("application_settings", 0);
                Picasso.with(getActivity()).load(prefs.getString("pic","")).transform(new CircleTransform()).into(pic);

            }
            else
            view = getActivity().getLayoutInflater().inflate(R.layout.item_sliding_pager, container, false);
            container.addView(view);
            return view;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }

}
