package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.TitlePageIndicator;
import org.ligi.axt.AXT;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.Pass;
import org.ligi.passandroid.model.PassImpl;
import org.ligi.passandroid.ui.edit_fragments.BarcodeEditFragment;
import org.ligi.passandroid.ui.edit_fragments.CategoryPickFragment;
import org.ligi.passandroid.ui.edit_fragments.ColorPickFragment;
import org.ligi.passandroid.ui.edit_fragments.ImageEditFragment;
import org.ligi.passandroid.ui.edit_fragments.MetaDataFragment;

public class PassEditActivity extends AppCompatActivity {

    @InjectView(R.id.passEditPager)
    ViewPager viewPager;

    @InjectView(R.id.titlesViewPagerIndicator)
    TitlePageIndicator titlePageIndicator;

    private PassImpl pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        ButterKnife.inject(this);

        final Pass currentPass = App.getPassStore().getCurrentPass();
        if (currentPass != null) {
            pass = (PassImpl) currentPass;
        } else {
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPager();
    }

    private void setupViewPager() {
        viewPager.setAdapter(new CreateFragmentPager(getSupportFragmentManager()));

        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setTextColor(0xFF424242);
        titlePageIndicator.setSelectedColor(0xFF000000);
    }

    private class CreateFragmentPager extends FragmentPagerAdapter {
        public CreateFragmentPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Category";
                case 1:
                    return "MetaData";
                case 2:
                    return "Images";
                case 3:
                    return "Color";

                case 4:
                default:
                    return "BarCode";
            }
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new CategoryPickFragment();
                case 1:
                    return new MetaDataFragment();
                case 2:
                    return new ImageEditFragment();
                case 3:
                    return new ColorPickFragment();

                case 4:
                default:
                    return new BarcodeEditFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    @Subscribe
    public void onPassRefresh(PassRefreshEvent event) {
        refresh(event.pass);
    }

    private void refresh(Pass pass) {
        //new PassViewHolder(getWindow().getDecorView().findViewById(R.id.pass_card)).apply(pass, this);
        new PassViewHolder(getWindow().getDecorView().findViewById(R.id.pass_card)).apply(pass, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getBus().register(this);
        refresh(pass);
    }

    @Override
    protected void onPause() {
        App.getBus().unregister(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_save:
                App.getPassStore().setCurrentPass(pass);
                pass.save(App.getPassStore());
                AXT.at(this).startCommonIntent().activityFromClass(PassViewActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
