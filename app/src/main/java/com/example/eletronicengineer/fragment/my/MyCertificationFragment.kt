package com.example.eletronicengineer.fragment.my

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eletronicengineer.R
import com.example.eletronicengineer.activity.MyCertificationActivity
import com.example.eletronicengineer.adapter.RecyclerviewAdapter
import com.example.eletronicengineer.adapter.RecyclerviewAdapter.Companion.MESSAGE_SELECT_OK
import com.example.eletronicengineer.custom.CustomDialog
import com.example.eletronicengineer.utils.FragmentHelper
import com.example.eletronicengineer.utils.UnSerializeDataBase
import kotlinx.android.synthetic.main.fragment_my_certification.view.*

class MyCertificationFragment :Fragment(){
    val mHandler = Handler(Handler.Callback {
        when(it.what)
        {
            MESSAGE_SELECT_OK->
            {
                val selectContent=it.data.getString("selectContent")
                mView.tv_subject_category_content.text=selectContent
                when(selectContent){
                    "个人 "-> FragmentHelper.switchFragment(activity!!,PersonalCertificationShowFragment(),R.id.frame_certification_category,"Certification")
                    else -> {
                        val bundle = Bundle()
                        bundle.putString("mainType",mView.tv_subject_category_content.text.toString())
                        FragmentHelper.switchFragment(activity!!,EnterpriseCertificationShowFragment.newInstace(bundle),R.id.frame_certification_category,"Certification")
                    }
                }
//                mView.sp_demand_moder.
                false
            }
            else->
            {
                false
            }
        }
    })
    lateinit var mView:View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_my_certification,container,false)
        initFragment()
        return mView
    }

    private fun initFragment() {
        mView.tv_my_certification_back.setOnClickListener {
            activity!!.finish()
        }

        mView.view_subject_category_sp.setOnClickListener{
            val Option1Items = listOf("供应商","建设单位","设计单位","施工单位","监理单位","咨询单位","职能部门","个人","其他")
            val Option2Items:List<List<String>> = listOf(listOf("材料供应","机械租赁","技术服务","金融服务"), listOf("政府单位","事业单位","企业"),listOf(""),
                listOf("施工企业","施工队","项目部"),
                listOf(""),listOf(""),listOf("安监部","质检部"),listOf(""),listOf(""))
            val selectDialog= CustomDialog(CustomDialog.Options.TWO_OPTIONS_SELECT_DIALOG,context!!,mHandler,Option1Items,Option2Items).multiDialog
            selectDialog.show()
        }
        mView.tv_my_certification_add.setOnClickListener {
            FragmentHelper.switchFragment(activity!!,InformationCertificationFragment(),R.id.frame_my_certification,"Certification")
        }
        if(!UnSerializeDataBase.isCertificate){
            mView.tv_my_certification_add.callOnClick()
        }
    }

}