package com.instamour.mathu.cmpln;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class TabPagerAdapter extends FragmentStatePagerAdapter{
    private String[] titles = {"All", "Followers", "Who I Follow"};
    private String email, university;
    private FragmentManager fm;
    private Map<Integer, String> mFragmentTags;
    public TabPagerAdapter(FragmentManager fm){
        super(fm);
        this.fm = fm;
    }
    public TabPagerAdapter(FragmentManager fm, String email, String university){
        super(fm);
        this.fm = fm;
        this.email = email;
        this.university = university;
        mFragmentTags =  new HashMap<Integer, String>();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
             //   Log.d("All Comments", "Tab");
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("university", university);
                AllComments ac = new AllComments();
                ac.setArguments(bundle);
                return ac;
            case 1:
               // Log.d("Followers Comment", "Tab");
                Bundle bundle1 = new Bundle();
                bundle1.putString("email", email);
                bundle1.putString("university", university);
                FollowerComments fc = new FollowerComments();
                fc.setArguments(bundle1);
                return fc;
            case 2:
                //Log.d("Who I follow", "Tab");
                Bundle bundle2 = new Bundle();
                bundle2.putString("email", email);
                bundle2.putString("university", university);
                WhoIFollowComments wif = new WhoIFollowComments();
                wif.setArguments(bundle2);
                return wif;
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj =  super.instantiateItem(container, position);
        if(obj instanceof Fragment){
            Fragment f =  (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return  obj;
    }

    public Fragment getFragment(int position){
        String tag = mFragmentTags.get(position);
        if(tag ==  null)
            return null;
        return fm.findFragmentByTag(tag);
    }
    @Override
    public int getCount() {
        return 3;
    }
}
