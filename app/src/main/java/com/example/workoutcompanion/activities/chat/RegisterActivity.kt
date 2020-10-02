package com.example.workoutcompanion.activities.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register_user.view.*
import java.net.URI
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // initialize dropdown menu for gender
        val gender = resources.getStringArray(R.array.gender)
        val dropDownMenuAdapter = ArrayAdapter(this, R.layout.list_item, gender)
        (edit_Gender.editText as? AutoCompleteTextView)?.setAdapter(dropDownMenuAdapter)

        btn1.setOnClickListener {
            performRegister()
        }
        button_Img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        button2.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    var selectedPhotoUri:Uri?= null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "pic was selected")

            selectedPhotoUri = data.data


            val uri_final = getCapturedImage(selectedPhotoUri)
            // val BITMAP = BitmapDrawable(uri_final)
            //  button_Img.setBackgroundDrawable(BITMAP)
            selectPhotoImageview.setImageBitmap(uri_final)
            button_Img.alpha = 0f

        }
    }

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

    private fun performRegister() {
        val name = getInput(edit_Name)
        val email = getInput(edit_Email)
        val pass = getInput(edit_PassWord)

        if(email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this,"Please fill in the fields",Toast.LENGTH_SHORT).show()
            return
        }


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
            if (!it.isSuccessful){
                return@addOnCompleteListener
            }else{
                Log.d("Main","Successfully created user with uid:${it.result?.user?.uid}")
                uploadImageToFirebasestorage()            }
        }.addOnFailureListener{
            Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
            Log.d("Main","Failed with ${it.message}")
        }
    }

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

    private fun saveUserrodatabse(ProfileImgUrl:String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref =  FirebaseDatabase.getInstance().getReference("/users/${uid}")
        val user = uid?.let { User(it, getInput(edit_Name), ProfileImgUrl, getInput(edit_Email),
            getInput(edit_Age), getInput(edit_Gender), getInput(edit_Weight), getInput(edit_Height),
            getInput(edit_Phone) ) }

        clearTxt(edit_Age, edit_Email, edit_Gender, edit_Height, edit_Name, edit_Phone,
            edit_Weight)

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
@Parcelize
class User(val uid: String, val username:String, val profileImag:String,val email:String,
           val Age:String,val Gender:String,val weight:String,val height:String,val phone:String):
    Parcelable{
    constructor() : this(
        "", "", "","","","",
        "","", "")
}