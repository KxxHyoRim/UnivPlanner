package edu.sungshin.univplanner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_lecture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_lecture extends Fragment {

    ListView lecture_list;
    ListViewAdapter listview_adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_lecture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_lecture.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_lecture newInstance(String param1, String param2) {
        fragment_lecture fragment = new fragment_lecture();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lecture, container, false);

        /*--------------Custom ListView---------------*/


        //Adapter 생성
        listview_adapter = new ListViewAdapter();

        //리스트뷰 참조 및 adapter 달기
        lecture_list = (ListView) rootView.findViewById(R.id.listView_lecture);
        lecture_list.setAdapter(listview_adapter);

        //아이템 추가 ------ 크롤링 해온 거로 넣어야함
        ///일단은 임시로 넣어보겠삼.
        listview_adapter.addItem("D-4","알고리즘","문제와 알고리즘의 표기","2021.03.09 오전 12:00~ 2021.03.14 오후 11:59","미수강");

        return rootView;
    }
}