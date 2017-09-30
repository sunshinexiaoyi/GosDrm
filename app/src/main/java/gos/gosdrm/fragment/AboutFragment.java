package gos.gosdrm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gos.gosdrm.R;
import gos.gosdrm.adapter.ReuseAdapter;


public class AboutFragment extends Fragment {
    private ListView mListview;
    private View view;
    ReuseAdapter<String> listAdapter;
    List<String> mList;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            Log.e("AboutFragment消息", "view复用");
            return view;
        }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about, container, false);
        initData();
        initListview();
        return view;
    }

    public void initData() {
        mList = new ArrayList<>();
        mList.add("功能介绍");
        mList.add("投诉");
        mList.add("检查新版本");
    }

    public void  initListview() {
        mListview = view.findViewById(R.id.aus_listView);
        listAdapter = new  ReuseAdapter<String>(getActivity(), R.layout.item_about_us) {
            @Override
            public void bindView(Holder holder, String obj) {
                holder.setText(R.id.item_aus_tv, obj);
            }
        };

        int i = 0;
        for (String s : mList) {
            listAdapter.add(i, mList.get(i));
            i++;
        }
        mListview.setAdapter(listAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Toast.makeText(getActivity(), "爱奇艺影音是一款专注视频播放的客户端软件，热播大剧、最新剧集，八卦娱乐，奇艺在手，应有尽有。主要功能：\n" +
                                "1、免费使用：免费安装，免费观看高清正版影视。\n" +
                                "2、内容丰富：最新影视、最热综艺、旅游、纪录片，支持奇艺网全部内容。\n" +
                                "3、播放流畅：比在线观看更流畅，越多人看越流畅。\n" +
                                "4、独家功能：边选边看，全屏关灯，清晰度调节。", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "投诉不了", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(), "已经是最新版本了", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mListview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(mList.size() > 1) {
                    if (0 == i) {
                       // Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();

                    } else if((mList.size()-1) == i) {
                        //Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
