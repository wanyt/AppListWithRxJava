package com.applistwithrxjava.wanyt;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.applistwithrxjava.wanyt.adapter.CatalogAdapter;
import com.applistwithrxjava.wanyt.bean.CatalogBean;
import com.applistwithrxjava.wanyt.bean.data.DataCatalogList;
import com.applistwithrxjava.wanyt.fragment.BaseFragment;
import com.applistwithrxjava.wanyt.fragment.FragmentDefault;
import com.applistwithrxjava.wanyt.fragment.FragmentDistinct;
import com.applistwithrxjava.wanyt.fragment.FragmentFilter;
import com.applistwithrxjava.wanyt.fragment.FragmentFrom;
import com.applistwithrxjava.wanyt.fragment.FragmentGroupby;
import com.applistwithrxjava.wanyt.fragment.FragmentInterval;
import com.applistwithrxjava.wanyt.fragment.FragmentJust;
import com.applistwithrxjava.wanyt.fragment.FragmentMap;
import com.applistwithrxjava.wanyt.fragment.FragmentMerge;
import com.applistwithrxjava.wanyt.fragment.FragmentReduce;
import com.applistwithrxjava.wanyt.fragment.FragmentSkip;
import com.applistwithrxjava.wanyt.fragment.FragmentTake;
import com.applistwithrxjava.wanyt.fragment.FragmentZip;
import com.applistwithrxjava.wanyt.listener.ItemClickListener;
import com.applistwithrxjava.wanyt.recyclerdivider.LinearDivider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 每个方法的演示放在了Fragment中，
 * 所有的Fragment继承BaseFragment，
 * 已安装的应用列表的获取和Fragment的一些数据的设置放在了BaseFragment中
 */
public class MainActivity extends AppCompatActivity {

    private final String tag = ".wanyt.MainActivity";

    @BindView(R.id.rv_catalog)
    RecyclerView recyclerView_catalog;
    @BindView(R.id.fl_main_container)
    FrameLayout flMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<CatalogBean> catalogList = DataCatalogList.getInstance().getCatalogList();
        initCatalog(catalogList);
        initDefaultView();
    }

    /**
     * 初始化默认显示
     */
    private void initDefaultView() {
        FragmentDefault fragmentDefault = new FragmentDefault();
        CatalogBean item = new CatalogBean();
        item.flag = " ";
        item.method = " ";
        item.fullName = " ";
        item.describe = " ";
        item.bulletGraphs = 0;
        manageFragment(fragmentDefault, item);
    }

    /**
     * 方法目录布局
     *
     * @param catalogList
     */
    private void initCatalog(ArrayList<CatalogBean> catalogList) {
        recyclerView_catalog.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_catalog.addItemDecoration(new LinearDivider(this, LinearLayoutManager.VERTICAL));
        CatalogAdapter catalogAdapter = new CatalogAdapter(this, catalogList);
        recyclerView_catalog.setAdapter(catalogAdapter);
        catalogAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position, Object obj) {
                catalogItemClick((CatalogBean) obj);
            }
        });
    }

    /**
     * 方法目录的点击事件
     *
     * @param item
     */
    private void catalogItemClick(CatalogBean item) {

        BaseFragment fragment = null;

        switch (item.flag) {
            case DataCatalogList.FROM:
                fragment = new FragmentFrom();
                break;
            case DataCatalogList.JUST:
                fragment = new FragmentJust();
                break;
            case DataCatalogList.INTERVAL:
                fragment = new FragmentInterval();
                break;
            case DataCatalogList.FILTER:
                fragment = new FragmentFilter();
                break;
            case DataCatalogList.TAKE:
                fragment = new FragmentTake();
                break;
            case DataCatalogList.DISTINCT:
                fragment = new FragmentDistinct();
                break;
            case DataCatalogList.SKIP:
                fragment = new FragmentSkip();
                break;
            case DataCatalogList.MAP:
                fragment = new FragmentMap();
                break;
            case DataCatalogList.MERGE:
                fragment = new FragmentMerge();
                break;
            case DataCatalogList.ZIP:
                fragment = new FragmentZip();
                break;
            case DataCatalogList.REDUCE:
                fragment = new FragmentReduce();
                break;
            case DataCatalogList.GROUPBY:
                fragment = new FragmentGroupby();
                break;
            default:
                fragment = new FragmentDefault();
                item.flag = " ";
                item.method = " ";
                item.fullName = " ";
                item.describe = " ";
                item.bulletGraphs = 0;
                break;

        }

        if (fragment == null) {
            throw new NullPointerException("装载app list的fragment为空 " + tag);
        }

        manageFragment(fragment, item);
    }

    private void manageFragment(BaseFragment fragment, CatalogBean item) {
        //当切换Fragment的时候，发送消息通知Fragment做一些释放操作
        RxBus.getInstance().post(new BusEventModel(Constants.EVENT_OBSERVER_UNREGISTER, true));

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CATALOG_PARAMS, item);
        fragment.setArguments(bundle);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_main_container, fragment)
                .commit();
    }

}
