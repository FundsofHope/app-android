/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.fundsofhope.androidapp.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.fundsofhope.androidapp.R;
import org.fundsofhope.androidapp.slidingtabs.fragments.SlidingTabsBasicFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navView;
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        configureToolbar();
        configureDrawer();
    }

    private void configureToolbar() {
        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("FundsofHope");

//        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
//                    mDrawerLayout.closeDrawer(Gravity.START);
//
//                } else {
//                    mDrawerLayout.openDrawer(Gravity.START);
//                }
//            }
//        });
    }

    private void configureDrawer() {
        // Configure drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navView = (NavigationView) findViewById(R.id.navView);

        setupDrawerContent(navView);


//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                R.string.drawer_open,
//                R.string.drawer_closed) {
//
//            public void onDrawerClosed(View view) {
//                supportInvalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mainToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
//  In case of Fragments
//        Fragment fragment = null;
//
//        Class fragmentClass;
//        switch(menuItem.getItemId()) {
//            case R.id.nav_first_fragment:
//                fragmentClass = MainActivity.class;
//                break;
//            case R.id.nav_second_fragment:
//                fragmentClass = TransitionFirstActivity.class;
//                break;
//            case R.id.nav_third_fragment:
//                fragmentClass = TransitionSecondActivity.class;
//                break;
//            default:
//                fragmentClass = MainActivity.class;
//        }
//
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.sample_content_fragment, fragment).commit();

        Intent intent = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.nav_second_fragment:
                intent = new Intent(this, TransitionFirstActivity.class);
                break;
            case R.id.nav_third_fragment:
                intent = new Intent(this, TransitionSecondActivity.class);
                break;
            default :
                intent = new Intent(this, MainActivity.class);
                break;
        }

        startActivity(intent);
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView parent, View view, int position, long id) {
//            selectItem(position);
//            drawerLayout.closeDrawer(drawerListView);
//
//        }
//    }
}
