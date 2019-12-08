package com.example.eletronicengineer.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.eletronicengineer.R
import com.example.eletronicengineer.adapter.NetworkAdapter
import com.example.eletronicengineer.fragment.sdf.DemandDisplayFragment
import com.example.eletronicengineer.fragment.sdf.ImageFragment
import com.example.eletronicengineer.model.Constants
import com.example.eletronicengineer.utils.UnSerializeDataBase
import com.lcw.library.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_photo_upload.view.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class DemandDisplayActivity : AppCompatActivity() {
  var type:Int=0
  lateinit var id:String
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demand_display)
    initFragment()
  }

  private fun initFragment() {
    initData()
    val data = Bundle()
    data.putInt("type",type)
    data.putString("id",id)
    addFragment(DemandDisplayFragment.newInstance(data))
  }

  //获取加载的界面类型
  private fun initData() {
    val intent = getIntent()
    type = intent.getIntExtra("type",0)
    id = intent.getStringExtra("id")
  }

  fun addFragment(fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.frame_display_demand, fragment)
    transaction.commit()
  }
  fun switchFragment(fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    transaction.replace(R.id.frame_display_demand, fragment)
    transaction.addToBackStack(null)
    transaction.commit()
  }
  //Fragment切换
  fun switchFragment(fragment: Fragment,frameLayout:Int,tag:String) {
    val transaction = supportFragmentManager.beginTransaction()
    //隐藏上个Fragment
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    transaction.replace(frameLayout, fragment,tag)
    transaction.addToBackStack(tag)
    transaction.commit()
  }
  //拍照回调
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode== Activity.RESULT_OK)
    {
      when(requestCode)
      {
        Constants.RequestCode.REQUEST_PHOTOGRAPHY.ordinal->{
          val bmp=data?.extras?.get("data") as Bitmap
          val file= File(this.filesDir.absolutePath+"tmp.png")
          val fos= FileOutputStream(file)
          bmp.compress(Bitmap.CompressFormat.PNG,100,fos)
          //val bmpToDrawable=BitmapDrawable.createFromPath(file.absolutePath)
          val fragment=this.supportFragmentManager.findFragmentByTag("Capture")
          fragment!!.view!!.iv_uplaod_photo_content.setImageBitmap(bmp)
          //UnSerializeDataBase.imgList.add(BitmapMap(file.path,""))
        }
        Constants.RequestCode.REQUEST_PICK_IMAGE.ordinal-> {
          val mImagePaths = data!!.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES)
          val fragment=this.supportFragmentManager.findFragmentByTag("Capture")!!
          NetworkAdapter(this).upImage(mImagePaths[0],fragment)
        }
        Constants.RequestCode.REQUEST_PICK_FILE.ordinal -> {
          val uri = data!!.data
          var path:String?=null
          if (uri!!.toString().contains("content")) {
            path = getRealPathFromURI(uri)
            Log.i("path", path)
            val fileMap=UnSerializeDataBase.fileList.get(UnSerializeDataBase.fileList.size-1)
            fileMap.path=path!!
            UnSerializeDataBase.fileList.set(UnSerializeDataBase.fileList.size-1,fileMap)
          }
          else
          {
            val file = File(uri.toString())
            if (file.exists())
            {
              Log.i("file", file.name)
            }
            val fileMap=UnSerializeDataBase.fileList.get(UnSerializeDataBase.fileList.size-1)
            fileMap.path=uri.toString()
            UnSerializeDataBase.fileList.set(UnSerializeDataBase.fileList.size-1,fileMap)
          }

          //val resultFile= File()
        }
      }
    }
    else
    {
      if (UnSerializeDataBase.fileList.size!=0)
      {
        val fileMap=UnSerializeDataBase.fileList.get(UnSerializeDataBase.fileList.size-1)
        if (fileMap.path=="")
        {
          UnSerializeDataBase.fileList.removeAt(UnSerializeDataBase.fileList.size-1)
        }
      }
      else if(UnSerializeDataBase.imgList.size!=0)
      {
        val imgMap=UnSerializeDataBase.imgList.get(UnSerializeDataBase.imgList.size-1)
        if (imgMap.path=="")
        {
          UnSerializeDataBase.imgList.removeAt(UnSerializeDataBase.imgList.size-1)
        }
      }

    }
  }
  fun getRealPathFromURI(contentUri: Uri): String? {
    var res: String? = null
    val projection: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    var cursor = contentResolver.query(contentUri, projection, null, null, null)
    try {
      if (cursor != null) {
        val column = cursor.getColumnIndexOrThrow(projection[0])
        if (cursor.moveToFirst()) {
          res = cursor.getString(column)
        }
        cursor.close()
      }
      if (res == null) {
        cursor =
          contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        if (cursor != null) {
          val column = cursor.getColumnIndexOrThrow(projection[0])
          if (cursor.moveToFirst()) {
            res = cursor.getString(column)
          }
          cursor.close()
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return res
  }
}
