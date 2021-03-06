package com.example.eletronicengineer.custom

import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.dialog_select.view.*


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
    SELECT_DIALOG,TWO_OPTIONS_SELECT_DIALOG,THREE_OPTIONS_SELECT_DIALOG,SELECT_CHECK_DIALOG
  }
  var options:Options
    constructor(options:Options,context: Context,contentList:Array<String>,palceIsCheck:BooleanArray,handler:Handler)
    {
        this.options=options
        this.mHandler=handler
        when(options)
        {
            Options.SELECT_CHECK_DIALOG->
            {
                var mPalceIsCheck:BooleanArray = booleanArrayOf(false)
                mPalceIsCheck=palceIsCheck
                val builder=AlertDialog.Builder(context)
                builder.setMultiChoiceItems(contentList,palceIsCheck,DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
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
                    for(i in selectCheckContent ){
                        if(selectContent!="")
                            selectContent+="、"
                           selectContent+=i
                    }
                    val data= Bundle()
                    data.putString("selectContent",selectContent)
                    data.putBooleanArray("selectContent1",mPalceIsCheck)
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
  constructor(options:Options,context: Context,contentList:List<String>,handler:Handler)
  {
    this.options=options
    this.mHandler=handler
    when(options)
    {
      Options.SELECT_DIALOG->
      {
        val v=LayoutInflater.from(context).inflate(R.layout.dialog_select,null,false)
        val wheelView=v.wv_dialog_select_container
        wheelView.adapter =ArrayWheelAdapter(contentList)
        wheelView.setCyclic(false)
        val builder=AlertDialog.Builder(context).setView(v)
        builder.setPositiveButton("确认") { dialog, which ->
          selectContent=contentList[wheelView.currentItem]

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