package TransactionFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madhushika.carbc_android_v3.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeasingPaymentFragment extends Fragment {


    public LeasingPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leasing_payment, container, false);
    }

}
