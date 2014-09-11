package org.reclaimphilly.android;


import android.content.Intent;
import android.support.v4.app.Fragment;


/**
 * Created by gosullivan on 8/12/13.
 */
public class PropertyDetailsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {



        Property parceledProperty = getParceledPropertyFromIntent();



        return PropertyDetailsFragment.newInstance(parceledProperty);
    }

//    public void submitReportButtonClick(View v){
//
//        return
//    }

    private Property getParceledPropertyFromIntent()
    {
        Intent i = this.getIntent();
        ParseObjectWrapper pow = (ParseObjectWrapper)i.getParcelableExtra(Property.t_tablename);
        Property parceledProperty = new Property(pow);
        return parceledProperty;
    }
}
