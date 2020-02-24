package com.example.eletronicengineer.custom

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.example.eletronicengineer.adapter.RecyclerviewAdapter
import com.example.eletronicengineer.R
import com.example.eletronicengineer.utils.ToastHelper
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import kotlinx.android.synthetic.main.dialog_long_select.view.*
import kotlinx.android.synthetic.main.dialog_select.view.*
import kotlinx.android.synthetic.main.dialog_two_datepicker.view.*
import java.util.*


class CustomDialog {
  lateinit var dialog:AlertDialog
  var selectContent:String=""
    var selectCheckContent:MutableList<String> = mutableListOf()
  lateinit var selectContent2:String
  lateinit var multiDialog:OptionsPickerView<String>
  var msgWhat:Int?=null
  var mHandler:Handler
  enum class Options
  {
    SELECT_DIALOG,TWO_OPTIONS_SELECT_DIALOG,THREE_OPTIONS_SELECT_DIALOG,SELECT_CHECK_DIALOG,SELECT_DATE_DIALOG,SELECT_LONG_DIALOG,SELECT_PROMPT_DIALOG
  }
  var options:Options
    constructor(options:Options,context: Context,selectContentTitle: String,contentList:List<String>,handler:Handler)
    {
        this.options=options
        this.mHandler=handler
        when(options)
        {
            Options.SELECT_LONG_DIALOG->
            {
                val selectContentList:MutableList<String> = mutableListOf()
                val v=LayoutInflater.from(context).inflate(R.layout.dialog_long_select,null,false)
                v.tv__dialog_long_select.text=contentList[0]
                v.tv__dialog_long_select1.text=contentList[1]
                v.tv__dialog_long_select2.text=contentList[2]
                for(i in 3..6){
                    selectContentList.add(contentList[i])
                }
                val wheelView=v.wv_dialog_long_select_container
                wheelView.adapter =ArrayWheelAdapter(selectContentList)
                wheelView.setLineSpacingMultiplier(1.2F)
                wheelView.setCyclic(false)
                val wheelView1=v.wv_dialog_long_select_container1
                wheelView1.adapter =ArrayWheelAdapter(selectContentList)
                wheelView1.setLineSpacingMultiplier(1.2F)
                wheelView1.setCyclic(false)
                val wheelView2=v.wv_dialog_long_select_container2
                wheelView2.adapter =ArrayWheelAdapter(selectContentList)
                wheelView2.setLineSpacingMultiplier(1.2F)
                wheelView2.setCyclic(false)
                val builder=AlertDialog.Builder(context)
                builder.setTitle(selectContentTitle+":")
                    .setView(v)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确认") { dialog, which ->
                        if(selectContentList[wheelView.currentItem]=="不选择"&&selectContentList[wheelView1.currentItem]=="不选择"
                            &&selectContentList[wheelView2.currentItem]=="不选择"){
                            ToastHelper.mToast(context,"请至少选择一种")
                        }else{
                            selectContent=if(selectContentList[wheelView.currentItem]=="不选择"){""}else{contentList[0]+selectContentList[wheelView.currentItem]}+" "+
                                    if(selectContentList[wheelView1.currentItem]=="不选择"){""}else{contentList[1]+selectContentList[wheelView1.currentItem]}+" "+
                                    if(selectContentList[wheelView2.currentItem]=="不选择"){""}else{contentList[2]+selectContentList[wheelView2.currentItem]}
                            val msg = if (msgWhat!=null) {
                                mHandler.obtainMessage(msgWhat!!)
                            } else {
                                mHandler.obtainMessage(RecyclerviewAdapter.MESSAGE_SELECT_OK)
                            }
                            val data= Bundle()
                            data.putString("selectContent",selectContent)
                            msg.data=data
                            mHandler.sendMessage(msg)
                        }
                }
                this.dialog=builder.create()
            }
            else->
            {

            }
        }
    }
    constructor(options:Options,context: Context,shiftInputTitle:String,shiftInputContent:String,handler:Handler)
    {
        this.options=options
        this.mHandler=handler
        when(options)
        {
            Options.SELECT_DATE_DIALOG->
            {
                    val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_two_datepicker, null)
                val builder = AlertDialog.Builder(context)
                    if (shiftInputContent !="") {
                        val start_end_Time=shiftInputContent.split("至")
                        val s =start_end_Time[0].split("-")
                        mDialogView.date_two_picker.init(s[0].toInt(), s[1].toInt() - 1, s[2].toInt(), null)
                        val end =start_end_Time[1].split("-")
                        mDialogView.date_two_picker1.init(end[0].toInt(), end[1].toInt() - 1, end[2].toInt(), null)
                    }
                builder.setTitle(shiftInputTitle)
                        .setView(mDialogView)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", DialogInterface.OnClickListener() { _, _ ->
                            val date =
                                java.sql.Date.valueOf("${mDialogView.date_two_picker.year}-${mDialogView.date_two_picker.month + 1}-${mDialogView.date_two_picker.dayOfMonth}")
                            val date1 =
                                java.sql.Date.valueOf("${mDialogView.date_two_picker1.year}-${mDialogView.date_two_picker1.month + 1}-${mDialogView.date_two_picker1.dayOfMonth}")
                            try {
                                if (date >= date1) {
                                    ToastHelper.mToast(context,"结束日期不能超过开始日期")
                                }else{
                                    val msg = if (msgWhat!=null) {
                                        mHandler.obtainMessage(msgWhat!!)
                                    } else {
                                        mHandler.obtainMessage(RecyclerviewAdapter.MESSAGE_SELECT_CHECK_OK)
                                    }
                                    val data= Bundle()
                                    data.putString("selectContent",date.toString()+"至"+date1.toString())
                                    msg.data=data
                                    mHandler.sendMessage(msg)
                                }
                            } catch (e: Exception) {
                                ToastHelper.mToast(context,"选择日期有误")
                            }

                        })
                    this.dialog=builder.create()
            }
            Options.SELECT_PROMPT_DIALOG->{
                val builder = AlertDialog.Builder(context)
                builder.setTitle(shiftInputTitle)
                    .setMessage(shiftInputContent)
                    .setPositiveButton("确定",null)
                this.dialog=builder.create()
            }
            else->
            {

            }
        }
    }
    constructor(options:Options,context: Context,contentList:Array<String>,selectContentStr:String,handler:Handler)
    {
        this.options=options
        this.mHandler=handler
        when(options)
        {
            Options.SELECT_CHECK_DIALOG->
            {
                var mPalceIsCheck:BooleanArray = booleanArrayOf(
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false,false,
                    false,false,false
                )

                var selectContentList = selectContentStr.split("、")
                for (j in 0 until selectContentList.size){
                    val position = contentList.indexOf(selectContentList[j])
                    if(position>=0)
                        mPalceIsCheck[position] = true
                }
                val builder=AlertDialog.Builder(context)
                builder.setMultiChoiceItems(contentList,mPalceIsCheck,DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                    if(isChecked){
                        mPalceIsCheck[which]=true
                    }else{
                        mPalceIsCheck[which]=false
                    }
                })

                builder.setPositiveButton("确认") { dialog, which ->
                    val msg = if (msgWhat!=null) {
                        mHandler.obtainMessage(msgWhat!!)
                    } else {
                        mHandler.obtainMessage(RecyclerviewAdapter.MESSAGE_SELECT_CHECK_OK)
                    }
                    if(mPalceIsCheck[0]){
                        selectCheckContent.add(contentList[0])
                    }else{
                        for(i in 1 until  mPalceIsCheck.size)
                        {
                            if(mPalceIsCheck[i]){
                                selectCheckContent.add(contentList[i])
                            }
                        }
                    }
                    if(selectCheckContent.size==mPalceIsCheck.size-1 && !mPalceIsCheck[0]){
                        selectCheckContent.clear()
                        selectCheckContent.add(contentList[0])
                        mPalceIsCheck[0]=true
                    }

                    if(selectCheckContent.size>10){
                        ToastHelper.mToast(context,"所选省份不能超过10个!请重新选择!")
                    }
                    else{
                        for(i in selectCheckContent ){
                            if(selectContent!="")
                                selectContent+="、"
                            selectContent+=i
                        }
                        val data= Bundle()
                        data.putString("selectContent",selectContent)
                        msg.data=data
                        mHandler.sendMessage(msg)
                    }
                }
                this.dialog=builder.create()
            }
            else->
            {

            }
        }
    }
  constructor(options:Options,context: Context,contentList:List<String>,handler:Handler)
  {
    this.options=options
    this.mHandler=handler
    when(options)
    {
      Options.SELECT_DIALOG->
      {
          var position = 0
        val v=LayoutInflater.from(context).inflate(R.layout.dialog_select,null,false)
        val wheelView=v.wv_dialog_select_container
        wheelView.adapter =ArrayWheelAdapter(contentList)
        wheelView.setCyclic(false)
        val builder=AlertDialog.Builder(context).setView(v)
        builder.setPositiveButton("确认") { dialog, which ->
            position = wheelView.currentItem
          selectContent=contentList[wheelView.currentItem]

          val msg = if (msgWhat!=null) {
            mHandler.obtainMessage(msgWhat!!)
          } else {
            mHandler.obtainMessage(RecyclerviewAdapter.MESSAGE_SELECT_OK)
          }
          val data= Bundle()
          data.putString("selectContent",selectContent)
            data.putInt("position",position)
          msg.data=data
          mHandler.sendMessage(msg)
        }
        this.dialog=builder.create()
      }
      else->
      {

      }
    }
  }
  constructor(options:Options,context: Context,handler:Handler,option1Items:List<String>,option2Items:List<List<String>>)
  {
    this.options=options
    this.mHandler=handler
    when(options)
    {
      Options.TWO_OPTIONS_SELECT_DIALOG->
      {
        val picker=OptionsPickerBuilder(context) { options1, options2, _, _ ->
          selectContent=option1Items[options1]+" "+option2Items[options1][options2]
          selectContent2=option1Items[options1]
          val msg= if (msgWhat!=null) {
            mHandler.obtainMessage(msgWhat!!)
          } else {
            mHandler.obtainMessage(RecyclerviewAdapter.MESSAGE_SELECT_OK)
          }
          val data= Bundle()
          data.putString("selectContent",selectContent)
          data.putString("selectContent2",selectContent2)
          msg.data=data
          mHandler.sendMessage(msg)

        }.isDialog(true).build<String>()
        picker.setPicker(option1Items,option2Items)
        this.multiDialog=picker
      }
    }
  }
  constructor(options:Options,context: Context,handler:Handler,option1Items:List<String>,option2Items:List<List<String>>,option3Items:List<List<List<String>>>)
  {
    this.options=options
    this.mHandler=handler
    when(options)
    {
      //wheelView
      Options.THREE_OPTIONS_SELECT_DIALOG->
      {
        val picker=OptionsPickerBuilder(context) { options1, options2, options3, v ->
          selectContent=option1Items[options1]+" "+option2Items[options1][options2]+" "+option3Items[options1][options2][options3]
          val msg = if (msgWhat!=null)
          {
            mHandler.obtainMessage(msgWhat!!)
          }
          else
          {
            mHandler.obtainMessage(RecyclerviewAdapter.MESSAGE_SELECT_OK)
          }
          val data= Bundle()
          data.putString("selectContent",selectContent)
          msg.data=data
          mHandler.sendMessage(msg)
        }.isDialog(true).build<String>()
        picker.setPicker(option1Items,option2Items,option3Items)
        this.multiDialog=picker
      }
    }
  }
}