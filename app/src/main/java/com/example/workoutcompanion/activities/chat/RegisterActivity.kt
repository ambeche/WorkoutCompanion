package com.example.workoutcompanion.activities.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

//This Activity Performs Registering
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val gender = resources.getStringArray(R.array.gender)
        val dropDownMenuAdapter = ArrayAdapter(this, R.layout.list_item, gender)

        //Register Button
        btn1.setOnClickListener {
            performRegister()
        }

        button_Img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        //Login Button
        button2.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    var selectedPhotoUri:Uri?= null

    //this function will be called after picking picture
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "pic was selected")

            selectedPhotoUri = data.data


            val uri_final = getCapturedImage(selectedPhotoUri)
            selectPhotoImageview.setImageBitmap(uri_final)
            button_Img.alpha = 0f

        }
    }

    //This function converts the Uri of picked pic into Bitmap according on The sdk of the device
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getCapturedImage(selectedPhotoUri: Uri?): Bitmap? {
        var uu:Bitmap? = null
        val bitmap = when {
            Build.VERSION.SDK_INT <= 28 ->
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    selectedPhotoUri
                )
            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
                uu =  ImageDecoder.decodeBitmap(source)
            }
        }
        return uu
    }

    //Function for register
    private fun performRegister() {
        val name = getInput(edit_Name)
        val email = getInput(edit_Email)
        val pass = getInput(edit_PassWord)

        if(email.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            Toast.makeText(this,"Please fill in the fields",Toast.LENGTH_SHORT).show()
            return
        }else if (selectedPhotoUri == null){
            Toast.makeText(this,"Please chose a pic",Toast.LENGTH_SHORT).show()
        }else {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                if (!it.isSuccessful){
                    return@addOnCompleteListener
                }else{
                    Log.d("Main","Successfully created user with uid:${it.result?.user?.uid}")
                    uploadImageToFirebasestorage()
                }
            }.addOnFailureListener{
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                Log.d("Main","Failed with ${it.message}")
            }

        }



    }

    //Function for uploading image to firebase
    private fun uploadImageToFirebasestorage() {

        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref =FirebaseStorage.getInstance().getReference("/images/${filename}")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("Register","Succefuly iploaded image ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                //it.toString()
                Log.d("register","File location :${it.toString()}")
                saveUserrodatabse(it.toString())
            }

        }.addOnFailureListener {
            Log.d("Register","Failed to select photo")
        }
    }

    //Function for saving user enterd in registeration into firebase data
    private fun saveUserrodatabse(ProfileImgUrl:String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref =  FirebaseDatabase.getInstance().getReference("/users/${uid}")
        val user = uid?.let { User(it, getInput(edit_Name), ProfileImgUrl, getInput(edit_Email),
            "", "", "", "", "" ) }

        clearTxt(edit_Email, edit_Name)

        ref.setValue(user).addOnSuccessListener {
            Log.d("Register","Finally the user to firebase")
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }.addOnFailureListener {
            Log.d("Register","Failed to save to database : ${it.message}")
        }
    }

    private fun getInput(v: TextInputLayout) = v.editText?.text.toString()

    private fun clearTxt(vararg v: TextInputLayout) {
        val ev = listOf(*v)
        ev.forEach { it.editText?.text?.clear() }
    }
}

//This class to get parcelize data from Firebase inform of User
@Parcelize
class User(val uid: String, var username:String, val profileImag:String,val email:String,
           var age:String,var gender:String,var weight:String,var height:String,var phone:String):
    Parcelable{
    constructor() : this(
        "", "", "","","","",
        "","", "")
}