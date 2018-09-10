
package com.example.pc_asus.tinhnguyenvien;

        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;


public class MapViewFragment  extends Fragment {



    View view;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState){


        view = inflater.inflate(R.layout.fragment_video_call_view, container, false);
        Log.e("abc", "camera");

        return view;

    }
}