package com.example.eletronicengineer.fragment.sdf

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eletronicengineer.R
import com.example.eletronicengineer.adapter.ListAdapterForDemand
import com.example.eletronicengineer.adapter.RecyclerviewAdapter
import com.example.eletronicengineer.aninterface.Movie
import com.example.eletronicengineer.custom.CustomDialog
import com.example.eletronicengineer.custom.LoadingDialog
import com.example.eletronicengineer.utils.*
import com.example.eletronicengineer.utils.getRequirementLease
import com.example.eletronicengineer.utils.getRequirementPerson
import com.example.eletronicengineer.utils.getRequirementTeam
import kotlinx.android.synthetic.main.demand.view.*
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder


lateinit var key:MutableList<String>
lateinit var value:MutableList<String>
class DemandInformationFragment: Fragment(){

    val selectOption1Items:MutableList<String> =ArrayList()
    val selectOption2Items:MutableList<MutableList<String>> =ArrayList()
    val selectOption3Items:MutableList<MutableList<MutableList<String>>> =ArrayList()
    var mPersonAdapter:ListAdapterForDemand?=null
    var checkedItems= arrayOf(false,false,false,false,false,false)
    var selectContentElect = arrayOf("", "", "", "", "", "")

    val mCountPerPageForPerson = 30
    var mPageNumberForPerson = 1
    val mCountPerPageForTeam = 30
    var mPageNumberForTeam = 1
    val mCountPerPageForLease = 30
    var mPageNumberForLease = 1
    val mCountPerPageForThird = 30
    var mPageNumberForThird = 1
    var mIsLastPageForPerson = false
    var mIsLastPageForTeam = false
    var mIsLastPageForLease = false
    var mIsLastPageForThird = false
    var mIsLoadingPerson = false
    var mIsLoadingTeam = false
    var mIsLoadingLease = false
    var mIsLoadingThird = false
    lateinit var mView: View
    //搜索栏三个变量
    var demandSite:String=""
    var demandVariety:String=""
    var demandElect:String=""
    //
    var theflag=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
          mView = inflater.inflate(R.layout.demand, container, false)
        mView.demand_swipe_refresh.setColorSchemeColors(Color.rgb(47,223,189))
        val Option1Items= listOf("普工","负责人","工程师","设计员","特种作业","专业操作","勘测员","驾驶员","九大员","注册类","其他") as MutableList<String>
        val Option2Items= listOf(listOf("普工"),
            listOf("配网项目负责人", "配网班组负责人", "主网项目负责人", "主网基础负责人", "主网组塔负责人","主网架线负责人","变电项目负责人","变电土建负责人","变电一次负责人","变电二次负责人","变电调试负责人"),
            listOf("助理工程师", "中级工程师", "高级工程师"),
            listOf("主网", "配网", "变电"),
            listOf("低压电工作业","高压电工作业","电力电缆作业","继电保护作业","电气试验作业","融化焊接与热切割作业","登高架设作业"),
            listOf("压接操作","机动绞磨操作","牵张设备操作","起重机械操作","钢筋工","混凝土工","木工","模板工","油漆工","砌筑工","通风工","打桩工","架子工"),
            listOf("测量工"), listOf("驾驶证A1","驾驶证A2","驾驶证A3","驾驶证B1","驾驶证B2","驾驶证C1","驾驶证C2","驾驶证C3","驾驶证D","驾驶证E"),
            listOf("施工员","安全员","质量员","材料员","资料员","预算员","标准员","机械员","劳务员"), listOf("造价工程师","一级建造师","安全工程师","电气工程师"),
            listOf("其他")) as MutableList<MutableList<String>>
        mView.tv_demand_type_select.movementMethod=ScrollingMovementMethod.getInstance()
        mView.tv_demand_site_select.movementMethod=ScrollingMovementMethod.getInstance()
        mView.tv_demand_elect_standard_select.movementMethod=ScrollingMovementMethod.getInstance()
        mView.tv_demand_type_select.setOnClickListener {
            initDemandPerson(mView,Option1Items,Option2Items,1)
        }
        initProjectSite()
        mView.tv_demand_site_select.setOnClickListener {
            initSite(mView,1)
        }
        mView.tv_demand_site_clear.setOnClickListener {
            mView.tv_demand_site_select.text="选择地点"
            mView.tv_demand_site_clear.visibility=View.GONE
            demandSite=""
            key= arrayListOf("page","number","projectSite","requirementMajor")
            value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
            mPageNumberForPerson = 1
            mIsLastPageForPerson = false
            mPersonAdapter!!.notifyMovie()
            demandPerson(mView)
        }
        mView.tv_demand_type_clear.setOnClickListener {
            mView.tv_demand_type_select.text="选择需求类别"
            mView.tv_demand_type_clear.visibility=View.GONE
            demandVariety=""
            key= arrayListOf("page","number","projectSite","requirementMajor")
            value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
            mPageNumberForPerson = 1
            mIsLastPageForPerson = false
            mPersonAdapter!!.notifyMovie()
            demandPerson(mView)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPersonAdapter = ListAdapterForDemand(activity!!)
        key= arrayListOf("page","number")
        value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson) as MutableList<String>
        demandPerson(view)
        select(view)
    }
    //需求个人
    private fun demandPerson(view:View) {
        loadDemandPersonData()
        theflag=1
        view.tv_demand_content.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mPersonAdapter
        }
        view.tv_demand_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var m = view.tv_demand_content.layoutManager as LinearLayoutManager
                if (m.findLastVisibleItemPosition() == m.itemCount - 1) {
                    selectScroll(theflag,view)
                }
            }
        })
        view.demand_swipe_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mView.demand_swipe_refresh.isRefreshing=false
                mPageNumberForPerson=1
                mIsLastPageForPerson=false
                mPersonAdapter!!.notifyMovie()
                value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                selectScroll(theflag,view)
            }
        })
    }
    //需求团队
    private fun demandTeam(view:View) {
        loadDemandTeamData()
        theflag=2
        view.tv_demand_content.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mPersonAdapter
        }
        view.tv_demand_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val m = view.tv_demand_content.layoutManager as LinearLayoutManager
                if (m.findLastVisibleItemPosition() == m.itemCount - 1) {
                    selectScroll(theflag,view)
                }
            }
        })
        view.demand_swipe_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mView.demand_swipe_refresh.isRefreshing=false
                mPageNumberForTeam=1
                mIsLastPageForTeam=false
                mPersonAdapter!!.notifyMovie()
                value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                    selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                    selectContentElect[4],selectContentElect[5]) as MutableList<String>
                selectScroll(theflag,view)
            }
        })
    }
    //需求租赁
    private fun demandLease(view:View) {
        loadDemandLeaseData()
        theflag=3
        view.tv_demand_content.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mPersonAdapter
        }
        view.tv_demand_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var m = view.tv_demand_content.layoutManager as LinearLayoutManager
                if (m.findLastVisibleItemPosition() == m.itemCount - 1) {
                    selectScroll(theflag,view)
                }
            }
        })
        view.demand_swipe_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mView.demand_swipe_refresh.isRefreshing=false
                mPageNumberForLease=1
                mIsLastPageForLease=false
                mPersonAdapter!!.notifyMovie()
                value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                selectScroll(theflag,view)
            }
        })
    }
    //需求三方
    private fun demandThird(view:View) {
        loadDemandThirdData()
        theflag=4
        view.tv_demand_content.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mPersonAdapter
        }
        view.tv_demand_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var m = view.tv_demand_content.layoutManager as LinearLayoutManager
                if (m.findLastVisibleItemPosition() == m.itemCount - 1) {
                    selectScroll(theflag,view)
                }
            }
        })
        view.demand_swipe_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mView.demand_swipe_refresh.isRefreshing=false
                mPageNumberForThird=1
                mIsLastPageForThird=false
                mPersonAdapter!!.notifyMovie()
                value= arrayListOf(mPageNumberForThird,mCountPerPageForThird,demandVariety) as MutableList<String>
                selectScroll(theflag,view)
            }
        })
    }
    private fun select(view:View) {
        val title = arrayListOf("需求个人","需求团队","需求租赁","需求三方")
        for(i in 0 until title.size)
        {
            view.demand_direction.addTab(view.demand_direction.newTab().setText(title[i]))
        }
        view.demand_direction.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //                添加选中Tab的逻辑
                when (tab.text) {
                    "需求个人"->{
                        demandSite=""
                        demandVariety=""
                        demandElect=""
                        selectContentElect = arrayOf("", "", "", "", "", "")
                        mPageNumberForPerson = 1
                        mIsLastPageForPerson = false
                        mPersonAdapter!!.notifyMovie()
                        view.tv_demand_site_select.setOnClickListener {
                            initSite(view, 1)
                        }
                        key= arrayListOf("page","number","projectSite","requirementMajor")
                        value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                        demandPerson(view)
                        view.tv_demand_site_select.text="选择地点"
                        view.tv_demand_type_select.text="选择需求类别"
                        view.tv_demand_site_clear.visibility=View.GONE
                        view.tv_demand_type_clear.visibility=View.GONE
                        view.tv_demand_elect_standars_clear.visibility=View.GONE
                        view.tv_demand_elect_standard_select.visibility=View.GONE
                        view.tv_demand_site_select.visibility=View.VISIBLE
                        view.tv_demand_type_select.visibility=View.VISIBLE
                        val Option1Items= listOf("普工","负责人","工程师","设计员","特种作业","专业操作","勘测员","驾驶员","九大员","注册类","其他") as MutableList<String>
                        val Option2Items= listOf(listOf("普工"),
                            listOf("配网项目负责人", "配网班组负责人", "主网项目负责人", "主网基础负责人", "主网组塔负责人","主网架线负责人","变电项目负责人","变电土建负责人","变电一次负责人","变电二次负责人","变电调试负责人"),
                            listOf("助理工程师", "中级工程师", "高级工程师"),
                            listOf("主网", "配网", "变电"),
                            listOf("低压电工作业","高压电工作业","电力电缆作业","继电保护作业","电气试验作业","融化焊接与热切割作业","登高架设作业"),
                            listOf("压接操作","机动绞磨操作","牵张设备操作","起重机械操作","钢筋工","混凝土工","木工","模板工","油漆工","砌筑工","通风工","打桩工","架子工"),
                            listOf("测量工"), listOf("驾驶证A1","驾驶证A2","驾驶证A3","驾驶证B1","驾驶证B2","驾驶证C1","驾驶证C2","驾驶证C3","驾驶证D","驾驶证E"),
                            listOf("施工员","安全员","质量员","材料员","资料员","预算员","标准员","机械员","劳务员"), listOf("造价工程师","一级建造师","安全工程师","电气工程师"),
                            listOf("其他")) as MutableList<MutableList<String>>
                        view.tv_demand_type_select.setOnClickListener {
                            initDemandPerson(view,Option1Items,Option2Items,1)
                        }
                        view.tv_demand_site_clear.setOnClickListener {
                            view.tv_demand_site_select.text="选择地点"
                            view.tv_demand_site_clear.visibility=View.GONE
                            demandSite=""
                            key= arrayListOf("page","number","projectSite","requirementMajor")
                            value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForPerson = 1
                            mIsLastPageForPerson = false
                            mPersonAdapter!!.notifyMovie()
                            demandPerson(view)
                        }
                        view.tv_demand_type_clear.setOnClickListener {
                            view.tv_demand_type_select.text="选择需求类别"
                            view.tv_demand_type_clear.visibility=View.GONE
                            demandVariety=""
                            key= arrayListOf("page","number","projectSite","requirementMajor")
                            value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForPerson = 1
                            mIsLastPageForPerson = false
                            mPersonAdapter!!.notifyMovie()
                            demandPerson(view)
                        }
                    }
                    "需求团队"->{
                        demandSite=""
                        demandVariety=""
                        demandElect=""
                        selectContentElect = arrayOf("", "", "", "", "", "")
                        mPageNumberForTeam = 1
                        mIsLastPageForTeam = false
                        mPersonAdapter!!.notifyMovie()
                        view.tv_demand_site_select.setOnClickListener {
                            initSite(view, 2)
                        }
                        key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                        value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                            selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                            selectContentElect[4],selectContentElect[5]) as MutableList<String>
                        demandTeam(view)
                        view.tv_demand_site_select.text="选择地点"
                        view.tv_demand_type_select.text="选择需求类别"
                        view.tv_demand_elect_standard_select.text="选择电压等级"
                        view.tv_demand_site_clear.visibility=View.GONE
                        view.tv_demand_type_clear.visibility=View.GONE
                        view.tv_demand_elect_standars_clear.visibility=View.GONE
                        view.tv_demand_elect_standard_select.visibility=View.VISIBLE
                        view.tv_demand_type_select.visibility=View.VISIBLE
                        view.tv_demand_site_select.visibility=View.VISIBLE
                        val Option1Items= listOf("变电施工队","主网施工队","配网施工队","测量设计","马帮运输","桩基服务","非开挖顶拉管作业","试验调试","跨越架","运行维护") as MutableList<String>
                        val Option2Items= listOf("0.4KV","10KV","35KV","110KV","220KV","500KV")
                        view.tv_demand_type_select.setOnClickListener {
                            initDemandTeam(view,Option1Items,1)
                        }
                        view.tv_demand_elect_standard_select.setOnClickListener {
                            initElectDialog(view, Option2Items as MutableList<String>)
                        }
                        //清空电压等级筛选
                        view.tv_demand_elect_standars_clear.setOnClickListener {
                            view.tv_demand_elect_standard_select.text="选择电压等级"
                            view.tv_demand_elect_standars_clear.visibility=View.GONE
                             checkedItems= arrayOf(false,false,false,false,false,false)
                             selectContentElect = arrayOf("", "", "", "", "", "")
                            demandElect=""
                            key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                            value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                                selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                                selectContentElect[4],selectContentElect[5]) as MutableList<String>
                            mPageNumberForTeam = 1
                            mIsLastPageForTeam = false
                            mPersonAdapter!!.notifyMovie()
                            demandTeam(view)
                        }
                        //清空地点筛选
                        view.tv_demand_site_clear.setOnClickListener {
                            view.tv_demand_site_select.text="选择地点"
                            view.tv_demand_site_clear.visibility=View.GONE
                            demandSite=""
                            key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                            value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                                selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                                selectContentElect[4],selectContentElect[5]) as MutableList<String>
                            mPageNumberForTeam = 1
                            mIsLastPageForTeam = false
                            mPersonAdapter!!.notifyMovie()
                            demandTeam(view)
                        }
                        //清空类别筛选
                        view.tv_demand_type_clear.setOnClickListener {
                            view.tv_demand_type_select.text="选择需求类别"
                            view.tv_demand_type_clear.visibility=View.GONE
                            demandVariety=""
                            key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                            value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                                selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                                selectContentElect[4],selectContentElect[5]) as MutableList<String>
                            mPageNumberForTeam = 1
                            mIsLastPageForTeam = false
                            mPersonAdapter!!.notifyMovie()
                            demandTeam(view)
                        }
                    }
                    "需求租赁"->{
                        demandSite=""
                        demandVariety=""
                        demandElect=""
                        selectContentElect = arrayOf("", "", "", "", "", "")
                        mPageNumberForLease = 1
                        mIsLastPageForLease = false
                        mPersonAdapter!!.notifyMovie()
                        view.tv_demand_site_select.setOnClickListener {
                            initSite(view, 3)
                        }
                        key= arrayListOf("page","number","projectSite","requirementVariety")
                        value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                        demandLease(view)
                        view.tv_demand_site_select.text="选择地点"
                        view.tv_demand_type_select.text="选择需求类别"
                        view.tv_demand_site_clear.visibility=View.GONE
                        view.tv_demand_type_clear.visibility=View.GONE
                        view.tv_demand_elect_standars_clear.visibility=View.GONE
                        view.tv_demand_elect_standard_select.visibility=View.GONE
                        view.tv_demand_type_select.visibility=View.VISIBLE
                        view.tv_demand_site_select.visibility=View.VISIBLE
                        val Option1Items= listOf("车辆租赁","工器具租赁","机械租赁","设备租赁") as MutableList<String>
                        view.tv_demand_type_select.setOnClickListener {
                            initDemandTeam(view,Option1Items,3)
                        }
                        view.tv_demand_site_clear.setOnClickListener {
                            view.tv_demand_site_select.text="选择地点"
                            view.tv_demand_site_clear.visibility=View.GONE
                            demandSite=""

                            key= arrayListOf("page","number","projectSite","requirementVariety")
                            value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForLease = 1
                            mIsLastPageForLease = false
                            mPersonAdapter!!.notifyMovie()
                            demandLease(view)
                        }
                        view.tv_demand_type_clear.setOnClickListener {
                            view.tv_demand_type_select.text="选择需求类别"
                            view.tv_demand_type_clear.visibility=View.GONE
                            demandVariety=""
                            key= arrayListOf("page","number","projectSite","requirementVariety")
                            value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForLease = 1
                            mIsLastPageForLease = false
                            mPersonAdapter!!.notifyMovie()
                            demandLease(view)
                        }
                    }
                    "需求三方"->{
                        demandSite=""
                        demandVariety=""
                        demandElect=""
                        selectContentElect = arrayOf("", "", "", "", "", "")
                        mPageNumberForThird = 1
                        mIsLastPageForThird = false
                        mPersonAdapter!!.notifyMovie()
                        key= arrayListOf("page","number","requirementVariety")
                        value= arrayListOf(mPageNumberForThird,mCountPerPageForThird,demandVariety) as MutableList<String>
                        demandThird(view)
                        view.tv_demand_type_select.text="选择需求类别"
                        view.tv_demand_site_clear.visibility=View.GONE
                        view.tv_demand_type_clear.visibility=View.GONE
                        view.tv_demand_elect_standars_clear.visibility=View.GONE
                        view.tv_demand_elect_standard_select.visibility=View.GONE
                        view.tv_demand_site_select.visibility=View.GONE
                        view.tv_demand_type_select.visibility=View.VISIBLE
                        val Option1Items= listOf("培训办证","财务记账","代办资格","标书服务","法律咨询","软件服务","资质合作","其他") as MutableList<String>
                        view.tv_demand_type_select.setOnClickListener {
                            initDemandTeam(view,Option1Items,2)
                        }
                        view.tv_demand_type_clear.setOnClickListener {
                            view.tv_demand_type_select.text="选择需求类别"
                            view.tv_demand_type_clear.visibility=View.GONE
                            demandVariety=""
                            key= arrayListOf("page","number","requirementVariety")
                            value= arrayListOf(mPageNumberForThird,mCountPerPageForThird,demandVariety) as MutableList<String>
                            mPageNumberForThird = 1
                            mIsLastPageForThird = false
                            mPersonAdapter!!.notifyMovie()
                            demandThird(view)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                //                添加未选中Tab的逻辑
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                //                再次选中tab的逻辑
            }
        })
    }

    @Synchronized fun setLoadingPerson() : Boolean {
        if (mIsLoadingPerson)
            return false
        mIsLoadingPerson = true
        return true
    }

    @Synchronized fun setLoadingTeam() : Boolean {
        if (mIsLoadingTeam)
            return false
        mIsLoadingTeam = true
        return true
    }
    @Synchronized fun setLoadingLease() : Boolean {
        if (mIsLoadingLease)
            return false
        mIsLoadingLease = true
        return true
    }
    @Synchronized fun setLoadingThird() : Boolean {
        if (mIsLoadingThird)
            return false
        mIsLoadingThird = true
        return true
    }

    fun loadDemandPersonData() {
        if (mIsLastPageForPerson)
            return
        if (!setLoadingPerson())
            return

        _loadDemandPersonData()
    }
    fun loadDemandTeamData() {
        if (mIsLastPageForTeam)
            return
        if (!setLoadingTeam())
            return

        _loadDemandTeamData()

    }
    fun loadDemandLeaseData() {
        if (mIsLastPageForLease)
            return
        if (!setLoadingLease())
            return

        _loadDemandLeaseData()
    }
    fun loadDemandThirdData() {
        if (mIsLastPageForThird)
            return
        if (!setLoadingThird())
            return

        _loadDemandThirdData()
    }
    fun _loadDemandPersonData() {
        val result= Observable.create<RequestBody> {
            //建立网络请求体 (类型，内容)
            val jsonObject = JSONObject()
            for (i in 0 until key.size) {
                jsonObject.put(key[i], value[i])
            }
            val requestBody= RequestBody.create(MediaType.parse("application/json"),jsonObject.toString())
            it.onNext(requestBody)
        }
            .subscribe {
                val loadingDialog = LoadingDialog(mView.context, "正在加载...", R.mipmap.ic_dialog_loading)
                loadingDialog.show()
                val result =
                    getRequirementPerson(it,UnSerializeDataBase.userToken,UnSerializeDataBase.dmsBasePath).
                        subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            val code = it.code
                            loadingDialog.dismiss()
                            mIsLoadingPerson = false
                            if(code=="200"){
                                val data = it.message.data
                                for (j in data) {
                                    var temp1: String
                                    var temp2: String
                                    var temp3:String
                                    temp1 = if(j.requirementMajor==null )
                                        " "
                                    else j.requirementMajor
                                    temp2 = if(j.projectSite == null)
                                        " "
                                    else j.projectSite
                                    temp3 = if(j.id == null)
                                        " "
                                    else
                                        j.id
                                    mPersonAdapter?.addMovie(Movie(temp1,temp2, temp3,"需求个人"))
                                }
                                if (data.size < mCountPerPageForPerson)
                                    mIsLastPageForPerson = true
                                else
                                    mPageNumberForPerson ++
                            }else if(code=="400" && code=="没有该数据"){
                                ToastHelper.mToast(context!!,"数据为空")
                            }else{
                                ToastHelper.mToast(context!!,"加载失败")
                            }

                        }, {
                            loadingDialog.dismiss()
                            it.printStackTrace()
                        })
            }
    }

    fun _loadDemandTeamData() {
        val result= Observable.create<RequestBody> {
            //建立网络请求体 (类型，内容)
            val jsonObject = JSONObject()
            for (i in 0 until key.size) {
                jsonObject.put(key[i], value[i])
            }
            val requestBody= RequestBody.create(MediaType.parse("application/json"),jsonObject.toString())
            it.onNext(requestBody)
        }
            .subscribe {
                val loadingDialog = LoadingDialog(mView.context, "正在加载...", R.mipmap.ic_dialog_loading)
                loadingDialog.show()
                val result =
                    getRequirementTeam(it,UnSerializeDataBase.userToken,UnSerializeDataBase.dmsBasePath).
                        subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            val code = it.code
                            loadingDialog.dismiss()
                            mIsLoadingTeam = false
                            if(code=="200"){
                                val data = it.message.data
                                for (j in data) {
                                    var temp1: String
                                    var temp2: String
                                    var temp3:String
                                    temp1 = if(j.requirementVariety==null )
                                        " "
                                    else j.requirementVariety
                                    temp2 = if(j.projectSite == null)
                                        " "
                                    else j.projectSite
                                    temp3 = if(j.requirementTeamId == null)
                                        " "
                                    else
                                        j.requirementTeamId
                                    mPersonAdapter?.addMovie(Movie(temp1,temp2, temp3,"需求团队"))
                                }
                                if (data.size < mCountPerPageForTeam)
                                    mIsLastPageForTeam = true
                                else
                                    mPageNumberForTeam ++
                            }else if(code=="400" && code=="没有该数据"){
                                ToastHelper.mToast(context!!,"数据为空")
                            }else{
                                ToastHelper.mToast(context!!,"加载失败")
                            }

                        }, {
                            loadingDialog.dismiss()
                            it.printStackTrace()
                        })
            }
    }
    fun _loadDemandLeaseData() {
        val result= Observable.create<RequestBody> {
            //建立网络请求体 (类型，内容)
            val jsonObject = JSONObject()
            for (i in 0 until key.size) {
                jsonObject.put(key[i], value[i])
            }
            val requestBody= RequestBody.create(MediaType.parse("application/json"),jsonObject.toString())
            it.onNext(requestBody)
        }
            .subscribe {
                val loadingDialog = LoadingDialog(mView.context, "正在加载...", R.mipmap.ic_dialog_loading)
                loadingDialog.show()
                val result =
                    getRequirementLease(it,UnSerializeDataBase.userToken,UnSerializeDataBase.dmsBasePath).
                        subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            val code = it.code
                            loadingDialog.dismiss()
                            mIsLoadingLease = false
                            if(code=="200"){
                                val data = it.message.data
                                for (j in data) {
                                    if(j.requirementVariety=="车辆租赁")
                                    {
                                        var temp1: String
                                        var temp2: String
                                        var temp3:String
                                        temp1 = if(j.requirementVariety==null )
                                            " "
                                        else j.requirementVariety
                                        temp2 = if(j.projectSite == null)
                                            " "
                                        else j.projectSite
                                        temp3 = if(j.requirementLeaseId == null)
                                            " "
                                        else
                                            j.requirementLeaseId
                                        mPersonAdapter?.addMovie(Movie(temp1,temp2, temp3,"需求车辆租赁"))
                                    }
                                    else
                                    {
                                        var temp1: String
                                        var temp2: String
                                        var temp3:String
                                        temp1 = if(j.requirementVariety==null )
                                            " "
                                        else j.requirementVariety
                                        temp2 = if(j.projectSite == null)
                                            " "
                                        else j.projectSite
                                        temp3 = if(j.requirementLeaseId == null)
                                            " "
                                        else
                                            j.requirementLeaseId
                                        mPersonAdapter?.addMovie(Movie(temp1,temp2, temp3,"需求租赁"))
                                    }
                                }
                                if (data.size < mCountPerPageForLease)
                                    mIsLastPageForLease = true
                                else
                                    mPageNumberForLease ++
                            }else if(code=="400" && code=="没有该数据"){
                                ToastHelper.mToast(context!!,"数据为空")
                            }else{
                                ToastHelper.mToast(context!!,"加载失败")
                            }


                        }, {
                            loadingDialog.dismiss()
                            it.printStackTrace()
                        })
            }
    }
    fun _loadDemandThirdData() {
        val result= Observable.create<RequestBody> {
            //建立网络请求体 (类型，内容)
            val jsonObject = JSONObject()
            for (i in 0 until key.size) {
                jsonObject.put(key[i], value[i])
            }
            val requestBody= RequestBody.create(MediaType.parse("application/json"),jsonObject.toString())
            it.onNext(requestBody)
        }
            .subscribe {
                val loadingDialog = LoadingDialog(mView.context, "正在加载...", R.mipmap.ic_dialog_loading)
                loadingDialog.show()
                val result =
                    getRequirementThirdParty(it,UnSerializeDataBase.userToken,UnSerializeDataBase.dmsBasePath).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            val code = it.code
                            loadingDialog.dismiss()
                            mIsLoadingThird = false
                            if(code=="200"){
                                val data = it.message.data
                                for (j in data) {
                                    if(j.requirementVariety=="资质合作")
                                    {
                                        var temp1: String
                                        var temp2: String
                                        var temp3:String
                                        temp1 = if(j.requirementVariety==null )
                                            " "
                                        else j.requirementVariety
                                        temp2 = if(j.cooperationRegion == null)
                                            " "
                                        else j.cooperationRegion
                                        temp3 = if(j.id == null)
                                            " "
                                        else
                                            j.id
                                        mPersonAdapter?.addMovie(Movie(temp1,temp2, temp3,"需求三方"))
                                    }
                                    else{
                                        var str: String
                                        str=if(j.partnerAttribute=="1")
                                        {
                                            "单位"
                                        }
                                        else if(j.partnerAttribute==null)
                                        {
                                            " "
                                        }
                                        else
                                        {
                                            "个人"
                                        }
                                        var temp1: String
                                        var temp3:String
                                        temp1 = if(j.requirementVariety==null )
                                            " "
                                        else j.requirementVariety
                                        temp3 = if(j.id == null)
                                            " "
                                        else
                                            j.id
                                        mPersonAdapter?.addMovie(Movie(temp1,str, temp3,"需求三方"))
                                    }
                                }
                                if (data.size < mCountPerPageForThird)
                                    mIsLastPageForThird = true
                                else
                                    mPageNumberForThird ++
                            }else if(code=="400" && code=="没有该数据"){
                                ToastHelper.mToast(context!!,"数据为空")
                            }else{
                                ToastHelper.mToast(context!!,"加载失败")
                            }


                        }, {
                            loadingDialog.dismiss()
                            it.printStackTrace()
                        })
            }
    }
    //初始化地点数据
    fun initProjectSite(){
        val resultBuilder= StringBuilder()
        val bf= BufferedReader(InputStreamReader(context!!.assets.open("pca.json")))
        try {
            var line=bf.readLine()
            while (line!=null)
            {
                resultBuilder.append(line)
                line=bf.readLine()
            }
        }
        catch (io: IOException)
        {
            io.printStackTrace()
        }
        val json= JSONObject(resultBuilder.toString())
        val keys:Iterator<String> = json.keys()
        while (keys.hasNext()){
            val key = keys.next()
            selectOption1Items.add(key)
            val js = json.getJSONObject(key)
            val jskeys = js.keys()
            selectOption2Items.add(ArrayList())
            selectOption3Items.add(ArrayList())
            while (jskeys.hasNext()){
                val jskey = jskeys.next()
                selectOption2Items[selectOption2Items.size-1].add(jskey)
                selectOption3Items[selectOption3Items.size-1].add(ArrayList())
                var valueArray = js.getJSONArray(jskey)
                for (j in 0 until valueArray.length()){
                    selectOption3Items[selectOption3Items.size-1][selectOption3Items[selectOption3Items.size-1].size-1].add(valueArray[j].toString())
                }
            }
        }
    }
    //地点选择弹窗
    fun initSite(view:View,flag:Int){
        var mHandler: Handler = Handler(Handler.Callback {
            when (it.what) {
                RecyclerviewAdapter.MESSAGE_SELECT_OK -> {
                    mPageNumberForPerson=1
                    mPageNumberForTeam=1
                    mPageNumberForLease=1
                    val selectContent = it.data.getString("selectContent")
                    view.tv_demand_site_select.text = selectContent
                    view.tv_demand_site_clear.visibility = View.VISIBLE
                    demandSite=""
                    val  str=selectContent.split(" ")
                    for(temp in str) {
                        if(demandSite!="")
                            demandSite+=" / "
                        demandSite+=temp
                    }
                    when (flag) {
                        1-> {//需求个人加载
                            key= arrayListOf("page","number","projectSite","requirementMajor")
                            value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForPerson = 1
                            mIsLastPageForPerson = false
                            mPersonAdapter!!.notifyMovie()
                            demandPerson(view)
                        }
                        2 -> {//需求团队加载

                            key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                            value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                                selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                                selectContentElect[4],selectContentElect[5]) as MutableList<String>
                            mPageNumberForTeam = 1
                            mIsLastPageForTeam = false
                            mPersonAdapter!!.notifyMovie()
                            demandTeam(view)
                        }
                        3 -> {//需求租赁加载

                            key= arrayListOf("page","number","projectSite","requirementVariety")
                            value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForLease = 1
                            mIsLastPageForLease = false
                            mPersonAdapter!!.notifyMovie()
                            demandLease(view)
                        }
                    }
                    false
                }
                else -> {
                    false
                }
            }
        })
        val selectDialog = CustomDialog(
            CustomDialog.Options.THREE_OPTIONS_SELECT_DIALOG,
            view.context,
            mHandler,
            selectOption1Items,
            selectOption2Items,
            selectOption3Items
        ).multiDialog
        selectDialog.show()
    }
    //需求个人 租赁弹窗
    fun initDemandPerson(view:View,Option1Items: MutableList<String>,Option2Items: MutableList<MutableList<String>>,flag: Int){
        var mHandler:Handler= Handler(Handler.Callback {
            when(it.what)
            {
                RecyclerviewAdapter.MESSAGE_SELECT_OK ->{
                    mPageNumberForPerson=1
                    val selectContent=it.data.getString("selectContent")
                    view.tv_demand_type_select.text=selectContent
                    view.tv_demand_type_clear.visibility=View.VISIBLE

                    if(flag==1){//需求个人
                        val  str=selectContent.split(" ")
                        var flagfortype=0
                        for(temp in str) {
                            flagfortype += 1
                            if (flagfortype<2) {
                                demandVariety=""
                            } else {
                                demandVariety=temp
                            }
                        }

                        key= arrayListOf("page","number","projectSite","requirementMajor")
                        value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                        mPageNumberForPerson = 1
                        mIsLastPageForPerson = false
                        mPersonAdapter!!.notifyMovie()
                        demandPerson(view)
                    }
                    false
                }
                else->
                {
                    false
                }
            }
        })
        val selectDialog= CustomDialog(CustomDialog.Options.TWO_OPTIONS_SELECT_DIALOG,view.context,mHandler,Option1Items,Option2Items).multiDialog
        selectDialog.show()
    }
    //需求团队 三方服务弹窗 租赁
    fun initDemandTeam(view:View,Option1Items:MutableList<String>,flag: Int){
        var mHandler:Handler= Handler(Handler.Callback {
            when(it.what)
            {
                RecyclerviewAdapter.MESSAGE_SELECT_OK ->{
                    mPageNumberForTeam=1
                    mPageNumberForThird=1
                    mPageNumberForLease=1
                   demandVariety=it.data.getString("selectContent")
                    view.tv_demand_type_select.text=demandVariety
                    view.tv_demand_type_clear.visibility=View.VISIBLE
                    if(flag==1){//需求团队

                        key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                        value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                            selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                            selectContentElect[4],selectContentElect[5]) as MutableList<String>
                        mPageNumberForTeam = 1
                        mIsLastPageForTeam = false
                        mPersonAdapter!!.notifyMovie()
                        demandTeam(view)
                    }
                    else if(flag==2)//需三方
                    {
                        key= arrayListOf("page","number","requirementVariety")
                        value= arrayListOf(mPageNumberForThird,mCountPerPageForThird,demandVariety) as MutableList<String>
                        mPageNumberForThird = 1
                        mIsLastPageForThird = false
                        mPersonAdapter!!.notifyMovie()
                        demandThird(view)
                    }else if(flag==3){//需租赁

                        key= arrayListOf("page","number","projectSite","requirementVariety")
                        value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                            mPageNumberForLease = 1
                            mIsLastPageForLease = false
                            mPersonAdapter!!.notifyMovie()
                            demandLease(view)

                    }
                    false
                }
                else->
                {
                    false
                }
            }
        })
        val selectDialog= CustomDialog(CustomDialog.Options.SELECT_DIALOG,view.context,Option1Items,mHandler).dialog
        selectDialog.show()
    }
    //电压等级弹窗
    fun initElectDialog(view:View,Option1Items:MutableList<String>){
        mPageNumberForTeam=1
        var dialog = AlertDialog.Builder(this.context)
            .setMultiChoiceItems(Option1Items.toTypedArray() as Array<out CharSequence>,checkedItems.toBooleanArray()) { dialog: DialogInterface?, which: Int, isChecked: Boolean ->
                checkedItems[which]=isChecked
            }
            .setPositiveButton("确定") { dialog, which ->
                demandElect=""
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        selectContentElect[i] = Option1Items[i]
                        if(demandElect!="")
                            demandElect+=" "
                        demandElect+=Option1Items[i]
                    }
                }
                view.tv_demand_elect_standard_select.text=demandElect
                    view.tv_demand_elect_standars_clear.visibility=View.VISIBLE

                key= arrayListOf("page","number","projectSite","requirementVariety","v1","v2","v3","v4","v5","v6")
                value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                    selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                    selectContentElect[4],selectContentElect[5]) as MutableList<String>
                        mPageNumberForTeam = 1
                        mIsLastPageForTeam = false
                        mPersonAdapter!!.notifyMovie()
                        demandTeam(view)
                dialog.dismiss()
            }.create()
        dialog.show()
    }
    fun selectScroll(theflag:Int,view:View){
        when(theflag)
        {
            1->{
                value= arrayListOf(mPageNumberForPerson,mCountPerPageForPerson,demandSite,demandVariety) as MutableList<String>
                loadDemandPersonData()
            }
            2->{
                value= arrayListOf(mPageNumberForTeam,mCountPerPageForTeam,demandSite,demandVariety,
                    selectContentElect[0],selectContentElect[1],selectContentElect[2],selectContentElect[3],
                    selectContentElect[4],selectContentElect[5]) as MutableList<String>
                loadDemandTeamData()
            }
            3->{
                value= arrayListOf(mPageNumberForLease,mCountPerPageForLease,demandSite,demandVariety) as MutableList<String>
                loadDemandLeaseData()
            }
            4->{
                value= arrayListOf(mPageNumberForThird,mCountPerPageForThird,demandVariety) as MutableList<String>
                loadDemandThirdData()
            }
            else->{
            }
        }
    }
}