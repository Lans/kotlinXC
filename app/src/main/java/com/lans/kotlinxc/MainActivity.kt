package com.lans.kotlinxc

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val contactPermission = android.Manifest.permission.READ_CONTACTS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDataBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, contactPermission) == PackageManager.PERMISSION_GRANTED) {
                val dialogFragment = DialogFragment()
                dialogFragment.show(supportFragmentManager, "ss")
                //线程代码块
                GlobalScope.launch {
                    //异步线程
                    val async = async {
                        getContactPhone()
                    }
                    //主线程
                    withContext(Dispatchers.Main) {
                        hello.text = async.await().toString()
                        Log.e("MainActivity", async.await().toString())
                        dialogFragment.dismiss()
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(contactPermission), 1)

            }
        }

        startBtn.setOnClickListener {
            Toast.makeText(applicationContext, "没有阻塞线程", Toast.LENGTH_SHORT).show()
        }

        stopBtn.setOnClickListener {
            Toast.makeText(applicationContext, "没有阻塞线程", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GlobalScope.launch {
                    val async = async {
                        getContactPhone()
                    }

                    hello.text = async.await().toString()
                    Log.e("MainActivity", async.await().toString())
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(contactPermission), 1)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    /**
     *  获取联系人列表
     */
    private fun getContactPhone(): List<ContactsBean> {
        val contactsList = ArrayList<ContactsBean>()

        //得到ContentResolver对象
        val cr = contentResolver
        //取得电话本中开始一项的光标
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        //向下移动光标
        while (cursor!!.moveToNext()) {
            val contact = ContactsBean()
            //取得联系人名字
            val nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            contact.name = cursor.getString(nameFieldColumnIndex)
            //取得电话号码
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phone = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                null,
                null
            )

            while (phone!!.moveToNext()) {
                val phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                //格式化手机号
                //  PhoneNumber = PhoneNumber.replace("-", "");
                //PhoneNumber = PhoneNumber.replace(" ", "");
                contact.phone = phoneNumber
            }
            phone.close()
            contactsList.add(contact)
        }
        cursor.close()
        return contactsList
    }

}
