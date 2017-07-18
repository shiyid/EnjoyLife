package com.stx.xhb.enjoylife.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.core.ui.BaseFragment;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.stx.xhb.enjoylife.R;
import com.stx.xhb.enjoylife.model.entity.VideoEntity;
import com.stx.xhb.enjoylife.ui.activity.VideoDetailActivity;
import com.stx.xhb.enjoylife.ui.adapter.VideoRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 视频推荐
 */
public class VideoFragment extends BaseFragment {

    @Bind(R.id.lv_video)
    ListView lvVideo;
    @Bind(R.id.ptr)
    PtrClassicFrameLayout ptr;
    private List<VideoEntity.IssueListEntity.ItemListEntity> list = new ArrayList<>();
    private String nextUrl;
    private VideoRecycleAdapter mAdapter;
    private boolean isRefresh;
    private boolean isRun;
    private RequestQueue mQueue;

    public VideoFragment() {
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_video;
    }

    @Override
    protected void onInitView() {
        mQueue = Volley.newRequestQueue(getActivity());
        setListener();
        setLvAdapter();
        downLoad("http://baobab.wandoujia.com/api/v2/feed?num=2");
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (lvVideo instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) lvVideo;
                return absListView.getChildCount() > 0 &&
                        (absListView.getFirstVisiblePosition() > 0 ||
                                absListView.getChildAt(0).getTop() < absListView.getPaddingTop());

            } else {
                return ViewCompat.canScrollVertically(lvVideo, -1) || lvVideo.getScrollY() > 0;
            }

        } else {

            return ViewCompat.canScrollVertically(lvVideo, -1);

        }

    }

    private void setListener() {

        ptr.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !canChildScrollUp();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                isRefresh = true;
                downLoad("http://baobab.wandoujia.com/api/v2/feed?num=2");
            }
        });
        //单个的点击事件
        lvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
                Bundle bundle = new Bundle();
                VideoEntity.IssueListEntity.ItemListEntity.DataEntity data = list.get(position).getData();
                if (!"video".equals(list.get(position).getType())) {
                    return;
                }
                bundle.putString("title", data.getTitle());
                //获取到时间
                int duration = data.getDuration();
                int mm = duration / 60;//分
                int ss = duration % 60;//秒
                String second = "";//秒
                String minute = "";//分
                if (ss < 10) {
                    second = "0" + String.valueOf(ss);
                } else {
                    second = String.valueOf(ss);
                }
                if (mm < 10) {
                    minute = "0" + String.valueOf(mm);
                } else {
                    minute = String.valueOf(mm);//分钟
                }
                bundle.putString("time", "#" + data.getCategory() + " / " + minute + "'" + second + '"');
                bundle.putString("desc", data.getDescription());//视频描述
                bundle.putString("blurred", data.getCover().getBlurred());//模糊图片地址
                bundle.putString("feed", data.getCover().getFeed());//图片地址
                bundle.putString("video", data.getPlayUrl());//视频播放地址
                bundle.putInt("collect", data.getConsumption().getCollectionCount());//收藏量
                bundle.putInt("share", data.getConsumption().getShareCount());//分享量
                bundle.putInt("reply", data.getConsumption().getReplyCount());//回复数量
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        lvVideo.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (!isRun) {
                        isRun = true;
                        downLoad(nextUrl);
                    }
                }
            }
        });


    }

    private void downLoad(String url) {
        final StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson mGson = new Gson();
                        VideoEntity homePicEntity = mGson.fromJson(response, VideoEntity.class);
                        List<VideoEntity.IssueListEntity> issueList = homePicEntity.getIssueList();
                        VideoEntity.IssueListEntity issueListEntity = issueList.get(0);
                        List<VideoEntity.IssueListEntity.ItemListEntity> itemList = issueListEntity.getItemList();
                        VideoEntity.IssueListEntity issueListEntity2 = issueList.get(1);
                        List<VideoEntity.IssueListEntity.ItemListEntity> itemList1 = issueListEntity2.getItemList();
                        isRun = false;

                        //刷新需要清除数据
                        if (isRefresh) {
                            list.removeAll(list);
                            ptr.refreshComplete();
                            isRefresh = false;
                        }
                        list.addAll(itemList);
                        list.addAll(itemList1);
                        nextUrl = homePicEntity.getNextPageUrl();

                        myNotify();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isRun = false;
                        if (isRefresh) {
                            ptr.refreshComplete();
                        }
                    }
                }
        );
        mQueue.add(request);
        mQueue.start();
    }

    public void myNotify() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

    }

    private void setLvAdapter() {

        mAdapter = new VideoRecycleAdapter(getActivity(), list);
        lvVideo.setAdapter(mAdapter);
    }

}