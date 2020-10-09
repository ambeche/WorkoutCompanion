package com.example.workoutcompanion.activities.chat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.imageview_chat_to_row
import kotlinx.android.synthetic.main.image_from_chat.view.*
import kotlinx.android.synthetic.main.image_to_row.view.*
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

class ChatLogActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 30
    lateinit var mCurrentPhotoPath:String

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    var selectedPhoto: Uri?= null

    var sharableimg:Bitmap? = null

    var imagetobeshared :String? = null

    var uri_fromCaptured:Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        if ((Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        }

        val fileName = "my_photo_wc"
        val imgPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile: File = File.createTempFile(fileName, ".jpg", imgPath)
        val photoURI: Uri = FileProvider.getUriForFile(this,
            "com.example.workoutcompanion.fileprovider",
            imageFile)

        uri_fromCaptured = photoURI

        mCurrentPhotoPath = imageFile.absolutePath

        val ip = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("FilesDire","$ip")

        val myIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        recyclerview_chat_log.layoutManager = LinearLayoutManager(this)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username


        recyclerview_chat_log.adapter = adapter


            listenForMessages()
            listenIMAGEMessages()



        send_button_chat_log.setOnClickListener {

            Log.d("SendMessage","Attempt to send Message")

            if(edittext_chat_log.text.isNotEmpty()){
                performSendMessage()
            }




        }
        upload_Button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type ="image/*"
            startActivityForResult(intent,12)
            edittext_chat_log.isEnabled = false
        }

        Camera_btn.setOnClickListener {
            if (myIntent.resolveActivity(packageManager) != null) {
                myIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                myIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            }
            startActivityForResult(myIntent, REQUEST_IMAGE_CAPTURE)
        }

    }


    //Function that send a message and  Save it To Firebase
    private fun performSendMessage() {

        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = toId?.let {
            ChatMessage(reference.key!!, text, fromId,
                it, System.currentTimeMillis() / 1000)
        }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("SendMessage", "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }


    //Function for uploading shared image to FireBase
    private fun uploadImageToFirebasestorage() {

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid




        if(selectedPhoto == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")


        ref.putFile(selectedPhoto!!).addOnSuccessListener { it ->
            Log.d("Chat","Succefuly iploaded image ${it.metadata?.path}")


            ref.downloadUrl.addOnSuccessListener {

                Log.d("register","File location :${it.toString()}")
                imagetobeshared = it.toString()


            }

        }.addOnFailureListener {
            Log.d("Register","Failed to select photo")
        }

        Timer("SettingUp", false).schedule(5900) {

            val reference = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${fromId}/${toId}").push()

            val toReference = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${toId}/${fromId}").push()

            val imagedMessage = ImagChatMessage(reference.key!!, imagetobeshared!!, fromId!!, toId!!, System.currentTimeMillis() / 1000)
            reference.setValue(imagedMessage)
                .addOnSuccessListener {
                    Log.d("SendImgMessage", "Saved our chat Imag message: ${reference.key}")
                    // edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
            toReference.setValue(imagedMessage)


        }


    }


    //Function for Imageshared Mesages From FireBase
    private fun listenIMAGEMessages() {

           val fromId = FirebaseAuth.getInstance().uid
           val toId = toUser?.uid
           val ref = FirebaseDatabase.getInstance().getReference("/Image-user-messages/${fromId}/${toId}")

           ref.addChildEventListener(object: ChildEventListener {

               override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                   val chatImageMessage = p0.getValue(ImagChatMessage::class.java)

                   if (chatImageMessage != null) {
                       chatImageMessage.bitm?.let { Log.d("ListenToImageMessage", it) }

                       if (chatImageMessage.fromId == FirebaseAuth.getInstance().uid) {
                           val currentUser = MainActivity.currentUser ?: return
                           adapter.add(ChatFromImageItem(chatImageMessage.bitm,currentUser))
                       } else {
                           adapter.add(ChatToImageItem(chatImageMessage.bitm,toUser!!))
                       }
                   }else {
                       Log.d("ListenImage","${chatImageMessage?.bitm}")
                   }

               }

               override fun onCancelled(p0: DatabaseError) {

               }

               override fun onChildChanged(p0: DataSnapshot, p1: String?) {

               }

               override fun onChildMoved(p0: DataSnapshot, p1: String?) {

               }

               override fun onChildRemoved(p0: DataSnapshot) {

               }

           })




    }

    //Function For Listening For messages from FireBase
    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId}/${toId}")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d("ListenToMessage", chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = MainActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    //Function called after picking up the SharedImage
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("UploadImagshare", "pic was selected")

            selectedPhoto = data.data
            Log.d("FF", selectedPhoto.toString())


            val uriFinal = getCapturedImage(selectedPhoto)

            sharableimg = uriFinal

            Log.d("Share1","Final_URi :   $sharableimg")
            Log.d("Share2","The URL to string  ${sharableimg.toString()}")
            // val BITMAP = BitmapDrawable(uri_final)
            //  button_Img.setBackgroundDrawable(BITMAP)
            //selectPhotoImageview.setImageBitmap(uri_final)

            if (uriFinal != null) {
                performImgSharing()
            }


        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null && uri_fromCaptured!= null){

            selectedPhoto = uri_fromCaptured
            Log.d("FF", selectedPhoto.toString())

            //val uri12 = getCapturedImage(selectedPhoto)


                performImgSharing()
            //content://com.google.android.apps.photos.contentprovider/-1/1/
            // content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F55/ORIGINAL/NONE/image%2Fpng/1358640755


        }else {
            println("DDDDDDDD")
        }
    }

    //Function For Sharing
    private fun performImgSharing() {

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid


        if (fromId == null) return

        uploadImageToFirebasestorage()

    }

    //This function converts the Uri of picked pic into Bitmap according on The sdk of the device
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getCapturedImage(selectedPhotoUri: Uri?): Bitmap? {

        var uri1:Bitmap? = null
        val bitmap = when {
            Build.VERSION.SDK_INT <= 28 ->
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    selectedPhotoUri
                )
            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
                uri1 =  ImageDecoder.decodeBitmap(source)
            }
        }
        return uri1

    }


}

    //Class takes care of populating messages row from  User to others
    class ChatFromItem(val message:String,val user:User): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textview_to_row.text = message

            val uri = user.profileImag
            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            Picasso.get().load(uri).into(targetImageView)

        }

        override fun getLayout(): Int {
            return R.layout.chat_to_row
        }
    }

    //Class takes care of populating imageMessages row from  User to others
    class ChatFromImageItem(val messageUrl:String?,val user:User): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            Log.d("insideChatFrom","received:  ${messageUrl}")

            val imagetoshow = viewHolder.itemView.imageSent_To_row
            Picasso.get().load(messageUrl).into(imagetoshow)
           // viewHolder.itemView.imageview_chat_to_row.setImageBitmap(image)

            val uri = user.profileImag
            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            Picasso.get().load(uri).into(targetImageView)

        }

        override fun getLayout(): Int {
            return R.layout.image_to_row
        }
    }

   //Class takes care of populating messages row from  User to others
    class ChatToImageItem(val messageUrl:String?,val user:User): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val imagetoshow = viewHolder.itemView.imageSent_from_row


            Picasso.get().load(messageUrl).into(imagetoshow)

           // viewHolder.itemView.imageview_chat_from_row.setImageBitmap(image)

            val uri = user.profileImag
            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            Picasso.get().load(uri).into(targetImageView)

        }

        override fun getLayout(): Int {
            return R.layout.image_from_chat
        }
    }


    //Class takes care of populating ImageMessages row from  User to others
    class ChatToItem(val message:String,val user:User): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textview_from_row.text = message

            val uri = user.profileImag
            val targetImageView = viewHolder.itemView.imageview_chat_from_row
            Picasso.get().load(uri).into(targetImageView)
        }

        override fun getLayout(): Int {
            return R.layout.chat_from_row
        }
    }